# SimpleFragment
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.tatarka/simplefragment/simplefragment/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/me.tatarka/simplefragment/simplefragment)

A fragment-like abstraction for Android that is easier to use and understand

The purpose of this library is to build a better foundation for working with the controller layer in Android-style MVC. If you have ever worked with Fragments, you know that they feel more complicated than they need to be AND sometimes have unexpected behavior. This is especially true when you try to nest them. SimpleFragment provides an api that is very similar to native fragments but is more powerful _and_ easier to understand.

## Download
```groovy
compile 'me.tatarka.simplefragment:simplefragment:0.1'
```
or if you are using AppCompat
```groovy
compile 'me.tatarka.simplefragment:simplefragment-appcompat:0.1'
```

## Features
- Survive orientation changes
- First-class nesting
- Immedatly added to the view when they are created
- Don't have to worry about `commitAllowingStateLoss()`
- Fails fast if you attempt to add the same fragment twice
- Many of the same features as native fragments: view paging, inflation from layouts, `startActivityForResult()`, back stack, dialogs

## Usage
The most important thing to understand is how the lifecycle differs from native fragments. It is, in fact, very similar to setting `setRetainInstance(true)` in that it survives configuration changes. This makes things like api calls much easer to handle. However, you do have to be careful to only keep references any view or activity state between `onViewCreated()` and `onViewDestroyed()`.

![SimpleFragment lifecycle](https://raw.githubusercontent.com/evant/simplefragment/master/images/lifecycle.png)

Here an example of a SimpleFragment that gets a string from a network call and populates a `TextView`.
```java
public class HelloWorldFragment extends SimpleFragment {
  static final String STATE_HELLO_TEXT = "STATE_HELLO_TEXT";
  String helloText;
  TextView helloTextView;
  
  @Override
  public void onCreate(Context context, @Nullable Bundle state) {
    if (state != null) {
      helloText = state.getString(STATE_HELLO_TEXT);
    }
  
    if (helloText == null) {
      Api.getInstance(context).getHelloWorld(new Api.Listener() {
        @Override
        public void onResult(String result) {
          helloText = result;
          if (helloTextView != null) {
            helloTextView.setText(helloText);
          }
        }
      });
    }
  }
    
  @Override
  public void onSave(Bundle state) {
    state.putString(STATE_HELLO_TEXT, helloText);
  }
  
  @Override
  public View onCreateView(final LayoutInflater inflater, final ViewGroup parent) {
    return inflater.inflate(R.layout.hello_world, parent, false);
  }
  
  @Override
  public void onViewCreated(View view) {
    helloTextView = (TextView) view.findViewById(R.id.text);
    helloTextView.setText(helloText);
  }
  
  @Override
  public void onViewDestroyed() {
    helloTextView = null;
  }
}
```

There are many ways to easily handle the view lifecycle part of this equation, like [Butter Knife](https://github.com/JakeWharton/butterknife), a custom view or view holder, or more recently, [data binding](https://developer.android.com/tools/data-binding/guide.html).

In order to use SimpleFragments in an Activity you should subclass `SimpleFragmentActivity` or `SimpleFragmentAppCompatActivity`. You can then either add the SimpleFragment in the layout
```xml
<fragment
    android:id="@+id/my_fragment"
    android:name="me.tatarka.simplefragment.sample.HelloWorldFragment"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:layout="@layout/hello_world" />
```
or dynamically in code
```java
public class MyActivity extends SimpleFragmentActivity {
  @Override
  protected void onCreate(Bundle state) {
    getSimpleFragmentManager().findOrAdd(SimpleFragmentIntent.of(HelloWorldFragment.class), LayoutKey.of(R.id.my_fragment));
  }
}
```
Note that when dynamicaly creating fragments, that they will be saved and restored by the `SimpleFragementManager` so you should not add it again when restoring state (Use `findOrAdd()` to simplify this check). Luckily, you will get an immedatly exception if you accidently do this instead of having an extra fragment silently added.

### Extras
You can pass extras to you `SimpleFragment` in a similar way to the way you do it in activities. Simply add them to the `SimpleFragmentIntent`
```java
SimpleFragmentIntent.of(MyFragment.class).putExtra(MyFragment.EXTRA_ARG, "foo");
```
and then access the intent in the fragment to obtain it's value.
```java
public class MyFragment extends SimpleFragment {
  static final String EXTRA_ARG = "ARG";
  @Override
  public void onCreate(Context context, @Nullable Bundle state) {
    String arg = getIntent().getStringExtra(EXTRA_ARG);
  }
```

### Nesting
Both `SimpleFragmentActivity` and `SimpleFragment` provide a `SimpleFragmentManager`. Unlike native fragments, you don't have to worry about werid inconsitencies in nested fragements like `startActivityForResult()` or the back stack not working.

### Back Stack
You can push a new `SimpleFragment` on the back stack by using `push()` instead of `add()`. The back button will pop the fragment off, or you can do it in code with `pop()`. This will work correctly in nested fragments, where the back button will alwasy pop the last one globbaly added, and the `pop()` method will be scoped to the current fragment.

### View Paging
You can subclass `SimpleFragmentPagerAdapter` to use fragments in a `ViewPager`.
```java
private static final List<Class<? extends SimpleFragment>> FRAGMENTS = Arrays.asList(
        FragmentFromLayout.class,
        FragmentWithBackStack.class,
        FragmentDialogs.class,
        FragmentActivityForResult.class
);

private class Adapter extends SimpleFragmentPagerAdapter {
    public Adapter() {
        super(MainActivity.this);
    }
  
    @Override
    public SimpleFragmentIntent getItem(int position) {
        return SimpleFragmentIntent.of(FRAGMENTS.get(position));
    }
  
    @Override
    public int getCount() {
        return FRAGMENTS.size();
    }
}
```

The default implementation will properly handle adding and removing pages dynamcialy when you call `notifyDataSetChange()`. No more having to worry about overriding `getItemPosition()`.

Note that fragments will still be completly destroyed when they are far off screen. You can always use `ViewPager.setOffscreenPageLimit()` to modify this limit.

### Dialogs
There is a `SimpleDialogFragment` subclass you can use for dialogs.
```java
public class MyDialogFragment extends SimpleDialogFragment {
    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
        // You must call super here.
        super.onCreate(context, state);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent) {
        return inflater.inflate(R.layout.fragment_dialog, parent, false);
    }

    @Override
    public Dialog onCreateDialog(View contentView) {
        // contentView will be the view created in onCreateView()
        return new AlertDialog.Builder(contentView.getContext())
                .setView(contentView)
                .setPositiveButton("ok", null)
                .setNegativeButton("cancel", null)
                .create();
    }
}
```

Show it with
```java
getSimpleFragmentManager().add(SimpelFragmentIntent.of(MyDialogFragment.class), DialogKey.of("my_dialog")).show();
```

A good way to propigate events from the dialog (or any fragment) back up to it's parent is with `SimpleFragment.getParent()` which will return either the parent fragment or Activity depending on where it is nested. You can cast this to an interface to communicate back up to it.

### Start Activity For Result
Not much to say here, just use `startActivityForResult()` on the `SimpleFragment` instead of the activity and the result will be sent back down to your fragment. Works with nesting too!

## License

    Copyright 2015 Evan Tatarka
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

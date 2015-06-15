# SimpleFragment
A fragment-like abstraction for Android that is easier to use and understand

**Warning!** The api here is still experimental and may be changed at any time.

The purpose of this library is to build a better foundation for working with the controller layer in Android-style MVC. If you have ever worked with Fragments, you know that they feel more complicated than they need to be AND sometimes have unexpected behavior. This is especially true when you try to nest them. SimpleFragment provides an api that is very similar to native fragments but is more powerful _and_ easier to understand.

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

You may notice that you are making an api call right in the fragment. This is perfectly fine! This is because SimpleFragments **survive orientation changes**.

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
  SimpleFragmentContainer container;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getSimpleFragmentManager().findOrAdd(SimpleFragmentIntent.of(HelloWorldFragment.class), LayoutKey.of(R.id.my_fragment));
  }
}
```

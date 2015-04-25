# SimpleFragment
A fragment-like abstraction for Android that is easier to use and understand

**Warning!** The api here is still experimental and may be changed at any time.

The purpose of this library is to build a better foundation for working with the controller layer in Android-style MVC. If you have ever worked with Fragments, you know that they feel more complicated than they need to be, sometimes have unexpected behavior, and are not very extensible. SimpleFragment provides an api that is very similar to native fragments but is more powerful, extensible, _and_ easier to understand.

Here an example of a SimpleFragment that gets a string from a network call and populates a `TextView`.
```java
public class HelloWorldFragment extends SimpleFragment<SimpleFragment.ViewHolder> {
  static final String STATE_HELLO_TEXT = "STATE_HELLO_TEXT";
  String helloText;
  
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
          Holder holder = getViewHolder();
          if (holder != null) {
            holder.text.setText(helloText);
          }
        }
      });
    }
  }
    
  @Override
  public void onSave(Context context, Bundle state) {
    state.putString(STATE_HELLO_TEXT, helloText);
  }
  
  @Override
  public ViewHolder onCreateViewHolder(final LayoutInflater inflater, final ViewGroup parent) {
    View view = inflater.inflate(R.layout.hello_world, parent, false);
    return new Holder(view);
  }
  
  class Holder implements SimpleFragment.ViewHolder {
    View view;
    TextView text;
    
    Holder(View view) {
      this.view = view;
      this.text = view.findViewById(R.id.text);
      this.text.setText(helloText);
    }
  
    @Override
    public View getView() {
      return view;
    }
  }
}
```

You'll notice that each SimpleFragment is made up of two distinct objects. The outer `SimpleFragment`, and an inner `SimpleFragment.ViewHolder`. The reason for this is to force you to seperate configuration-based code from nonconfigution-based code. This is due to one of the major differences of SimpleFragment over native ones. They are _not_ destroyed on configuration changes. This allows you to make network and other assyncrouns calls right in the fragment without issue.

In order to use SimpleFragments in an Activity you should subclass `SimpleFragmentActivity` or `SimpleFragmentActionBarActivity`. You can then either add the SimpleFragment in the layout
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
    container = SimpleFragmentContainer.getInstance(this);
    container.findOrAdd(new SimpleFragmentIntent<>(HelloWorldFragment.class), R.id.my_fragment);
  }
}
```

package me.tatarka.simplefragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

/**
 * A custom {@code LayoutInflater.Factory} that instantiates fragment tags as SimpleFragments instead of
 * Android fragments.
 */
public class SimpleFragmentViewInflater implements LayoutInflaterFactory {
    private static final int[] ATTRS = new int[]{
            android.R.attr.name
    };
    private static final int ATTR_NAME = 0;

    private SimpleFragmentContainer container;

    public SimpleFragmentViewInflater(SimpleFragmentContainerManagerProvider provider) {
        this(SimpleFragmentContainer.getInstance(provider));
    }

    public SimpleFragmentViewInflater(SimpleFragmentContainer container) {
        this.container = container;
    }

    public final View createView(View parent, final String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        if (!name.equals("fragment")) {
            return null;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        String fragmentName = a.getString(ATTR_NAME);
        a.recycle();

        if (fragmentName == null) {
            throw new NullPointerException("SimpleFragment name is null.");
        }

        try {
            Class<? extends SimpleFragment> fragmentClass = (Class<? extends SimpleFragment>) Class.forName(fragmentName);
            FrameLayout view = new FrameLayout(context, attrs);
            container.findOrAdd(new SimpleFragmentIntent<>(fragmentClass), view.getId());
            return view;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return createView(parent, name, context, attrs);
    }
}

package me.tatarka.simplefragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import me.tatarka.simplefragment.key.LayoutKey;
import me.tatarka.simplefragment.key.SimpleFragmentKey;

/**
 * A custom {@code LayoutInflater.Factory} that instantiates fragment tags as SimpleFragments instead of
 * Android fragments.
 */
public class SimpleFragmentLayoutInflaterFactory implements LayoutInflater.Factory {
    private static final int[] ATTRS = new int[]{
            android.R.attr.name
    };
    private static final int ATTR_NAME = 0;

    @Nullable
    public static View onCreateView(SimpleFragmentContainer container, String name, Context context, AttributeSet attrs) {
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

    private SimpleFragmentContainer container;

    public SimpleFragmentLayoutInflaterFactory(SimpleFragmentContainer container) {
        this.container = container;
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return onCreateView(container, name, context, attrs);
    }
}

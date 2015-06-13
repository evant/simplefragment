package me.tatarka.simplefragment.key;

import android.support.annotation.Nullable;
import android.view.View;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentManager;

/**
 * A key that is used it a {@link SimpleFragmentManager}. These keys know how to attach and detach
 * themselves to the view hierarchy.
 */
public interface SimpleFragmentContainerKey extends SimpleFragmentKey {
    void attach(SimpleFragmentManager container, View rootView, SimpleFragment fragment);

    void detach(SimpleFragmentManager container, View rootView, SimpleFragment fragment);

    SimpleFragmentContainerKey withParent(SimpleFragmentKey parent);

    boolean matches(@Nullable SimpleFragmentContainerKey other);
}

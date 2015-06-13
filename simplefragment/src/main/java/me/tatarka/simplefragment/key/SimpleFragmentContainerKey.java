package me.tatarka.simplefragment.key;

import android.support.annotation.Nullable;
import android.view.View;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentContainer;

/**
 * A key that is used it a {@link SimpleFragmentContainer}. These keys know how to attach and detach
 * themselves to the view hierarchy.
 */
public interface SimpleFragmentContainerKey extends SimpleFragmentKey {
    void attach(SimpleFragmentContainer container, View rootView, SimpleFragment fragment);

    void detach(SimpleFragmentContainer container, View rootView, SimpleFragment fragment);

    SimpleFragmentKey getParent();

    SimpleFragmentContainerKey withParent(SimpleFragmentKey parent);

    boolean matches(@Nullable SimpleFragmentContainerKey other);
}

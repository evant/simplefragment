package me.tatarka.simplefragment.key;

import android.view.View;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentContainer;

/**
 * A key that is used it a {@link SimpleFragmentContainer}. These keys know how to attach and detach
 * themselves to the view hierarchy.
 */
public abstract class SimpleFragmentContainerKey implements SimpleFragmentKey {
    public abstract void attach(SimpleFragmentContainer container, View rootView, SimpleFragment fragment);

    public abstract void detach(SimpleFragmentContainer container, View rootView, SimpleFragment fragment);
    
    public abstract SimpleFragmentKey getParent();
}

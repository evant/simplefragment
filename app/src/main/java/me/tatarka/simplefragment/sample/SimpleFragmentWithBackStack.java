package me.tatarka.simplefragment.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentContainer;
import me.tatarka.simplefragment.SimpleFragmentIntent;

import static me.tatarka.simplefragment.sample.SimpleFragmentChildWithBackStack.ARG_STACK_COUNT;
import static me.tatarka.simplefragment.sample.SimpleFragmentChildWithBackStack.ARG_VIEW_ID;

/**
 * Created by evan on 2/2/15.
 */
public class SimpleFragmentWithBackStack extends SimpleFragment<SimpleFragment.ViewHolder> {
    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
        final SimpleFragmentContainer container = getSimpleFragmentContainer();

        container.findOrAdd(
                new SimpleFragmentIntent<>(SimpleFragmentChildWithBackStack.class)
                        .putArg(ARG_VIEW_ID, R.id.child_fragment1), R.id.child_fragment1);
        container.findOrAdd(
                new SimpleFragmentIntent<>(SimpleFragmentChildWithBackStack.class)
                        .putArg(ARG_VIEW_ID, R.id.child_fragment2), R.id.child_fragment2);

        final SimpleFragmentChildWithBackStack.OnRemoveListener removeListener = new SimpleFragmentChildWithBackStack.OnRemoveListener() {
            @Override
            public void onRemove(SimpleFragmentChildWithBackStack fragment) {
                container.remove(fragment);
            }
        };

        final SimpleFragmentChildWithBackStack.OnAddListener addListener = new SimpleFragmentChildWithBackStack.OnAddListener() {
            @Override
            public void onAdd(int viewId, int stackCount) {
                SimpleFragmentChildWithBackStack newFragment = container.push(
                        new SimpleFragmentIntent<>(SimpleFragmentChildWithBackStack.class)
                                .putArg(ARG_STACK_COUNT, stackCount + 1)
                                .putArg(ARG_VIEW_ID, viewId), viewId);
                newFragment.setListeners(this, removeListener);
            }
        };

        for (SimpleFragment<?> fragment : container.getFragments()) {
            ((SimpleFragmentChildWithBackStack) fragment).setListeners(addListener, removeListener);
        }
    }
    
    @Override
    public ViewHolder onCreateViewHolder(final LayoutInflater inflater, final ViewGroup parent) {
        return new ViewHolder() {
            @Override
            public View getView() {
                return inflater.inflate(R.layout.fragment_with_backstack, parent, false);
            }
        };
    }
}

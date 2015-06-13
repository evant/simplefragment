package me.tatarka.simplefragment.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.key.LayoutKey;

import static me.tatarka.simplefragment.sample.SimpleFragmentChildWithBackStack.ARG_STACK_COUNT;
import static me.tatarka.simplefragment.sample.SimpleFragmentChildWithBackStack.ARG_VIEW_ID;

/**
 * Created by evan on 2/2/15.
 */
public class SimpleFragmentWithBackStack extends SimpleFragment implements SimpleFragmentChildWithBackStack.OnBackStackRequestListener {

    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
        final SimpleFragmentManager container = getSimpleFragmentManager();

        container.findOrAdd(
                SimpleFragmentIntent.of(SimpleFragmentChildWithBackStack.class)
                        .putExtra(ARG_VIEW_ID, R.id.child_fragment1), LayoutKey.of(R.id.child_fragment1));
        container.findOrAdd(
                SimpleFragmentIntent.of(SimpleFragmentChildWithBackStack.class)
                        .putExtra(ARG_VIEW_ID, R.id.child_fragment2), LayoutKey.of(R.id.child_fragment2));
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent) {
        return inflater.inflate(R.layout.fragment_with_backstack, parent, false);
    }

    @Override
    public void onAdd(int viewId, int stackCount) {
        getSimpleFragmentManager().push(
                SimpleFragmentIntent.of(SimpleFragmentChildWithBackStack.class)
                        .putExtra(ARG_STACK_COUNT, stackCount + 1)
                        .putExtra(ARG_VIEW_ID, viewId), LayoutKey.of(viewId));
    }

    @Override
    public void onRemove(SimpleFragmentChildWithBackStack fragment) {
        getSimpleFragmentManager().remove(fragment);
    }
}

package me.tatarka.simplefragment.test;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentIntent;

/**
 * Since this class is constructed through reflection, we can't use a mock. Instead use a poor-man's
 * version by setting booleans when methods are called.
 */
public class TestSimpleFragment extends SimpleFragment {
    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent) {
        return new TestSimpleFragmentRootView(inflater.getContext());
    }
}

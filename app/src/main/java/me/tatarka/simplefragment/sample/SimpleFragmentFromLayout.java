package me.tatarka.simplefragment.sample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentIntent;

/**
 * Created by evan on 2/2/15.
 */
public class SimpleFragmentFromLayout extends SimpleFragment {
    @Override
    public void onCreate(Context context, @Nullable Bundle state) {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup parent) {
        return inflater.inflate(R.layout.fragment_from_layout, parent, false);
    }
}

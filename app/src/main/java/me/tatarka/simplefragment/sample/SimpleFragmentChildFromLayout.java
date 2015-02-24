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
public class SimpleFragmentChildFromLayout extends SimpleFragment<SimpleFragment.ViewHolder> {
    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
        
    }

    @Override
    public ViewHolder onCreateViewHolder(final LayoutInflater inflater, final ViewGroup parent) {
        return new ViewHolder() {
            @Override
            public View getView() {
                return inflater.inflate(R.layout.fragment_child_from_layout, parent, false);
            }
        };
    }
}

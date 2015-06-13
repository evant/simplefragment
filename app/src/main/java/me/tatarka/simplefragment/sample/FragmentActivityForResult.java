package me.tatarka.simplefragment.sample;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.key.LayoutKey;

/**
 * Created by evan on 6/13/15.
 */
public class FragmentActivityForResult extends SimpleFragment {
    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
        SimpleFragmentManager manager = getSimpleFragmentManager();

        manager.findOrAdd(SimpleFragmentIntent.of(FragmentChildActivityForResult.class), LayoutKey.of(R.id.child_fragment1));
        manager.findOrAdd(SimpleFragmentIntent.of(FragmentChildActivityForResult.class), LayoutKey.of(R.id.child_fragment2));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        return inflater.inflate(R.layout.fragment_activity_for_result, parent, false);
    }
}

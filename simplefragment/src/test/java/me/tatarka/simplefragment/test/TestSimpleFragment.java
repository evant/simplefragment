package me.tatarka.simplefragment.test;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentIntent;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Since this class is constructed through reflection, we can't use a mock. Instead use a poor-man's
 * version by setting booleans when methods are called.
 */
public class TestSimpleFragment extends SimpleFragment<SimpleFragment.ViewHolder> {
    public boolean wasOnCreateCalled;
    public boolean wasOnCreateViewHolderCalled;
    public boolean wasOnSaveCalled;
    public boolean wasGetViewCalled;
    
    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
        wasOnCreateCalled = true; 
    }

    @Override
    public ViewHolder onCreateViewHolder(LayoutInflater inflater, final ViewGroup parent) {
        wasOnCreateViewHolderCalled = true;
        return new ViewHolder() {
            @Override
            public View getView() {
                wasGetViewCalled = true;
                View view = mock(View.class);
                when(view.getParent()).thenReturn(parent);
                ViewGroup nestedView = mock(FrameLayout.class);
                when(nestedView.findViewById(anyInt())).thenReturn(nestedView);
                when(view.findViewById(anyInt())).thenReturn(nestedView);
                return view;
            }
        };
    }

    @Override
    public void onSave(Context context, Bundle state) {
        wasOnSaveCalled = true;    
    }
}

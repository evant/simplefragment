package me.tatarka.simplefragment.test;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import me.tatarka.simplefragment.SimpleFragment;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockingDetails;
import static org.mockito.Mockito.when;

/**
 * Since this class is constructed through reflection, we can't use a mock. Instead use a poor-man's
 * version by setting booleans when methods are called.
 */
public class TestSimpleFragment extends SimpleFragment {
    public boolean wasOnCreateCalled;
    public boolean wasOnCreateViewCalled;
    public boolean wasOnSaveCalled;

    @Override
    public void onCreate(Context context, @Nullable Bundle state) {
        wasOnCreateCalled = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent) {
        wasOnCreateViewCalled = true;
        View view = mock(View.class);
        when(view.getParent()).thenReturn(parent);
        ViewGroup nestedView = mock(FrameLayout.class);
        when(nestedView.findViewById(anyInt())).thenReturn(nestedView);
        when(view.findViewById(anyInt())).thenReturn(nestedView);
        return view;
    }

    @Override
    public void onSave(@NonNull Bundle state) {
        wasOnSaveCalled = true;
    }

    @Override
    public LayoutInflater getLayoutInflater() {
        LayoutInflater inflater = mock(LayoutInflater.class);
        when(inflater.cloneInContext(any(Context.class))).thenReturn(inflater);
        return inflater;
    }
}

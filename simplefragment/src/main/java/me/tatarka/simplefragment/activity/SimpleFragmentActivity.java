package me.tatarka.simplefragment.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import me.tatarka.simplefragment.SimpleFragmentContainer;
import me.tatarka.simplefragment.SimpleFragmentContainerProvider;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.SimpleFragmentManagerProvider;

/**
 * Created by evan on 3/7/15.
 */
public class SimpleFragmentActivity extends Activity implements SimpleFragmentManagerProvider, SimpleFragmentContainerProvider {
    private SimpleFragmentDelegate delegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSimpleFragmentDelegate().installViewFactory(null);
        super.onCreate(savedInstanceState);
        getSimpleFragmentDelegate().onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        getSimpleFragmentDelegate().onSetContentView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        getSimpleFragmentDelegate().onSetContentView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        getSimpleFragmentDelegate().onSetContentView();
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        super.addContentView(view, params);
        getSimpleFragmentDelegate().onSetContentView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSimpleFragmentDelegate().onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSimpleFragmentDelegate().onSaveInstanceState(outState);
    }

    @Override
    public final Object onRetainNonConfigurationInstance() {
        return getSimpleFragmentDelegate().onRetainNonConfigurationInstance(onRetainCustomNonConfigurationInstance());
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return null;
    }

    public Object getLastCustomNonCofigurationInstance() {
        return getSimpleFragmentDelegate().getLastCustomNonConfigurationInstance(getLastNonConfigurationInstance());
    }

    @Override
    public void onBackPressed() {
        if (!getSimpleFragmentDelegate().onBackPress()) {
            super.onBackPressed();
        }
    }

    @Override
    public SimpleFragmentManager getSimpleFragmentManager() {
        return getSimpleFragmentDelegate().getSimpleFragmentManager();
    }

    @Override
    public SimpleFragmentContainer getSimpleFragmentContainer() {
        return getSimpleFragmentDelegate().getSimpleFragmentContainer();
    }

    public SimpleFragmentDelegate getSimpleFragmentDelegate() {
        if (delegate == null) {
            delegate = SimpleFragmentDelegate.create(this);
        }
        return delegate;
    }
}

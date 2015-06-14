package me.tatarka.simplefragment.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.SimpleFragmentManagerProvider;

/**
 * Created by evan on 3/7/15.
 */
public class SimpleFragmentActivity extends Activity implements SimpleFragmentManagerProvider, SimpleFragmentDelegate.Methods {
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
    public void startActivityFromFragment(SimpleFragment fragment, Intent intent, int requestCode, @Nullable Bundle options) {
        int maskedRequestCode = getSimpleFragmentDelegate().getMaskedRequestCode(fragment, requestCode);
        if (Build.VERSION.SDK_INT >= 16) {
            super.startActivityForResult(intent, maskedRequestCode, options);
        } else {
            super.startActivityForResult(intent, maskedRequestCode);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        getSimpleFragmentDelegate().checkStartActivityForResult(intent, requestCode, null);
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        getSimpleFragmentDelegate().checkStartActivityForResult(intent, requestCode, options);
        super.startActivityForResult(intent, requestCode, options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getSimpleFragmentDelegate().onActivityResult(requestCode, resultCode, data);
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

    private SimpleFragmentDelegate getSimpleFragmentDelegate() {
        if (delegate == null) {
            delegate = SimpleFragmentDelegate.create(this);
        }
        return delegate;
    }

}

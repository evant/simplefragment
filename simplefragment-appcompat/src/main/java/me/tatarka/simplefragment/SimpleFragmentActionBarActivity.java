package me.tatarka.simplefragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import me.tatarka.simplefragment.activity.SimpleFragmentActivityHelper;

/**
 * Created by evan on 2/2/15.
 */
public class SimpleFragmentActionBarActivity extends ActionBarActivity {
    private SimpleFragmentActivityHelper helper = new SimpleFragmentActivityHelper(new SimpleFragmentActivityHelper.ActivityInfo() {
        @Override
        public Context getContext() {
            return SimpleFragmentActionBarActivity.this;
        }

        @Override
        public LayoutInflater getLayoutInflater() {
            return SimpleFragmentActionBarActivity.super.getLayoutInflater();
        }

        @Override
        public View getRootView() {
            return findViewById(android.R.id.content);
        }

        @Override
        public Object getLastNonConfigurationInstance() {
            return SimpleFragmentActionBarActivity.this.getLastCustomNonConfigurationInstance();
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        helper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        helper.onSaveInstanceState(outState);
    }
    
    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        View view = helper.onCreateView(name, context, attrs);
        return view != null ? view : super.onCreateView(name, context, attrs);
    }
    
    @Override
    public final Object onRetainCustomNonConfigurationInstance() {
        return helper.onRetainNonConfigurationInstance(onRetainCustomNonConfigurationInstance2());
    }

    @Override
    @Deprecated
    public Object getLastCustomNonConfigurationInstance() {
        return super.getLastCustomNonConfigurationInstance();
    }

    public Object onRetainCustomNonConfigurationInstance2() {
        return null;
    }
    
    public Object getLastCustomNonConfigurationInstance2() {
        return helper.getLastCustomNonConfigurationInstance(getLastCustomNonConfigurationInstance());
    }
    
    @Override
    public void onBackPressed() {
        if (!helper.onBackPress()) {
            super.onBackPressed();
        }
    }

    public SimpleFragmentManager getSimpleFragmentManager() {
        return helper.getSimpleFragmentManager();
    }
    
    public SimpleFragmentContainer getSimpleFragmentContainer() {
        return helper.getSimpleFragmentContainer();
    }
}

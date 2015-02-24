package me.tatarka.simplefragment.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import me.tatarka.simplefragment.SimpleFragmentContainer;
import me.tatarka.simplefragment.SimpleFragmentManager;

/**
 * Created by evan on 3/7/15.
 */
public class SimpleFragmentActivity extends Activity {
    private SimpleFragmentActivityHelper helper = new SimpleFragmentActivityHelper(new SimpleFragmentActivityHelper.ActivityInfo() {
        @Override
        public Context getContext() {
            return SimpleFragmentActivity.this;
        }

        @Override
        public LayoutInflater getLayoutInflater() {
            return SimpleFragmentActivity.super.getLayoutInflater();
        }

        @Override
        public View getRootView() {
            return findViewById(android.R.id.content);
        }

        @Override
        public Object getLastNonConfigurationInstance() {
            return SimpleFragmentActivity.this.getLastNonConfigurationInstance();
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
    public final Object onRetainNonConfigurationInstance() {
        return helper.onRetainNonConfigurationInstance(onRetainCustomNonConfigurationInstance());
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return null;
    }

    public Object getLastCustomNonCofigurationInstance() {
        return helper.getLastCustomNonConfigurationInstance(getLastNonConfigurationInstance());
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

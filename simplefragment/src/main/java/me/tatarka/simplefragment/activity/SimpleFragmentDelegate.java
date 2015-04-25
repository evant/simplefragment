package me.tatarka.simplefragment.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import me.tatarka.simplefragment.SimpleFragmentContainer;
import me.tatarka.simplefragment.SimpleFragmentContainerManager;
import me.tatarka.simplefragment.SimpleFragmentContainerManagerProvider;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.SimpleFragmentManagerProvider;
import me.tatarka.simplefragment.SimpleFragmentViewInflater;
import me.tatarka.simplefragment.backstack.SimpleFragmentBackStack;

/**
 * Created by evan on 3/7/15.
 */
public class SimpleFragmentDelegate implements SimpleFragmentManagerProvider, SimpleFragmentContainerManagerProvider, LayoutInflaterFactory {
    private static final String TAG = "SimpleFragmentDelegate";
    private static final String STATE = "me.tatarka.simplefragment.STATE";

    private Activity activity;
    private SimpleFragmentManager fm;
    private SimpleFragmentContainerManager cm;
    private SimpleFragmentViewInflater viewInflater;
    private LayoutInflaterFactory delegateFactory;

    public static SimpleFragmentDelegate create(Activity activity) {
        return new SimpleFragmentDelegate(activity);
    }

    private SimpleFragmentDelegate(Activity activity) {
        this.activity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        Context context = activity;

        if (savedInstanceState == null) {
            fm = new SimpleFragmentManager(context);
            cm = new SimpleFragmentContainerManager(fm, null);
        } else {
            Object lastNonConfigInstance = activity.getLastNonConfigurationInstance();
            if (lastNonConfigInstance != null) {
                NonConfigInstance instance = (NonConfigInstance) lastNonConfigInstance;
                fm = instance.fm;
                fm.restoreConfigurationState(context);
                cm = instance.cm;
            } else {
                State state = savedInstanceState.getParcelable(STATE);
                fm = new SimpleFragmentManager(context);
                cm = new SimpleFragmentContainerManager(fm, null);
                fm.restoreState(state.fmState);
                cm.restoreState(state.cmState);
            }
        }

        View rootView = activity.getWindow().getDecorView();
        cm.setView(rootView);
    }

    public void onDestroy() {
        fm.clearConfigurationState();
        cm.clearView();
    }

    public void onSaveInstanceState(Bundle outState) {
        State state = new State(fm.saveState(), cm.saveState());
        outState.putParcelable(STATE, state);
    }

    public Object onRetainNonConfigurationInstance(@Nullable Object clientState) {
        NonConfigInstance instance = new NonConfigInstance();
        instance.fm = fm;
        instance.cm = cm;
        instance.clientState = clientState;
        return instance;
    }

    public Object getLastCustomNonConfigurationInstance(Object nonConfigInstance) {
        NonConfigInstance instance = (NonConfigInstance) nonConfigInstance;
        return instance.clientState;
    }

    @Nullable
    public View createView(View parent, String name, Context context, AttributeSet attrs) {
        if (viewInflater == null) {
            viewInflater = new SimpleFragmentViewInflater(this);
        }
        return viewInflater.createView(parent, name, context, attrs);
    }

    public void installViewFactory(@Nullable LayoutInflaterFactory delegateFactory) {
        this.delegateFactory = delegateFactory;
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        if (layoutInflater.getFactory() == null) {
            LayoutInflaterCompat.setFactory(layoutInflater, this);
        } else {
            Log.i(TAG, "The Activity's LayoutInflater already has a Factory installed"
                    + " so we can not install AppCompat's");
        }
    }

    /**
     * From {@link android.support.v4.view.LayoutInflaterFactory}
     */
    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        // First let the Activity's Factory try and inflate the view
        View view = activity.onCreateView(name, context, attrs);
        if (view != null) {
            return view;
        }
        // If that doesn't work, try our delegate factory.
        if (delegateFactory != null) {
            view = delegateFactory.onCreateView(parent, name, context, attrs);
            if (view != null) {
                return view;
            }
        }

        // If the Factory didn't handle it, let our createView() method try
        return createView(parent, name, context, attrs);
    }

    @Override
    public SimpleFragmentManager getSimpleFragmentManager() {
        return fm;
    }

    @Override
    public SimpleFragmentContainerManager getSimpleFragmentContainerManager() {
        return cm;
    }

    public boolean onBackPress() {
        return SimpleFragmentBackStack.getInstance(fm).pop();
    }

    private static class NonConfigInstance {
        SimpleFragmentManager fm;
        SimpleFragmentContainerManager cm;
        Object clientState;
    }

    private static class State implements Parcelable {
        Parcelable fmState;
        Parcelable cmState;

        State(Parcelable fmState, Parcelable cmState) {
            this.fmState = fmState;
            this.cmState = cmState;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(fmState, flags);
            dest.writeParcelable(cmState, flags);
        }

        private State(Parcel in) {
            this.fmState = in.readParcelable(SimpleFragmentManager.class.getClassLoader());
            this.cmState = in.readParcelable(SimpleFragmentContainer.class.getClassLoader());
        }

        public static final Parcelable.Creator<State> CREATOR = new Parcelable.Creator<State>() {
            public State createFromParcel(Parcel source) {
                return new State(source);
            }

            public State[] newArray(int size) {
                return new State[size];
            }
        };
    }

}

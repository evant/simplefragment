package me.tatarka.simplefragment.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import me.tatarka.simplefragment.SimpleFragmentContainer;
import me.tatarka.simplefragment.SimpleFragmentContainerManager;
import me.tatarka.simplefragment.SimpleFragmentContainerManagerProvider;
import me.tatarka.simplefragment.SimpleFragmentDialogContainer;
import me.tatarka.simplefragment.SimpleFragmentLayoutInflaterFactory;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.SimpleFragmentManagerProvider;
import me.tatarka.simplefragment.backstack.SimpleFragmentBackStack;
import me.tatarka.simplefragment.key.SimpleFragmentKey;

/**
 * Created by evan on 3/7/15.
 */
public class SimpleFragmentActivityHelper implements SimpleFragmentManagerProvider, SimpleFragmentContainerManagerProvider {
    private static final String STATE = "me.tatarka.simplefragment.STATE";

    private ActivityInfo info;
    private SimpleFragmentManager fm;
    private SimpleFragmentContainerManager cm;

    public SimpleFragmentActivityHelper(ActivityInfo info) {
        this.info = info;
    }

    public void onCreate(Bundle savedInstanceState) {
        Context context = info.getContext();

        if (savedInstanceState == null) {
            fm = new SimpleFragmentManager(context);
            cm = new SimpleFragmentContainerManager(fm, null);
        } else {
            Object lastNonConfigInstance = info.getLastNonConfigurationInstance();
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

        View rootView = info.getRootView();
        LayoutInflater layoutInflater = info.getLayoutInflater();
        cm.setView(layoutInflater, rootView);
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

    public View onCreateView(String name, Context context, AttributeSet attrs) {
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        return SimpleFragmentLayoutInflaterFactory.onCreateView(container, name, context, attrs);
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

    public interface ActivityInfo {
        Context getContext();

        LayoutInflater getLayoutInflater();

        View getRootView();

        Object getLastNonConfigurationInstance();
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

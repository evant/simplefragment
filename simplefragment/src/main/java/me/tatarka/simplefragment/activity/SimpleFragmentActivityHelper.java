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
import me.tatarka.simplefragment.SimpleFragmentDialogContainer;
import me.tatarka.simplefragment.SimpleFragmentLayoutInflaterFactory;
import me.tatarka.simplefragment.SimpleFragmentManager;

/**
 * Created by evan on 3/7/15.
 */
public class SimpleFragmentActivityHelper {
    private static final String STATE = "me.tatarka.simplefragment.STATE";

    private ActivityInfo info;
    private SimpleFragmentManager fm;
    private SimpleFragmentContainer container;
    private SimpleFragmentDialogContainer dialogContainer;

    public SimpleFragmentActivityHelper(ActivityInfo info) {
        this.info = info;
    }

    public void onCreate(Bundle savedInstanceState) {
        Context context = info.getContext();

        if (savedInstanceState == null) {
            fm = new SimpleFragmentManager(context);
            container = new SimpleFragmentContainer(fm, null);
            dialogContainer = new SimpleFragmentDialogContainer(fm, null);
        } else {
            Object lastNonConfigInstance = info.getLastNonConfigurationInstance();
            if (lastNonConfigInstance != null) {
                NonConfigInstance instance = (NonConfigInstance) lastNonConfigInstance;
                fm = instance.fm;
                container = instance.container;
                dialogContainer = instance.dialogContainer;
                fm.restoreConfigurationState(context);
            } else {
                fm = new SimpleFragmentManager(context);
                State state = savedInstanceState.getParcelable(STATE);
                container = new SimpleFragmentContainer(fm, null);
                container.restoreState(state.containerState);
                dialogContainer = new SimpleFragmentDialogContainer(fm, null);
                dialogContainer.restoreState(state.dialogContainerState);
                fm.restoreState(state.fmState);
            }
        }

        View rootView = info.getRootView();
        LayoutInflater layoutInflater = info.getLayoutInflater();
        container.setRootView(rootView, layoutInflater);
        dialogContainer.setRootView(layoutInflater);
    }
    
    public void onDestroy() {
        fm.clearConfigurationState();
        container.clearRootView();
        dialogContainer.clearRootView();
    }

    public void onSaveInstanceState(Bundle outState) {
        State state = new State(fm.saveState(), container.saveState(), dialogContainer.saveState());
        outState.putParcelable(STATE, state);
    }

    public Object onRetainNonConfigurationInstance(@Nullable Object clientState) {
        NonConfigInstance instance = new NonConfigInstance();
        instance.fm = fm;
        instance.container = container;
        instance.dialogContainer = dialogContainer;
        instance.clientState = clientState;
        return instance;
    }

    public Object getLastCustomNonConfigurationInstance(Object nonConfigInstance) {
        NonConfigInstance instance = (NonConfigInstance) nonConfigInstance;
        return instance.clientState;
    }

    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return SimpleFragmentLayoutInflaterFactory.onCreateView(container, name, context, attrs);
    }

    public SimpleFragmentManager getSimpleFragmentManager() {
        return fm;
    }

    public SimpleFragmentContainer getSimpleFragmentContainer() {
        return container;
    }

    public boolean onBackPress() {
        return container.popGlobal();
    }

    public interface ActivityInfo {
        Context getContext();

        LayoutInflater getLayoutInflater();

        View getRootView();

        Object getLastNonConfigurationInstance();
    }

    private static class NonConfigInstance {
        SimpleFragmentManager fm;
        SimpleFragmentContainer container;
        SimpleFragmentDialogContainer dialogContainer;
        Object clientState;
    }

    private static class State implements Parcelable {
        Parcelable fmState;
        Parcelable containerState;
        Parcelable dialogContainerState;

        State(Parcelable fmState, Parcelable containerState, Parcelable dialogContainerState) {
            this.fmState = fmState;
            this.containerState = containerState;
            this.dialogContainerState = dialogContainerState;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(fmState, flags);
            dest.writeParcelable(containerState, flags);
            dest.writeParcelable(dialogContainerState, flags);
        }

        private State(Parcel in) {
            this.fmState = in.readParcelable(SimpleFragmentManager.class.getClassLoader());
            this.containerState = in.readParcelable(SimpleFragmentContainer.class.getClassLoader());
            this.dialogContainerState = in.readParcelable(SimpleFragmentDialogContainer.class.getClassLoader());
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

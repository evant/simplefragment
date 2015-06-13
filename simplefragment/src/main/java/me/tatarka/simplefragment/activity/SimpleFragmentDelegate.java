package me.tatarka.simplefragment.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import java.util.List;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.SimpleFragmentManagerProvider;
import me.tatarka.simplefragment.SimpleFragmentStateManager;
import me.tatarka.simplefragment.SimpleFragmentViewInflater;

/**
 * A delegate class for implementing SimpleFragments in your own Activity. While there are a lot of
 * methods, implementing is mostly a matter of overriding all the methods with the same name and
 * calling the delegate. See {@link SimpleFragmentActivity} for an example on how this is done.
 */
public class SimpleFragmentDelegate implements SimpleFragmentManagerProvider, LayoutInflaterFactory {
    private static final String TAG = "SimpleFragmentDelegate";
    private static final String STATE = "me.tatarka.simplefragment.STATE";

    private Activity activity;
    private SimpleFragmentStateManager stateManager;
    private SimpleFragmentManager manager;
    private SimpleFragmentViewInflater viewInflater;
    private LayoutInflaterFactory delegateFactory;
    private boolean isRootViewSet = false;

    public static SimpleFragmentDelegate create(Activity activity) {
        return new SimpleFragmentDelegate(activity);
    }

    private SimpleFragmentDelegate(Activity activity) {
        this.activity = activity;
    }

    public void onCreate(Bundle savedInstanceState) {
        Activity activity = this.activity;

        if (savedInstanceState == null) {
            stateManager = new SimpleFragmentStateManager(activity);
            manager = new SimpleFragmentManager(stateManager, null);
        } else {
            Object lastNonConfigInstance;
            if (this.activity instanceof FragmentActivity) {
                lastNonConfigInstance = ((FragmentActivity) this.activity).getLastCustomNonConfigurationInstance();
            } else {
                lastNonConfigInstance = this.activity.getLastNonConfigurationInstance();
            }

            if (lastNonConfigInstance != null) {
                NonConfigInstance instance = (NonConfigInstance) lastNonConfigInstance;
                stateManager = instance.stateManager;
                stateManager.restoreConfigurationState(activity);
                manager = instance.manager;
            } else {
                State state = savedInstanceState.getParcelable(STATE);
                stateManager = new SimpleFragmentStateManager(activity);
                manager = new SimpleFragmentManager(stateManager, null);
                stateManager.restoreState(state.stateManagerState);
                manager.restoreState(state.managerState);
            }
        }
    }

    public void onSetContentView() {
        if (!isRootViewSet) {
            isRootViewSet = true;
            View rootView = activity.findViewById(android.R.id.content);
            manager.setView(rootView);
        }
    }

    public void onDestroy() {
        stateManager.clearConfigurationState();
        manager.clearView();
    }

    public void onSaveInstanceState(Bundle outState) {
        State state = new State(stateManager.saveState(), manager.saveState());
        outState.putParcelable(STATE, state);
    }

    public Object onRetainNonConfigurationInstance(@Nullable Object clientState) {
        NonConfigInstance instance = new NonConfigInstance();
        instance.stateManager = stateManager;
        instance.manager = manager;
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
        return manager;
    }

    public boolean onBackPress() {
        return stateManager.getBackStack().pop();
    }

    public interface Methods {
        void startActivityFromFragment(SimpleFragment fragment, Intent intent, int requestCode, @Nullable Bundle options);
    }

    public int getMaskedRequestCode(SimpleFragment fragment, int requestCode) {
        if (requestCode == -1) {
            return -1;
        }
        if ((requestCode & 0xffff0000) != 0) {
            throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
        }
        int index = stateManager.getFragments().indexOf(fragment);
        return ((index + 1) << 16) + (requestCode & 0xffff);
    }

    @TargetApi(16)
    public void startActivityForResult(Intent intent, int requestCode, @Nullable Bundle options) {
        if (requestCode != -1 && (requestCode & 0xffff0000) != 0) {
            throw new IllegalArgumentException("Can only use lower 16 bits for requestCode");
        }
        if (Build.VERSION.SDK_INT >= 16) {
            activity.startActivityForResult(intent, requestCode, options);
        } else {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        int index = requestCode >> 16;
        if (index != 0) {
            index--;
            List<SimpleFragment> fragments = stateManager.getFragments();
            if (index < 0 || index >= fragments.size()) {
                Log.w(TAG, "Activity result fragment index out of range: 0x"
                        + Integer.toHexString(requestCode));
            } else {
                SimpleFragment fragment = fragments.get(index);
                fragment.onActivityResult(requestCode & 0xffff, resultCode, data);
                return true;
            }
        }
        return false;
    }

    private static class NonConfigInstance {
        SimpleFragmentStateManager stateManager;
        SimpleFragmentManager manager;
        Object clientState;
    }

    private static class State implements Parcelable {
        Parcelable stateManagerState;
        Parcelable managerState;

        State(Parcelable stateManagerState, Parcelable managerState) {
            this.stateManagerState = stateManagerState;
            this.managerState = managerState;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(stateManagerState, flags);
            dest.writeParcelable(managerState, flags);
        }

        private State(Parcel in) {
            this.stateManagerState = in.readParcelable(SimpleFragmentStateManager.class.getClassLoader());
            this.managerState = in.readParcelable(SimpleFragmentManager.class.getClassLoader());
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

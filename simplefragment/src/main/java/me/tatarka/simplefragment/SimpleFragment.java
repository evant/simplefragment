package me.tatarka.simplefragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.simplefragment.key.SimpleFragmentKey;

/**
 * Created by evan on 1/11/15.
 */
public abstract class SimpleFragment<V extends SimpleFragment.ViewHolder> {
    private V viewHolder;
    private View view;
    private State state = new State();
    private SimpleFragmentManager fm;
    private SimpleFragmentContainer container;
    private SimpleFragmentDialogContainer dialogContainer;
    private List<LifecycleListener<V>> lifecycleListeners = new ArrayList<>();

    public abstract void onCreate(Context context, @Nullable Bundle state);

    public void onSave(Context context, Bundle state) {
    }

    public abstract V onCreateViewHolder(LayoutInflater inflater, ViewGroup parent);

    public void onViewHolderCreated(V viewHolder, View view) {
    }
    
    public void onDestroyViewHolder() {
    }

    public boolean onBackPressed() {
        return false;
    }

    final void create(SimpleFragmentManager fm, SimpleFragmentIntent intent, SimpleFragmentKey key) {
        this.fm = fm;
        this.container = new SimpleFragmentContainer(fm, key);
        this.dialogContainer = new SimpleFragmentDialogContainer(fm, key);
        state.intent = intent;
        state.key = key;
        onCreate(fm.getContext().getApplicationContext(), this.state.state);
    }

    final View createView(LayoutInflater inflater, ViewGroup parent) {
        viewHolder = onCreateViewHolder(inflater, parent);
        if (viewHolder == null) {
            throw new NullPointerException("The SimpleFragment '" + this + "' must not return null from onCreateViewHolder()");
        }
        view = viewHolder.getView();
        if (view == null) {
            throw new NullPointerException("The SimpleFragment.ViewHolder '" + viewHolder + "' must not return null from getView()");
        }
        for (LifecycleListener<V> listener : lifecycleListeners) {
            listener.onViewCreated(viewHolder, view);
        }
        container.setRootView(view, inflater);
        dialogContainer.setRootView(inflater);
        onViewHolderCreated(viewHolder, view);
        return view;
    }

    final void destroyView() {
        for (LifecycleListener<V> listener : lifecycleListeners) {
            listener.onViewDestroyed();
        }
        onDestroyViewHolder();
        viewHolder = null;
        view = null;
        container.clearRootView();
        dialogContainer.clearRootView();
    }

    final Parcelable saveState(Context context) {
        state.state = new Bundle();
        onSave(context, state.state);
        state.containerState = container.saveState();
        state.dialogContainerState = dialogContainer.saveState();
        return state;
    }

    final void restoreState(SimpleFragmentManager fm, Parcelable parcelable) {
        this.state = (State) parcelable;
        this.state.state.setClassLoader(getClass().getClassLoader());
        this.fm = fm;
        this.container = new SimpleFragmentContainer(fm, state.key);
        this.container.restoreState(state.containerState);
        this.dialogContainer = new SimpleFragmentDialogContainer(fm, state.key);
        this.dialogContainer.restoreState(state.dialogContainerState);
        this.onCreate(fm.getContext().getApplicationContext(), state.state);
    }

    static SimpleFragment newInstance(Parcelable parcelable) {
        State state = (State) parcelable;
        SimpleFragment fragment = newInstance(state.intent.getSimpleFragmentClassName());
        fragment.state = state;
        return fragment;
    }

    static SimpleFragment newInstance(String simpleFragmentName) {
        try {
            return (SimpleFragment) Class.forName(simpleFragmentName).newInstance();
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @Nullable
    public V getViewHolder() {
        return viewHolder;
    }

    @Nullable
    public View getView() {
        return view;
    }

    public SimpleFragmentKey getKey() {
        return state.key;
    }

    public SimpleFragmentManager getSimpleFragmentManager() {
        return fm;
    }

    public SimpleFragmentContainer getSimpleFragmentContainer() {
        return container;
    }
    
    public SimpleFragmentDialogContainer getSimpleFragmentDialogContainer() {
        return dialogContainer;
    }

    public SimpleFragmentIntent getIntent() {
        return state.intent;
    }
    
    public void addLifecycleListener(LifecycleListener<V> listener) {
        lifecycleListeners.add(listener);
    }
    
    public void removeLifecycleListener(LifecycleListener<V> listener) {
        lifecycleListeners.remove(listener);
    }
    
    public interface ViewHolder {
        View getView();
    }

    State getState() {
        return state;
    }

    boolean handleBack() {
        return onBackPressed();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleFragment<?> fragment = (SimpleFragment<?>) o;
        return getKey().equals(fragment.getKey());
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    @Override
    public String toString() {
        return "SimpleFragment(" + getKey() + ")";
    }
    
    private static class State implements Parcelable {
        private SimpleFragmentIntent intent;
        private Bundle state;
        private SimpleFragmentKey key;
        private Parcelable containerState;
        private Parcelable dialogContainerState;

        State() {
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.intent, 0);
            dest.writeBundle(state);
            dest.writeParcelable(key, flags);
            dest.writeParcelable(containerState, flags);
            dest.writeParcelable(dialogContainerState, flags);
        }

        State(Parcel in) {
            this.intent = in.readParcelable(SimpleFragmentIntent.class.getClassLoader());
            this.state = in.readBundle();
            this.key = in.readParcelable(SimpleFragmentKey.class.getClassLoader());
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

    public interface LifecycleListener<T extends ViewHolder> {
        void onViewCreated(T viewHolder, View view);
        void onViewDestroyed();
    }
}

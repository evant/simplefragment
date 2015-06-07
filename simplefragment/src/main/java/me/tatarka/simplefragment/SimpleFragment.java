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

import me.tatarka.simplefragment.key.SimpleFragmentKey;

/**
 * Created by evan on 1/11/15.
 */
public abstract class SimpleFragment implements SimpleFragmentManagerProvider, SimpleFragmentContainerManagerProvider {
    private View view;
    private State state = new State();
    private SimpleFragmentManager fm;
    private SimpleFragmentContainerManager cm;

    public abstract void onCreate(Context context, @Nullable Bundle state);

    public abstract View onCreateView(LayoutInflater inflater, ViewGroup parent);

    public void onViewCreated(@NonNull View view) {
    }

    public void onViewDestroyed(@NonNull View view) {
    }

    public void onSave(@NonNull Bundle state) {
    }

    public void onDestroy() {
    }

    public boolean onBackPressed() {
        return false;
    }

    final void create(SimpleFragmentManager fm, SimpleFragmentIntent intent, SimpleFragmentKey key) {
        this.fm = fm;
        this.cm = new SimpleFragmentContainerManager(fm, key);
        state.intent = intent;
        state.key = key;
        onCreate(fm.getContext().getApplicationContext(), this.state.state);
    }

    final View createView(LayoutInflater inflater, ViewGroup parent) {
        view = onCreateView(inflater, parent);
        if (view == null) {
            throw new NullPointerException("SimpleFragment.onCreateView() in '" + this + "' must not return null.");
        }
        cm.setView(view);
        onViewCreated(view);
        return view;
    }

    final void destroyView() {
        onViewDestroyed(view);
        view = null;
        cm.clearView();
    }

    final Parcelable saveState() {
        state.state = new Bundle();
        onSave(state.state);
        state.cmState = cm.saveState();
        return state;
    }

    final void restoreState(SimpleFragmentManager fm, Parcelable parcelable) {
        this.state = (State) parcelable;
        this.state.state.setClassLoader(getClass().getClassLoader());
        this.fm = fm;
        this.cm = new SimpleFragmentContainerManager(fm, state.key);
        this.cm.restoreState(state.cmState);
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

    /**
     * Returns the view attached to the fragment. Since the lifecycle of the SimpleFragment is
     * longer than the view, you must not keep a reference to it. It also may be null if you are not
     * between the lifecycle events {@link #onViewCreated(View)} and {@link
     * #onViewDestroyed(View)}.
     */
    @Nullable
    public View getView() {
        return view;
    }

    public SimpleFragmentKey getKey() {
        return state.key;
    }

    @Override
    public SimpleFragmentManager getSimpleFragmentManager() {
        return fm;
    }

    @Override
    public SimpleFragmentContainerManager getSimpleFragmentContainerManager() {
        return cm;
    }

    public SimpleFragmentIntent getIntent() {
        return state.intent;
    }

    /**
     * Returns the layoutInflater for the given SimpleFragment, you may override this to customize
     * the inflater.
     */
    public LayoutInflater getLayoutInflater() {
        return LayoutInflater.from(fm.getContext());
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

        SimpleFragment fragment = (SimpleFragment) o;
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
        private Parcelable cmState;

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
            dest.writeParcelable(cmState, flags);
        }

        State(Parcel in) {
            this.intent = in.readParcelable(SimpleFragmentIntent.class.getClassLoader());
            this.state = in.readBundle();
            this.key = in.readParcelable(SimpleFragmentKey.class.getClassLoader());
            this.cmState = in.readParcelable(SimpleFragmentContainerManager.class.getClassLoader());
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

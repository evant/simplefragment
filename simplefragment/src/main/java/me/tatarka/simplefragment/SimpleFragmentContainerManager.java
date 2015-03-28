package me.tatarka.simplefragment;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import me.tatarka.simplefragment.key.SimpleFragmentKey;

/**
 * Created by evan on 3/28/15.
 */
public class SimpleFragmentContainerManager implements SimpleFragmentManagerProvider {
    private SimpleFragmentManager fm;
    @Nullable
    private SimpleFragmentKey parentKey;
    private LayoutInflater layoutInflater;
    private View rootView;
    private Map<Key<?>, Value> containers;
    
    public SimpleFragmentContainerManager(SimpleFragmentManager fm, @Nullable SimpleFragmentKey parentKey) {
        this.fm = fm;
        this.parentKey = parentKey;
        this.containers = new HashMap<>();
    }
    
    @Override
    public SimpleFragmentManager getSimpleFragmentManager() {
        return fm;
    }
    
    public void setView(LayoutInflater layoutInflater, View rootView) {
        this.rootView = rootView; 
        this.layoutInflater = layoutInflater;
        for (Value container: containers.values()) {
            container.onAttachView(layoutInflater, rootView);
        }
    }
    
    public void clearView() {
        if (layoutInflater != null) {
            layoutInflater = null;
            rootView = null;
            for (Value container: containers.values()) {
                container.onClearView();
            }
        }
    }
    
    public Parcelable saveState() {
        Map<String, Parcelable> containersState = new HashMap<>(containers.size());
        for (Map.Entry<Key<?>, Value> entry : containers.entrySet()) {
            containersState.put(entry.getKey().name, entry.getValue());
        }
        return new State(containersState);
    }
    
    public void restoreState(Parcelable parcelable) {
        State state = (State) parcelable;
        containers = new HashMap<>(state.containerStates.size());
        for (Map.Entry<String, Parcelable> entry : state.containerStates.entrySet()) {
            Key<?> key = new Key<>(entry.getKey());
            Value value = (Value) entry.getValue();
            containers.put(key, value);
            value.onAttachScope(fm, parentKey);
            if (layoutInflater != null) {
                value.onAttachView(layoutInflater, rootView);
            }
        }
    }
    
    public <T extends Value> void put(Key<T> key, T value) {
        containers.put(key, value);
        value.onAttachScope(fm, parentKey);
        if (layoutInflater != null) {
            value.onAttachView(layoutInflater, rootView);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T extends Value> T get(Key<T> key) {
        return (T) containers.get(key);
    }

    @SuppressWarnings("unchecked")
    public <T extends Value> T remove(Key<T> key) {
        return (T) containers.remove(key);
    }
    
    private static class State implements Parcelable {
        private Map<String, Parcelable> containerStates;

        State(Map<String, Parcelable> containerStates) {
            this.containerStates = containerStates;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeMap(containerStates);
        }

        public State() {
        }

        private State(Parcel in) {
            this.containerStates = in.readHashMap(Value.class.getClassLoader());
        }

        public static final Creator<State> CREATOR = new Creator<State>() {
            public State createFromParcel(Parcel source) {
                return new State(source);
            }

            public State[] newArray(int size) {
                return new State[size];
            }
        };
    }
    
    public static final class Key<T extends Value> {
        private String name;

        public Key(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key<?> key = (Key<?>) o;

            return name.equals(key.name);

        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public interface Value extends Parcelable {
        void onAttachScope(SimpleFragmentManager fm, @Nullable SimpleFragmentKey parentKey);

        void onAttachView(LayoutInflater layoutInflater, View rootView);

        void onClearView();
    }
}

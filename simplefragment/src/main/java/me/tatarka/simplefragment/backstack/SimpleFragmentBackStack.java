package me.tatarka.simplefragment.backstack;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.key.LayoutKey;
import me.tatarka.simplefragment.key.SimpleFragmentKey;

/**
 * Manages a back-stack for all the {@code SimpleFragmentContainers} for a {@code
 * SimpleFragmentManager}. Each container should register itself with {@link
 * #addListener(SimpleFragmentKey, BackStackListener)} in order to respond to pushing and poping a
 * fragment of the back-stack. You should not use this directly, instead delegate through the {@link
 * me.tatarka.simplefragment.SimpleFragmentContainer}.
 */
public class SimpleFragmentBackStack {
    private SimpleFragmentManager fm;
    private List<LayoutKey> backStack;
    private Map<SimpleFragmentKey, BackStackListener> listeners = new HashMap<>();

    public SimpleFragmentBackStack(SimpleFragmentManager fm) {
        this.fm = fm;
        this.backStack = new ArrayList<>();
    }

    private void restoreFm(SimpleFragmentManager fm) {
        this.fm = fm;
    }

    /**
     * Adds a listener for pushing and poping fragments. Listeners are scoped to a given parent key
     * in order to cleanly implement nesting.
     *
     * @param parentKey The listener will only be called if the parent key of the pushed or poped
     *                  fragment matches this.
     * @param listener  The listener that will be called on pushing an poping fragments.
     */
    public void addListener(@Nullable SimpleFragmentKey parentKey, BackStackListener listener) {
        listeners.put(parentKey, listener);
    }

    public <T extends SimpleFragment> T push(SimpleFragmentIntent<T> intent, LayoutKey key) {
        SimpleFragment previousFragment = findPreviousFragmentUnknownIndex(key);
        int index;
        if (previousFragment != null) {
            index = ((LayoutKey) previousFragment.getKey()).getIndex() + 1;
        } else {
            index = 0;
        }
        LayoutKey newKey = key.withIndex(index);
        backStack.add(newKey);
        T fragment = fm.create(intent, newKey);
        BackStackListener listener = listeners.get(newKey.getParent());
        if (listener == null) {
            throw new IllegalArgumentException("No listener found for the given key's parent: " + newKey.getParent());
        }
        listener.onReplace(previousFragment, fragment);
        return fragment;
    }

    public boolean pop(@Nullable SimpleFragmentKey parentKey) {
        if (backStack.isEmpty()) {
            return false;
        }
        LayoutKey popKey = null;
        for (int i = backStack.size() - 1; i >= 0; i--) {
            LayoutKey key = backStack.get(i);
            if (equals(key.getParent(), parentKey)) {
                popKey = key;
                break;
            }
        }
        if (popKey == null) {
            return false;
        }
        remove(popKey);
        return true;
    }

    public boolean pop() {
        if (backStack.isEmpty()) {
            return false;
        }
        LayoutKey key = backStack.get(backStack.size() - 1);
        remove(key);
        return true;
    }

    public boolean remove(LayoutKey key) {
        if (backStack.remove(key)) {
            SimpleFragment fragment = fm.find(key);
            SimpleFragment previousFragment = findPreviousFragmentKnownIndex(key);
            if (previousFragment != null) {
                LayoutKey previousFragmentKey = (LayoutKey) previousFragment.getKey();
                BackStackListener listener = listeners.get(previousFragmentKey.getParent());
                if (listener == null) {
                    throw new IllegalArgumentException("No listener found for the given key's parent: " + previousFragmentKey.getParent());
                }
                listener.onReplace(fragment, previousFragment);
            }
            fm.destroy(fragment);
            return true;
        } else {
            return false;
        }
    }

    private SimpleFragment findPreviousFragmentUnknownIndex(LayoutKey key) {
        SimpleFragment previousFragment = null;
        int previousIndex = -1;
        for (SimpleFragment fragment : fm.getFragments()) {
            if (fragment.getKey() instanceof LayoutKey) {
                LayoutKey testKey = (LayoutKey) fragment.getKey();
                if (equals(testKey.getParent(), key.getParent()) && testKey.getViewId() == key.getViewId()) {
                    if (previousIndex < testKey.getIndex()) {
                        previousIndex = testKey.getIndex();
                        previousFragment = fm.find(testKey);
                    }
                }
            }
        }
        return previousFragment;
    }

    private SimpleFragment findPreviousFragmentKnownIndex(LayoutKey key) {
        SimpleFragment previousFragment = null;
        int index = key.getIndex() - 1;
        while (index >= 0) {
            LayoutKey previousKey = key.withIndex(index);
            previousFragment = fm.find(previousKey);
            if (previousFragment != null) {
                break;
            }
            index--;
        }
        return previousFragment;
    }

    private static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }


    public Parcelable saveState() {
        return new State(backStack);
    }

    public void restoreState(Parcelable parcelable) {
        State state = (State) parcelable;
        this.backStack = state.backStack;
    }

    static class State implements Parcelable {
        List<LayoutKey> backStack;

        State(List<LayoutKey> backStack) {
            this.backStack = backStack;
        }

        protected State(Parcel in) {
            backStack = in.createTypedArrayList(LayoutKey.CREATOR);
        }

        public static final Creator<State> CREATOR = new Creator<State>() {
            @Override
            public State createFromParcel(Parcel in) {
                return new State(in);
            }

            @Override
            public State[] newArray(int size) {
                return new State[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(backStack);
        }
    }

    private SimpleFragmentBackStack(Parcel in) {
        this.backStack = new ArrayList<>();
        in.readTypedList(this.backStack, LayoutKey.CREATOR);
    }

    public static final Parcelable.Creator<SimpleFragmentBackStack> CREATOR = new Parcelable.Creator<SimpleFragmentBackStack>() {
        public SimpleFragmentBackStack createFromParcel(Parcel source) {
            return new SimpleFragmentBackStack(source);
        }

        public SimpleFragmentBackStack[] newArray(int size) {
            return new SimpleFragmentBackStack[size];
        }
    };

    public interface BackStackListener {
        void onReplace(SimpleFragment oldFragment, SimpleFragment newFragment);
    }
}

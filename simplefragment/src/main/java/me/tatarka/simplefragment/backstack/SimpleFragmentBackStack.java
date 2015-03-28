package me.tatarka.simplefragment.backstack;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.ViewGroup;

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
public class SimpleFragmentBackStack implements SimpleFragmentManager.ExtraValue {
    public static final SimpleFragmentManager.ExtraKey<SimpleFragmentBackStack> KEY = new SimpleFragmentManager.ExtraKey<>(SimpleFragmentBackStack.class.getName());

    /**
     * Obtains an instance of {@code SimpleFragmentBackStack} for the given manager.
     */
    public static SimpleFragmentBackStack getInstance(SimpleFragmentManager fm) {
        SimpleFragmentBackStack backStack = fm.getExtra(KEY);
        if (backStack == null) {
            backStack = new SimpleFragmentBackStack(fm);
            fm.putExtra(KEY, backStack);
        } else {
            backStack.restoreFm(fm);
        }
        return backStack;
    }

    private SimpleFragmentManager fm;
    private List<LayoutKey> backStack;
    private Map<SimpleFragmentKey, BackStackListener> listeners = new HashMap<>();

    private SimpleFragmentBackStack(SimpleFragmentManager fm) {
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

    public <T extends SimpleFragment> T push(SimpleFragmentIntent<T> intent, @Nullable SimpleFragmentKey parentKey, @IdRes int viewId) {
        SimpleFragment previousFragment = findPreviousFragment(parentKey, viewId);
        int index;
        if (previousFragment != null) {
            index = ((LayoutKey) previousFragment.getKey()).getIndex() + 1;
        } else {
            index = 0;
        }
        LayoutKey key = new LayoutKey(parentKey, viewId, index);
        backStack.add(key);
        T fragment = fm.create(intent, key);
        BackStackListener listener = listeners.get(key.getParent());
        if (listener == null) {
            throw new IllegalArgumentException("No listener found for the given key's parent: " + key.getParent());
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
            SimpleFragment previousFragment = findPreviousFragment(key);
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

    private SimpleFragment findPreviousFragment(SimpleFragmentKey parentKey, int viewId) {
        SimpleFragment previousFragment = null;
        int previousIndex = -1;
        for (SimpleFragment fragment : fm.getFragments()) {
            if (fragment.getKey() instanceof LayoutKey) {
                LayoutKey key = (LayoutKey) fragment.getKey();
                if (equals(key.getParent(), parentKey) && key.getViewId() == viewId) {
                    if (previousIndex < key.getIndex()) {
                        previousIndex = key.getIndex();
                        previousFragment = fm.find(key);
                    }
                }
            }
        }
        return previousFragment;
    }

    private SimpleFragment findPreviousFragment(LayoutKey key) {
        SimpleFragment previousFragment = null;
        int index = key.getIndex() - 1;
        while (index >= 0) {
            LayoutKey previousKey = new LayoutKey(key.getParent(), key.getViewId(), index);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.backStack);
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

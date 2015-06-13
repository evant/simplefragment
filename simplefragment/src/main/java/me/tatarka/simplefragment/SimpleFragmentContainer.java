package me.tatarka.simplefragment;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import me.tatarka.simplefragment.backstack.SimpleFragmentBackStack;
import me.tatarka.simplefragment.key.LayoutKey;
import me.tatarka.simplefragment.key.SimpleFragmentContainerKey;
import me.tatarka.simplefragment.key.SimpleFragmentKey;

/**
 * A container where you can directly add and remove fragments to the view hierarchy. It also
 * supports back stack-like features.
 */
public class SimpleFragmentContainer {
    private SimpleFragmentManager fm;
    private SimpleFragmentKey parentKey;
    private View rootView;
    private final List<SimpleFragmentContainerKey> attachedKeys = new ArrayList<>();
    private final Map<SimpleFragmentContainerKey, SimpleFragment> fragmentsPendingAttach = new ArrayMap<>();
    private SimpleFragmentBackStack backStack;

    public SimpleFragmentContainer(SimpleFragmentManager fm, @Nullable SimpleFragmentKey parentKey) {
        this.fm = fm;
        this.parentKey = parentKey;
        this.backStack = fm.getBackStack();
        this.backStack.addListener(parentKey, new BackStackListener());
    }

    public SimpleFragmentManager getFragmentManager() {
        return fm;
    }

    public void setView(View rootView) {
        this.rootView = rootView;

        // Restore previously attached fragments.
        for (SimpleFragmentKey key : attachedKeys) {
            SimpleFragment childFragment = fm.find(key);
            attachFragment(rootView, childFragment);
        }

        // View is ready, we can createView all pending fragments now.
        for (Map.Entry<SimpleFragmentContainerKey, SimpleFragment> entry : fragmentsPendingAttach.entrySet()) {
            attachFragment(rootView, entry.getValue());
            attachedKeys.add(entry.getKey());
        }
        fragmentsPendingAttach.clear();
    }

    public void clearView() {
        for (SimpleFragmentContainerKey key : attachedKeys) {
            SimpleFragment fragment = fm.find(key);
            key.detach(this, rootView, fragment);

        }
        rootView = null;
    }

    /**
     * Creates a {@code SimpleFragment} and attaches it to the view with the given id.
     *
     * @param intent The intent to create the fragment with.
     * @param viewId The view id to createView to.
     * @param <T>    The fragment class.
     * @return The created fragment.
     */
    public <T extends SimpleFragment> T add(SimpleFragmentIntent<T> intent, SimpleFragmentContainerKey key) {
        T fragment = fm.create(intent, key.withParent(parentKey));
        maybeAttachFragment(rootView, fragment);
        return fragment;
    }

    /**
     * Finds and existing {@code SimpleFragment} or creates a new one if it doesn't exist and
     * attaches it to the view with the given id.
     *
     * @param intent The intent to create the fragment with.
     * @param viewId The view id to createView to.
     * @param <T>    The fragment class.
     * @return The created fragment.
     */
    @SuppressWarnings("unchecked")
    public <T extends SimpleFragment> T findOrAdd(SimpleFragmentIntent<T> intent, SimpleFragmentContainerKey key) {
        SimpleFragmentContainerKey nestedKey = key.withParent(parentKey);
        for (SimpleFragmentContainerKey testKey : attachedKeys) {
            if (testKey.equals(nestedKey)) {
                return (T) fm.find(nestedKey);
            }
        }
        return add(intent, key);
    }

    /**
     * Detaches the given {@code SimpleFragment} and destroys it.
     *
     * @param fragment The fragment to remove.
     */
    public void remove(SimpleFragment fragment) {
        SimpleFragmentContainerKey key = (SimpleFragmentContainerKey) fragment.getKey();
        attachedKeys.remove(key);

        if (key instanceof LayoutKey && backStack.remove((LayoutKey) key)) {
            return;
        }

        if (fragment.getView() != null) {
            key.detach(this, rootView, fragment);
        }
        fm.destroy(fragment);
    }

    /**
     * Finds a {@code SimpleFragment} attached to the given view id.
     *
     * @param viewId The view id to search with.
     * @return The fragment or null if it cannot be found.
     */
    public SimpleFragment find(SimpleFragmentContainerKey key) {
        for (int i = 0; i < attachedKeys.size(); i++) {
            SimpleFragmentContainerKey testKey = attachedKeys.get(i);
            if (testKey.matches(key)) {
                return fm.find(testKey);
            }
        }
        return null;
    }

    /**
     * Returns a list of all fragments that are in this container, including non-attached ones. (A
     * fragment may not be attached if it's in the back stack). If you only want attached fragments,
     * use {@link #getAttachedFragments()} instead.
     *
     * @return The list of fragments.
     */
    public List<SimpleFragment> getFragments() {
        List<SimpleFragment> fragments = new ArrayList<>();
        for (SimpleFragment fragment : fm.getFragments()) {
            SimpleFragmentKey key = fragment.getKey();
            if (key instanceof SimpleFragmentContainerKey && equals(((SimpleFragmentContainerKey) key).getParent(), parentKey)) {
                fragments.add(fragment);
            }
        }
        return Collections.unmodifiableList(fragments);
    }

    /**
     * Returns a list of all fragments that are currently attached to this containers view
     * hierarchy.
     *
     * @return The list of fragments.
     */
    public List<SimpleFragment> getAttachedFragments() {
        List<SimpleFragment> fragments = new ArrayList<>(attachedKeys.size());
        for (SimpleFragmentContainerKey key : attachedKeys) {
            fragments.add(fm.find(key));
        }
        return Collections.unmodifiableList(fragments);
    }

    /**
     * Pushes the given fragment onto the back stack, replacing the previous fragment at the given
     * view id. You can later restore the state with {@link #pop()}.
     *
     * @param intent The fragment to push on the stack.
     * @param viewId The view id to createView the fragment to.
     * @param <T>    The fragment type.
     * @return The new fragment.
     */
    public <T extends SimpleFragment> T push(SimpleFragmentIntent<T> intent, LayoutKey key) {
        return backStack.push(intent, key.withParent(parentKey));
    }

    /**
     * Pops the last fragment added to the back stack, reattaching the previous one if it exists.
     * This is only scoped to this container.
     *
     * @return True if the was a fragment on the back stack to pop, false otherwise.
     */
    public boolean pop() {
        return backStack.pop(parentKey);
    }

    @Nullable
    public SimpleFragmentKey getParentKey() {
        return parentKey;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("SimpleFragmentContainer");
        if (parentKey != null) {
            builder.append('(').append(parentKey).append(')');
        }
        return builder.toString();
    }

    /**
     * Attaches the fragment to the rootView given it's path and adds it to the attached keys if
     * successful. If the root view hasn't been set yet, this attachment will be delayed until the
     * root view is set.
     */
    private void maybeAttachFragment(View rootView, SimpleFragment fragment) {
        SimpleFragmentContainerKey key = (SimpleFragmentContainerKey) fragment.getKey();
        if (rootView != null) {
            attachFragment(rootView, fragment);
            attachedKeys.add(key);
        } else {
            fragmentsPendingAttach.put(key, fragment);
        }
    }

    /**
     * Attaches the fragment to the rootView given it's path.
     */
    private void attachFragment(View rootView, SimpleFragment fragment) {
        ((SimpleFragmentContainerKey) fragment.getKey()).attach(this, rootView, fragment);
    }

    public Context getContext() {
        return fm.getContext();
    }

    private class BackStackListener implements SimpleFragmentBackStack.BackStackListener {
        @Override
        public void onReplace(SimpleFragment oldFragment, SimpleFragment newFragment) {
            SimpleFragmentContainerKey oldKey = (SimpleFragmentContainerKey) oldFragment.getKey();
            attachedKeys.remove(oldKey);
            if (rootView != null) {
                oldKey.detach(SimpleFragmentContainer.this, rootView, oldFragment);
            }
            maybeAttachFragment(rootView, newFragment);
        }
    }

    private static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    public Parcelable saveState() {
        return new State(attachedKeys);
    }

    public void restoreState(Parcelable parcelable) {
        State state = (State) parcelable;
        attachedKeys.addAll(state.attachedKeys);
    }

    static class State implements Parcelable {
        List<SimpleFragmentContainerKey> attachedKeys;

        State(List<SimpleFragmentContainerKey> attachedKeys) {
            this.attachedKeys = attachedKeys;
        }

        State(Parcel in) {
            attachedKeys = new ArrayList<>();
            in.readList(attachedKeys, getClass().getClassLoader());
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
            dest.writeList(attachedKeys);
        }
    }
}

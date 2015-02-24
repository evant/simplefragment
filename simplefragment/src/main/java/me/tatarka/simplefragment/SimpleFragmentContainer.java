package me.tatarka.simplefragment;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.tatarka.simplefragment.backstack.SimpleFragmentBackStack;
import me.tatarka.simplefragment.key.LayoutKey;
import me.tatarka.simplefragment.key.SimpleFragmentKey;
import me.tatarka.simplefragment.util.ResUtil;

/**
 * A container where you can directly add and remove fragments to the view hierarchy. It also
 * supports back stack-like features.
 */
public class SimpleFragmentContainer {
    private SimpleFragmentManager fm;
    private SimpleFragmentKey parentKey;
    private View rootView;
    private LayoutInflater layoutInflater;
    private List<LayoutKey> attachedKeys;
    private Map<LayoutKey, SimpleFragment> fragmentsPendingAttach;
    private SimpleFragmentBackStack backStack;

    public SimpleFragmentContainer(SimpleFragmentManager fm, @Nullable SimpleFragmentKey parentKey) {
        this.fm = fm;
        this.parentKey = parentKey;
        this.attachedKeys = new ArrayList<>();
        this.fragmentsPendingAttach = new HashMap<>();
        this.backStack = SimpleFragmentBackStack.getInstance(fm);
        this.backStack.addListener(parentKey, new BackStackListener());
    }

    /**
     * Creates a {@code SimpleFragment} and attaches it to the view with the given id.
     *
     * @param intent The intent to create the fragment with.
     * @param viewId The view id to createView to.
     * @param <T>    The fragment class.
     * @return The created fragment.
     */
    public <T extends SimpleFragment> T add(SimpleFragmentIntent<T> intent, @IdRes int viewId) {
        SimpleFragmentKey key = new LayoutKey(parentKey, viewId);
        T fragment = fm.create(intent, key);
        maybeAttachFragment(rootView, layoutInflater, fragment);
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
    public <T extends SimpleFragment> T findOrAdd(SimpleFragmentIntent<T> intent, @IdRes int viewId) {
        for (LayoutKey key : attachedKeys) {
            if (key.getViewId() == viewId) {
                return (T) fm.find(key);

            }
        }
        return add(intent, viewId);
    }

    /**
     * Detaches the given {@code SimpleFragment} and destroys it.
     *
     * @param fragment The fragment to remove.
     */
    public void remove(SimpleFragment<?> fragment) {
        LayoutKey key = (LayoutKey) fragment.getKey();
        attachedKeys.remove(key);
        if (!backStack.remove(key)) {
            if (fragment.getView() != null) {
                ViewGroup parentView = (ViewGroup) fragment.getView().getParent();
                fm.destroyView(fragment, parentView);
            }
            fm.destroy(fragment);
        }
    }

    /**
     * Finds a {@code SimpleFragment} attached to the given view id.
     *
     * @param viewId The view id to search with.
     * @return The fragment or null if it cannot be found.
     */
    public SimpleFragment<?> find(@IdRes int viewId) {
        for (LayoutKey key : attachedKeys) {
            if (key.getViewId() == viewId) {
                return fm.find(key);
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
    public List<SimpleFragment<?>> getFragments() {
        List<SimpleFragment<?>> fragments = new ArrayList<>();
        for (SimpleFragment<?> fragment : fm.getFragments()) {
            SimpleFragmentKey key = fragment.getKey();
            if (key instanceof LayoutKey && equals(((LayoutKey) key).getParent(), parentKey)) {
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
    public List<SimpleFragment<?>> getAttachedFragments() {
        List<SimpleFragment<?>> fragments = new ArrayList<>(attachedKeys.size());
        for (LayoutKey key : attachedKeys) {
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
    public <T extends SimpleFragment> T push(SimpleFragmentIntent<T> intent, @IdRes int viewId) {
        return backStack.push(intent, parentKey, viewId);
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

    /**
     * Pops the last fragment globally added to to back stack for this {@code
     * SimpleFragmentManager}. This is equivalent to what should happen when the user presses the
     * back button. Warning! this may remove a fragment in a completely different container, if you
     * just want to manage your own child fragments, use {@link #pop()} instead.
     *
     * @return True if there was a fragment on the back stack to pop, false otherwise.
     */
    public boolean popGlobal() {
        return backStack.pop();
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
     * Set the root view for fragments to be attached to by finding a child view's id. Any fragments
     * added before calling this will be immediately attached.
     *
     * @param rootView       The view to createView to.
     * @param layoutInflater The layout inflater used when crating the fragment's views.
     */
    public void setRootView(View rootView, LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
        this.rootView = rootView;

        // Restore previously attached fragments.
        for (LayoutKey key : attachedKeys) {
            SimpleFragment childFragment = fm.find(key);
            attachFragment(rootView, layoutInflater, childFragment);
        }

        // View is ready, we can createView all pending fragments now.
        for (Map.Entry<LayoutKey, SimpleFragment> entry : fragmentsPendingAttach.entrySet()) {
            attachFragment(rootView, layoutInflater, entry.getValue());
            attachedKeys.add(entry.getKey());
        }
        fragmentsPendingAttach.clear();
    }

    /**
     * Clears the root view. This will also destroy the views of any fragments attached to this
     * container. Should be called if the container is being retained but the views are being
     * destroyed.
     */
    public void clearRootView() {
        for (LayoutKey key : attachedKeys) {
            ViewGroup parentView = (ViewGroup) rootView.findViewById(key.getViewId());
            SimpleFragment fragment = fm.find(key);
            fm.destroyView(fragment, parentView);
        }
        layoutInflater = null;
        rootView = null;
    }

    /**
     * Attaches the fragment to the rootView given it's path and adds it to the attached keys if
     * successful. If the root view hasn't been set yet, this attachment will be delayed until the
     * root view is set.
     */
    private void maybeAttachFragment(View rootView, LayoutInflater layoutInflater, SimpleFragment fragment) {
        LayoutKey key = (LayoutKey) fragment.getKey();
        if (rootView != null) {
            attachFragment(rootView, layoutInflater, fragment);
            attachedKeys.add(key);
        } else {
            fragmentsPendingAttach.put(key, fragment);
        }
    }

    /**
     * Attaches the fragment to the rootView given it's path.
     */
    private void attachFragment(View rootView, LayoutInflater layoutInflater, SimpleFragment fragment) {
        LayoutKey key = (LayoutKey) fragment.getKey();
        int viewId = key.getViewId();
        View parentView = rootView.findViewById(viewId);
        if (parentView == null) {
            throw new IllegalArgumentException("Cannot find view with id '" + ResUtil.safeGetIdName(fm.getContext().getResources(), viewId) + "'.");
        }
        if (!(parentView instanceof ViewGroup)) {
            throw new IllegalArgumentException("View with id '" + ResUtil.safeGetIdName(fm.getContext().getResources(), viewId) + "' is not an instance of ViewGroup.");
        }
        ViewGroup parent = (ViewGroup) parentView;
        View view = fm.createView(fragment, layoutInflater, parent);
        parent.addView(view);
    }

    public Parcelable saveState() {
        return new State(attachedKeys);
    }

    public void restoreState(Parcelable parcelable) {
        State state = (State) parcelable;
        attachedKeys = state.attachedKeys;
    }

    public Context getContext() {
        return fm.getContext();
    }

    private class BackStackListener implements SimpleFragmentBackStack.BackStackListener {
        @Override
        public void onReplace(SimpleFragment oldFragment, SimpleFragment newFragment) {
            LayoutKey oldKey = (LayoutKey) oldFragment.getKey();
            attachedKeys.remove(oldKey);
            maybeAttachFragment(rootView, layoutInflater, newFragment);
        }
    }

    private static class State implements Parcelable {
        private List<LayoutKey> attachedKeys;

        State(List<LayoutKey> attachedKeys) {
            this.attachedKeys = attachedKeys;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeTypedList(attachedKeys);
        }

        private State(Parcel in) {
            this.attachedKeys = new ArrayList<>();
            in.readTypedList(this.attachedKeys, LayoutKey.CREATOR);
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

    private static boolean equals(Object a, Object b) {
        return (a == null) ? (b == null) : a.equals(b);
    }
}

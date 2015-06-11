package me.tatarka.simplefragment;

import android.content.Context;
import android.os.Parcel;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.tatarka.simplefragment.backstack.SimpleFragmentBackStack;
import me.tatarka.simplefragment.key.DialogKey;
import me.tatarka.simplefragment.key.LayoutKey;
import me.tatarka.simplefragment.key.SimpleFragmentContainerKey;
import me.tatarka.simplefragment.key.SimpleFragmentKey;

/**
 * A container where you can directly add and remove fragments to the view hierarchy. It also
 * supports back stack-like features.
 */
public class SimpleFragmentContainer implements SimpleFragmentContainerManager.Value {
    public static final SimpleFragmentContainerManager.Key<SimpleFragmentContainer> KEY = new SimpleFragmentContainerManager.Key<>("me.tatarka.simplefragment.SimpleFragmentContainer");

    private SimpleFragmentManager fm;
    private SimpleFragmentKey parentKey;
    private View rootView;
    private final List<SimpleFragmentContainerKey> attachedKeys = new ArrayList<>();
    private final Map<SimpleFragmentContainerKey, SimpleFragment> fragmentsPendingAttach = new HashMap<>();
    private SimpleFragmentBackStack backStack;

    public static SimpleFragmentContainer getInstance(SimpleFragmentContainerManagerProvider provider) {
        return getInstance(provider.getSimpleFragmentContainerManager());
    }

    public static SimpleFragmentContainer getInstance(SimpleFragmentContainerManager cm) {
        SimpleFragmentContainer container = cm.get(KEY);
        if (container == null) {
            container = new SimpleFragmentContainer();
            cm.put(KEY, container);
        }
        container.setManager(cm);
        return container;
    }

    private SimpleFragmentContainer() {
    }

    private void setManager(SimpleFragmentContainerManager cm) {
        this.fm = cm.getSimpleFragmentManager();
        this.parentKey = cm.getParentKey();
        this.backStack = SimpleFragmentBackStack.getInstance(fm);
        this.backStack.addListener(parentKey, new BackStackListener());
    }

    public SimpleFragmentManager getFragmentManager() {
        return fm;
    }

    @Override
    public void onAttachView(View rootView) {
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

    @Override
    public void onClearView() {
        for (SimpleFragmentContainerKey key : attachedKeys) {
            SimpleFragment fragment = fm.find(key);
            key.detach(this, rootView, fragment);

        }
        rootView = null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(attachedKeys);
    }

    private SimpleFragmentContainer(Parcel in) {
        in.readList(this.attachedKeys, getClass().getClassLoader());
    }

    public static final Creator<SimpleFragmentContainer> CREATOR = new Creator<SimpleFragmentContainer>() {
        public SimpleFragmentContainer createFromParcel(Parcel source) {
            return new SimpleFragmentContainer(source);
        }

        public SimpleFragmentContainer[] newArray(int size) {
            return new SimpleFragmentContainer[size];
        }
    };

    /**
     * Creates a {@code SimpleFragment} and attaches it to the view with the given id.
     *
     * @param intent The intent to create the fragment with.
     * @param viewId The view id to createView to.
     * @param <T>    The fragment class.
     * @return The created fragment.
     */
    public <T extends SimpleFragment> T add(SimpleFragmentIntent<T> intent, @IdRes int viewId) {
        return add(intent, new LayoutKey(parentKey, viewId));
    }

    public <T extends SimpleFragment> T addDialog(SimpleFragmentIntent<T> intent, String tag) {
        return add(intent, new DialogKey(parentKey, tag));
    }

    public <T extends SimpleFragment> T add(SimpleFragmentIntent<T> intent, SimpleFragmentContainerKey key) {
        T fragment = fm.create(intent, key);
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
    public <T extends SimpleFragment> T findOrAdd(SimpleFragmentIntent<T> intent, @IdRes int viewId) {
        return findOrAdd(intent, new LayoutKey(parentKey, viewId));
    }

    public <T extends SimpleFragment> T findOrAddDialog(SimpleFragmentIntent<T> intent, String tag) {
        return findOrAdd(intent, new DialogKey(parentKey, tag));
    }

    @SuppressWarnings("unchecked")
    public <T extends SimpleFragment> T findOrAdd(SimpleFragmentIntent<T> intent, SimpleFragmentContainerKey key) {
        for (SimpleFragmentContainerKey testKey : attachedKeys) {
            if (testKey.equals(key)) {
                return (T) fm.find(key);
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
    public SimpleFragment find(@IdRes int viewId) {
        // We can't just search for a LayoutKey because we don't know the index in the backstack.
        for (SimpleFragmentContainerKey testKey : attachedKeys) {
            if (testKey instanceof LayoutKey) {
                LayoutKey key = (LayoutKey) testKey;
                if (equals(parentKey, key.getParent()) && key.getViewId() == viewId) {
                    return fm.find(key);
                }
            }
        }
        return null;
    }

    public SimpleFragment findDialog(String tag) {
        return find(new DialogKey(parentKey, tag));
    }

    public SimpleFragment find(SimpleFragmentContainerKey key) {
        for (SimpleFragmentContainerKey testKey : attachedKeys) {
            if (testKey.equals(key)) {
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
}

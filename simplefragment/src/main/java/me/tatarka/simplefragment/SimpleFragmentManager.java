package me.tatarka.simplefragment;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.LayoutInflaterCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.tatarka.simplefragment.backstack.SimpleFragmentBackStack;
import me.tatarka.simplefragment.key.LayoutKey;
import me.tatarka.simplefragment.key.SimpleFragmentKey;

/**
 * This class manages the state of {@code SimpleFragments}. It creates and destroys them, attaches
 * and detaches them from the view hierarchy, and saves and restores their state. Other classes
 * build upon this to manage the fragments in a certain way, like nesting them in an Activity or
 * paging between them in a ViewPager.
 */
public class SimpleFragmentManager {
    private Activity activity;
    private List<SimpleFragment> fragments;
    private SimpleFragmentBackStack backStack;

    public SimpleFragmentManager(Activity activity) {
        this.activity = activity;
        this.fragments = new ArrayList<>();
        this.backStack = new SimpleFragmentBackStack(this);
    }

    public Activity getActivity() {
        return activity;
    }

    public List<SimpleFragment> getFragments() {
        return Collections.unmodifiableList(fragments);
    }

    public SimpleFragmentBackStack getBackStack() {
        return backStack;
    }

    @Nullable
    public SimpleFragment find(SimpleFragmentKey key) {
        if (key == null) {
            return null;
        }
        for (SimpleFragment fragment : fragments) {
            if (fragment.getKey().equals(key)) {
                return fragment;
            }
        }
        return null;
    }

    /**
     * Constructs a new {@code SimpleFragment} and adds it to this manager. This will trigger {@link
     * me.tatarka.simplefragment.SimpleFragment#onCreate(android.content.Context,
     * android.os.Bundle)} immediately.
     *
     * @param intent The {@code Intent} to construct the {@code SimpleFragment} with. This {@code
     *               Intent} must have the {@code SimpleFragment} class name and may optionally
     *               contain extras that you want to pass to the fragment.
     * @return The new {@code SimpleFragment}.
     */
    @SuppressWarnings("unchecked")
    public <T extends SimpleFragment> T create(SimpleFragmentIntent<T> intent, SimpleFragmentKey key) {
        if (find(key) != null) {
            // Special case for better error reporting of layout id's
            String keyName;
            if (key instanceof LayoutKey) {
                keyName = ((LayoutKey) key).toString(activity.getResources());
            } else {
                keyName = key.toString();
            }
            throw new IllegalArgumentException("A SimpleFragment has already been added with the given key '" + keyName + "'.");
        }
        T fragment = (T) SimpleFragment.newInstance(intent.getSimpleFragmentClassName());
        fragments.add(fragment);
        fragment.create(this, intent, key);
        return fragment;
    }

    /**
     * Returns an existing {@code SimpleFragment} that matches the given path or constructs a new
     * one with the given {@code SimpleFragmentIntent} if it doesn't exists.
     *
     * @param intent The intent to create the fragment with if it's not found.
     * @param key    The path to find or create the fragment with.
     * @param <T>    The fragment Type
     * @return The found or created fragment
     * @throws IllegalArgumentException If the given intent fragment class and the found fragment
     *                                  class do not match.
     */
    @SuppressWarnings("unchecked")
    public <T extends SimpleFragment> T findOrCreate(SimpleFragmentIntent<T> intent, SimpleFragmentKey key) {
        SimpleFragment currentFragment = find(key);
        if (currentFragment != null) {
            if (!currentFragment.getClass().getName().equals(intent.getSimpleFragmentClassName())) {
                throw new IllegalArgumentException("SimpleFragmentIntent class does not match existing SimpleFragment class. Expected '" + currentFragment.getClass() + "' but found '" + intent.getSimpleFragmentClassName() + "'.");
            }
            return (T) currentFragment;
        } else {
            return create(intent, key);
        }
    }

    /**
     * Destroys the given {@code SimpleFragment} and removes it from this manager. If the fragment
     * is attached to the view hierarchy it's view will be destroyed as well.
     *
     * @param fragment The {@code SimpleFragment} to destroy.
     * @throws java.lang.IllegalArgumentException If the given fragment is null or was not added to
     *                                            this manager.
     */
    public void destroy(SimpleFragment fragment) {
        if (fragment == null) {
            throw new IllegalArgumentException("SimpleFragment cannot be null.");
        }
        if (fragment.getView() != null) {
            destroyView(fragment);
        }
        if (!fragments.remove(fragment)) {
            throw new IllegalArgumentException("Attempting to remove fragment that was not added: '" + fragment + "'");
        }
    }

    /**
     * Creates the view for the given fragment. This will trigger {@link
     * me.tatarka.simplefragment.SimpleFragment#onCreateView(LayoutInflater, ViewGroup)}
     * immediately. This will <em>not</em> occur automatically on configuration changes, you are
     * responsible for calling it again in those cases.
     *
     * @param fragment The {@code SimpleFragment} to createView.
     * @throws java.lang.IllegalArgumentException If the given fragment is null or was not added to
     *                                            this manager.
     */
    public View createView(SimpleFragment fragment, LayoutInflater layoutInflater, @Nullable ViewGroup parentView) {
        if (fragment == null) {
            throw new IllegalArgumentException("SimpleFragment cannot be null.");
        }
        if (!fragments.contains(fragment)) {
            throw new IllegalArgumentException("Attempting to createView fragment that was not added: '" + fragment + "'");
        }

        if (fragment.getView() != null) {
            throw new IllegalArgumentException("Attempting to createView fragment that has already been attached.");
        }

        // To support <fragment> tags in nested layouts, we need a custom inflater.
        LayoutInflater fragmentInflater = layoutInflater.cloneInContext(activity);
        LayoutInflaterCompat.setFactory(fragmentInflater, new SimpleFragmentViewInflater(fragment.getSimpleFragmentContainer()));

        return fragment.createView(fragmentInflater, parentView);
    }

    /**
     * Destroys the {@code SimpleFragment}'s view. If it has already been destroyed is a no-op.
     *
     * @param fragment The The {@code SimpleFragment} to destroyView.
     * @return The view being destroyed so you can remove it from the layout hierarchy if required,
     * or null if the fragment's view has already been destroyed.
     */
    public View destroyView(SimpleFragment fragment) {
        if (fragment == null) {
            throw new IllegalArgumentException("SimpleFragment cannot be null.");
        }
        View view = fragment.getView();
        if (view != null) {
            fragment.destroyView();
        }
        return view;
    }

    public Parcelable saveState() {
        Parcelable[] fragmentStates = new Parcelable[fragments.size()];
        for (int i = 0; i < fragments.size(); i++) {
            SimpleFragment fragment = fragments.get(i);
            fragmentStates[i] = fragment.saveState();
        }
        return new State(fragmentStates, backStack.saveState());
    }

    public void restoreState(Parcelable parcelable) {
        State state = (State) parcelable;
        backStack.restoreState(state.backStackState);

        // We need to loop twice, once to add all the fragments to the manager, and once to restore
        // their states. This is so fragments will always see their children as existing.
        fragments = new ArrayList<>(state.fragmentStates.length);
        for (Parcelable fragmentState : state.fragmentStates) {
            SimpleFragment fragment = SimpleFragment.newInstance(fragmentState);
            fragments.add(fragment);
        }
        // Looping backwards makes it more likely that child states are restored before parents.
        for (int i = state.fragmentStates.length - 1; i >= 0; i--) {
            SimpleFragment fragment = fragments.get(i);
            Parcelable fragmentState = state.fragmentStates[i];
            fragment.restoreState(this, fragmentState);
        }
    }

    /**
     * Clears any references to state that will change on a configuration change and detaches all
     * fragments. This <em>must</em> be called when retaining the manager on a configuration change.
     * You can restore this cleared state with {@link #restoreConfigurationState(Activity)}.
     */
    public void clearConfigurationState() {
        for (SimpleFragment fragment : fragments) {
            if (fragment.getView() != null) {
                destroyView(fragment);
            }
        }
        activity = null;
    }

    /**
     * Restores references to configuration state and restores all fragments that were detaches with
     * {@link #clearConfigurationState()}.
     *
     * @param activity the activity to restore to.
     */
    public void restoreConfigurationState(Activity activity) {
        this.activity = activity;
    }

    private static class State implements Parcelable {
        Parcelable[] fragmentStates;
        Parcelable backStackState;

        State(Parcelable[] fragmentStates, Parcelable backStackState) {
            this.fragmentStates = fragmentStates;
            this.backStackState = backStackState;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelableArray(this.fragmentStates, flags);
            dest.writeParcelable(backStackState, flags);
        }

        @SuppressWarnings("unchecked")
        private State(Parcel in) {
            this.fragmentStates = in.readParcelableArray(getClass().getClassLoader());
            this.backStackState = in.readParcelable(getClass().getClassLoader());
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

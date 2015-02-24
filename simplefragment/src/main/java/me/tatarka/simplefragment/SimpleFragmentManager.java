package me.tatarka.simplefragment;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.tatarka.simplefragment.key.LayoutKey;
import me.tatarka.simplefragment.key.SimpleFragmentKey;

/**
 * This class manages the state of {@code SimpleFragments}. It creates and destroys them, attaches
 * and detaches them from the view hierarchy, and saves and restores their state. Other classes
 * build upon this to manage the fragments in a certain way, like nesting them in an Activity or
 * paging between them in a ViewPager.
 */
public class SimpleFragmentManager {
    private Context context;
    private List<SimpleFragment<?>> fragments;
    private Map<ExtraKey<?>, ExtraValue> extras;

    public SimpleFragmentManager(Context context) {
        this.context = context;
        this.fragments = new ArrayList<>();
        this.extras = new HashMap<>();
    }

    public Context getContext() {
        return context;
    }

    public List<SimpleFragment<?>> getFragments() {
        return Collections.unmodifiableList(fragments);
    }

    @Nullable
    public SimpleFragment<?> find(SimpleFragmentKey key) {
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
                keyName = ((LayoutKey) key).toString(getContext().getResources());
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
    public void destroy(SimpleFragment<?> fragment) {
        if (fragment == null) {
            throw new IllegalArgumentException("SimpleFragment cannot be null.");
        }
        if (fragment.getView() != null) {
            destroyView(fragment, null);
        }
        if (!fragments.remove(fragment)) {
            throw new IllegalArgumentException("Attempting to remove fragment that was not added: '" + fragment + "'");
        }
    }

    /**
     * Creates the view for the given fragment. This will trigger {@link
     * me.tatarka.simplefragment.SimpleFragment#onCreateViewHolder(LayoutInflater, ViewGroup)}
     * immediately. This will <em>not</em> occur automatically on configuration changes, you are
     * responsible for calling it again in those cases.
     *
     * @param fragment The {@code SimpleFragment} to createView.
     * @throws java.lang.IllegalArgumentException If the given fragment is null or was not added to
     *                                            this manager.
     */
    public View createView(SimpleFragment<?> fragment, LayoutInflater layoutInflater, @Nullable ViewGroup parentView) {
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
        LayoutInflater fragmentInflater = layoutInflater.cloneInContext(context);
        fragmentInflater.setFactory(new SimpleFragmentLayoutInflaterFactory(fragment.getSimpleFragmentContainer()));

        return fragment.createView(fragmentInflater, parentView);
    }

    /**
     * Destroys the {@code SimpleFragment}'s view. If it has already been destroyed is a no-op.
     *
     * @param fragment   The The {@code SimpleFragment} to destroyView.
     * @param parentView An optional parent view. If this is not null then the fragment will
     *                   automatically be removed from that parent. Otherwise, you are responsible
     *                   for removing it yourself.
     */
    public void destroyView(SimpleFragment<?> fragment, @Nullable ViewGroup parentView) {
        if (fragment == null) {
            throw new IllegalArgumentException("SimpleFragment cannot be null.");
        }
        View view = fragment.getView();
        if (view != null) {
            if (parentView != null) {
                parentView.removeView(view);
            }
            fragment.destroyView();
        }
    }

    public Parcelable saveState() {
        Parcelable[] fragmentStates = new Parcelable[fragments.size()];
        for (int i = 0; i < fragments.size(); i++) {
            SimpleFragment fragment = fragments.get(i);
            fragmentStates[i] = fragment.saveState(getContext());
        }
        Map<String, Parcelable> extraStates = new HashMap<>(extras.size());
        for (Map.Entry<ExtraKey<?>, ExtraValue> entry : extras.entrySet()) {
            extraStates.put(entry.getKey().name, entry.getValue());
        }
        return new State(fragmentStates, extraStates);
    }

    public void restoreState(Parcelable parcelable) {
        State state = (State) parcelable;
        extras = new HashMap<>(state.extraStates.size());
        for (Map.Entry<String, Parcelable> entry : state.extraStates.entrySet()) {
            ExtraKey<?> key = new ExtraKey<>(entry.getKey());
            ExtraValue value = (ExtraValue) entry.getValue();
            extras.put(key, value);
        }
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
     * You can restore this cleared state with {@link #restoreConfigurationState(Context)}.
     */
    public void clearConfigurationState() {
        for (SimpleFragment fragment : fragments) {
            if (fragment.getView() != null) {
                destroyView(fragment, null);
            }
        }
        context = null;
    }

    /**
     * Attach an arbitrary value to the {@code SimpleFragmentManager} that will be persisted with
     * it. This allows easy access to that value across anything that has a reference to the
     * manager.
     *
     * @param key   The key to store the extra with. Must be something unique (like a class name).
     * @param value The value to store.
     * @param <T>   The type of the value.
     * @see me.tatarka.simplefragment.SimpleFragmentManager.ExtraKey
     */
    public <T extends ExtraValue> void putExtra(ExtraKey<T> key, T value) {
        extras.put(key, value);
    }

    /**
     * Get the extra with the given key.
     *
     * @param key The key.
     * @param <T> The type fo the value.
     * @return The extra value.
     * @see #putExtra(ExtraKey, ExtraValue)
     */
    @SuppressWarnings("unchecked")
    public <T extends ExtraValue> T getExtra(ExtraKey<T> key) {
        return (T) extras.get(key);
    }

    /**
     * Removes the extra with the given key and returns it.
     *
     * @param key The key.
     * @param <T> The type fo the value.
     * @return The extra value.
     * @see #putExtra(ExtraKey, ExtraValue)
     */
    @SuppressWarnings("unchecked")
    public <T extends ExtraValue> T removeExtra(ExtraKey<T> key) {
        return (T) extras.remove(key);
    }

    /**
     * Restores references to configuration state and restores all fragments that were detaches with
     * {@link #clearConfigurationState()}.
     *
     * @param context the context to restore to.
     */
    public void restoreConfigurationState(Context context) {
        this.context = context;
    }

    private static class State implements Parcelable {
        Parcelable[] fragmentStates;
        Map<String, Parcelable> extraStates;

        State(Parcelable[] fragmentStates, Map<String, Parcelable> extraStates) {
            this.fragmentStates = fragmentStates;
            this.extraStates = extraStates;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelableArray(this.fragmentStates, flags);
            dest.writeMap(extraStates);
        }

        @SuppressWarnings("unchecked")
        private State(Parcel in) {
            this.fragmentStates = in.readParcelableArray(State.class.getClassLoader());
            this.extraStates = in.readHashMap(SimpleFragmentKey.class.getClassLoader());
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

    public static final class ExtraKey<T extends ExtraValue> {
        private String name;

        public ExtraKey(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ExtraKey<?> extraKey = (ExtraKey<?>) o;

            return name.equals(extraKey.name);

        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public interface ExtraValue extends Parcelable {
    }
}

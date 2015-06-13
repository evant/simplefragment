package me.tatarka.simplefragment.widget;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.SimpleFragmentManagerProvider;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.SimpleFragmentStateManager;
import me.tatarka.simplefragment.key.SimpleFragmentKey;
import me.tatarka.simplefragment.key.UuidKey;

/**
 * A {@link PagerAdapter} that supports SimpleFragments. Unlike {@link
 * android.support.v4.app.FragmentPagerAdapter}, you don't have to worry about state not being saved
 * or it not updating correctly when you call {@link #notifyDataSetChanged()}.
 */
public abstract class SimpleFragmentPagerAdapter extends PagerAdapter {
    private SimpleFragmentKey parentKey;
    private SimpleFragmentStateManager stateManager;
    private LayoutInflater layoutInflater;
    private List<SimpleFragmentIntent> fragmentIntents;
    private SparseArray<SimpleFragmentKey> fragmentKeys;

    public SimpleFragmentPagerAdapter(SimpleFragmentManagerProvider provider) {
        this(provider.getSimpleFragmentManager());
    }

    public SimpleFragmentPagerAdapter(SimpleFragmentManager manager) {
        this.parentKey = manager.getParentKey();
        this.stateManager = manager.getStateManager();
        this.layoutInflater = stateManager.getActivity().getLayoutInflater();
        this.fragmentKeys = new SparseArray<>();
        this.fragmentIntents = new ArrayList<>();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SimpleFragment fragment = stateManager.find(fragmentKeys.get(position));
        if (fragment == null) {
            SimpleFragmentIntent intent = getItem(position);
            if (intent == null) {
                throw new NullPointerException("getItem() returned null.");
            }
            SimpleFragmentKey key = UuidKey.create(parentKey);
            fragment = stateManager.create(intent, key);
        }
        View view = stateManager.createView(fragment, layoutInflater, container);
        container.addView(view);
        fragmentKeys.put(position, fragment.getKey());
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        SimpleFragment fragment = (SimpleFragment) object;
        View view = stateManager.destroyView(fragment);
        container.removeView(view);
    }

    @Override
    public void notifyDataSetChanged() {
        fragmentIntents.clear();
        for (int i = 0; i < getCount(); i++) {
            fragmentIntents.add(getItem(i));
        }
        SparseArray<SimpleFragmentKey> newKeyPositions = new SparseArray<>();
        List<Integer> keysToRemove = new ArrayList<>();
        for (int i = 0; i < fragmentKeys.size(); i++) {
            int oldPosition = fragmentKeys.keyAt(i);
            SimpleFragment fragment = stateManager.find(fragmentKeys.valueAt(i));
            int newPosition = getItemPosition(fragment);
            if (newPosition == POSITION_NONE) {
                keysToRemove.add(oldPosition);
                stateManager.destroy(fragment);
            } else if (oldPosition != POSITION_UNCHANGED && oldPosition != newPosition) {
                keysToRemove.add(oldPosition);
                newKeyPositions.put(newPosition, fragment.getKey());
            }
        }
        for (int keyToRemove : keysToRemove) {
            fragmentKeys.remove(keyToRemove);
        }
        for (int i = 0; i < newKeyPositions.size(); i++) {
            fragmentKeys.put(newKeyPositions.keyAt(i), newKeyPositions.valueAt(i));
        }
        super.notifyDataSetChanged();
    }

    /**
     * Called when the host view is attempting to determine if an item's position has changed.
     * Returns {@link #POSITION_UNCHANGED} if the position of the given item has not changed or
     * {@link #POSITION_NONE} if the item is no longer present in the adapter.
     * <p/>
     * <p>The default implementation assumes that the fragment hasn't changed if and only if the
     * returned {@code SimpleFragmentIntent} for the given position is equal to the previous one (in
     * both class name and arguments). This should cover the vast majority of cases but you may need
     * to override if you have 2 distinct fragments with the same intent.
     *
     * @param object Object representing an item, previously returned by a call to {@link
     *               #instantiateItem(View, int)}.
     * @return object's new position index from [0, {@link #getCount()}), {@link
     * #POSITION_UNCHANGED} if the object's position has not changed, or {@link #POSITION_NONE} if
     * the item is no longer present.
     */
    @Override
    public int getItemPosition(Object object) {
        SimpleFragment fragment = (SimpleFragment) object;
        for (int i = 0; i < fragmentIntents.size(); i++) {
            if (fragment.getIntent().equals(fragmentIntents.get(i))) {
                return i;
            }
        }
        return POSITION_NONE;
    }

    public abstract SimpleFragmentIntent<?> getItem(int position);

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((SimpleFragment) object).getView();
    }

    @Override
    public Parcelable saveState() {
        SparseArray<Object> fragmentKeysState = new SparseArray<>(fragmentKeys.size());
        for (int i = 0; i < fragmentKeys.size(); i++) {
            fragmentKeysState.append(fragmentKeys.keyAt(i), fragmentKeys.valueAt(i));
        }
        return new State(super.saveState(), fragmentKeysState);
    }

    @Override
    public void restoreState(Parcelable parcelable, ClassLoader loader) {
        State state = (State) parcelable;
        super.restoreState(state.parent, loader);
        this.fragmentKeys = new SparseArray<>(state.fragmentKeys.size());
        for (int i = 0; i < state.fragmentKeys.size(); i++) {
            fragmentKeys.append(state.fragmentKeys.keyAt(i), (SimpleFragmentKey) state.fragmentKeys.valueAt(i));
        }
    }

    /**
     * Gets the {@code SimpleFragment} for the given position in the adapter. Note that since a
     * viewpager instantiates it's items lazily, this method may return null if called before that
     * page has been instantiated.
     *
     * @param position The position to get the fragment for.
     * @return The fragment, or null if no fragment has been instantiated for the given position.
     */
    @Nullable
    public SimpleFragment getFragmentForPosition(int position) {
        if (position < 0 || position >= getCount()) {
            throw new IndexOutOfBoundsException();
        }
        return stateManager.find(fragmentKeys.get(position));
    }

    /**
     * Gets the position in the adapter for the given {@code SimpleFragment}.
     *
     * @param fragment The fragment to get the position of.
     * @return The position of the fragment, or -1 if the fragment is not found in the adapter.
     */
    public int getPositionForFragment(SimpleFragment fragment) {
        for (int i = 0; i < fragmentKeys.size(); i++) {
            SimpleFragmentKey key = fragmentKeys.valueAt(i);
            if (key.equals(fragment.getKey())) {
                return fragmentKeys.keyAt(i);
            }
        }
        return -1;
    }

    private static class State implements Parcelable {
        private Parcelable parent;
        private SparseArray<Object> fragmentKeys;

        private State(Parcelable parent, SparseArray<Object> fragmentKeys) {
            this.parent = parent;
            this.fragmentKeys = fragmentKeys;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.parent, flags);
            dest.writeSparseArray(fragmentKeys);
        }

        private State(Parcel in) {
            this.parent = in.readParcelable(Parcelable.class.getClassLoader());
            this.fragmentKeys = in.readSparseArray(SimpleFragmentKey.class.getClassLoader());
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

package me.tatarka.simplefragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.tatarka.simplefragment.key.SimpleFragmentKey;
import me.tatarka.simplefragment.key.TagKey;

/**
 * Created by evan on 3/22/15.
 */
public class SimpleFragmentDialogContainer {
    private SimpleFragmentManager fm;
    private SimpleFragmentKey parentKey;
    private LayoutInflater layoutInflater;
    private List<TagKey> attachedKeys;
    private Map<TagKey, SimpleDialogFragment> fragmentsPendingAttach;

    public SimpleFragmentDialogContainer(SimpleFragmentManager fm, @Nullable SimpleFragmentKey parentKey) {
        this.fm = fm;
        this.parentKey = parentKey;
        this.attachedKeys = new ArrayList<>();
        this.fragmentsPendingAttach = new HashMap<>();
    }

    public <T extends SimpleDialogFragment> T add(SimpleFragmentIntent<T> intent, @NonNull String tag) {
        TagKey key = new TagKey(parentKey, tag);
        T fragment = fm.create(intent, key);
        maybeAttachDialog(layoutInflater, fragment);
        return fragment;
    }

    @SuppressWarnings("unchecked")
    public <T extends SimpleDialogFragment> T findOrAdd(SimpleFragmentIntent<T> intent, @NonNull String tag) {
        for (TagKey key : attachedKeys) {
            if (key.getTag().equals(tag)) {
                return (T) fm.find(key);
            }
        }
        return add(intent, tag);
    }
    
    public void remove(SimpleDialogFragment<?> fragment) {
        TagKey key = (TagKey) fragment.getKey();
        attachedKeys.remove(key);
        if (fragment.getView() != null) {
            fm.destroyView(fragment, null);
        }
        fm.destroy(fragment); 
    }
    
    public SimpleDialogFragment<?> find(String tag) {
        if (tag == null) {
            return null;
        }
        for (TagKey key : attachedKeys) {
            if (key.getTag().equals(tag)) {
                return (SimpleDialogFragment<?>) fm.find(key);
            }
        }
        return null;
    }
    
    public List<SimpleDialogFragment<?>> getFragments() {
        List<SimpleDialogFragment<?>> fragments = new ArrayList<>(attachedKeys.size());
        for (TagKey key : attachedKeys) {
            fragments.add((SimpleDialogFragment<?>) fm.find(key));
        }
        return Collections.unmodifiableList(fragments);
    }

    public void setRootView(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
        
        // Restore previously attached fragments.
        for (TagKey key : attachedKeys) {
            SimpleDialogFragment fragment = (SimpleDialogFragment) fm.find(key);
            attachDialog(layoutInflater, fragment);
        }
        
        // View is ready, we can createView all pending fragments now.
        for (Map.Entry<TagKey, SimpleDialogFragment> entry : fragmentsPendingAttach.entrySet()) {
            attachDialog(layoutInflater, entry.getValue());
            attachedKeys.add(entry.getKey());
        }
        fragmentsPendingAttach.clear();
    }

    public void clearRootView() {
        for (TagKey key : attachedKeys) {
            SimpleDialogFragment fragment = (SimpleDialogFragment) fm.find(key);
            fm.destroyView(fragment, null);
        }
        this.layoutInflater = null;
    }

    private void maybeAttachDialog(LayoutInflater layoutInflater, SimpleDialogFragment fragment) {
        TagKey key = (TagKey) fragment.getKey();
        if (layoutInflater != null) {
            attachDialog(layoutInflater, fragment);
            attachedKeys.add(key);
        } else {
            fragmentsPendingAttach.put(key, fragment);
        }
    }

    @SuppressWarnings("unchecked")
    private void attachDialog(LayoutInflater layoutInflater, final SimpleDialogFragment fragment) {
        fm.createView(fragment, layoutInflater, null);
        Dialog dialog = fragment.getDialog();
        if (dialog != null) {
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    remove(fragment);
                }
            });
        }
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

    private static class State implements Parcelable {
        private List<TagKey> attachedKeys;

        State(List<TagKey> attachedKeys) {
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
            in.readTypedList(this.attachedKeys, TagKey.CREATOR);
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
}

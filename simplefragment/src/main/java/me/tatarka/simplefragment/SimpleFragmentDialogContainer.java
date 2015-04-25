package me.tatarka.simplefragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import me.tatarka.simplefragment.key.SimpleFragmentKey;
import me.tatarka.simplefragment.key.TagKey;

import java.util.*;

/**
 * Created by evan on 3/22/15.
 */
public class SimpleFragmentDialogContainer implements SimpleFragmentContainerManager.Value {
    public static final SimpleFragmentContainerManager.Key<SimpleFragmentDialogContainer> KEY = new SimpleFragmentContainerManager.Key<>("me.tatarka.simplefragment.SimpleFragmentDialogContainer");

    private SimpleFragmentManager fm;
    private SimpleFragmentKey parentKey;
    private View rootView;
    private List<TagKey> attachedKeys;
    private Map<TagKey, SimpleDialogFragment> fragmentsPendingAttach;

    public static SimpleFragmentDialogContainer getInstance(SimpleFragmentContainerManagerProvider provider) {
        return getInstance(provider.getSimpleFragmentContainerManager());
    }

    public static SimpleFragmentDialogContainer getInstance(SimpleFragmentContainerManager cm) {
        SimpleFragmentDialogContainer container = cm.get(KEY);
        if (container == null) {
            container = new SimpleFragmentDialogContainer();
            cm.put(KEY, container);
        }
        container.setManager(cm);
        return container;
    }

    public SimpleFragmentDialogContainer() {
        this.attachedKeys = new ArrayList<>();
        this.fragmentsPendingAttach = new HashMap<>();
    }

    private void setManager(SimpleFragmentContainerManager cm) {
        this.fm = cm.getSimpleFragmentManager();
        this.parentKey = cm.getParentKey();
    }

    @Override
    public void onAttachView(View rootView) {
        this.rootView = rootView;

        // Restore previously attached fragments.
        for (TagKey key : attachedKeys) {
            SimpleDialogFragment fragment = (SimpleDialogFragment) fm.find(key);
            attachDialog(rootView, fragment);
        }

        // View is ready, we can createView all pending fragments now.
        for (Map.Entry<TagKey, SimpleDialogFragment> entry : fragmentsPendingAttach.entrySet()) {
            attachDialog(rootView, entry.getValue());
            attachedKeys.add(entry.getKey());
        }
        fragmentsPendingAttach.clear();
    }

    @Override
    public void onClearView() {
        for (TagKey key : attachedKeys) {
            SimpleDialogFragment fragment = (SimpleDialogFragment) fm.find(key);
            fm.destroyView(fragment);
        }
        this.rootView = null;
    }

    public <T extends SimpleDialogFragment> T add(SimpleFragmentIntent<T> intent, @NonNull String tag) {
        TagKey key = new TagKey(parentKey, tag);
        T fragment = fm.create(intent, key);
        maybeAttachDialog(rootView, fragment);
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
            fm.destroyView(fragment);
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

    private void maybeAttachDialog(View rootView, SimpleDialogFragment fragment) {
        TagKey key = (TagKey) fragment.getKey();
        if (rootView != null) {
            attachDialog(rootView, fragment);
            attachedKeys.add(key);
        } else {
            fragmentsPendingAttach.put(key, fragment);
        }
    }

    @SuppressWarnings("unchecked")
    private void attachDialog(View rootView, final SimpleDialogFragment fragment) {
        fm.createView(fragment, LayoutInflater.from(rootView.getContext()), null);
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

    public Context getContext() {
        return fm.getContext();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(attachedKeys);
    }

    private SimpleFragmentDialogContainer(Parcel in) {
        this.attachedKeys = new ArrayList<>();
        in.readTypedList(this.attachedKeys, TagKey.CREATOR);
    }

    public static final Creator<SimpleFragmentDialogContainer> CREATOR = new Creator<SimpleFragmentDialogContainer>() {
        public SimpleFragmentDialogContainer createFromParcel(Parcel source) {
            return new SimpleFragmentDialogContainer(source);
        }

        public SimpleFragmentDialogContainer[] newArray(int size) {
            return new SimpleFragmentDialogContainer[size];
        }
    };
}

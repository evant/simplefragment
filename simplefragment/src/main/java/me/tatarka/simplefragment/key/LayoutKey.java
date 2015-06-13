package me.tatarka.simplefragment.key;

import android.content.res.Resources;
import android.os.Parcel;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentContainer;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.util.ResUtil;

/**
 * An implementation of {@code SimpleFragmentKey} that uses a view id. Useful, for tying a fragment
 * to a specific view in a layout.
 */
public class LayoutKey implements SimpleFragmentContainerKey {
    private SimpleFragmentKey parent;
    @IdRes
    private int viewId;
    private int index;

    public static LayoutKey of(@IdRes int viewId) {
        return new LayoutKey(null, viewId, 0);
    }

    private LayoutKey(SimpleFragmentKey parent, @IdRes int viewId, int index) {
        this.parent = parent;
        this.viewId = viewId;
        this.index = index;
    }

    @IdRes
    public int getViewId() {
        return viewId;
    }

    public int getIndex() {
        return index;
    }

    public LayoutKey withIndex(int index) {
        if (this.index == index) {
            return this;
        } else {
            return new LayoutKey(parent, viewId, index);
        }
    }

    @Override
    public void attach(SimpleFragmentContainer container, View rootView, SimpleFragment fragment) {
        SimpleFragmentManager fm = container.getSimpleFragmentManager();
        View parentView = rootView.findViewById(viewId);
        if (parentView == null) {
            throw new IllegalArgumentException("Cannot find view with id '" + ResUtil.safeGetIdName(fm.getActivity().getResources(), viewId) + "'.");
        }
        if (!(parentView instanceof ViewGroup)) {
            throw new IllegalArgumentException("View with id '" + ResUtil.safeGetIdName(fm.getActivity().getResources(), viewId) + "' is not an instance of ViewGroup.");
        }
        ViewGroup parent = (ViewGroup) parentView;
        View view = fm.createView(fragment, fragment.getLayoutInflater(), parent);
        parent.addView(view);
    }

    @Override
    public void detach(SimpleFragmentContainer container, View rootView, SimpleFragment fragment) {
        SimpleFragmentManager fm = container.getSimpleFragmentManager();
        ViewGroup parentView = (ViewGroup) rootView.findViewById(viewId);
        View view = fm.destroyView(fragment);
        parentView.removeView(view);
    }

    @Override
    public SimpleFragmentKey getParent() {
        return parent;
    }

    @Override
    public LayoutKey withParent(SimpleFragmentKey parent) {
        if (this.parent == parent) {
            return this;
        } else {
            return new LayoutKey(parent, viewId, index);
        }
    }

    @Override
    public boolean matches(@Nullable SimpleFragmentContainerKey other) {
        return other instanceof LayoutKey && ((LayoutKey) other).viewId == viewId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LayoutKey key = (LayoutKey) o;

        if (viewId != key.viewId) return false;
        if (index != key.index) return false;
        return !(parent != null ? !parent.equals(key.parent) : key.parent != null);

    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + viewId;
        result = 31 * result + index;
        return result;
    }

    @Override
    public String toString() {
        return toString(null);
    }

    public String toString(Resources resources) {
        StringBuilder builder = new StringBuilder("LayoutKey(");
        if (parent != null) {
            builder.append(parent).append(", ");
        }
        builder.append(ResUtil.safeGetIdName(resources, viewId));
        if (index != 0) {
            builder.append(", ").append(index);
        }
        builder.append(")");
        return builder.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.parent, 0);
        dest.writeInt(this.viewId);
        dest.writeInt(this.index);
    }

    private LayoutKey(Parcel in) {
        this.parent = in.readParcelable(SimpleFragmentKey.class.getClassLoader());
        this.viewId = in.readInt();
        this.index = in.readInt();
    }

    public static final Creator<LayoutKey> CREATOR = new Creator<LayoutKey>() {
        public LayoutKey createFromParcel(Parcel source) {
            return new LayoutKey(source);
        }

        public LayoutKey[] newArray(int size) {
            return new LayoutKey[size];
        }
    };
}

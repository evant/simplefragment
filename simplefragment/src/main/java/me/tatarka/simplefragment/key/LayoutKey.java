package me.tatarka.simplefragment.key;

import android.content.res.Resources;
import android.os.Parcel;
import android.support.annotation.IdRes;

import me.tatarka.simplefragment.util.ResUtil;

/**
 * An implementation of {@code SimpleFragmentKey} that uses a view id. Useful, for tying a fragment
 * to a specific view in a layout.
 */
public class LayoutKey implements SimpleFragmentKey {
    private SimpleFragmentKey parent;
    @IdRes
    private int viewId;
    private int index;

    public LayoutKey(@IdRes int viewId) {
        this(viewId, 0);
    }

    public LayoutKey(@IdRes int viewId, int index) {
        this(null, viewId, index);
    }

    public LayoutKey(SimpleFragmentKey parent, @IdRes int viewId) {
        this(parent, viewId, 0);
    }

    public LayoutKey(SimpleFragmentKey parent, @IdRes int viewId, int index) {
        this.parent = parent;
        this.viewId = viewId;
        this.index = index;
    }

    public SimpleFragmentKey getParent() {
        return parent;
    }

    @IdRes
    public int getViewId() {
        return viewId;
    }

    public int getIndex() {
        return index;
    }

    @Override
    public int describeContents() {
        return 0;
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
}

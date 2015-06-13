package me.tatarka.simplefragment.key;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.UUID;

/**
 * Created by evan on 3/14/15.
 */
public class UuidKey implements SimpleFragmentKey {
    @NonNull
    private final String uuid;
    private SimpleFragmentKey parent;

    public static UuidKey create(@Nullable SimpleFragmentKey parent) {
        return new UuidKey(parent);
    }

    private UuidKey(@Nullable SimpleFragmentKey parent) {
        this.uuid = UUID.randomUUID().toString();
        this.parent = parent;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UuidKey uuidKey = (UuidKey) o;

        if (!uuid.equals(uuidKey.uuid)) return false;
        return !(parent != null ? !parent.equals(uuidKey.parent) : uuidKey.parent != null);

    }

    @Override
    public int hashCode() {
        int result = uuid.hashCode();
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("UuidKey(");
        if (parent != null) {
            builder.append(parent).append(", ");
        }
        builder.append(uuid).append(")");
        return builder.toString();
    }

    @Nullable
    @Override
    public SimpleFragmentKey getParent() {
        return parent;
    }

    protected UuidKey(Parcel in) {
        uuid = in.readString();
        parent = in.readParcelable(SimpleFragmentKey.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uuid);
        dest.writeParcelable(parent, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UuidKey> CREATOR = new Creator<UuidKey>() {
        @Override
        public UuidKey createFromParcel(Parcel in) {
            return new UuidKey(in);
        }

        @Override
        public UuidKey[] newArray(int size) {
            return new UuidKey[size];
        }
    };
}

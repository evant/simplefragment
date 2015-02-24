package me.tatarka.simplefragment.key;

import android.os.Parcel;

import java.util.UUID;

/**
 * Created by evan on 3/14/15.
 */
public class UuidKey implements SimpleFragmentKey {
    private String uuid;

    public UuidKey() {
        uuid = UUID.randomUUID().toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UuidKey uuidKey = (UuidKey) o;

        return uuid.equals(uuidKey.uuid);

    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public String toString() {
        return "UuidKey(" + uuid + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.uuid);
    }

    private UuidKey(Parcel in) {
        this.uuid = in.readString();
    }

    public static final Creator<UuidKey> CREATOR = new Creator<UuidKey>() {
        public UuidKey createFromParcel(Parcel source) {
            return new UuidKey(source);
        }

        public UuidKey[] newArray(int size) {
            return new UuidKey[size];
        }
    };
}

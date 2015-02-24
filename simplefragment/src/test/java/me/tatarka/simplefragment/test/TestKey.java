package me.tatarka.simplefragment.test;

import android.os.Parcel;

import me.tatarka.simplefragment.key.SimpleFragmentKey;

/**
 * Created by evan on 3/12/15.
 */
public class TestKey implements SimpleFragmentKey, android.os.Parcelable {

    @Override
    public boolean equals(Object o) {
        return o != null && getClass().equals(o.getClass());
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public TestKey() {
    }

    private TestKey(Parcel in) {
    }

    public static final Creator<TestKey> CREATOR = new Creator<TestKey>() {
        public TestKey createFromParcel(Parcel source) {
            return new TestKey(source);
        }

        public TestKey[] newArray(int size) {
            return new TestKey[size];
        }
    };
}

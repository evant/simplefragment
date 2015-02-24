package me.tatarka.simplefragment.key;

import android.os.Parcel;

/**
 * An implementation of {@code SimpleFragmentKey} that uses a position. Useful in a viewpager, for
 * example.
 */
public class PositionKey implements SimpleFragmentKey {
    private SimpleFragmentKey parent;
    private int position;

    public PositionKey(int position) {
        this(null, position);
    }

    public PositionKey(SimpleFragmentKey parent, int position) {
        this.parent = parent;
        this.position = position;
    }
    
    public int getPosition() {
        return position;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.parent, 0);
        dest.writeInt(this.position);
    }

    private PositionKey(Parcel in) {
        this.parent = in.readParcelable(SimpleFragmentKey.class.getClassLoader());
        this.position = in.readInt();
    }

    public static final Creator<PositionKey> CREATOR = new Creator<PositionKey>() {
        public PositionKey createFromParcel(Parcel source) {
            return new PositionKey(source);
        }

        public PositionKey[] newArray(int size) {
            return new PositionKey[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PositionKey layoutKey = (PositionKey) o;

        if (position != layoutKey.position) return false;
        return !(parent != null ? !parent.equals(layoutKey.parent) : layoutKey.parent != null);

    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + position;
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("PositionKey(");
        if (parent != null) {
            builder.append(parent).append(", ");
        }
        builder.append(position).append(")");
        return builder.toString();
    }
}

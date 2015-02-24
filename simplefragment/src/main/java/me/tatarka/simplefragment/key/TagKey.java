package me.tatarka.simplefragment.key;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * An implementation of {@code SimpleFragmentKey} that uses a unique string tag. 
 */
public class TagKey implements SimpleFragmentKey {
    private SimpleFragmentKey parent;
    private String tag;

    public TagKey(@NonNull String tag) {
        this(null, tag);
    }

    public TagKey(SimpleFragmentKey parent, @NonNull String tag) {
        this.parent = parent;
        this.tag = tag;
    }
    
    public String getTag() {
        return tag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.parent, 0);
        dest.writeString(this.tag);
    }

    private TagKey(Parcel in) {
        this.parent = in.readParcelable(SimpleFragmentKey.class.getClassLoader());
        this.tag = in.readString();
    }

    public static final Creator<TagKey> CREATOR = new Creator<TagKey>() {
        public TagKey createFromParcel(Parcel source) {
            return new TagKey(source);
        }

        public TagKey[] newArray(int size) {
            return new TagKey[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TagKey tagKey = (TagKey) o;

        if (parent != null ? !parent.equals(tagKey.parent) : tagKey.parent != null) return false;
        return tag.equals(tagKey.tag);

    }

    @Override
    public int hashCode() {
        int result = parent != null ? parent.hashCode() : 0;
        result = 31 * result + tag.hashCode();
        return result;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("TagKey(");
        if (parent != null) {
            builder.append(parent).append(", ");
        }
        builder.append(tag).append(")");
        return builder.toString();
    }
}

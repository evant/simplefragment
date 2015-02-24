package me.tatarka.simplefragment;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Set;

/**
 * Created by evan on 3/7/15.
 */
public final class SimpleFragmentIntent<T extends SimpleFragment> implements Parcelable {
    private String simpleFragmentClassName;
    private Bundle args;

    public SimpleFragmentIntent(Class<T> simpleFragmentClass) {
        simpleFragmentClassName = simpleFragmentClass.getName();
        args = new Bundle();
    }

    public SimpleFragmentIntent<T> putArgs(Bundle args) {
        args.putAll(args);
        return this;
    }

    public Bundle getArgs() {
        return args;
    }

    //TODO: bundle convenience methods like Intent

    public SimpleFragmentIntent<T> putArg(String key, int value) {
        args.putInt(key, value);
        return this;
    }
    
    public int getIntArg(String key) {
        return args.getInt(key);
    }
    
    public int getIntArg(String key, int defaultValue) {
        return args.getInt(key, defaultValue);
    }

    public String getSimpleFragmentClassName() {
        return simpleFragmentClassName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !getClass().equals(o.getClass())) {
            return false;
        }
        
        SimpleFragmentIntent other = (SimpleFragmentIntent) o;
        return simpleFragmentClassName.equals(other.simpleFragmentClassName)
                && equalBundles(args, other.args);
        
    }

    private static boolean equalBundles(Bundle one, Bundle two) {
        if (one.size() != two.size())
            return false;

        Set<String> setOne = one.keySet();
        Object valueOne;
        Object valueTwo;
        
        if (setOne == null && two.keySet() == null) {
            return true;
        }

        for (String key : setOne) {
            valueOne = one.get(key);
            valueTwo = two.get(key);
            if (valueOne instanceof Bundle && valueTwo instanceof Bundle &&
                    !equalBundles((Bundle) valueOne, (Bundle) valueTwo)) {
                return false;
            } else if (valueOne == null) {
                if (valueTwo != null || !two.containsKey(key)) {
                    return false;
                }
            } else if (!valueOne.equals(valueTwo)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.simpleFragmentClassName);
        dest.writeBundle(args);
    }

    private SimpleFragmentIntent(Parcel in) {
        this.simpleFragmentClassName = in.readString();
        args = in.readBundle();
    }

    public static final Parcelable.Creator<SimpleFragmentIntent> CREATOR = new Parcelable.Creator<SimpleFragmentIntent>() {
        public SimpleFragmentIntent createFromParcel(Parcel source) {
            return new SimpleFragmentIntent(source);
        }

        public SimpleFragmentIntent[] newArray(int size) {
            return new SimpleFragmentIntent[size];
        }
    };
}

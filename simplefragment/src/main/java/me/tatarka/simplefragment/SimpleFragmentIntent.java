package me.tatarka.simplefragment;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Set;

/**
 * An intent to create a {@link SimpleFragment}, in a simlar way in which you would create
 * activities.
 */
public final class SimpleFragmentIntent<F extends SimpleFragment> implements Parcelable {
    @NonNull
    private final String simpleFragmentClassName;
    @Nullable
    private Bundle extras;

    /**
     * Constructs a new intent for the given fragment class.
     */
    public static <F extends SimpleFragment> SimpleFragmentIntent<F> of(@NonNull Class<F> simpleFragmentClass) {
        return new SimpleFragmentIntent<>(simpleFragmentClass);
    }

    private SimpleFragmentIntent(@NonNull Class<F> simpleFragmentClass) {
        simpleFragmentClassName = simpleFragmentClass.getName();
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type is stored with
     *                     the given name.
     * @return the value of an item that previously added with putExtra() or the default value if
     * none was found.
     * @see #putExtra(String, boolean)
     */
    public boolean getBooleanExtra(String name, boolean defaultValue) {
        return extras == null ? defaultValue :
                extras.getBoolean(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type is stored with
     *                     the given name.
     * @return the value of an item that previously added with putExtra() or the default value if
     * none was found.
     * @see #putExtra(String, byte)
     */
    public byte getByteExtra(String name, byte defaultValue) {
        return extras == null ? defaultValue :
                extras.getByte(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type is stored with
     *                     the given name.
     * @return the value of an item that previously added with putExtra() or the default value if
     * none was found.
     * @see #putExtra(String, short)
     */
    public short getShortExtra(String name, short defaultValue) {
        return extras == null ? defaultValue :
                extras.getShort(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type is stored with
     *                     the given name.
     * @return the value of an item that previously added with putExtra() or the default value if
     * none was found.
     * @see #putExtra(String, char)
     */
    public char getCharExtra(String name, char defaultValue) {
        return extras == null ? defaultValue :
                extras.getChar(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type is stored with
     *                     the given name.
     * @return the value of an item that previously added with putExtra() or the default value if
     * none was found.
     * @see #putExtra(String, int)
     */
    public int getIntExtra(String name, int defaultValue) {
        return extras == null ? defaultValue :
                extras.getInt(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type is stored with
     *                     the given name.
     * @return the value of an item that previously added with putExtra() or the default value if
     * none was found.
     * @see #putExtra(String, long)
     */
    public long getLongExtra(String name, long defaultValue) {
        return extras == null ? defaultValue :
                extras.getLong(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type is stored with
     *                     the given name.
     * @return the value of an item that previously added with putExtra(), or the default value if
     * no such item is present
     * @see #putExtra(String, float)
     */
    public float getFloatExtra(String name, float defaultValue) {
        return extras == null ? defaultValue :
                extras.getFloat(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name         The name of the desired item.
     * @param defaultValue the value to be returned if no value of the desired type is stored with
     *                     the given name.
     * @return the value of an item that previously added with putExtra() or the default value if
     * none was found.
     * @see #putExtra(String, double)
     */
    public double getDoubleExtra(String name, double defaultValue) {
        return extras == null ? defaultValue :
                extras.getDouble(name, defaultValue);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no String value
     * was found.
     * @see #putExtra(String, String)
     */
    public String getStringExtra(String name) {
        return extras == null ? null : extras.getString(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no CharSequence
     * value was found.
     * @see #putExtra(String, CharSequence)
     */
    public CharSequence getCharSequenceExtra(String name) {
        return extras == null ? null : extras.getCharSequence(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no Parcelable
     * value was found.
     * @see #putExtra(String, Parcelable)
     */
    public <T extends Parcelable> T getParcelableExtra(String name) {
        return extras == null ? null : extras.<T>getParcelable(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no Parcelable[]
     * value was found.
     * @see #putExtra(String, Parcelable[])
     */
    public Parcelable[] getParcelableArrayExtra(String name) {
        return extras == null ? null : extras.getParcelableArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no
     * ArrayList<Parcelable> value was found.
     * @see #putParcelableArrayListExtra(String, ArrayList)
     */
    public <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(String name) {
        return extras == null ? null : extras.<T>getParcelableArrayList(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no Serializable
     * value was found.
     * @see #putExtra(String, Serializable)
     */
    public Serializable getSerializableExtra(String name) {
        return extras == null ? null : extras.getSerializable(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no
     * ArrayList<Integer> value was found.
     * @see #putIntegerArrayListExtra(String, ArrayList)
     */
    public ArrayList<Integer> getIntegerArrayListExtra(String name) {
        return extras == null ? null : extras.getIntegerArrayList(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no
     * ArrayList<String> value was found.
     * @see #putStringArrayListExtra(String, ArrayList)
     */
    public ArrayList<String> getStringArrayListExtra(String name) {
        return extras == null ? null : extras.getStringArrayList(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no
     * ArrayList<CharSequence> value was found.
     * @see #putCharSequenceArrayListExtra(String, ArrayList)
     */
    public ArrayList<CharSequence> getCharSequenceArrayListExtra(String name) {
        return extras == null ? null : extras.getCharSequenceArrayList(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no boolean
     * array value was found.
     * @see #putExtra(String, boolean[])
     */
    public boolean[] getBooleanArrayExtra(String name) {
        return extras == null ? null : extras.getBooleanArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no byte array
     * value was found.
     * @see #putExtra(String, byte[])
     */
    public byte[] getByteArrayExtra(String name) {
        return extras == null ? null : extras.getByteArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no short array
     * value was found.
     * @see #putExtra(String, short[])
     */
    public short[] getShortArrayExtra(String name) {
        return extras == null ? null : extras.getShortArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no char array
     * value was found.
     * @see #putExtra(String, char[])
     */
    public char[] getCharArrayExtra(String name) {
        return extras == null ? null : extras.getCharArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no int array
     * value was found.
     * @see #putExtra(String, int[])
     */
    public int[] getIntArrayExtra(String name) {
        return extras == null ? null : extras.getIntArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no long array
     * value was found.
     * @see #putExtra(String, long[])
     */
    public long[] getLongArrayExtra(String name) {
        return extras == null ? null : extras.getLongArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no float array
     * value was found.
     * @see #putExtra(String, float[])
     */
    public float[] getFloatArrayExtra(String name) {
        return extras == null ? null : extras.getFloatArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no double array
     * value was found.
     * @see #putExtra(String, double[])
     */
    public double[] getDoubleArrayExtra(String name) {
        return extras == null ? null : extras.getDoubleArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no String array
     * value was found.
     * @see #putExtra(String, String[])
     */
    public String[] getStringArrayExtra(String name) {
        return extras == null ? null : extras.getStringArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no CharSequence
     * array value was found.
     * @see #putExtra(String, CharSequence[])
     */
    public CharSequence[] getCharSequenceArrayExtra(String name) {
        return extras == null ? null : extras.getCharSequenceArray(name);
    }

    /**
     * Retrieve extended data from the intent.
     *
     * @param name The name of the desired item.
     * @return the value of an item that previously added with putExtra() or null if no Bundle value
     * was found.
     * @see #putExtra(String, Bundle)
     */
    public Bundle getBundleExtra(String name) {
        return extras == null ? null : extras.getBundle(name);
    }

    /**
     * Retrieves a map of extended data from the intent.
     *
     * @return the map of all extras previously added with putExtra()
     */
    public Bundle getExtras() {
        return extras == null ? new Bundle() : new Bundle(extras);
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The boolean data value.
     * @return Returns the same Intent object, for chaining multiple calls into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getBooleanExtra(String, boolean)
     */
    public SimpleFragmentIntent<F> putExtra(String name, boolean value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putBoolean(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The byte data value.
     * @return Returns the same Intent object, for chaining multiple calls into a single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getByteExtra(String, byte)
     */
    public SimpleFragmentIntent<F> putExtra(String name, byte value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putByte(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The char data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharExtra(String, char)
     */
    public SimpleFragmentIntent<F> putExtra(String name, char value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putChar(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The short data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getShortExtra(String, short)
     */
    public SimpleFragmentIntent<F> putExtra(String name, short value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putShort(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The integer data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntExtra(String, int)
     */
    public SimpleFragmentIntent<F> putExtra(String name, int value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putInt(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The long data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getLongExtra(String, long)
     */
    public SimpleFragmentIntent<F> putExtra(String name, long value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putLong(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The float data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getFloatExtra(String, float)
     */
    public SimpleFragmentIntent<F> putExtra(String name, float value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putFloat(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The double data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getDoubleExtra(String, double)
     */
    public SimpleFragmentIntent<F> putExtra(String name, double value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putDouble(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The String data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, String value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putString(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The CharSequence data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, CharSequence value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putCharSequence(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The Parcelable data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getParcelableExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, Parcelable value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putParcelable(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The Parcelable[] data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getParcelableArrayExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, Parcelable[] value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putParcelableArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The ArrayList<Parcelable> data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getParcelableArrayListExtra(String)
     */
    public SimpleFragmentIntent<F> putParcelableArrayListExtra(String name, ArrayList<? extends Parcelable> value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putParcelableArrayList(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The ArrayList<Integer> data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntegerArrayListExtra(String)
     */
    public SimpleFragmentIntent<F> putIntegerArrayListExtra(String name, ArrayList<Integer> value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putIntegerArrayList(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The ArrayList<String> data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringArrayListExtra(String)
     */
    public SimpleFragmentIntent<F> putStringArrayListExtra(String name, ArrayList<String> value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putStringArrayList(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The ArrayList<CharSequence> data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceArrayListExtra(String)
     */
    public SimpleFragmentIntent<F> putCharSequenceArrayListExtra(String name, ArrayList<CharSequence> value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putCharSequenceArrayList(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The Serializable data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getSerializableExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, Serializable value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putSerializable(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The boolean array data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getBooleanArrayExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, boolean[] value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putBooleanArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The byte array data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getByteArrayExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, byte[] value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putByteArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The short array data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getShortArrayExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, short[] value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putShortArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The char array data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharArrayExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, char[] value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putCharArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The int array data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getIntArrayExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, int[] value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putIntArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The byte array data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getLongArrayExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, long[] value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putLongArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The float array data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getFloatArrayExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, float[] value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putFloatArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The double array data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getDoubleArrayExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, double[] value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putDoubleArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The String array data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getStringArrayExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, String[] value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putStringArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The CharSequence array data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getCharSequenceArrayExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, CharSequence[] value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putCharSequenceArray(name, value);
        return this;
    }

    /**
     * Add extended data to the intent.
     *
     * @param name  The name of the extra data, with package prefix.
     * @param value The Bundle data value.
     * @return Returns the same SimpleFragmentIntent<F> object, for chaining multiple calls into a
     * single statement.
     * @see #putExtras
     * @see #removeExtra
     * @see #getBundleExtra(String)
     */
    public SimpleFragmentIntent<F> putExtra(String name, Bundle value) {
        if (extras == null) {
            extras = new Bundle();
        }
        extras.putBundle(name, value);
        return this;
    }

    /**
     * Copy all extras in 'src' in to this intent.
     *
     * @param src Contains the extras to copy.
     * @see #putExtra
     */
    public SimpleFragmentIntent<F> putExtras(SimpleFragmentIntent<?> src) {
        if (src.extras != null) {
            if (extras == null) {
                extras = new Bundle(src.extras);
            } else {
                extras.putAll(src.extras);
            }
        }
        return this;
    }

    /**
     * Add a set of extended data to the intent.  SimpleFragmentContainer
     *
     * @param extras The Bundle of extras to add to this intent.
     * @see #putExtra
     * @see #removeExtra
     */
    public SimpleFragmentIntent<F> putExtras(Bundle extras) {
        if (this.extras == null) {
            this.extras = new Bundle();
        }
        this.extras.putAll(extras);
        return this;
    }

    /**
     * Completely replace the extras in the SimpleFragmentIntent<F> with the extras in the given
     * Intent.
     *
     * @param src The exact extras contained in this SimpleFragmentIntent<F> are copied into the
     *            target intent, replacing any that were previously there.
     */
    public SimpleFragmentIntent<F> replaceExtras(SimpleFragmentIntent<?> src) {
        extras = src.extras != null ? new Bundle(src.extras) : null;
        return this;
    }

    /**
     * Completely replace the extras in the SimpleFragmentIntent<F> with the given Bundle of
     * extras.
     *
     * @param extras The new set of extras in the SimpleFragmentIntent<F>, or null to erase all
     *               extras.
     */
    public SimpleFragmentIntent<F> replaceExtras(Bundle extras) {
        this.extras = extras != null ? new Bundle(extras) : null;
        return this;
    }

    /**
     * Remove extended data from the intent.
     *
     * @see #putExtra
     */
    public void removeExtra(String name) {
        if (extras != null) {
            extras.remove(name);
            if (extras.size() == 0) {
                extras = null;
            }
        }
    }

    /**
     * Returns the fragment class name that the intent will contruct.
     */
    @NonNull
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
                && equalBundles(extras, other.extras);

    }

    private static boolean equalBundles(@Nullable Bundle one, @Nullable Bundle two) {
        if (one == null && two == null) {
            return true;
        }

        if (one == null || two == null) {
            return false;
        }

        if (one.size() != two.size()) {
            return false;
        }

        Set<String> setOne = one.keySet();
        Object valueOne;
        Object valueTwo;

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
        dest.writeBundle(extras);
    }

    private SimpleFragmentIntent(Parcel in) {
        this.simpleFragmentClassName = in.readString();
        extras = in.readBundle();
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

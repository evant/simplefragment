package me.tatarka.simplefragment.key;

import android.os.Parcelable;

/**
 * A {@code SimpleFragmentKey} uniquely identifies a {@code SimpleFragment} in the {@code
 * SimpleFragmentManager}. You can implement your own keys that provide the necessary information to
 * reattach a fragment on a configuration change, or use can use one of the provided
 * implementations. Implementations of this class <em>must</em> implement {@code equals()}, {@code
 * hashcode()}, and the {@code Parcelable} interface. It is common to nest fragments inside each
 * other. To support this, you can take a parent {@code SimpleFragmentKey} as an argument as use
 * that as part of the equality implementation.
 *
 * @see LayoutKey
 * @see PositionKey
 */
public interface SimpleFragmentKey extends Parcelable {
}

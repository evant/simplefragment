package me.tatarka.simplefragment;

/**
 * A convenience interface so that you can just pass {@code foo} instead of {@code
 * foo.getSimpleFragmentManager()}. Both {@link SimpleFragment} and {@link
 * me.tatarka.simplefragment.activity.SimpleFragmentActivity} implement this interface.
 */
public interface SimpleFragmentManagerProvider {
    SimpleFragmentManager getSimpleFragmentManager();
}

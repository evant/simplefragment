package me.tatarka.simplefragment.test;

import android.content.Context;
import android.os.Parcelable;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import me.tatarka.simplefragment.SimpleFragmentContainer;
import me.tatarka.simplefragment.SimpleFragmentContainerManager;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.SimpleFragmentManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Created by evan on 3/21/15.
 */
@RunWith(CustomRobolectricRunner.class) // Robolectric needed to not crash on Bundle usage.
public class SimpleFragmentContainerTest {
    @Mock
    Context context;
    @Mock
    ViewGroup rootView;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(rootView.findViewById(anyInt())).thenReturn(rootView);
    }

    @Test
    public void testAddAfterSetRootView() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainerManager cm = new SimpleFragmentContainerManager(fm, null);
        cm.setView(rootView);
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        TestSimpleFragment fragment = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testAddBeforeSetRootView() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainerManager cm = new SimpleFragmentContainerManager(fm, null);
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        TestSimpleFragment fragment = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        cm.setView(rootView);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testAddConfigurationChange() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainerManager cm = new SimpleFragmentContainerManager(fm, null);
        cm.setView(rootView);
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        TestSimpleFragment fragment = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        cm.clearView();
        fm.clearConfigurationState();
        fragment.wasOnCreateViewCalled = false;
        fm.restoreConfigurationState(context);
        cm.setView(rootView);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testAddSaveState() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainerManager cm = new SimpleFragmentContainerManager(fm, null);
        cm.setView(rootView);
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        TestSimpleFragment fragment = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        Parcelable fmState = fm.saveState();
        Parcelable cmState = cm.saveState();
        fm = new SimpleFragmentManager(context);
        cm = new SimpleFragmentContainerManager(fm, null);
        fm.restoreState(fmState);
        cm.restoreState(cmState);

        assertThat(container.find(0)).isEqualTo(fragment);
    }

    @Test
    public void testRemove() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainerManager cm = new SimpleFragmentContainerManager(fm, null);
        cm.setView(rootView);
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        TestSimpleFragment fragment = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        container.remove(fragment);

        assertThat(fm.getFragments()).isEmpty();
    }

    @Test
    public void testPush() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainerManager cm = new SimpleFragmentContainerManager(fm, null);
        cm.setView(rootView);
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        TestSimpleFragment fragment1 = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        TestSimpleFragment fragment2 = container.push(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);

        assertThat(container.find(0)).isSameAs(fragment2);
    }

    @Test
    public void testPushConfigurationChange() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainerManager cm = new SimpleFragmentContainerManager(fm, null);
        cm.setView(rootView);
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        TestSimpleFragment fragment1 = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        TestSimpleFragment fragment2 = container.push(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        cm.clearView();
        fm.clearConfigurationState();
        fm.restoreConfigurationState(context);
        cm.setView(rootView);

        assertThat(container.find(0)).isSameAs(fragment2);
    }

    @Test
    public void testPushAfterConfigurationChange() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainerManager cm = new SimpleFragmentContainerManager(fm, null);
        cm.setView(rootView);
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        TestSimpleFragment fragment1 = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        cm.clearView();
        ;
        fm.clearConfigurationState();
        fm.restoreConfigurationState(context);
        cm.setView(rootView);
        TestSimpleFragment fragment2 = container.push(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        cm.clearView();
        fm.clearConfigurationState();
        fm.restoreConfigurationState(context);
        cm.setView(rootView);

        assertThat(container.find(0)).isSameAs(fragment2);
    }

    @Test
    public void testPushSaveState() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainerManager cm = new SimpleFragmentContainerManager(fm, null);
        cm.setView(rootView);
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        TestSimpleFragment fragment1 = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        TestSimpleFragment fragment2 = container.push(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        Parcelable fmState = fm.saveState();
        Parcelable cmState = cm.saveState();
        fm.clearConfigurationState();
        cm.clearView();
        fm = new SimpleFragmentManager(context);
        fm.restoreState(fmState);
        cm = new SimpleFragmentContainerManager(fm, null);
        cm.restoreState(cmState);
        cm.setView(rootView);

        assertThat(container.find(0)).isEqualTo(fragment2);
    }

    @Test
    public void testPop() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainerManager cm = new SimpleFragmentContainerManager(fm, null);
        cm.setView(rootView);
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        TestSimpleFragment fragment1 = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        TestSimpleFragment fragment2 = container.push(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        container.pop();

        assertThat(container.find(0)).isSameAs(fragment1);
    }

    @Test
    public void testPopConfigurationChange() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainerManager cm = new SimpleFragmentContainerManager(fm, null);
        cm.setView(rootView);
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        TestSimpleFragment fragment1 = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        TestSimpleFragment fragment2 = container.push(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        cm.clearView();
        fm.clearConfigurationState();
        fm.restoreConfigurationState(context);
        cm.setView(rootView);
        container.pop();

        assertThat(container.find(0)).isSameAs(fragment1);
    }

    @Test
    public void testPopSaveState() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainerManager cm = new SimpleFragmentContainerManager(fm, null);
        cm.setView(rootView);
        SimpleFragmentContainer container = SimpleFragmentContainer.getInstance(cm);
        TestSimpleFragment fragment1 = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        TestSimpleFragment fragment2 = container.push(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        Parcelable fmState = fm.saveState();
        Parcelable cmState = cm.saveState();
        fm.clearConfigurationState();
        cm.clearView();
        fm = new SimpleFragmentManager(context);
        fm.restoreState(fmState);
        cm = new SimpleFragmentContainerManager(fm, null);
        cm.restoreState(cmState);
        cm.setView(rootView);
        container = SimpleFragmentContainer.getInstance(cm);
        container.pop();

        assertThat(container.find(0)).isEqualTo(fragment1);
    }
}

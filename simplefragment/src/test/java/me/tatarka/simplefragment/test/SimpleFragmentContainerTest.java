package me.tatarka.simplefragment.test;

import android.app.Activity;
import android.os.Parcelable;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import me.tatarka.simplefragment.SimpleFragmentContainer;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.key.LayoutKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Created by evan on 3/21/15.
 */
@RunWith(CustomRobolectricRunner.class) // Robolectric needed to not crash on Bundle usage.
public class SimpleFragmentContainerTest {
    private static final LayoutKey KEY = LayoutKey.of(0);

    @Mock
    Activity activity;
    @Mock
    ViewGroup rootView;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(rootView.findViewById(anyInt())).thenReturn(rootView);
    }

    @Test
    public void testAddAfterSetRootView() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testAddBeforeSetRootView() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        TestSimpleFragment fragment = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        cm.setView(rootView);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testAddConfigurationChange() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        cm.clearView();
        fm.clearConfigurationState();
        fragment.wasOnCreateViewCalled = false;
        fm.restoreConfigurationState(activity);
        cm.setView(rootView);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testAddSaveState() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        Parcelable fmState = fm.saveState();
        Parcelable cmState = cm.saveState();
        fm = new SimpleFragmentManager(activity);
        cm = new SimpleFragmentContainer(fm, null);
        fm.restoreState(fmState);
        cm.restoreState(cmState);

        assertThat(cm.find(KEY)).isEqualTo(fragment);
    }

    @Test
    public void testRemove() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        cm.remove(fragment);

        assertThat(fm.getFragments()).isEmpty();
    }

    @Test
    public void testPush() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment1 = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = cm.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);

        assertThat(cm.find(KEY)).isSameAs(fragment2);
    }

    @Test
    public void testPushConfigurationChange() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment1 = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = cm.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        cm.clearView();
        fm.clearConfigurationState();
        fm.restoreConfigurationState(activity);
        cm.setView(rootView);

        assertThat(cm.find(KEY)).isSameAs(fragment2);
    }

    @Test
    public void testPushAfterConfigurationChange() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment1 = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        cm.clearView();
        ;
        fm.clearConfigurationState();
        fm.restoreConfigurationState(activity);
        cm.setView(rootView);
        TestSimpleFragment fragment2 = cm.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        cm.clearView();
        fm.clearConfigurationState();
        fm.restoreConfigurationState(activity);
        cm.setView(rootView);

        assertThat(cm.find(KEY)).isSameAs(fragment2);
    }

    @Test
    public void testPushSaveState() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment1 = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = cm.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        Parcelable fmState = fm.saveState();
        Parcelable cmState = cm.saveState();
        fm.clearConfigurationState();
        cm.clearView();
        fm = new SimpleFragmentManager(activity);
        fm.restoreState(fmState);
        cm = new SimpleFragmentContainer(fm, null);
        cm.restoreState(cmState);
        cm.setView(rootView);

        assertThat(cm.find(KEY)).isEqualTo(fragment2);
    }

    @Test
    public void testPop() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment1 = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = cm.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        cm.pop();

        assertThat(cm.find(KEY)).isSameAs(fragment1);
    }

    @Test
    public void testPopConfigurationChange() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment1 = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = cm.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        cm.clearView();
        fm.clearConfigurationState();
        fm.restoreConfigurationState(activity);
        cm.setView(rootView);
        cm.pop();

        assertThat(cm.find(KEY)).isSameAs(fragment1);
    }

    @Test
    public void testPopSaveState() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment1 = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = cm.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        Parcelable fmState = fm.saveState();
        Parcelable cmState = cm.saveState();
        fm.clearConfigurationState();
        cm.clearView();
        fm = new SimpleFragmentManager(activity);
        fm.restoreState(fmState);
        cm = new SimpleFragmentContainer(fm, null);
        cm.restoreState(cmState);
        cm.setView(rootView);
        cm.pop();

        assertThat(cm.find(KEY)).isEqualTo(fragment1);
    }

    @Test
    public void testGetActivity() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        assertThat(fragment.getActivity()).isSameAs(activity);
    }

    @Test
    public void testParentFragment() {
        SimpleFragmentManager fm = new SimpleFragmentManager(activity);
        SimpleFragmentContainer cm = new SimpleFragmentContainer(fm, null);
        cm.setView(rootView);
        TestSimpleFragment fragment = cm.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment nestedFragment = fragment.getSimpleFragmentContainer().add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        assertThat(nestedFragment.getParentFragment()).isSameAs(fragment);
    }
}

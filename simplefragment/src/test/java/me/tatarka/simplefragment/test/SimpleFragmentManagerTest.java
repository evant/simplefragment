package me.tatarka.simplefragment.test;

import android.app.Activity;
import android.os.Parcelable;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.SimpleFragmentStateManager;
import me.tatarka.simplefragment.key.LayoutKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Created by evan on 3/21/15.
 */
@RunWith(CustomRobolectricRunner.class) // Robolectric needed to not crash on Bundle usage.
public class SimpleFragmentManagerTest {
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
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testAddBeforeSetRootView() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.setView(rootView);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testAddConfigurationChange() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.clearView();
        stateManager.clearConfigurationState();
        fragment.wasOnCreateViewCalled = false;
        stateManager.restoreConfigurationState(activity);
        manager.setView(rootView);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testAddSaveState() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        Parcelable stateManagerState = stateManager.saveState();
        Parcelable managerState = manager.saveState();
        stateManager = new SimpleFragmentStateManager(activity);
        manager = new SimpleFragmentManager(stateManager, null);
        stateManager.restoreState(stateManagerState);
        manager.restoreState(managerState);

        assertThat(manager.find(KEY)).isEqualTo(fragment);
    }

    @Test
    public void testRemove() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.remove(fragment);

        assertThat(stateManager.getFragments()).isEmpty();
    }

    @Test
    public void testPush() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);

        assertThat(manager.find(KEY)).isSameAs(fragment2);
    }

    @Test
    public void testPushConfigurationChange() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.clearView();
        stateManager.clearConfigurationState();
        stateManager.restoreConfigurationState(activity);
        manager.setView(rootView);

        assertThat(manager.find(KEY)).isSameAs(fragment2);
    }

    @Test
    public void testPushAfterConfigurationChange() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.clearView();
        ;
        stateManager.clearConfigurationState();
        stateManager.restoreConfigurationState(activity);
        manager.setView(rootView);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.clearView();
        stateManager.clearConfigurationState();
        stateManager.restoreConfigurationState(activity);
        manager.setView(rootView);

        assertThat(manager.find(KEY)).isSameAs(fragment2);
    }

    @Test
    public void testPushSaveState() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        Parcelable stateManagerState = stateManager.saveState();
        Parcelable managerState = manager.saveState();
        stateManager.clearConfigurationState();
        manager.clearView();
        stateManager = new SimpleFragmentStateManager(activity);
        stateManager.restoreState(stateManagerState);
        manager = new SimpleFragmentManager(stateManager, null);
        manager.restoreState(managerState);
        manager.setView(rootView);

        assertThat(manager.find(KEY)).isEqualTo(fragment2);
    }

    @Test
    public void testPop() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.pop();

        assertThat(manager.find(KEY)).isSameAs(fragment1);
    }

    @Test
    public void testPopConfigurationChange() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        manager.clearView();
        stateManager.clearConfigurationState();
        stateManager.restoreConfigurationState(activity);
        manager.setView(rootView);
        manager.pop();

        assertThat(manager.find(KEY)).isSameAs(fragment1);
    }

    @Test
    public void testPopSaveState() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment1 = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment fragment2 = manager.push(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        Parcelable stateManagerState = stateManager.saveState();
        Parcelable managerState = manager.saveState();
        stateManager.clearConfigurationState();
        manager.clearView();
        stateManager = new SimpleFragmentStateManager(activity);
        stateManager.restoreState(stateManagerState);
        manager = new SimpleFragmentManager(stateManager, null);
        manager.restoreState(managerState);
        manager.setView(rootView);
        manager.pop();

        assertThat(manager.find(KEY)).isEqualTo(fragment1);
    }

    @Test
    public void testGetActivity() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        assertThat(fragment.getActivity()).isSameAs(activity);
    }

    @Test
    public void testParentFragment() {
        SimpleFragmentStateManager stateManager = new SimpleFragmentStateManager(activity);
        SimpleFragmentManager manager = new SimpleFragmentManager(stateManager, null);
        manager.setView(rootView);
        TestSimpleFragment fragment = manager.add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        TestSimpleFragment nestedFragment = fragment.getSimpleFragmentManager().add(SimpleFragmentIntent.of(TestSimpleFragment.class), KEY);
        assertThat(nestedFragment.getParentFragment()).isSameAs(fragment);
    }
}

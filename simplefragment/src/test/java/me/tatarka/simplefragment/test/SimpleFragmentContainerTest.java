package me.tatarka.simplefragment.test;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
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
import static org.mockito.Matchers.any;
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
    LayoutInflater layoutInflater;
    @Mock
    ViewGroup rootView;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(layoutInflater.cloneInContext(any(Context.class))).thenReturn(layoutInflater);
        when(rootView.findViewById(anyInt())).thenReturn(rootView);
    }

    @Test
    public void testAddAfterSetRootView() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        container.setRootView(rootView, layoutInflater);
        TestSimpleFragment fragment = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);

        assertThat(fragment.wasOnCreateViewHolderCalled).isTrue();
    }

    @Test
    public void testAddBeforeSetRootView() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        TestSimpleFragment fragment = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        container.setRootView(rootView, layoutInflater);

        assertThat(fragment.wasOnCreateViewHolderCalled).isTrue();
    }

    @Test
    public void testAddConfigurationChange() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        container.setRootView(rootView, layoutInflater);
        TestSimpleFragment fragment = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        container.clearRootView();
        fm.clearConfigurationState();
        fragment.wasOnCreateViewHolderCalled = false;
        fm.restoreConfigurationState(context);
        container.setRootView(rootView, layoutInflater);

        assertThat(fragment.wasOnCreateViewHolderCalled).isTrue();
    }

    @Test
    public void testAddSaveState() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        container.setRootView(rootView, layoutInflater);
        TestSimpleFragment fragment = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        Parcelable managerState = fm.saveState();
        Parcelable containerState = container.saveState();
        fm = new SimpleFragmentManager(context);
        container = new SimpleFragmentContainer(fm, null);
        fm.restoreState(managerState);
        container.restoreState(containerState);

        assertThat(container.find(0)).isEqualTo(fragment);
    }

    @Test
    public void testRemove() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        container.setRootView(rootView, layoutInflater);
        TestSimpleFragment fragment = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        container.remove(fragment);

        assertThat(fm.getFragments()).isEmpty();
    }

    @Test
    public void testPush() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        container.setRootView(rootView, layoutInflater);
        TestSimpleFragment fragment1 = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        TestSimpleFragment fragment2 = container.push(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);

        assertThat(container.find(0)).isSameAs(fragment2);
    }
    
    @Test
    public void testPushConfigurationChange() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        container.setRootView(rootView, layoutInflater);
        TestSimpleFragment fragment1 = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        TestSimpleFragment fragment2 = container.push(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        container.clearRootView();
        fm.clearConfigurationState();
        fm.restoreConfigurationState(context);
        container.setRootView(rootView, layoutInflater);

        assertThat(container.find(0)).isSameAs(fragment2);
    }
    
    @Test
    public void testPushAfterConfigurationChange() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        container.setRootView(rootView, layoutInflater);
        TestSimpleFragment fragment1 = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        container.clearRootView();
        fm.clearConfigurationState();
        fm.restoreConfigurationState(context);
        container.setRootView(rootView, layoutInflater);
        TestSimpleFragment fragment2 = container.push(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        container.clearRootView();
        fm.clearConfigurationState();
        fm.restoreConfigurationState(context);
        container.setRootView(rootView, layoutInflater);
        
        assertThat(container.find(0)).isSameAs(fragment2);
    }

    @Test
    public void testPushSaveState() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        container.setRootView(rootView, layoutInflater);
        TestSimpleFragment fragment1 = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        TestSimpleFragment fragment2 = container.push(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        Parcelable managerState = fm.saveState();
        Parcelable containerState = container.saveState();
        fm = new SimpleFragmentManager(context);
        container = new SimpleFragmentContainer(fm, null);
        fm.restoreState(managerState);
        container.restoreState(containerState);

        assertThat(container.find(0)).isEqualTo(fragment2);
    }

    @Test
    public void testPop() {
        SimpleFragmentManager fm = new SimpleFragmentManager(context);
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        container.setRootView(rootView, layoutInflater);
        TestSimpleFragment fragment1 = container.add(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        TestSimpleFragment fragment2 = container.push(new SimpleFragmentIntent<>(TestSimpleFragment.class), 0);
        container.pop();

        assertThat(container.find(0)).isSameAs(fragment1);
    }
}

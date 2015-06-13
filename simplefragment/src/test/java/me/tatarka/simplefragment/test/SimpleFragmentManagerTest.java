package me.tatarka.simplefragment.test;

import android.app.Activity;
import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import me.tatarka.simplefragment.SimpleFragment;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.SimpleFragmentManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by evan on 3/6/15.
 */
@RunWith(CustomRobolectricRunner.class) // Robolectric needed to not crash on Bundle usage.
public class SimpleFragmentManagerTest {
    @Mock
    Activity activity;
    @Mock
    LayoutInflater layoutInflater;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(layoutInflater.cloneInContext(any(Context.class))).thenReturn(layoutInflater);
    }

    @Test
    public void testCreateInstance() {
        SimpleFragmentManager manager = new SimpleFragmentManager(activity);
        SimpleFragmentIntent intent = SimpleFragmentIntent.of(TestSimpleFragment.class);
        SimpleFragment fragment = manager.create(intent, new TestKey());

        assertThat(fragment).isInstanceOf(TestSimpleFragment.class);
    }

    @Test
    public void testOnCreateCalled() {
        SimpleFragmentManager manager = new SimpleFragmentManager(activity);
        SimpleFragmentIntent<TestSimpleFragment> intent = SimpleFragmentIntent.of(TestSimpleFragment.class);
        TestSimpleFragment fragment = manager.create(intent, new TestKey());

        assertThat(fragment.wasOnCreateCalled).isTrue();
    }

    @Test
    public void testFind() {
        SimpleFragmentManager manager = new SimpleFragmentManager(activity);
        SimpleFragmentIntent intent = SimpleFragmentIntent.of(TestSimpleFragment.class);
        SimpleFragment fragment = manager.create(intent, new TestKey());

        assertThat(manager.find(new TestKey())).isSameAs(fragment);
    }

    @Test
    public void testDestroy() {
        SimpleFragmentManager manager = new SimpleFragmentManager(activity);
        SimpleFragmentIntent intent = SimpleFragmentIntent.of(TestSimpleFragment.class);
        SimpleFragment fragment = manager.create(intent, new TestKey());
        manager.destroy(fragment);

        assertThat(manager.getFragments()).doesNotContain(fragment);
    }

    @Test
    public void testAttachFragment() {
        FrameLayout view = mock(FrameLayout.class);

        SimpleFragmentManager manager = new SimpleFragmentManager(activity);
        SimpleFragmentIntent<TestSimpleFragment> intent = SimpleFragmentIntent.of(TestSimpleFragment.class);
        TestSimpleFragment fragment = manager.create(intent, new TestKey());
        manager.createView(fragment, layoutInflater, view);

        assertThat(fragment.wasOnCreateViewCalled).isTrue();
    }

    @Test
    public void testDetachFragment() {
        FrameLayout view = mock(FrameLayout.class);

        SimpleFragmentManager manager = new SimpleFragmentManager(activity);
        SimpleFragmentIntent<TestSimpleFragment> intent = SimpleFragmentIntent.of(TestSimpleFragment.class);
        TestSimpleFragment fragment = manager.create(intent, new TestKey());
        manager.createView(fragment, layoutInflater, view);
        manager.destroyView(fragment);

        assertThat(fragment.getView()).isNull();
    }

    @Test
    public void testConfigurationChange() {
        SimpleFragmentManager manager = new SimpleFragmentManager(activity);
        SimpleFragmentIntent<TestSimpleFragment> intent = SimpleFragmentIntent.of(TestSimpleFragment.class);
        SimpleFragment fragment = manager.create(intent, new TestKey());
        manager.clearConfigurationState();
        manager.restoreConfigurationState(activity);

        assertThat(manager.find(new TestKey())).isSameAs(fragment);
    }

    @Test
    public void testSaveState() {
        SimpleFragmentManager manager = new SimpleFragmentManager(activity);
        SimpleFragmentIntent<TestSimpleFragment> intent = SimpleFragmentIntent.of(TestSimpleFragment.class);
        TestSimpleFragment fragment = manager.create(intent, new TestKey());
        manager.saveState();

        assertThat(fragment.wasOnSaveCalled).isTrue();
    }

    @Test
    public void testRestoreState() {
        SimpleFragmentManager manager = new SimpleFragmentManager(activity);
        SimpleFragmentIntent intent = SimpleFragmentIntent.of(TestSimpleFragment.class);
        manager.create(intent, new TestKey());
        Parcelable state = manager.saveState();
        manager = new SimpleFragmentManager(activity);
        manager.restoreState(state);

        assertThat(manager.getFragments()).hasOnlyElementsOfType(TestSimpleFragment.class);
    }
}

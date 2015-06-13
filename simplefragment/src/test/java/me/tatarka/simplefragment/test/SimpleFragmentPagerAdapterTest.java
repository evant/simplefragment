package me.tatarka.simplefragment.test;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.widget.SimpleFragmentPagerAdapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests them implementation of {@code SimpleFragmentPagerAdapter}. In order not to rely on
 * ViewPager, these tests assume that certain methods will be called on the adapter in specific
 * cases.
 */
@SuppressWarnings("ConstantConditions")
@RunWith(CustomRobolectricRunner.class) // Robolectric needed to not crash on Bundle usage.
public class SimpleFragmentPagerAdapterTest {
    @Mock
    Context context;
    @Mock
    LayoutInflater layoutInflater;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(layoutInflater.cloneInContext(any(Context.class))).thenReturn(layoutInflater);
    }

    @Test
    public void createFragmentPage() {
        final SimpleFragmentManager manager = new SimpleFragmentManager(context);
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(manager, layoutInflater) {
            @Override
            public SimpleFragmentIntent<?> getItem(int position) {
                return SimpleFragmentIntent.of(TestSimpleFragment.class).putExtra("test", 1);
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
        ViewGroup parent = mock(ViewGroup.class);
        adapter.instantiateItem(parent, 0);

        assertThat(adapter.getFragmentForPosition(0).getIntent().getIntExtra("test", 0)).isEqualTo(1);
    }

    @Test
    public void recreateFragmentPageOnConfigurationChange() {
        final SimpleFragmentManager manager = new SimpleFragmentManager(context);
        PagerAdapter adapter = new SimpleFragmentPagerAdapter(manager, layoutInflater) {
            @Override
            public SimpleFragmentIntent<?> getItem(int position) {
                return SimpleFragmentIntent.of(TestSimpleFragment.class);
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
        ViewGroup parent = mock(ViewGroup.class);
        Object item = adapter.instantiateItem(parent, 0);
        Parcelable adapterState = adapter.saveState();
        manager.clearConfigurationState();
        manager.restoreConfigurationState(context);
        PagerAdapter newAdapter = new SimpleFragmentPagerAdapter(manager, layoutInflater) {
            @Override
            public SimpleFragmentIntent<?> getItem(int position) {
                return SimpleFragmentIntent.of(TestSimpleFragment.class);
            }

            @Override
            public int getCount() {
                return 1;
            }
        };
        newAdapter.restoreState(adapterState, getClass().getClassLoader());
        ViewGroup newParent = mock(ViewGroup.class);
        Object newItem = adapter.instantiateItem(newParent, 0);

        assertThat(newItem).isSameAs(item);
    }

    @Test
    public void destroyFragmentWhenNotifyRemoved() {
        final SimpleFragmentManager manager = new SimpleFragmentManager(context);
        final List<SimpleFragmentIntent> fragments = new ArrayList<>();
        fragments.add(SimpleFragmentIntent.of(TestSimpleFragment.class));
        PagerAdapter adapter = new SimpleFragmentPagerAdapter(manager, layoutInflater) {
            @Override
            public SimpleFragmentIntent<?> getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        ViewGroup parent = mock(ViewGroup.class);
        adapter.instantiateItem(parent, 0);
        fragments.clear();
        adapter.notifyDataSetChanged();

        assertThat(manager.getFragments()).isEmpty();
    }

    @Test
    public void addNewFragmentOnNotifyAdd() {
        final SimpleFragmentManager manager = new SimpleFragmentManager(context);
        final List<SimpleFragmentIntent> fragments = new ArrayList<>();
        fragments.add(SimpleFragmentIntent.of(TestSimpleFragment.class).putExtra("test", 1));
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(manager, layoutInflater) {
            @Override
            public SimpleFragmentIntent<?> getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        ViewGroup parent = mock(ViewGroup.class);
        adapter.instantiateItem(parent, 0);
        fragments.add(SimpleFragmentIntent.of(TestSimpleFragment.class).putExtra("test", 2));
        adapter.notifyDataSetChanged();
        adapter.instantiateItem(parent, 1);

        assertThat(adapter.getFragmentForPosition(0).getIntent().getIntExtra("test", 0)).isEqualTo(1);
        assertThat(adapter.getFragmentForPosition(1).getIntent().getIntExtra("test", 0)).isEqualTo(2);
    }

    @Test
    public void tracksChangingPositionsOfFragmentIntents() {
        final SimpleFragmentManager manager = new SimpleFragmentManager(context);
        final List<SimpleFragmentIntent> fragments = new ArrayList<>();
        fragments.add(SimpleFragmentIntent.of(TestSimpleFragment.class).putExtra("test", 1));
        fragments.add(SimpleFragmentIntent.of(TestSimpleFragment.class).putExtra("test", 2));
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(manager, layoutInflater) {
            @Override
            public SimpleFragmentIntent<?> getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        ViewGroup parent = mock(ViewGroup.class);
        adapter.instantiateItem(parent, 0);
        adapter.instantiateItem(parent, 1);
        fragments.add(fragments.remove(0)); // Swap fragment intents
        adapter.notifyDataSetChanged();

        assertThat(adapter.getFragmentForPosition(0).getIntent().getIntExtra("test", 0)).isEqualTo(2);
        assertThat(adapter.getFragmentForPosition(1).getIntent().getIntExtra("test", 0)).isEqualTo(1);
    }

    @Test
    public void moveAndReplaceFragmentAtPosition() {
        final SimpleFragmentManager manager = new SimpleFragmentManager(context);
        final List<SimpleFragmentIntent> fragments = new ArrayList<>();
        fragments.add(SimpleFragmentIntent.of(TestSimpleFragment.class).putExtra("test", 1));
        fragments.add(SimpleFragmentIntent.of(TestSimpleFragment.class).putExtra("test", 2));
        SimpleFragmentPagerAdapter adapter = new SimpleFragmentPagerAdapter(manager, layoutInflater) {
            @Override
            public SimpleFragmentIntent<?> getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return fragments.size();
            }
        };
        ViewGroup parent = mock(ViewGroup.class);
        adapter.instantiateItem(parent, 0);
        adapter.instantiateItem(parent, 1);
        fragments.remove(0);
        fragments.add(SimpleFragmentIntent.of(TestSimpleFragment.class).putExtra("test", 3));
        adapter.notifyDataSetChanged();
        adapter.instantiateItem(parent, 1);

        assertThat(adapter.getFragmentForPosition(0).getIntent().getIntExtra("test", 0)).isEqualTo(2);
        assertThat(adapter.getFragmentForPosition(1).getIntent().getIntExtra("test", 0)).isEqualTo(3);
    }
}

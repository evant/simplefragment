package me.tatarka.simplefragment.test;

import android.test.AndroidTestCase;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import me.tatarka.simplefragment.SimpleFragmentContainer;
import me.tatarka.simplefragment.SimpleFragmentViewInflater;
import me.tatarka.simplefragment.SimpleFragmentManager;
import me.tatarka.simplefragment.key.LayoutKey;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by evan on 3/9/15.
 */
public class SimpleFragmentLayoutTest extends AndroidTestCase {
    LayoutInflater inflater;
    ViewGroup rootView;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        inflater = LayoutInflater.from(getContext()).cloneInContext(getContext());
        rootView = new FrameLayout(getContext());
    }

    public void testInflate() {
        SimpleFragmentManager fm = new SimpleFragmentManager(getContext());
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        inflater.setFactory(new SimpleFragmentViewInflater(container));
        inflater.inflate(R.layout.fragment_layout_test, rootView);
        container.setRootView(rootView, inflater);

        assertThat(fm.find(new LayoutKey(R.id.test_fragment))).isInstanceOf(TestSimpleFragment.class);
    }

    public void testInflateNested() {
        SimpleFragmentManager fm = new SimpleFragmentManager(getContext());
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        inflater.setFactory(new SimpleFragmentViewInflater(container));
        inflater.inflate(R.layout.fragment_layout_test_nested, rootView);
        container.setRootView(rootView, inflater);

        assertThat(fm.find(new LayoutKey(R.id.test_fragment))).isInstanceOf(TestNestedSimpleFragment.class);
        assertThat(fm.find(new LayoutKey(new LayoutKey(R.id.test_fragment), R.id.test_fragment))).isInstanceOf(TestSimpleFragment.class);
    }

    public void testInflateMultipleTimes() {
        SimpleFragmentManager fm = new SimpleFragmentManager(getContext());
        SimpleFragmentContainer container = new SimpleFragmentContainer(fm, null);
        inflater.setFactory(new SimpleFragmentViewInflater(container));
        inflater.inflate(R.layout.fragment_layout_test, rootView);
        container.setRootView(rootView, inflater);
        fm.destroyView(fm.find(new LayoutKey(R.id.test_fragment)), rootView);
        inflater.inflate(R.layout.fragment_layout_test, rootView);

        assertThat(fm.find(new LayoutKey(R.id.test_fragment))).isInstanceOf(TestSimpleFragment.class);
    }
}

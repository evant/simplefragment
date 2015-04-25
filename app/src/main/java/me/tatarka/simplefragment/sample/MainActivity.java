package me.tatarka.simplefragment.sample;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import me.tatarka.simplefragment.SimpleFragmentAppCompatActivity;
import me.tatarka.simplefragment.SimpleFragmentIntent;
import me.tatarka.simplefragment.widget.SimpleFragmentPagerAdapter;

public class MainActivity extends SimpleFragmentAppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(new Adapter());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class Adapter extends SimpleFragmentPagerAdapter {
        public Adapter() {
            super(MainActivity.this, getLayoutInflater());
        }

        @Override
        public SimpleFragmentIntent getItem(int position) {
            switch (position) {
                case 0:
                    return new SimpleFragmentIntent<>(SimpleFragmentFromLayout.class);
                case 1:
                    return new SimpleFragmentIntent<>(SimpleFragmentWithBackStack.class);
                case 2:
                    return new SimpleFragmentIntent<>(SimpleFragmentDialogs.class);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }
}

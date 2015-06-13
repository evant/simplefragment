package me.tatarka.simplefragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatCallback;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import me.tatarka.simplefragment.activity.SimpleFragmentDelegate;

/**
 * Created by evan on 2/2/15.
 */
public class SimpleFragmentAppCompatActivity extends Activity implements SimpleFragmentManagerProvider, AppCompatCallback, SimpleFragmentDelegate.Methods {
    private AppCompatDelegate appCompatDelegate;
    private SimpleFragmentDelegate simpleFragmentDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSimpleFragmentDelegate().installViewFactory(new DelegateFactory(getAppCompatDelegate()));
        super.onCreate(savedInstanceState);
        getSimpleFragmentDelegate().onCreate(savedInstanceState);
        getAppCompatDelegate().onCreate(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        getAppCompatDelegate().onPostCreate(savedInstanceState);
    }

    /**
     * Support library version of {@link android.app.Activity#getActionBar}.
     * <p/>
     * <p>Retrieve a reference to this activity's ActionBar.
     *
     * @return The Activity's ActionBar, or null if it does not have one.
     */
    @Nullable
    public ActionBar getSupportActionBar() {
        return getAppCompatDelegate().getSupportActionBar();
    }

    /**
     * Set a {@link android.widget.Toolbar Toolbar} to act as the {@link
     * android.support.v7.app.ActionBar} for this Activity window. <p/> <p>When set to a non-null
     * value the {@link #getActionBar()} method will return an {@link android.support.v7.app.ActionBar}
     * object that can be used to control the given toolbar as if it were a traditional window decor
     * action bar. The toolbar's menu will be populated with the Activity's options menu and the
     * navigation button will be wired through the standard {@link android.R.id#home home} menu
     * select action.</p> <p/> <p>In order to use a Toolbar within the Activity's window content the
     * application must not request the window feature {@link android.view.Window#FEATURE_ACTION_BAR
     * FEATURE_ACTION_BAR}.</p>
     *
     * @param toolbar Toolbar to set as the Activity's action bar
     */
    public void setSupportActionBar(@Nullable Toolbar toolbar) {
        getAppCompatDelegate().setSupportActionBar(toolbar);
    }

    @Override
    public MenuInflater getMenuInflater() {
        return getAppCompatDelegate().getMenuInflater();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        getAppCompatDelegate().setContentView(layoutResID);
        getSimpleFragmentDelegate().onSetContentView();
    }

    @Override
    public void setContentView(View view) {
        getAppCompatDelegate().setContentView(view);
        getSimpleFragmentDelegate().onSetContentView();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getAppCompatDelegate().setContentView(view, params);
        getSimpleFragmentDelegate().onSetContentView();
    }

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getAppCompatDelegate().addContentView(view, params);
        getSimpleFragmentDelegate().onSetContentView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        getAppCompatDelegate().onConfigurationChanged(newConfig);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getAppCompatDelegate().onStop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getAppCompatDelegate().onPostResume();
    }

    @Override
    public final boolean onMenuItemSelected(int featureId, android.view.MenuItem item) {
        if (super.onMenuItemSelected(featureId, item)) {
            return true;
        }

        final ActionBar ab = getSupportActionBar();
        if (item.getItemId() == android.R.id.home && ab != null &&
                (ab.getDisplayOptions() & ActionBar.DISPLAY_HOME_AS_UP) != 0) {
            return onSupportNavigateUp();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getSimpleFragmentDelegate().onDestroy();
        getAppCompatDelegate().onDestroy();
    }

    @Override
    protected void onTitleChanged(CharSequence title, int color) {
        super.onTitleChanged(title, color);
        getAppCompatDelegate().setTitle(title);
    }

    /**
     * Enable extended support library window features. <p> This is a convenience for calling {@link
     * android.view.Window#requestFeature getWindow().requestFeature()}. </p>
     *
     * @param featureId The desired feature as defined in {@link android.view.Window} or {@link
     *                  android.support.v4.view.WindowCompat}.
     * @return Returns true if the requested feature is supported and now enabled.
     * @see android.app.Activity#requestWindowFeature
     * @see android.view.Window#requestFeature
     */
    public boolean supportRequestWindowFeature(int featureId) {
        return getAppCompatDelegate().requestWindowFeature(featureId);
    }

    public void invalidateOptionsMenu() {
        getAppCompatDelegate().invalidateOptionsMenu();
    }

    /**
     * Notifies the Activity that a support action mode has been started. Activity subclasses
     * overriding this method should call the superclass implementation.
     *
     * @param mode The new action mode.
     */
    @Override
    public void onSupportActionModeStarted(ActionMode mode) {

    }

    /**
     * Notifies the activity that a support action mode has finished. Activity subclasses overriding
     * this method should call the superclass implementation.
     *
     * @param mode The action mode that just finished.
     */
    @Override
    public void onSupportActionModeFinished(ActionMode mode) {

    }

    @Override
    public ActionMode onWindowStartingSupportActionMode(ActionMode.Callback callback) {
        return null;
    }

    public ActionMode startSupportActionMode(ActionMode.Callback callback) {
        return getAppCompatDelegate().startSupportActionMode(callback);
    }

    /**
     * Support version of {@link #onCreateNavigateUpTaskStack(android.app.TaskStackBuilder)}. This
     * method will be called on all platform versions. <p/> Define the synthetic task stack that
     * will be generated during Up navigation from a different task. <p/> <p>The default
     * implementation of this method adds the parent chain of this activity as specified in the
     * manifest to the supplied {@link android.support.v4.app.TaskStackBuilder}. Applications may
     * choose to override this method to construct the desired task stack in a different way.</p>
     * <p/> <p>This method will be invoked by the default implementation of {@link #onNavigateUp()}
     * if {@link #shouldUpRecreateTask(android.content.Intent)} returns true when supplied with the
     * intent returned by {@link #getParentActivityIntent()}.</p> <p/> <p>Applications that wish to
     * supply extra Intent parameters to the parent stack defined by the manifest should override
     * {@link #onPrepareSupportNavigateUpTaskStack(android.support.v4.app.TaskStackBuilder)}.</p>
     *
     * @param builder An empty TaskStackBuilder - the application should add intents representing
     *                the desired task stack
     */
    public void onCreateSupportNavigateUpTaskStack(TaskStackBuilder builder) {
        builder.addParentStack(this);
    }

    /**
     * Support version of {@link #onPrepareNavigateUpTaskStack(android.app.TaskStackBuilder)}. This
     * method will be called on all platform versions. <p/> Prepare the synthetic task stack that
     * will be generated during Up navigation from a different task. <p/> <p>This method receives
     * the {@link android.support.v4.app.TaskStackBuilder} with the constructed series of Intents as
     * generated by {@link #onCreateSupportNavigateUpTaskStack(android.support.v4.app.TaskStackBuilder)}.
     * If any extra data should be added to these intents before launching the new task, the
     * application should override this method and add that data here.</p>
     *
     * @param builder A TaskStackBuilder that has been populated with Intents by
     *                onCreateNavigateUpTaskStack.
     */
    public void onPrepareSupportNavigateUpTaskStack(TaskStackBuilder builder) {
    }

    /**
     * This method is called whenever the user chooses to navigate Up within your application's
     * activity hierarchy from the action bar. <p/> <p>If a parent was specified in the manifest for
     * this activity or an activity-alias to it, default Up navigation will be handled
     * automatically. See {@link #getSupportParentActivityIntent()} for how to specify the parent.
     * If any activity along the parent chain requires extra Intent arguments, the Activity subclass
     * should override the method {@link #onPrepareSupportNavigateUpTaskStack(android.support.v4.app.TaskStackBuilder)}
     * to supply those arguments.</p> <p/> <p>See <a href="{@docRoot}guide/topics/fundamentals/tasks-and-back-stack.html">Tasks
     * and Back Stack</a> from the developer guide and <a href="{@docRoot}design/patterns/navigation.html">Navigation</a>
     * from the design guide for more information about navigating within your app.</p> <p/> <p>See
     * the {@link android.support.v4.app.TaskStackBuilder} class and the Activity methods {@link
     * #getSupportParentActivityIntent()}, {@link #supportShouldUpRecreateTask(android.content.Intent)},
     * and {@link #supportNavigateUpTo(android.content.Intent)} for help implementing custom Up
     * navigation.</p>
     *
     * @return true if Up navigation completed successfully and this Activity was finished, false
     * otherwise.
     */
    public boolean onSupportNavigateUp() {
        Intent upIntent = getSupportParentActivityIntent();

        if (upIntent != null) {
            if (supportShouldUpRecreateTask(upIntent)) {
                TaskStackBuilder b = TaskStackBuilder.create(this);
                onCreateSupportNavigateUpTaskStack(b);
                onPrepareSupportNavigateUpTaskStack(b);
                b.startActivities();

                try {
                    ActivityCompat.finishAffinity(this);
                } catch (IllegalStateException e) {
                    // This can only happen on 4.1+, when we don't have a parent or a result set.
                    // In that case we should just finish().
                    finish();
                }
            } else {
                // This activity is part of the application's task, so simply
                // navigate up to the hierarchical parent activity.
                supportNavigateUpTo(upIntent);
            }
            return true;
        }
        return false;
    }

    /**
     * Obtain an {@link android.content.Intent} that will launch an explicit target activity
     * specified by sourceActivity's {@link android.support.v4.app.NavUtils#PARENT_ACTIVITY}
     * &lt;meta-data&gt; element in the application's manifest. If the device is running Jellybean
     * or newer, the android:parentActivityName attribute will be preferred if it is present.
     *
     * @return a new Intent targeting the defined parent activity of sourceActivity
     */
    @Nullable
    public Intent getSupportParentActivityIntent() {
        return NavUtils.getParentActivityIntent(this);
    }

    /**
     * Returns true if sourceActivity should recreate the task when navigating 'up' by using
     * targetIntent. <p/> <p>If this method returns false the app can trivially call {@link
     * #supportNavigateUpTo(android.content.Intent)} using the same parameters to correctly perform
     * up navigation. If this method returns false, the app should synthesize a new task stack by
     * using {@link android.support.v4.app.TaskStackBuilder} or another similar mechanism to perform
     * up navigation.</p>
     *
     * @param targetIntent An intent representing the target destination for up navigation
     * @return true if navigating up should recreate a new task stack, false if the same task should
     * be used for the destination
     */
    public boolean supportShouldUpRecreateTask(Intent targetIntent) {
        return NavUtils.shouldUpRecreateTask(this, targetIntent);
    }

    /**
     * Navigate from sourceActivity to the activity specified by upIntent, finishing sourceActivity
     * in the process. upIntent will have the flag {@link android.content.Intent#FLAG_ACTIVITY_CLEAR_TOP}
     * set by this method, along with any others required for proper up navigation as outlined in
     * the Android Design Guide. <p/> <p>This method should be used when performing up navigation
     * from within the same task as the destination. If up navigation should cross tasks in some
     * cases, see {@link #supportShouldUpRecreateTask(android.content.Intent)}.</p>
     *
     * @param upIntent An intent representing the target destination for up navigation
     */
    public void supportNavigateUpTo(Intent upIntent) {
        NavUtils.navigateUpTo(this, upIntent);
    }

    @Override
    public void onContentChanged() {
        // Call onSupportContentChanged() for legacy reasons
        onSupportContentChanged();
    }

    /**
     * @deprecated Use {@link #onContentChanged()} instead.
     */
    @Deprecated
    public void onSupportContentChanged() {
    }

    @Nullable
    public ActionBarDrawerToggle.Delegate getDrawerToggleDelegate() {
        return getAppCompatDelegate().getDrawerToggleDelegate();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSimpleFragmentDelegate().onSaveInstanceState(outState);
    }

    @Override
    public final Object onRetainNonConfigurationInstance() {
        return getSimpleFragmentDelegate().onRetainNonConfigurationInstance(onRetainCustomNonConfigurationInstance());
    }

    public Object onRetainCustomNonConfigurationInstance() {
        return null;
    }

    public Object getLastCustomNonCofigurationInstance() {
        return getSimpleFragmentDelegate().getLastCustomNonConfigurationInstance(getLastNonConfigurationInstance());
    }

    @Override
    public void onBackPressed() {
        if (!getSimpleFragmentDelegate().onBackPress()) {
            super.onBackPressed();
        }
    }

    @Override
    public SimpleFragmentManager getSimpleFragmentManager() {
        return getSimpleFragmentDelegate().getSimpleFragmentManager();
    }

    /**
     * @return The {@link AppCompatDelegate} being used by this Activity.
     */
    public AppCompatDelegate getAppCompatDelegate() {
        if (appCompatDelegate == null) {
            appCompatDelegate = AppCompatDelegate.create(this, this);
        }
        return appCompatDelegate;
    }

    public SimpleFragmentDelegate getSimpleFragmentDelegate() {
        if (simpleFragmentDelegate == null) {
            simpleFragmentDelegate = SimpleFragmentDelegate.create(this);
        }
        return simpleFragmentDelegate;
    }

    @Override
    public void startActivityFromFragment(SimpleFragment fragment, Intent intent, int requestCode, @Nullable Bundle options) {
        int maskedRequestCode = getSimpleFragmentDelegate().getMaskedRequestCode(fragment, requestCode);
        if (Build.VERSION.SDK_INT >= 16) {
            super.startActivityForResult(intent, maskedRequestCode, options);
        } else {
            super.startActivityForResult(intent, maskedRequestCode);
        }
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        getSimpleFragmentDelegate().startActivityForResult(intent, requestCode, null);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        getSimpleFragmentDelegate().startActivityForResult(intent, requestCode, options);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getSimpleFragmentDelegate().onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Delegate to AppCompat.
     */
    private static class DelegateFactory implements LayoutInflaterFactory {
        AppCompatDelegate delegate;

        public DelegateFactory(AppCompatDelegate delegate) {
            this.delegate = delegate;
        }

        @Override
        public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
            return delegate.createView(parent, name, context, attrs);
        }
    }
}

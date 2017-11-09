package com.playposse.thomas.lindenmayer.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.playposse.thomas.lindenmayer.CommonMenuActions;
import com.playposse.thomas.lindenmayer.LindenmayerApplication;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

/**
 * An abstract activity that extends {@link AppCompatActivity} to add analytics tracking.
 */
public abstract class ParentActivity<F extends Fragment> extends AppCompatActivity {

    private static final String MAIN_FRAGMENT_TAG = "mainFragment";

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.main_fragment_container) LinearLayout mainFragmentContainer;
    @BindView(R.id.navigation_view) NavigationView navigationView;
    @BindView(R.id.my_toolbar) Toolbar toolbar;

    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent);

        ButterKnife.bind(this);

        initNavigationDrawer();

        setSupportActionBar(toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Load the image with Glide, so that Glide resizes the image to the actual size to readuce
        // memory consumption.
        ImageView navigationHeaderImageView =
                navigationView.getHeaderView(0).findViewById(R.id.navigation_header_image_view);
        GlideApp.with(this)
                .load(R.drawable.navigation_banner)
                .into(navigationHeaderImageView);

        Fabric.with(this, new Crashlytics());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        LindenmayerApplication application = (LindenmayerApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName(getClass().getSimpleName());
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        tracker.enableExceptionReporting(true);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (drawerToggle != null) {
            drawerToggle.syncState();
        }
    }

    private void initNavigationDrawer() {
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        return onOptionsItemSelected(item);
                    }
                });
    }

    protected void addContentFragment(Fragment mainFragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (fragmentManager.findFragmentByTag(MAIN_FRAGMENT_TAG) == null) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.main_fragment_container, mainFragment, MAIN_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if ((drawerToggle != null) && drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                if (drawerLayout != null) {
                    drawerLayout.openDrawer(GravityCompat.START);
                } else {
                    finish();
                }
                return true;
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            case R.id.action_send_feedback:
                CommonMenuActions.sendFeedbackAction(this);
                return true;
            case R.id.action_video_tutorial:
                ActivityNavigator.startVideoTutorialActivity(this);
                return true;
            case R.id.action_turtle_tutorial:
                ActivityNavigator.startTurtleTrainingActivity(this);
                return true;
            case R.id.action_new_rule_set:
                ActivityNavigator.startNewRuleSetActivity(this);
                return true;
            case R.id.action_samples:
                ActivityNavigator.startSampleLibraryActivity(this);
                return true;
            case R.id.action_private_rule_sets:
                ActivityNavigator.startPrivateLibraryActivity(this);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("unchecked")
    protected F getContentFragment() {
        return (F) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
    }
}

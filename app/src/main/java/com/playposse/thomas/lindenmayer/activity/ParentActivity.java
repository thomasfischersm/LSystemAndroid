package com.playposse.thomas.lindenmayer.activity;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
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

import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.playposse.thomas.lindenmayer.CommonMenuActions;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.activity.common.ActivityNavigator;
import com.playposse.thomas.lindenmayer.glide.GlideApp;
import com.playposse.thomas.lindenmayer.util.MenuUtil;
import com.playposse.thomas.lindenmayer.util.SurveyUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * An abstract activity that extends {@link AppCompatActivity} to add analytics tracking.
 */
public abstract class ParentActivity<F extends Fragment> extends MinimumActivity {

    private static final String MAIN_FRAGMENT_TAG = "mainFragment";

    @BindView(R.id.drawer_layout) DrawerLayout drawerLayout;
    @BindView(R.id.main_fragment_container) LinearLayout mainFragmentContainer;
    @BindView(R.id.navigation_view) NavigationView navigationView;
    @BindView(R.id.my_toolbar) Toolbar toolbar;

    private ActionBarDrawerToggle drawerToggle;

    @SuppressLint("Range")
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
                .apply(RequestOptions.overrideOf(Target.SIZE_ORIGINAL))
                .into(navigationHeaderImageView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        // Only show logout option when logged in.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        boolean isLoggedIn = (user != null);
        MenuUtil.setMenuItemVisibility(menu, R.id.action_logout, isLoggedIn);

        return true;
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (drawerToggle != null) {
            drawerToggle.syncState();
        }

        // Try to show a snackbar with a survey prompt.
        F contentFragment = getContentFragment();
        if ((contentFragment != null) && (contentFragment.getView() != null)) {
            CoordinatorLayout coordinatorLayout =
                    contentFragment.getView().findViewById(R.id.coordinator_layout);
            SurveyUtil.showSurveyNudge(coordinatorLayout);
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
                ActivityNavigator.startHelpActivity(this);
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
                return true;
            case R.id.action_public_rule_sets:
                ActivityNavigator.startPublicLibraryActivity(this);
                return true;
            case R.id.action_logout:
                onLogoutClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onLogoutClicked() {
        FirebaseAuth.getInstance().signOut();

        // Remove the logout menu item.
        invalidateOptionsMenu();
    }

    @SuppressWarnings("unchecked")
    protected F getContentFragment() {
        return (F) getSupportFragmentManager().findFragmentByTag(MAIN_FRAGMENT_TAG);
    }
}

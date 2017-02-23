package com.playposse.thomas.lindenmayer;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * An abstract activity that extends {@link AppCompatActivity} to add analytics tracking.
 */
public abstract class ParentActivity extends AppCompatActivity {

    @Override
    protected void onResume() {
        super.onResume();

        LindenmayerApplication application = (LindenmayerApplication) getApplication();
        Tracker tracker = application.getDefaultTracker();
        tracker.setScreenName(getClass().getSimpleName());
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}

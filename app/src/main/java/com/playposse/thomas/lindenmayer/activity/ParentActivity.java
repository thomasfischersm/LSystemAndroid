package com.playposse.thomas.lindenmayer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.playposse.thomas.lindenmayer.LindenmayerApplication;

import io.fabric.sdk.android.Fabric;

/**
 * An abstract activity that extends {@link AppCompatActivity} to add analytics tracking.
 */
public abstract class ParentActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
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
}

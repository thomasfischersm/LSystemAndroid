package com.playposse.thomas.lindenmayer;

import android.app.Application;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerDatabaseHelper;
import com.playposse.thomas.lindenmayer.service.ImportRuleSetsService;

/**
 * An instance of {@link Application} that helps with Google analytics.
 */
public class LindenmayerApplication extends MultiDexApplication {

    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();

        // Reset the database for debug releases.
        if (BuildConfig.DEBUG) {
            getApplicationContext().deleteDatabase(LindenmayerDatabaseHelper.DB_NAME);
        }

        // Check if anything needs to be imported.
        startService(new Intent(getApplicationContext(), ImportRuleSetsService.class));

        // Leave this comment because we need to enable this frequently during development.
//        Benchmark.runBenchmark();
    }

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.global_tracker);
        }
        return tracker;
    }
}

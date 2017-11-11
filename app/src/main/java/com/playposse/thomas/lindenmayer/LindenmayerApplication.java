package com.playposse.thomas.lindenmayer;

import android.app.Application;
import android.content.Intent;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerDatabaseHelper;
import com.playposse.thomas.lindenmayer.service.ImportRuleSetsService;

/**
 * An instance of {@link Application} that helps with Google analytics.
 */
public class LindenmayerApplication extends Application {

    private Tracker tracker;

    @Override
    public void onCreate() {
        super.onCreate();

        // TODO: Remove this for the release
        if (BuildConfig.DEBUG) {
            getApplicationContext().deleteDatabase(LindenmayerDatabaseHelper.DB_NAME);
        }

        // Check if anything needs to be imported.
        startService(new Intent(getApplicationContext(), ImportRuleSetsService.class));

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

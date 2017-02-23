package com.playposse.thomas.lindenmayer;

import android.app.Application;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * A helper class to deal with Google Analytics.
 */
public abstract class AnalyticsUtil {

    private AnalyticsUtil() {}

    public static void sendEvent(Application application, String category, String action) {
        LindenmayerApplication lsystemApplication = (LindenmayerApplication) application;
        Tracker tracker = lsystemApplication.getDefaultTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .build());
    }
}

package com.playposse.thomas.lindenmayer.util;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.playposse.thomas.lindenmayer.LindenmayerApplication;

/**
 * A helper class to deal with Google Analytics.
 */
public final class AnalyticsUtil {

    public static final String START_TAKING_PHOTOS_ACTION = "startTakingPhotos";
    public static final String STOP_TAKING_PHOTOS_ACTION = "stopTakingPhotos";
    public static final String SET_INTERVAL_ACTION = "setInterval";
    public static final String TAKE_PHOTO_ACTION = "takePhoto";

    private static final String DEFAULT_CATEGORY = "Action";

    public enum AnalyticsCategory {
        sharePhoto,
        sharePhotoShoot,
        deleteAll,
        deleteUnselected,
        deletePhoto,
        selectPhoto,
        rotatePhoto,
        editPhoto,
        sendFeedback,
        fileIntegrityCheck,
        deleteDirectory,
    }

    private AnalyticsUtil() {}

    public static void sendEvent(Fragment fragment, String action) {
        sendEvent(fragment.getActivity().getApplication(), action);
    }

    public static void sendEvent(Application defaultApp, String action) {
        LindenmayerApplication app = (LindenmayerApplication) defaultApp;
        Tracker tracker = app.getDefaultTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(DEFAULT_CATEGORY)
                .setAction(action)
                .build());
    }

    public static void reportScreenName(Activity activity) {
        reportScreenName(activity, activity.getClass().getSimpleName());
    }

    public static void reportScreenName(Activity activity, String screenName) {
        LindenmayerApplication app = (LindenmayerApplication) activity.getApplication();
        Tracker tracker = app.getDefaultTracker();
        tracker.setScreenName(screenName);

        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);
        tracker.enableExceptionReporting(true);

        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    public static void reportEvent(
            Application defaultApp,
            AnalyticsCategory category,
            String action) {

        if (StringUtil.isEmpty(action)) {
            action = category.name();
        }

        LindenmayerApplication app = (LindenmayerApplication) defaultApp;
        Tracker tracker = app.getDefaultTracker();
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category.name())
                .setAction(action)
                .build());
    }
}

package com.playposse.thomas.lindenmayer.test;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import com.playposse.thomas.lindenmayer.data.AppPreferences;

/**
 * Customizes {@link ActivityTestRule} that resets the application data on startup.
 */
public class ActivityTestRuleWithReset<T extends Activity> extends ActivityTestRule<T> {

    private static final String LOG_CAT = ActivityTestRuleWithReset.class.getSimpleName();

    public ActivityTestRuleWithReset(Class activityClass) {
        super(activityClass);
    }

    public ActivityTestRuleWithReset(Class activityClass, boolean initialTouchMode) {
        super(activityClass, initialTouchMode);
    }

    public ActivityTestRuleWithReset(Class activityClass, boolean initialTouchMode, boolean launchActivity) {
        super(activityClass, initialTouchMode, launchActivity);
    }

    @Override
    protected void beforeActivityLaunched() {
        super.beforeActivityLaunched();
        resetAppData();
    }

    @Override
    protected void afterActivityFinished() {
        super.afterActivityFinished();
        resetAppData();
    }

    private void resetAppData() {
        // Ensure that no file with user defined rules is left over.
        Context context = InstrumentationRegistry.getTargetContext();
        context.deleteFile("userDefinedRuleSets.json");

        // Reset shared preferences.
        AppPreferences.reset(context);

        Log.i(LOG_CAT, "Preference after reset: " + AppPreferences.getShowTurtleTutorialDialog(context));
    }
}

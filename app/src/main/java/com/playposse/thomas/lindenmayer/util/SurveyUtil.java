package com.playposse.thomas.lindenmayer.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.data.AppPreferences;

/**
 * A utility that nudges the user with intermittent snack bars to fill out a survey about the app.
 */
public final class SurveyUtil {

    private static final String LOG_TAG = SurveyUtil.class.getSimpleName();

    private static final String SURVEY_URL = "https://docs.google.com/forms/d/e/1FAIpQLSfMJrEae9_scGNF1KCkt5B4vFhFlIeHvQj1DTONS0QWJMGJBw/viewform";

    private SurveyUtil() {
    }

    public static void showSurveyNudge(@Nullable CoordinatorLayout coordinatorLayout) {
        if (coordinatorLayout == null) {
            // The current activity doesn't have a CoordinatorLayout.
            return;
        }

        Context context = coordinatorLayout.getContext();

        if (AppPreferences.hasSurveyBeenClicked(context)) {
            // The survey has already been shown. Don't show it again!
            return;
        }

        int nudgeCount = AppPreferences.incrementSurveyNudgeCounter(context);
        Log.d(LOG_TAG, "showSurveyNudge: Survey nudge count is " + nudgeCount);

        if (!isPowerOfTwo(nudgeCount)) {
            // Only bug the user in these intervals: 2nd, 4th, 8th, 16th, 32nd... time of starting
            // an activity.
            return;
        }

        showSnackBar(coordinatorLayout);
    }

    private static void showSnackBar(final CoordinatorLayout coordinatorLayout) {
        Snackbar snackbar = Snackbar.make(
                coordinatorLayout,
                R.string.survey_snack_bar_prompt,
                Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(
                R.string.survey_snack_bar_action, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onSurveyClicked(coordinatorLayout.getContext());
                    }
                });
        snackbar.show();
    }

    private static void onSurveyClicked(Context context) {
        AppPreferences.setHasSurveyBeenClicked(context, true);

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(SURVEY_URL));
        context.startActivity(intent);
    }

    private static boolean isPowerOfTwo(int x) {
        return (x & (x - 1)) == 0;
    }
}

package com.playposse.thomas.lindenmayer.data;

import android.content.Context;

/**
 * Central registry for all shared preferences.
 */
public class AppPreferences {

    public static final String SHARED_PREFERENCES_NAME = "ourPreferences";

    private static final String SHOW_TURTLE_TUTORIAL_DIALOG = "showTurtleTutorialDialog";

    public static boolean getShowTurtleTutorialDialog(Context context) {
        return context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .getBoolean(SHOW_TURTLE_TUTORIAL_DIALOG, true);
    }

    public static void setShowTurtleTutorialDialog(Context context, boolean value) {
        context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(SHOW_TURTLE_TUTORIAL_DIALOG, value)
                .commit();
    }
}

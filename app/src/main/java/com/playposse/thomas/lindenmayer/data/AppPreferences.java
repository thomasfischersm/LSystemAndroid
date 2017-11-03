package com.playposse.thomas.lindenmayer.data;

import android.content.Context;

import com.playposse.thomas.lindenmayer.util.BasePreferences;

/**
 * Central registry for all shared preferences.
 */
public class AppPreferences {

    private static final String SHARED_PREFERENCES_NAME = "ourPreferences";

    private static final String SHOW_TURTLE_TUTORIAL_DIALOG = "showTurtleTutorialDialog";
    private static final String HAS_IMPORTED_USER_RULE_SETS = "hasImportedUserRuleSets";

    private static final boolean SHOW_TURTLE_TUTORIAL_DIALOG_DEFAULT = true;
    private static final boolean HAS_IMPORTED_USER_RULE_SETS_DEFAULT = false;

    private static final BasePreferences basePreferences =
            new BasePreferences(SHARED_PREFERENCES_NAME);

    public static boolean getShowTurtleTutorialDialog(Context context) {
        return basePreferences.getBoolean(
                context,
                SHOW_TURTLE_TUTORIAL_DIALOG,
                SHOW_TURTLE_TUTORIAL_DIALOG_DEFAULT);
    }

    public static void setShowTurtleTutorialDialog(Context context, boolean value) {
        basePreferences.setBoolean(context, SHOW_TURTLE_TUTORIAL_DIALOG, value);
    }

    public static boolean hasImportedUserRuleSets(Context context) {
        return basePreferences.getBoolean(
                context,
                HAS_IMPORTED_USER_RULE_SETS,
                HAS_IMPORTED_USER_RULE_SETS_DEFAULT);
    }

    public static void setHasImportedUserRuleSets(Context context, boolean value) {
        basePreferences.setBoolean(context, HAS_IMPORTED_USER_RULE_SETS, value);
    }

    public static void reset(Context context) {
        basePreferences.reset(context);
    }
}

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
    private static final String HAS_SEEN_INTRO_DECK = "hasSeenIntroDeck";
    private static final String HAS_SURVEY_BEEN_CLICKED = "hasSurveyBeenClicked";
    private static final String SURVEY_NUDGE_COUNTER = "surveyNudgeCounter";

    private static final boolean SHOW_TURTLE_TUTORIAL_DIALOG_DEFAULT = true;
    private static final boolean HAS_IMPORTED_USER_RULE_SETS_DEFAULT = false;
    private static final boolean HAS_SEEN_INTRO_DECK_DEFAULT = false;
    private static final boolean HAS_SURVEY_BEEN_CLICKED_DEFAULT = false;
    private static final int SURVEY_NUDGE_COUNTER_DEFAULT = 0;

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

    public static boolean hasSeenIntroDeck(Context context) {
        return basePreferences.getBoolean(
                context,
                HAS_SEEN_INTRO_DECK,
                HAS_SEEN_INTRO_DECK_DEFAULT);
    }

    public static void setHasSeenIntroDeck(Context context, boolean value) {
        basePreferences.setBoolean(context, HAS_SEEN_INTRO_DECK, value);
    }

    /**
     * Returns true if the user has clicked on the sanck bar to start taking the survey. We do not
     * know if the user actually submitted the survey.
     */
    public static boolean hasSurveyBeenClicked(Context context) {
        return basePreferences.getBoolean(
                context,
                HAS_SURVEY_BEEN_CLICKED,
                HAS_SURVEY_BEEN_CLICKED_DEFAULT);
    }

    public static void setHasSurveyBeenClicked(Context context, boolean value) {
        basePreferences.setBoolean(context, HAS_SURVEY_BEEN_CLICKED, value);
    }

    /**
     * Returns the number of times that the app considered showing a snack bar to nudge the user to
     * fill out a survey about the app. The app shows the snack bar progressively less frequently to
     * avoid annoying the user.
     */
    public static int getSurveyNudgeCounter(Context context) {
        return basePreferences.getInt(context, SURVEY_NUDGE_COUNTER, SURVEY_NUDGE_COUNTER_DEFAULT);
    }

    public static int incrementSurveyNudgeCounter(Context context) {
        int count = getSurveyNudgeCounter(context);
        count++;
        basePreferences.setInt(context, SURVEY_NUDGE_COUNTER, count);
        return count;
    }

    public static void reset(Context context) {
        basePreferences.reset(context);
    }
}

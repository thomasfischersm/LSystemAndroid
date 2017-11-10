package com.playposse.thomas.lindenmayer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.playposse.thomas.lindenmayer.contentprovider.QueryHelper;
import com.playposse.thomas.lindenmayer.domain.RuleSet;

/**
 * A central class that manages navigation between activities.
 */
public class ActivityNavigator {

    public static void startIntroductionActivity(Context context) {
        context.startActivity(new Intent(context, IntroductionActivity.class));
    }

    public static void startMainActivity(Context context) {
        context.startActivity(new Intent(context, MainActivity.class));
    }

    public static void startVideoTutorialActivity(Context context) {
        context.startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://youtu.be/bruY40g_S2w")));
    }

    public static void startTurtleTrainingActivity(Context context) {
        context.startActivity(new Intent(context, TurtleTrainingActivity.class));
    }

    public static void startRuleSetActivity(Context context, long ruleSetId) {
        // TODO: Transition to using ruleSetIds to navigate between activities. Once rule sets
        // include more complex data, the will be too large to be passed as intent parameters.

        RuleSet ruleSet = QueryHelper.getParsedRuleSetById(context.getContentResolver(), ruleSetId);
        startRuleSetActivity(context, ruleSet);
    }

    public static void startRuleSetActivity(Context context, RuleSet ruleSet) {
        Intent intent = new Intent(context, RulesActivity.class);
        intent.putExtra(RuleSet.EXTRA_RULE_SET, ruleSet);
        context.startActivity(intent);
    }

    public static void startRenderActivity(Context context, RuleSet ruleSet) {
        Intent intent = new Intent(context, RenderingActivity.class);
        intent.putExtra(RuleSet.EXTRA_RULE_SET, ruleSet);
        context.startActivity(intent);
    }

    public static void startNewRuleSetActivity(Activity activity) {
        activity.finish();

        Intent intent = new Intent(activity, RulesActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
    }

    public static void startSampleLibraryActivity(Context context) {
        context.startActivity(new Intent(context, SampleLibraryActivity.class));
    }

    public static void startPrivateLibraryActivity(Context context) {
        context.startActivity(new Intent(context, PrivateLibraryActivity.class));
    }
}

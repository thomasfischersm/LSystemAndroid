package com.playposse.thomas.lindenmayer.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * A central class that manages navigation between activities.
 */
public class ActivityNavigator {

    public static void startVideoTutorialActivity(Context context) {
        context.startActivity(new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://youtu.be/bruY40g_S2w")));
    }

    public static void startTurtleTrainingActivity(Context context) {
        context.startActivity(new Intent(context, TurtleTrainingActivity.class));
    }
}

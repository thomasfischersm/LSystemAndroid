package com.playposse.thomas.lindenmayer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.playposse.thomas.lindenmayer.data.AppPreferences;

/**
 * An {@link android.app.Activity} that provides the entry point into the application.
 */
public class MainActivity extends ParentActivity {

    private static final String LOG_CAT = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!AppPreferences.hasSeenIntroDeck(this)) {
            ActivityNavigator.startIntroductionActivity(this);
        }

        addContentFragment(new MainFragment());
    }
}

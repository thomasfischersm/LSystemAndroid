package com.playposse.thomas.lindenmayer.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * An {@link android.app.Activity} that provides the entry point into the application.
 */
public class MainActivity extends ParentActivity {

    private static final String LOG_CAT = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentFragment(new MainFragment());
    }
}

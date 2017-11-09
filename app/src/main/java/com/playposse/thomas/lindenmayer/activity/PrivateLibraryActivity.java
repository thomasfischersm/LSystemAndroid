package com.playposse.thomas.lindenmayer.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * An {@link Activity} that shows a grid with private rule sets.
 */
public class PrivateLibraryActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentFragment(new PrivateLibraryFragment());
    }
}

package com.playposse.thomas.lindenmayer.activity;

import android.app.Activity;
import android.os.Bundle;

/**
 * An {@link Activity} that shows a grid with sample rule sets.
 */
public class SampleLibraryActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentFragment(new SampleLibraryFragment());
    }
}

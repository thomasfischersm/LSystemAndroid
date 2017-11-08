package com.playposse.thomas.lindenmayer.activity;

import android.os.Bundle;

/**
 * An {@link android.app.Activity} that shows general help.
 */
public class HelpActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentFragment(new HelpFragment());
    }
}

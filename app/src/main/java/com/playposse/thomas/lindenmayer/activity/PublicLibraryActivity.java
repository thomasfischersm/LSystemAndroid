package com.playposse.thomas.lindenmayer.activity;

import android.app.Activity;
import android.os.Bundle;

import com.playposse.thomas.lindenmayer.domain.RuleSet;

/**
 * An {@link Activity} that shows a grid of {@link RuleSet}s that other users have published.
 */
public class PublicLibraryActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentFragment(new PublicLibraryFragment());
    }
}

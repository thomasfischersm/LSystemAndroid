package com.playposse.thomas.lindenmayer.activity;

import android.os.Bundle;

/**
 * An activity that trains the user to use turtle graphics. The upper half of the screen is the
 * drawing area for the turtle. The lower half offers buttons for each turtle action. The middle
 * of the screen is the instruction string for the turtle.
 */
public class TurtleTrainingActivity extends ParentActivity {

    private static final String LOG_CAT = TurtleTrainingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentFragment(new TurtleTrainingFragment());
    }
}
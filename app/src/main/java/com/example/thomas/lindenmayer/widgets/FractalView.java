package com.example.thomas.lindenmayer.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.example.thomas.lindenmayer.domain.Fragment;
import com.example.thomas.lindenmayer.domain.RuleSet;
import com.example.thomas.lindenmayer.logic.DimensionProcessor;

/**
 * Created by Thomas on 5/19/2016.
 */
public class FractalView extends View {

    private static final String LOG_CAT = FractalView.class.getSimpleName();

    private Fragment fragment;
    private int directionIncrement;
    private Fragment.Dimension dimension;

    public FractalView(Context context) {
        super(context);
    }

    public void assignFragment(Fragment fragment, int directionIncrement) {
        this.fragment = fragment;
        this.directionIncrement = directionIncrement;
        dimension = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(LOG_CAT, "FractalView is redrawing");

        if (fragment == null) {
            // Wait for fragment computation to be complete.
            return;
        }

        if (dimension == null) {
            dimension = DimensionProcessor.computeDimension(fragment, getWidth() - 1, getHeight() - 1, directionIncrement);
        }

        Fragment.Turtle turtle = new Fragment.Turtle(canvas, dimension, directionIncrement);
        fragment.draw(turtle);
    }
}

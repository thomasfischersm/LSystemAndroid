package com.playposse.thomas.lindenmayer.ui;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;

import com.playposse.thomas.lindenmayer.domain.Dimension;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.domain.Turtle;
import com.playposse.thomas.lindenmayer.logic.BruteForceAlgorithm;

import java.io.File;

/**
 * Brute force implementation of the {@see RenderAsyncTask}. This algorithm writes out the string
 * for each iteration. The memory and time consumption grows linearly and becomes very
 * slow to compute before reaching ten iterations for most L-Sytems. However, for calculation
 * stochastic rule sets, this is necessary.
 */
public class BruteForceRenderAsyncTask extends RenderAsyncTask<String> {

    public BruteForceRenderAsyncTask(
            RuleSet ruleSet,
            FractalView fractalView,
            int iterationCount,
            ShareActionProvider shareActionProvider,
            File cacheDir,
            Context context,
            SwipeRefreshLayout swipeRefreshLayout,
            boolean enableProgressDialog) {

        super(
                ruleSet,
                fractalView,
                iterationCount,
                shareActionProvider,
                cacheDir,
                context,
                swipeRefreshLayout,
                enableProgressDialog);
    }

    @Override
    protected String iterate(RuleSet ruleSet, int iterationCount) {
        return BruteForceAlgorithm.iterate(ruleSet, iterationCount);
    }

    @Override
    protected Dimension computeDimension(
            String str,
            int canvasWidth,
            int canvasHeight,
            int directionIncrement) {

        return BruteForceAlgorithm.computeDimension(
                str,
                canvasWidth,
                canvasHeight,
                directionIncrement);
    }

    @Override
    protected void draw(String str, Turtle turtle) {
        BruteForceAlgorithm.paint(str, turtle);
    }

    @Override
    public ProgressCallbackImpl createProgessCallback(String str) {
        return new ProgressCallbackImpl(str.length());
    }
}

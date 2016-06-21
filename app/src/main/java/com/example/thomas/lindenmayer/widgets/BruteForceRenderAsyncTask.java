package com.example.thomas.lindenmayer.widgets;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;

import com.example.thomas.lindenmayer.bruteforce.BruteForceAlgorithm;
import com.example.thomas.lindenmayer.domain.Fragment;
import com.example.thomas.lindenmayer.domain.RuleSet;

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
    protected Fragment.Dimension computeDimension(
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
    protected void draw(String str, Fragment.Turtle turtle) {
        BruteForceAlgorithm.paint(str, turtle);
    }

    @Override
    protected ProgressCallback createProgessCallback(String str) {
        return new ProgressCallback(str.length());
    }
}

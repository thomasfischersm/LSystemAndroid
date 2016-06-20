package com.example.thomas.lindenmayer.widgets;

import android.content.Context;
import android.support.v7.widget.ShareActionProvider;

import com.example.thomas.lindenmayer.domain.Fragment;
import com.example.thomas.lindenmayer.domain.RuleSet;
import com.example.thomas.lindenmayer.logic.DimensionProcessor;
import com.example.thomas.lindenmayer.logic.RuleProcessor;

import java.io.File;

/**
 * Optimized implementation of {@see RenderAsyncTask}. Fractals are repeating. In processing a fractal,
 * this can be utlized for fast speed and low memory consumption. Say if a substitution creates
 * a certain pattern, future iterations will repeat the same pattern. Rather than compute them
 * each time, the pattern can be cached.
 *
 * Say an axiom has multiple 'f' instructions. This first iteration 'f' will get more complicated
 * with each iteration. When the final fractal is computed, we only need to compute the result of
 * the first 'f'. Then, we can use the result of that complex 'f' for all the other complex 'f's
 * of the initial axiom. Also, when we look at the first iteration of the 'f', we only need to
 * compute that slightly less complex 'f' once. In essence, we only need to compute what one
 * 'f' looks like at each iteration level. This makes the problem that would grow with
 * exponential complexity a problem of linear complexity.
 *
 * The limitation is that the approach doesn't work for certain instructions. For example, pushing
 * the current turtle location to the stack won't work. Also stochastic systems won't work because
 * each 'f' can be randomized.
 */
public class FastRenderAsyncTask extends RenderAsyncTask<Fragment> {

    public FastRenderAsyncTask(
            RuleSet ruleSet,
            FractalView fractalView,
            int iterationCount,
            ShareActionProvider shareActionProvider,
            File cacheDir,
            Context context,
            boolean enableProgressDialog) {

        super(
                ruleSet,
                fractalView,
                iterationCount,
                shareActionProvider,
                cacheDir,
                context,
                enableProgressDialog);
    }

    @Override
    protected Fragment iterate(RuleSet ruleSet, int iterationCount) {
        return RuleProcessor.runIterations(ruleSet, iterationCount);
    }

    @Override
    protected Fragment.Dimension computeDimension(
            Fragment fragment,
            int width,
            int height,
            int directionIncrement) {

        return DimensionProcessor.computeDimension(fragment, width - 3, height - 3, directionIncrement);
    }

    @Override
    protected void draw(Fragment fragment, Fragment.Turtle turtle) {
        fragment.draw(turtle);
    }

    @Override
    protected ProgressCallback createProgessCallback(Fragment fragment) {
        return new ProgressCallback(fragment.getSize());
    }
}

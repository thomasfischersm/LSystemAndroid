package com.playposse.thomas.lindenmayer.glide;

import android.util.Log;

import com.bumptech.glide.load.Key;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.widgets.ProgressCallback;

import javax.annotation.Nullable;

/**
 * A Glide resource that will load and render a RuleSet to a bitmap.
 */
public class RuleSetResource {

    private static final String LOG_TAG = RuleSetResource.class.getSimpleName();

    // TODO: Handle stochastic rulesets!
    // TODO: Handle iteration!

    private final RuleSet ruleSet;
    private final int iterationCount;
    @Nullable
    private final ProgressCallback progressCallback;
    private boolean isIgnoreStochastic;

    /**
     * Creates a new instance.
     *
     * @param ruleSet Provide the RuleSet to render.
     * @param iterationCount Specify the iteration count to run.
     * @param progressCallback Optionally, provide a callback that provides progress updates.
     * @param isIgnoreStochastic By default, stochastic RuleSets are not cached. Each time they
     *                           render, they should be rendered fresh because they will be
     *                           rendered differently. For thumbnails, it is useful to override
     *                           this behavior to cache them.
     */
    public RuleSetResource(
            RuleSet ruleSet,
            int iterationCount,
            @Nullable ProgressCallback progressCallback,
            boolean isIgnoreStochastic) {


        this.ruleSet = ruleSet;
        this.iterationCount = iterationCount;
        this.progressCallback = progressCallback;
        this.isIgnoreStochastic = isIgnoreStochastic;
    }

    Key getKey() {
        final Key key;
        if (isIgnoreStochastic || !ruleSet.isStochastic()) {
            key = new HashCodeKey(ruleSet, iterationCount);
            Log.d(LOG_TAG, "getKey: Generated hash key: " + key.hashCode());
        } else {
            key = new RandomKey();
            Log.d(LOG_TAG, "getKey: Generated a random key: " + key.hashCode());
        }
        return key;
    }

    public RuleSet getRuleSet() {
        return ruleSet;
    }

    int getIterationCount() {
        return iterationCount;
    }

    @Nullable
    ProgressCallback getProgressCallback() {
        return progressCallback;
    }
}

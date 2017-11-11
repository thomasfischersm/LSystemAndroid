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

    public RuleSetResource(RuleSet ruleSet, int iterationCount, @Nullable ProgressCallback progressCallback) {
        this.ruleSet = ruleSet;
        this.iterationCount = iterationCount;
        this.progressCallback = progressCallback;
    }

    public Key getKey() {
        HashCodeKey key = new HashCodeKey(ruleSet, iterationCount);
        Log.d(LOG_TAG, "getKey: Generated hash key: " + key.hashCode());
        return key;
    }

    public RuleSet getRuleSet() {
        return ruleSet;
    }

    public int getIterationCount() {
        return iterationCount;
    }

    @Nullable
    public ProgressCallback getProgressCallback() {
        return progressCallback;
    }
}

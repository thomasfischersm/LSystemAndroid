package com.playposse.thomas.lindenmayer.glide;

import android.widget.ProgressBar;

import com.bumptech.glide.load.Key;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.widgets.ProgressCallback;

import javax.annotation.Nullable;

/**
 * A Glide resource that will load and render a RuleSet to a bitmap.
 */
public class RuleSetResource {

    // TODO: Handle stochastic rulesets!
    // TODO: Handle iteration!

    private final RuleSet ruleSet;
    private final int iterationCount;
    @Nullable
    private final ProgressBarProgressCallbackImpl progressCallback;

    public RuleSetResource(RuleSet ruleSet, int iterationCount, @Nullable ProgressBar progressBar) {
        this.ruleSet = ruleSet;
        this.iterationCount = iterationCount;
        progressCallback = new ProgressBarProgressCallbackImpl(progressBar);
    }

    public Key getKey() {
        return new HashCodeKey(ruleSet, iterationCount);
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

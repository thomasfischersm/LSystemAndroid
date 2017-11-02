package com.playposse.thomas.lindenmayer.glide;

import android.widget.ProgressBar;

import com.playposse.thomas.lindenmayer.widgets.ProgressCallback;

/**
 * A {@link ProgressCallback} that updates a {@link ProgressBar}.
 */
public class ProgressBarProgressCallbackImpl implements ProgressCallback {

    private final ProgressBar progressBar;

    public ProgressBarProgressCallbackImpl(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    // TODO: Set max progress.

    @Override
    public void markProgress() {
        // TODO: implement
    }
}

package com.playposse.thomas.lindenmayer.widgets;

/**
 * An interface that logs progress of rendering a fractal.
 */
public interface ProgressCallback {

    /**
     * Called each time the algorithm makes one step forward.
     */
    void markProgress();

    /**
     * Called when the algorithm can anticipate how many steps the run has.
     */
    void declareMaxProgress(int maxProgress);
}

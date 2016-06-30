package com.playposse.thomas.lindenmayer.logic;

import com.playposse.thomas.lindenmayer.domain.Fragment;

/**
 * Created by Thomas on 5/19/2016.
 */
public class DimensionProcessor {

    public static Fragment.Dimension computeDimension(Fragment fragment, int canvasWidth, int canvasHeight, int directionIncrement) {
        Fragment.Dimension dimension = fragment.computeDimension(0, directionIncrement);
        dimension.computeScale(canvasWidth, canvasHeight);
        return dimension;
    }
}

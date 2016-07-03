package com.playposse.thomas.lindenmayer.domain;

import android.graphics.Color;
import android.graphics.Paint;

/**
 * A list of colors to cycle through when rendering.
 */
public class ColorPalette {

    public static final Paint[] COLORS = new Paint[]{
            createPaint("#000000"),
            createPaint("#607D8B"),
            createPaint("#9E9E9E"),
            createPaint("#795548"),
            createPaint("#FF5722"),
            createPaint("#FF9800"),
            createPaint("#FFC107"),
            createPaint("#FFEB3B"),
            createPaint("#CDDC39"),
            createPaint("#8BC34A"),
            createPaint("#4CAF50"),
            createPaint("#009688"),
            createPaint("#00BCD4"),
            createPaint("#03A9F4"),
            createPaint("#2196F3"),
            createPaint("#3F51B5"),
            createPaint("#673AB7"),
            createPaint("#9C27B0"),
            createPaint("#E91E63"),
            createPaint("#F44336"),
    };
    
    private static Paint createPaint(String color) {
        Paint paint = new Paint();
        paint.setColor(Color.parseColor(color));
        return paint;
    }
}

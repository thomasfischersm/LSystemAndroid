package com.playposse.thomas.lindenmayer.util;

import android.graphics.Paint;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache for {@link Paint} objects. The creation of objects is expensive. The algorithm has to
 * create {@link Paint} objects many time. Caching them is a lot cheaper.
 */
public final class PaintCache {

    private static final Map<Integer, Map<Float, Paint>> cache = new HashMap<>();

    private PaintCache() {}

    public static Paint getPaint(int color, float strokeWidth) {
        Map<Float, Paint> floatPaintMap = cache.get(color);
        if (floatPaintMap == null) {
            floatPaintMap = new HashMap<>();
            cache.put(color, floatPaintMap);
        }

        Paint paint = floatPaintMap.get(strokeWidth);
        if (paint == null) {
            paint = createPaint(color, strokeWidth);
            floatPaintMap.put(strokeWidth, paint);
        }

        return paint;
    }

    private static Paint createPaint(int color, float strokeWidth) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        return paint;
    }
}

package com.playposse.thomas.lindenmayer.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.playposse.thomas.lindenmayer.domain.Dimension;

/**
 * A {@link View} that can draw a Lindenmayer system. The {@link FractalView} doesn't do the
 * rendering itself actually. Rather, it depends on the {@link RenderAsyncTask} to do the
 * work and give this class the {@link Bitmap} with the L-system.
 */
public class FractalView extends View {

    private static final String LOG_CAT = FractalView.class.getSimpleName();

    private Bitmap bitmap;
    private int directionIncrement;
    private Dimension dimension;

    public FractalView(Context context) {
        super(context);

        setDrawingCacheEnabled(true);
    }

    public FractalView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setDrawingCacheEnabled(true);
    }

    public FractalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setDrawingCacheEnabled(true);
    }

    public void assignBitmap(Bitmap bitmap, int directionIncrement) {
        this.bitmap = bitmap;
        this.directionIncrement = directionIncrement;
        dimension = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap == null) {
            // Wait for fragment computation to be complete.
            return;
        }

        canvas.drawBitmap(bitmap, 2, 2, null);
    }
}

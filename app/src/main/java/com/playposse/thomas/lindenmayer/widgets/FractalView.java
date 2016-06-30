package com.playposse.thomas.lindenmayer.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import com.playposse.thomas.lindenmayer.domain.Fragment;

/**
 * Created by Thomas on 5/19/2016.
 */
public class FractalView extends View {

    private static final String LOG_CAT = FractalView.class.getSimpleName();

    private Bitmap bitmap;
    private int directionIncrement;
    private Fragment.Dimension dimension;

    public FractalView(Context context) {
        super(context);

        setDrawingCacheEnabled(true);
    }

    public void assignBitmap(Bitmap bitmap, int directionIncrement) {
        this.bitmap = bitmap;
        this.directionIncrement = directionIncrement;
        dimension = null;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.i(LOG_CAT, "FractalView is redrawing");

        if (bitmap == null) {
            // Wait for fragment computation to be complete.
            return;
        }

        canvas.drawBitmap(bitmap, 2, 2, null);
    }
}

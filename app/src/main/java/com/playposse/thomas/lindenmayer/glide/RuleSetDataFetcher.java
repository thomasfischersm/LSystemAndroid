package com.playposse.thomas.lindenmayer.glide;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;
import com.playposse.thomas.lindenmayer.domain.Dimension;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.domain.Turtle;
import com.playposse.thomas.lindenmayer.logic.BruteForceAlgorithm;
import com.playposse.thomas.lindenmayer.widgets.ProgressCallback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * A {@link DataFetcher} that renders a {@link RuleSet}.
 */
class RuleSetDataFetcher implements DataFetcher<InputStream> {

    private static final String LOG_TAG = RuleSetDataFetcher.class.getSimpleName();

    private final RuleSetResource ruleSetResource;
    private final RuleSet ruleSet;
    private final int iterationCount;
    private final int width;
    private final int height;

    RuleSetDataFetcher(RuleSetResource ruleSetResource, int width, int height) {
        this.ruleSetResource = ruleSetResource;
        this.width = width;
        this.height = height;

        ruleSet = ruleSetResource.getRuleSet();
        iterationCount = ruleSetResource.getIterationCount();
    }

    @Override
    public void loadData(Priority priority, final DataCallback<? super InputStream> callback) {
        Log.i(LOG_TAG, "loadData: Start rendering.");
        ProgressCallback progressCallback = ruleSetResource.getProgressCallback();

        // Generate string.
        String fractalRepresentation = BruteForceAlgorithm.iterate(ruleSet, iterationCount);
        if (progressCallback != null) {
            progressCallback.declareMaxProgress(fractalRepresentation.length());
        }

        // Create bitmap.
        Bitmap bitmap = createBitmap();

        // Compute dimension.
        Dimension dimension = BruteForceAlgorithm.computeDimension(
                fractalRepresentation,
                width - 3,
                height - 3,
                ruleSet.getDirectionIncrement());

        // Draw fractal.
        Canvas canvas = new Canvas(bitmap);
        Turtle turtle = new Turtle(
                canvas,
                dimension,
                ruleSet.getDirectionIncrement(),
                progressCallback);
        BruteForceAlgorithm.paint(fractalRepresentation, turtle);

        // Convert to PNG.
        ByteArrayInputStream pngFile = createPngFile(bitmap);

        // Return result to Glide.
        callback.onDataReady(pngFile);
        Log.i(LOG_TAG, "loadData: Done Rendering");
    }

    @Override
    public void cleanup() {
        // Nothing to do.
    }

    @Override
    public void cancel() {

    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }

    private Bitmap createBitmap() {
        // Draw on white background.
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, paint);
        return bitmap;
    }

    private ByteArrayInputStream createPngFile(Bitmap bitmap) {
        // Convert to PNG.
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        return new ByteArrayInputStream(buffer.toByteArray());
    }
}

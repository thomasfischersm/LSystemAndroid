package com.playposse.thomas.lindenmayer.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.activity.RenderingActivity;
import com.playposse.thomas.lindenmayer.domain.Dimension;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.domain.Turtle;
import com.playposse.thomas.lindenmayer.logic.EffortEstimator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * An {@link AsyncTask} that does the rendering of the Lindenmayer System in the background.
 * Optionally, it can display a progress dialog. Typically, only the
 * {@link RenderingActivity} uses the progress dialog because it
 * only renders complex enough iterations.
 */
public abstract class RenderAsyncTask<FRACTAL_REPRESENTATION>
        extends AsyncTask<Void, Integer, Bitmap> {

    private static final String LOG_TAG = RenderAsyncTask.class.getSimpleName();

    private final RuleSet ruleSet;
    private final FractalView fractalView;
    private final int iterationCount;
    private final int width;
    private final int height;
    private final ShareActionProvider shareActionProvider;
    private final File cacheDir;
    private final Context context;
    private final SwipeRefreshLayout swipeRefreshLayout;
    private final boolean enableProgressDialog;

    private ProgressDialog progressDialog;

    public RenderAsyncTask(
            RuleSet ruleSet,
            FractalView fractalView,
            int iterationCount,
            ShareActionProvider shareActionProvider,
            File cacheDir,
            Context context,
            SwipeRefreshLayout swipeRefreshLayout,
            boolean enableProgressDialog) {

        this.ruleSet = ruleSet;
        this.fractalView = fractalView;
        this.iterationCount = iterationCount;
        this.shareActionProvider = shareActionProvider;
        this.cacheDir = cacheDir;
        this.context = context;
        this.swipeRefreshLayout = swipeRefreshLayout;
        this.enableProgressDialog = enableProgressDialog;

        this.width = fractalView.getWidth();
        this.height = fractalView.getHeight();
    }

    @Override
    protected void onPreExecute() {
        if (enableProgressDialog && !isCancelled()) {
            int estimatedComputations = EffortEstimator.estimateIterations(ruleSet, iterationCount);

            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage(context.getResources().getString(R.string.progress_bar_title));
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setMax(estimatedComputations);
            progressDialog.show();
        }
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        if ((width == 0) || (height ==0)) {
            // Not ready to render the preview yet.
            Log.e(LOG_TAG, "doInBackground: Failed to render because the ImageView wasn't " +
                    "attached yet.");
            return null;
        }

        FRACTAL_REPRESENTATION fractalRepresentation = iterate(ruleSet, iterationCount);

        long start = System.currentTimeMillis();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Dimension dimension = computeDimension(fractalRepresentation, width - 3, height - 3, ruleSet.getDirectionIncrement());
        Canvas canvas = new Canvas(bitmap);
        ProgressCallbackImpl progressCallback = createProgessCallback(fractalRepresentation);
        Turtle turtle = new Turtle(
                canvas,
                dimension,
                ruleSet.getDirectionIncrement(),
                progressCallback);
        draw(fractalRepresentation, turtle);
        long end = System.currentTimeMillis();

        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (enableProgressDialog && !isCancelled()) {
            progressDialog.setProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap == null) {
            // The preview isn't ready to render yet.
            return;
        }

        if (enableProgressDialog) {
            progressDialog.hide();
        }

        if (!isCancelled()) {
            fractalView.assignBitmap(bitmap, ruleSet.getDirectionIncrement());
            fractalView.invalidate();

            if (swipeRefreshLayout != null) {
                swipeRefreshLayout.setEnabled(true);
                swipeRefreshLayout.setRefreshing(false);
            }

            if (shareActionProvider != null) {
                try {
                    setShareIntent(bitmap);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void setShareIntent(Bitmap screenBitmap) throws IOException {
        // Draw on white background.
        Bitmap sharedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas sharedCanvas = new Canvas(sharedBitmap);
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        sharedCanvas.drawRect(0, 0, width, height, paint);
        sharedCanvas.drawBitmap(screenBitmap, 1, 1, null);

        // Convert to PNG.
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        sharedBitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);

        // Save file to cache.
        File dir = new File(cacheDir, "images");
        dir.mkdirs();
        File file = new File(dir, "screenshot.png");
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(buffer.toByteArray());
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
//        file.setReadable(true, false);
//        Uri uri = Uri.fromFile(file);

        // Create share intent
        if (shareActionProvider != null) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, "Check out this cool L-System!");
            Uri contentUri = FileProvider.getUriForFile(context, "com.playposse.thomas.lindenmayer", file);
            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
            intent.setType("image/png");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareActionProvider.setShareIntent(intent);
        }
    }

    protected abstract FRACTAL_REPRESENTATION iterate(RuleSet ruleSet, int iterationCount);

    protected abstract Dimension computeDimension(
            FRACTAL_REPRESENTATION fractalRepresentation,
            int width,
            int height,
            int directionIncrement);

    protected abstract void draw(
            FRACTAL_REPRESENTATION fractalRepresentation,
            Turtle turtle);

    protected abstract ProgressCallbackImpl createProgessCallback(
            FRACTAL_REPRESENTATION fractalRepresentation);

    public class ProgressCallbackImpl implements ProgressCallback {

        private final int progressEstimate;

        private int progress = 0;
        private int lastUpdate = 0;
        private boolean progressMaxInitalized = false;

        public ProgressCallbackImpl(int progressEstimate) {
            this.progressEstimate = progressEstimate;
        }

        @Override
        public void markProgress() {
            if (!enableProgressDialog) {
                return;
            }

            progress++;

            if (!progressMaxInitalized) {
                progressDialog.setMax(progressEstimate);
                progressMaxInitalized = true;
            }

            if ((progress - lastUpdate) * 100.0 / progressEstimate > 1) {
                onProgressUpdate(progress);
                lastUpdate = progress;
            }
        }

        @Override
        public void declareMaxProgress(int maxProgress) {
            // Ignore. This class will be deleted soon.
        }
    }
}

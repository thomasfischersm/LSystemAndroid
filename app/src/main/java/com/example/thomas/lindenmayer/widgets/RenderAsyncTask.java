package com.example.thomas.lindenmayer.widgets;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;

import com.example.thomas.lindenmayer.domain.Fragment;
import com.example.thomas.lindenmayer.domain.RuleSet;
import com.example.thomas.lindenmayer.logic.DimensionProcessor;
import com.example.thomas.lindenmayer.logic.RuleProcessor;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Thomas on 5/20/2016.
 */
public class RenderAsyncTask extends AsyncTask<Void, Void, Bitmap> {

    private static final String LOG_CAT = RenderAsyncTask.class.getSimpleName();

    private final RuleSet ruleSet;
    private final FractalView fractalView;
    private final int iterationCount;
    private final int width;
    private final int height;
    private final ShareActionProvider shareActionProvider;
    private final File cacheDir;
    private final Context context;

    public RenderAsyncTask(
            RuleSet ruleSet,
            FractalView fractalView,
            int iterationCount,
            ShareActionProvider shareActionProvider,
            File cacheDir,
            Context context) {

        this.ruleSet = ruleSet;
        this.fractalView = fractalView;
        this.iterationCount = iterationCount;
        this.shareActionProvider = shareActionProvider;
        this.cacheDir = cacheDir;
        this.context = context;

        this.width = fractalView.getWidth();
        this.height = fractalView.getHeight();
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Log.i(LOG_CAT, "Start computing fragment.");
        Fragment fragment = RuleProcessor.runIterations(ruleSet, iterationCount);
        Log.i(LOG_CAT, "Done computing fragment.");

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Fragment.Dimension dimension = DimensionProcessor.computeDimension(fragment, width - 3, height - 3, ruleSet.getDirectionIncrement());
        Canvas canvas = new Canvas(bitmap);
        Fragment.Turtle turtle = new Fragment.Turtle(canvas, dimension, ruleSet.getDirectionIncrement());
        fragment.draw(turtle);
        Log.i(LOG_CAT, "Done rendering bitmap.");

        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        fractalView.assignBitmap(bitmap, ruleSet.getDirectionIncrement());
        fractalView.invalidate();

        try {
            setShareIntent(bitmap);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Log.i(LOG_CAT, "Caused view to redraw.");
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
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "Check out this cool L-System!");
        Uri contentUri = FileProvider.getUriForFile(context, "com.example.thomas.lindenmayer", file);
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.setType("image/png");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareActionProvider.setShareIntent(intent);
    }
}

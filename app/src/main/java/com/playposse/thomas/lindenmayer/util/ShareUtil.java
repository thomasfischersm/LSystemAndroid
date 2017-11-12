package com.playposse.thomas.lindenmayer.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.ImageView;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.glide.GlideApp;
import com.playposse.thomas.lindenmayer.glide.RuleSetResource;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * A helper to make sharing easier.
 */
public final class ShareUtil {

    private static final String LOG_TAG = ShareUtil.class.getSimpleName();

    private static final String FILE_PROVIDER_AUTHORITY = "com.playposse.thomas.lindenmayer";
    private static final String IMAGE_CONTENT_TYPE = "image/png";
    private static final String SHARED_IMAGE_DIRECTORY = "images";
    private static final String TMP_FILE_NAME = "screenshot.png";

    private ShareUtil() {
    }

    @UiThread
    public static void share(final RuleSet ruleSet, final int iterationCount, ImageView imageView) {

        // Extract info from view.
        final int width = imageView.getWidth();
        final int height = imageView.getHeight();
//        Drawable drawable = imageView.getDrawable();
//        if (!(drawable instanceof BitmapDrawable)) {
//            throw new IllegalStateException("Unexpected drawable: "
//                    + drawable.getClass().getName());
//        }
//        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

        final Context context = imageView.getContext();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    shareAsync(context, ruleSet, iterationCount, width, height);
                } catch (IOException | ExecutionException | InterruptedException ex) {
                    Log.e(LOG_TAG, "onShareClicked: Failed to share l-system.", ex);
                }
            }
        }).start();
    }

    @WorkerThread
    public static void shareAsync(
            Context context,
            RuleSet ruleSet,
            int iterationCount,
            int width,
            int height)
            throws IOException, ExecutionException, InterruptedException {


        // Get cached file out of Glide.
        File file = GlideApp.with(context)
                .downloadOnly()
                .load(new RuleSetResource(ruleSet, iterationCount, null))
                .submit(width, height)
                .get();

//        // Draw on white background.
//        Bitmap sharedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        Canvas sharedCanvas = new Canvas(sharedBitmap);
//        Paint paint = new Paint();
//        paint.setColor(Color.WHITE);
//        paint.setStyle(Paint.Style.FILL);
//        sharedCanvas.drawRect(0, 0, width, height, paint);
//        sharedCanvas.drawBitmap(bitmap, 1, 1, null);

//        // Convert to PNG.
//        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//        sharedBitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);

        // Save file to cache.
//        File cacheDir = context.getExternalCacheDir();
//        File dir = new File(cacheDir, SHARED_IMAGE_DIRECTORY);
//        dir.mkdirs();
//        File file = new File(dir, TMP_FILE_NAME);
//        FileOutputStream outputStream = null;
//        try {
//            outputStream = new FileOutputStream(file);
//            outputStream.write(buffer.toByteArray());
//        } finally {
//            if (outputStream != null) {
//                outputStream.close();
//            }
//        }

        // Create share intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_message));
        Uri contentUri = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, file);
        intent.putExtra(Intent.EXTRA_STREAM, contentUri);
        intent.setType(IMAGE_CONTENT_TYPE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        context.startActivity(intent);
    }
}

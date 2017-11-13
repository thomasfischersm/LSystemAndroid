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

    private ShareUtil() {
    }

    @UiThread
    public static void share(final RuleSet ruleSet, final int iterationCount, ImageView imageView) {

        // Extract info from view.
        final int width = imageView.getWidth();
        final int height = imageView.getHeight();
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
    private static void shareAsync(
            Context context,
            RuleSet ruleSet,
            int iterationCount,
            int width,
            int height)
            throws IOException, ExecutionException, InterruptedException {


        // Get cached file out of Glide.
        File file = GlideApp.with(context)
                .downloadOnly()
                .load(new RuleSetResource(ruleSet, iterationCount, null, true))
                .submit(width, height)
                .get();

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

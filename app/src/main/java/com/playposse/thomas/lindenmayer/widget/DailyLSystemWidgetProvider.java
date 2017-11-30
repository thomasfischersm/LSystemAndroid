package com.playposse.thomas.lindenmayer.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RemoteViews;

import com.bumptech.glide.request.target.AppWidgetTarget;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;
import com.playposse.thomas.lindenmayer.contentprovider.parser.RuleSetConverter;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.glide.GlideApp;
import com.playposse.thomas.lindenmayer.glide.RuleSetResource;
import com.playposse.thomas.lindenmayer.util.SmartCursor;

import java.util.Random;

/**
 * A widget that shows a different L-System each day.
 */
public class DailyLSystemWidgetProvider extends AppWidgetProvider {

    private static final String LOG_TAG = DailyLSystemWidgetProvider.class.getSimpleName();

    private static final Random random = new Random();
    public static final int DEFAULT_ITERATION_COUNT = 5;

    private static RuleSet currentRuleSet;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        currentRuleSet = pickRandomRuleSet(context);

        for (int appWidgetId : appWidgetIds) {
            prepareView(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(
            Context context,
            AppWidgetManager appWidgetManager,
            int appWidgetId,
            Bundle newOptions) {

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        // The rule set should already be loaded. Let's be defensive anyway.
        if (currentRuleSet == null) {
            currentRuleSet = pickRandomRuleSet(context);
        }

        prepareView(context, appWidgetManager, appWidgetId);
    }

    private void prepareView(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews remoteViews =
                new RemoteViews(context.getPackageName(), R.layout.widget_daily_l_system);

        // Set text.
        String msg = context.getString(R.string.daily_message, currentRuleSet.getName());
        remoteViews.setTextViewText(R.id.daily_text_view, msg);

        // Set image.
        Bundle appWidgetOptions = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int maxWidth = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int maxHeight = appWidgetOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);
        int width = (int) convertDpToPixel(context, maxWidth);
        int height = (int) convertDpToPixel(context, maxHeight);

        AppWidgetTarget appWidgetTarget =
                new AppWidgetTarget(context, R.id.daily_image_view, remoteViews, appWidgetId);

        GlideApp.with(context)
                .asBitmap()
                .override(width, height)
                .load(new RuleSetResource(
                        currentRuleSet,
                        DEFAULT_ITERATION_COUNT,
                        null,
                        false))
                .into(appWidgetTarget);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Nullable
    private static RuleSet pickRandomRuleSet(Context context) {
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = contentResolver.query(
                RuleSetTable.CONTENT_URI,
                RuleSetTable.COLUMN_NAMES,
                null,
                null,
                null);

        if (cursor == null) {
            throw new IllegalStateException("Failed to read rule sets!");
        }

        if (cursor.getCount() <= 0) {
            Log.e(LOG_TAG, "pickRandomRuleSet: Unexpected amount of rule sets: "
                    + cursor.getCount());
            return null;
        }

        try {
            // Pick random rule set.
            int index = random.nextInt(cursor.getCount());
            cursor.move(index);

            // Read rule set.
            SmartCursor smartCursor = new SmartCursor(cursor, RuleSetTable.COLUMN_NAMES);
            String json = smartCursor.getString(RuleSetTable.RULE_SET_COLUMN);
            return RuleSetConverter.read(json);
        } finally {
            cursor.close();
        }
    }

    public static float convertDpToPixel(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }
}

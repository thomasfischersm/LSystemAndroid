package com.playposse.thomas.lindenmayer.contentprovider;

import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;

import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.util.SmartCursor;

import javax.annotation.Nullable;

/**
 * A collection of common queries against the {@link LindenmayerContentProvider}.
 */
public class QueryHelper {

    private static final String LOG_TAG = QueryHelper.class.getSimpleName();

    public static Cursor getRuleSetById(ContentResolver contentResolver, long ruleSetId) {
        return contentResolver.query(
                RuleSetTable.CONTENT_URI,
                RuleSetTable.COLUMN_NAMES,
                RuleSetTable.ID_COLUMN + "=?",
                new String[]{Long.toString(ruleSetId)},
                null);
    }

    /**
     * Determines if the {@link RuleSet} exists.
     * @return Returns null for no and the id for yes.
     */
    @Nullable
    public static Long doesRulSetExistByName(
            ContentResolver contentResolver,
            String name,
            int type) {

        Cursor cursor = contentResolver.query(
                RuleSetTable.CONTENT_URI,
                RuleSetTable.COLUMN_NAMES,
                RuleSetTable.NAME_COLUMN + "=? and " + RuleSetTable.TYPE_COLUMN + "=?",
                new String[]{name, Integer.toString(type)},
                null);

        if (cursor == null) {
            Log.e(LOG_TAG, "doesRulSetExistByName: Failed to get a cursor.");
            return null;
        }

        try {
            if (cursor.moveToFirst()) {
                SmartCursor smartCursor = new SmartCursor(cursor, RuleSetTable.COLUMN_NAMES);
                return smartCursor.getLong(RuleSetTable.ID_COLUMN);
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }
}

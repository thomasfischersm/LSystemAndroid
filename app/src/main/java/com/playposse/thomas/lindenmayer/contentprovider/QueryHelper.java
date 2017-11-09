package com.playposse.thomas.lindenmayer.contentprovider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;
import com.playposse.thomas.lindenmayer.contentprovider.parser.RuleSetConverter;
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

    public static RuleSet getParsedRuleSetById(ContentResolver contentResolver, long ruleSetId) {
        Cursor cursor = getRuleSetById(contentResolver, ruleSetId);

        if (cursor == null) {
            throw new IllegalStateException("Failed to load RuleSet. The Cursor was null.");
        }

        try {
            if (cursor.moveToFirst()) {
                SmartCursor smartCursor = new SmartCursor(cursor, RuleSetTable.COLUMN_NAMES);
                String json = smartCursor.getString(RuleSetTable.RULE_SET_COLUMN);
                return RuleSetConverter.read(json);
            } else {
                throw new IllegalStateException("Cursor failed to return a row!");
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Determines if the {@link RuleSet} exists.
     *
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

    /**
     * Saves a private {@link RuleSet}.
     */
    public static void savePrivateRuleSet(Context context, RuleSet ruleSet) {
        String json = RuleSetConverter.write(ruleSet);

        ContentValues values = new ContentValues();
        values.put(RuleSetTable.NAME_COLUMN, ruleSet.getName());
        values.put(RuleSetTable.RULE_SET_COLUMN, json);
        values.put(RuleSetTable.TYPE_COLUMN, RuleSetTable.PRIVATE_TYPE);

        ContentResolver contentResolver = context.getContentResolver();

        Long ruleSetId = doesRulSetExistByName(
                contentResolver,
                ruleSet.getName(),
                RuleSetTable.PRIVATE_TYPE);
        if (ruleSetId == null) {
            contentResolver.insert(RuleSetTable.CONTENT_URI, values);
        } else {
            contentResolver.update(
                    RuleSetTable.CONTENT_URI,
                    values,
                    RuleSetTable.ID_COLUMN + "=?",
                    new String[]{ruleSetId.toString()});
        }
    }
}

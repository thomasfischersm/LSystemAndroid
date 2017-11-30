package com.playposse.thomas.lindenmayer.contentprovider;

import android.content.ContentProvider;
import android.database.sqlite.SQLiteOpenHelper;

import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;
import com.playposse.thomas.lindenmayer.util.BasicContentProvider;

/**
 * A {@link ContentProvider} that stores the rule sets.
 */
public class LindenmayerContentProvider extends BasicContentProvider {

    private static final int RULE_SET_TABLE_KEY = 1;

    public LindenmayerContentProvider() {
        addTable(
                RULE_SET_TABLE_KEY,
                LindenmayerContentContract.AUTHORITY,
                RuleSetTable.PATH,
                RuleSetTable.CONTENT_URI,
                RuleSetTable.TABLE_NAME);
    }

    @Override
    protected SQLiteOpenHelper createDatabaseHelper() {
        return new LindenmayerDatabaseHelper(getContext());
    }
}

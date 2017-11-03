package com.playposse.thomas.lindenmayer.contentprovider;

import android.content.ContentProvider;
import android.database.sqlite.SQLiteOpenHelper;

import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;
import com.playposse.thomas.lindenmayer.util.BasicContentProvider;

/**
 * A {@link ContentProvider} that stores the rule sets.
 */
public class LindenmayerContentProvider extends BasicContentProvider {

    private static final int RECIPE_TABLE_KEY = 1;
    private static final int INGREDIENT_TABLE_KEY = 2;
    private static final int STEP_TABLE_KEY = 3;

    public LindenmayerContentProvider() {
        addTable(
                RECIPE_TABLE_KEY,
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

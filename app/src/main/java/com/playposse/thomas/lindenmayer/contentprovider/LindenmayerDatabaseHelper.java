package com.playposse.thomas.lindenmayer.contentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;

/**
 * A helper class that manages the SQLLite database.
 */
public class LindenmayerDatabaseHelper extends SQLiteOpenHelper {

    public  static final String DB_NAME = "lindenmayer";

    private static final int DB_VERSION = 1;

    public LindenmayerDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RuleSetTable.SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Nothing to upgrade. This is the first version.
    }
}

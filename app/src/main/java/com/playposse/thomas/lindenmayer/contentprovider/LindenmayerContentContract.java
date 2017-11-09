package com.playposse.thomas.lindenmayer.contentprovider;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * A contract for {@link LindenmayerContentProvider}.
 */
public class LindenmayerContentContract {

    public static final String AUTHORITY = "com.playposse.thomas.lindenmayer.provider";

    private static final String CONTENT_SCHEME = "content";

    private LindenmayerContentContract() {}

    private static Uri createContentUri(String path) {
        return new Uri.Builder()
                .scheme(CONTENT_SCHEME)
                .encodedAuthority(AUTHORITY)
                .appendPath(path)
                .build();
    }

    /**
     * Stores rule sets
     */
    public static final class RuleSetTable implements BaseColumns {

        public static final String PATH = "ruleSet";
        public static final Uri CONTENT_URI = createContentUri(PATH);
        public static final String TABLE_NAME = "rule_set_table";

        public static final String ID_COLUMN = _ID;
        public static final String NAME_COLUMN = "name";
        public static final String RULE_SET_COLUMN = "rule_set";
        public static final String TYPE_COLUMN = "type";

        public static final int SAMPLE_TYPE = 1;
        public static final int PRIVATE_TYPE = 2;
        public static final int PUBLIC_TYPE = 3;

        public static final String[] COLUMN_NAMES = new String[]{
                ID_COLUMN,
                NAME_COLUMN,
                RULE_SET_COLUMN,
                TYPE_COLUMN};

        static final String SQL_CREATE_TABLE =
                "CREATE TABLE rule_set_table "
                        + "(_id INTEGER PRIMARY KEY, "
                        + "name TEXT, "
                        + "rule_set TEXT, "
                        + "type INTEGER)";
    }
}

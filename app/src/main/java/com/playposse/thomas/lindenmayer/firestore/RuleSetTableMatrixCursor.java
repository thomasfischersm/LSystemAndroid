package com.playposse.thomas.lindenmayer.firestore;

import android.database.MatrixCursor;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract;
import com.playposse.thomas.lindenmayer.util.StringUtil;

/**
 * A {@link MatrixCursor} that mimics the {@link LindenmayerContentContract.RuleSetTable} from the content repository.
 */
public class RuleSetTableMatrixCursor extends MatrixCursor {

    public RuleSetTableMatrixCursor(Task<QuerySnapshot> task) {
        super(LindenmayerContentContract.RuleSetTable.COLUMN_NAMES, task.getResult().size());

        QuerySnapshot result = task.getResult();

        for (DocumentSnapshot document : result.getDocuments()) {
            int id = document.getId().hashCode();
            String name = document.getString(FireStoreHelper.RULE_SET_NAME_PROPERTY);
            String json = document.getString(FireStoreHelper.RULE_SET_PROPERTY);

            if (!StringUtil.isEmpty(name) && !StringUtil.isEmpty(json)) {
                addRow(new Object[]{
                        id,
                        name,
                        json,
                        LindenmayerContentContract.RuleSetTable.PUBLIC_TYPE});
            }
        }
    }
}

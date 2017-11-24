package com.playposse.thomas.lindenmayer.firestore;

import android.database.MatrixCursor;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.PublicRuleSetTable;
import com.playposse.thomas.lindenmayer.firestore.data.FireStoreRuleSet;
import com.playposse.thomas.lindenmayer.util.StringUtil;

/**
 * A {@link MatrixCursor} that mimics the {@link LindenmayerContentContract.RuleSetTable} from the content repository.
 */
class RuleSetTableMatrixCursor extends MatrixCursor {

    RuleSetTableMatrixCursor(Task<QuerySnapshot> task) {
        super(PublicRuleSetTable.COLUMN_NAMES, task.getResult().size());

        QuerySnapshot result = task.getResult();

        for (DocumentSnapshot document : result.getDocuments()) {
            int hashedId = document.getId().hashCode();

            FireStoreRuleSet fireRuleSet = document.toObject(FireStoreRuleSet.class);
            String name = fireRuleSet.getName();
            String json = fireRuleSet.getRuleSet();

            if (!StringUtil.isEmpty(name) && !StringUtil.isEmpty(json)) {
                addRow(new Object[]{
                        hashedId,
                        name,
                        json,
                        LindenmayerContentContract.RuleSetTable.PUBLIC_TYPE,
                        fireRuleSet.getCreatorName(),
                        fireRuleSet.getCreatorPhotoUrl(),
                        document.getId()});
            }
        }
    }
}

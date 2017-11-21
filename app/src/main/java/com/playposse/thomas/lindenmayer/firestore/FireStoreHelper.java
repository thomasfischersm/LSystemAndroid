package com.playposse.thomas.lindenmayer.firestore;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.playposse.thomas.lindenmayer.R;

/**
 * A helper for accessing Firestore.
 */
public final class FireStoreHelper {

    private static final String LOG_TAG = FireStoreHelper.class.getSimpleName();

    private static final String RULE_SETS_COLLECTION = "ruleSets";

    private FireStoreHelper() {
    }

    public static void loadRuleSets(final LoadingCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RULE_SETS_COLLECTION)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            RuleSetTableMatrixCursor cursor = new RuleSetTableMatrixCursor(task);
                            callback.onLoaded(cursor);
                        } else {
                            Log.w(LOG_TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    static void onPublishConfirmedAndSignedIn(
            final Context context,
            final String ruleSetName,
            String ruleSetJson) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The FirebaseUser should never be null here!");
        }

        // TODO: Check if we should update instead of create.
        // TODO: Prompt for rule set name if none given.
        // TODO: Check that the rule set is NOT a sample!
        // TODO: Check if the actual rules have already been submitted!

        FireStoreRuleSet fireRuleSet = new FireStoreRuleSet(
                ruleSetName,
                ruleSetJson,
                user.getUid(), user.
                getDisplayName());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RULE_SETS_COLLECTION)
                .add(fireRuleSet)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String toastMsg = context.getString(
                                R.string.rule_set_published_successfully,
                                ruleSetName);

                        Toast.makeText(
                                context,
                                toastMsg,
                                Toast.LENGTH_LONG)
                                .show();
                        Log.d(LOG_TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(LOG_TAG, "Error adding document", e);
                    }
                });
    }

    /**
     * A callback interface that gets invoked when the RuleSets have been loaded from the Firestore.
     */
    public interface LoadingCallback {
        void onLoaded(Cursor cursor);
    }
}

package com.playposse.thomas.lindenmayer.firestore;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.util.DialogUtil;
import com.playposse.thomas.lindenmayer.util.StringUtil;

/**
 * A helper for accessing Firestore.
 */
public final class FireStoreHelper {

    private static final String LOG_TAG = FireStoreHelper.class.getSimpleName();

    private static final String RULE_SETS_COLLECTION = "ruleSets";

    private static final String RULE_SET_NAME_PROPERTY = "name";
    private static final String RULE_SET_PROPERTY = "ruleSet";
    private static final String CREATOR_ID_PROPERTY = "creatorId";
    private static final String CREATOR_NAME_PROPERTY = "creatorName";

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
            final String ruleSetJson) {

        // Check if a name has been given.
        if (StringUtil.isEmpty(ruleSetName)) {
            DialogUtil.alert(
                    context,
                    R.string.name_missing_dialog_title,
                    R.string.name_missing_dialog_message);
            return;
        }

        // Check if the RuleSet needs to be created or updated.
        getRuleSetByNameAndCurrentUser(ruleSetName, new LookupCallback() {
            @Override
            public void onLoaded(
                    @Nullable String ruleSetId,
                    @Nullable FireStoreRuleSet fireRuleSet) {

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (ruleSetId == null) {
                    onPublishConfirmedAndSignedIn(context, ruleSetName, ruleSetJson, null);
                } else if (fireRuleSet.getCreatorId().equals(user.getUid())) {
                    onPublishConfirmedAndSignedIn(context, ruleSetName, ruleSetJson, ruleSetId);
                } else {
                    // Another user has already chosen that name.
                    String toastMsg =
                            context.getString(R.string.published_name_exists_toast, ruleSetName);
                    Toast.makeText(context, toastMsg, Toast.LENGTH_LONG)
                            .show();
                }
            }
        });
    }

    private static void onPublishConfirmedAndSignedIn(
            final Context context,
            final String ruleSetName,
            String ruleSetJson,
            @Nullable final String existingId) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The FirebaseUser should never be null here!");
        }

        // TODO: Check that the rule set is NOT a sample!
        // TODO: Check if the actual rules have already been submitted!

        Uri creatorPhotoUrl = user.getPhotoUrl();
        String creatorPhotoUrlString =
                (creatorPhotoUrl != null) ? creatorPhotoUrl.toString() : null;
        FireStoreRuleSet fireRuleSet = new FireStoreRuleSet(
                ruleSetName,
                ruleSetJson,
                user.getUid(),
                user.getDisplayName(),
                creatorPhotoUrlString);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection(RULE_SETS_COLLECTION);

        if (existingId == null) {
            // Insert.
            collection
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
                        public void onFailure(@NonNull Exception ex) {
                            Log.w(LOG_TAG, "Error adding document", ex);
                            Toast.makeText(context, ex.getLocalizedMessage(), Toast.LENGTH_LONG)
                                    .show();
                            throw new RuntimeException("Failed to add RuleSet to FireStore", ex);
                        }
                    });
        } else {
            // Update.
            collection
                    .document(existingId)
                    .set(fireRuleSet)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            String toastMsg = context.getString(
                                    R.string.rule_set_published_successfully,
                                    ruleSetName);

                            Toast.makeText(
                                    context,
                                    toastMsg,
                                    Toast.LENGTH_LONG)
                                    .show();
                            Log.d(LOG_TAG, "DocumentSnapshot updated with ID: " + existingId);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception ex) {
                            Log.w(LOG_TAG, "Error updating document", ex);
                            Toast.makeText(context, ex.getLocalizedMessage(), Toast.LENGTH_LONG)
                                    .show();
                            throw new RuntimeException(
                                    "Failed to update RuleSet to FireStore. Rule set name: "
                                            + ruleSetName + " user uid: " + user.getUid(),
                                    ex);
                        }
                    });
        }
    }

    private static void getRuleSetByNameAndCurrentUser(
            final String ruleSetName,
            final LookupCallback callback) {

        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The FirebaseUser should never be null here!");
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(RULE_SETS_COLLECTION)
                .whereEqualTo(RULE_SET_NAME_PROPERTY, ruleSetName)
//                .whereEqualTo(CREATOR_ID_PROPERTY, user.getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        if (querySnapshot.isEmpty()
                                || (querySnapshot.size() == 0)
                                || (querySnapshot.getDocuments().size() == 0)) {
                            callback.onLoaded(null, null);
                        } else {
                            DocumentSnapshot documentSnapshot = querySnapshot.getDocuments().get(0);
                            FireStoreRuleSet fireRuleSet =
                                    documentSnapshot.toObject(FireStoreRuleSet.class);
                            callback.onLoaded(documentSnapshot.getId(), fireRuleSet);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception ex) {
                        Log.e(
                                LOG_TAG,
                                "onFailure: Failed to find RuleSet: " + ruleSetName + " "
                                        + user.getUid(),
                                ex);
                    }
                });
    }

    /**
     * A callback interface that gets invoked when the RuleSets have been loaded from the Firestore.
     */
    public interface LoadingCallback {
        void onLoaded(Cursor cursor);
    }

    /**
     * A callback interface that gets the result of a rule set lookup.
     */
    private interface LookupCallback {
        void onLoaded(@Nullable String ruleSetId, @Nullable FireStoreRuleSet fireRuleSet);
    }
}

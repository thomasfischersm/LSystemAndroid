package com.playposse.thomas.lindenmayer.firestore;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.playposse.thomas.lindenmayer.firestore.data.FireLike;

/**
 * A helper for reading and writing likes from FireStore.
 */
public final class FireStoreLikeHelper {

    private static final String LOG_TAG = FireStoreLikeHelper.class.getSimpleName();

    private static final String LIKE_COLLECTION = "likes";

    private FireStoreLikeHelper() {
    }

    /**
     * Checks if the user has liked the particular rule set.
     */
    public static void read(String fireStoreRuleSetId, final LoadCallback callback) {
        String likeId = createDataObject(fireStoreRuleSetId).computeKey();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String path = LIKE_COLLECTION + "/" + likeId;
        db.document(path)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean hasLikeEntry = task.getResult().exists();
                            callback.onLoaded(hasLikeEntry);
                        } else {
                            Log.w(LOG_TAG, "Error reading like: " + path, task.getException());
                        }
                    }
                });
    }

    /**
     * Update the like for the specified rule set.
     */
    public static void write(String fireStoreRuleSetId, boolean isLiked) {
        FireLike like = createDataObject(fireStoreRuleSetId);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final String path = LIKE_COLLECTION + "/" + like.computeKey();
        DocumentReference documentRef = db.document(path);

        if (isLiked) {
            documentRef
                    .set(like)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(LOG_TAG, "onSuccess: Succeeded in saving the like.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception ex) {
                            Log.e(LOG_TAG, "onFailure: Failed to save the like: " + path, ex);
                        }
                    });
        } else {
            documentRef.delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(LOG_TAG, "onSuccess: Successfully deleted like.");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception ex) {
                            Log.d(LOG_TAG, "onFailure: Failed to delete like: " + path, ex);
                        }
                    });
        }
    }

    @NonNull
    private static FireLike createDataObject(String fireStoreRuleSetId) {
        // Check that the user is logged in.
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            throw new IllegalStateException("The FirebaseUser should be logged in here!");
        }

        // Create a compound id from the rule set id and the user likeId.
        return new FireLike(fireStoreRuleSetId, user.getUid());
    }

    /**
     * A callback interface that indicates if the user has liked the particular rule set.
     */
    public interface LoadCallback {

        void onLoaded(boolean hasLiked);
    }
}

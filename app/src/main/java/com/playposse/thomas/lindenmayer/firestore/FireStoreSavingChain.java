package com.playposse.thomas.lindenmayer.firestore;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.data.AppPreferences;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.util.DialogUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Helper for saving {@link RuleSet}s with Firestore. Saving is a chain of asynchronous methods
 * for authentication, loading, saving, and so on.
 */
public final class FireStoreSavingChain {

    private static final String LOG_TAG = FireStoreSavingChain.class.getSimpleName();

    private static final int SIGN_IN_RETURN_CODE = 1;

    private FireStoreSavingChain() {
    }

    public static void onPublishClicked(
            final Activity activity,
            final String ruleSetName,
            final String ruleSetJson) {

        if (AppPreferences.hasSeenPublishDialog(activity)) {
            onPublishConfirmed(activity, ruleSetName, ruleSetJson);
        } else {
            DialogUtil.confirm(
                    activity,
                    R.string.publish_rule_set_dialog_title,
                    R.string.publish_rule_set_dialog_message,
                    R.string.ok_button_label,
                    R.string.cancel_button_label,
                    new Runnable() {
                        @Override
                        public void run() {
                            AppPreferences.setHasSeenPublishDialog(activity, true);

                            onPublishConfirmed(activity ,ruleSetName, ruleSetJson);
                        }
                    });
        }
    }

    private static void onPublishConfirmed(
            Activity activity,
            String ruleSetName,
            String ruleSetJson) {

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FireStoreHelper.onPublishConfirmedAndSignedIn(activity, ruleSetName, ruleSetJson);
        } else {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                    new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build());

            activity.startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(),
                    SIGN_IN_RETURN_CODE);
        }
    }

    public static void onActivityResult(
            Context context,
            int requestCode,
            int resultCode,
            Intent data,
            String ruleSetName,
            String ruleSetJson) {

        if (requestCode == SIGN_IN_RETURN_CODE) {
            if ((resultCode == Activity.RESULT_OK)
                    && (FirebaseAuth.getInstance().getCurrentUser() != null)) {
                FireStoreHelper.onPublishConfirmedAndSignedIn(context, ruleSetName, ruleSetJson);
            }
        }

        // If the user aborted sign in, let 'em be in peace.
    }


}

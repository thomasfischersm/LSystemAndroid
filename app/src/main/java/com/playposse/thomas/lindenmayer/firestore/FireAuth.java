package com.playposse.thomas.lindenmayer.firestore;

import android.app.Activity;

import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

/**
 * A utility for dealing with Firebase authentication.
 */
public final class FireAuth {

    static final int SIGN_IN_RETURN_CODE = 1;

    private FireAuth() {
    }

    public static void signIn(Activity activity) {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                SIGN_IN_RETURN_CODE);
    }
}

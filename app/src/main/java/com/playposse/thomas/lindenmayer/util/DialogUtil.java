package com.playposse.thomas.lindenmayer.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;

import com.playposse.thomas.lindenmayer.R;

import java.util.List;


/**
 * A utility for creating simple dialogs.
 */
public final class DialogUtil {

    private static final String LOG_TAG = DialogUtil.class.getSimpleName();

    private DialogUtil() {
    }

    public static void showMultiChoiceDialog(
            Context context,
            int titleResId,
            int arrayResId,
            final List<Runnable> actionList) {

        new AlertDialog.Builder(context)
                .setTitle(titleResId)
                .setItems(
                        arrayResId,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                                if ((which < 0) || (which >= actionList.size())) {
                                    Log.e(LOG_TAG, "onClick: Multiple choice dialog received an " +
                                            "unexpected choice: " + which);
                                    return;
                                }

                                actionList.get(which).run();
                            }
                        })
                .setNegativeButton(
                        R.string.cancel_button_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .show();

    }
    /**
     * Shows a dialog with a message that the user can agree with or reject.
     */
    public static void confirm(
            Context context,
            int titleResId,
            int messageResId,
            Runnable confirmationRunnable) {

        confirm(
                context,
                titleResId,
                messageResId,
                R.string.ok_button_label,
                R.string.cancel_button_label,
                confirmationRunnable);
    }

    /**
     * Shows a dialog with a message that the user can agree with or reject.
     */
    public static void confirm(
            Context context,
            int titleResId,
            int messageResId,
            int positiveResId,
            int negativeResId,
            final Runnable confirmationRunnable) {

        new AlertDialog.Builder(context)
                .setTitle(titleResId)
                .setMessage(messageResId)
                .setNegativeButton(
                        negativeResId,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                )
                .setPositiveButton(
                        positiveResId,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                confirmationRunnable.run();
                            }
                        }
                )
                .show();
    }

    /**
     * Shows a dialog with a simple message for the user to acknowledge.
     */
    public static void alert(Context context, int titleResId, int messageResId) {
        new AlertDialog.Builder(context)
                .setTitle(titleResId)
                .setMessage(messageResId)
                .setPositiveButton(
                        R.string.ok_button_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }
                )
                .show();
    }

    /**
     * Asks the user to input text into a dialog.
     */
    public static void requestInput(
            Context context,
            int titleResId,
            int messageResId,
            @Nullable String currentValue,
            final UserInputCallback callback) {

        // TODO: Disable the submit button until some text has been entered.

        final EditText editText = new EditText(context);
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);

        if (!StringUtil.isEmpty(currentValue)) {
            editText.setText(currentValue);
        }

        new AlertDialog.Builder(context)
                .setTitle(titleResId)
                .setMessage(messageResId)
                .setView(editText)
                .setPositiveButton(
                        R.string.ok_button_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                callback.onSubmit(editText.getText().toString());
                            }
                        }
                )
                .setNegativeButton(
                        R.string.cancel_button_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }
                )
                .show();
    }

    /**
     * Callback to return user input from a dialog.
     */
    public interface UserInputCallback {

        void onSubmit(String userInput);
    }
}

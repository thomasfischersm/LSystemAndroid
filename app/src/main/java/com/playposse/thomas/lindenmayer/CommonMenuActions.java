package com.playposse.thomas.lindenmayer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

/**
 * Helper class that contains menu actions that are shared among all activities.
 */
public final class CommonMenuActions {

    public static void sendFeedbackAction(Context context) {
        showInfoDialogAndSendEmail(
                context,
                R.string.feedback_dialog_title,
                R.string.feedback_dialog_message,
                R.string.feedback_email_recipient,
                R.string.feedback_email_subject,
                context.getResources().getString(R.string.feedback_email_body));
    }

    private static void showInfoDialogAndSendEmail(
            final Context context,
            int dialogTitle,
            int dialogMessage,
            final int recipient,
            final int subject,
            final String bodyString) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        dialogBuilder.setTitle(context.getResources().getString(dialogTitle));
        dialogBuilder.setMessage(context.getResources().getString(dialogMessage));
        dialogBuilder.setPositiveButton(
                context.getResources().getString(R.string.dialog_continue_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sendEmail(context, recipient, subject, bodyString);
                    }
                }
        );
        dialogBuilder.setNegativeButton(
                context.getResources().getString(R.string.dialog_cancel_button),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );

        dialogBuilder.create().show();
    }

    private static void sendEmail(Context context, int recipient, int subject, String bodyString) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getResources().getString(recipient)});
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(subject));
        intent.putExtra(Intent.EXTRA_TEXT, bodyString);
        context.startActivity(intent);
    }
}

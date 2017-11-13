package com.playposse.thomas.lindenmayer.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;
import com.playposse.thomas.lindenmayer.contentprovider.QueryHelper;
import com.playposse.thomas.lindenmayer.data.DataReader;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.util.AnalyticsUtil;
import com.playposse.thomas.lindenmayer.util.StringUtil;
import com.playposse.thomas.lindenmayer.widgets.SaveView;

import org.json.JSONException;

import java.io.IOException;

/**
 * An {@link android.app.Activity} that allows the user to enter a
 * {@link com.playposse.thomas.lindenmayer.domain.RuleSet}.
 */
public class RulesActivity extends ParentActivity<RulesFragment> {

    private static final String LOG_CAT = RulesActivity.class.getSimpleName();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // TODO: Hide/show the delete button intelligently based on if the RuleSet is currently
        // saved or transient.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rules_menu, menu);

        final MenuItem saveMenuItem = menu.findItem(R.id.action_save);
        final SaveView saveView = (SaveView) saveMenuItem.getActionView();
        saveView.addSaveHandler(new SaveView.SaveHandler() {
            @Override
            public void onSave(String fileName) {
                try {
                    if (!getContentFragment().validate()) {
                        return;
                    }

                    if (DataReader.doesUserRuleSetExist(getApplicationContext(), fileName)) {
                        showOverwriteWarningBeforeSaving(fileName, saveMenuItem);
                    } else {
                        saveRuleSetAndCollapseInput(fileName, saveMenuItem);
                    }
                } catch (IOException | JSONException ex) {
                    Log.e(LOG_CAT, "Failed to save rule set.", ex);
                }
            }
        });

        saveMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (!getContentFragment().validate()) {
                    return false;
                }

                String ruleSetName = getContentFragment().getRuleSetName();
                if (!StringUtil.isEmpty(ruleSetName)) {
                    saveView.setText(ruleSetName);
                }
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return true;
            }
        });
        return true;
    }

    /**
     * Shows a dialog asking the user to confirm overwriting an existing {@link RuleSet}.
     */
    private void showOverwriteWarningBeforeSaving(
            final String fileName,
            final MenuItem saveMenuItem) {

        // Retrieve strings.
        String titleStr = getResources().getString(R.string.rules_activity_overwrite_warning_title);
        String messageStr =
                getResources().getString(R.string.rules_activity_overwrite_warning_message);
        String positiveButtonStr =
                getResources().getString(R.string.dialog_continue_button);
        String negativeButtonStr =
                getResources().getString(R.string.dialog_cancel_button);

        // Build dialog.
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle(titleStr);
        alertBuilder.setMessage(messageStr);
        alertBuilder.setPositiveButton(positiveButtonStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    saveRuleSetAndCollapseInput(fileName, saveMenuItem);
                } catch (IOException | JSONException ex) {
                    Log.e(LOG_CAT, "Failed to save rule set.", ex);
                }
                dialog.dismiss();
            }
        });
        alertBuilder.setNegativeButton(negativeButtonStr, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing. Let the user pick another file name.
                dialog.dismiss();
            }
        });

        alertBuilder.create().show();
    }

    /**
     * Handles all the UI management of saving and defers the actual saving to
     * {@link #saveRuleSet(String)}.
     */
    private void saveRuleSetAndCollapseInput(String fileName, MenuItem saveMenuItem)
            throws IOException, JSONException {

        saveRuleSet(fileName);
        saveMenuItem.collapseActionView();
        Toast toast = Toast.makeText(getApplicationContext(), fileName + " saved.", Toast.LENGTH_SHORT);
        toast.show();
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(findViewById(R.id.axiom_text_view).getWindowToken(), 0);
    }

    /**
     * Does the actual saving of the {@link RuleSet}.
     */
    private void saveRuleSet(String fileName) throws IOException, JSONException {
        RuleSet ruleSet = getContentFragment().createRuleSet();

        ruleSet.setName(fileName);
        QueryHelper.savePrivateRuleSet(this, ruleSet);

        // Update activity title.
        setTitle(fileName);

        AnalyticsUtil.sendEvent(getApplication(), "SaveRuleSet");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentFragment(new RulesFragment());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                onDeleteClicked();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onDeleteClicked() {
        String name = getContentFragment().getRuleSetName();

        // Ensure that the RuleSet exists.
        Long ruleSetId = QueryHelper.doesRulSetExistByName(
                getContentResolver(),
                name,
                RuleSetTable.PRIVATE_TYPE);
        if (ruleSetId == null) {
            Toast.makeText(this, R.string.cant_delete_rule_set_toast, Toast.LENGTH_LONG)
                    .show();
            return;
        }

        // Delete the RuleSet.
        QueryHelper.deleteRuleSet(getContentResolver(), ruleSetId);

        // Show toast.
        String deleteToastString = getString(R.string.delete_toast, name);
        Toast toast = Toast.makeText(this, deleteToastString, Toast.LENGTH_SHORT);
        toast.show();

        // TODO: Update the RuleSet name inside of RulesFragment.
    }
}

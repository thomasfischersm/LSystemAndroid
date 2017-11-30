package com.playposse.thomas.lindenmayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;
import com.playposse.thomas.lindenmayer.contentprovider.QueryHelper;
import com.playposse.thomas.lindenmayer.contentprovider.parser.RuleSetConverter;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.firestore.FireStoreSavingChain;
import com.playposse.thomas.lindenmayer.util.AnalyticsUtil;
import com.playposse.thomas.lindenmayer.util.DialogUtil;
import com.playposse.thomas.lindenmayer.util.StringUtil;

/**
 * An {@link android.app.Activity} that allows the user to enter a
 * {@link com.playposse.thomas.lindenmayer.domain.RuleSet}.
 */
public class RulesActivity extends ParentActivity<RulesFragment> {
    // TODO: Hide/show the delete button intelligently based on if the RuleSet is currently
    // saved or transient.

    private static final String LOG_TAG = RulesActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentFragment(new RulesFragment());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        RuleSet ruleSet = getContentFragment().createRuleSet();
        String ruleSetJson = RuleSetConverter.write(ruleSet);
        String ruleSetName = getContentFragment().getRuleSetName();

        FireStoreSavingChain.onActivityResult(
                this,
                requestCode,
                resultCode,
                data,
                ruleSetName,
                ruleSetJson);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                onSaveClicked();
                return true;
            case R.id.action_delete:
                onDeleteClicked();
                return true;
            case R.id.action_publish:
                RuleSet ruleSet = getContentFragment().createRuleSet();
                String ruleSetJson = RuleSetConverter.write(ruleSet);
                String ruleSetName = getContentFragment().getRuleSetName();
                FireStoreSavingChain.onPublishClicked(this, ruleSetName, ruleSetJson);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void onSaveClicked() {
        if (!getContentFragment().validate()) {
            DialogUtil.alert(
                    this,
                    R.string.incomplete_rules_set_dialog_title,
                    R.string.publish_rule_set_dialog_message);
            return;
        }

        final String currentRuleSetName = getContentFragment().getRuleSetName();
        DialogUtil.requestInput(
                this,
                R.string.save_rule_set_dialog_title,
                R.string.save_rule_set_dialog_message,
                currentRuleSetName,
                new DialogUtil.UserInputCallback() {
                    @Override
                    public void onSubmit(String newRuleSetName) {
                        onSaveNameEntered(currentRuleSetName, newRuleSetName);
                    }
                });
    }

    private void onSaveNameEntered(String currentRuleSetName, final String newRuleSetName) {
        // Check if anything was entered.
        if (StringUtil.isEmpty(newRuleSetName)) {
            DialogUtil.alert(
                    this,
                    R.string.empty_rule_set_name_dialog_title,
                    R.string.empty_rule_set_name_dialog_message);
            return;
        }

        // Check if an existing rule set is updated.
        if (currentRuleSetName.equals(newRuleSetName)) {
            saveRuleSet(newRuleSetName);
            return;
        }

        // Check if the name would overwrite an existing rule set.
        Long ruleSetId = QueryHelper.doesRulSetExistByName(
                getContentResolver(),
                newRuleSetName,
                RuleSetTable.PRIVATE_TYPE);
        if (ruleSetId == null) {
            saveRuleSet(newRuleSetName);
        } else {
            DialogUtil.confirm(
                    this,
                    R.string.rules_activity_overwrite_warning_title,
                    R.string.rules_activity_overwrite_warning_message,
                    new Runnable() {
                        @Override
                        public void run() {
                            saveRuleSet(newRuleSetName);
                        }
                    });
        }
    }

    /**
     * Does the actual saving of the {@link RuleSet}.
     */
    private void saveRuleSet(String fileName) {
        // Do the actual saving.
        RuleSet ruleSet = getContentFragment().createRuleSet();
        ruleSet.setName(fileName);
        QueryHelper.savePrivateRuleSet(this, ruleSet);

        // Update the fragment.
        getContentFragment().setRuleSetName(fileName);

        // Update activity title.
        setTitle(fileName);

        // Show the user a success toast.
        String toastMsg = getString(R.string.rule_set_saved_toast, fileName);
        Toast.makeText(getApplicationContext(), toastMsg, Toast.LENGTH_SHORT)
                .show();

        // Might have to hide/show the delete action in the options menu.
        getContentFragment().checkIfSaved();

        AnalyticsUtil.sendEvent(getApplication(), "SaveRuleSet");
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

        // Might have to hide/show the delete action in the options menu.
        getContentFragment().checkIfSaved();

        // Show toast.
        String deleteToastString = getString(R.string.delete_toast, name);
        Toast toast = Toast.makeText(this, deleteToastString, Toast.LENGTH_SHORT);
        toast.show();
    }
}

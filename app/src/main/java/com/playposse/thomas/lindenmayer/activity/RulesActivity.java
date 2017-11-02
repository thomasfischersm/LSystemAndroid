package com.playposse.thomas.lindenmayer.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.playposse.thomas.lindenmayer.AnalyticsUtil;
import com.playposse.thomas.lindenmayer.CommonMenuActions;
import com.playposse.thomas.lindenmayer.HelpActivity;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.data.DataReader;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.widgets.BruteForceRenderAsyncTask;
import com.playposse.thomas.lindenmayer.widgets.FractalView;
import com.playposse.thomas.lindenmayer.widgets.SaveView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link android.app.Activity} that allows the user to enter a
 * {@link com.playposse.thomas.lindenmayer.domain.RuleSet}.
 */
public class RulesActivity extends ParentActivity {

    private static final String LOG_CAT = RulesActivity.class.getSimpleName();

    private final PreviewUpdateWatcher previewUpdateWatcher = new PreviewUpdateWatcher();

    private RuleSet intentRuleSet;
    private NeatRowWatcher neatRowWatcher = new NeatRowWatcher();
    private FractalView fractalView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.rules_menu, menu);

        final MenuItem saveMenuItem = menu.findItem(R.id.action_save);
        final SaveView saveView = (SaveView) saveMenuItem.getActionView();
        saveView.addSaveHandler(new SaveView.SaveHandler() {
            @Override
            public void onSave(String fileName) {
                try {
                    if (!validate()) {
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

        MenuItemCompat.setOnActionExpandListener(saveMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                if (!validate()) {
                    return false;
                }

                if (intentRuleSet != null) {
                    String filename = intentRuleSet.getName();
                    if (filename != null) {
                        saveView.setText(filename);
                    }
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
                } catch (IOException|JSONException ex) {
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
        inputManager.hideSoftInputFromWindow(findViewById(R.id.axiomText).getWindowToken(), 0);
    }

    /**
     * Does the actual saving of the {@link RuleSet}.
     */
    private void saveRuleSet(String fileName) throws IOException, JSONException {
        RuleSet ruleSet = createRuleSet();

        ruleSet.setName(fileName);
        DataReader.saveUserRuleSets(this, ruleSet);

        AnalyticsUtil.sendEvent(getApplication(), "Action", "SaveRuleSet");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rules);

        Toolbar rulesToolbar = (Toolbar) findViewById(R.id.rulesToolbar);
        setSupportActionBar(rulesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton goButton = (FloatingActionButton) findViewById(R.id.goButton);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                render();
            }
        });

        if (getIntent().hasExtra(RuleSet.EXTRA_RULE_SET)) {
            intentRuleSet = getIntent().getParcelableExtra(RuleSet.EXTRA_RULE_SET);
            populateUi();
            logContentViewToFabric(intentRuleSet);
        } else {
            intentRuleSet = null;
            clearUi();
        }

        TextView axiomText = (TextView) findViewById(R.id.axiomText);
        axiomText.addTextChangedListener(previewUpdateWatcher);
        TextView incrementText = (TextView) findViewById(R.id.directionIncrementText);
        incrementText.addTextChangedListener(new AngleWatcher());
        incrementText.addTextChangedListener(previewUpdateWatcher);

        findViewById(R.id.axiomText).requestFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            case R.id.action_send_feedback:
                CommonMenuActions.sendFeedbackAction(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateUi() {
        TextView axiomText = (TextView) findViewById(R.id.axiomText);
        TextView incrementText = (TextView) findViewById(R.id.directionIncrementText);

        axiomText.setText(intentRuleSet.getAxiom());
        incrementText.setText("" + intentRuleSet.getDirectionIncrement());

        rebuildRulesTable(intentRuleSet);
    }

    private void rebuildRulesTable(RuleSet ruleSet) {
        TableLayout rulesTable = (TableLayout) findViewById(R.id.rulesTable);
        rulesTable.removeAllViewsInLayout();
        if ((ruleSet != null) && (ruleSet.getRules() != null)) {
            for (RuleSet.Rule rule : ruleSet.getRules()) {
                addRow(rulesTable, rule.getMatch(), rule.getReplacement());
            }
        }

        addRow(rulesTable, "", "");
    }

    private void clearUi() {
        TextView axiomText = (TextView) findViewById(R.id.axiomText);
        TextView incrementText = (TextView) findViewById(R.id.directionIncrementText);
        TableLayout rulesTable = (TableLayout) findViewById(R.id.rulesTable);

        axiomText.setText("");
        incrementText.setText("");
        rulesTable.removeAllViewsInLayout();
        addRow(rulesTable, "", "");
    }

    private TableRow addRow(TableLayout rulesTable, String match, String replacement) {
        TableRow row = new TableRow(this);
        TableRow.LayoutParams rowLayoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(rowLayoutParams);

        EditText matchText = new EditText(this);
        TableRow.LayoutParams matchTextLayoutParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        matchText.setLayoutParams(matchTextLayoutParams);
        matchText.setEms(1);
        matchText.setText(match);
        matchText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        matchText.setContentDescription(
                getResources().getString(R.string.rules_activity_match_content_description));
        matchText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(1)});
        matchText.addTextChangedListener(neatRowWatcher);
        matchText.addTextChangedListener(previewUpdateWatcher);
        row.addView(matchText);

        ImageView arrowImage = new ImageView(this);
        TableRow.LayoutParams arrowImageLayoutParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        arrowImageLayoutParams.gravity = Gravity.CENTER;
        arrowImage.setLayoutParams(arrowImageLayoutParams);
        arrowImage.setImageResource(R.drawable.ic_forward_black_24dp);
        row.addView(arrowImage);

        EditText replacementText = new EditText(this);
        TableRow.LayoutParams replacementTextLayoutParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        replacementText.setLayoutParams(replacementTextLayoutParams);
        replacementText.setText(replacement);
        replacementText.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        replacementText.setContentDescription(
                getResources().getString(R.string.rules_activity_replacement_content_description));
        replacementText.addTextChangedListener(neatRowWatcher);
        replacementText.addTextChangedListener(previewUpdateWatcher);
        row.addView(replacementText);

        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        rulesTable.addView(row, tableLayoutParams);
        return row;
    }

    private void render() {
        if (!validate()) {
            return;
        }

        RuleSet ruleSet = createRuleSet();

        Intent intent = new Intent(this, RenderingActivity.class);
        intent.putExtra(RuleSet.EXTRA_RULE_SET, ruleSet);
        startActivity(intent);
    }

    private RuleSet createRuleSet() {
        RuleSet ruleSet = null;
        try {
            String axiom = ((TextView) findViewById(R.id.axiomText)).getText().toString();
            String directionIncrementString = ((TextView) findViewById(R.id.directionIncrementText)).getText().toString();
            int directionIncrement = Integer.parseInt(directionIncrementString);
            TableLayout rulesTable = (TableLayout) findViewById(R.id.rulesTable);

            List<RuleSet.Rule> rules = new ArrayList<>();
            for (int i = 0; i < rulesTable.getChildCount(); i++) {
                TableRow row = (TableRow) rulesTable.getChildAt(i);
                Editable matchText = ((EditText) row.getChildAt(0)).getText();
                Editable replacementText = ((EditText) row.getChildAt(2)).getText();
                if (matchText.length() > 0) {
                    RuleSet.Rule rule = new RuleSet.Rule(matchText.toString(), replacementText.toString());
                    rules.add(rule);
                }
            }

            ruleSet = new RuleSet(axiom, rules, directionIncrement);
        } catch (NumberFormatException ex) {
            // This method is called to attempt creating a RuleSet. Getting a bad number
            // simply means that the RuleSet is incomplete.
            return null;
        }

        return ruleSet;
    }

    private void attemptToShowPreview() {
        // Clear out container.
        RelativeLayout relativeScrollContainer = (RelativeLayout) findViewById(R.id.relativeScrollContainer);
        View previewTextView = findViewById(R.id.previewTextView);
        if (fractalView != null) {
            relativeScrollContainer.removeView(fractalView);
            previewTextView.setVisibility(View.GONE);
            fractalView = null;
        }

        // Check if rules are complete.
        final RuleSet ruleSet = createRuleSet();
        if ((ruleSet == null) || !ruleSet.isValid()) {
            return;
        }

        // Add the preview to the UI.
        fractalView = new FractalView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(180, 180);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        layoutParams.setMargins(10, 0, 10, 10);
        fractalView.setLayoutParams(layoutParams);
        fractalView.setBackgroundColor(Color.WHITE);
        fractalView.setElevation(10);
        relativeScrollContainer.addView(fractalView);
        previewTextView.setVisibility(View.VISIBLE);
        previewTextView.bringToFront();

        // Render the preview.
        fractalView.post(new Runnable() {
            @Override
            public void run() {
                new BruteForceRenderAsyncTask(
                        ruleSet,
                        fractalView,
                        2,
                        null,
                        null,
                        RulesActivity.this,
                        null,
                        false).execute();
            }
        });
    }

    /**
     * Check that the rule set is complete for rendering and saving. Show errors if necessary.
     */
    private boolean validate() {
        boolean valid = true;
        String requiredError =
                getResources().getString(R.string.rules_activity_required_value_error);

        // Validate axiom.
        EditText axiomText = (EditText) findViewById(R.id.axiomText);
        if (axiomText.getText().length() == 0) {
            axiomText.setError(requiredError);
            valid = false;
        }

        // Validate direction change.
        valid = valid && validateDirectionIncrement();

        // Validate rules.
        TableLayout rulesTable = (TableLayout) findViewById(R.id.rulesTable);
        List<RuleSet.Rule> rules = new ArrayList<>();
        for (int i = 0; i < rulesTable.getChildCount(); i++) {
            TableRow row = (TableRow) rulesTable.getChildAt(i);
            EditText matchText = (EditText) row.getChildAt(0);
            EditText replacementText = (EditText) row.getChildAt(2);
            if ((matchText.length() == 0) && (replacementText.length() > 0)) {
                matchText.setError(requiredError);
                valid = false;
            }
        }

        return valid;
    }

    private boolean validateDirectionIncrement() {
        EditText directionIncrementText = (EditText) findViewById(R.id.directionIncrementText);

        // Validate direction increment.
        boolean valid = true;
        try {
            String str = directionIncrementText.getText().toString();
            int number = 0;
            number = Integer.parseInt(str);
            valid = (number > 0) && (number < 359);
        } catch (NumberFormatException ex) {
            valid = false;
        }

        // Show error if necessary.
        if (!valid) {
            String errorStr = getResources().getString(R.string.rules_activity_angle_error);
            directionIncrementText.setError(errorStr);
        }

        return valid;
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        RuleSet savedRuleSet = savedInstanceState.getParcelable(RuleSet.EXTRA_RULE_SET);
        rebuildRulesTable(savedRuleSet);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        RuleSet ruleSet = createRuleSet();
        if (ruleSet == null) {
            // Invalid RuleSets are not saved. As a quick little cheat, let's set the angle.
            TextView incrementText = (TextView) findViewById(R.id.directionIncrementText);
            incrementText.setText("90");
            ruleSet = createRuleSet();
        }

        outState.putParcelable(RuleSet.EXTRA_RULE_SET, ruleSet);
    }

    private void logContentViewToFabric(RuleSet intentRuleSet) {
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Viewed L-System rule")
                .putContentType("L-system view")
                .putContentId(intentRuleSet.getName()));
    }

    private class NeatRowWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            TableLayout rulesTable = (TableLayout) findViewById(R.id.rulesTable);
            for (int i = rulesTable.getChildCount() - 1; i >= 0; i--) {
                TableRow row = (TableRow) rulesTable.getChildAt(i);
                EditText matchText = (EditText) row.getChildAt(0);
                EditText replacementText = (EditText) row.getChildAt(2);
                boolean isEmpty =
                        matchText.getText().toString().isEmpty()
                                && replacementText.getText().toString().isEmpty();

                if (i != rulesTable.getChildCount() - 1) {
                    if (isEmpty) {
                        rulesTable.removeView(row);
                    }
                } else {
                    if (!isEmpty) {
                        addRow(rulesTable, "", "");
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    private class PreviewUpdateWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Ignore.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Ignore.
        }

        @Override
        public void afterTextChanged(Editable s) {
            attemptToShowPreview();
        }
    }

    /**
     * TextWatcher for the direction change input. This watcher adds an error to the
     * {@link EditText} if the entered number is outside of the range (1 -> 359).
     */
    private class AngleWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Ignore.
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // Ignore.
        }

        @Override
        public void afterTextChanged(Editable s) {
            validateDirectionIncrement();
        }
    }
}

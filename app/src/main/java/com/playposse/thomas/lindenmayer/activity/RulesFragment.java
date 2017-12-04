package com.playposse.thomas.lindenmayer.activity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.activity.common.ActivityNavigator;
import com.playposse.thomas.lindenmayer.contentprovider.LindenmayerContentContract.RuleSetTable;
import com.playposse.thomas.lindenmayer.contentprovider.QueryHelper;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.glide.GlideApp;
import com.playposse.thomas.lindenmayer.glide.RuleSetResource;
import com.playposse.thomas.lindenmayer.ui.ColorPaletteAdapter;
import com.playposse.thomas.lindenmayer.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The content {@link Fragment} for the {@link RulesActivity}.
 */
public class RulesFragment extends Fragment {

    private static final String RULE_SET_NAME_KEY = "ruleSetName";

    @BindView(R.id.go_button) FloatingActionButton goButton;
    @BindView(R.id.axiom_text_view) TextView axiomTextView;
    @BindView(R.id.direction_increment_text_view) TextView incrementTextView;
    @BindView(R.id.relative_scroll_container) RelativeLayout relativeScrollContainer;
    @BindView(R.id.rules_table_layout)TableLayout rulesTableLayout;
    @BindView(R.id.preview_layout) FrameLayout previewLayout;
    @BindView(R.id.preview_text_view) TextView previewTextView;
    @BindView(R.id.preview_image_view) ImageView previewImageView;

    @Nullable
    @BindView(R.id.color_palette_grid) GridView colorPaletteGridView;

    private final PreviewUpdateWatcher previewUpdateWatcher = new PreviewUpdateWatcher();

    private RuleSet intentRuleSet;
    private NeatRowWatcher neatRowWatcher = new NeatRowWatcher();

    /**
     * A rule set name that the user has specified after the intent was created.
     */
    private String overwrittenRuleSetName = null;

    /**
     * A boolean that indicates if the current rule set has been saved. This flag is used by the
     * options menu to determine if the delete action should be shown or not.
     */
    private Boolean isSaved = null;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rules, container, false);

        ButterKnife.bind(this, rootView);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(RULE_SET_NAME_KEY)) {
            overwrittenRuleSetName = savedInstanceState.getString(RULE_SET_NAME_KEY);
        }

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                render();
            }
        });

        if (getActivity().getIntent().hasExtra(RuleSet.EXTRA_RULE_SET)) {
            intentRuleSet = getActivity().getIntent().getParcelableExtra(RuleSet.EXTRA_RULE_SET);
            populateUi();
            logContentViewToFabric(intentRuleSet);
        } else {
            intentRuleSet = null;
            clearUi();
        }

        axiomTextView.addTextChangedListener(previewUpdateWatcher);
        axiomTextView.requestFocus();

        incrementTextView.addTextChangedListener(new AngleWatcher());
        incrementTextView.addTextChangedListener(previewUpdateWatcher);

        if ((intentRuleSet != null) && !StringUtil.isEmpty(getRuleSetName())) {
            getActivity().setTitle(getRuleSetName());
        }

        // This is only visible on landscape tablets.
        if (colorPaletteGridView != null) {
            colorPaletteGridView.setAdapter(new ColorPaletteAdapter(getActivity()));
        }

        checkIfSaved();

        attemptToShowPreview();

        return rootView;
    }

    private void populateUi() {
        axiomTextView.setText(intentRuleSet.getAxiom());
        incrementTextView.setText("" + intentRuleSet.getDirectionIncrement());

        rebuildRulesTable(intentRuleSet);
    }

    private void rebuildRulesTable(RuleSet ruleSet) {
        rulesTableLayout.removeAllViewsInLayout();
        if ((ruleSet != null) && (ruleSet.getRules() != null)) {
            for (RuleSet.Rule rule : ruleSet.getRules()) {
                addRow(rulesTableLayout, rule.getMatch(), rule.getReplacement());
            }
        }

        addRow(rulesTableLayout, "", "");
    }

    private void clearUi() {
        axiomTextView.setText("");
        incrementTextView.setText("");
        rulesTableLayout.removeAllViewsInLayout();
        addRow(rulesTableLayout, "", "");
    }

    private TableRow addRow(TableLayout rulesTable, String match, String replacement) {
        TableRow row = new TableRow(getActivity());
        TableRow.LayoutParams rowLayoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);
        row.setLayoutParams(rowLayoutParams);

        EditText matchText = new EditText(getActivity());
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

        ImageView arrowImage = new ImageView(getActivity());
        TableRow.LayoutParams arrowImageLayoutParams = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        arrowImageLayoutParams.gravity = Gravity.CENTER;
        arrowImage.setLayoutParams(arrowImageLayoutParams);
        arrowImage.setImageResource(R.drawable.ic_forward_black_24dp);
        row.addView(arrowImage);

        EditText replacementText = new EditText(getActivity());
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

        ActivityNavigator.startRenderActivity(getActivity(), ruleSet);
    }

    protected RuleSet createRuleSet() {
        RuleSet ruleSet = null;
        try {
            String axiom = axiomTextView.getText().toString();
            String directionIncrementString = incrementTextView.getText().toString();
            int directionIncrement = Integer.parseInt(directionIncrementString);

            List<RuleSet.Rule> rules = new ArrayList<>();
            for (int i = 0; i < rulesTableLayout.getChildCount(); i++) {
                TableRow row = (TableRow) rulesTableLayout.getChildAt(i);
                Editable matchText = ((EditText) row.getChildAt(0)).getText();
                Editable replacementText = ((EditText) row.getChildAt(2)).getText();
                if (matchText.length() > 0) {
                    RuleSet.Rule rule = new RuleSet.Rule(matchText.toString(), replacementText.toString());
                    rules.add(rule);
                }
            }

            ruleSet = new RuleSet(axiom, rules, directionIncrement);
            ruleSet.setName(getRuleSetName());
        } catch (NumberFormatException ex) {
            // This method is called to attempt creating a RuleSet. Getting a bad number
            // simply means that the RuleSet is incomplete.
            return null;
        }

        return ruleSet;
    }

    private void attemptToShowPreview() {
        // Check if rules are complete.
        final RuleSet ruleSet = createRuleSet();
        if ((ruleSet == null) || !ruleSet.isValid()) {
            previewLayout.setVisibility(View.INVISIBLE);
            return;
        }

        previewLayout.setVisibility(View.VISIBLE);

        GlideApp.with(this)
                .load(new RuleSetResource(ruleSet, 4, null, true))
                .into(previewImageView);
    }

    /**
     * Check that the rule set is complete for rendering and saving. Show errors if necessary.
     */
    protected boolean validate() {
        boolean valid = true;
        String requiredError =
                getResources().getString(R.string.rules_activity_required_value_error);

        // Validate axiom.
        if (axiomTextView.getText().length() == 0) {
            axiomTextView.setError(requiredError);
            valid = false;
        }

        // Validate direction change.
        valid = valid && validateDirectionIncrement();

        // Validate rules.
        for (int i = 0; i < rulesTableLayout.getChildCount(); i++) {
            TableRow row = (TableRow) rulesTableLayout.getChildAt(i);
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

        // Validate direction increment.
        boolean valid = true;
        try {
            String str = incrementTextView.getText().toString();
            int number = 0;
            number = Integer.parseInt(str);
            valid = (number > 0) && (number < 359);
        } catch (NumberFormatException ex) {
            valid = false;
        }

        // Show error if necessary.
        if (!valid) {
            String errorStr = getResources().getString(R.string.rules_activity_angle_error);
            incrementTextView.setError(errorStr);
        }

        return valid;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if ((savedInstanceState != null)
                && savedInstanceState.containsKey(RuleSet.EXTRA_RULE_SET)) {
            RuleSet savedRuleSet = savedInstanceState.getParcelable(RuleSet.EXTRA_RULE_SET);
            rebuildRulesTable(savedRuleSet);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        RuleSet ruleSet = createRuleSet();
        if (ruleSet == null) {
            // Invalid RuleSets are not saved. As a quick little cheat, let's set the angle.
            incrementTextView.setText("90");
            ruleSet = createRuleSet();
        }

        outState.putParcelable(RuleSet.EXTRA_RULE_SET, ruleSet);
        outState.putString(RULE_SET_NAME_KEY, getRuleSetName());
    }

    private void logContentViewToFabric(RuleSet intentRuleSet) {
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Viewed L-System rule")
                .putContentType("L-system view")
                .putContentId(getRuleSetName()));
    }

    public String getRuleSetName() {
        if (!StringUtil.isEmpty(overwrittenRuleSetName)) {
            return overwrittenRuleSetName;
        } else if ((intentRuleSet != null) && (!StringUtil.isEmpty(intentRuleSet.getName()))) {
            return intentRuleSet.getName();
        } else {
            return null;
        }
    }

    public void setRuleSetName(String ruleSetName) {
        overwrittenRuleSetName = ruleSetName;
    }

    public Boolean isSaved() {
        return isSaved;
    }

    /**
     * Checks if the rule set has been saved and updates {@link #isSaved}.
     */
    @SuppressLint("StaticFieldLeak")
    void checkIfSaved() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                if ((getActivity() == null) || (getActivity().getContentResolver() == null)) {
                    return null;
                }

                String ruleSetName = getRuleSetName();
                if (StringUtil.isEmpty(ruleSetName)) {
                    // This is a new rule set or has no valid name for other reasons.
                    return null;
                }

                ContentResolver contentResolver = getActivity().getContentResolver();

                Long ruleSetId = QueryHelper.doesRulSetExistByName(
                        contentResolver,
                        ruleSetName,
                        RuleSetTable.PRIVATE_TYPE);

                isSaved = (ruleSetId != null);

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                getActivity().invalidateOptionsMenu();
            }
        }.execute();
    }

    private class NeatRowWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            for (int i = rulesTableLayout.getChildCount() - 1; i >= 0; i--) {
                TableRow row = (TableRow) rulesTableLayout.getChildAt(i);
                EditText matchText = (EditText) row.getChildAt(0);
                EditText replacementText = (EditText) row.getChildAt(2);
                boolean isEmpty =
                        matchText.getText().toString().isEmpty()
                                && replacementText.getText().toString().isEmpty();

                if (i != rulesTableLayout.getChildCount() - 1) {
                    if (isEmpty) {
                        rulesTableLayout.removeView(row);
                    }
                } else {
                    if (!isEmpty) {
                        addRow(rulesTableLayout, "", "");
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

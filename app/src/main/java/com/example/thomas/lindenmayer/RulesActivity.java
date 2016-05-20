package com.example.thomas.lindenmayer;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thomas.lindenmayer.data.DataReader;
import com.example.thomas.lindenmayer.domain.Fragment;
import com.example.thomas.lindenmayer.domain.RuleSet;
import com.example.thomas.lindenmayer.logic.DimensionProcessor;
import com.example.thomas.lindenmayer.logic.RuleProcessor;
import com.example.thomas.lindenmayer.widgets.SaveView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RulesActivity extends AppCompatActivity {

    private static final String LOG_CAT = RulesActivity.class.getSimpleName();

    private RuleSet intentRuleSet;
    private NeatRowWatcher neatRowWatcher = new NeatRowWatcher();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rules, menu);

        final MenuItem saveMenuItem = menu.findItem(R.id.action_save);
        final SaveView saveView = (SaveView) saveMenuItem.getActionView();
        saveView.addSaveHandler(new SaveView.SaveHandler() {
            @Override
            public void onSave(String fileName) {
                try {
                    saveRuleSet(fileName);
                    saveMenuItem.collapseActionView();
                    Toast toast = Toast.makeText(getApplicationContext(), fileName + " saved.", Toast.LENGTH_SHORT);
                    toast.show();
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(findViewById(R.id.axiomText).getWindowToken(), 0);
                } catch (IOException | JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });

        MenuItemCompat.setOnActionExpandListener(saveMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                saveView.setText(intentRuleSet.getName());
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });
        return true;
    }

    private void saveRuleSet(String fileName) throws IOException, JSONException {
        intentRuleSet.setName(fileName);
        DataReader.saveUserRuleSets(this, intentRuleSet);

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
        } else {
            intentRuleSet = null;
            clearUi();
        }

        findViewById(R.id.axiomText).requestFocus();
    }

    private void populateUi() {
        TextView axiomText = (TextView) findViewById(R.id.axiomText);
        TextView incrementText = (TextView) findViewById(R.id.directionIncrementText);
        TableLayout rulesTable = (TableLayout) findViewById(R.id.rulesTable);

        axiomText.setText(intentRuleSet.getAxiom());
        incrementText.setText("" + intentRuleSet.getDirectionIncrement());

        rulesTable.removeAllViewsInLayout();
        for (RuleSet.Rule rule : intentRuleSet.getRules()) {
            addRow(rulesTable, rule.getMatch(), rule.getReplacement());
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
                40,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        matchText.setLayoutParams(matchTextLayoutParams);
        matchText.setText(match);
        matchText.addTextChangedListener(neatRowWatcher);
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
        replacementText.addTextChangedListener(neatRowWatcher);
        row.addView(replacementText);

        TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.MATCH_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        rulesTable.addView(row, tableLayoutParams);
        return row;
    }

    private void render() {
        RuleSet ruleSet = createRuleSet();

        Intent intent = new Intent(this, RenderingActivity.class);
        intent.putExtra(RuleSet.EXTRA_RULE_SET, ruleSet);
        startActivity(intent);
    }

    private RuleSet createRuleSet() {
        String axiom = ((TextView) findViewById(R.id.axiomText)).getText().toString();
        int directionIncrement = Integer.parseInt(((TextView) findViewById(R.id.directionIncrementText)).getText().toString());
        TableLayout rulesTable = (TableLayout) findViewById(R.id.rulesTable);

        List<RuleSet.Rule> rules = new ArrayList<>();
        for (int i = 0; i < rulesTable.getChildCount(); i++) {
            TableRow row = (TableRow) rulesTable.getChildAt(i);
            EditText matchText = (EditText) row.getChildAt(0);
            EditText replacementText = (EditText) row.getChildAt(2);
            RuleSet.Rule rule = new RuleSet.Rule(matchText.getText().toString(), replacementText.getText().toString());
            rules.add(rule);
        }

        RuleSet ruleSet = new RuleSet(axiom, rules, directionIncrement);

        return ruleSet;
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
}

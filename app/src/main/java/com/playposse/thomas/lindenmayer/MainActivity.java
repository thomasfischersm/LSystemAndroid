package com.playposse.thomas.lindenmayer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.playposse.thomas.lindenmayer.data.DataReader;
import com.playposse.thomas.lindenmayer.domain.RuleSet;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_CAT = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);

        Button videoTutorialButton = (Button) findViewById(R.id.videoTutorialButton);
        videoTutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/bruY40g_S2w")));
            }
        });

        Button newButton = (Button) findViewById(R.id.newButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRulesActivity();
            }
        });

        Button turtleTutorialButton = (Button) findViewById(R.id.turtleTutorialButton);
        turtleTutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TurtleTrainingActivity.class));
            }
        });


        Log.i(LOG_CAT, "MainActivity.onCreate finished");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Sometimes onCreate isn't called after the user returns from editing (and saving) a new
        // rule set. So, the onResume method has to rebuild to show any potentially new rule sets.
        try {
            populateRuleSets();
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }

        Log.i(LOG_CAT, "Resume has been called.");
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

    private void populateRuleSets() throws IOException, JSONException {
        List<RuleSet> userDefinedRuleSets = DataReader.readUserRuleSets(this);
        LinearLayout userDefinedLayout = (LinearLayout) findViewById(R.id.userDefinedLayout);
        userDefinedLayout.removeAllViewsInLayout(); Log.i(LOG_CAT, "Got user rules: " + userDefinedRuleSets.size());
        if (userDefinedRuleSets.size() == 0) {
            userDefinedLayout.setVisibility(View.GONE);
            findViewById(R.id.userDefinedLabel).setVisibility((View.GONE));
        } else {
            userDefinedLayout.setVisibility(View.VISIBLE);
            findViewById(R.id.userDefinedLabel).setVisibility((View.VISIBLE));
            addRulesToView(userDefinedRuleSets, userDefinedLayout, true);
        }

        List<RuleSet> sampleRuleSets = DataReader.readSampleRuleSets(getResources());
        LinearLayout samplesLayout = (LinearLayout) findViewById(R.id.sampleLayout);
        samplesLayout.removeAllViewsInLayout();
        addRulesToView(sampleRuleSets, samplesLayout, false);
    }

    private void addRulesToView(List<RuleSet> ruleSets, LinearLayout samplesLayout, boolean deleteable) {
        final DeleteActionMode deleteActionMode = deleteable ? new DeleteActionMode() : null;

        for (final RuleSet ruleSet : ruleSets) {
            Button button = new Button(this);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            button.setLayoutParams(layoutParams);
            button.setText(ruleSet.getName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RulesActivity.class);
                    intent.putExtra(RuleSet.EXTRA_RULE_SET, ruleSet);
                    startActivity(intent);
                }
            });
            if (deleteable) {
                button.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        view.setSelected(true);
                        view.setPressed(true);
                        deleteActionMode.setSelectedButton((Button) view);
                        startActionMode(deleteActionMode);
                        return true;
                    }
                });
            }
            samplesLayout.addView(button);
        }
    }

    private void openRulesActivity() {
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }

    private class DeleteActionMode implements ActionMode.Callback {

        private Button selectedButton;

        public void setSelectedButton(Button selectedButton) {
            this.selectedButton = selectedButton;
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.delete_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            String deleteActionTitle =
                    String.format(getResources().getString(R.string.delete_action_title), selectedButton.getText());
            mode.setTitle(deleteActionTitle);
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    try {
                        String name = selectedButton.getText().toString();
                        DataReader.deleteUserRuleSet(getApplicationContext(), name);
                        String deleteToastString = String.format(getResources().getString(R.string.delete_toast), name);
                        Toast toast = Toast.makeText(getApplicationContext(), deleteToastString, Toast.LENGTH_SHORT);
                        toast.show();
                    } catch (IOException|JSONException ex) {
                        ex.printStackTrace();
                    }
                    selectedButton.setSelected(false);
                    selectedButton.setPressed(false);
                    mode.finish();
                    try {
                        populateRuleSets();
                    } catch (IOException|JSONException ex) {
                        ex.printStackTrace();
                    }
                    return true;
            }

            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
        }
    }
}

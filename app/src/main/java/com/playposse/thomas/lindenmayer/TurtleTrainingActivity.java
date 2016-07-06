package com.playposse.thomas.lindenmayer;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.playposse.thomas.lindenmayer.data.AppPreferences;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.widgets.BruteForceRenderAsyncTask;
import com.playposse.thomas.lindenmayer.widgets.FractalView;

import java.util.ArrayList;

/**
 * An activity that trains the user to use turtle graphics. The upper half of the screen is the
 * drawing area for the turtle. The lower half offers buttons for each turtle action. The middle
 * of the screen is the instruction string for the turtle.
 */
public class TurtleTrainingActivity extends AppCompatActivity {

    private static final String LOG_CAT = TurtleTrainingActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turtle_training);

        Toolbar toolbar = (Toolbar) findViewById(R.id.turtleTutorialToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addButtonClickHandler(R.id.drawLineButton, "f");
        addButtonClickHandler(R.id.invisibleLineButton, "F");
        addButtonClickHandler(R.id.plusButton, "+");
        addButtonClickHandler(R.id.minusButton, "-");
        addButtonClickHandler(R.id.nextColorButton, "<");
        addButtonClickHandler(R.id.previousColorButton, ">");
        addButtonClickHandler(R.id.increaseStrokeWidthButton, "{");
        addButtonClickHandler(R.id.decreaseStrokeWidthButton, "}");
        addButtonClickHandler(R.id.redButton, "c18");
        addButtonClickHandler(R.id.greenButton, "c10");
        addButtonClickHandler(R.id.blackButton, "c0");
        addButtonClickHandler(R.id.turnAroundButton, "|");

        EditText turtleInstructionsText = (EditText) findViewById(R.id.turtleInstructionsEditText);
        turtleInstructionsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Nothing to do.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Nothing to do.
            }

            @Override
            public void afterTextChanged(Editable s) {
                redrawFractal();
            }
        });

        showDialogIfNecessary();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.turtle_tutorial_menu, menu);
        return true;
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

    private void showDialogIfNecessary() {
        Log.i(LOG_CAT, "preference check: " + AppPreferences.getShowTurtleTutorialDialog(this));
        if (AppPreferences.getShowTurtleTutorialDialog(this)) {
            Log.i(LOG_CAT, "Going to show dialog.");
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            dialogBuilder.setTitle(R.string.turtle_trainer_dialog_title);
            dialogBuilder.setMessage(R.string.turtle_Trainer_Dialog_message);
            dialogBuilder.setPositiveButton(R.string.dialog_continue_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    AppPreferences.setShowTurtleTutorialDialog(TurtleTrainingActivity.this, false);
                }
            });
            dialogBuilder.show();
        }
    }

    private void addButtonClickHandler(int viewId, final String str) {
        Button button = (Button) findViewById(viewId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText turtleInstructionsText =
                        (EditText) findViewById(R.id.turtleInstructionsEditText);
                turtleInstructionsText.getText().append(str);
            }
        });
    }

    private void redrawFractal() {
        EditText turtleInstructionsText = (EditText) findViewById(R.id.turtleInstructionsEditText);
        String axiom = turtleInstructionsText.getText().toString();

        RuleSet ruleSet = new RuleSet(axiom, new ArrayList<RuleSet.Rule>(), 60);

        FractalView fractalView = (FractalView) findViewById(R.id.turtlePlayArea);
        new BruteForceRenderAsyncTask(ruleSet, fractalView, 1, null, null, this, null, false)
                .execute();
    }
}
// Learn by experimenting how letters are translated to drawing.
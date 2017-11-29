package com.playposse.thomas.lindenmayer.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.data.AppPreferences;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.ui.BruteForceRenderAsyncTask;
import com.playposse.thomas.lindenmayer.ui.FractalView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The content {@link Fragment} for the {@link TurtleTrainingActivity}.
 */
public class TurtleTrainingFragment extends Fragment {

    @BindView(R.id.turtle_instructions_edit_text) EditText turtleInstructionsText;
    @BindView(R.id.turtle_play_area) FractalView fractalView;

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_turtle_training, container, false);

        ButterKnife.bind(this, rootView);

        addButtonClickHandler(R.id.draw_line_button, "f");
        addButtonClickHandler(R.id.invisible_line_button, "F");
        addButtonClickHandler(R.id.plus_button, "+");
        addButtonClickHandler(R.id.minus_button, "-");
        addButtonClickHandler(R.id.next_color_button, "<");
        addButtonClickHandler(R.id.previous_color_button, ">");
        addButtonClickHandler(R.id.increase_stroke_width_button, "{");
        addButtonClickHandler(R.id.decrease_stroke_width_button, "}");
        addButtonClickHandler(R.id.red_button, "c18");
        addButtonClickHandler(R.id.green_button, "c10");
        addButtonClickHandler(R.id.black_button, "c0");
        addButtonClickHandler(R.id.turn_around_button, "|");

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
        return rootView;
    }

    private void showDialogIfNecessary() {
        if (AppPreferences.getShowTurtleTutorialDialog(getActivity())) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setTitle(R.string.turtle_trainer_dialog_title);
            dialogBuilder.setMessage(R.string.turtle_Trainer_Dialog_message);
            dialogBuilder.setPositiveButton(R.string.dialog_continue_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    AppPreferences.setShowTurtleTutorialDialog(getActivity(), false);
                }
            });
            dialogBuilder.show();
        }
    }

    private void addButtonClickHandler(int viewId, final String str) {
        Button button = rootView.findViewById(viewId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                turtleInstructionsText.getText().append(str);
            }
        });
    }

    private void redrawFractal() {
        String axiom = turtleInstructionsText.getText().toString();

        RuleSet ruleSet = new RuleSet(axiom, new ArrayList<RuleSet.Rule>(), 60);

        new BruteForceRenderAsyncTask(
                ruleSet, fractalView,
                1,
                null,
                null, getActivity(),
                null,
                false)
                .execute();
    }
}

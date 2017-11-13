package com.playposse.thomas.lindenmayer.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.data.DataReader;
import com.playposse.thomas.lindenmayer.domain.RuleSet;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link Fragment} that contains the content of the main activity.
 */
public class MainFragment extends Fragment {

    @BindView(R.id.video_tutorial_button) Button videoTutorialButton;
    @BindView(R.id.new_button) Button newButton;
    @BindView(R.id.turtle_tutorial_button) Button turtleTutorialButton;
    @BindView(R.id.user_defined_layout) LinearLayout userDefinedLayout;
    @BindView(R.id.user_defined_label) TextView userDefinedLabel;
    @BindView(R.id.sample_layout) LinearLayout samplesLayout;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        videoTutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityNavigator.startVideoTutorialActivity(getActivity());
            }
        });

        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityNavigator.startNewRuleSetActivity(getActivity());
            }
        });

        turtleTutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityNavigator.startTurtleTrainingActivity(getActivity());
            }
        });

        return rootView;
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
    }

    private void populateRuleSets() throws IOException, JSONException {
        List<RuleSet> userDefinedRuleSets = DataReader.readUserRuleSets(getActivity());
        userDefinedLayout.removeAllViewsInLayout();
        if (userDefinedRuleSets.size() == 0) {
            userDefinedLayout.setVisibility(View.GONE);
            userDefinedLabel.setVisibility((View.GONE));
        } else {
            userDefinedLayout.setVisibility(View.VISIBLE);
            userDefinedLabel.setVisibility((View.VISIBLE));
            addRulesToView(userDefinedRuleSets, userDefinedLayout, true);
        }

        List<RuleSet> sampleRuleSets = DataReader.readSampleRuleSets(getResources());
        samplesLayout.removeAllViewsInLayout();
        addRulesToView(sampleRuleSets, samplesLayout, false);
    }

    private void addRulesToView(List<RuleSet> ruleSets, LinearLayout samplesLayout, boolean deleteable) {
//        final DeleteActionMode deleteActionMode = deleteable ? new DeleteActionMode() : null;

        for (final RuleSet ruleSet : ruleSets) {
            Button button = new Button(getActivity());
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            button.setLayoutParams(layoutParams);
            button.setText(ruleSet.getName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityNavigator.startRuleSetActivity(getActivity(), ruleSet);
                }
            });
//            if (deleteable) {
//                button.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View view) {
//                        view.setSelected(true);
//                        view.setPressed(true);
//                        deleteActionMode.setSelectedButton((Button) view);
//                        getActivity().startActionMode(deleteActionMode);
//                        return true;
//                    }
//                });
//            }
            samplesLayout.addView(button);
        }
    }

//    private class DeleteActionMode implements ActionMode.Callback {
//
//        private Button selectedButton;
//
//        public void setSelectedButton(Button selectedButton) {
//            this.selectedButton = selectedButton;
//        }
//
//        @Override
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            MenuInflater inflater = mode.getMenuInflater();
//            inflater.inflate(R.menu.delete_menu, menu);
//            return true;
//        }
//
//        @Override
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            String deleteActionTitle =
//                    String.format(getResources().getString(R.string.delete_action_title), selectedButton.getText());
//            mode.setTitle(deleteActionTitle);
//            return false;
//        }
//
//        @Override
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.action_delete:
//                    try {
//                        String name = selectedButton.getText().toString();
//                        DataReader.deleteUserRuleSet(getActivity(), name);
//                        String deleteToastString = String.format(getResources().getString(R.string.delete_toast), name);
//                        Toast toast = Toast.makeText(getActivity(), deleteToastString, Toast.LENGTH_SHORT);
//                        toast.show();
//                    } catch (IOException | JSONException ex) {
//                        ex.printStackTrace();
//                    }
//                    selectedButton.setSelected(false);
//                    selectedButton.setPressed(false);
//                    mode.finish();
//                    try {
//                        populateRuleSets();
//                    } catch (IOException | JSONException ex) {
//                        ex.printStackTrace();
//                    }
//                    return true;
//            }
//
//            return false;
//        }
//
//        @Override
//        public void onDestroyActionMode(ActionMode mode) {
//        }
//    }
}

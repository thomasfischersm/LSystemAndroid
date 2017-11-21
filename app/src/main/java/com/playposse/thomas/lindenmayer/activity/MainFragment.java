package com.playposse.thomas.lindenmayer.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.activity.common.ActivityNavigator;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link Fragment} that contains the content of the main activity.
 */
public class MainFragment extends Fragment {

    @BindView(R.id.watch_video_text_view) TextView watchVideoTextView;
    @BindView(R.id.read_help_text_view) TextView readHelpTextView;
    @BindView(R.id.learn_turtle_text_view) TextView learnTurtleTextView;
    @BindView(R.id.check_out_samples_text_view) TextView checkOutSamplesTextView;
    @BindView(R.id.create_your_own_text_view) TextView createYourOwnTextView;
    @BindView(R.id.see_community_contributions_text_view) TextView seeCommunityContributionsTextView;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ButterKnife.bind(this, rootView);

        watchVideoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityNavigator.startVideoTutorialActivity(getActivity());
            }
        });

        readHelpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityNavigator.startHelpActivity(getActivity());
            }
        });

        learnTurtleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityNavigator.startTurtleTrainingActivity(getActivity());
            }
        });

        checkOutSamplesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityNavigator.startSampleLibraryActivity(getActivity());
            }
        });

        createYourOwnTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityNavigator.startNewRuleSetActivity(getActivity());
            }
        });

        seeCommunityContributionsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityNavigator.startPublicLibraryActivity(getActivity());
            }
        });

        return rootView;
    }
}

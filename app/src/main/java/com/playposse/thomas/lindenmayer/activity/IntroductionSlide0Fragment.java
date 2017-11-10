package com.playposse.thomas.lindenmayer.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.playposse.thomas.lindenmayer.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A {@link Fragment} that is shown after the user logs on for the first time. It explains the app
 * to the user.
 */
public class IntroductionSlide0Fragment extends Fragment {

    @BindView(R.id.introduction_image_view) ImageView introductionImageView;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_introduction_slide0, container, false);

        ButterKnife.bind(this, rootView);

        Glide.with(getActivity())
                .load(R.drawable.introduction_image0)
                .into(introductionImageView);

        return rootView;
    }
}

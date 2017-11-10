package com.playposse.thomas.lindenmayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.data.AppPreferences;
import com.playposse.thomas.lindenmayer.util.AnalyticsUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.v4.view.ViewPager.OnPageChangeListener;

/**
 * An {@link Activity} that shows introductory slides to the user after the first user log on.
 */
public class IntroductionActivity extends MinimumActivity {

    private static final String LOG_TAG = IntroductionActivity.class.getSimpleName();

    private static final int SLIDE_COUNT = 3;

    @BindView(R.id.introduction_slide_pager) ViewPager introductionSlidePager;
    @BindView(R.id.get_started_button) Button getStartedButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AppPreferences.hasSeenIntroDeck(this)) {
            finish();
            ActivityNavigator.startMainActivity(getApplicationContext());
            return;
        }

        setContentView(R.layout.activity_introduction);

        ButterKnife.bind(this);

        IntroductionSlidePagerAdapter pagerAdapter =
                new IntroductionSlidePagerAdapter(getSupportFragmentManager());
        introductionSlidePager.setAdapter(pagerAdapter);

        introductionSlidePager.addOnPageChangeListener(new AnalyticsPageChangeListener());

        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreferences.setHasSeenIntroDeck(getApplicationContext(), true);
                finish();
                ActivityNavigator.startMainActivity(getApplicationContext());
            }
        });
    }

    private class IntroductionSlidePagerAdapter extends FragmentPagerAdapter {

        private IntroductionSlidePagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new IntroductionSlide0Fragment();
                case 1:
                    return new IntroductionSlide1Fragment();
                case 2:
                    return new IntroductionSlide2Fragment();
                default:
                    throw new IllegalStateException(
                            "Unexpected introduction deck was requested: " + position);
            }
        }

        @Override
        public int getCount() {
            return SLIDE_COUNT;
        }
    }

    /**
     * A {@link OnPageChangeListener} that reports to Analytics when a new fragment is selected.
     */
    private class AnalyticsPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            // Nothing to do.
        }

        @Override
        public void onPageSelected(int position) {
            String screenName = IntroductionActivity.class.getSimpleName() + position;
            AnalyticsUtil.reportScreenName(IntroductionActivity.this, screenName);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            // Nothing to do.
        }
    }
}

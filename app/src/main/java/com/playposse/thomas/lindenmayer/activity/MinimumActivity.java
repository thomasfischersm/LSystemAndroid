package com.playposse.thomas.lindenmayer.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;
import com.playposse.thomas.lindenmayer.util.AnalyticsUtil;

import io.fabric.sdk.android.Fabric;

/**
 * An {@link Activity} that has the minimum functionality of an activity for this app. Most
 * activities should extend {@link ParentActivity}. However, some activities are outside of the
 * framework of an on-boarded standard user experience. Those activities still need to extend from
 * this class.
 */
public abstract class MinimumActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fabric.with(this, new Crashlytics());
    }

    @Override
    protected void onResume() {
        super.onResume();

        AnalyticsUtil.reportScreenName(this);
    }
}

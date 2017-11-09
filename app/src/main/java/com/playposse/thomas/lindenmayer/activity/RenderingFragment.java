package com.playposse.thomas.lindenmayer.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.playposse.thomas.lindenmayer.AnalyticsUtil;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.widgets.BruteForceRenderAsyncTask;
import com.playposse.thomas.lindenmayer.widgets.FractalView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The content {@link Fragment} for the {@link RenderingActivity}.
 */
public class RenderingFragment extends Fragment {

    private static final String LOG_TAG = RenderingFragment.class.getSimpleName();

    @BindView(R.id.rendering_root_view) RelativeLayout renderingRootView;
    @BindView(R.id.decrement_iteration_button) FloatingActionButton decrementButton;
    @BindView(R.id.increment_iteration_button) FloatingActionButton incrementButton;
    @BindView(R.id.render_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;

    private RuleSet ruleSet;
    private int iterationCount = 1;
    private FractalView fractalView;
    private BruteForceRenderAsyncTask asyncTask;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_rendering, container, false);

        ButterKnife.bind(this, rootView);

        Intent intent = getActivity().getIntent();
        ruleSet = intent.getParcelableExtra(RuleSet.EXTRA_RULE_SET);

        fractalView = new FractalView(getActivity());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fractalView.setLayoutParams(layoutParams);
        renderingRootView.addView(fractalView);

        decrementButton.hide();
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iterationCount--;
                if (iterationCount < 1) {
                    iterationCount = 1;
                } else {
                    render();
                    AnalyticsUtil.sendEvent(
                            getActivity().getApplication(),
                            "Action",
                            "DecrementIteration");
                }

                if (iterationCount == 1) {
                    decrementButton.hide();
                }
            }
        });

        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iterationCount++;
                render();
                AnalyticsUtil.sendEvent(
                        getActivity().getApplication(),
                        "Action",
                        "IncrementIteration");

                if (!decrementButton.isShown()) {
                    decrementButton.show();
                }
            }
        });

        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                render();
            }
        });

        if (ruleSet.isStochastic()) {
            Toast.makeText(getActivity(), R.string.stochastic_toast, Toast.LENGTH_LONG)
                    .show();
        }

        return rootView;
    }

    protected void render() {
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false); // Don't show the icon. there already is a progress dialog!

        if ((asyncTask != null) && (!asyncTask.isCancelled())) {
            asyncTask.cancel(true);
        }

        ShareActionProvider shareActionProvider =
                ((RenderingActivity) getActivity()).getShareActionProvider();

        asyncTask = new BruteForceRenderAsyncTask(
                ruleSet,
                fractalView,
                iterationCount,
                shareActionProvider,
                getActivity().getCacheDir(),
                getActivity(),
                swipeRefreshLayout,
                true);
        asyncTask.execute();
    }

    protected RuleSet getRuleSet() {
        return ruleSet;
    }

    protected byte[] getPngBytes() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Bitmap bitmap = fractalView.getDrawingCache();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
        try {
            buffer.flush();
        } catch (IOException ex) {
            Log.e(LOG_TAG, "getPngBytes: Failed to flush PNG bytes before sharing.", ex);
        }
        return buffer.toByteArray();
    }
}

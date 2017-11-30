package com.playposse.thomas.lindenmayer.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.glide.GlideApp;
import com.playposse.thomas.lindenmayer.glide.RuleSetResource;
import com.playposse.thomas.lindenmayer.util.AnalyticsUtil;
import com.playposse.thomas.lindenmayer.util.StringUtil;
import com.playposse.thomas.lindenmayer.ui.ProgressCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * The content {@link Fragment} for the {@link RenderingActivity}.
 */
public class RenderingFragment extends Fragment {

    private static final String LOG_TAG = RenderingFragment.class.getSimpleName();

    private static final String ITERATION_COUNT_KEY = "iterationCount";

    @BindView(R.id.fractal_image_view) ImageView fractalImageView;
    @BindView(R.id.decrement_iteration_button) FloatingActionButton decrementButton;
    @BindView(R.id.increment_iteration_button) FloatingActionButton incrementButton;
    @BindView(R.id.render_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progress_layout) ConstraintLayout progressLayout;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.progress_text_view) TextView progressTextView;

    private RuleSet ruleSet;
    private int iterationCount = 1;

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

        if ((savedInstanceState != null) && (savedInstanceState.containsKey(ITERATION_COUNT_KEY))) {
            iterationCount = savedInstanceState.getInt(ITERATION_COUNT_KEY);
        }

        if (iterationCount <= 1) {
            decrementButton.hide();
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                render();
            }
        });

        if ((ruleSet != null) && !StringUtil.isEmpty(ruleSet.getName())) {
            getActivity().setTitle(ruleSet.getName());
        }

        if (ruleSet.isStochastic()) {
            Toast.makeText(getActivity(), R.string.stochastic_toast, Toast.LENGTH_LONG)
                    .show();
        }

        render();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ITERATION_COUNT_KEY, iterationCount);
    }

    @OnClick(R.id.decrement_iteration_button)
    void onDecrementClicked() {
        iterationCount--;
        if (iterationCount < 1) {
            iterationCount = 1;
        } else {
            render();
            AnalyticsUtil.sendEvent(getActivity().getApplication(), "DecrementIteration");
        }

        if (iterationCount == 1) {
            decrementButton.hide();
        }
    }

    @OnClick(R.id.increment_iteration_button)
    void onIncrementClicked() {
        iterationCount++;
        render();
        AnalyticsUtil.sendEvent(getActivity().getApplication(), "IncrementIteration");

        if (!decrementButton.isShown()) {
            decrementButton.show();
        }

    }

    protected void render() {
        fractalImageView.setVisibility(View.VISIBLE);

        GlideApp.with(this)
                .load(new RuleSetResource(ruleSet, iterationCount, new ProgressCallbackImpl(), false))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(
                            @Nullable GlideException ex,
                            Object model,
                            Target<Drawable> target,
                            boolean isFirstResource) {

                        Log.e(LOG_TAG, "onLoadFailed: Glide failed to render.", ex);

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(
                            Drawable resource,
                            Object model,
                            Target<Drawable> target,
                            DataSource dataSource,
                            boolean isFirstResource) {

                        Log.i(LOG_TAG, "onResourceReady: The fragment got notification that " +
                                "rendering is complete.");

                        fractalImageView.setVisibility(View.VISIBLE);
                        progressLayout.setVisibility(View.GONE);

                        if (ruleSet.isStochastic()) {
                            swipeRefreshLayout.setEnabled(true);
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        return false;
                    }
                })
                .into(fractalImageView);
    }

    protected RuleSet getRuleSet() {
        return ruleSet;
    }

    ImageView getFractalImageView() {
        return fractalImageView;
    }

    protected int getIterationCount() {
        return iterationCount;
    }

    /**
     * A {@link ProgressCallback} that updates the progress indicators in the fragment.
     */
    private class ProgressCallbackImpl implements ProgressCallback {

        private int maxProgress = 0;
        private int currentProgress = 0;

        /**
         * Progress when the UI was updated the last time. Don't update the UI with every progress
         * step. This would be unnecessary and expensive.
         */
        private int lastUpdate = 0;

        private ProgressCallbackImpl() {
            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
            refresh();
        }

        @Override
        public void markProgress() {
            currentProgress++;

            // Update at every percentage point of progress.
            if ((currentProgress - lastUpdate) * 100.0 / maxProgress > 1) {
                refresh();
            }
        }

        @Override
        public void declareMaxProgress(final int maxProgress) {
            this.maxProgress = maxProgress;

            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setMax(maxProgress);
                    }
                });
            }

            refresh();
        }

        private void refresh() {
            lastUpdate = currentProgress;

            FragmentActivity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(LOG_TAG, "run: Updating the render progress.");
                        if (isAdded()) {
                            progressBar.setProgress(currentProgress);

                            String progressStr =
                                    getString(R.string.progress_steps, currentProgress, maxProgress);
                            progressTextView.setText(progressStr);
                        }
                    }
                });
            }
        }
    }
}

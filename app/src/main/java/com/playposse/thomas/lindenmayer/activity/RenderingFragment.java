package com.playposse.thomas.lindenmayer.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;
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
import com.playposse.thomas.lindenmayer.widgets.BruteForceRenderAsyncTask;
import com.playposse.thomas.lindenmayer.widgets.FractalView;
import com.playposse.thomas.lindenmayer.widgets.ProgressCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The content {@link Fragment} for the {@link RenderingActivity}.
 */
public class RenderingFragment extends Fragment {

    private static final String LOG_TAG = RenderingFragment.class.getSimpleName();

    @BindView(R.id.fractal_image_view) ImageView fractalImageView;
    @BindView(R.id.decrement_iteration_button) FloatingActionButton decrementButton;
    @BindView(R.id.increment_iteration_button) FloatingActionButton incrementButton;
    @BindView(R.id.render_swipe_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progress_layout) ConstraintLayout progressLayout;
    @BindView(R.id.progress_bar) ProgressBar progressBar;
    @BindView(R.id.progress_text_view) TextView progressTextView;

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

//        fractalView = new FractalView(getActivity());
//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        fractalView.setLayoutParams(layoutParams);
//        renderingRootView.addView(fractalView);

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

        if ((ruleSet != null) && !StringUtil.isEmpty(ruleSet.getName())) {
            getActivity().setTitle(ruleSet.getName());
        }

        if (ruleSet.isStochastic()) {
            Toast.makeText(getActivity(), R.string.stochastic_toast, Toast.LENGTH_LONG)
                    .show();
        }

        return rootView;
    }

    protected void render() {
//        swipeRefreshLayout.setEnabled(false);
//        swipeRefreshLayout.setRefreshing(false); // Don't show the icon. there already is a progress dialog!
//
//        if ((asyncTask != null) && (!asyncTask.isCancelled())) {
//            asyncTask.cancel(true);
//        }

        ShareActionProvider shareActionProvider =
                ((RenderingActivity) getActivity()).getShareActionProvider();

//        asyncTask = new BruteForceRenderAsyncTask(
//                ruleSet,
//                fractalView,
//                iterationCount,
//                shareActionProvider,
//                getActivity().getCacheDir(),
//                getActivity(),
//                swipeRefreshLayout,
//                true);
//        asyncTask.execute();

        fractalImageView.setVisibility(View.GONE);

        GlideApp.with(this)
                .load(new RuleSetResource(ruleSet, iterationCount, new ProgressCallbackImpl()))
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

                        // TODO: Set share action

                        fractalImageView.setVisibility(View.VISIBLE);
                        progressLayout.setVisibility(View.GONE);

                        return false;
                    }
                })
                .into(fractalImageView);
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
                        progressBar.setProgress(currentProgress);

                        String progressStr =
                                getString(R.string.progress_steps, currentProgress, maxProgress);
                        progressTextView.setText(progressStr);
                    }
                });
            }
        }
    }
}

package com.playposse.thomas.lindenmayer.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import com.playposse.thomas.lindenmayer.AnalyticsUtil;
import com.playposse.thomas.lindenmayer.CommonMenuActions;
import com.playposse.thomas.lindenmayer.HelpActivity;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.widgets.BruteForceRenderAsyncTask;
import com.playposse.thomas.lindenmayer.widgets.FractalView;

import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * An {@link android.app.Activity} that shows the render Lindenmayer System.
 */
public class RenderingActivity
        extends ParentActivity
        implements ShareActionProvider.OnShareTargetSelectedListener {

    private static final String LOG_CAT = RenderingActivity.class.getSimpleName();

    private static final String UNKNOWN_METHOD = "unknown";
    private static final String UNKNOWN_RULE_SET = "unknown";
    private static final String RULE_SET_NAME_EXTRA = "ruleSetName";

    private RuleSet ruleSet;
    private int iterationCount = 1;
    private ShareActionProvider shareActionProvider;
    private FractalView fractalView;
    private BruteForceRenderAsyncTask asyncTask;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.render_menu, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        shareActionProvider.setOnShareTargetSelectedListener(this);

        render();

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_rendering);

        Intent intent = getIntent();
        ruleSet = intent.getParcelableExtra(RuleSet.EXTRA_RULE_SET);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.mainToolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fractalView = new FractalView(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        fractalView.setLayoutParams(layoutParams);
        RelativeLayout rootView = (RelativeLayout) findViewById(R.id.renderingRootView);
        rootView.addView(fractalView);

        final FloatingActionButton decrementButton = (FloatingActionButton) findViewById(R.id.decrementIterationButton);
        decrementButton.hide();
        decrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iterationCount--;
                if (iterationCount < 1) {
                    iterationCount = 1;
                } else {
                    render();
                    AnalyticsUtil.sendEvent(getApplication(), "Action", "DecrementIteration");
                }

                if (iterationCount == 1) {
                    decrementButton.hide();
                }
            }
        });

        FloatingActionButton incrementButton =
                (FloatingActionButton) findViewById(R.id.incrementIterationButton);
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iterationCount++;
                render();
                AnalyticsUtil.sendEvent(getApplication(), "Action", "IncrementIteration");

                if (!decrementButton.isShown()) {
                    decrementButton.show();
                }
            }
        });

        SwipeRefreshLayout swipeRefreshLayout =
                (SwipeRefreshLayout) findViewById(R.id.renderSwipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                render();
            }
        });

        if (ruleSet.isStochastic()) {
            Toast.makeText(this, R.string.stochastic_toast, Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                setShareIntent();
                return false;
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            case R.id.action_refresh:
                render();
                return true;
            case R.id.action_send_feedback:
                CommonMenuActions.sendFeedbackAction(this);
                return true;
            case R.id.action_send_us_your_best:
                try {
                    CommonMenuActions.sendUsYourBest(this, ruleSet);
                } catch (JSONException ex) {
                    Log.e(LOG_CAT, "Failed to execute menu action 'send us your best'", ex);
                }
            default:
                return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }

    private void setShareIntent() {
        // Convert to PNG.
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Bitmap bitmap = fractalView.getDrawingCache();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);

        // Encode in base64.
        String encodedImage = Base64.encodeToString(buffer.toByteArray(), Base64.DEFAULT);

        // Save file to cache.
        File file = new File(getCacheDir(), "screenshot.png");
        file.setReadable(true, false);
        Uri uri = Uri.fromFile(file);

        // Create share intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        if ((ruleSet != null) && (ruleSet.getName() != null)) {
            intent.putExtra(RULE_SET_NAME_EXTRA, ruleSet.getName());
        }
        shareActionProvider.setShareIntent(intent);
    }

    private void render() {
        SwipeRefreshLayout swipeRefreshLayout =
                (SwipeRefreshLayout) findViewById(R.id.renderSwipeRefreshLayout);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false); // Don't show the icon. there already is a progress dialog!

        if ((asyncTask != null) && (!asyncTask.isCancelled())) {
            asyncTask.cancel(true);
        }
        asyncTask = new BruteForceRenderAsyncTask(
                ruleSet,
                fractalView,
                iterationCount,
                shareActionProvider,
                getCacheDir(),
                this,
                swipeRefreshLayout,
                true);
        asyncTask.execute();
    }

    /**
     * Notify Fabric that a rule set has been shared.
     */
    @Override
    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
        final String method;
        if (intent.getComponent() != null) {
            method = intent.getComponent().getPackageName();
        } else {
            method = UNKNOWN_METHOD;
        }

        final String ruleSetName;
        if (intent.hasExtra(RULE_SET_NAME_EXTRA)) {
            ruleSetName = intent.getStringExtra(RULE_SET_NAME_EXTRA);
        } else {
            ruleSetName = UNKNOWN_RULE_SET;
        }

        Answers.getInstance().logShare(new ShareEvent()
                .putMethod(method)
                .putContentName("Share l-system")
                .putContentType("l-system")
                .putContentId(ruleSetName));

        return false;
    }
}

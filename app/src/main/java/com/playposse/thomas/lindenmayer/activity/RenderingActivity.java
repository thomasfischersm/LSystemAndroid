package com.playposse.thomas.lindenmayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import com.playposse.thomas.lindenmayer.CommonMenuActions;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.util.ShareUtil;

import org.json.JSONException;

/**
 * An {@link android.app.Activity} that shows the render Lindenmayer System.
 */
public class RenderingActivity
        extends ParentActivity<RenderingFragment>
        implements ShareActionProvider.OnShareTargetSelectedListener {

    private static final String LOG_TAG = RenderingActivity.class.getSimpleName();

    private static final String UNKNOWN_METHOD = "unknown";
    private static final String UNKNOWN_RULE_SET = "unknown";
    private static final String RULE_SET_NAME_EXTRA = "ruleSetName";

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.render_menu, menu);

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addContentFragment(new RenderingFragment());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (super.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_refresh:
                getContentFragment().render();
                return true;
            case R.id.action_send_us_your_best:
                try {
                    CommonMenuActions.sendUsYourBest(
                            this,
                            getContentFragment().getRuleSet());
                } catch (JSONException ex) {
                    Log.e(LOG_TAG, "Failed to execute menu action 'send us your best'", ex);
                }
                return true;
            case R.id.action_share:
                onShareClicked();
                return true;
            default:
                return false;
        }
    }

    private void onShareClicked() {
        RenderingFragment contentFragment = getContentFragment();
        ImageView fractalImageView = contentFragment.getFractalImageView();
        RuleSet ruleSet = contentFragment.getRuleSet();
        int iterationCount = contentFragment.getIterationCount();

        ShareUtil.share(ruleSet, iterationCount, fractalImageView);
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

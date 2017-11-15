package com.playposse.thomas.lindenmayer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.playposse.thomas.lindenmayer.CommonMenuActions;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.contentprovider.parser.RuleSetConverter;
import com.playposse.thomas.lindenmayer.domain.RuleSet;
import com.playposse.thomas.lindenmayer.firestore.FireStoreSavingChain;
import com.playposse.thomas.lindenmayer.util.ShareUtil;

import org.json.JSONException;

/**
 * An {@link android.app.Activity} that shows the render Lindenmayer System.
 */
public class RenderingActivity extends ParentActivity<RenderingFragment> {

    private static final String LOG_TAG = RenderingActivity.class.getSimpleName();

    @Nullable private RuleSet ruleSet;
    @Nullable private String ruleSetJson;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        FireStoreSavingChain.onActivityResult(
                this,
                requestCode,
                requestCode,
                data,
                getRuleSetJson(),
                getRuleSetName());
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
            case R.id.action_publish:
                FireStoreSavingChain.onPublishClicked(this, getRuleSetName(), getRuleSetJson());
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

    private RuleSet getRuleSet() {
        if (ruleSet == null) {
            ruleSet = getIntent().getParcelableExtra(RuleSet.EXTRA_RULE_SET);
        }
        return ruleSet;
    }

    private String getRuleSetName() {
        return getRuleSet().getName();
    }

    private String getRuleSetJson() {
        if (ruleSetJson == null) {
            ruleSetJson = RuleSetConverter.write(getRuleSet());
        }
        return ruleSetJson;
    }
}

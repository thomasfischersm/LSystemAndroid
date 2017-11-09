package com.playposse.thomas.lindenmayer.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ShareEvent;
import com.playposse.thomas.lindenmayer.CommonMenuActions;
import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.domain.RuleSet;

import org.json.JSONException;

import java.io.File;

/**
 * An {@link android.app.Activity} that shows the render Lindenmayer System.
 */
public class RenderingActivity
        extends ParentActivity<RenderingFragment>
        implements ShareActionProvider.OnShareTargetSelectedListener {

    private static final String LOG_CAT = RenderingActivity.class.getSimpleName();

    private static final String UNKNOWN_METHOD = "unknown";
    private static final String UNKNOWN_RULE_SET = "unknown";
    private static final String RULE_SET_NAME_EXTRA = "ruleSetName";

    private ShareActionProvider shareActionProvider;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.render_menu, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        shareActionProvider.setOnShareTargetSelectedListener(this);

        getContentFragment().render();

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
                    Log.e(LOG_CAT, "Failed to execute menu action 'send us your best'", ex);
                }
            default:
                return false;
        }
    }

    private void setShareIntent() {
        // Convert to PNG.
        byte[] pngBytes = getContentFragment().getPngBytes();

        // Encode in base64.
        String encodedImage = Base64.encodeToString(pngBytes, Base64.DEFAULT);

        // Save file to cache.
        File file = new File(getCacheDir(), "screenshot.png");
        file.setReadable(true, false);
        Uri uri = Uri.fromFile(file);

        // Create share intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        RuleSet ruleSet = getContentFragment().getRuleSet();
        if ((ruleSet != null) && (ruleSet.getName() != null)) {
            intent.putExtra(RULE_SET_NAME_EXTRA, ruleSet.getName());
        }
        shareActionProvider.setShareIntent(intent);
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

    protected ShareActionProvider getShareActionProvider() {
        return shareActionProvider;
    }
}

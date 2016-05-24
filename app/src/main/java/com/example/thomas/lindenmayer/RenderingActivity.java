package com.example.thomas.lindenmayer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.thomas.lindenmayer.domain.RuleSet;
import com.example.thomas.lindenmayer.widgets.FractalView;
import com.example.thomas.lindenmayer.widgets.RenderAsyncTask;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class RenderingActivity extends AppCompatActivity {

    private RuleSet ruleSet;
    private int iterationCount = 1;
    private ShareActionProvider shareActionProvider;
    private FractalView fractalView;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.render_menu, menu);

        MenuItem item = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);

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
                }

                if (iterationCount == 1) {
                    decrementButton.hide();
                }
            }
        });

        FloatingActionButton incrementButton = (FloatingActionButton) findViewById(R.id.incrementIterationButton);
        incrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iterationCount++;
                render();

                if (!decrementButton.isShown()) {
                    decrementButton.show();
                }
            }
        });
//
//        render();
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
            default:
                return false;
        }
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
        shareActionProvider.setShareIntent(intent);
    }

    private void render() {
        new RenderAsyncTask(
                ruleSet,
                fractalView,
                iterationCount,
                shareActionProvider,
                getCacheDir(),
                getApplicationContext()).execute();
    }
}

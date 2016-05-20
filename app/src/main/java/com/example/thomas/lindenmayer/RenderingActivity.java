package com.example.thomas.lindenmayer;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.thomas.lindenmayer.domain.Fragment;
import com.example.thomas.lindenmayer.domain.RuleSet;
import com.example.thomas.lindenmayer.widgets.FractalView;
import com.example.thomas.lindenmayer.widgets.RenderAsyncTask;

public class RenderingActivity extends AppCompatActivity {

    private RuleSet ruleSet;
    private int iterationCount = 1;
    private FractalView fractalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rendering);

        Intent intent = getIntent();
        ruleSet = intent.getParcelableExtra(RuleSet.EXTRA_RULE_SET);

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

        render();
    }

    private void render() {
        new RenderAsyncTask(ruleSet, fractalView, iterationCount).execute();
    }
}

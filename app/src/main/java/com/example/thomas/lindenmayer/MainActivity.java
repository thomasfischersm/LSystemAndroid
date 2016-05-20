package com.example.thomas.lindenmayer;

import android.app.ActionBar;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.thomas.lindenmayer.data.DataReader;
import com.example.thomas.lindenmayer.domain.RuleSet;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button newButton = (Button) findViewById(R.id.newButton);
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRulesActivity();
            }
        });

        try {
            populateSamples();
        } catch (IOException | JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void populateSamples() throws IOException, JSONException {
        List<RuleSet> ruleSets = DataReader.readSampleRuleSets(getResources());
        LinearLayout layout = (LinearLayout) findViewById(R.id.sampleLayout);
        layout.removeAllViewsInLayout();

        for (final RuleSet ruleSet : ruleSets) {
            Button button = new Button(this);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            button.setLayoutParams(layoutParams);
            button.setText(ruleSet.getName());
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), RulesActivity.class);
                    intent.putExtra(RuleSet.EXTRA_RULE_SET, ruleSet);
                    startActivity(intent);
                }
            });
            layout.addView(button);
        }
    }

    private void openRulesActivity() {
        Intent intent = new Intent(this, RulesActivity.class);
        startActivity(intent);
    }
}

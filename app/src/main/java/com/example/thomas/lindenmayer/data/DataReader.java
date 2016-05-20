package com.example.thomas.lindenmayer.data;

import android.content.res.Resources;

import com.example.thomas.lindenmayer.R;
import com.example.thomas.lindenmayer.domain.RuleSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thomas on 5/20/2016.
 */
public class DataReader {

    public static List<RuleSet> readSampleRuleSets(Resources resources) throws IOException, JSONException {
        String jsonString = readResource(resources, R.raw.samples);

        List<RuleSet> ruleSets = new ArrayList<>();
        JSONObject json = new JSONObject(jsonString);
        JSONArray sampleArray = json.getJSONArray("samples");

        for (int i = 0; i < sampleArray.length(); i++) {
            JSONObject sample = sampleArray.getJSONObject(i);
            String name = sample.getString("name");
            String axiom = sample.getString("axiom");
            int directionIncrement = sample.getInt("directionIncrement");

            List<RuleSet.Rule> rules = new ArrayList<>();
            JSONArray rulesArray = sample.getJSONArray("rules");
            for (int j = 0; j < rulesArray.length(); j++) {
                JSONObject rule = rulesArray.getJSONObject(j);
                String match = rule.getString("match");
                String replacement = rule.getString("replacement");
                rules.add(new RuleSet.Rule(match, replacement));
            }

            ruleSets.add(new RuleSet(axiom, directionIncrement, name, rules));
        }

        return ruleSets;
    }

    private static String readResource(Resources resources, int resource) throws IOException {
        InputStream inputStream = resources.openRawResource(resource);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        char[] buffer = new char[512];

        try {
            int length;
            while ((length = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, length);
            }
        } finally {
            reader.close();
        }

        return sb.toString();
    }
}

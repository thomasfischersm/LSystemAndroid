package com.playposse.thomas.lindenmayer.service;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Log;

import com.playposse.thomas.lindenmayer.R;
import com.playposse.thomas.lindenmayer.domain.RuleSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Utility that provides access to stored {@link RuleSet}s (examples and user defined ones. They
 * are stored as JSON on the device.
 *
 * <p>Note: This class must be package private, so that only the {@link ImportRuleSetsService} can
 * access this code. All other code should read from the ContentRepository.
 */
class DataReader {

    private static final String LOG_CAT = DataReader.class.getSimpleName();

    private static final String USER_FILE_NAME = "userDefinedRuleSets.json";

    private static final String SAMPLES_NODE_NAME = "samples";
    private static final String NAME_NODE_NAME = "name";
    private static final String AXIOM_NODE_NAME = "axiom";
    private static final String DIRECTION_INCREMENT_NODE_NAME = "directionIncrement";
    private static final String RULES_NODE_NAME = "rules";
    private static final String MATCH_NODE_NAME = "match";
    private static final String REPLACEMENT_NODE_NAME = "replacement";

    static List<RuleSet> readSampleRuleSets(Resources resources)
            throws IOException, JSONException {

        String jsonString = readResource(resources, R.raw.samples);

        return parseJson(jsonString);
    }

    static List<RuleSet> readUserRuleSets(Context context)
            throws JSONException, IOException {

        try {
            return  parseJson(readUserFile(context));
        } catch (FileNotFoundException e) {
            // The first time, the file needs to be created.
            return new ArrayList<>();
        }
    }

    @NonNull
    private static List<RuleSet> parseJson(String jsonString) throws JSONException {
        List<RuleSet> ruleSets = new ArrayList<>();
        JSONObject json = new JSONObject(jsonString);
        JSONArray sampleArray = json.getJSONArray(SAMPLES_NODE_NAME);

        for (int i = 0; i < sampleArray.length(); i++) {
            try {
                JSONObject sample = sampleArray.getJSONObject(i);
                String name = sample.getString(NAME_NODE_NAME);
                String axiom = sample.getString(AXIOM_NODE_NAME);
                int directionIncrement = sample.getInt(DIRECTION_INCREMENT_NODE_NAME);

                List<RuleSet.Rule> rules = new ArrayList<>();
                JSONArray rulesArray = sample.getJSONArray(RULES_NODE_NAME);
                for (int j = 0; j < rulesArray.length(); j++) {
                    JSONObject rule = rulesArray.getJSONObject(j);
                    String match = rule.getString(MATCH_NODE_NAME);
                    String replacement = rule.getString(REPLACEMENT_NODE_NAME);
                    rules.add(new RuleSet.Rule(match, replacement));
                }

                ruleSets.add(new RuleSet(axiom, directionIncrement, name, rules));
            } catch(JSONException ex) {
                Log.e(LOG_CAT, "Failed to parse JSON with saved rule sets.", ex);
            }
        }

        Collections.sort(ruleSets, new AlphabeticalComparator());
        return ruleSets;
    }

    private static String readResource(Resources resources, int resource) throws IOException {
        InputStream inputStream = resources.openRawResource(resource);
        return readInputStream(inputStream);
    }

    private static String readUserFile(Context context) throws IOException {
        return readInputStream(context.openFileInput(USER_FILE_NAME));
    }

    @NonNull
    private static String readInputStream(InputStream inputStream) throws IOException {
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

    private static void writeUserRuleSets(Context context, List<RuleSet> ruleSets) throws IOException, JSONException {
        String jsonString = generateJson(ruleSets);

        FileOutputStream outputStream = context.openFileOutput(USER_FILE_NAME, Context.MODE_PRIVATE);
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            writer.write(jsonString);
            writer.flush();
        } finally {
            outputStream.close();
        }
    }

    private static String generateJson(List<RuleSet> ruleSets) throws JSONException {
        JSONObject rootNode = new JSONObject();

        JSONArray samplesNode = new JSONArray();
        rootNode.put(SAMPLES_NODE_NAME, samplesNode);

        for (RuleSet ruleSet : ruleSets) {
            JSONObject ruleSetNode = new JSONObject();
            ruleSetNode.put(NAME_NODE_NAME, ruleSet.getName());
            ruleSetNode.put(AXIOM_NODE_NAME, ruleSet.getAxiom());
            ruleSetNode.put(DIRECTION_INCREMENT_NODE_NAME, ruleSet.getDirectionIncrement());
            samplesNode.put(ruleSetNode);

            JSONArray rulesNode = new JSONArray();
            ruleSetNode.put(RULES_NODE_NAME, rulesNode);

            for (RuleSet.Rule rule : ruleSet.getRules()) {
                JSONObject ruleNode = new JSONObject();
                ruleNode.put(MATCH_NODE_NAME, rule.getMatch());
                ruleNode.put(REPLACEMENT_NODE_NAME, rule.getReplacement());
                rulesNode.put(ruleNode);
            }
        }

        return rootNode.toString();
    }

    private static class AlphabeticalComparator implements Comparator<RuleSet> {

        @Override
        public int compare(RuleSet lhs, RuleSet rhs) {
            return lhs.getName().compareTo(rhs.getName());
        }
    }
}

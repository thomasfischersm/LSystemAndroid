package com.playposse.thomas.lindenmayer.contentprovider.parser;

import com.google.gson.Gson;
import com.playposse.thomas.lindenmayer.data.DataReader;
import com.playposse.thomas.lindenmayer.domain.RuleSet;

import java.util.ArrayList;
import java.util.List;

/**
 * A GSON converter for {@link RuleSet}s.
 *
 * <p>This replaces {@link DataReader}. DataReader is kept around to read the legacy JSON files.
 */
public final class RuleSetConverter {

    private RuleSetConverter() {}

    public static RuleSet read(String json) {
        // Parse JSON.
        Gson gson = new Gson();
        RuleSetJson ruleSetJson = gson.fromJson(json, RuleSetJson.class);

        // Create RuleSet object.
        List<RuleSet.Rule> rules = new ArrayList<>();
        if (ruleSetJson.getRules() != null) {
            for (RuleJson ruleJson : ruleSetJson.getRules()) {
                rules.add(new RuleSet.Rule(ruleJson.getMatch(), ruleJson.getReplacement()));
            }
        }

        return new RuleSet(
                ruleSetJson.getAxiom(),
                ruleSetJson.getDirectionIncrement(),
                ruleSetJson.getName(),
                rules);
    }

    public static String write(RuleSet ruleSet) {
        // Create JSON domain object.
        List<RuleJson> ruleJsons = new ArrayList<>();
        if (ruleSet.getRules() != null) {
            for (RuleSet.Rule rule : ruleSet.getRules()) {
                ruleJsons.add(new RuleJson(rule.getMatch(), rule.getReplacement()));
            }
        }
        RuleSetJson ruleSetJson = new RuleSetJson(
                ruleSet.getName(),
                ruleSet.getAxiom(),
                ruleSet.getDirectionIncrement(),
                ruleJsons);

        // Generate JSON.
        Gson gson = new Gson();
        return gson.toJson(ruleSetJson);
    }
}

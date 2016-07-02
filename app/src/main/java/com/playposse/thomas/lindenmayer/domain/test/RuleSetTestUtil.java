package com.playposse.thomas.lindenmayer.domain.test;

import com.playposse.thomas.lindenmayer.domain.RuleSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for using {@link RuleSet}s in unit tests.
 */
public class RuleSetTestUtil {

    public static RuleSet createSampleRuleSet(
            String axiom,
            int directionIncrement,
            String match0,
            String replacement0) {

        RuleSet.Rule rule0 = new RuleSet.Rule(match0, replacement0);
        List<RuleSet.Rule> rules = new ArrayList<>();
        rules.add(rule0);
        return new RuleSet(axiom, rules, directionIncrement);
    }

    public static RuleSet createSampleRuleSet(
            String axiom,
            int directionIncrement,
            String match0,
            String replacement0,
            String match1,
            String replacement1) {

        RuleSet.Rule rule0 = new RuleSet.Rule(match0, replacement0);
        RuleSet.Rule rule1 = new RuleSet.Rule(match1, replacement1);
        List<RuleSet.Rule> rules = new ArrayList<>();
        rules.add(rule0);
        rules.add(rule1);
        return new RuleSet(axiom, rules, directionIncrement);
    }
}

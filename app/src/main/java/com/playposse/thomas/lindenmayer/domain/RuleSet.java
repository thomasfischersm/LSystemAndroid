package com.playposse.thomas.lindenmayer.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Data class that describes the parameters for a Lindenmayer System.
 */
public class RuleSet implements Parcelable {

    public static final String EXTRA_RULE_SET = "com.playposse.thomas.lindenmayer.EXTRA_RULE_SET";

    private final String axiom;
    private final List<Rule> rules;
    private final int directionIncrement;
    private final Random rand = new Random();

    private String name;

    public RuleSet(String axiom, List<Rule> rules, int directionIncrement) {
        this.axiom = axiom;
        this.rules = rules;
        this.directionIncrement = directionIncrement;
    }

    public RuleSet(String axiom, int directionIncrement, String name, List<Rule> rules) {
        this.axiom = axiom;
        this.directionIncrement = directionIncrement;
        this.name = name;
        this.rules = rules;
    }

    protected RuleSet(Parcel in) {
        axiom = in.readString();
        directionIncrement = in.readInt();
        name = in.readString();

        int rulesSize = in.readInt();
        rules = new ArrayList<>();
        for (int i = 0; i < rulesSize; i++) {
            rules.add(new Rule(in.readString(), in.readString()));
        }
    }

    public static final Creator<RuleSet> CREATOR = new Creator<RuleSet>() {
        @Override
        public RuleSet createFromParcel(Parcel in) {
            return new RuleSet(in);
        }

        @Override
        public RuleSet[] newArray(int size) {
            return new RuleSet[size];
        }
    };

    public String getAxiom() {
        return axiom;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean hasRule(String match) {
        for (Rule rule : rules) {
            if (rule.getMatch().equals(match)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the rule that matches the specified string. If there are multiple matching rules,
     * one is picked at random.
     */
    public Rule getRule(String match) {
        List<Rule> possibleRules = new ArrayList<>();
        for (Rule rule : rules) {
            if (rule.getMatch().equals(match)) {
                possibleRules.add(rule);
            }
        }

        if (possibleRules.size() == 0) {
            return null;
        } else {
            return possibleRules.get(rand.nextInt(possibleRules.size()));
        }
    }

    public int getDirectionIncrement() {
        return directionIncrement;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(axiom);
        dest.writeInt(directionIncrement);
        dest.writeString(name);
        dest.writeInt(rules.size());
        for (Rule rule : rules) {
            dest.writeString(rule.getMatch());
            dest.writeString(rule.getReplacement());
        }
    }

    public boolean isValid() {
        if ((axiom == null) || axiom.length() == 0) {
            return false; // Needs an axiom to start.
        }

        if ((directionIncrement < 1) || (directionIncrement > 359)) {
            return false; // Needs to be between 1-359 degrees.
        }

        if (rules.size() == 0) {
            return false; // Needs at least one rule.
        }

        for (Rule rule : rules) {
            if ((rule.getMatch() == null) || rule.getMatch().isEmpty()
                    || (rule.getReplacement() == null) || rule.getReplacement().isEmpty()) {
                continue; // The rule is incomplete.
            }

            if (rule.getMatch().length() != 1) {
                continue; // The matching string is invalid.
            }

            if (!axiom.contains(rule.getMatch())) {
                continue; // The rule is irrelevant to the axiom.
            }

            return true;
        }

        return false;
    }

    /**
     * Checks if any match has multiple rules.
     */
    public boolean isStochastic() {
        List<Rule> possibleRules = new ArrayList<>();
        List<String> matches = new ArrayList<>();
        for (Rule rule : rules) {
            if (matches.contains(rule.getMatch())) {
                return true;
            } else {
                matches.add(rule.getMatch());
            }
        }
        return false;
    }

    public static class Rule {
        private final String match;
        private final String replacement;

        public Rule(String match, String replacement) {
            this.match = match;
            this.replacement = replacement;
        }

        public String getMatch() {
            return match;
        }

        public String getReplacement() {
            return replacement;
        }
    }
}

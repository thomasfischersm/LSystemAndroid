package com.playposse.thomas.lindenmayer.contentprovider.parser;

/**
 * A JSON type that represents a replacement rule.
 */
public class RuleJson {

    private String match;
    private String replacement;

    public RuleJson() {
    }

    public RuleJson(String match, String replacement) {
        this.match = match;
        this.replacement = replacement;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }
}

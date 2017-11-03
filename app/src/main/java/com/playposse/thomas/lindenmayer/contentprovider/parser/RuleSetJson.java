package com.playposse.thomas.lindenmayer.contentprovider.parser;

import com.playposse.thomas.lindenmayer.domain.RuleSet;

import java.util.List;

/**
 * A JSON type that represents a {@link RuleSet}.
 */
public class RuleSetJson {

    private String name;
    private String axiom;
    private int directionIncrement;
    private List<RuleJson> rules;

    public RuleSetJson() {
    }

    public RuleSetJson(String name, String axiom, int directionIncrement, List<RuleJson> rules) {
        this.name = name;
        this.axiom = axiom;
        this.directionIncrement = directionIncrement;
        this.rules = rules;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAxiom() {
        return axiom;
    }

    public void setAxiom(String axiom) {
        this.axiom = axiom;
    }

    public int getDirectionIncrement() {
        return directionIncrement;
    }

    public void setDirectionIncrement(int directionIncrement) {
        this.directionIncrement = directionIncrement;
    }

    public List<RuleJson> getRules() {
        return rules;
    }

    public void setRules(List<RuleJson> rules) {
        this.rules = rules;
    }
}

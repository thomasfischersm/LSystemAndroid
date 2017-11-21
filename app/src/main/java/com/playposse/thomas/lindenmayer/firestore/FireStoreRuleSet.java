package com.playposse.thomas.lindenmayer.firestore;

/**
 * A data class that represents a public L-System rule set in the Firestore.
 */
class FireStoreRuleSet {

    private String name;
    private String ruleSet;
    private String creatorId;
    private String creatorName;

    FireStoreRuleSet() {
    }

    FireStoreRuleSet(String name, String ruleSet, String creatorId, String creatorName) {
        this.name = name;
        this.ruleSet = ruleSet;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRuleSet() {
        return ruleSet;
    }

    public void setRuleSet(String ruleSet) {
        this.ruleSet = ruleSet;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}

package com.playposse.thomas.lindenmayer.firestore.data;

/**
 * A data class that represents a like.
 */
public class FireLike {

    private String ruleSetId;
    private String userId;

    public FireLike() {
    }

    public FireLike(String ruleSetId, String userId) {
        this.ruleSetId = ruleSetId;
        this.userId = userId;
    }

    public String getRuleSetId() {
        return ruleSetId;
    }

    public void setRuleSetId(String ruleSetId) {
        this.ruleSetId = ruleSetId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String computeKey() {
        return ruleSetId + "_" + userId;
    }
}

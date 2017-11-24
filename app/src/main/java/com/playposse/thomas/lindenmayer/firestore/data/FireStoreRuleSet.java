package com.playposse.thomas.lindenmayer.firestore.data;

import android.support.annotation.Nullable;

/**
 * A data class that represents a public L-System rule set in the Firestore.
 */
public class FireStoreRuleSet {

    private String name;
    private String ruleSet;
    private String creatorId;
    private String creatorName;
    @Nullable private String creatorPhotoUrl;

    FireStoreRuleSet() {
    }

    public FireStoreRuleSet(
            String name,
            String ruleSet,
            String creatorId,
            String creatorName,
            @Nullable String creatorPhotoUrl) {

        this.name = name;
        this.ruleSet = ruleSet;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.creatorPhotoUrl = creatorPhotoUrl;
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

    public String getCreatorPhotoUrl() {
        return creatorPhotoUrl;
    }

    public void setCreatorPhotoUrl(String creatorPhotoUrl) {
        this.creatorPhotoUrl = creatorPhotoUrl;
    }
}

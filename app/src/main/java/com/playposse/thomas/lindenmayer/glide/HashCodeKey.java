package com.playposse.thomas.lindenmayer.glide;

import android.util.Log;

import com.bumptech.glide.load.Key;
import com.google.common.base.Objects;
import com.playposse.thomas.lindenmayer.domain.RuleSet;

import java.security.MessageDigest;

/**
 * A {@link Key} that uses the hashCode of a {@link RuleSet}. The hashCode is unique to the
 * rule set parameters. Thus, if the user changes the rule set, the hashCode changes and the
 * image no longer hits the Glide cache.
 */
public class HashCodeKey implements Key {

    private static final String LOG_TAG = HashCodeKey.class.getSimpleName();

    private final int hashCode;

    HashCodeKey(RuleSet ruleSet, int iterationCount) {
        this.hashCode = Objects.hashCode(ruleSet, iterationCount);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(Integer.toString(hashCode).getBytes());
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof HashCodeKey) {
            int otherHashCode = ((HashCodeKey) other).hashCode;
            Log.d(LOG_TAG, "equals: Got asked to compare key hash: " + hashCode + " vs "
                    + otherHashCode
            + " (" + (hashCode == otherHashCode) + ")"
            + " (" + (this == other) + ")");
            return hashCode == otherHashCode;
        } else {
            return super.equals(other);
        }
    }
}

package com.playposse.thomas.lindenmayer.glide;

import com.bumptech.glide.load.Key;

import java.security.MessageDigest;

/**
 * A {@link Key} that is random to prevent the resource from being cached.
 */
public class RandomKey implements Key {

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(Integer.toString(hashCode()).getBytes());
    }
}

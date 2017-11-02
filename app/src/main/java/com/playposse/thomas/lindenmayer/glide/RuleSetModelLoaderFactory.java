package com.playposse.thomas.lindenmayer.glide;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

/**
 * A custom {@link ModelLoaderFactory} to load images for RuleSets.
 */
public class RuleSetModelLoaderFactory implements ModelLoaderFactory<RuleSetResource, InputStream> {

    @Override
    public ModelLoader<RuleSetResource, InputStream> build(MultiModelLoaderFactory multiFactory) {
        return new RuleSetModelLoader();
    }

    @Override
    public void teardown() {

    }
}

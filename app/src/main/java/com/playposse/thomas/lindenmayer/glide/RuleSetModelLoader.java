package com.playposse.thomas.lindenmayer.glide;

import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.playposse.thomas.lindenmayer.domain.RuleSet;

import java.io.InputStream;

/**
 * A {@link ModelLoader} for {@link RuleSet}s.
 */
public class RuleSetModelLoader implements ModelLoader<RuleSetResource, InputStream> {

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(
            RuleSetResource ruleSetResource,
            int width,
            int height,
            Options options) {

        return new LoadData<InputStream>(
                ruleSetResource.getKey(),
                new RuleSetDataFetcher(ruleSetResource, width, height));
    }

    @Override
    public boolean handles(RuleSetResource ruleSetResource) {
        return true;
    }
}

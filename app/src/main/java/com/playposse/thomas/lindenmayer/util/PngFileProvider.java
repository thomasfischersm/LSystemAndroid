package com.playposse.thomas.lindenmayer.util;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

/**
 * A {@link FileProvider} that hardcodes the content type to "image/png." Because the Glide
 * cache files don't have a file extension, the default {@link FileProvider} doesn't figure out the
 * right content type.
 */
public class PngFileProvider extends FileProvider {

    @Override
    public String getType(@NonNull Uri uri) {
        return "image/png";
    }
}

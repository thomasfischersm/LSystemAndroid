package com.playposse.thomas.lindenmayer.activity;

/**
 * An enum to keep track of all the loader constants.
 */
public enum Loaders {
    samplesLoader(1),
    privateLibraryLoader(2),
    publicLibraryLoader(3);
    private final int loaderId;

    Loaders(int loaderId) {
        this.loaderId = loaderId;
    }

    public int getLoaderId() {
        return loaderId;
    }
}

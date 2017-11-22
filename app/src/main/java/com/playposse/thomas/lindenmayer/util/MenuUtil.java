package com.playposse.thomas.lindenmayer.util;

import android.view.Menu;
import android.view.MenuItem;

/**
 * A utility for dealing with toolbar menus.
 */
public final class MenuUtil {

    private MenuUtil() {}

    public static void setMenuItemVisibility(Menu menu, int actionId, boolean shouldBeVisible) {
        if (shouldBeVisible) {
            return;
        }

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.getItemId() == actionId) {
                menuItem.setVisible(false);
            }
        }
    }
}

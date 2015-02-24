package me.tatarka.simplefragment.util;

import android.content.res.Resources;

/**
 * Created by evan on 3/12/15.
 */
public class ResUtil {
    public static String safeGetIdName(Resources resources, int id) {
        if (resources == null) {
            return String.valueOf(id);
        }
        try {
            return resources.getResourceEntryName(id);
        } catch (Resources.NotFoundException e) {
            return String.valueOf(id);
        }
    }
}

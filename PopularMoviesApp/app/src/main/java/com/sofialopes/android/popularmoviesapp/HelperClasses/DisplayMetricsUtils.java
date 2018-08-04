package com.sofialopes.android.popularmoviesapp.HelperClasses;

import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 *
 * Created by Sofia on 2/24/2018.
 */

public class DisplayMetricsUtils {
    private DisplayMetricsUtils(){}

    private static DisplayMetrics getDisplayMetrics(){
        return Resources.getSystem().getDisplayMetrics();
    }

    public static float getDensity(){
        return getDisplayMetrics().density;
    }

    public static int getWidthPixels(){
        return getDisplayMetrics().widthPixels;
    }

    public static int getNumberOfColumns(){
        int dpWidth = (int) (getWidthPixels() / getDensity());
        return (dpWidth / 180) + 1;
    }
}

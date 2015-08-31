package com.smurph.recyclerviewmultiselect;

import android.os.Build;

/**
 * Created by ben on 8/30/15.
 */
public class Config {

    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static boolean isAboveOrEqualAPILvl(int apiLvl) {
        return Build.VERSION.SDK_INT >= apiLvl;
    }
}

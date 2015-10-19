package com.smurph.recyclerviewmultiselect;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;

/**
 * Created by ben on 8/30/15.
 */
public class Config {

    public static final boolean DEBUG = BuildConfig.DEBUG;

    public static boolean isAboveOrEqualAPILvl(int apiLvl) {
        return Build.VERSION.SDK_INT >= apiLvl;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public static int getResourceColor(@NonNull Context context, @ColorRes int id) {
        if (Config.isAboveOrEqualAPILvl(Build.VERSION_CODES.M)) {
            return context.getColor(id);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(id);
        }
    }
}

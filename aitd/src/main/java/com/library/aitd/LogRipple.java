package com.library.aitd;

import android.util.Log;

public class LogRipple {

    static boolean enable = BuildConfig.DEBUG;

    public static void enableLog(boolean log) {
        enable = log;
    }

    public static void e(String tag, Object content) {
        if (enable) {
            Log.e(tag, content.toString());
        }
    }

}

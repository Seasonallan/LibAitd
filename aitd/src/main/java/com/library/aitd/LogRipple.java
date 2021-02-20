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
            System.out.println(content.toString());
        }
    }

    public static void error(String tag, Exception e) {
        if (enable && e != null) {
            Log.e(tag, e.getMessage());
            System.out.println(e.getMessage());
        }
    }

}

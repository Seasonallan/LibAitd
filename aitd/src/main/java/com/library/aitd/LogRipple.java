package com.library.aitd;

import android.util.Log;

public class LogRipple {
    public static void e(String tag, Object content){
        if (BuildConfig.DEBUG){
            Log.e(tag, content.toString());
        }
    }

}

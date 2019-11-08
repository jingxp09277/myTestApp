package com.ask.commonLib;


import android.util.Log;


public final class SLog {

    private static boolean isLoggable = BuildConfig.DEBUG;

    public static void d(String tag, String msg) {
        if (isLoggable && msg != null)
            Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        if (isLoggable && msg != null)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        if (isLoggable && msg != null)
            Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (isLoggable && msg != null)
            Log.e(tag, msg);
    }

    public static void enableLog(boolean b) {
        isLoggable = b;
    }
}

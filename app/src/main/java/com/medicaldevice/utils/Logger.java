package com.medicaldevice.utils;

import android.util.Log;

public class Logger {
    public static final String TAG = "MEDICAL-DEVICE";

    private Logger() {
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        d(tag, msg, null);
    }

    public static void d(String tag, String msg, Throwable tr) {
        Log.d(tag, msg, tr);
    }

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void e(String tag, String msg) {
        e(tag, msg, null);
    }

    public static void e(String tag, String msg, Throwable tr) {
        Log.e(tag, msg, tr);
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        i(tag, msg, null);
    }

    public static void i(String tag, String msg, Throwable tr) {
        Log.i(tag, msg, tr);
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void v(String tag, String msg) {
        v(tag, msg, null);
    }

    public static void v(String tag, String msg, Throwable tr) {
        Log.v(tag, msg, tr);
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void w(String tag, String msg) {
        w(tag, msg, null);
    }

    public static void w(String tag, String msg, Throwable tr) {
        Log.w(tag, msg, tr);
    }

    public static void wtf(String msg) {
        w(TAG, msg);
    }

    public static void wtf(String tag, String msg) {
        w(tag, msg, null);
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        Log.wtf(tag, msg, tr);
    }
}

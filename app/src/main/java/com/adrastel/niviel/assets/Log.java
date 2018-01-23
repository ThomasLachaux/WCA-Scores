package com.adrastel.niviel.assets;

@SuppressWarnings("unused")
public class Log {

    private static String TAG = "niviel";

    public static void d(String message) {
        android.util.Log.d(TAG, message);
    }

    public static void d(String tag, String message) {
        android.util.Log.d(TAG, tag + ": " + message);
    }


    public static void e(String message)  {
        android.util.Log.e(TAG, message);
    }
    public static void e(String tag, String message) {
        android.util.Log.e(TAG, tag + ": " + message);
    }

    public static void i(String message)  {
        android.util.Log.i(TAG, message);
    }
    public static void i(String tag, String message) {
        android.util.Log.i(TAG, tag + ": " + message);
    }

    public static void v(String message)  {
        android.util.Log.v(TAG, message);
    }
    public static void v(String tag, String message) {
        android.util.Log.v(TAG, tag + ": " + message);
    }

    public static void w(String message)  {
        android.util.Log.w(TAG, message);
    }
    public static void w(String tag, String message) {
        android.util.Log.w(TAG, tag + ": " + message);
    }

}

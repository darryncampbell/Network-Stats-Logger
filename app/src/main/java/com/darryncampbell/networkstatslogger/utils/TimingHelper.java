package com.darryncampbell.networkstatslogger.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.darryncampbell.networkstatslogger.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Created by darry on 01/02/2018.
 */

public class TimingHelper {

    public static void setStartTime(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(context.getString(R.string.saved_start_time), System.currentTimeMillis());
        editor.commit();
    }

    public static long getStartTime(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getLong(context.getString(R.string.saved_start_time), 0L);
    }

    public static String getStartTimeForUI(Context context)
    {
        long startTime = getStartTime(context);
        DateFormat formatter = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
        return formatter.format(startTime);
    }

    public static void setIsTestRunning(Context context, Boolean value)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(context.getString(R.string.saved_is_test_running), value);
        editor.commit();
    }

    public static boolean getIsTestRunning(Context context)
    {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(context.getString(R.string.saved_is_test_running), false);
    }

    public static long getTestDurationInMins(long endTime, Context context) {
        long startTime = getStartTime(context);
        long minutes = (endTime - startTime) / 1000 / 60;
        return minutes;
    }
}

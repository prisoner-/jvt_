package com.jvt.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * SharedPreferences存储数据方式工具类 ,直接调用即可
 *
 * @author
 */

public class SharedPrefsUtil {
    public final static String SETTING = "Setting";
    public final static String DAY_DATA = "day_data";
    public final static String MONTH_DATA = "month_data";
    public final static String HISTORY_DATA = "history_data";
    public final static String DAY = "day";
    public final static String MONTH = "month";

    public static void putValue(Context context, String key, int value) {
        Editor sp = context.getSharedPreferences(SETTING,
                Context.MODE_PRIVATE).edit();
        sp.putInt(key, value);
        sp.commit();
    }

    public static void putValue(Context context, String key, long value) {
        Editor sp = context.getSharedPreferences(SETTING,
                Context.MODE_PRIVATE).edit();
        sp.putLong(key, value);
        sp.commit();
    }

    public static void putValue(Context context, String key, boolean value) {
        Editor sp = context.getSharedPreferences(SETTING,
                Context.MODE_PRIVATE).edit();
        sp.putBoolean(key, value);
        sp.commit();
    }

    public static void putValue(Context context, String key, String value) {
        Editor sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        sp.putString(key, value);
        sp.commit();
    }

    public static int getValue(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        int value = sp.getInt(key, defValue);
        return value;
    }

    public static long getLongValue(Context context, String key, long defValue) {

        try {
            Context contexta = context.createPackageContext(
                    context.getPackageName(), Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences sp = contexta.getSharedPreferences(SETTING,
                    Context.MODE_PRIVATE);
            return sp.getLong(key, defValue);
        } catch (Exception e) {
            // TODO: handle exception
            return 0;
        }

    }

    public static boolean getValue(Context context, String key, boolean defValue) {
        try {
            Context contexta = context.createPackageContext(
                    context.getPackageName(), Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences sp = contexta.getSharedPreferences(SETTING,
                    Context.MODE_PRIVATE);
            boolean value = sp.getBoolean(key, defValue);
            return value;
        } catch (Exception e) {
            // TODO: handle exception
            return false;
        }
    }

    public static String getValue(Context context, String key, String defValue) {
        try {
            Context contexta = context.createPackageContext(
                    context.getPackageName(), Context.CONTEXT_IGNORE_SECURITY);
            SharedPreferences sp = contexta.getSharedPreferences(SETTING,
                    Context.MODE_PRIVATE);
            String value = sp.getString(key, defValue);
            return value;
        } catch (Exception e) {
            // TODO: handle exception
            return "";
        }
    }
}

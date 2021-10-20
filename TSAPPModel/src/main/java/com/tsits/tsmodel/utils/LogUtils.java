package com.tsits.tsmodel.utils;

import android.text.TextUtils;
import android.util.Log;

public class LogUtils {

    public static boolean LOG_DEBUG = true;
    public static final String default_TAG = "TSITS-APP ";

    public static void i(Object objTag, String msg) {
        if (LOG_DEBUG && !TextUtils.isEmpty(msg)) {
            String tag = getTAG(objTag);
            Log.i(default_TAG.concat(tag),msg);
        }
    }

    public static void d(Object objTag, String msg) {
        if (LOG_DEBUG && !TextUtils.isEmpty(msg)) {
            String tag = getTAG(objTag);
            Log.d(default_TAG.concat(tag),msg);
        }
    }

    public static void e(Object objTag, String msg) {
        if (LOG_DEBUG && !TextUtils.isEmpty(msg)) {
            String tag = getTAG(objTag);
            Log.e(default_TAG.concat(tag),msg);
        }
    }

    public static void w(Object objTag, String msg) {
        if (LOG_DEBUG && !TextUtils.isEmpty(msg)) {
            String tag = getTAG(objTag);
            Log.w(default_TAG.concat(tag),msg);
        }
    }

    private static String getTAG(Object objTag){
        String tag;
        // 如果objTag是String，则直接使用
        // 如果objTag不是String，则使用它的类名
        // 如果在匿名内部类，写this的话是识别不了该类，所以获取当前对象全类名来分隔
        if (objTag instanceof String) {
            tag = (String) objTag;
        } else if (objTag instanceof Class) {
            tag = ((Class) objTag).getSimpleName();
        } else {
            tag = objTag.getClass().getName();
            String[] split = tag.split("\\.");
            tag = split[split.length-1].split("\\$")[0];
        }
        return tag;
    }

}

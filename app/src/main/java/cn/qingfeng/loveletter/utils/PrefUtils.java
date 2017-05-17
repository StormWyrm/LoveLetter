package cn.qingfeng.loveletter.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * fileName    : cn.qingfeng.aixin.utils.PrefUtils.java
 * author      : 李青峰
 * date        : 2016-07-27 14:34
 * description :
 * version     : V1.0
 */
public class PrefUtils {

    public static void putString(Context context, String key, String value){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(key,value);
        edit.commit();
    }
    public static String getString(Context context, String key){
        SharedPreferences sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        return sp.getString(key,"");
    }
}

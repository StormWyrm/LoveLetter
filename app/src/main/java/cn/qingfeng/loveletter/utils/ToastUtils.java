package cn.qingfeng.loveletter.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * fileName    : cn.qingfeng.aixin.utils.ToastUtils.java
 * author      : 李青峰
 * date        : 2016-07-22 22:29
 * description : 显示吐司的工具类
 * version     : V1.0
 */
public class ToastUtils {
    public static void showToastSafe(final Context context, final String text){
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public static void showToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}

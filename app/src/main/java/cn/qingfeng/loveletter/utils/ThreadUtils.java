package cn.qingfeng.loveletter.utils;


import android.os.Handler;

/**
 * fileName    : cn.qingfeng.aixin.utils.ThreadUtils.java
 * author      : 李青峰
 * date        : 2016-07-22 19:52
 * description : 执行任务的工具类
 * version     : V1.0
 */
public class ThreadUtils {
    private static Handler handler = new Handler();

    /**
     * 在子线程中执行任务
     * */
    public static void runOnThread(Runnable runnable){
        new Thread(runnable).start();
    }
    /**
     * 在主线程中执行任务
     * */
    public static void runOnUiThread(Runnable runnable){
        handler.post(runnable);
    }
    /**
     * 在主线程中延迟执行任务
     * */
    public static void runOnUiThreadDelayed(Runnable runnable, long delayMillis){
        handler.postDelayed(runnable,delayMillis);
    }
}

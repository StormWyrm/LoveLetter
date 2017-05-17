package cn.qingfeng.loveletter.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;

import java.util.LinkedList;
import java.util.List;

import cn.qingfeng.loveletter.R;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:33
 * @DESC:         Activity的基类
 * @VERSION:      V1.0
 */
public abstract class BaseActivity extends AppCompatActivity {
   private static final List<BaseActivity> mAllActivitys = new LinkedList<>();//记录activity的信息
    protected ActionBar mActionBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        synchronized (mAllActivitys) {
            mAllActivitys.add(this);
        }
        initUi();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        synchronized (mAllActivitys) {
            mAllActivitys.remove(this);
        }
    }
    //杀死所有的activity
    public void killAll() {
        // 复制了一份mActivities 集合
        List<BaseActivity> copy;
        synchronized (mAllActivitys) {
            copy = new LinkedList<BaseActivity>(mAllActivitys);
        }
        for (BaseActivity activity : copy) {
            activity.finish();
        }
        // 杀死当前的进程
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    protected  void initData(){};

    protected  void initListener(){};

    protected abstract void initUi();

    //为Activity添加ToolBar
    protected void addActionBar(String title, boolean isBackable){
        setSupportActionBar((Toolbar) findViewById(R.id.toolBar));
        mActionBar = getSupportActionBar();
        if(!TextUtils.isEmpty(title)){
            mActionBar.setTitle(title);
        }
        if(isBackable){
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}

package cn.qingfeng.loveletter.common.ui;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.AppApplication;


/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2016/12/1 8:33
 * @DESC: Activity的基类
 * @VERSION: V1.0
 */
public abstract class BaseActivity extends AppCompatActivity {
    protected ActionBar mActionBar;
    protected BaseActivity mActivity;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        AppApplication.getInstance().addActivity(this);
        transparentStatusBar();
        initUi();
        initData();
        initListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppApplication.getInstance().removeActivity(this);
    }

    //杀死所有的activity
    public void killAll() {
        // 复制了一份mActivities 集合
        AppApplication.getInstance().exit();
    }

    protected void initData() {
    }


    protected void initListener() {
    }


    protected abstract void initUi();

    //为Activity添加ToolBar
    protected void addActionBar(String title, boolean isBackable) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if (!TextUtils.isEmpty(title)) {
            mActionBar.setTitle(title);
        }
        if (isBackable) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    //透明状态栏兼容到4.4
    private void transparentStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

        }
        //清除5.0以上半透明状态
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);

        }

    }
}

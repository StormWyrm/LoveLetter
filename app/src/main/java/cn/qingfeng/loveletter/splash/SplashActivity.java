package cn.qingfeng.loveletter.splash;

import android.content.Intent;
import android.text.TextUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.LinearLayout;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.util.ThreadUtil;
import cn.qingfeng.loveletter.guide.GuideActivity;
import cn.qingfeng.loveletter.login.LoginActivity;
import cn.qingfeng.loveletter.main.MainActivity;
import cn.qingfeng.loveletter.service.IMService;
import cn.qingfeng.loveletter.common.ui.BaseActivity;
import cn.qingfeng.loveletter.common.util.SPUtil;
import cn.qingfeng.loveletter.common.util.ToastUtil;
import cn.qingfeng.loveletter.common.util.XmppUtil;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:32
 * @DESC:         闪屏界面
 * @VERSION:      V1.0
 */
public class SplashActivity extends BaseActivity {

    private LinearLayout ll_splash;


    protected void initUi() {
        setContentView(R.layout.activity_splash);
        ll_splash = (LinearLayout) findViewById(R.id.ll_splash);
        initAnimation();
    }

    private void initAnimation() {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        animation.setDuration(3000);
        ll_splash.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //是否进入引导页面
                if (!(boolean) SPUtil.get(SplashActivity.this, "isGuided", false)) {
                    startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                } else {
                    //获取上一次登录账号密码
                    final String username = (String) SPUtil.get(SplashActivity.this, "username", "");
                    final String password = (String) SPUtil.get(SplashActivity.this, "password", "");
                    //是否自动登录
                    if ((boolean) SPUtil.get(SplashActivity.this, "isAutoLogin", true)) {

                        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        } else {
                            ThreadUtil.runOnThread(new Runnable() {
                                @Override
                                public void run() {
                                    login(username, password);
                                }
                            });

                        }
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    }
                }
                finish();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.start();
    }

    /**
     * 登录
     * */
    public void login(String username, String password) {

        boolean conServer = XmppUtil.conServer();
        if (!conServer) {
            ToastUtil.showToastSafe(SplashActivity.this, "服务器出现异常,请重试");
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            return;
        }

        //开始登录
        boolean b = XmppUtil.login(username, password);
        if (!b) {
            ToastUtil.showToastSafe(SplashActivity.this, "自动登录失败,请重试");
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            return;
        }


        //将连接对象保存下来
        IMService.conn = XmppUtil.getConnection();
        IMService.ACCOUNT = username + "@" + IMService.conn.getServiceName();
        //开启服务去获取监听数据
        startService(new Intent(SplashActivity.this, IMService.class));
        // 跳转到主页面
        startActivity(new Intent(SplashActivity.this, MainActivity.class));

    }
}

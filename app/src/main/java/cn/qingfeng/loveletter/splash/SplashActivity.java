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
public class SplashActivity extends BaseActivity implements SplashContract.View {

    private LinearLayout ll_splash;
    private SplashContract.Presenter mPresenter;

    protected void initUi() {
        setContentView(R.layout.activity_splash);
        ll_splash = (LinearLayout) findViewById(R.id.ll_splash);
        mPresenter = new SplashPresenter(this,this);
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

                mPresenter.endSplash();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.start();
    }



    @Override
    public void setPresenter(SplashContract.Presenter presenter) {
        mPresenter = presenter;
    }
}

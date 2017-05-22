package cn.qingfeng.loveletter.splash;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.util.SPUtil;
import cn.qingfeng.loveletter.common.util.ThreadUtil;
import cn.qingfeng.loveletter.common.util.ToastUtil;
import cn.qingfeng.loveletter.common.util.XmppUtil;
import cn.qingfeng.loveletter.guide.GuideActivity;
import cn.qingfeng.loveletter.login.LoginActivity;
import cn.qingfeng.loveletter.main.MainActivity;
import cn.qingfeng.loveletter.service.IMService;

/**
 * Created by liqingfeng on 2017/5/22.
 */

public class SplashPresenter implements SplashContract.Presenter {
    private Context mContext;
    private SplashContract.View mView;

    public SplashPresenter(Context context, SplashContract.View view) {
        mContext = context;
        mView = view;
        view.setPresenter(this);
    }

    @Override
    public void endSplash() {
        //是否进入引导页面
        if (!(boolean) SPUtil.get(mContext, "isGuided", false)) {
            jumpToGuidePage();
        } else {
            //是否自动登录
            if ((boolean) SPUtil.get(mContext, "isAutoLogin", true)) {
                autoLogin();
            } else {
                jumpToLoginPage();
            }
        }

    }

    private void autoLogin() {
        //获取上一次登录账号密码
        final String username = (String) SPUtil.get(mContext, "username", "");
        final String password = (String) SPUtil.get(mContext, "password", "");
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            jumpToLoginPage();
        } else {
            ThreadUtil.runOnThread(new Runnable() {
                @Override
                public void run() {
                    login(username, password);
                }
            });
        }

    }

    private void jumpToLoginPage() {
        mContext.startActivity(new Intent(mContext, LoginActivity.class));
    }

    private void jumpToGuidePage() {
        mContext.startActivity(new Intent(mContext, GuideActivity.class));
    }

    /**
     * 登录
     */
    private void login(String username, String password) {

        boolean conServer = XmppUtil.conServer();
        if (!conServer) {
            ToastUtil.showToastSafe(mContext, mContext.getString(R.string.login_server_error));
            jumpToLoginPage();
            return;
        }

        //开始登录
        boolean b = XmppUtil.login(username, password);
        if (!b) {
            ToastUtil.showToastSafe(mContext, mContext.getString(R.string.login_server_error));
            jumpToLoginPage();
            return;
        }


        //将连接对象保存下来
        IMService.conn = XmppUtil.getConnection();
        IMService.ACCOUNT = username + "@" + IMService.conn.getServiceName();
        //开启服务去获取监听数据
        mContext.startService(new Intent(mContext, IMService.class));
        // 跳转到主页面
        mContext.startActivity(new Intent(mContext, MainActivity.class));

    }
}

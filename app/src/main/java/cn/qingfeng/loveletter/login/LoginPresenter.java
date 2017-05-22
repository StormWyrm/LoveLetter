package cn.qingfeng.loveletter.login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.ui.BasePresenter;
import cn.qingfeng.loveletter.common.ui.BaseView;
import cn.qingfeng.loveletter.common.util.SPUtil;
import cn.qingfeng.loveletter.common.util.ThreadUtil;
import cn.qingfeng.loveletter.common.util.ToastUtil;
import cn.qingfeng.loveletter.common.util.XmppUtil;
import cn.qingfeng.loveletter.main.MainActivity;
import cn.qingfeng.loveletter.register.RegisterActivity;
import cn.qingfeng.loveletter.service.IMService;
import cn.qingfeng.loveletter.splash.SplashContract;

/**
 * Created by liqingfeng on 2017/5/22.
 */

public class LoginPresenter implements LoginContract.Presenter {
    private Activity mActivity;
    private LoginContract.View mView;

    public LoginPresenter(Activity activity, LoginContract.View view) {
        mActivity = activity;
        mView = view;
        mView.setPresenter(this);
    }

    @Override
    public void login(final String username, final String password) {
        ThreadUtil.runOnThread(new Runnable() {
            @Override
            public void run() {
                boolean conServer = XmppUtil.conServer();
                if (!conServer) {
                    mView.showServerError();
                    return;
                }

                //开始登录
                boolean login = XmppUtil.login(username, password);
                if (!login) {
                    mView.showLoginError();
                    return;
                }

                //把登录账号保存到本地 方便自动登录
                SPUtil.put(mActivity, "username", username);
                SPUtil.put(mActivity, "password", password);

                //设置自动登录
                SPUtil.put(mActivity, "isAutoLogin", true);

                //将连接对象保存下来
                IMService.conn = XmppUtil.getConnection();
                IMService.ACCOUNT = username + "@" + IMService.conn.getServiceName();
                //开启服务去获取监听数据
                mActivity.startService(new Intent(mActivity, IMService.class));
                // 跳转到主页面
                mActivity.startActivity(new Intent(mActivity, MainActivity.class));

                mView.finishActivity();
            }
        });
    }

    @Override
    public void jumpToRegisterPage() {
        mActivity.startActivity(new Intent(mActivity, RegisterActivity.class));

    }


}

package cn.qingfeng.loveletter.login;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.util.ThreadUtil;
import cn.qingfeng.loveletter.common.util.ToastUtil;
import cn.qingfeng.loveletter.main.MainActivity;
import cn.qingfeng.loveletter.service.IMService;
import cn.qingfeng.loveletter.common.ui.BaseActivity;
import cn.qingfeng.loveletter.register.RegisterActivity;
import cn.qingfeng.loveletter.common.ui.view.MyEditText;
import cn.qingfeng.loveletter.common.util.SPUtil;
import cn.qingfeng.loveletter.common.util.XmppUtil;


/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2016/12/1 8:30
 * @DESC: 登录界面
 * @VERSION: V1.0
 */
public class LoginActivity extends BaseActivity implements LoginContract.View {

    private LinearLayout linearLayout;
    private MyEditText mUsername;
    private MyEditText mPassword;
    private Button mLogin;
    private TextView mRegist;

    private LoginContract.Presenter mPresenter;

    @Override
    protected void initUi() {
        setContentView(R.layout.activity_login);
        linearLayout = (LinearLayout) findViewById(R.id.ll_login);
        mUsername = (MyEditText) findViewById(R.id.username);
        mPassword = (MyEditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.btn_login);
        mRegist = (TextView) findViewById(R.id.tv_regist);

        mPresenter = new LoginPresenter(mActivity, this);
        //开启动画
        startAnimation();
    }

    @Override
    protected void initData() {
        //获取注册页面的intent 并将用户的注册信息填写
        Intent intent = getIntent();
        if (intent != null) {
            String username = intent.getStringExtra("username");
            String password = intent.getStringExtra("password");
            mUsername.setText(username);
            mPassword.setText(password);
        }

        //获取上次登录的账号密码
        String username = (String) SPUtil.get(LoginActivity.this, "username", "");
        String password = (String) SPUtil.get(LoginActivity.this, "password", "");
        mUsername.setText(username);
        mPassword.setText(password);

    }

    @Override
    protected void initListener() {
        //登录
        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsername.getText().toString();
                final String password = mPassword.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                    ToastUtil.showToast(LoginActivity.this, getString(R.string.login_username_or_password_empty));
                    return;
                }
                mPresenter.login(username, password);

            }
        });
        //注册
        mRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.register();
            }
        });
    }

    /**
     * 登录界面进入动画
     */
    private void startAnimation() {
        TranslateAnimation translateAnimation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_PARENT, -1, Animation.RELATIVE_TO_PARENT, 0
        );

        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(translateAnimation);
        set.addAnimation(alphaAnimation);
        set.setDuration(3500);
        set.setInterpolator(new BounceInterpolator());
        linearLayout.startAnimation(set);

    }


    @Override
    public void setPresenter(LoginContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showServerError() {
        ToastUtil.showToastSafe(mActivity, getString(R.string.login_server_error));
    }

    @Override
    public void showLoginError() {
        ToastUtil.showToastSafe(mActivity, getString(R.string.login_error));
    }

    @Override
    public void finishActivity() {
        finish();
    }
}

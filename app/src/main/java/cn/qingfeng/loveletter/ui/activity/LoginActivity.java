package cn.qingfeng.loveletter.ui.activity;

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
import cn.qingfeng.loveletter.service.IMService;
import cn.qingfeng.loveletter.ui.base.BaseActivity;
import cn.qingfeng.loveletter.ui.view.MyEditText;
import cn.qingfeng.loveletter.utils.SPUtils;
import cn.qingfeng.loveletter.utils.ThreadUtils;
import cn.qingfeng.loveletter.utils.ToastUtils;
import cn.qingfeng.loveletter.utils.XmppUtil;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:30
 * @DESC:         登录界面
 * @VERSION:      V1.0
 */
public class LoginActivity extends BaseActivity {

    private LinearLayout linearLayout;
    private MyEditText mUsername;
    private MyEditText mPassword;
    private Button mLogin;
    private TextView mRegist;


    @Override
    protected void initUi() {
        setContentView(R.layout.activity_login);
        linearLayout = (LinearLayout) findViewById(R.id.ll_login);
        mUsername = (MyEditText) findViewById(R.id.username);
        mPassword = (MyEditText) findViewById(R.id.password);
        mLogin = (Button) findViewById(R.id.btn_login);
        mRegist = (TextView) findViewById(R.id.tv_regist);



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
        String username = (String) SPUtils.get(LoginActivity.this, "username", "");
        String password = (String) SPUtils.get(LoginActivity.this, "password", "");
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
                    ToastUtils.showToast(LoginActivity.this, "账号或密码不能为空");
                    return;
                }
                ThreadUtils.runOnThread(new Runnable() {
                    @Override
                    public void run() {
                        login(username, password);
                    }
                });

            }
        });
        //注册
        mRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
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

    //登录
    public void login(String username, String password) {

        boolean conServer = XmppUtil.conServer();
        if (!conServer) {
            ToastUtils.showToastSafe(LoginActivity.this, "服务器出现异常");
            return;
        }

        //开始登录
        boolean login = XmppUtil.login(username, password);
        if (!login) {
            ToastUtils.showToastSafe(LoginActivity.this, "登录失败,请重试");
            return;
        }

        //把登录账号保存到本地 方便自动登录
        SPUtils.put(LoginActivity.this, "username", username);
        SPUtils.put(LoginActivity.this, "password", password);

        //设置自动登录
        SPUtils.put(LoginActivity.this, "isAutoLogin", true);

        //将连接对象保存下来
        IMService.conn = XmppUtil.getConnection();
        IMService.ACCOUNT = username + "@" + IMService.conn.getServiceName();
        //开启服务去获取监听数据
        startService(new Intent(LoginActivity.this, IMService.class));
        // 跳转到主页面
        startActivity(new Intent(LoginActivity.this, MainActivity.class));

        finish();
    }

}

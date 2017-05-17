package cn.qingfeng.loveletter.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.ui.base.BaseActivity;
import cn.qingfeng.loveletter.ui.view.MyEditText;
import cn.qingfeng.loveletter.utils.ThreadUtils;
import cn.qingfeng.loveletter.utils.ToastUtils;
import cn.qingfeng.loveletter.utils.XmppUtil;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:31
 * @DESC:         注册界面
 * @VERSION:      V1.0
 */

public class RegisterActivity extends BaseActivity {
    private Toolbar mToolBar;
    private MyEditText mUsername;
    private MyEditText mPassword;
    private MyEditText mConfirmPassword;
    private Button mRegist;

    @Override
    protected void initUi() {
        setContentView(R.layout.activity_register);
        addActionBar("注册新用户",true);


        mRegist = (Button) findViewById(R.id.btn_regist);
        mUsername = (MyEditText) findViewById(R.id.username);
        mPassword = (MyEditText) findViewById(R.id.password);
        mConfirmPassword = (MyEditText) findViewById(R.id.confirmPassword);
    }

    @Override
    protected void initListener() {
        mRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsername.getText().toString();
                final String password = mPassword.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    ToastUtils.showToast(RegisterActivity.this, "用户名不能为空");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtils.showToast(RegisterActivity.this, "密码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    ToastUtils.showToast(RegisterActivity.this, "确认密码不能为空");
                    return;
                }
                if (!confirmPassword.equals(password)) {
                    ToastUtils.showToast(RegisterActivity.this, "密码前后不一致");
                    return;
                }

                register(username, password);
            }
        });
    }

    private void register(final String username, final String password) {
        ThreadUtils.runOnThread(new Runnable() {
            @Override
            public void run() {
                boolean b = XmppUtil.conServer();
                if (!b) {
                    ToastUtils.showToastSafe(RegisterActivity.this, "服务器出现异常");
                    return;
                }
                int id = XmppUtil.regist(username, password);
                switch (id) {
                    case 0:
                        //服务器出现异常
                        ToastUtils.showToastSafe(RegisterActivity.this, "服务器出现异常");
                        break;
                    case 1:
                        ThreadUtils.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("注册成功,前去登录?");
                                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //注册成功
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.putExtra("username", username);
                                        intent.putExtra("password", password);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.show();
                            }
                        });
                        break;
                    case 2:
                        //用户已经存在
                        ToastUtils.showToastSafe(RegisterActivity.this, "用户已存在");
                        break;
                    case 3:
                        //注册失败
                        ToastUtils.showToastSafe(RegisterActivity.this, "注册失败,请重试");
                        break;
                }
            }
        });
    }

}

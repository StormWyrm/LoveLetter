package cn.qingfeng.loveletter.register;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.ui.BaseActivity;
import cn.qingfeng.loveletter.common.util.ThreadUtil;
import cn.qingfeng.loveletter.common.util.ToastUtil;
import cn.qingfeng.loveletter.login.LoginActivity;
import cn.qingfeng.loveletter.common.ui.view.MyEditText;
import cn.qingfeng.loveletter.common.util.XmppUtil;


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
        addActionBar(getString(R.string.register_title),true);


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
                    ToastUtil.showToast(RegisterActivity.this, getString(R.string.register_name_not_empty));
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    ToastUtil.showToast(RegisterActivity.this, getString(R.string.register_password_not_empty));
                    return;
                }
                if (TextUtils.isEmpty(confirmPassword)) {
                    ToastUtil.showToast(RegisterActivity.this,  getString(R.string.register_confirm_password_not_empty));
                    return;
                }
                if (!confirmPassword.equals(password)) {
                    ToastUtil.showToast(RegisterActivity.this, getString(R.string.register_password_inconsistent));
                    return;
                }

                register(username, password);
            }
        });
    }

    private void register(final String username, final String password) {
        ThreadUtil.runOnThread(new Runnable() {
            @Override
            public void run() {
                boolean b = XmppUtil.conServer();
                if (!b) {
                    ToastUtil.showToastSafe(RegisterActivity.this, getString(R.string.register_server_error));
                    return;
                }
                int id = XmppUtil.regist(username, password);
                switch (id) {
                    case 0:
                        //服务器出现异常
                        ToastUtil.showToastSafe(RegisterActivity.this, getString(R.string.register_server_error));
                        break;
                    case 1:
                        ThreadUtil.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage(R.string.register_dialog_message);
                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
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
                                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
                        ToastUtil.showToastSafe(RegisterActivity.this, getString(R.string.register_user_exist));
                        break;
                    case 3:
                        //注册失败
                        ToastUtil.showToastSafe(RegisterActivity.this, getString(R.string.resiter_failure));
                        break;
                }
            }
        });
    }

}

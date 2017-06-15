package cn.qingfeng.loveletter.register;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.AppApplication;
import cn.qingfeng.loveletter.common.ui.BaseActivity;
import cn.qingfeng.loveletter.common.ui.view.MyEditText;
import cn.qingfeng.loveletter.common.util.SPUtil;
import cn.qingfeng.loveletter.common.util.ToastUtil;
import cn.qingfeng.loveletter.login.LoginActivity;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:31
 * @DESC:         注册界面
 * @VERSION:      V1.0
 */

public class RegisterActivity extends BaseActivity implements RegisterContract.View{
    private MyEditText mUsername;
    private MyEditText mPassword;
    private MyEditText mConfirmPassword;
    private MyEditText mIp;
    private Button mRegist;
    private RegisterContract.Presenter mPresenter;

    @Override
    protected void initUi() {
        setContentView(R.layout.activity_register);
        addActionBar(getString(R.string.register_title),true);

        mRegist = (Button) findViewById(R.id.btn_regist);
        mUsername = (MyEditText) findViewById(R.id.username);
        mPassword = (MyEditText) findViewById(R.id.password);
        mConfirmPassword = (MyEditText) findViewById(R.id.confirmPassword);
        mIp = (MyEditText) findViewById(R.id.ip);
        mIp.setText((String) SPUtil.get(AppApplication.getInstance(), "ip", "192.168.0.1"));

        mPresenter = new RegisterPresenter(this);

    }

    @Override
    protected void initListener() {
        mRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = mUsername.getText().toString();
                final String password = mPassword.getText().toString();
                String confirmPassword = mConfirmPassword.getText().toString();
                String ip = mIp.getText();
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
                if (TextUtils.isEmpty(ip)) {
                    ToastUtil.showToast(RegisterActivity.this, getString(R.string.login_password_ip_error));
                    return;
                }

                mPresenter.register(username, password,ip);
            }
        });
    }


    @Override
    public void setPresenter(RegisterContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void serverError() {
        ToastUtil.showToastSafe(RegisterActivity.this, getString(R.string.register_server_error));
    }

    @Override
    public void registerSuccess(final String username, final String password, final String ip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setMessage(R.string.register_dialog_message);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //注册成功
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                intent.putExtra("ip",ip);
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

    @Override
    public void registerFailure() {
        ToastUtil.showToastSafe(RegisterActivity.this, getString(R.string.resiter_failure));
    }

    @Override
    public void userExit() {
        ToastUtil.showToastSafe(RegisterActivity.this, getString(R.string.register_user_exist));
    }
}

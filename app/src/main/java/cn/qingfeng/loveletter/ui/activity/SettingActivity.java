package cn.qingfeng.loveletter.ui.activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.service.IMService;
import cn.qingfeng.loveletter.ui.base.BaseActivity;
import cn.qingfeng.loveletter.ui.fragment.FragmentFactory;
import cn.qingfeng.loveletter.utils.SPUtils;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:32
 * @DESC:         设置界面
 * @VERSION:      V1.0
 */
public class SettingActivity extends BaseActivity {

    @Override
    protected void initUi() {
        setContentView(R.layout.activity_setting);
        addActionBar("设置",true);

    }

    public void setting(View view) {
        View v = View.inflate(this, R.layout.layout_logout_dialog, null);
        Button btnZhuxiao = (Button) v.findViewById(R.id.zhuxiao);
        Button btnTuichu = (Button) v.findViewById(R.id.tuichu);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.create();
        alertDialog.setView(v);
        alertDialog.setCanceledOnTouchOutside(true);

        btnZhuxiao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                XmppUtil.deleteAccount(IMService.conn);
                stopService(new Intent(SettingActivity.this, IMService.class));
                FragmentFactory.clearAll();
                restartApplication();
            }
        });

        btnTuichu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                stopService(new Intent(SettingActivity.this,IMService.class));
                killAll();
            }
        });

        alertDialog.show();
    }
    //新消息提醒设置
    public void newMessage(View view){
        startActivity(new Intent(SettingActivity.this,MessageAlertActivity.class));
    }

    /**
     * 重新启动应用
     * */
    private void restartApplication() {
        SPUtils.put(SettingActivity.this,"isAutoLogin",false);

        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}

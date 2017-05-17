package cn.qingfeng.loveletter.ui.activity;

import android.support.v7.widget.SwitchCompat;
import android.widget.CompoundButton;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.ui.base.BaseActivity;
import cn.qingfeng.loveletter.utils.SPUtils;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:31
 * @DESC:         设置界面中的消息提醒设置
 * @VERSION:      V1.0
 */
public class MessageAlertActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener{
    private SwitchCompat scMessageDetail;
    private SwitchCompat scMessageVoice;
    private SwitchCompat scMessageShock;
    @Override
    protected void initUi() {
        setContentView(R.layout.activity_message_alert);
        addActionBar("新消息提醒",true);

        scMessageDetail = (SwitchCompat) findViewById(R.id.sc_message_detail);
        scMessageVoice = (SwitchCompat) findViewById(R.id.sc_message_voice);
        scMessageShock = (SwitchCompat) findViewById(R.id.sc_message_shock);
    }

    @Override
    protected void initData() {
        boolean isShowMessageDetail = (boolean) SPUtils.get(this, "isShowMessageDetail", true);
        boolean isShowMessageVoice = (boolean) SPUtils.get(this, "isShowMessageVoice", true);
        boolean isShowMessageShock = (boolean) SPUtils.get(this, "isShowMessageShock", true);
        scMessageDetail.setChecked(isShowMessageDetail);
        scMessageVoice.setChecked(isShowMessageVoice);
        scMessageShock.setChecked(isShowMessageShock);
    }

    @Override
    protected void initListener() {
        scMessageDetail.setOnCheckedChangeListener(this);
        scMessageVoice.setOnCheckedChangeListener(this);
        scMessageShock.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.sc_message_detail:
                if(b){
                    SPUtils.put(this,"isShowMessageDetail",true);
                }else{
                    SPUtils.put(this,"isShowMessageDetail",false);
                }
                break;
            case R.id.sc_message_voice:
                if(b){
                    SPUtils.put(this,"isShowMessageVoice",true);
                }else{
                    SPUtils.put(this,"isShowMessageVoice",false);
                }
                break;
            case R.id.sc_message_shock:
                if(b){
                    SPUtils.put(this,"isShowMessageShock",true);
                }else{
                    SPUtils.put(this,"isShowMessageShock",false);
                }
                break;
        }

    }
}

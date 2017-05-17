package cn.qingfeng.loveletter.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Presence;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.service.IMService;
import cn.qingfeng.loveletter.ui.base.BaseActivity;
import cn.qingfeng.loveletter.ui.view.MyEditText;
import cn.qingfeng.loveletter.utils.ToastUtils;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:29
 * @DESC:         添加朋友
 * @VERSION:      V1.0
 */
public class AddFriendActivity extends BaseActivity {

    private MyEditText searchAccount;
    private Button btnSearch;

    @Override
    protected void initUi() {
        setContentView(R.layout.activity_add_friend);
        addActionBar("添加好友",true);

        searchAccount = ((MyEditText) findViewById(R.id.search_account));
        btnSearch = (Button) findViewById(R.id.btn_search);
        Intent intent = getIntent();
        if(intent != null){
            String username = intent.getStringExtra("username");
            if(!TextUtils.isEmpty(username)){
                searchAccount.setText(username);
                searchAccount.getEditText().setSelection(username.length());
            }
        }
    }

    @Override
    protected void initListener() {
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = searchAccount.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    ToastUtils.showToast(AddFriendActivity.this, "搜索账号不能为空");
                    return;
                }
                addFriend(name);
            }
        });
    }

    private void addFriend(String name) {

        XMPPConnection conn = IMService.conn;
        String username = name + "@" + conn.getServiceName();

        Presence subscription = new Presence(Presence.Type.subscribe);
        subscription.setTo(username);
        conn.sendPacket(subscription);


        finish();
    }

    public void back(View view) {
        finish();
    }
}

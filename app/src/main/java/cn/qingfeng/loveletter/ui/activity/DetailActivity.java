package cn.qingfeng.loveletter.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.dbhelper.ContactOpenHelper;
import cn.qingfeng.loveletter.ui.base.BaseActivity;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:29
 * @DESC:         联系人信息详情页
 * @VERSION:      V1.0
 */
public class DetailActivity extends BaseActivity {
    private TextView tvNickname;
    private TextView tvAccount;

    private String mClickNickname;
    private String mClickAccount;
    @Override
    protected void initUi() {
        setContentView(R.layout.activity_detail);
        addActionBar("详细资料",true);

        tvNickname = (TextView) findViewById(R.id.nickname);
        tvAccount = (TextView) findViewById(R.id.account);
        Intent intent = getIntent();
        if (intent != null) {
            mClickAccount = intent.getStringExtra(ContactOpenHelper.ContactTable.ACCOUNT);
            mClickNickname = intent.getStringExtra(ContactOpenHelper.ContactTable.NICKNAME);
        }
        tvNickname.setText("昵称: "+mClickNickname);
        tvAccount.setText("账号: "+mClickAccount);

    }
    public void back(View view){
        finish();
    }
    public void sendMessage(View view){
        finish();
        Intent intent = new Intent(DetailActivity.this, ChatActivity.class);
        intent.putExtra(ContactOpenHelper.ContactTable.ACCOUNT, mClickAccount);
        intent.putExtra(ContactOpenHelper.ContactTable.NICKNAME, mClickNickname);
        startActivity(intent);
    }
}

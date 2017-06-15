package cn.qingfeng.loveletter.chat;


import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.AppApplication;
import cn.qingfeng.loveletter.common.util.EmotionUtil;
import cn.qingfeng.loveletter.common.util.SpanStringUtil;
import cn.qingfeng.loveletter.db.ContactOpenHelper;
import cn.qingfeng.loveletter.db.SmsOpenHelper;
import cn.qingfeng.loveletter.provider.SmsProvider;
import cn.qingfeng.loveletter.service.IMService;


/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2016/11/28 20:42
 * @DESC: 聊天界面的实现
 * @VERSION: V1.0
 */
public class ChatActivity extends AppCompatActivity implements ChatContract.View, View.OnClickListener {
    private ListView mListView;
    private EditText etChatMessage;
    private Button btnSend;
    private ImageView iv_more;
    private ImageView iv_emotion;
    private FrameLayout fl_emotion;

    private InputMethodManager imm;//软键盘服务

    private boolean isEmotionShowing;//表情界面是否显示
    private boolean isMoreShowing;//更多界面是否显示
    private String mClickAccount;
    private String mClickNickname;
    private ContentObserver mContentObserver = new MyContentObserver(new Handler());//数据库的观察者
    private MyCursorAdapter mCursorAdapter;
    private ActionBar mActionBar;
    private ChatContract.Presenter mPresenter;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppApplication.getInstance().addActivity(this);
        //注册内容观察者
        getContentResolver().registerContentObserver(SmsProvider.URI_SMS, true, mContentObserver);
        //绑定服务
        mPresenter = new ChatPresenter(this, this);
        mPresenter.bindIMService();
        initUi();
        initData();
        initListener();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppApplication.getInstance().removeActivity(this);
        getContentResolver().unregisterContentObserver(mContentObserver);
        //解除绑定
        mPresenter.unbindIMService();
    }

    protected void initUi() {
        setContentView(R.layout.activity_chat);
        addActionBar("", true);

        mListView = (ListView) findViewById(R.id.listview);
        etChatMessage = (EditText) findViewById(R.id.et_chat_message);
        iv_more = (ImageView) findViewById(R.id.iv_more);
        iv_emotion = (ImageView) findViewById(R.id.iv_emotion);
        btnSend = (Button) findViewById(R.id.btn_send);
        fl_emotion = (FrameLayout) findViewById(R.id.fl_emotion);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        //获取用户名以及账号
        Intent intent = getIntent();
        if (intent != null) {
            mClickAccount = intent.getStringExtra(ContactOpenHelper.ContactTable.ACCOUNT);
            mClickNickname = intent.getStringExtra(ContactOpenHelper.ContactTable.NICKNAME);
            mActionBar.setTitle(mClickNickname);//更改actionBar的名称
        }
    }

    protected void initData() {
        mPresenter.getDialogueMessage(mClickAccount);
    }

    protected void initListener() {
        btnSend.setOnClickListener(this);
        iv_emotion.setOnClickListener(this);
        iv_more.setOnClickListener(this);
        etChatMessage.setOnClickListener(this);
        etChatMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if (TextUtils.isEmpty(s)) {
                    btnSend.setVisibility(View.GONE);
                    iv_more.setVisibility(View.VISIBLE);
                } else {
                    btnSend.setVisibility(View.VISIBLE);
                    iv_more.setVisibility(View.GONE);
                }
            }
        });
        //当ListView触摸的时候 强制关闭软键盘
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(etChatMessage.getWindowToken(), 0);
                }
                if (isEmotionShowing || isMoreShowing) {
                    fl_emotion.setVisibility(View.GONE);
                    isMoreShowing = false;
                    isEmotionShowing = false;
                }
                return false;
            }
        });

    }

    //为Activity添加ToolBar
    protected void addActionBar(String title, boolean isBackable) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        mActionBar = getSupportActionBar();
        if (!TextUtils.isEmpty(title)) {
            mActionBar.setTitle(title);
        }
        if (isBackable) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * 让Fragment能获取edit的内容
     */
    public String getText() {
        return etChatMessage.getText().toString();
    }

    /**
     * 让Fragment能获取edit
     */
    public EditText getEdit() {
        return etChatMessage;
    }

    /**
     * 让Fragment更改fragment的内容
     */
    public void setText(CharSequence text) {
        etChatMessage.setText(text);
        if (!TextUtils.isEmpty(text)) {
            etChatMessage.setSelection(text.length());
        }
    }

    //改变表情界面状态
    private void changeEmotionStatus() {
        if (!isMoreShowing) {
            if (!isEmotionShowing) {
                fl_emotion.setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(etChatMessage.getWindowToken(), 0);
                isEmotionShowing = true;
                isMoreShowing = false;
                showEmotionFragment();

            } else {
                fl_emotion.setVisibility(View.GONE);
                imm.showSoftInput(etChatMessage, InputMethodManager.SHOW_IMPLICIT);
                isEmotionShowing = false;
                isMoreShowing = false;
            }
        } else {
            showEmotionFragment();
            isEmotionShowing = true;
            isMoreShowing = false;
        }
    }

    //点击选择更多
    private void selectMore() {
        if (!isEmotionShowing) {
            if (!isMoreShowing) {
                fl_emotion.setVisibility(View.VISIBLE);
                imm.hideSoftInputFromWindow(etChatMessage.getWindowToken(), 0);
                isEmotionShowing = false;
                isMoreShowing = true;
                showMoreFrament();

            } else {
                fl_emotion.setVisibility(View.GONE);
                imm.showSoftInput(etChatMessage, InputMethodManager.SHOW_IMPLICIT);
                isEmotionShowing = false;
                isMoreShowing = false;
            }
        } else {
            showMoreFrament();
            isEmotionShowing = false;
            isMoreShowing = true;
        }
    }

    /**
     * 显示表情界面
     */
    private void showEmotionFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_emotion, new ChatEmotionFragment())
                .commit();
    }

    /**
     * 显示更多界面
     */
    private void showMoreFrament() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_emotion, new ChatMoreFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (isEmotionShowing) {
            isEmotionShowing = false;
            fl_emotion.setVisibility(View.GONE);
            return;
        }
        if (isMoreShowing) {
            isMoreShowing = false;
            fl_emotion.setVisibility(View.GONE);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                mPresenter.sendMessage(mClickAccount);
                break;
            case R.id.iv_emotion:
                changeEmotionStatus();
                break;
            case R.id.iv_more:
                selectMore();
                break;
            case R.id.et_chat_message:
                //隐藏表情界面
                if (isEmotionShowing | isMoreShowing) {
                    fl_emotion.setVisibility(View.GONE);
                    isEmotionShowing = false;
                    isMoreShowing = false;
                }
                break;
        }
    }


    /**
     * ListView的适配器
     */
    private class MyCursorAdapter extends CursorAdapter {
        private static final int SEND = 0;
        private static final int RECEIVE = 1;
        private Cursor cursor;

        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
            cursor = c;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return null;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
        }

        //获取view的类型
        @Override
        public int getItemViewType(int position) {
            cursor.moveToPosition(position);
            // 取出消息的创建者
            String fromAccount = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.FROM_ACCOUNT));
            if (IMService.ACCOUNT.equals(fromAccount)) {// 接收
                return SEND;
            } else {// 发送
                return RECEIVE;
            }
            // return super.getItemViewType(position);// 0 1
            // 接收--->如果当前的账号 不等于 消息的创建者
            // 发送
        }

        //总共有几种类型的View
        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (getItemViewType(position) == RECEIVE) {
                if (convertView == null) {
                    convertView = View.inflate(ChatActivity.this, R.layout.item_chat_receiver, null);
                    holder = new ViewHolder();
                    convertView.setTag(holder);

                    // holder赋值
                    holder.time = (TextView) convertView.findViewById(R.id.time);
                    holder.body = (TextView) convertView.findViewById(R.id.content);
                    holder.head = (ImageView) convertView.findViewById(R.id.head);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }

                // 得到数据,展示数据
            } else {// 发送
                if (convertView == null) {
                    convertView = View.inflate(ChatActivity.this, R.layout.item_chat_send, null);
                    holder = new ViewHolder();
                    convertView.setTag(holder);

                    // holder赋值
                    holder.time = (TextView) convertView.findViewById(R.id.time);
                    holder.body = (TextView) convertView.findViewById(R.id.content);
                    holder.head = (ImageView) convertView.findViewById(R.id.head);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                // 得到数据,展示数据

            }
            // 得到数据,展示数据
            cursor.moveToPosition(position);

            String time = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.TIME));
            String body = cursor.getString(cursor.getColumnIndex(SmsOpenHelper.SmsTable.BODY));

            String formatTime = new SimpleDateFormat("HH:mm").format(new Date(Long
                    .parseLong(time)));

            holder.time.setText(formatTime);

            //使用SpanImage代替特殊字符
            SpannableString emotionContent = SpanStringUtil.getEmotionContent(EmotionUtil.EMOTION_CLASSIC_TYPE,
                    ChatActivity.this, holder.body, body);
            holder.body.setText(emotionContent);


            return convertView;
        }

        private class ViewHolder {
            TextView body;
            TextView time;
            ImageView head;

        }
    }


    /**
     * SmsOpenHelper的观察者
     */
    private class MyContentObserver extends ContentObserver {
        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            System.out.println("观察到改变");
            mPresenter.getDialogueMessage(mClickAccount);
        }
    }


    @Override
    public void setPresenter(ChatContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showDialogueMessage(Cursor cursor) {
        if (mCursorAdapter != null) {
            mCursorAdapter.getCursor().requery();
            mListView.setSelection(cursor.getCount() - 1);//让ListView到达最后一列
            return;
        }

        mCursorAdapter = new MyCursorAdapter(ChatActivity.this, cursor);
        mListView.setAdapter(mCursorAdapter);
        mListView.setSelection(mCursorAdapter.getCount() - 1);

    }

    @Override
    public String getMessage() {
        return getText();
    }

    @Override
    public void clearMessage() {
        etChatMessage.setText("");
    }


}

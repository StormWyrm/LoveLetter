package cn.qingfeng.loveletter.main;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.util.ThreadUtil;
import cn.qingfeng.loveletter.db.ContactOpenHelper;
import cn.qingfeng.loveletter.db.SmsOpenHelper;
import cn.qingfeng.loveletter.provider.ContactProvider;
import cn.qingfeng.loveletter.provider.SmsProvider;
import cn.qingfeng.loveletter.service.IMService;
import cn.qingfeng.loveletter.chat.ChatActivity;
import cn.qingfeng.loveletter.common.ui.BaseFragment;
import cn.qingfeng.loveletter.common.util.EmotionUtil;
import cn.qingfeng.loveletter.common.util.SpanStringUtil;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:38
 * @DESC:         聊天消息记录界面
 * @VERSION:      V1.0
 */
public class MessageFragment extends BaseFragment {
    private TextView mTextView;
    private ListView mListView;

    private CursorAdapter mAdapter;
    private PopupWindow popupWindow;

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销内容监听器
        unRegisterContentObserver();
    }

    @Override
    protected View initUi() {
        //注册内容监听器
        registerContentObserver();

        View view = View.inflate(mActivity, R.layout.fragment_message, null);
        mListView = (ListView) view.findViewById(R.id.listview);
        mTextView = (TextView) view.findViewById(R.id.textview);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        //更新数据
        setOrUpdateAdapter();
    }

    @Override
    protected void initListener() {
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                View contentView = View.inflate(mActivity, R.layout.layout_popuwindow, null);
                if (popupWindow == null) {
                    popupWindow = new PopupWindow(contentView,
                            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                    //popupWindow的组件要想获取焦点必须设置以下的值
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                    popupWindow.setFocusable(true);
                    popupWindow.setOutsideTouchable(true);//点击PopuWindow的外部消失
                    popupWindow.setTouchInterceptor(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                            // 这里如果返回true的话，touch事件将被拦截
                            // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
                        }
                    });
                }
                contentView.measure(0, 0);
                popupWindow.showAsDropDown(view, view.getWidth() / 2 - contentView.getMeasuredWidth() / 2, 0);


                Button delete = (Button) contentView.findViewById(R.id.delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor c = mAdapter.getCursor();
                        String mClickAccount = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));
                        System.out.println("mclickAccout" + mClickAccount);

                        mActivity.getContentResolver().delete(
                                SmsProvider.URI_SMS,
                                "(from_account = ? and to_account = ?) or (from_account = ? and to_account = ?)",
                                new String[]{IMService.ACCOUNT, mClickAccount, mClickAccount, IMService.ACCOUNT});
                        mAdapter.getCursor().requery();
                        popupWindow.dismiss();

                    }
                });

                return true;//返回true拦截点击事件
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor c = mAdapter.getCursor();
                c.moveToPosition(position);
                // 拿到jid(账号)-->发送消息的时候需要
                String account = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));
                // 拿到nickName-->显示效果
                String nickname = getNickNameByAccount(account);

                Intent intent = new Intent(getActivity(), ChatActivity.class);

                intent.putExtra(ContactOpenHelper.ContactTable.ACCOUNT, account);
                intent.putExtra(ContactOpenHelper.ContactTable.NICKNAME, nickname);

                startActivity(intent);
            }
        });

    }

    //设置或者是更新Adapter
    private void setOrUpdateAdapter() {
        // 判断adapter是否存在
        if (mAdapter != null) {
            // 刷新adapter就行了
            mAdapter.getCursor().requery();
            return;
        }

        ThreadUtil.runOnThread(new Runnable() {
            @Override
            public void run() {
                // 对应查询记录
                final Cursor c = getActivity().getContentResolver().query(SmsProvider.URI_SESSION,
                        null, null, new String[]{IMService.ACCOUNT}, null);

                // 设置adapter,然后显示数据
                ThreadUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 假如没有数据的时候
                        if (c == null || c.getCount() <= 0) {
                            mListView.setVisibility(View.GONE);
                            mTextView.setVisibility(View.VISIBLE);
                            return;
                        }
                        mListView.setVisibility(View.VISIBLE);
                        mTextView.setVisibility(View.GONE);

                        mAdapter = new MyCursorAdapter(getActivity(), c);
                        mListView.setAdapter(mAdapter);
                    }

                });
            }
        });
    }

    /**
     * ListView的adapter
     */
    private class MyCursorAdapter extends CursorAdapter {
        private Cursor c;

        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
            this.c = c;
        }

        // 如果convertView==null,返回一个具体的根视图
        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            View view = View.inflate(context, R.layout.item_message_fragment, null);
            return view;
        }

        // 设置数据显示数据
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvTime = (TextView) view.findViewById(R.id.time);
            TextView tvBody = (TextView) view.findViewById(R.id.body);
            TextView tvNickName = (TextView) view.findViewById(R.id.nickname);

            String time = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.TIME));
            String body = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.BODY));
            String acccount = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));
            String nickName = getNickNameByAccount(acccount);
            // acccount 但是在聊天记录表(sms)里面没有保存别名信息,只有(Contact表里面有)
            tvBody.setText(SpanStringUtil.getEmotionContent(EmotionUtil.EMOTION_CLASSIC_TYPE, mActivity, tvBody, body));
            tvNickName.setText(nickName);

            String formatTime = new SimpleDateFormat("HH:mm").format(new Date(Long
                    .parseLong(time)));
            tvTime.setText(formatTime);
        }
    }

    /**
     * 通过账户获取昵称
     */
    public String getNickNameByAccount(String account) {
        String nickName = "";
        Cursor c =
                getActivity().getContentResolver().query(ContactProvider.URI_CONTACT, null,
                        ContactOpenHelper.ContactTable.ACCOUNT + "=?", new String[]{account}, null);
        if (c.getCount() > 0) {// 有数据
            c.moveToFirst();
            nickName = c.getString(c.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));
        }
        return nickName;
    }


    /*=============== 监听数据库记录的改变 ===============*/

    MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());

    /**
     * 注册监听
     */
    public void registerContentObserver() {
        // content://xxxx/contact
        // content://xxxx/contact/i
        getActivity().getContentResolver().registerContentObserver(SmsProvider.URI_SMS, true, mMyContentObserver);
    }

    public void unRegisterContentObserver() {
        getActivity().getContentResolver().unregisterContentObserver(mMyContentObserver);
    }

    /**
     * 内容观察者 内容发生变化的时候
     */

    private class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        /**
         * 如果数据库数据改变会在这个方法收到通知
         */
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            // 更新adapter或者刷新adapter
            setOrUpdateAdapter();
        }
    }


}

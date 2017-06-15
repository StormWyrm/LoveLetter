package cn.qingfeng.loveletter.main;

import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.ui.BaseFragment;
import cn.qingfeng.loveletter.db.SmsOpenHelper;
import cn.qingfeng.loveletter.main.adapter.MessageCursorAdapter;
import cn.qingfeng.loveletter.provider.SmsProvider;
import cn.qingfeng.loveletter.service.IMService;


/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2016/12/1 8:38
 * @DESC: 聊天消息记录界面
 * @VERSION: V1.0
 */
public class MessageFragment extends BaseFragment implements MessageContract.View, AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {
    private TextView mTextView;
    private ListView mListView;

    private CursorAdapter mAdapter;
    private PopupWindow popupWindow;
    private MessageContract.Presenter mPresenter;
    private MyContentObserver mMyContentObserver = new MyContentObserver(new Handler());

    @Override
    public void onDestroy() {
        super.onDestroy();
        //注销内容监听器
        getActivity().getContentResolver().unregisterContentObserver(mMyContentObserver);
    }

    @Override
    protected View initUi() {
        //注册内容监听器
        getActivity().getContentResolver().registerContentObserver(SmsProvider.URI_SMS, true, mMyContentObserver);

        View view = View.inflate(mActivity, R.layout.fragment_message, null);
        mListView = (ListView) view.findViewById(R.id.listview);
        mTextView = (TextView) view.findViewById(R.id.textview);

        mPresenter = new MessagePresenter(mActivity, this);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        //更新数据
        mPresenter.getContact();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor c = mAdapter.getCursor();
        c.moveToPosition(position);
        // 拿到jid(账号)-->发送消息的时候需要
        String account = c.getString(c.getColumnIndex(SmsOpenHelper.SmsTable.SESSION_ACCOUNT));
        // 拿到nickName-->显示效果

        mPresenter.chat(account);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        initPopupWindow(view, new Runnable() {
            @Override
            public void run() {
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

    private void initPopupWindow(View view, final Runnable runnable) {
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
                runnable.run();
            }
        });
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
            mPresenter.getContact();
        }
    }


    @Override
    public void setPresenter(MessageContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void showContact(Cursor cursor) {
        if (mAdapter != null) {
            // 刷新adapter就行了
            mAdapter.getCursor().requery();
            return;
        }

        // 假如没有数据的时候
        if (cursor == null || cursor.getCount() <= 0) {
            mListView.setVisibility(View.GONE);
            mTextView.setVisibility(View.VISIBLE);
            return;
        }
        mListView.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.GONE);

        mAdapter = new MessageCursorAdapter(getActivity(), cursor);
        mListView.setAdapter(mAdapter);
    }


}

package cn.qingfeng.loveletter.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.TextView;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.dbhelper.ContactOpenHelper;
import cn.qingfeng.loveletter.provider.ContactProvider;
import cn.qingfeng.loveletter.service.IMService;
import cn.qingfeng.loveletter.ui.activity.DetailActivity;
import cn.qingfeng.loveletter.ui.base.BaseFragment;
import cn.qingfeng.loveletter.ui.view.ListViewWidthHeader;
import cn.qingfeng.loveletter.ui.view.QuickIndexBar;
import cn.qingfeng.loveletter.utils.ThreadUtils;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:36
 * @DESC:         联系人
 * @VERSION:      V1.0
 */
public class ContactFragment extends BaseFragment {

    private ListViewWidthHeader mListView;
    private QuickIndexBar mQuickIndexBar;
    private TextView mToast;

    private ContentObserver mContentObserver = new MyContentObserver(new Handler());
    private MyCursorAdapter mCursorAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity.getContentResolver().registerContentObserver(ContactProvider.URI_CONTACT, true, mContentObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity.getContentResolver().unregisterContentObserver(mContentObserver);
    }

    @Override
    protected View initUi() {
        View view = View.inflate(mActivity, R.layout.fragment_contact, null);
        mListView = (ListViewWidthHeader) view.findViewById(R.id.listview);
        mQuickIndexBar = (QuickIndexBar) view.findViewById(R.id.quickIndexBar);
        mToast = (TextView) view.findViewById(R.id.toast);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        setOrUpdateAdapter();
    }

    @Override
    protected void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mCursorAdapter.getCursor();
                boolean b = cursor.moveToPosition(position);
                if (b) {
                    //将当前点击的联系人的数据带过去
                    String account = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
                    String nickname = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));
                    Intent intent = new Intent(mActivity, DetailActivity.class);
                    intent.putExtra(ContactOpenHelper.ContactTable.ACCOUNT, account);
                    intent.putExtra(ContactOpenHelper.ContactTable.NICKNAME, nickname);

                    startActivity(intent);
                }
            }
        });
        mQuickIndexBar.setOnLetterChangeListener(new QuickIndexBar.OnLetterChangeListener() {
            @Override
            public void onLetterChange(String letter) {
                mToast.setVisibility(View.VISIBLE);
                mToast.setText(letter);
                ThreadUtils.runOnUiThreadDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mToast.setVisibility(View.GONE);
                    }
                }, 1500);

                // 根据字母定位ListView, 找到集合中第一个以letter为拼音首字母的对象,得到索引
                for (int i = 0; i < mCursorAdapter.getCount(); i++) {
                    Cursor cursor = (Cursor) mCursorAdapter.getItem(i);
                    String index = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.PINYIN));
                    index = index.substring(0, 1).toUpperCase();

                    if (TextUtils.equals(letter, index)) {
                        // 匹配成功
                        mListView.setSelection(i + 1);
                        break;
                    }
                }
            }
        });
    }

    //设置或者更新Adapter
    private void setOrUpdateAdapter() {
        // 判断adapter是否存在
        if (mCursorAdapter != null) {
            // 更新adpter
            mCursorAdapter.getCursor().requery();
            return;
        }
        ThreadUtils.runOnThread(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = getContext().getContentResolver().
                        query(ContactProvider.URI_CONTACT, null,
                                "my_account = ?",
                                new String[]{IMService.ACCOUNT}, "pinyin ASC");

                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCursorAdapter = new MyCursorAdapter(mActivity, cursor);
                        mListView.setAdapter(mCursorAdapter);
                    }
                });
            }
        });
    }

    /**
     * 操作从数据库查询出来的数据的适配器
     */
    private class MyCursorAdapter extends CursorAdapter {

        public MyCursorAdapter(Context context, Cursor c) {
            super(context, c);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return View.inflate(context, R.layout.item_contact_fragment, null);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView tvNickname = (TextView) view.findViewById(R.id.nickname);
            TextView tvAccount = (TextView) view.findViewById(R.id.account);
            TextView tvIndex = (TextView) view.findViewById(R.id.tv_index);

            String acccount =
                    cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.ACCOUNT));
            String nickName =
                    cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.NICKNAME));
            String currentIndex = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.PINYIN));
            currentIndex = currentIndex.substring(0, 1).toUpperCase();


            tvNickname.setText(nickName);
            tvAccount.setText(acccount);
            tvIndex.setText(currentIndex);

            if (!cursor.moveToPrevious()) {
                tvIndex.setVisibility(View.VISIBLE);
            } else {
                String preIndex = cursor.getString(cursor.getColumnIndex(ContactOpenHelper.ContactTable.PINYIN));
                preIndex = preIndex.substring(0, 1).toUpperCase();
                if (currentIndex.equals(preIndex)) {
                    tvIndex.setVisibility(View.GONE);
                } else {
                    tvIndex.setVisibility(View.VISIBLE);
                }
                cursor.moveToNext();
            }


        }
    }

    /**
     * 内容观察者
     */
    private class MyContentObserver extends ContentObserver {
        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public MyContentObserver(Handler handler) {
            super(handler);
            System.out.println("注册内容观察者");
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            //数据发生改变的时候 刷新列表
            setOrUpdateAdapter();
        }

    }
}


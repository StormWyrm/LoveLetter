package cn.qingfeng.loveletter.main;

import android.app.Activity;
import android.database.Cursor;

import cn.qingfeng.loveletter.common.util.ThreadUtil;
import cn.qingfeng.loveletter.provider.ContactProvider;
import cn.qingfeng.loveletter.service.IMService;

/**
 * Created by liqingfeng on 2017/5/22.
 */

public class ContactPresenter implements ContactContract.Presenter {
    private Activity mAcitivity;
    private ContactContract.View mView;

    public ContactPresenter(Activity acitivity, ContactContract.View view) {
        this.mAcitivity = acitivity;
        this.mView = view;
    }

    @Override
    public void getContact() {
        ThreadUtil.runOnThread(new Runnable() {
            @Override
            public void run() {
                final Cursor cursor = mAcitivity.getContentResolver().
                        query(ContactProvider.URI_CONTACT, null,
                                "my_account = ?",
                                new String[]{IMService.ACCOUNT}, "pinyin ASC");

                ThreadUtil.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mView.showContact(cursor);
                    }
                });
            }
        });
    }
}

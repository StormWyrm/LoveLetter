package cn.qingfeng.loveletter.main;

import android.database.Cursor;

import cn.qingfeng.loveletter.common.ui.BasePresenter;
import cn.qingfeng.loveletter.common.ui.BaseView;

/**
 * Created by liqingfeng on 2017/5/22.
 */

public class ContactContract {
    public interface Presenter extends BasePresenter{
        void getContact();
    }

    public interface View extends BaseView<ContactContract.Presenter>{
        void showContact(Cursor cursor);
    }
}

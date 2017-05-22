package cn.qingfeng.loveletter.main;

import android.database.Cursor;

import cn.qingfeng.loveletter.common.ui.BasePresenter;
import cn.qingfeng.loveletter.common.ui.BaseView;

/**
 * Created by liqingfeng on 2017/5/22.
 */

public class MessageContract {
    public interface Presenter extends BasePresenter{
        void getContact();

        void chat(String account);

        void deleteSession(String clickAccount);
    }

    public interface View extends BaseView<MessageContract.Presenter>{
        void showContact(Cursor cursor);
    }
}

package cn.qingfeng.loveletter.chat;

import android.database.Cursor;

import cn.qingfeng.loveletter.common.ui.BasePresenter;
import cn.qingfeng.loveletter.common.ui.BaseView;

/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2017/6/13 14:49
 * @DESC: $TODO
 * @VERSION: V1.0
 */
public class ChatContract {
    public interface Presenter extends BasePresenter {
        void getDialogueMessage(String clickAccout);

        void sendMessage(String clickAccout);

        void bindIMService();

        void unbindIMService();
    }

    public interface View extends BaseView<Presenter> {
        void showDialogueMessage(Cursor cursor);

        String getMessage();

        void clearMessage();
    }
}

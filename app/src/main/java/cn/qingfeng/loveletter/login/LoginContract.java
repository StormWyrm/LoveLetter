package cn.qingfeng.loveletter.login;

import cn.qingfeng.loveletter.common.ui.BasePresenter;
import cn.qingfeng.loveletter.common.ui.BaseView;

/**
 * Created by liqingfeng on 2017/5/22.
 */

public class LoginContract {
    public interface View extends BaseView<LoginContract.Presenter> {
        void showServerError();

        void showLoginError();

        void finishActivity();
    }

    public interface Presenter extends BasePresenter {
        void login(String username, String password, String ip);

        void jumpToRegisterPage();
    }
}

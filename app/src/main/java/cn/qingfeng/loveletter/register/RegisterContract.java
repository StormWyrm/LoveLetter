package cn.qingfeng.loveletter.register;

import cn.qingfeng.loveletter.common.ui.BasePresenter;
import cn.qingfeng.loveletter.common.ui.BaseView;

/**
 * Created by liqingfeng on 2017/5/22.
 */

public class RegisterContract {
    public interface Presenter extends BasePresenter {
        void register(String username, String password, String ip);
    }

    public interface View extends BaseView<RegisterContract.Presenter> {
        void serverError();

        void registerSuccess(String username, String password,String ip);

        void registerFailure();

        void userExit();
    }
}

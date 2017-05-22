package cn.qingfeng.loveletter.splash;

import cn.qingfeng.loveletter.common.ui.BasePresenter;
import cn.qingfeng.loveletter.common.ui.BaseView;

/**
 * Created by liqingfeng on 2017/5/22.
 */

public class SplashContract {
    public interface View extends BaseView<SplashContract.Presenter> {

    }

    public interface Presenter extends BasePresenter {
        void endSplash();
    }
}

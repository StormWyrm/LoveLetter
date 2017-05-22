package cn.qingfeng.loveletter.common.ui;

/**
 * Created by liqingfeng on 2017/5/22.
 */

public interface BaseView<T extends BasePresenter> {

    public void setPresenter(T presenter);
}

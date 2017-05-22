package cn.qingfeng.loveletter.register;

import cn.qingfeng.loveletter.common.util.ThreadUtil;
import cn.qingfeng.loveletter.common.util.XmppUtil;

/**
 * Created by liqingfeng on 2017/5/22.
 */

public class RegisterPresenter implements RegisterContract.Presenter {
    private RegisterContract.View mView;

    public RegisterPresenter(RegisterContract.View view) {
        this.mView = view;
        this.mView.setPresenter(this);
    }

    @Override
    public void register(final String username, final String password) {
        ThreadUtil.runOnThread(new Runnable() {
            @Override
            public void run() {
                boolean b = XmppUtil.conServer();
                if (!b) {
                    mView.serverError();
                    return;
                }
                int id = XmppUtil.regist(username, password);
                switch (id) {
                    case 0:
                        //服务器出现异常
                        mView.serverError();
                        break;
                    case 1:
                        ThreadUtil.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mView.registerSuccess(username, password);
                            }
                        });
                        break;
                    case 2:
                        //用户已经存在
                        mView.userExit();
                        break;
                    case 3:
                        //注册失败
                        mView.registerFailure();
                        break;
                }
            }
        });
    }
}

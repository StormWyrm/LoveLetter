package cn.qingfeng.loveletter.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2016/11/14 20:33
 * @DESC: fragment的基类 实现Fragment的懒加载以及contentView的复用问题的解决
 * @VERSION: V1.0
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = "BaseFragment";
    private boolean isViewCreate;//判断view是否已经创建
    private boolean isDataLoad;//判断数据是否加载过
    protected BaseActivity mActivity;
    protected View mContentView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(TAG, this+ "onAttach: ");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (mActivity == null) {
            mActivity = (BaseActivity) getActivity();
        }
        Log.i(TAG, this+ "onCreate: ");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mContentView == null) {
            mContentView = initUi();
        } else {
            removeContentView(mContentView);
        }
        isViewCreate = true;
        Log.i(TAG, this+ "onCreateView: ");
        return mContentView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewCreate && !isDataLoad) {
            initData();
            initListener();
            Log.i(TAG, this+ "setUserVisibleHint: 加载数据");
        }
    }


    /**
     * 第一页的数据的创建的时候不用被调用
     * 我们在这让其调用
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getUserVisibleHint() && isViewCreate && !isDataLoad) {
            initData();
            initListener();
            Log.i(TAG, this + "onActivityCreated: 加载数据");
        }

    }


    protected abstract View initUi();

    protected void initData() {
        isDataLoad = true;
    }

    protected void initListener() {
    }

    private void removeContentView(View view) {
        ViewParent viewParent = mContentView.getParent();
        if (viewParent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) viewParent;
            viewGroup.removeView(mContentView);
        }
    }
}

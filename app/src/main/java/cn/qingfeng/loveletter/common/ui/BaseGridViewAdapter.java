package cn.qingfeng.loveletter.common.ui;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.List;

import cn.qingfeng.loveletter.model.EmotionModel;


/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2016/11/26 16:09
 * @DESC: GridView的adapter
 * @VERSION: V1.0
 */
public abstract class BaseGridViewAdapter extends BaseAdapter {
    private List<EmotionModel> mDatas;
    private Context mContext;

    /**
     * 页数下标,从0开始(当前是第几页)
     */
    private int curIndex;
    /**
     * 每一页显示的个数
     */
    private int pageSize;

    public BaseGridViewAdapter(Context context, List<EmotionModel> mDatas, int curIndex, int pageSize) {
        this.mContext = context;
        this.mDatas = mDatas;
        this.curIndex = curIndex;
        this.pageSize = pageSize;
    }

    /**
     * 先判断数据集的大小是否足够显示满本页？mDatas.size() > (curIndex+1)*pageSize,
     * 如果够，则直接返回每一页显示的最大条目个数pageSize,
     * 如果不够，则有几项返回几,(mDatas.size() - curIndex * pageSize);(也就是最后一页的时候就显示剩余item)
     */
    @Override
    public int getCount() {
        return mDatas.size() > (curIndex + 1) * pageSize ? pageSize : (mDatas.size() - curIndex * pageSize);

    }

    @Override
    public EmotionModel getItem(int position) {
        return mDatas.get(position + curIndex * pageSize);
    }

    @Override
    public long getItemId(int position) {
        return position + curIndex * pageSize;
    }


}

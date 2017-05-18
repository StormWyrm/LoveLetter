package cn.qingfeng.loveletter.chat;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.util.ToastUtil;
import cn.qingfeng.loveletter.model.EmotionModel;
import cn.qingfeng.loveletter.common.ui.BaseChatFragment;
import cn.qingfeng.loveletter.common.ui.BaseGridViewAdapter;


/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2016/11/26 10:55
 * @DESC: 管理聊天界面更多的fragment
 * @VERSION: V1.0
 */
public class ChatMoreFragment extends BaseChatFragment {
    private int eachPageSize = 8;
    private String[] contents = {"图片", "小视频", "位置", "白板", "语音聊天", "视频聊天", "我的收藏", "位置", "名片"};

    @Override
    protected void initGridViewsAndDatas() {
        for (int i = 0; i < contents.length; i++) {
            int resourceId = mActivity.getResources().getIdentifier("more_picture" + i, "drawable", mActivity.getPackageName());
            mDatas.add(new EmotionModel(contents[i], resourceId));
        }

        pageCount = (int) Math.ceil(mDatas.size() * 1.0 / eachPageSize);
        for (int i = 0; i < pageCount; i++) {
            GridView gridView = new GridView(mActivity);
            gridView.setNumColumns(4);
            gridView.setGravity(Gravity.CENTER);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ToastUtil.showToast(mActivity, mDatas.get((int) l).getName());
                }
            });
            gridView.setAdapter(new MoreGridViewAdapter(getContext(), mDatas, i, eachPageSize));
            gridViews.add(gridView);
        }
    }
    private class MoreGridViewAdapter extends BaseGridViewAdapter {

        public MoreGridViewAdapter(Context context, List<EmotionModel> mDatas, int curIndex, int pageSize) {
            super(context, mDatas, curIndex, pageSize);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.item_more_gridview, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv);
                holder.textView = (TextView) convertView.findViewById(R.id.tv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imageView.setImageResource(getItem(position).getIconRes());
            holder.textView.setText(getItem(position).getName());
            return convertView;
        }


        class ViewHolder {
            ImageView imageView;
            TextView textView;
        }
    }

}

package cn.qingfeng.loveletter.chat;


import android.content.Context;
import android.text.SpannableString;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.ui.BaseChatFragment;
import cn.qingfeng.loveletter.common.ui.BaseGridViewAdapter;
import cn.qingfeng.loveletter.common.util.EmotionUtil;
import cn.qingfeng.loveletter.common.util.SpanStringUtil;
import cn.qingfeng.loveletter.model.EmotionModel;


/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2016/11/26 10:54
 * @DESC: 管理表情的Fragment
 * @VERSION: V1.0
 */
public class ChatEmotionFragment extends BaseChatFragment {

    private int eachPageSize = 21;

    @Override
    protected void initGridViewsAndDatas() {
        HashMap<String, Integer> emojiMap = EmotionUtil.getEmojiMap(EmotionUtil.EMOTION_CLASSIC_TYPE);
        Set<Map.Entry<String, Integer>> entries = emojiMap.entrySet();
        for (Map.Entry<String, Integer> me : entries) {
            mDatas.add(new EmotionModel(me.getKey(), me.getValue()));
        }

        pageCount = (int) Math.ceil(mDatas.size() * 1.0/ eachPageSize);
        for (int i = 0; i < pageCount; i++) {
            GridView gridView = new GridView(mActivity);
            gridView.setNumColumns(7);
            gridView.setGravity(Gravity.CENTER);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ChatActivity chatActivity = (ChatActivity) getActivity();
                    String content = chatActivity.getText();
                    content += mDatas.get((int) l).getName();
                    SpannableString emotionContent = SpanStringUtil.getEmotionContent(EmotionUtil.EMOTION_CLASSIC_TYPE, getContext(), chatActivity.getEdit(), content);
                    chatActivity.setText(emotionContent);
                }
            });
            gridView.setAdapter(new EmotionGridViewAdapter(getContext(), mDatas, i, eachPageSize));
            gridViews.add(gridView);
        }
    }

    private class EmotionGridViewAdapter extends BaseGridViewAdapter {

        public EmotionGridViewAdapter(Context context, List<EmotionModel> mDatas, int curIndex, int pageSize) {
            super(context, mDatas, curIndex, pageSize);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = View.inflate(mActivity, R.layout.item_emotion_gridview, null);
                holder = new ViewHolder();
                holder.imageView = (ImageView) convertView.findViewById(R.id.iv);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.imageView.setImageResource(getItem(position).getIconRes());
            return convertView;
        }


        class ViewHolder {
            ImageView imageView;

        }
    }

}

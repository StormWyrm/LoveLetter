package cn.qingfeng.loveletter.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import cn.qingfeng.loveletter.R;


/**
 * @AUTHER: 李青峰
 * @EMAIL: 1021690791@qq.com
 * @PHONE: 18045142956
 * @DATE: 2016/11/29 22:37
 * @DESC: 自定义View----设置界面条目
 * @VERSION: V1.0
 */
public class SettingView extends FrameLayout {
    private int drawableId;
    private String text;
    private int padding;
    private TextView textView;

    public SettingView(Context context) {
        this(context,null);

    }

    public SettingView(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public SettingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClickable(true);
        setBackgroundResource(R.drawable.selector_click_bg);
        View view = View.inflate(context, R.layout.layout_setting_view, this);
        textView = (TextView) view.findViewById(R.id.tv_setting_view);
        getAttributesParams(context,attrs,defStyleAttr);
    }
    private void getAttributesParams(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SettingView, defStyleAttr, 0);
        text = typedArray.getString(R.styleable.SettingView_text);
        drawableId = typedArray.getResourceId(R.styleable.SettingView_drawableLeft,-1);
        padding = (int) typedArray.getDimension(R.styleable.SettingView_padding,-1);
        if(!TextUtils.isEmpty(text)){
            textView.setText(text);
        }
        if(drawableId != -1){
            Drawable drawable = getResources().getDrawable(drawableId);
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable,null,null,null);
        }
        if(drawableId != -1){
            textView.setPadding(padding,padding,padding,padding);
        }
        typedArray.recycle();
    }

}

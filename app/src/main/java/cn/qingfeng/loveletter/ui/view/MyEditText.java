package cn.qingfeng.loveletter.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.qingfeng.loveletter.R;

import static cn.qingfeng.loveletter.R.id.et;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:40
 * @DESC:         自定义EditText----能够根据当前EditText的状态显示全部清除按钮
 * @VERSION:      V1.0
 */
public class MyEditText extends RelativeLayout {
    private EditText mEditText;
    private ImageView mImageView;

    private String hintContent;
    private int textType;
    private int passwordType;

    public MyEditText(Context context) {
        this(context, null);
    }

    public MyEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttributeParams(context, attrs, defStyleAttr);
        init();
        initListener();
    }

    private void getAttributeParams(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyEditText, defStyleAttr, 0);
        hintContent = typedArray.getString(R.styleable.MyEditText_hint);
        textType = typedArray.getInt(R.styleable.MyEditText_textType, 0);
        passwordType = typedArray.getInt(R.styleable.MyEditText_passwordType, 0);
        typedArray.recycle();

    }


    private void init() {
        View view = View.inflate(getContext(), R.layout.layout_et, this);
        mEditText = (EditText) view.findViewById(et);
        mImageView = (ImageView) view.findViewById(R.id.iv);
        if(!TextUtils.isEmpty(hintContent)){
            mEditText.setHint(hintContent);
        }
        switch (textType) {
            case 1:
                mEditText.setInputType(InputType.TYPE_CLASS_DATETIME);
                break;
            case 2:
                mEditText.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case 3:
                mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case 4:
                mEditText.setInputType(InputType.TYPE_CLASS_TEXT);
                break;
        }
        switch (passwordType) {
            case 1:
                mEditText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                break;
            case 2:
                mEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
        }

    }

    private void initListener() {
        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    mImageView.setVisibility(View.GONE);
                } else {
                    mImageView.setVisibility(View.VISIBLE);
                }
            }
        });
        mImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText.setText("");
            }
        });
    }

    public void setText(String text) {
        if (!TextUtils.isEmpty(text)){
            mEditText.setText(text);
            mEditText.setSelection(text.length());
        }

    }

    public String getText() {
        return mEditText.getText().toString();
    }

    public EditText getEditText() {
        return mEditText;
    }


}

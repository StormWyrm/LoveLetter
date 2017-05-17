package cn.qingfeng.loveletter.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.utils.DensityUtils;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:41
 * @DESC:         自定义View---索引条
 * @VERSION:      V1.0
 */
public class QuickIndexBar extends View {
    private Paint mPaint;
    private int mBarWidth;
    private float mBarHeight;
    private String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I"
            , "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z"};
    private int mScreenHeight;
    private int mIndex = -1;
    private OnLetterChangeListener onLetterChangeListener;

    public interface OnLetterChangeListener {
        void onLetterChange(String letter);
    }

    public void setOnLetterChangeListener(OnLetterChangeListener onLetterChangeListener) {
        this.onLetterChangeListener = onLetterChangeListener;
    }

    public QuickIndexBar(Context context) {
        this(context, null);
    }

    public QuickIndexBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickIndexBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(DensityUtils.dp2px(context, 14));

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mScreenHeight = getMeasuredHeight();
        mBarHeight = mScreenHeight * 1.0f / letters.length;
        mBarWidth = getMeasuredWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < letters.length; i++) {
            // 根据按下的字母, 设置画笔颜色
            int color = getResources().getColor(R.color.colorTheme);
            mPaint.setColor(mIndex == i ? color : Color.BLACK);

            int left = (int) (mBarWidth / 2 - mPaint.measureText(letters[i]) / 2);
            Rect bounds = new Rect();
            mPaint.getTextBounds(letters[i], 0, 0, bounds);
            int top = (int) (mBarHeight / 2 + bounds.height() / 2 + i * mBarHeight);
            canvas.drawText(letters[i], left, top, mPaint);
//            System.out.println("left:" + left + ";top:" + top);
//            System.out.println("measureText:" + mPaint.measureText(letters[i]) + ";TextBounds:" + bounds.width() + ";" + bounds.height());

        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = -1;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                float downY = event.getY();
                index = (int) (downY / mBarHeight);
                if (index >= 0 && index < letters.length) {
                    if (onLetterChangeListener != null) {
                        onLetterChangeListener.onLetterChange(letters[index]);
                    }
                    mIndex = index;
                    invalidate();
                }

                break;
            case MotionEvent.ACTION_MOVE:
                float moveY = event.getY();
                index = (int) (moveY / mBarHeight);
                if (index >= 0 && index < letters.length) {
                    if (index != mIndex) {
                        if (onLetterChangeListener != null) {
                            onLetterChangeListener.onLetterChange(letters[index]);
                        }
                        mIndex = index;
                        postInvalidate();
                    }
                }

                break;
        }
        return true;
    }
}


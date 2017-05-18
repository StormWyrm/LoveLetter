package cn.qingfeng.loveletter.guide;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.nineoldandroids.view.ViewHelper;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.common.ui.BaseActivity;
import cn.qingfeng.loveletter.common.util.DensityUtil;
import cn.qingfeng.loveletter.common.util.SPUtil;
import cn.qingfeng.loveletter.login.LoginActivity;


/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:29
 * @DESC:         引导界面
 * @VERSION:      V1.0
 */
public class GuideActivity extends BaseActivity {
    private ViewPager vp;
    private LinearLayout llDot;
    private LinearLayout llLogin;

    private MyAdapter mAdapter;
    private int[] imageId = {R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4};

    protected void initUi() {
        setContentView(R.layout.activity_guide);
        vp = (ViewPager) findViewById(R.id.vp);
        llDot = (LinearLayout) findViewById(R.id.ll_dot_guide);
        llLogin = (LinearLayout) findViewById(R.id.ll_login_guide);
    }

    protected void initData() {
        mAdapter = new MyAdapter();
        vp.setPageTransformer(true, new DepthPageTransformer());
        vp.setAdapter(mAdapter);
        initDot();
    }

    protected void initListener() {
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                changeDotStatus(position);
                if (position == mAdapter.getCount() - 1) {
                    llLogin.setVisibility(View.VISIBLE);
                } else {
                    llLogin.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 初始化小圆点
     */
    private void initDot() {
        int dotSize = DensityUtil.dp2px(this, 10);
        for (int i = 0; i < imageId.length; i++) {
            ImageView imageView = new ImageView(GuideActivity.this);
            imageView.setImageResource(R.drawable.dot_default);
            llDot.addView(imageView, dotSize, dotSize);
            if (i == 0) {
                imageView.setImageResource(R.drawable.dot_pressed);
                continue;
            }
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.leftMargin = dotSize;
            imageView.setLayoutParams(layoutParams);
        }
    }

    /**
     * 根据当前页码 更改小点的状态
     *
     * @param state
     */
    private void changeDotStatus(int state) {
        for (int i = 0; i < llDot.getChildCount(); i++) {
            ImageView imageView = (ImageView) llDot.getChildAt(i);
            imageView.setImageResource(R.drawable.dot_default);
            if (i == state) {
                imageView.setImageResource(R.drawable.dot_pressed);
            }
        }
    }

    public void enter(View view) {
        startActivity(new Intent(GuideActivity.this, LoginActivity.class));
        SPUtil.put(GuideActivity.this, "isGuided", true);
        finish();
    }

    private class MyAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return imageId.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(GuideActivity.this);
            imageView.setBackgroundResource(imageId[position]);
            container.addView(imageView, ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    private class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                ViewHelper.setAlpha(view, 0);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                ViewHelper.setAlpha(view, 1);
                ViewHelper.setTranslationX(view, 1);
                ViewHelper.setScaleX(view, 1);
                ViewHelper.setScrollY(view, 1);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                ViewHelper.setAlpha(view, 1 - position);
                // Counteract the default slide transition
                ViewHelper.setTranslationX(view, pageWidth * -position);
                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                ViewHelper.setScaleX(view, scaleFactor);
                ViewHelper.setScaleY(view, scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                ViewHelper.setAlpha(view, 0);
            }
        }
    }

}

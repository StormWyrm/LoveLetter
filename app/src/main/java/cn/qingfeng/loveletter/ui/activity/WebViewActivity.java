package cn.qingfeng.loveletter.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import cn.qingfeng.loveletter.R;
import cn.qingfeng.loveletter.ui.base.BaseActivity;



/**
 * @AUTHER:       李青峰
 * @EMAIL:        1021690791@qq.com
 * @PHONE:        18045142956
 * @DATE:         2016/12/1 8:32
 * @DESC:         WebView界面
 * @VERSION:      V1.0
 */
public class WebViewActivity extends BaseActivity {

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private TextView mTextView;

    @Override
    protected void initUi() {
        setContentView(R.layout.activity_web_view);
        mWebView = (WebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.pb);
        mTextView = (TextView) findViewById(R.id.title);
    }

    @Override
    protected void initData() {
        //对WebView进行设置
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);//设置支持js
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);//自适应屏幕
        settings.setBuiltInZoomControls(true);//支持缩放
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            settings.setDisplayZoomControls(false);
        }
        settings.setSupportZoom(true);//设定支持缩放


        Intent intent = getIntent();
        if (intent != null) {
            String url = intent.getStringExtra("url");
            Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
            if (url != null) {
                mWebView.loadUrl(url);
            }
        }
    }

    @Override
    protected void initListener() {
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.setWebChromeClient(new MyWebChromeClient());
    }

    private class MyWebViewClient extends WebViewClient {
        //当网页开始加载的时候调用
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        //当网页加载结束的时候调用
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mProgressBar.setVisibility(View.GONE);
        }

        //当遇到错误的时候调用
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        //
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            mWebView.loadUrl(url);
            return true;
        }
    }
    private class MyWebChromeClient extends WebChromeClient {
        //处理Js中的Alert对话框
        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            return super.onJsAlert(view, url, message, result);
        }
        //处理js中的confirm对话框
        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            return super.onJsConfirm(view, url, message, result);
        }
        //处理js中prompt对话框
        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            return super.onJsPrompt(view, url, message, defaultValue, result);
        }
        //当获取网页的icon时候调用
        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
        }
        //当获取网页的标题的时候调用
        @Override
        public void onReceivedTitle(WebView view, String title) {
            mTextView.setText(title);
        }
        //当网页加载的进度改变的时候调用
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if(newProgress == 100){
                mProgressBar.setVisibility(View.GONE);
            }else{
                mProgressBar.setProgress(newProgress);
            }
        }
    }
    //back按钮点击的处理
    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    public void back(View view){
        finish();
    }
}

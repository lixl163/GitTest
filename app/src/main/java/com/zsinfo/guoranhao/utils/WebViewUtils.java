package com.zsinfo.guoranhao.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.zsinfo.guoranhao.js_interact.SettingInfo;

import static android.os.Looper.getMainLooper;

/**
 * Created by admin on 2017/8/28.
 */

public class WebViewUtils {


    public static void initWebView(MyWebChromeClient mMyWebChromeClient, MyWebViewClient mMyWebViewClient, WebView mWebView, Activity activity) {
        mWebView.requestFocusFromTouch();
        mWebView.setBackgroundColor(Color.parseColor("#00000000"));
        mWebView.setWebChromeClient(mMyWebChromeClient);
        mWebView.setWebViewClient(mMyWebViewClient);
        mWebView.addJavascriptInterface(new SettingInfo(activity,mWebView),"android");
    }


    /**
     * 清除缓存数据
     */
    public static void clearWebViewData(final WebView webView){
        Looper looper = getMainLooper();
        Handler handler=new Handler(looper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                webView.clearCache(true);
                webView.clearFormData();
            }
        });

    }

    /**
     * 清除所有的历史记录
     */
    public static void clearHistroy(final WebView mWebView){
        Looper looper = getMainLooper();
        Handler handler=new Handler(looper);
        handler.post(new Runnable() {
            @Override
            public void run() {
                mWebView.clearHistory();
            }
        });
    }


    /**
     * 初始化webview的settings信息
     */
    public static void initSettings(WebView mWebView,Activity activity) {
        WebSettings settings = mWebView.getSettings();
        String userAgentString = settings.getUserAgentString();
        settings.setUserAgentString(userAgentString+"grh_app/android");
        settings.setAllowContentAccess(true);
        settings.setAppCacheMaxSize(1024*1024*8);//设置缓冲大小，我设的是8M
        String appCacheDir = activity.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        settings.setAppCachePath(appCacheDir);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);  //判断是否有网络，有的话，使用LOAD_DEFAULT，无网络时，使用LOAD_CACHE_ELSE_NETWORK
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //优先使用缓存
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);//  不使用缓存：
        settings.setGeolocationEnabled(true);//支持定位
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //开启 database storage API 功能
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(activity.getCacheDir()+"/guoranhao.db");
        settings.setJavaScriptEnabled(true);//支持js
        settings.setDefaultTextEncodingName("UTF-8");//设置编码方式
        settings.setBuiltInZoomControls(false); //便页面支持缩放：
//        settings.setSupportZoom(true);  //支持缩放
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
//        settings.supportMultipleWindows();  //支持多窗口
        settings.setAllowFileAccess(true);//设置可以访问文件
        settings.setNeedInitialFocus(true);//当webview调用requestFocus时为webview设置节点 暂不知道什么用
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
        //打开页面时， 自适应屏幕：
        settings.setUseWideViewPort(true);//设置此属性，可任意比例缩放 将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        settings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        settings.setDomStorageEnabled(true); //不设置此 无法加载h5
    }
}



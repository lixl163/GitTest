package com.zsinfo.guoranhao.utils;

/**
 * @author cyk
 * @date 2016/12/15 12:04
 * @email choe0227@163.com
 * @desc
 * @modifier
 * @modify_time
 * @modify_remark
 */
public class Constant {
    /**
     * 6.0系统的拍照权限  必须小于16
     */
    public final static int TAKE_PHOTO_PERMISSION = 1;

    public final static String WXPAY_APPID = "wx02afc2a1605d4d78";
    //下一页title值的key
    public static final String NEXT_PAGE_TITLE_NAME = "titleName";
    //下一页的url key值
    public static final String NEXT_PAGE_URL = "pageUrl";
    //下一页的右边的文字值
    public static final String NEXT_PAGE_TITLE_TEXT = "titleRIGHTTEXT";
    //下一页的右边的图片值
    public static final String NEXT_PAGE_TITLE_IMG = "titleRIGHTIMG";
   //下一页的右边的图片值
    public static final String NEXT_PAGE_TITLE_CALLBACK= "titleRIGHTCallBack";
    //刷新首页
    public static final int MainUI_TAB_HOME0 = 0;
    //刷新更多商品
    public static final int MainUI_TAB_HOME1 = 1;
    //刷新购物车
    public static final int MainUI_TAB_HOME2 = 2;
    //刷新整件购买
    public static final int MainUI_TAB_HOME6 = 6;
    //刷新我的页面
    public static final int MainUI_TAB_HOME3 = 3;
    //刷新购物车底部的数标
    public static final int MainUI_SHOPCARNUM = 4;
    //继承自BaseActivity的整个webView刷新
    public static final int BASE_RELOAD = 5;
    //WebViewActivity 间跳转的requestCode
    public static final int REQUESTCODE = 10000;
    //WebViewActivity 间跳转的resultCode
    public static final int RESULTCODE = 10001;
    //用来标志WebViewActivity startActivityForResult 是否需要刷新的key
    public static final String IS_NEED_REFRESH = "isNeedRefresh";
    //用来标志WebViewActivity startActivityForResult 从哪个页面返回的
    public static final String BACK_URL = "back_url";
    //加载框显示的默认超时时间
    public static final long DEFAULT_TIMEOUT = 10000;

    public static final String THEME_TYPE = "theme_type";
}

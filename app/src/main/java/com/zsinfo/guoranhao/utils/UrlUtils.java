package com.zsinfo.guoranhao.utils;

/**
 * Created by admin on 2016/2/23.
 */
public interface UrlUtils {

//    //果然好webview正式地址
//    String URL = "http://wxapp.grhao.com";
//    //ali正式支付回掉
//    public static final String NOTIFY_URL = "https://api.grhao.com/alipay/notify.jsp";
//    //果然好服务器正式地址用来检测更新
//    String SERVE = "https://api.grhao.com/server/api.do";

    //果然好webview测试地址
    String URL = "http://testdata.grhao.cn";
    //ali测试支付回掉
    public static final String NOTIFY_URL = "http://122.228.113.77:8090/grh_api/alipay/notify.jsp";
    //果然好服务器测试地址用来检测更
    String SERVE = "http://122.228.113.77:8090/grh_api/server/api.do";

    //调试页面
    //    String HOME_PAGE="file:///android_asset/test2.html";

    //首页
    String HOME_PAGE = URL + "/index.html";

    //全部
    String MORE_GOOD = URL + "/html/moregoods.html";

    //购物车
    String SHOP_CAR = URL + "/html/cart.html";

    //整件购买
    String WHOLE_BUY = URL + "/html/whole.html";

    //我的
    String ABOUT_ME = URL + "/html/my.html";

    //订单完成界面
    String ORDER_PAY_PAGE = "/html/order_pay.html";

    //预购订金支付
    String ORDER_PRE_PAGE = "/html/order_pay.html?search=pre";

    //订单管理界面
    String ORDER_MANAGE_PAGE = "/html/order_management.html";
    //订单管理的完整的url
    //    String FULL_ORDER_MANAGE_PAGE =URL+ORDER_MANAGE_PAGE;

    //预购管理
    String PRE_ORDER_MANAGE_PAGE = "/html/PreOrder_management.html";
    //预购管理的完整url
    //    String FULL_PRE_ORDER_MANAGE_PAGE=URL+PRE_ORDER_MANAGE_PAGE;

    //在线充值
    String ONLINE_RECHARGE_PAGE = "/html/month_recharge.html?search=recharge";
    //在线充值完整url
    //    String FULL_ONLINE_RECHARGE_PAGE=URL+"/html/month_recharge.html?search=recharge";

    //订单详情
    String PREORDERDETAIL = "/html/preOrderDetail.html";

    //订单详情
    String ORDERDETAILS = "/html/orderDetails.html";

    //需要有下拉刷新的界面集合
    String[] REFRESH_URLS = new String[]{ORDER_MANAGE_PAGE, PRE_ORDER_MANAGE_PAGE, PREORDERDETAIL, ORDERDETAILS};

    //一对cp 返回刷新的cp
    String ADDRESS_URL = "/html/address_management.html";
    String COMMIT_ORDER_URL = "/html/order_set_charge.html";

    //连连的订单管理回调地址（支付成功之后）
    String LIANLIAN_OREDER_MANAGE_ORDER = URL + ORDER_MANAGE_PAGE + "?flag=app";
    //连连的预订单回调地址(预订单支付成功)
    String LIANLIAN_PRE_OREDER_MANAGE_ORDER = URL + PRE_ORDER_MANAGE_PAGE + "?flag=app";
    //连连的在线充值回调地址
    String LIANLIAN_ONLINE_OREDER_MANAGE_ORDER = URL + ONLINE_RECHARGE_PAGE + "?flag=app";
    //连连的三种支付情况下的回调集合(因在线充值不需要进行页面跳转 暂时不加，如后期有其他的特殊处理，在加上做其他的操作)
    String[] LIANLIAN_CALLBACKS = new String[]{LIANLIAN_OREDER_MANAGE_ORDER, LIANLIAN_PRE_OREDER_MANAGE_ORDER, LIANLIAN_ONLINE_OREDER_MANAGE_ORDER};

    //首页需要缓存的两个页面
    String cacheUrl1 = HOME_PAGE;
    String cacheUrl2 = URL;
    String cacheUrl3 = ABOUT_ME;
}

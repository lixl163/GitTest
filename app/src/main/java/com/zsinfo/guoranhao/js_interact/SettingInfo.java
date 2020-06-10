package com.zsinfo.guoranhao.js_interact;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.activitys.MainApplication;
import com.zsinfo.guoranhao.activitys.QRActivity;
import com.zsinfo.guoranhao.activitys.SearchActivity;
import com.zsinfo.guoranhao.activitys.WebviewActivity;
import com.zsinfo.guoranhao.alipay.Alipay;
import com.zsinfo.guoranhao.beans.LoginBean;
import com.zsinfo.guoranhao.beans.SystemConfigBean;
import com.zsinfo.guoranhao.beans.WXBean;
import com.zsinfo.guoranhao.chat.activity.ChatActivity;
import com.zsinfo.guoranhao.chat.utils.MD5Util;
import com.zsinfo.guoranhao.event.BaseEvent;
import com.zsinfo.guoranhao.event.EventDialog;
import com.zsinfo.guoranhao.event.EventLogin;
import com.zsinfo.guoranhao.event.EventUpdateMainUI;
import com.zsinfo.guoranhao.utils.Constant;
import com.zsinfo.guoranhao.utils.LocationUtils;
import com.zsinfo.guoranhao.utils.LogUtils;
import com.zsinfo.guoranhao.utils.SPUtils;
import com.zsinfo.guoranhao.utils.SharedPreferencesUtil;
import com.zsinfo.guoranhao.utils.UrlUtils;
import com.zsinfo.guoranhao.utils.WebViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author cyk
 * @date 2017/2/27 11:43
 * @email choe0227@163.com
 * @desc 原生为js提供的调用方法
 * @modifier
 * @modify_time
 * @modify_remark
 */
public class SettingInfo {
    private Activity mContext;
    private WebView webview;

    public SettingInfo(Activity context, WebView webview) {
        mContext = context;
        this.webview = webview;
    }

    /**
     * 2018/04/08
     * 打开主界面，将服务器返回的肤色type值 传递给h5
     */
    @JavascriptInterface
    public void getChangeSkin() {
        String theme_type = SPUtils.getValue(Constant.THEME_TYPE);
        Map map = new HashMap();
        map.put("type", theme_type);
        final String json = new Gson().toJson(map);
        Log.i("000000000setting", json);

        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (webview != null) {
                    webview.loadUrl("javascript:changeSkin('" + json + "')");
                }
            }
        });

    }

    /**
     * @param result 登录成功时返回的结果信息
     */
    @JavascriptInterface
    public void saveLoginInfo(String result) {
        Log.e("js 给的数据 saveLoginInfo", result);
//        com.zsinfo.guoranhao.chat.utils.SPUtils.setParam(
//                com.zsinfo.guoranhao.chat.utils.SPUtils.updateUserInfo, result);
        // 解析数据
        try {
            String userAccount = result;
            JSONObject object = new JSONObject(result);
            String statusCode = object.optString("statusCode");
            String statusStr = object.optString("statusStr");
            if ("100000".equals(statusCode)) {
                //解析数据
                Gson gson = new Gson();
                LoginBean loginBean = gson.fromJson(userAccount, LoginBean.class);
                String mobile = loginBean.getData().getCuserInfo().getMobile();
                //保存需要的数据
                SharedPreferencesUtil.setUserAccount(mobile);
                SharedPreferencesUtil.setIsLogin(true);
                //注册别名接收推送消息
                MiPushClient.setAlias(MainApplication.context, mobile, null);

                //todo xmpp注册用户--2018/07/16
                //这里可以保存id,name,logo,密码在本地
                final String jid = loginBean.getData().getCuserInfo().getId();
                final String aliasName = loginBean.getData().getCuserInfo().getPetName();
                final String pwd = MD5Util.makeMD5("123456");
                final String logo = loginBean.getData().getCuserInfo().getFaceImg();
                com.zsinfo.guoranhao.chat.utils.SPUtils.setParam(
                        com.zsinfo.guoranhao.chat.utils.SPUtils.loginName, jid);
                com.zsinfo.guoranhao.chat.utils.SPUtils.setParam(
                        com.zsinfo.guoranhao.chat.utils.SPUtils.loginNameAlias, aliasName);
                com.zsinfo.guoranhao.chat.utils.SPUtils.setParam(
                        com.zsinfo.guoranhao.chat.utils.SPUtils.userLogo, logo);
                com.zsinfo.guoranhao.chat.utils.SPUtils.setParam(
                        com.zsinfo.guoranhao.chat.utils.SPUtils.loginPwd, pwd);
//                //①关闭连接
//                XmppConnection.closeConnection();
//                new Thread(new Runnable() {
//                    public void run() {
//                        try {
//                            //②重连服务器
//                            XmppConnection.connection = XmppConnection.getConnection();
//                            //③注册
//                            XmppConnection.regStatus(jid, pwd);
//                            //④登录
//                            XmppConnection.login(jid, pwd);
//                            //⑤设置头像和别名
//                            Bitmap bm = ChatUtils.getbitmap(logo);
//                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                            try {
//                                XmppConnection.updateAvater(baos.toByteArray());
//                                XmppConnection.updateNickname(aliasName);
//                            } catch (XMPPException e) {
//                                e.printStackTrace();
//                                LogUtils.e("XmppConnection VCard-------------", "头像设置失败");
//                            }
//                        } catch (Exception e) {
//                            XmppConnection.closeConnection();
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//                Log.e("登录时保存信息", "电话号码" + mobile);
            } else {
                Log.e("登录时保存信息", "登录是失败的");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出时需要清除的数据
     */
    @JavascriptInterface
    public void exit1(String json) {
        Log.e("js调用", "t推出啦：" + json);
        //退出取消注册别名
        MiPushClient.unsetAlias(MainApplication.context, SharedPreferencesUtil.getUserAccount(), null);
        //并且删除所有保存的数据
        SharedPreferencesUtil.clearAllData();
//        //xmpp断开连接，离线--2018/07/16
//        XmppConnection.closeConnection();
        //清除本地保存的数据
        com.zsinfo.guoranhao.chat.utils.SPUtils.remove(
                com.zsinfo.guoranhao.chat.utils.SPUtils.loginName);
        com.zsinfo.guoranhao.chat.utils.SPUtils.remove(
                com.zsinfo.guoranhao.chat.utils.SPUtils.loginNameAlias);
        com.zsinfo.guoranhao.chat.utils.SPUtils.remove(
                com.zsinfo.guoranhao.chat.utils.SPUtils.userLogo);
        com.zsinfo.guoranhao.chat.utils.SPUtils.remove(
                com.zsinfo.guoranhao.chat.utils.SPUtils.loginPwd);

        // {"callback":"exitCallBack()"}
        JSONObject object = null;
        try {
            object = new JSONObject(json);
            final String exitCallBack = object.optString("callback");

            //手动跳转到"首页-我"
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    webview.loadUrl("javascript:" + exitCallBack);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 去聊天
     */
    @JavascriptInterface
    public void goChat() {
        //跳转聊天界面
        mContext.startActivity(new Intent(mContext, ChatActivity.class));
    }

    /**
     * 更新用户信息
     */
    @JavascriptInterface
    public void updateUserInfo(String result) {
        LogUtils.e("JavascriptInterface-----", "updateUserInfo走了:" + result);
        Log.e("js 给的数据 updateUserInfo", result);
//        com.zsinfo.guoranhao.chat.utils.SPUtils.setParam(
//                com.zsinfo.guoranhao.chat.utils.SPUtils.updateUserInfo, result);
    }

    /**
     * 水果游戏中，提示更新最新apk
     */
    @JavascriptInterface
    public void versionUpdate() {
        EventDialog dialog = new EventDialog();
        dialog.setFlag(2);
        dialog.setUrl("");
        EventBus.getDefault().post(dialog);
    }

    /**
     * 水果游戏分享
     */
    @JavascriptInterface
    public void gameShare(String json) {
        Log.e("水果游戏分享方法中", json);
        String title = "";
        String imgUrl = "";
        String desc = "";
        String linkUrl = "";
        try {
            JSONObject object = new JSONObject(json);
            title = object.optString("title");
            imgUrl = object.optString("imgUrl");
            desc = object.optString("desc");
            linkUrl = object.optString("linkUrl");
            Log.e("水果游戏分享方法中", title + "====" + imgUrl + "====" + desc + "====" + linkUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        UMWeb web = new UMWeb(linkUrl);
        web.setTitle(title);
        web.setThumb(new UMImage(mContext, imgUrl));
        web.setDescription(desc);

        new ShareAction(mContext)
                .withText("hello")
                .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                .withMedia(web)
                .setCallback(umShareListener)
                .open();
    }

    /**
     * 点击调用设置中的分享
     *
     * 2019.4.3
     * WeChat 表示微信,
     * WeChatSpace  表示微信朋友圈,
     * QQ  表示QQ,
     * QZone  表示qq空间
     */
    @JavascriptInterface
    public void share(String json) {
        //{"list":["WeChat","WeChatSpace","QQ","QZone"]}
        Log.e("sss", "调用了分享：" + json);

        UMWeb web = new UMWeb(SharedPreferencesUtil.getAppDownload_android()); //链接
        String appShareMsg = SharedPreferencesUtil.getAppShareMsg();
        String title = appShareMsg.substring(0, SharedPreferencesUtil.getAppShareMsg().indexOf("@"));
        web.setTitle(title);//标题
        web.setThumb(new UMImage(mContext, BitmapFactory.decodeResource(mContext.getResources(), R.drawable.shar)));  //缩略图
        String desc = appShareMsg.substring(SharedPreferencesUtil.getAppShareMsg().indexOf("@") + 1);
        web.setDescription(desc);//描述


        //解析，判断list的数量
        JSONObject object = null;
        try {
            object = new JSONObject(json);
            String list = object.optString("list");
            JSONArray arr = new JSONArray(list);

            SHARE_MEDIA[] displaylist = new SHARE_MEDIA[arr.length()];
            for (int i = 0; i < arr.length(); i ++){
                if (arr.getString(i).equals("WeChat")) {
                    displaylist[i] = SHARE_MEDIA.WEIXIN;
                    continue;
                }
                if (arr.getString(i).equals("WeChatSpace")) {
                    displaylist[i] = SHARE_MEDIA.WEIXIN_CIRCLE;
                    continue;
                }
                if (arr.getString(i).equals("QQ")) {
                    displaylist[i] = SHARE_MEDIA.QQ;
                    continue;
                }
                if (arr.getString(i).equals("QZone")) {
                    displaylist[i] = SHARE_MEDIA.QZONE;
                    continue;
                }
            }

            //分享给指定的平台
            new ShareAction(mContext)
                    .withText("hello")
                    .setDisplayList(displaylist) //SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE
                    .withMedia(web)
                    .setCallback(umShareListener)
                    .open();
        } catch (JSONException e) {
            e.printStackTrace();

            //如果解析抛异常，就直接分享给所有平台
            new ShareAction(mContext)
                    .withText("hello")
                    .setDisplayList(SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withMedia(web)
                    .setCallback(umShareListener)
                    .open();
        }

    }

    /**
     * 获取分享的信息
     * 2019.4.3 每次打开应用的时候都会调用此方法，点击设置
     */
    @JavascriptInterface
    public void getShare(String json) {
        Log.e("系统配置信息", "" + json);
        try {
            JSONObject object = new JSONObject(json);
            String statusCode = object.optString("statusCode");
            String statusStr = object.optString("statusStr");
            if ("100000".equals(statusCode)) {
                Gson gson = new Gson();
                SystemConfigBean systemBean = gson.fromJson(json, SystemConfigBean.class);
                SharedPreferencesUtil.setAppShareMsg(systemBean.getData().getShare_app_strs());
                SharedPreferencesUtil.setAppDownload_android(systemBean.getData().getAndroid_download_uri());
                SharedPreferencesUtil.setAPP_ID(systemBean.getData().getAli_app_id());
                SharedPreferencesUtil.setRSA_PRIVATE(systemBean.getData().getAlipay_rsa_private());
            } else {
                Toast.makeText(mContext, statusStr, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private UMShareListener umShareListener = new UMShareListener() {

        @Override
        public void onStart(SHARE_MEDIA share_media) {

        }

        @Override
        public void onResult(SHARE_MEDIA platform) {
            Log.d("友盟分享", "platform" + platform);
            Toast.makeText(mContext, "成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            Toast.makeText(mContext, "失败"+t.getMessage(), Toast.LENGTH_SHORT).show();
            if (t != null) {
                Log.d("throw", "throw:" + t.getMessage());
            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            Toast.makeText(mContext, platform + "已取消", Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * 支付宝支付
     * @param orderJson
     * @param ordercode
     */
    @JavascriptInterface
    public void GoAliPay(String orderJson, String ordercode) {
        try {
            JSONObject jsonObject = new JSONObject(orderJson);
            Alipay alipay = new Alipay(mContext);
            alipay.zhifu(jsonObject.optString("productName"), jsonObject.optString("orderCode"), jsonObject.optString("money"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 微信支付
     */
    @JavascriptInterface
    public void WXPay(String json, String flag) {
        Log.e("微信支付", "start" + flag);
        try {
            JSONObject jsonObject = new JSONObject(json);
            String statusCode = jsonObject.optString("statusCode");
            String statusStr = jsonObject.optString("statusStr");
            if ("100000".equals(statusCode)) {
                IWXAPI mWxApi = WXAPIFactory.createWXAPI(mContext, Constant.WXPAY_APPID, true);
                MainApplication.flag = flag;
                mWxApi.registerApp(Constant.WXPAY_APPID);
                Gson gson = new Gson();
                WXBean wxBean = gson.fromJson(json, WXBean.class);
                PayReq request = new PayReq();
                request.appId = wxBean.getData().getAppid();

                request.partnerId = wxBean.getData().getPartnerid();

                request.prepayId = wxBean.getData().getPrepayid();

                request.packageValue = wxBean.getData().getPackageN();

                request.nonceStr = wxBean.getData().getNoncestr();

                request.timeStamp = wxBean.getData().getTimestamp();

                request.sign = wxBean.getData().getSign();

                boolean b = mWxApi.sendReq(request);
                Log.e("吊起微信", "" + b);

            } else {
                Toast.makeText(mContext, statusStr, Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * 清除缓存
     */
    @JavascriptInterface
    public void clearCache() {
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setMessage("确定清空缓存?")
                .setTitle("果然好")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        clearCache2();
                        dialogInterface.dismiss();
                        Toast.makeText(mContext, "缓存清理成功", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();

    }

    /**
     * 清除历史记录
     */
    private void clearCache2() {
        WebViewUtils.clearWebViewData(webview);
        //删除app缓存
        String appCacheDir = mContext.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        File appCache = new File(appCacheDir);
        if (appCache.exists()) {
            deleteFile(appCache);
        }

        //删除webview的数据库信息

        //删除缓存的html页面 以及缓存的图片信息
        File filesDir = new File(Environment.getExternalStorageDirectory() + "/guoranhao");
        if (filesDir.exists()) {
            deleteFile(filesDir);
        }

        //清除sharedPreference信息 看场景需要是否需要清楚这个缓存
//        SharedPreferencesUtil.clearAllData();

        //需要重新缓存数据
        MainApplication.isNeedCacheAboutMe = true;
        MainApplication.isNeedCacheHomePage = true;
    }

    /**
     * 递归删除 文件/文件夹
     *
     * @param file
     */
    private void deleteFile(File file) {

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        } else {
            Log.e("SettingInfo", "delete file no exists " + file.getAbsolutePath());
        }
    }

    //跳转到搜索界面
    @JavascriptInterface
    public void goToSearch(String url) {
        Intent intent = new Intent(mContext, SearchActivity.class);
        intent.putExtra(Constant.NEXT_PAGE_URL, url);
        mContext.startActivity(intent);
    }

    //跳转到上一级界面
    @JavascriptInterface
    public void goBack(String json) {
//       {'hierarchy':1,'reload':1,'url':'url','refreshUrl':""}
        Log.e("goBack", "" + json);
        SettingInfoUtils.goBack(json, mContext);
    }

    //接收onShow()方法
    @JavascriptInterface
    public void registerOnshow(final String json){
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //获取当前的url地址
                String url = webview.getUrl();
                Log.e("2019.8.13--Onshow", "截取之前：" + url + "---" + json);
                //开始进行字符串截取
                String subUrl = url.substring(UrlUtils.URL.length(), url.length());
                Log.e("2019.8.13--Onshow", "截取之后：" + subUrl + "---" + json);
                //本地存储，每个webview对应一个方法名
                SharedPreferencesUtil.SetJSMethod(subUrl, json);
            }
        });
    }

    /**
     * 跳转到下一个页面
     *
     * @param json 下一个页面的标题 及加载的url信息，例如果币商城
     */
    @JavascriptInterface
    public void goToNextLevel(String json) {
        //{"title":"秒杀活动","url":"/html/seckill.html"}
        Log.e("json", "" + json);
        SettingInfoUtils.goToNextLevel(json, mContext);
    }

    /*** 跳转到下一个页面
     *
     * @param json 下一个页面的 及加载的url信息，例如优惠券，账户充值
     */
    @JavascriptInterface
    public void jumpLinkCustom(String json) {
        //{"title":"优惠券","url":"//html/couponList.html","imgIcon":"http://testdata.grhao.cn/img/icon_Invalid.png","callBack":"pub.couponList.apiHandle.jumpLink()"}
        Log.e("json", "" + json);
        SettingInfoUtils.jumpLinkCustomApp(json, mContext);
    }

    /**
     * Fragment跳转
     *
     * @param json 当前主界面中底部4个按钮界面的跳转，0首页，1更多商品，2整件购买，3我
     */
    @JavascriptInterface
    public void goFirstView(String json) {
        //{"navIndex":3}
        Log.e("json", "" + json);
        SettingInfoUtils.goToFirstLevel(json, mContext);
    }

    /**
     * 返回首页的第一个tab 且不刷新
     */
    @JavascriptInterface
    public void goHome() {
        //跳转到首页 且不刷新
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new EventUpdateMainUI(Constant.MainUI_TAB_HOME0, false));
                MainApplication.backHome();
            }
        });
    }

    /**
     * 刷新购物车数量
     * @param num 购物车商品的数量
     */
    @JavascriptInterface
    public void setShopCarNum(String num) {
        SharedPreferencesUtil.setShopCarNum(Integer.parseInt(num));
        EventUpdateMainUI event = new EventUpdateMainUI(Constant.MainUI_SHOPCARNUM, true);
        event.setNum(num);
        EventBus.getDefault().post(event);
        // 购物车数量发生变化的时候 用来通知购物车是否需要刷新
        if (!MainApplication.isClickToRefreshShopCar) {
            MainApplication.isClickToRefreshShopCar = true;
        }
    }


    /**
     * 购物车不在当前页面 刷新购物车数量
     * @param num 购物车商品的数量
     */
    @JavascriptInterface
    public void setShopCarNum_ShoppingCart(String num) {
        SharedPreferencesUtil.setShopCarNum(Integer.parseInt(num));
        EventUpdateMainUI event = new EventUpdateMainUI(Constant.MainUI_SHOPCARNUM, true);
        event.setNum(num);
        EventBus.getDefault().post(event);
        if (!MainApplication.isClickToRefreshMoreGoods) {
            MainApplication.isClickToRefreshMoreGoods = true;
        }
    }



    /**
     * 将点击的搜索门类设置到搜索框中去
     */
    @JavascriptInterface
    public void searchHot(final String msg) {
        if (mContext instanceof SearchActivity) {
            final SearchActivity searchActivity = (SearchActivity) mContext;
            searchActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    searchActivity.setEditText(msg);
                }
            });

        }
    }


    /**
     * 点机空购物车去购物 设置tab栏改变
     */
    @JavascriptInterface
    public void changeTabBarItem(int tabIndex) {
        try {
            EventUpdateMainUI ui = new EventUpdateMainUI(tabIndex, false);
            EventBus.getDefault().post(ui);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 将点击的搜索门类设置到搜索框中去
     */
    @JavascriptInterface
    public void didLogin() {
        EventLogin event = new EventLogin();
        event.setFlag(0);
        EventBus.getDefault().post(event);
    }



    /**
     * 登录成功之后刷新
     */
    @JavascriptInterface
    public void tellRefresh() {
        EventLogin event = new EventLogin();
        event.setFlag(0);
        EventBus.getDefault().post(event);
    }


    /**
     * 用来设置返回上一个界面 上一个界面是否需要刷新
     */
    @JavascriptInterface
    public void noticeRefresh() {
        if (mContext instanceof WebviewActivity) {
            WebviewActivity webviewActivity = (WebviewActivity) mContext;
            webviewActivity.setNeedRefresh(true);
        }
    }

    /**
     * 显示dialog
     */
    @JavascriptInterface
    public void showDialog() {
        int flag = 0;
        SettingInfoUtils.operateDialog(0, mContext);
    }

    /**
     * 隐藏dialog
     */
    @JavascriptInterface
    public void cancelDialog() {
        int flag = 1;
        SettingInfoUtils.operateDialog(1, mContext);
    }

    /**
     * 重新定位
     */
    @JavascriptInterface
    public void replaceLocation() {
        Log.e("replaceLocation", "replaceLocation: ");
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LocationUtils.startLocation(webview, mContext);
            }
        });
    }


    //{'type':1,'title':'标题','canclefn':'你那边要调用的取消的函数','truefn':'你那边要调用的确定的函数'}
    @JavascriptInterface
    public void alertMask(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int type = jsonObject.optInt("type");
            String title = jsonObject.optString("title");
            com.zsinfo.guoranhao.widget.AlertDialog alertDialog = null;
            if (type == 1) { // 有确定与取消按钮的
                final String functionConfirmName = jsonObject.optString("truefn");
                final String functionCancleName = jsonObject.optString("canclefn");
                alertDialog = new com.zsinfo.guoranhao.widget.AlertDialog(mContext)
                        .builder()
                        .setCancelable(false)
                        .setMsg(title)
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mContext.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        webview.loadUrl("javascript:" + functionConfirmName + "()");
                                    }
                                });

                            }
                        })
                        .setNegativeButton("取消", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mContext.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        webview.loadUrl("javascript:" + functionCancleName + "()");
                                    }
                                });

                            }
                        });
            } else if (type == 2) { //只有确定按钮
                final String functionConfirmName = jsonObject.optString("truefn");
                alertDialog = new com.zsinfo.guoranhao.widget.AlertDialog(mContext)
                        .builder()
                        .setCancelable(false)
                        .setMsg(title)
                        .setPositiveButton("确定", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mContext.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        webview.loadUrl("javascript:" + functionConfirmName + "()");
                                    }
                                });
                            }
                        });
            }

            if (alertDialog != null) {
                alertDialog.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Toast mToast;
    private UMAuthListener authListener = new UMAuthListener() {
        /**
         * @desc 授权开始的回调
         * @param platform 平台名称
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
            mToast = Toast.makeText(mContext, "正在前往微信...", Toast.LENGTH_SHORT);
            mToast.show();
        }

        /**
         * @desc 授权成功的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param data 用户资料返回
         */
        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {

            Log.e("Conker", "action: =" + action + "==" + data);
            if (action == 2) {
                if (data.size() > 0) {
                    String temp = "";
                    temp += "{";
                    for (String key : data.keySet()) {
                        temp += "\"" + key + "\":\"" + data.get(key) + "\",";
                    }
                    temp = temp.substring(0, temp.length() - 1);
                    temp += "}";
                    webview.loadUrl("javascript:trueFn('" + temp + "')");
                    Log.e("Conker", action + "==action;" + "data==" + temp);
                }
            }

        }

        /**
         * @desc 授权失败的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            mToast.cancel();
            webview.loadUrl("javascript:failFn('" + t.getMessage() + "')");
            Log.e("Conker", t.getMessage());
        }

        /**
         * @desc 授权取消的回调
         * @param platform 平台名称
         * @param action 行为序号，开发者用不上
         */
        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            String resultCanel = "用户取消";
            webview.loadUrl("javascript:failFn('" + resultCanel + "')");
            Toast.makeText(mContext, "取消授权", Toast.LENGTH_LONG).show();
        }
    };
    private ProgressDialog dialog;

    /**
     * 前往微信登录
     */
    @JavascriptInterface
    public void wxLoginApp() {
        UMShareAPI.get(mContext).getPlatformInfo(mContext, SHARE_MEDIA.WEIXIN, authListener);
    }


    @JavascriptInterface
    public void cancelBack() {
        mContext.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mContext.finish();
            }
        });
    }

    @JavascriptInterface
    public void confirmBack(String json) {
        Log.e("Conker", "confirmBack: " + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            SharedPreferencesUtil.setJsCallBack(jsonObject.getString("callBack"));
            mContext.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mContext.finish();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void goBackCustom(String json) {

        Log.e("Conker", "goBackCustom: " + json);
        try {
            JSONObject jsonObject = new JSONObject(json);
            SharedPreferencesUtil.setJsCallBack(jsonObject.getString("callBack"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void StartToScanPage(String json) {

        try {
            JSONObject jsonObject = new JSONObject(json);
            Intent intent = new Intent(mContext, QRActivity.class);
            intent.putExtra(Constant.NEXT_PAGE_URL, jsonObject.getString("url"));
            intent.putExtra(Constant.NEXT_PAGE_TITLE_NAME, jsonObject.getString("title"));
            mContext.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @JavascriptInterface
    public String GetJSMethod(String key) {
        return SharedPreferencesUtil.GetJSMethod(key);
    }

    @JavascriptInterface
    public void SetJSMethod(String key, String json) {
        SharedPreferencesUtil.SetJSMethod(key, json);

    }

    @JavascriptInterface
    public void ClearJSMethod(String key) {
        SharedPreferencesUtil.ClearJSMethod(key);
    }


    @JavascriptInterface
    public void setGlobalVariable(String json) {


        String p1 = "";

        try {

            JSONObject object = new JSONObject(json);

            p1 = object.optString("value");

            MainApplication.AppLifeCycle = p1;


            Log.e("setGlobalVariable", p1);

        } catch (JSONException e) {

            e.printStackTrace();

        }

    }


    @JavascriptInterface
    public void getGlobalVariable(String json) {


        String p2 = "";

        try {

            JSONObject object = new JSONObject(json);

            p2 = object.optString("callBack");

            if (webview != null) {
                final String finalP = p2;
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        webview.loadUrl("javascript:" + finalP + "('" + MainApplication.AppLifeCycle + "')");
                    }
                });

            }

        } catch (JSONException e) {

            e.printStackTrace();

        }

    }


    //专门针对评论的返回
    @JavascriptInterface
    public void EvaluateGoBack(String json) {

        try {
            JSONObject object = new JSONObject(json);
            String url = object.getString("url");
            BaseEvent event = new BaseEvent();
            event.setFlag(Constant.BASE_RELOAD);
            event.setUrl(url);
            EventBus.getDefault().post(event);

            mContext.finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}

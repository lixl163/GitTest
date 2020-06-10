package com.zsinfo.guoranhao.activitys;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.imagepicker.ImagePicker;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.beans.LoginBean;
import com.zsinfo.guoranhao.chat.utils.ChatUtils;
import com.zsinfo.guoranhao.chat.utils.MD5Util;
import com.zsinfo.guoranhao.chat.utils.SPUtils;
import com.zsinfo.guoranhao.chat.xmpp.XmppConnection;
import com.zsinfo.guoranhao.event.EventInterface;
import com.zsinfo.guoranhao.event.EventLogin;
import com.zsinfo.guoranhao.event.EventUpdateMainUI;
import com.zsinfo.guoranhao.fragment.BaseFragment;
import com.zsinfo.guoranhao.fragment.HomeFragment;
import com.zsinfo.guoranhao.fragment.MeFragment;
import com.zsinfo.guoranhao.fragment.MoreFragment;
import com.zsinfo.guoranhao.fragment.WholeBuyFragment;
import com.zsinfo.guoranhao.utils.AppUtils;
import com.zsinfo.guoranhao.utils.Constant;
import com.zsinfo.guoranhao.utils.MyWebChromeClient;
import com.zsinfo.guoranhao.utils.NetMessageUtil;
import com.zsinfo.guoranhao.utils.NetUtil;
import com.zsinfo.guoranhao.utils.ToastUtil;
import com.zsinfo.guoranhao.utils.UrlUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jivesoftware.smack.XMPPException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    public int NET_ALERT_YET = -1; //网络连接后提醒一次

    //首页
    private RelativeLayout ll_home;
    private ImageView iv_home;
    private TextView tv_home;

    //更多商品
    private RelativeLayout ll_more;
    private ImageView iv_more;
    private TextView tv_more;

//    //购物车
//    private RelativeLayout ll_buycar;
//    private ImageView iv_buycar;
//    private TextView tv_buycar;
//    private TextView tv_num;

    //我的
    private RelativeLayout ll_me;
    private ImageView iv_me;
    private TextView tv_me;

    //新增“整件购买”
    private RelativeLayout ll_whole_buy;
    private ImageView iv_whole_buy;
    private TextView tv_whole_buy;

    BaseFragment homefragment = null;
    BaseFragment moreFragment = null;
//    BaseFragment shopCarFragment = null;
    BaseFragment wholeBuyFragment = null;
    BaseFragment meFragment = null;

    private int currentShowTabIndex = 0;

    private String theme_type;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterReceiver(netReceiver);
        netReceiver = null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("lixl", "MainActivity:onResume" + "重复跳登录");
        if (homefragment != null){
            Log.e("lixl", "MainActivity:onResume：homefragment-" + homefragment.isVisible());
            if (homefragment.isVisible()){
                homefragment.onHidden();
            }
        }
        if (moreFragment != null){
            Log.e("lixl", "MainActivity:onResume：moreFragment-" + moreFragment.isVisible());
            if (moreFragment.isVisible()){
                moreFragment.onHidden();
            }
        }
        if (wholeBuyFragment != null){
            Log.e("lixl", "MainActivity:onResume：wholeBuyFragment-" + wholeBuyFragment.isVisible());
            if (wholeBuyFragment.isVisible()){
                wholeBuyFragment.onHidden();
            }
        }
        if (meFragment != null){
            Log.e("lixl", "MainActivity:onResume：meFragment-" + meFragment.isVisible());
            if (meFragment.isVisible()){
                meFragment.onHidden();
            }
        }
    }

    private BroadcastReceiver netReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                int netWorkState = NetUtil.getNetWorkState(context);
                // 接口回调传过去状态的类型
                if (netWorkState == NetUtil.NETWORK_NONE) {//没有网络
                    ToastUtil.showToast(MainActivity.this, "当前无网络连接，请检查网络");
                    NET_ALERT_YET = -1;
                } else {//有网络的时候
                    if (NET_ALERT_YET == -1) {
                        ToastUtil.showToast(MainActivity.this, "网络已连接");
                        NET_ALERT_YET = 1;
                    }
                }
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        initView();
        initOthers();
        MainApplication.pushStack(this);
        EventBus.getDefault().register(this);

        //请求客服列表
        //getXmppKF();
        //这里处理xmpp登录以及用户个人信息
        //if (SharedPreferencesUtil.getIsLogin()){
        //    toXmpp();
        //}
    }

    /**
     * POST异步请求，查询客服列表
     */
    private void getXmppKF(){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(UrlUtils.SERVE + "?method=iminfo_list")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                //失败
                SPUtils.setParam(SPUtils.chatToKF, "");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //成功
                final String result = response.body().string();
                Log.e("js 给的数据", result);
                SPUtils.setParam(SPUtils.chatToKF, result);
            }
        });
    }

    /**
     * 登录
     * 更改用户名片
     */
    private void toXmpp(){
        //判断一下，是否有修改过数据，然后更换名片
        String updateResult = (String)SPUtils.getParam(SPUtils.updateUserInfo, "");
        if (!TextUtils.isEmpty(updateResult)) {
            //表示当前的个人名片已经更改了
            JSONObject object = null;
            try {
                object = new JSONObject(updateResult);
                String statusCode = object.optString("statusCode");
                if ("100000".equals(statusCode)) {
                    //解析数据
                    Gson gson = new Gson();
                    LoginBean loginBean = gson.fromJson(updateResult, LoginBean.class);
                    //这里可以保存id,name,logo,密码在本地
                    final String jid = loginBean.getData().getCuserInfo().getId();
                    final String aliasName = loginBean.getData().getCuserInfo().getPetName();
                    final String pwd = MD5Util.makeMD5("123456");
                    final String logo = loginBean.getData().getCuserInfo().getFaceImg();
                    SPUtils.setParam(SPUtils.loginNameAlias, aliasName);
                    SPUtils.setParam(SPUtils.userLogo, logo);
                    //登录
                    if (XmppConnection.connection == null){
                        // 关闭连接
                        XmppConnection.closeConnection();
                        //子线程连接
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    // 重连服务器
                                    XmppConnection.connection = XmppConnection.getConnection();
                                    //登录
                                    XmppConnection.login(jid, pwd);
                                    //设置昵称
                                    XmppConnection.updateNickname(aliasName);
                                    //设置头像
                                    if (!TextUtils.isEmpty(logo)) {
                                        Bitmap bm = ChatUtils.getbitmap(logo);
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                        try {
                                            XmppConnection.updateAvater(baos.toByteArray());
                                        } catch (XMPPException e) {
                                            e.printStackTrace();
                                        } catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                    //在这里，将本地保存的个人名片置为空
                                    SPUtils.setParam(SPUtils.updateUserInfo, "");
                                } catch (Exception e) {
                                    XmppConnection.closeConnection();
                                }
                            }
                        }).start();
                    }
                    //设置头像和别名
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(logo)) {
                                Bitmap bm = ChatUtils.getbitmap(logo);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                try {
                                    XmppConnection.updateAvater(baos.toByteArray());
                                    XmppConnection.updateNickname(aliasName);
                                    //在这里，将本地保存的个人名片置为空
                                    SPUtils.setParam(SPUtils.updateUserInfo, "");
                                } catch (XMPPException e) {
                                    e.printStackTrace();
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void checkPermission() {
        //本地存储权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ||ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1000);
        }
        //定位权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);
        }
    }

    private void checkPermissionAgain() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开启读写手机存储权限，否则无法更新本应用！", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
        }
    }

    protected void initView() {
        ll_home = (RelativeLayout) findViewById(R.id.ll_home);
        iv_home = (ImageView) findViewById(R.id.iv_home);
        tv_home = (TextView) findViewById(R.id.tv_home);

        ll_more = (RelativeLayout) findViewById(R.id.ll_more);
        iv_more = (ImageView) findViewById(R.id.iv_more);
        tv_more = (TextView) findViewById(R.id.tv_more);

//        ll_buycar = (RelativeLayout) findViewById(R.id.ll_buycar);
//        iv_buycar = (ImageView) findViewById(R.id.iv_buycar);
//        tv_buycar = (TextView) findViewById(R.id.tv_buycar);
//        tv_num = (TextView) findViewById(R.id.tv_num);
//        tv_num.setText(SharedPreferencesUtil.getShopCarNum() + "");

        ll_whole_buy = (RelativeLayout) findViewById(R.id.ll_whole_buy);
        iv_whole_buy = (ImageView) findViewById(R.id.iv_whole_buy);
        tv_whole_buy = (TextView) findViewById(R.id.tv_whole_buy);

        ll_me = (RelativeLayout) findViewById(R.id.ll_me);
        iv_me = (ImageView) findViewById(R.id.iv_me);
        tv_me = (TextView) findViewById(R.id.tv_me);

        ll_home.setOnClickListener(this);
        ll_more.setOnClickListener(this);
//        ll_buycar.setOnClickListener(this);
        ll_whole_buy.setOnClickListener(this);
        ll_me.setOnClickListener(this);

        setSelect(0);
    }


    protected void initOthers() {
        initBroadCastReceiver();
        checkVersionUpdate();
        getThemeData();
    }

    /**
     * 初始化网络监听器
     */
    private void initBroadCastReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceiver, filter);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
//            if (MainApplication.isClickToRefreshShopCar) {
//                shopCarFragment = null;
//                MainApplication.isClickToRefreshShopCar = false;
//            }

            if (MainApplication.isClickToRefreshWholeBuy) {
                wholeBuyFragment = null;
                MainApplication.isClickToRefreshWholeBuy = false;
            }

            if (MainApplication.isClickToRefreshMoreGoods) {
                moreFragment = null;
                MainApplication.isClickToRefreshMoreGoods = false;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "收到返回消息了");
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS || requestCode == 200 || resultCode == Activity.RESULT_CANCELED) {//头像上传
            if (meFragment instanceof MeFragment) {
                MeFragment me = (MeFragment) meFragment;
                MyWebChromeClient myWebChromeClient = me.getMyWebChromeClient();
                myWebChromeClient.onActivityResult(requestCode, resultCode, data);
            }
        }
        // qq或者新浪精简版jar，需要在您使用分享或授权的Activity（fragment不行）中添加如下回调
//        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Constant.TAKE_PHOTO_PERMISSION == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission Denied
                Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private long exit = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (System.currentTimeMillis() - exit < 2000) {
                /**
                 * 2018/04/11 将Mainactivity这个栈移出
                 * Caused by: java.lang.ClassCastException:
                 * com.zsinfo.guoranhao.activitys.MainActivity cannot be cast to com.zsinfo.guoranhao.activitys.BaseActivity
                 */
                MainApplication.popStackRemoveAll();
                return super.onKeyDown(keyCode, event);
            } else {
                exit = System.currentTimeMillis();
                Toast.makeText(MainActivity.this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_home:
                setSelect(0);
                currentShowTabIndex = 0;
                break;
            case R.id.ll_more:
                if (MainApplication.isClickToRefreshMoreGoods) {
                    moreFragment = null;
                    MainApplication.isClickToRefreshMoreGoods = false;
                }
                setSelect(1);
                currentShowTabIndex = 1;
                break;
//            case R.id.ll_buycar:
//                if (MainApplication.isClickToRefreshShopCar) {
//                    shopCarFragment = null;
//                    MainApplication.isClickToRefreshShopCar = false;
//                }
//                setSelect(2);
//                currentShowTabIndex = 2;
//                break;
            case R.id.ll_whole_buy:
                if (MainApplication.isClickToRefreshWholeBuy) {
                    wholeBuyFragment = null;
                    MainApplication.isClickToRefreshWholeBuy = false;
                }
                setSelect(2);
                currentShowTabIndex = 2;
                break;
            case R.id.ll_me:
                setSelect(3);
                currentShowTabIndex = 3;
                break;
        }
    }

    private void setSelect(int i) {

        hideFragments();

        resetImgs();

        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        switch (i) {
            case 0:
                if (homefragment == null) {
                    homefragment = new HomeFragment();
                    trans.add(R.id.content, homefragment);
                }
                trans.show(homefragment).commitAllowingStateLoss();

                if (!TextUtils.isEmpty(theme_type)) {
                    switch (theme_type) {
                        case "1":
                            iv_home.setImageResource(R.drawable.homepage_check);
                            tv_home.setTextColor(getResources().getColor(R.color.green_back));
                            break;

                        case "2":
                            iv_home.setImageResource(R.drawable.homepage_check1);
                            tv_home.setTextColor(getResources().getColor(R.color.green_back1));
                            break;

                        case "3":
                            iv_home.setImageResource(R.drawable.homepage_check2);
                            tv_home.setTextColor(getResources().getColor(R.color.green_back2));
                            break;

                        case "4":
                            iv_home.setImageResource(R.drawable.homepage_check3);
                            tv_home.setTextColor(getResources().getColor(R.color.green_back3));
                            break;
                    }

                } else {
                    iv_home.setImageResource(R.drawable.homepage_check);
                    tv_home.setTextColor(getResources().getColor(R.color.green_back));
                }
                break;
            case 1:
                if (moreFragment == null) {
                    moreFragment = new MoreFragment();
                    trans.add(R.id.content, moreFragment);
                }
                trans.show(moreFragment).commitAllowingStateLoss();

                if (!TextUtils.isEmpty(theme_type)) {
                    switch (theme_type) {
                        case "1":
                            iv_more.setImageResource(R.drawable.more_good_checked);
                            tv_more.setTextColor(getResources().getColor(R.color.green_back));
                            break;

                        case "2":
                            iv_more.setImageResource(R.drawable.more_good_checked1);
                            tv_more.setTextColor(getResources().getColor(R.color.green_back1));
                            break;

                        case "3":
                            iv_more.setImageResource(R.drawable.more_good_checked2);
                            tv_more.setTextColor(getResources().getColor(R.color.green_back2));
                            break;

                        case "4":
                            iv_more.setImageResource(R.drawable.more_good_checked3);
                            tv_more.setTextColor(getResources().getColor(R.color.green_back3));
                            break;
                    }

                } else {
                    iv_more.setImageResource(R.drawable.more_good_checked);
                    tv_more.setTextColor(getResources().getColor(R.color.green_back));
                }
                break;
            case 3:
                if (meFragment == null) {
                    meFragment = new MeFragment();
                    trans.add(R.id.content, meFragment);
                }
                trans.show(meFragment).commitAllowingStateLoss();

                if (!TextUtils.isEmpty(theme_type)) {
                    switch (theme_type) {
                        case "1":
                            iv_me.setImageResource(R.drawable.me_checked);
                            tv_me.setTextColor(getResources().getColor(R.color.green_back));
                            break;

                        case "2":
                            iv_me.setImageResource(R.drawable.me_checked1);
                            tv_me.setTextColor(getResources().getColor(R.color.green_back1));
                            break;

                        case "3":
                            iv_me.setImageResource(R.drawable.me_checked2);
                            tv_me.setTextColor(getResources().getColor(R.color.green_back2));
                            break;

                        case "4":
                            iv_me.setImageResource(R.drawable.me_checked3);
                            tv_me.setTextColor(getResources().getColor(R.color.green_back3));
                            break;
                    }

                } else {
                    iv_me.setImageResource(R.drawable.me_checked);
                    tv_me.setTextColor(getResources().getColor(R.color.green_back));
                }
                break;

            //整件购买
            case 2:
                if (wholeBuyFragment == null) {
                    wholeBuyFragment = new WholeBuyFragment();
                    trans.add(R.id.content, wholeBuyFragment);
                }
                trans.show(wholeBuyFragment).commitAllowingStateLoss();

                if (!TextUtils.isEmpty(theme_type)) {
                    switch (theme_type) {
                        case "1":
                            iv_whole_buy.setImageResource(R.drawable.whole_buy_checked);
                            tv_whole_buy.setTextColor(getResources().getColor(R.color.green_back));
                            break;

                        case "2":
                            iv_whole_buy.setImageResource(R.drawable.whole_buy_checked1);
                            tv_whole_buy.setTextColor(getResources().getColor(R.color.green_back1));
                            break;

                        case "3":
                            iv_whole_buy.setImageResource(R.drawable.whole_buy_checked2);
                            tv_whole_buy.setTextColor(getResources().getColor(R.color.green_back2));
                            break;

                        case "4":
                            iv_whole_buy.setImageResource(R.drawable.whole_buy_checked3);
                            tv_whole_buy.setTextColor(getResources().getColor(R.color.green_back3));
                            break;
                    }

                } else {
                    iv_whole_buy.setImageResource(R.drawable.whole_buy_checked);
                    tv_whole_buy.setTextColor(getResources().getColor(R.color.green_back));
                }
                break;

//            //购物车
//            case 2:
//                if (shopCarFragment == null) {
//                    shopCarFragment = new ShopCarFragment();
//                    ft.add(R.id.content, shopCarFragment);
//                }
//                ft.show(shopCarFragment);
//
//                if (!TextUtils.isEmpty(theme_type)) {
//                    switch (theme_type) {
//                        case "1":
//                            iv_buycar.setImageResource(R.drawable.buy_car_checked);
//                            tv_buycar.setTextColor(getResources().getColor(R.color.green_back));
//                            break;
//
//                        case "2":
//                            iv_buycar.setImageResource(R.drawable.buy_car_checked1);
//                            tv_buycar.setTextColor(getResources().getColor(R.color.green_back1));
//                            break;
//
//                        case "3":
//                            iv_buycar.setImageResource(R.drawable.buy_car_checked2);
//                            tv_buycar.setTextColor(getResources().getColor(R.color.green_back2));
//                            break;
//
//                        case "4":
//                            iv_buycar.setImageResource(R.drawable.buy_car_checked3);
//                            tv_buycar.setTextColor(getResources().getColor(R.color.green_back3));
//                            break;
//                    }
//
//                } else {
//                    iv_buycar.setImageResource(R.drawable.buy_car_checked);
//                    tv_buycar.setTextColor(getResources().getColor(R.color.green_back));
//                }
//                break;
        }
    }

    /**
     * 隐藏Fragment
     */
    private void hideFragments() {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        if (homefragment != null) {
            trans.hide(homefragment);
        }
        if (moreFragment != null) {
            trans.hide(moreFragment);
        }
        if (wholeBuyFragment != null) {
            trans.hide(wholeBuyFragment);
        }
//        if (shopCarFragment != null) {
//            ft.hide(shopCarFragment);
//        }
        if (meFragment != null) {
            trans.hide(meFragment);
        }
        trans.commitAllowingStateLoss();
    }

    /**
     * 重置所有的底部tab
     */
    private void resetImgs() {
        iv_home.setImageResource(R.drawable.homepage_uncheck);
        iv_more.setImageResource(R.drawable.more_good_unchecked);
//        iv_buycar.setImageResource(R.drawable.buy_car_unchecked);
        iv_whole_buy.setImageResource(R.drawable.whole_buy_unchecked);
        iv_me.setImageResource(R.drawable.me_unchecked);

        tv_home.setTextColor(getResources().getColor(R.color.home_back_unselected));
        tv_more.setTextColor(getResources().getColor(R.color.home_back_unselected));
//        tv_buycar.setTextColor(getResources().getColor(R.color.home_back_unselected));
        tv_whole_buy.setTextColor(getResources().getColor(R.color.home_back_unselected));
        tv_me.setTextColor(getResources().getColor(R.color.home_back_unselected));
    }

    public void reload(EventUpdateMainUI ui) {
        int flag = ui.getFlag();
        boolean isReload = ui.getIsNeedReload();
        switch (flag) {
            case Constant.MainUI_TAB_HOME0:
                if (isReload) {
                    //先隐藏当前正显示的Fragment
                    if (homefragment !=null){
                        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                        trans.hide(homefragment).commitAllowingStateLoss();
                    }
                    //将所有的fragment置空，登录后刷新
                    reset();
                    setSelect(0);
                } else {
                    setSelect(0);
                }
                break;
            case Constant.MainUI_TAB_HOME1:
                if (isReload) {
                    if (moreFragment !=null){
                        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                        trans.hide(moreFragment).commitAllowingStateLoss();
                    }
                    reset();
                    setSelect(1);
                } else {
                    setSelect(1);
                }
                break;
//TODO 2019.8.12 发现此处报错：java.lang.IllegalStateException: commit already called
//
//            case Constant.MainUI_TAB_HOME2:
//                //刷新购物车
//                if (isReload) {
//                    shopCarFragment = new ShopCarFragment();
//                    ft = getSupportFragmentManager().beginTransaction();
//                    ft.replace(R.id.content, shopCarFragment);
//                    ft.commitAllowingStateLoss();
//                    ft = null;
//                    ft = getSupportFragmentManager().beginTransaction();
//                    //将所有的fragment置空，登录后刷新
//                    reset();
//                    setSelect(2);
//                } else {
//                    setSelect(2);
//                }
//                break;
            case Constant.MainUI_TAB_HOME6:
                //刷新整件购买
                if (isReload) {
                    if (wholeBuyFragment !=null){
                        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                        trans.hide(wholeBuyFragment).commitAllowingStateLoss();
                    }
                    reset();
                    setSelect(2);
                } else {
                    setSelect(2);
                }
                break;

            case Constant.MainUI_TAB_HOME3:
                if (isReload) {
                    if (meFragment !=null){
                        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                        trans.hide(meFragment).commitAllowingStateLoss();
                    }
                    reset();
                    setSelect(3);
                } else {
                    setSelect(3);
                }
                break;

//            case Constant.MainUI_SHOPCARNUM:
//                //刷新购物车数量信息
//                if (isReload) {
//                    tv_num.setText(ui.getNum());
//                }
//                break;
        }
    }

    private void reset() {
        if (homefragment != null) homefragment.onDestroy();
        if (moreFragment != null) moreFragment.onDestroy();
//        if (shopCarFragment != null) shopCarFragment.onDestroy();
        if (wholeBuyFragment != null) wholeBuyFragment.onDestroy();
        if (meFragment != null) meFragment.onDestroy();
        homefragment = null;
        moreFragment = null;
//        shopCarFragment = null;
        wholeBuyFragment = null;
        meFragment = null;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventInterface minterface) {
        if (minterface instanceof EventUpdateMainUI) {
            EventUpdateMainUI ui = (EventUpdateMainUI) minterface;
            reload(ui);
        } else if (minterface instanceof EventLogin) {
            EventLogin login = (EventLogin) minterface;
            //所有页面置空 重新加载
            homefragment = null;
            moreFragment = null;
//            shopCarFragment = null;
            wholeBuyFragment = null;
            meFragment = null;
        }
    }

    /**
     * 获取当前的tab 加载的url
     */
    public BaseFragment getFragmentByCurrentShowTabIndex() {
        switch (currentShowTabIndex) {
            case 0:
                return homefragment;
            case 1:
                return moreFragment;
//            case 2:
//                return shopCarFragment;
            case 2:
                return wholeBuyFragment;
            case 3:
                return meFragment;
            default:
                return null;
        }
    }

    /**
     * 检查版本更新
     */
    private void checkVersionUpdate() {
        if (!NetUtil.isNetworkAvailable()) {
            ToastUtil.showToast(this, "网络连接不可用");
            return;
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(UrlUtils.SERVE + "?method=version_check")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                //失败
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //成功
                final String result = response.body().string();
                Log.e("lixl版本更新ToAndroid", result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        parseCheckResult(result);
                    }
                });

            }
        });
    }

    /**
     * 检查版本更新
     * @param result
     */
    private void parseCheckResult(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String statusCode = jsonObject.optString("statusCode");
            if (!"100000".equals(statusCode)) {
                return;
            }
            String data = jsonObject.optString("data");
            JSONObject dataObj = new JSONObject(data);
            if (AppUtils.getVerName(MainActivity.this).equals(dataObj.optString("number"))) {

                return;
            } else {

                String isForce = dataObj.optString("isForce");
                String isShow = dataObj.optString("isShow");
                if ("0".equals(isShow)) { //不显示升级
                    return;
                }
                if (!"1".equals(isForce)) { //通知更新
                    noticeUpdate(dataObj);
                } else { //强制更新
                    mustUpdate(dataObj);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.showToast(MainActivity.this, "检查更新失败");
        }
    }

    /**
     * 获取主题类型
     */
    private void getThemeData() {
        if (!NetUtil.isNetworkAvailable()) {
            ToastUtil.showToast(this, "网络连接不可用");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("method", "change_app_theme");
        NetMessageUtil.getPostString(UrlUtils.SERVE, map, new NetMessageUtil.CallBack() {
            @Override
            public void send(String result) {
                Log.i("test", result);

                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String statusCode = jsonObject.optString("statusCode");
                    if ("100000".equals(statusCode)) {

                        String data = jsonObject.optString("data");

                        JSONObject dataJsonObject = new JSONObject(data);
                        theme_type = dataJsonObject.optString("type");
                        com.zsinfo.guoranhao.utils.SPUtils.setValue(Constant.THEME_TYPE, theme_type);

                        if (!TextUtils.isEmpty(theme_type)) {
                            resetImgs();
                            switch (theme_type) {

                                case "1":
                                    iv_home.setImageResource(R.drawable.homepage_check);
                                    tv_home.setTextColor(getResources().getColor(R.color.green_back));
                                    break;

                                case "2":
                                    iv_home.setImageResource(R.drawable.homepage_check1);
                                    tv_home.setTextColor(getResources().getColor(R.color.green_back1));
                                    break;

                                case "3":
                                    iv_home.setImageResource(R.drawable.homepage_check2);
                                    tv_home.setTextColor(getResources().getColor(R.color.green_back2));
                                    break;

                                case "4":
                                    iv_home.setImageResource(R.drawable.homepage_check3);
                                    tv_home.setTextColor(getResources().getColor(R.color.green_back3));
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {

                }
            }
        });
    }

    /**
     * 通知更新
     *
     * @param object
     */
    private void noticeUpdate(final JSONObject object) {
        View view = LayoutInflater.from(this).inflate(R.layout.updata_view, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).show();
        TextView content = (TextView) view.findViewById(R.id.content);
        TextView number = (TextView) view.findViewById(R.id.number);
        Button now = (Button) view.findViewById(R.id.now);
        Button later = (Button) view.findViewById(R.id.later);
        now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkPermissionAgain();
                downloadAPK(object.optString("appUrl").trim(), object.optString("number"));
            }
        });
        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        content.setText(object.optString("upgradeContent"));
        number.setText(object.optString("number"));
    }

    /**
     * 强制更新
     *
     * @param object
     */
    private void mustUpdate(final JSONObject object) {
        View view = LayoutInflater.from(this).inflate(R.layout.updata_view, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).setView(view).show();
        dialog.setCancelable(false);
        TextView content = (TextView) view.findViewById(R.id.content);
        TextView number = (TextView) view.findViewById(R.id.number);
        Button now = (Button) view.findViewById(R.id.now);
        Button later = (Button) view.findViewById(R.id.later);
        later.setText("退出");
        content.setText(object.optString("upgradeContent"));
        number.setText(object.optString("number"));
        now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                downloadAPK(object.optString("appUrl").trim(), object.optString("number"));
            }
        });
        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Process.killProcess(Process.myPid());
                ActivityManager activitymanager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
                activitymanager.restartPackage(getPackageName());
            }
        });
    }

    private void downloadAPK(final String apkUrl, final String version) {
        final ProgressDialog dialog = new ProgressDialog(this);
//        View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.updata_view, null);
////        dialog.setView(view);
//        dialog.setContentView(view);
        dialog.setTitle("新版本下载中");
        dialog.setMessage("正在下载中，请稍等");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(100);
        dialog.show();
        final Handler handle = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        dialog.setProgress(msg.arg1);
                        break;
                    case 1:
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                            ToastUtil.showToast(MainActivity.this, "下载失败，请稍后再试");
                        }
                        break;
                    case 2:
                        ToastUtil.showToast(MainActivity.this, "请检查存储设置");
                        break;
                    case 3:
                        if (dialog.isShowing()) {
                            dialog.dismiss();
                        }
                        Uri uri = Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), (String) msg.obj));
                        installApk(uri, (String) msg.obj);
                        break;
                }
            }
        };
        //开启子线程下载最新版本的apk
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = null;
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/guoranhao_V" + version + ".apk");
                    if (!file.exists()) {
                        try {
                            file.createNewFile();
                        } catch (IOException e) {
                            handle.sendEmptyMessage(2);
                            return;
                        }
                    }
                }
                InputStream in = null;
                FileOutputStream fos = null;
                try {
                    URL url = new URL(apkUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.connect();
                    long maxLenght = connection.getContentLength();
                    fos = new FileOutputStream(file);
                    in = connection.getInputStream();
                    int len = 0;
                    int size = 0;
                    byte[] buf = new byte[1024];
                    while ((size = in.read(buf)) != -1) {
                        len += size;
                        fos.write(buf, 0, size);
                        fos.flush();
                        Message msg = handle.obtainMessage();
                        msg.what = 0;
                        msg.arg1 = (int) (len * 100 / maxLenght);
                        handle.sendMessageDelayed(msg, 1000);
                    }
                    fos.close();
                    in.close();
                    Message message = handle.obtainMessage();
                    message.what = 3;
                    message.obj = file.getName();
                    handle.sendMessageDelayed(message, 1000);
                } catch (Exception e) {
                    handle.sendEmptyMessage(1);
                }
            }
        }).start();
    }

    /**
     * 打开安装apk
     */
    private void installApk(Uri apkUri, String obj) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            install.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), obj);
            Uri contentUri = FileProvider.getUriForFile(this, "com.zsinfo.guoranhao.FileProvider", file);
            install.setDataAndType(contentUri, "application/vnd.android.package-archive");
            startActivity(install);
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(install);
        }

        android.os.Process.killProcess(Process.myPid());
        ActivityManager activitymanager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        activitymanager.restartPackage(getPackageName());
//        System.exit(0);
    }
}

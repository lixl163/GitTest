package com.zsinfo.guoranhao.activitys;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zsinfo.guoranhao.BuildConfig;
import com.zsinfo.guoranhao.utils.KqwException;
import com.zsinfo.guoranhao.utils.SharedPreferencesUtil;
import com.zsinfo.guoranhao.utils.UrlUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cyk
 * @date 2017/2/27 09:16
 * @email choe0227@163.com
 * @desc
 * @modifier
 * @modify_time
 * @modify_remark
 */
public class MainApplication extends Application {
    // 小米推送的配置 start
    public static final String APP_ID = "2882303761517552016";
    public static final String APP_KEY = "5501755240016";
    public static final String TAG = "com.zsinfo.guoranhao";
    public static String AppLifeCycle = "";
    // 小米推送的配置 end
    public static Context context;

    public static MainApplication baseApplication;

    /**
     * 用来判断调用微信支付成功之后跳转到哪个界面上去
     */
    public static String flag = null;
    /**
     * 用来判断是否首次启动app 是否需要缓存首页的html文件
     */
    public static boolean isNeedCacheHomePage = false;
    public static boolean isNeedCacheAboutMe = false;
    /**
     * 用来标志在首页点击购物车的时候 是否需要重新加载购物车数据
     */
    public static boolean isClickToRefreshShopCar = false;
    /**
     * 用来标志在首页点击更多商品的时候 是否需要重新加载更多商品数据
     */
    public static boolean isClickToRefreshMoreGoods = false;
    /**
     * 用来标志在首页点击整件购买的时候 是否需要重新加载整件购买数据
     */
    public static boolean isClickToRefreshWholeBuy = false;
    /**
     * 在没有定位权限的时候，用来标志是否显示过申请定位权限的对话框
     */
    public static boolean isShowedLocationDialog = false;
    /**
     * 存放 按返回按钮需要刷新的界面的url连接 key表示返回前的页面url  value表示返回的目的页面（刷新页面）
     */
    public static Map<String, String> map = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        isNeedCacheHomePage = true;
        isNeedCacheAboutMe = true;
        Config.DEBUG = false;
        context = this;
        baseApplication = this;
        //初始化push推送服务
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }

        //打开Log
        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
                // ignore
            }

            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }

            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);

        if (SharedPreferencesUtil.getIsLogin()) {
            String userAccount = SharedPreferencesUtil.getUserAccount();
            MiPushClient.setAlias(this, userAccount, null);
        }

        // 配置友盟分享，以及第三方登录
        UMShareAPI.get(this);

        //TODO 设置各个平台的appid
        PlatformConfig.setWeixin("wx02afc2a1605d4d78", "e834820447a9d6591d81e4e8b87059a3");
        PlatformConfig.setQQZone("100424468", "c7394704798a158208a74ab60104f0ba");

        initBackShouldRefreshMap();

        initBugly();

        initException();
    }

    /**
     * 初始化 Bugly
     *
     * @return
     */
    private void initBugly() {
        CrashReport.initCrashReport(context, "58a32bdddb", BuildConfig.DEBUG);
    }

    /**
     * 添加异常
     */
    public void initException(){
        KqwException handler = KqwException.getInstance(this);
        Thread.setDefaultUncaughtExceptionHandler(handler);
    }

    /**
     * 初始化那些界面需要返回刷新
     */
    private void initBackShouldRefreshMap() {
        map.put(UrlUtils.ADDRESS_URL, UrlUtils.COMMIT_ORDER_URL);
    }


    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    private static List<Activity> stacks = new ArrayList<>();

    public static void pushStack(Activity activity) {
        if (activity instanceof MainActivity && stacks.size() == 0) { //MainActivity 始终保持在栈低
            stacks.add(activity);
        } else {
            AppCompatActivity baseActivity = (AppCompatActivity) activity;
            if (stacks.contains(baseActivity)) {
                //如果之前有这个页面就进行覆盖
                int i = stacks.indexOf(baseActivity);
                stacks.get(i).finish();
            }
            stacks.add(baseActivity);
        }

    }

    /**
     * 根据Url弹栈
     *
     * @param url
     * @return
     */
    public static BaseActivity popStack(String url) {

        int index = isExistPageByUrl(url);

        if (index == -1) {
            Log.e("没有这个页面", "你返回个啥");
            return null;
        }
        // 清除栈顶所有的页面
        int size = stacks.size();
        for (int i = size - 1; i > index; i--) {
            //获取栈顶的activity
            stacks.get(i).finish();
        }

        return (BaseActivity) stacks.get(stacks.size() - 1);
    }

    /**
     * 移除栈顶
     */
    public static void popStack(BaseActivity baseActivity) {
        if (stacks.contains(baseActivity)) {
            //如果之前有这个页面就进行覆盖
            int i = stacks.indexOf(baseActivity);
            stacks.remove(i);
        }
    }

    /**
     * 移除栈顶
     */
    public static void popStack(Activity baseActivity) {
        if (stacks.contains(baseActivity)) {
            //如果之前有这个页面就进行覆盖
            int i = stacks.indexOf(baseActivity);
            stacks.remove(i);
        }
    }

    /**
     * 2018/04/11 退出应用之后，将Mainactivity这个栈移出
     */
    public static void popStackRemoveAll() {
        stacks.clear();
    }

    /**
     * 查询回退栈中是否存在这个url的activity
     *
     * @param url
     * @return 若为-1 表示不存在
     */
    public static int isExistPageByUrl(String url) {
        int index = -1;
        // 检测之前是否有这个url
        for (int i = 1; i < stacks.size(); i++) {
            BaseActivity activity = (BaseActivity) stacks.get(i);
            if (activity.getUrl().contains(url)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 回到首页 只保留栈底的最后一个
     *
     * @return 返回MainActity 对象引用
     */
    public static MainActivity backHome() {
        int size = stacks.size();
        for (int i = size - 1; i > 0; i--) {
            //获取栈顶的activity
            stacks.get(i).finish();
        }
        return (MainActivity) stacks.get(0);
    }

    /**
     * 根据倒数序列号获取activity
     *
     * @param index 最后一个为0 依次类推
     * @return
     */
    public static Activity getActivityByLastIndex(int index) {

        return stacks.get(stacks.size() - 1 - index);
    }
}

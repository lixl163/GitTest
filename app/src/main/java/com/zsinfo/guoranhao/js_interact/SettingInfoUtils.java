package com.zsinfo.guoranhao.js_interact;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.zsinfo.guoranhao.activitys.BaseActivity;
import com.zsinfo.guoranhao.activitys.EvaluateActivity;
import com.zsinfo.guoranhao.activitys.MainActivity;
import com.zsinfo.guoranhao.activitys.MainApplication;
import com.zsinfo.guoranhao.activitys.WebviewActivity;
import com.zsinfo.guoranhao.event.BaseEvent;
import com.zsinfo.guoranhao.event.EventDialog;
import com.zsinfo.guoranhao.event.EventUpdateMainUI;
import com.zsinfo.guoranhao.utils.Constant;
import com.zsinfo.guoranhao.utils.UrlUtils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import static com.zsinfo.guoranhao.activitys.MainApplication.popStack;
import static com.zsinfo.guoranhao.utils.Constant.MainUI_TAB_HOME0;
import static com.zsinfo.guoranhao.utils.Constant.MainUI_TAB_HOME1;
import static com.zsinfo.guoranhao.utils.Constant.MainUI_TAB_HOME3;
import static com.zsinfo.guoranhao.utils.Constant.MainUI_TAB_HOME6;

/**
 * Created by admin on 2017/8/31.
 */

public class SettingInfoUtils {
    /**
     * {'hierarchy':1,'reload':1,'url':'url'}
     *
     * @param json     1表示刷新
     * @param mContext
     */
    public static void goBack(String json, Activity mContext) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            final String url = jsonObject.optString("url");
            int reload = jsonObject.optInt("reload");
            final boolean isReload = (reload == 1 ? true : false);
            goBack(url, isReload, mContext);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("返回层级时settinginfo:", "解析数据出错");
        }
    }

    /**
     * 根据层级来回退
     *
     * @param json     {'hierarchy':1,'reload':1,'url':'url'}
     * @param activity
     */
    public static void goBackByHierarchy(String json, Activity activity) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int hierarchy = jsonObject.optInt("hierarchy");
            int reload = jsonObject.optInt("reload");

            final boolean isRefresh = (reload == 1 ? true : false);
            Activity targetActivity = MainApplication.getActivityByLastIndex(hierarchy);
            final String url = getActivityUrl(targetActivity);
            goBack(url, isRefresh, activity);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("返回层级时settinginfo:", "解析数据出错");
        }
    }

    /**
     * 回退的核心实现
     *
     * @param url
     * @param isRefresh
     * @param activity
     */
    private static void goBack(final String url, final boolean isRefresh, Activity activity) {
        Log.e("需要回退到的页面url=", "" + url);
        // 判断是否回退到首页
        final int tabIndex = getHomePageTabIndexByUrl(url);
        Log.e("需要回退到的页面url=", "" + tabIndex);
        if (tabIndex == -1) { //非首页

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("回到非首页", "====");
                    BaseActivity activity = popStack(url);
                    if (activity == null) {
                        Log.e("SettingIngo:goBack", "不可以回退");
                        return;
                    }
                    if (isRefresh) {
                        BaseEvent event = new BaseEvent();
                        event.setFlag(Constant.BASE_RELOAD);
                        event.setUrl(url);
                        EventBus.getDefault().post(event);
                    } else {
                        popStack(url);
                    }
                }
            });
        } else { //首页

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.e("回到首页", "====");
                    EventBus.getDefault().post(new EventUpdateMainUI(tabIndex, isRefresh));
                    MainApplication.backHome();
                }
            });

        }
    }

    private static String getActivityUrl(Activity activity) {
        String url = "";
        if (activity instanceof BaseActivity) {
            BaseActivity baseActivity = (BaseActivity) activity;
            url = baseActivity.getUrl();
        } else if (activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) activity;
            url = mainActivity.getFragmentByCurrentShowTabIndex().getUrl();
        }

        return url;
    }

    /**
     * 根据url 来判断是首页的哪个tab
     *
     * @return 返回tab的序列 0代表首页 1更多商品 2购物车 3我的页面 ,-1 不是首页的页面
     */
    private static int getHomePageTabIndexByUrl(String url) {
        if (UrlUtils.HOME_PAGE.contains(url)) {
            return MainUI_TAB_HOME0;
        } else if (UrlUtils.MORE_GOOD.contains(url)) {
            return MainUI_TAB_HOME1;
        } else if (UrlUtils.WHOLE_BUY.contains(url)) {
            return MainUI_TAB_HOME6;
        } else if (UrlUtils.ABOUT_ME.contains(url)) {
            return MainUI_TAB_HOME3;
        }
//        else if (UrlUtils.SHOP_CAR.contains(url)) {
//            return Constant.MainUI_TAB_HOME2;
//        }
        return -1;
    }

    /**
     * 跳转到二级网页界面，或者二级评论界面
     * @param json
     * @param mContext
     */
    public static void goToNextLevel(String json, Activity mContext) {

        try {
            JSONObject object = new JSONObject(json);
            String titleName = object.optString("title");
            String url = object.optString("url");

            if (url.contains("order_evaluation")) {
                Intent intent = new Intent(mContext, EvaluateActivity.class);
                intent.putExtra(Constant.NEXT_PAGE_TITLE_NAME, titleName);
                intent.putExtra(Constant.NEXT_PAGE_URL, url);
                mContext.startActivity(intent);
            } else {
                Intent intent = new Intent(mContext, WebviewActivity.class);
                intent.putExtra(Constant.NEXT_PAGE_TITLE_NAME, titleName);
                intent.putExtra(Constant.NEXT_PAGE_URL, url);
                mContext.startActivityForResult(intent, Constant.REQUESTCODE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("跳转下个页面时settinginfo:", "解析数据出错");
        }
    }

    /**
     * 跳转到一级Fragment界面
     * @param json
     * @param mContext
     */
    public static void goToFirstLevel(String json, Activity mContext) {
        //{"navIndex":2}
        try {
            JSONObject object = new JSONObject(json);
            int navIndex = object.optInt("navIndex");

            if (navIndex == 0) {
                //首页Fragment
                EventBus.getDefault().post(new EventUpdateMainUI(MainUI_TAB_HOME0, false));
            } else  if (navIndex == 1) {
                //首更多商品Fragment
                EventBus.getDefault().post(new EventUpdateMainUI(MainUI_TAB_HOME1, false));
            } else  if (navIndex == 2) {
                //整件购买Fragment
                EventBus.getDefault().post(new EventUpdateMainUI(MainUI_TAB_HOME6, false));
            } else  if (navIndex == 3) {
                //我Fragment
                EventBus.getDefault().post(new EventUpdateMainUI(MainUI_TAB_HOME3, false));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("跳转Fragment时settinginfo:", "解析数据出错");
        }
    }

    public static void jumpLinkCustomApp(String json, Activity mContext) {

        try {
            JSONObject object = new JSONObject(json);
            String titleName = object.optString("title");
            String url = object.optString("url");
            String img = object.optString("imgIcon");
            String text = object.optString("txt");
            String callBack = object.optString("callBack");
            Intent intent = new Intent(mContext, WebviewActivity.class);
            intent.putExtra(Constant.NEXT_PAGE_TITLE_NAME, titleName);  //标题
            intent.putExtra(Constant.NEXT_PAGE_URL, url);  //url链接
            intent.putExtra(Constant.NEXT_PAGE_TITLE_TEXT, text);  //标题右侧文本
            intent.putExtra(Constant.NEXT_PAGE_TITLE_IMG, img);  //标题右侧icon
            intent.putExtra(Constant.NEXT_PAGE_TITLE_CALLBACK, callBack);
            mContext.startActivityForResult(intent, Constant.REQUESTCODE);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("跳转下个页面时settinginfo:", "解析数据出错");
        }
    }

    /**
     * 发送控制dialog的显示与隐藏的事件
     *
     * @param flag     0表示显示 1表示隐藏
     * @param mContext 要显示货隐藏页面的上下文
     */
    public static void operateDialog(int flag, Activity mContext) {
        String url = "";
        if (mContext instanceof WebviewActivity) {
            WebviewActivity webviewActivity = (WebviewActivity) mContext;
            url = webviewActivity.getUrl();
        }
        if (mContext instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) mContext;
            url = mainActivity.getFragmentByCurrentShowTabIndex().getUrl();
        }
        EventDialog dialog = new EventDialog();
        dialog.setUrl(url);
        dialog.setFlag(flag);
        EventBus.getDefault().post(dialog);
    }
}

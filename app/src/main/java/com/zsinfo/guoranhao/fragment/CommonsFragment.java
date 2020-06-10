package com.zsinfo.guoranhao.fragment;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.event.EventDialog;
import com.zsinfo.guoranhao.event.EventInterface;
import com.zsinfo.guoranhao.event.EventRefesh;
import com.zsinfo.guoranhao.utils.AppUtils;
import com.zsinfo.guoranhao.utils.MyWebChromeClient;
import com.zsinfo.guoranhao.utils.MyWebViewClient;
import com.zsinfo.guoranhao.utils.NetMessageUtil;
import com.zsinfo.guoranhao.utils.NetUtil;
import com.zsinfo.guoranhao.utils.SharedPreferencesUtil;
import com.zsinfo.guoranhao.utils.ToastUtil;
import com.zsinfo.guoranhao.utils.UrlUtils;
import com.zsinfo.guoranhao.utils.WebViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * 通用的Fragment 适用于复用的activity
 */

public class CommonsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected WebView mWebView;
    protected MyWebChromeClient mMyWebChromeClient;
    protected MyWebViewClient mMyWebViewClient;
    private String url;
//    private MySwipeRereshLayout mRefreshLayout;

    //根据网络 判断是否显示webview
    private LinearLayout ll_refresh;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 需要把WebView这个控件 提供给WebviewActivity界面使用
     *
     * @return
     */
    public WebView getmWebView() {
        return mWebView;
    }

    public void setmWebView(WebView mWebView) {
        this.mWebView = mWebView;
    }

    public CommonsFragment() {
    }

    public static CommonsFragment newInstance(String url) {
        CommonsFragment fragment = new CommonsFragment();
        fragment.setUrl(url);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_common, null);
        mWebView = (WebView) view.findViewById(R.id.webview);
        ll_refresh = (LinearLayout) view.findViewById(R.id.ll_refresh);
//        mRefreshLayout= (MySwipeRereshLayout) view.findViewById(R.id.swipeRefresh);
        mMyWebChromeClient = new MyWebChromeClient(getActivity());
        mMyWebViewClient = new MyWebViewClient(getActivity(), mWebView);
        WebViewUtils.initWebView(mMyWebChromeClient, mMyWebViewClient, mWebView, getActivity());
        initWebViewListener();
        WebViewUtils.initSettings(mWebView, getActivity());
        mWebView.loadUrl(UrlUtils.URL + url);
        Log.e("加载的url********", UrlUtils.URL + url);
//        mRefreshLayout.setOnRefreshListener(this);
        checkNetWork();
        initOthers();
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {

                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                result.confirm();
                return true;
            }
        });

        EventBus.getDefault().register(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            String url = mWebView.getUrl();
            String subUrl = url.substring(UrlUtils.URL.length(), url.length());
            String json = SharedPreferencesUtil.GetJSMethod(subUrl);
            if (!TextUtils.isEmpty(json)) {
                mWebView.loadUrl("javascript:" + json);
            }
            Log.e("2019.8.13--onHidden", "走了：" + subUrl + "---" + json);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 检查网络状态，显示不同布局
     */
    private void checkNetWork() {
        if (!NetUtil.isNetworkAvailable()) {
            mWebView.setVisibility(View.GONE);
            ll_refresh.setVisibility(View.VISIBLE);
            ll_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_refresh.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                    mWebView.reload();
                }
            });
        }
    }

    private void initOthers() {
        try {
            //判断页面是否需要刷新
            for (int i = 0; i < UrlUtils.REFRESH_URLS.length; i++) {
                if (url.endsWith(UrlUtils.REFRESH_URLS[i])) {
                    //mRefreshLayout.setEnabled(true);
                    return;
                }
            }
            //mRefreshLayout.setEnabled(false);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public MyWebChromeClient getMyWebChromeClient() {
        return mMyWebChromeClient;
    }

    @Override
    public void onRefresh() {
        reload();
    }

    /**
     * 初始化一些监听
     */
    protected void initWebViewListener() {
        //取消长按出现复制
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }

    private void reload() {
        mWebView.reload();
//        mRefreshLayout.setRefreshing(false);
    }

    /**
     * 在commonFragment中调用js的方法
     *
     * @param funcName
     */
    public void runJsMethod(String funcName) {
        mWebView.loadUrl(funcName);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventInterface minterface) {
        if (minterface instanceof EventDialog) {
            EventDialog eventDialog = (EventDialog) minterface;
            if (!url.endsWith(eventDialog.getUrl())) {
                //如果不是当前页面 就不再执行以下的操作
                return;
            }
            switch (eventDialog.getFlag()) {
                case 0://显示dialog
                    //                    showDialog();
                    break;
                case 1://隐藏dialog
                    //                    cancleDialog();
                    break;
                case 2:
                    versionUpdate();
                    break;
            }
        } else if (minterface instanceof EventRefesh) {
            //显示布局
            mWebView.setVisibility(View.GONE);
            ll_refresh.setVisibility(View.VISIBLE);
            ll_refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ll_refresh.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                    mWebView.reload();
                }
            });
        }
    }

    /**
     * 接口请求apk更新
     */
    public void versionUpdate(){
        if (!NetUtil.isNetworkAvailable()) {
            ToastUtil.showToast(getActivity(), "网络连接不可用");
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("method", "version_check");
        NetMessageUtil.getPostString(UrlUtils.SERVE, map, new NetMessageUtil.CallBack() {
            @Override
            public void send(String result) {
                parseCheckResult(result);
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
            if (AppUtils.getVerName(getActivity()).equals(dataObj.optString("number"))) {
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
            ToastUtil.showToast(getActivity(), "检查更新失败");
        }
    }
    /**
     * 通知更新
     *
     * @param object
     */
    private void noticeUpdate(final JSONObject object) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.updata_view, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).show();
        TextView content = (TextView) view.findViewById(R.id.content);
        TextView number = (TextView) view.findViewById(R.id.number);
        Button now = (Button) view.findViewById(R.id.now);
        Button later = (Button) view.findViewById(R.id.later);
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
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.updata_view, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).show();
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
                android.os.Process.killProcess( android.os.Process.myPid());
                ActivityManager activitymanager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
                activitymanager.restartPackage(getActivity().getPackageName());
            }
        });

    }

    private void downloadAPK(final String apkUrl, final String version) {
        final ProgressDialog dialog = new ProgressDialog(getActivity());
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
                            ToastUtil.showToast(getActivity(), "下载失败，请稍后再试");
                        }
                        break;
                    case 2:
                        ToastUtil.showToast(getActivity(), "请检查存储设置");
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
            Uri contentUri = FileProvider.getUriForFile(getActivity(), "com.zsinfo.guoranhao.FileProvider", file);
            install.setDataAndType(contentUri, "application/vnd.android.package-archive");
            getActivity().startActivity(install);
        } else {
            Intent install = new Intent(Intent.ACTION_VIEW);
            install.setDataAndType(apkUri, "application/vnd.android.package-archive");
            install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(install);
        }

        android.os.Process.killProcess( android.os.Process.myPid());
        ActivityManager activitymanager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        activitymanager.restartPackage(getActivity().getPackageName());
    }
}

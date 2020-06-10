package com.zsinfo.guoranhao.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.activitys.MainActivity;
import com.zsinfo.guoranhao.event.EventDialog;
import com.zsinfo.guoranhao.event.EventInterface;
import com.zsinfo.guoranhao.event.EventRefesh;
import com.zsinfo.guoranhao.utils.DelayRunnable;
import com.zsinfo.guoranhao.utils.MyWebChromeClient;
import com.zsinfo.guoranhao.utils.MyWebViewClient;
import com.zsinfo.guoranhao.utils.SharedPreferencesUtil;
import com.zsinfo.guoranhao.utils.UrlUtils;
import com.zsinfo.guoranhao.utils.ViewUtils;
import com.zsinfo.guoranhao.utils.WebViewUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public abstract class BaseFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    protected WebView mWebView;
    protected MyWebChromeClient mMyWebChromeClient;
    protected MyWebViewClient mMyWebViewClient;
    protected TextView tv_header;
    protected String url;
    protected Dialog mDialog;
    private DelayRunnable delayRunnable;
    private LinearLayout ll_refresh;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base, null);

        mDialog = ViewUtils.createLoadingDialog(getActivity(), "正在加载...");

        mWebView = (WebView) view.findViewById(R.id.webview);
        tv_header = (TextView) view.findViewById(R.id.tv_header);
        ll_refresh = (LinearLayout) view.findViewById(R.id.ll_refresh);

        String header_content = getHeaderContent();
        if (TextUtils.isEmpty(header_content)) {
            tv_header.setVisibility(View.GONE);
        } else {
            tv_header.setVisibility(View.VISIBLE);
            tv_header.setText(header_content);
        }

        mMyWebChromeClient = new MyWebChromeClient(getActivity());
        mMyWebViewClient = new MyWebViewClient(getActivity(), mWebView);
        WebViewUtils.initWebView(mMyWebChromeClient, mMyWebViewClient, mWebView, getActivity());
        initWebViewListener();
        WebViewUtils.initSettings(mWebView, getActivity());

        url = getLoadUrl();
        mWebView.loadUrl(url);
        Log.e("加载的url-----Fragment", url);

        delayRunnable = new DelayRunnable(url);

        EventBus.getDefault().register(this);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            //相当于Fragment的onResume
            onHidden();
        } else {
            //相当于Fragment的onPause
        }
    }

    public void onHidden(){
        try {
            if (!url.equals("")) {  //防止出现空指针
                String subUrl = this.url.substring(UrlUtils.URL.length(), this.url.length());
                String json = SharedPreferencesUtil.GetJSMethod(subUrl);
                if (!TextUtils.isEmpty(json)) {
                    mWebView.loadUrl("javascript:" + json);
                }
                Log.e("2019.8.13--onHidden", "走了：" + subUrl + "---" + json);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getUrl() {
        return url;
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

                    break;
                case 1://隐藏dialog

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

    protected abstract String getLoadUrl();

    /**
     * 设置头部标题内容
     *
     * @return 若返回为空的话 则不显示
     */
    protected abstract String getHeaderContent();

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

    public void reload() {
        mWebView.loadUrl(url);
    }

    public void cancleDialog() {
        if (mDialog == null) return;
        if (mDialog.isShowing()) {
            mDialog.cancel();
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mDialog != null) {
            cancleDialog();
            mDialog = null;
        }
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        reload();
    }
}

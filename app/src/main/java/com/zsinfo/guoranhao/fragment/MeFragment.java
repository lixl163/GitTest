package com.zsinfo.guoranhao.fragment;

import com.zsinfo.guoranhao.utils.AppUtils;
import com.zsinfo.guoranhao.utils.LogUtils;
import com.zsinfo.guoranhao.utils.MyWebChromeClient;
import com.zsinfo.guoranhao.utils.SharedPreferencesUtil;
import com.zsinfo.guoranhao.utils.UrlUtils;

/**
 * Created by admin on 2017/8/28.
 * 我--个人中心
 */
public class MeFragment extends BaseFragment {

    @Override
    protected String getLoadUrl() {
        return UrlUtils.ABOUT_ME;
    }

    @Override
    protected String getHeaderContent() {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            LogUtils.e("当前版本===" + AppUtils.getVerName(getActivity()));
            mWebView.loadUrl("javascript:setAppVersion('" + AppUtils.getVerName(getActivity()) + "')");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!SharedPreferencesUtil.getJsCallBack().isEmpty() && mWebView != null) {
            mWebView.loadUrl("javascript:" + SharedPreferencesUtil.getJsCallBack());
            SharedPreferencesUtil.setJsCallBack("");
        }
    }

    public MyWebChromeClient getMyWebChromeClient() {
        return mMyWebChromeClient;
    }
}

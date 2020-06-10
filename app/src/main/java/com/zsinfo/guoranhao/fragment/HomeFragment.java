package com.zsinfo.guoranhao.fragment;

import com.zsinfo.guoranhao.utils.AppUtils;
import com.zsinfo.guoranhao.utils.LocationUtils;
import com.zsinfo.guoranhao.utils.LogUtils;
import com.zsinfo.guoranhao.utils.UrlUtils;

/**
 * Created by admin on 2017/8/28.
 * 首页--商品列表
 */
public class HomeFragment extends BaseFragment {

    @Override
    protected String getLoadUrl() {
        return UrlUtils.HOME_PAGE;
    }

    @Override
    protected String getHeaderContent() {
        return null;
    }

    ///首页是否请求过定位权限
    @Override
    public void onResume() {
        super.onResume();
        LocationUtils.startLocation(mWebView,getActivity());
        try {
            //打开应用，添加当前应用的版本
            LogUtils.e("当前版本===" + AppUtils.getVerName(getActivity()));
            mWebView.loadUrl("javascript:setAppVersion('" + AppUtils.getVerName(getActivity()) + "')");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

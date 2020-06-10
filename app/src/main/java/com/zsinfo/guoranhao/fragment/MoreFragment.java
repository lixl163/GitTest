package com.zsinfo.guoranhao.fragment;

import com.zsinfo.guoranhao.utils.UrlUtils;

/**
 * Created by admin on 2017/8/28.
 * 更多商品
 */
public class MoreFragment extends BaseFragment {
    @Override
    protected String getLoadUrl() {
        return UrlUtils.MORE_GOOD;
    }

    @Override
    protected String getHeaderContent() {
        return "全部商品";
    }
}

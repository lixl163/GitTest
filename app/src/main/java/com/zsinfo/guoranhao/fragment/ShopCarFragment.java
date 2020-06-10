package com.zsinfo.guoranhao.fragment;

import com.zsinfo.guoranhao.utils.UrlUtils;

/**
 * Created by admin on 2017/8/28.
 * 购物车
 */
public class ShopCarFragment extends BaseFragment {

    @Override
    protected String getLoadUrl() {
        return UrlUtils.SHOP_CAR;
    }

    @Override
    protected String getHeaderContent() {
        return "购物车";
    }

}

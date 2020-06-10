package com.zsinfo.guoranhao.fragment;

import com.zsinfo.guoranhao.utils.UrlUtils;

/**
 * Created by lixl on 2019/4/2.
 *
 * 做调整，将"购物车"改成"整件购买"
 */
public class WholeBuyFragment extends BaseFragment {

    @Override
    protected String getLoadUrl() {
        return UrlUtils.WHOLE_BUY;
    }

    @Override
    protected String getHeaderContent() {
        return "整件出售";
    }

}

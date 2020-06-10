package com.zsinfo.guoranhao.utils.Alipayutils;

/**
 *
 * 支付宝支付的观察者接口
 * Created by hyk on 2017/4/6.
 */

public interface AlipayObserver {
    public void update(AlipaySubject subject);
}

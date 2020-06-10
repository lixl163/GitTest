package com.zsinfo.guoranhao.event;

/**
 * Created by lixl on 2017/8/31.
 */
public class EventWebview extends BaseEvent {

    private String alipayStatus;  //成功9000  失败/取消7000  支付结果确认中(小概率状态)8000
    private int type = 0;//0 阿里 1 微信

    public EventWebview(String alipayStatus) {
        this.alipayStatus = alipayStatus;
    }

    public EventWebview(String alipayStatus, int type) {
        this.alipayStatus = alipayStatus;
        this.type = type;
    }

    public String getAlipayStatus() {
        return alipayStatus;
    }

    public void setAlipayStatus(String alipayStatus) {
        this.alipayStatus = alipayStatus;
    }


    public int getType() {
        return type;
    }

}

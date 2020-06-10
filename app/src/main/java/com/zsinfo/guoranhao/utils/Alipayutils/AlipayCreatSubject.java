package com.zsinfo.guoranhao.utils.Alipayutils;

/**
 * 具体的目标对象
 * Created by hyk on 2017/4/6.
 */

public class AlipayCreatSubject extends AlipaySubject {
    //获取内容信息
    private String resultContent;

    public String getResultContent() {
        return resultContent;
    }

    public void setResultContent(String mresultContent) {
        this.resultContent = mresultContent;
        //内容有了，说明更新了，通知所有订阅天气的人
        this.notifyObservers();
    }


}

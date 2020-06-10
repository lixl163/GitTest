package com.zsinfo.guoranhao.event;


/**
 * @author cyk
 * @date 2017年8月31日 11:12:00
 * @email choe0227@163.com
 * @desc  
 * @modifier
 * @modify_time
 * @modify_remark
 */
public class BaseEvent implements EventInterface {

    private String url;
    private int flag;

    public String  getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}

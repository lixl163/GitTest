package com.zsinfo.guoranhao.event;

/**
 * Created by admin on 2017/9/5.
 */

public class EventDialog implements EventInterface{
    //用来判断哪个页面需要显示或者隐藏
    private String url;
    //0 表示show 1 表示cancle
    private int flag;

    public String getUrl() {
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

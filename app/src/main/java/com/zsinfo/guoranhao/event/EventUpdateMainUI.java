package com.zsinfo.guoranhao.event;

/**
 * Created by admin on 2017/8/30.
 */

public class EventUpdateMainUI implements EventInterface {

    //标志刷新哪个页面
    private int flag;
    //是否需要刷新
    private boolean isNeedReload;
    //首页购物车商品的数量
    private String num;

    public EventUpdateMainUI(int flag,boolean isNeedReload) {
        this.flag = flag;
        this.isNeedReload = isNeedReload;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean getIsNeedReload() {
        return isNeedReload;
    }

    public void setIsNeedReload(boolean isNeedReload) {
        this.isNeedReload = isNeedReload;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }
}

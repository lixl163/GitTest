package com.zsinfo.guoranhao.event;


/**
 * @author cyk
 * @date 2017年9月4日 10:13:38
 * @email choe0227@163.com
 * @desc 登录成功之后的事件
 * @modifier
 * @modify_time
 * @modify_remark
 */
public class EventLogin implements EventInterface {
    // 0表示首页刷新的四个界面
    private int flag;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}

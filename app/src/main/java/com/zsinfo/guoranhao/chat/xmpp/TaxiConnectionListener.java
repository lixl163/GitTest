package com.zsinfo.guoranhao.chat.xmpp;

import com.zsinfo.guoranhao.chat.utils.LogUtils;
import com.zsinfo.guoranhao.chat.utils.ToastUtils;

import org.jivesoftware.smack.ConnectionListener;

/**
 * Created by lixl on 2018/7/11.
 * <p>
 * 连接监听类
 */
public class TaxiConnectionListener implements ConnectionListener {

    @Override
    public void connectionClosed() {
        // 大概5分钟之内没有任何操作，会失去xmpp连接
        LogUtils.e("Service ConnectionListener-----", "connectionClosed");
        removeXmpp();
    }

    @Override
    public void connectionClosedOnError(Exception e) {
        // 这里就是网络不正常或者被挤掉断线激发的事件
        LogUtils.e("Service ConnectionListener-----", "connectionClosedOnError,被挤掉线啦" + e.getMessage());
        removeXmpp();
        //todo 可以在这里做处理，可以判断1.是否有网络，2.被挤掉线了
    }

    @Override
    public void reconnectingIn(int i) {
        // 重新连接的动作正在进行的动作，里面的参数arg0是一个倒计时的数字，
        // 如果连接失败的次数增多，数字会越来越小0，开始的时候是12
        LogUtils.e("Service ConnectionListener-----", "reconnectingIn" + i);
    }

    @Override
    public void reconnectionSuccessful() {
        // 当网络断线了，重新连接上服务器触发的事件
        LogUtils.e("Service ConnectionListener-----", "reconnectionSuccessful重连成功");
    }

    @Override
    public void reconnectionFailed(Exception e) {
        // 重新连接失败
        LogUtils.e("Service ConnectionListener-----", "reconnectionFailed");
        removeXmpp();
    }

    /**
     * 无网络，断开连接的时候，需要把XMPPConnection设置为null
     */
    public void removeXmpp(){
        ToastUtils.showShortToast("请确认网络连接或重启聊天界面!");
        XmppConnection.closeConnection();
    }

}

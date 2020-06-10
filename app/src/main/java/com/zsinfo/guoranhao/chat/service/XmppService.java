//package com.zsinfo.guoranhao.chat.service;
//
//import android.app.Service;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.Handler;
//import android.os.IBinder;
//import android.os.Message;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//
//import com.zsinfo.guoranhao.chat.receiver.NetWorkReceiver;
//import com.zsinfo.guoranhao.chat.utils.NetUtil;
//import com.zsinfo.guoranhao.chat.utils.SPUtils;
//import com.zsinfo.guoranhao.chat.utils.ToastUtils;
//import com.zsinfo.guoranhao.chat.xmpp.XmppConnection;
//import com.zsinfo.guoranhao.utils.LogUtils;
//
//
///**
// * Created by lixl on 2018/7/5.
// *
// * Xmpp断线重连
// *
// 开启服务XmppService在后台长时间监听
// 在服务XmppService的onStart()中设置监听事件，如果xmpp断开则，打开定时器
// 在定时器中判断，如果有网络就重新连接，如果没有网络，继续走定时器，在后台5s判断一次
// 最终如果有了网络就再次连接服务器
// 在服务销毁的时候关闭定时器
// *
// */
//public class XmppService extends Service implements NetWorkReceiver.NetWorkCallback {
//
//    private NetWorkReceiver netWorkReceiver;
//
//    public Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            if (msg.what == 4) {
//                //是否连接成功
//                if (!XmppConnection.connection.isConnected()) {
//                    setXmppConnListener();
//                } else {
//                    //连接成功，需要重新登录
//                    LogUtils.e("Service  Handler-----", "需要登录");
//                    reLogin();
//                    XmppConnection.connection.addConnectionListener(XmppConnection.connectionListener);
//                }
//            } else if(msg.what == 1){
//                //已经连接成功，需要使用用户密码登录
//                reLogin();
//            }
//        }
//    };
//
//    /**
//     * 动态注册广播，这里和网络有关的
//     */
//    private void initReceiver() {
//        netWorkReceiver = new NetWorkReceiver();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
//        filter.setPriority(Integer.MAX_VALUE);
//        registerReceiver(netWorkReceiver, filter);
//        netWorkReceiver.setNetWorkCallback(this);
//    }
//
//
//    @Override
//    public void sendNetWorkChanged() {
//        setXmppConnListener();
//    }
//
//    /**
//     * 判断网络
//     * 判断xmpp连接状态
//     */
//    private void setXmppConnListener(){
//        //判断一下网络
//        if (!NetUtil.isNetConnected(this)){
//            ToastUtils.showShortToast("当前无网络状态");
//            return;
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                XmppConnection.getConnection();
//                handler.sendEmptyMessage(4);
//            }
//        }).start();
//    }
//
//    /**
//     * connectionClosed 需要重新登录
//     */
//    public void reLogin(){
//        String jid = (String) SPUtils.getParam(SPUtils.loginName, "");
//        String password = (String) SPUtils.getParam(SPUtils.loginPwd, "");
//        if (TextUtils.isEmpty(jid) || TextUtils.isEmpty(password)) {
//            return;
//        }
//        try {
//            XmppConnection.login(jid, password);
//            LogUtils.e("Service  Handler-----", "重新登录成功");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        initReceiver();
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(netWorkReceiver);
//        handler.removeCallbacksAndMessages(null);
//        LogUtils.e("Service onDestroy----------", "");
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//}

//package com.zsinfo.guoranhao.chat.receiver;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.text.TextUtils;
//
//import com.zsinfo.guoranhao.chat.service.XmppService;
//
///**
// * Created by Administrator on 2016/4/29.
// *
// * 使用广播监听 网络
// */
//public class NetWorkReceiver extends BroadcastReceiver {
//    private NetWorkCallback netWorkCallback;
//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String action = intent.getAction();
//        if (TextUtils.equals(action, ConnectivityManager.CONNECTIVITY_ACTION)) {
//            if (netWorkCallback != null){
//                netWorkCallback.sendNetWorkChanged();
//            }
//        } else if (TextUtils.equals(action, Intent.ACTION_SHUTDOWN)) {
//            context.stopService(new Intent(context, XmppService.class));
//        } else if (TextUtils.equals(action, Intent.ACTION_BOOT_COMPLETED)) {
//            context.startService(new Intent(context, XmppService.class));
//        }
//    }
//
//    public interface NetWorkCallback {
//        void sendNetWorkChanged();
//    }
//
//    public void setNetWorkCallback(NetWorkCallback netWorkCallback) {
//        this.netWorkCallback = netWorkCallback;
//    }
//}

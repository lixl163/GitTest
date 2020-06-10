package com.zsinfo.guoranhao.chat.receiver;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.zsinfo.guoranhao.activitys.MainApplication;
import com.zsinfo.guoranhao.chat.bean.ChatMessageBean;
import com.zsinfo.guoranhao.chat.utils.ChatUtils;
import com.zsinfo.guoranhao.chat.utils.SPUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 消息通知栏
 */
public class ChatBroadCastRecevier extends BroadcastReceiver {

    private NotificationManager notificationManager = null;
    protected static int notifyID = 0627; // start notification id
    private EventHandler eventHandler;

    private String msg;  //{"msgId":1530080665336,"msgType":"text","createTime":1530080665336,"content":"999","fromUser":"lxl","toUser":"admin"}
    private String chatType; //聊天类型，chat单聊
    private long msgId;
    private long createTime; //创建时间
    private String strVoiceTime = "";
    private String msgType;  //消息类型
    private String content;  //消息内容
    private String fromUser; //发送者
    private String toUser;  //接收者
    private String title;

    @SuppressLint("NewApi")
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("chat.receiver.CahtMessageBroadCast")) {
            //消息内容
            msg = intent.getStringExtra("msg");
            if (msg.equals("")) {
                return;
            }
            //开始解析
            try {
                JSONObject jsonObject = new JSONObject(msg);
                msgId = jsonObject.optLong("msgId");
                createTime = jsonObject.optLong("createTime");
                msgType = jsonObject.optString("msgType");
                content = jsonObject.optString("content");
                fromUser = jsonObject.optString("fromUser");
                toUser = jsonObject.optString("toUser");
                strVoiceTime = jsonObject.optString("strVoiceTime");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            try {
                if (fromUser == null) {
                    String from = intent.getStringExtra("msg_from");
                    int index = from.lastIndexOf("@"); //from.lastIndexOf("@")没有的时候，值为-1
                    if (index != -1){
                        fromUser = from.substring(0, index);
                    } else {
                        fromUser = from;
                    }
                }
            } catch (Exception e){  //StringIndexOutOfBoundsException
                e.printStackTrace();
            }
            if (msgType.equals("")) {
                msgType = "other";
            }
            if ("voice".equals(msgType)) {
                title = "语音消息";
            } else if ("image".equals(msgType)) {
                title = "图片消息";
            } else if ("text".equals(msgType)) {
                title = "文本消息:" + content;
            } else if ("face".equals(msgType)) {
                title = "表情消息";
            } else {
                title = "您有新消息";
            }
            //判断----当前不是聊天界面，并且当前应用不在前台，才可以发送通知
            //!".chat.activity.ChatActivity".equals(ChatUtils.whoRunningForeground(MainApplication.context))
            //!ChatUtils.isChatForeground(MainApplication.context)
            if (!(Boolean)SPUtils.getParam(SPUtils.isChatForeground, false) &&
                    !ChatUtils.isChatForeground(MainApplication.context)) {
                //表示处于后台
                sendNotification(MainApplication.context, fromUser, title);
            } else {
                //表示当前是在聊天界面，刷新消息列表适配器
                if (eventHandler != null) {
                    eventHandler.onNetChange(true, (ChatMessageBean) intent.getSerializableExtra("msg_bean"));
                }
            }
        }
    }

    public void setEventHandler(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
    }

    /**
     * 显示消息提示框
     *
     * @param message
     */
    private void sendNotification(Context appContext, String title, String message) {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        try {
            String notifyText = message;
            // notification titile
            String contentTitle = title;
            String packageName = appContext.getApplicationInfo().packageName;

            Uri defaultSoundUrlUri = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // create and send notificaiton
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(appContext)
                    .setSmallIcon(appContext.getApplicationInfo().icon)
                    .setSound(defaultSoundUrlUri)
                    .setWhen(System.currentTimeMillis()).setAutoCancel(true);

            Intent msgIntent = new Intent(MainApplication.context, com.zsinfo.guoranhao.chat.activity.ChatActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(appContext,
                    notifyID, msgIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentTitle(contentTitle);
            mBuilder.setTicker(notifyText);
            mBuilder.setContentText(notifyText);
            mBuilder.setContentIntent(pendingIntent);
            Notification notification = mBuilder.build();

            notificationManager.notify(notifyID, notification);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public abstract interface EventHandler {

        public abstract void onNetChange(boolean paramBoolean, ChatMessageBean messageBean);

        public abstract void onNotify(String paramName, String paramValue);
    }
}

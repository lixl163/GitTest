package com.zsinfo.guoranhao.chat.xmpp;

import android.content.Intent;
import android.util.Log;

import com.zsinfo.guoranhao.activitys.MainApplication;
import com.zsinfo.guoranhao.chat.bean.ChatMessageBean;
import com.zsinfo.guoranhao.chat.db.TalkListManager;
import com.zsinfo.guoranhao.chat.utils.ChatUtils;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lixl on 2018/7/12.
 *
 * 消息类监听
 */
public class MessagePackageListener implements PacketListener {

    private TalkListManager talkListManager = TalkListManager.getInstance();

    @Override
    public void processPacket(Packet packet) {
        if (packet == null)
            return;

        Message msg = (Message) packet;
        Log.e("接收的消息*****", msg.toXML());
        Log.e("接收的消息*****", msg.getType().name()+"");
        Log.e("接收的消息*****", msg.getFrom()+"");
        Log.e("接收的消息*****", msg.getTo()+"");
        Log.e("接收的消息*****", msg.getBody().toString()+"");
        //{"msgId":1530863845432,"msgType":"text","createTime":1530863845432,"content":"好的","fromUser":"lxl","toUser":"wukanghui"}
        //{"msgId":1531119804068,"msgType":"voice","createTime":1531119804068,"content":"zhangshuoinfo.b0.upaiyun.com/zsChat/20180709/MP3/1531119803910.mp3","fromUser":"lxl","toUser":"admin","strVoiceTime":"2"}
        //if (msg.getType().name().equals("chat")){ //是否是聊天，ios返回的是normal
            //todo 获取消息类型msgType，如果是语音，属于未读消息
            String json = msg.getBody().toString();
            String msgType = "";
            ChatMessageBean messageBean = null;
            try {
                JSONObject jsonObject = new JSONObject(json);
                //先获取消息类型msgType
                msgType = jsonObject.optString("msgType");
                if (msgType.equals("text")){
                    //推送过来的json字符串
                    messageBean = ChatUtils.getMessage(json);
                } else if (msgType.equals("image")){
                    messageBean = ChatUtils.getMessage(json);
                } else if (msgType.equals("voice")){
                    messageBean = ChatUtils.getVoiceMessage(json);
                }
                //存放数据库
                talkListManager.insertMessage(true, messageBean, "");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            //发送广播传递数据
            Intent intent = new Intent();
            intent.putExtra("msg", msg.getBody().toString());
            intent.putExtra("msg_from", msg.getFrom());
            intent.putExtra("msg_to", msg.getTo());
            intent.putExtra("msg_bean", messageBean);
            intent.setAction("chat.receiver.CahtMessageBroadCast");
            MainApplication.context.sendBroadcast(intent);
       // }
    }
}

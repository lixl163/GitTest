package com.zsinfo.guoranhao.chat.bean;

import java.io.Serializable;

/**
 * Created by admin on 2018/7/4.
 *
 * 聊天界面 消息类
 */
public class ChatMessageBean implements Serializable {

    /**
     *
     * {"msgId":1530695096067,
     * "msgType":"text",
     * "createTime":1530695096067,"
     * content":"你好",
     * "fromUser":"lxl",
     * "toUser":"admin"}
     */

    private long msgId;
    private String msgType;  //目前3种类型：text/image/voice/location
    private long createTime; //创建时间
    private String content;  //内容，如果是图片或者语音"http://"，当为位置消息时，描述位置
    private String fromUser;
    private String toUser;
    private String strVoiceTime;  //语音时长
    private int msgIsRead = 0;  //消息是否点击，0未读状态，接收的语音默认是未读状态
    private int sendStatus = 0; //消息发送状态，0发送中，1发送失败，2发送成功

    /**
     * 默认无参构造方法
     */
    public ChatMessageBean() {
    }

    /**
     * 实例化一个单聊的文本、图片消息
     * @param msgId
     * @param msgType
     * @param createTime
     * @param content
     * @param fromUser
     * @param toUser
     */
    public ChatMessageBean(long msgId, String msgType, long createTime, String content, String fromUser, String toUser,
                           int msgIsRead, int sendStatus) {
        this.msgId = msgId;
        this.msgType = msgType;
        this.createTime = createTime;
        this.content = content;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.msgIsRead = msgIsRead;
        this.sendStatus = sendStatus;
    }

    /**
     * 实例化一个单聊发送的语音消息
     * @param msgId
     * @param msgType
     * @param createTime
     * @param content
     * @param fromUser
     * @param toUser
     * @param strVoiceTime
     */
    public ChatMessageBean(long msgId, String msgType, long createTime, String content, String fromUser, String toUser, String strVoiceTime,
            int msgIsRead, int sendStatus) {
        this.msgId = msgId;
        this.msgType = msgType;
        this.createTime = createTime;
        this.content = content;
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.strVoiceTime = strVoiceTime;
        this.msgIsRead = msgIsRead;
        this.sendStatus = sendStatus;
    }

    public long getMsgId() {
        return msgId;
    }

    public void setMsgId(long msgId) {
        this.msgId = msgId;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getStrVoiceTime() {
        return strVoiceTime;
    }

    public void setStrVoiceTime(String strVoiceTime) {
        this.strVoiceTime = strVoiceTime;
    }

    public int getMsgIsRead() {
        return msgIsRead;
    }

    public void setMsgIsRead(int msgIsRead) {
        this.msgIsRead = msgIsRead;
    }

    public int getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(int sendStatus) {
        this.sendStatus = sendStatus;
    }

    @Override
    public String toString() {
        return "ChatMessageBean{" +
                "msgId=" + msgId +
                ", msgType='" + msgType + '\'' +
                ", createTime=" + createTime +
                ", content='" + content + '\'' +
                ", fromUser='" + fromUser + '\'' +
                ", toUser='" + toUser + '\'' +
                ", strVoiceTime='" + strVoiceTime + '\'' +
                ", msgIsRead=" + msgIsRead +
                ", sendStatus=" + sendStatus +
                '}';
    }
}

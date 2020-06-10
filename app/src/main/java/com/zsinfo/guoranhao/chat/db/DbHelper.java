package com.zsinfo.guoranhao.chat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

    private static DbHelper mDbHelper;

    //数据库名称
    private static final String DATABASE_NAME = "zsChat.db";
    //数据库版本
    private static final int Version = 1;
    //表名（单聊，群聊，好友，群组，订阅号等等）
    public static final String SINGLE_CHAT = "singleChat";
//    public static final String GROUP_CHAT = "groupChat";
    public static final String id = "_id";
    public static final String msgId = "msgId";
    public static final String msgType = "msgType"; //text文字消息，image图片消息，voice语音消息，location位置消息
    public static final String msgCreateTime = "msgCreateTime";
    public static final String msgContent = "msgContent";
    public static final String msgFrom = "msgFrom";
    public static final String msgTo = "msgTo";
    public static final String voiceTime = "strVoiceTime";//语音时长
    public static final String msgIsRead = "msgIsRead";  // 0表示未读，1表示已读
    public static final String msgSendStatus = "msgSendStatus"; //消息发送状态，0发送中，1发送失败，2发送成功


    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, Version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //消息记录表
        String sql_chatRecord = "create table " + SINGLE_CHAT +
                "(" + id + " integer primary key," +
                msgId + " integer not null," +
                msgType + " varchar(10)," +
                msgCreateTime + " integer," +
                msgContent + " varchar(50)," +
                msgFrom + " varchar(10)," +
                msgTo + " varchar(10)," +
                voiceTime + " varchar(10)," +
                msgIsRead + " integer," +
                msgSendStatus + " integer)";
        db.execSQL(sql_chatRecord);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }

    public static DbHelper getInstence(Context mCtx){
        if(mDbHelper == null) {
            mDbHelper = new DbHelper(mCtx);
        }
        return mDbHelper;
    }

}

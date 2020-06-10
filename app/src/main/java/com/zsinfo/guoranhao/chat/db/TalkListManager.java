package com.zsinfo.guoranhao.chat.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.zsinfo.guoranhao.activitys.MainApplication;
import com.zsinfo.guoranhao.chat.bean.ChatMessageBean;
import com.zsinfo.guoranhao.chat.utils.TimeSortCompare;
import com.zsinfo.guoranhao.utils.LogUtils;

import org.jivesoftware.smack.XMPPException;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by lixl on 2018/7/6.
 *
 * 聊天处理：增删改查...
 */
public class TalkListManager {

    private static TalkListManager talkListManager;
    private DbHelper helper;
    private SQLiteDatabase mDatabase;

    private TalkListManager(){
        try {
            this.openDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static TalkListManager getInstance(){
        if(talkListManager == null) {
            talkListManager = new TalkListManager();
        }
        return talkListManager;
    }

    /**
     * 打开数据库
     * @throws SQLException
     */
    private void openDB() throws SQLException {
        helper = DbHelper.getInstence(MainApplication.context);
        mDatabase = helper.getWritableDatabase();
    }

    /**
     * 关闭数据库
     */
    public void closeDB() {
        mDatabase.close();
    }

    /**
     * 插入一条数据
     * @param isCome  是否是发送过来的消息，默认自己发送的消息标注已读
     * @param myMsg
     * @param json  发送给xmpp的json格式，防止误网络情况下，重发消息
     * @throws XMPPException
     */
    public long insertMessage(boolean isCome, ChatMessageBean myMsg, String json){
        long id = -2;
        ContentValues values = new ContentValues();
        values.put(DbHelper.msgId, myMsg.getMsgId());
        values.put(DbHelper.msgType, myMsg.getMsgType());
        values.put(DbHelper.msgCreateTime, myMsg.getCreateTime());
        values.put(DbHelper.msgContent, myMsg.getContent());
        values.put(DbHelper.msgFrom, myMsg.getFromUser());
        values.put(DbHelper.msgTo, myMsg.getToUser());
        values.put(DbHelper.voiceTime, myMsg.getStrVoiceTime());
        if (isCome){
            values.put(DbHelper.msgIsRead, 0);
        } else {
            values.put(DbHelper.msgIsRead, 1);
        }
        values.put(DbHelper.msgSendStatus, myMsg.getSendStatus());
        id = mDatabase.insert(helper.SINGLE_CHAT, null, values);
        LogUtils.e("DB---insert成功---", id+"");
        return id;
    }

    /**
     * 删除数据
     * @param toUser
     */
    public void deleteMessage(String toUser){
        String sql = "delete from " + helper.SINGLE_CHAT + " where " + helper.msgFrom + " =? or " + helper.msgTo + " =?";
        mDatabase.execSQL(sql, new String[]{toUser, toUser});
    }

    /**
     * 查询所有聊天列表, 点对点聊天（当前聊天的对象）
     * @return  SQL 语句中, asc是指定列按升序排列，desc则是指定列按降序排列
     */
    public List<ChatMessageBean> queryTalkList(String toUser){
        List<ChatMessageBean> beans = new ArrayList<ChatMessageBean>();
        String sql = "select * from " + helper.SINGLE_CHAT +
                " where " + helper.msgFrom + " =? or " + helper.msgTo + " =?" +
                " order by " + helper.msgCreateTime + " asc";
        LogUtils.e("DB---query---", sql);
        Cursor cursor = mDatabase.rawQuery(sql, new String[]{toUser, toUser});
        while (cursor.moveToNext()) {
            ChatMessageBean bean = new ChatMessageBean();
            bean.setMsgId(cursor.getLong(cursor.getColumnIndex(helper.msgId)));
            bean.setMsgType(cursor.getString(cursor.getColumnIndex(helper.msgType)));
            bean.setCreateTime(cursor.getLong(cursor.getColumnIndex(helper.msgCreateTime)));
            bean.setContent(cursor.getString(cursor.getColumnIndex(helper.msgContent)));
            bean.setFromUser(cursor.getString(cursor.getColumnIndex(helper.msgFrom)));
            bean.setToUser(cursor.getString(cursor.getColumnIndex(helper.msgTo)));
            bean.setStrVoiceTime(cursor.getString(cursor.getColumnIndex(helper.voiceTime)));
            bean.setMsgIsRead(cursor.getInt(cursor.getColumnIndex(helper.msgIsRead)));
            bean.setSendStatus(cursor.getInt(cursor.getColumnIndex(helper.msgSendStatus)));
            beans.add(bean);
        }
        cursor.close();
        return beans;
    }

    /**
     * 查询所有聊天列表, 查询所有消息记录
     * @return
     */
    public List<ChatMessageBean> queryTalkList(){
        List<ChatMessageBean> beans = new ArrayList<ChatMessageBean>();
        String sql = "select * from " + helper.SINGLE_CHAT +
                " order by " + helper.msgCreateTime + " asc";
        LogUtils.e("DB---query---", sql);
        Cursor cursor = mDatabase.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            ChatMessageBean bean = new ChatMessageBean();
            bean.setMsgId(cursor.getLong(cursor.getColumnIndex(helper.msgId)));
            bean.setMsgType(cursor.getString(cursor.getColumnIndex(helper.msgType)));
            bean.setCreateTime(cursor.getLong(cursor.getColumnIndex(helper.msgCreateTime)));
            bean.setContent(cursor.getString(cursor.getColumnIndex(helper.msgContent)));
            bean.setFromUser(cursor.getString(cursor.getColumnIndex(helper.msgFrom)));
            bean.setToUser(cursor.getString(cursor.getColumnIndex(helper.msgTo)));
            bean.setStrVoiceTime(cursor.getString(cursor.getColumnIndex(helper.voiceTime)));
            bean.setMsgIsRead(cursor.getInt(cursor.getColumnIndex(helper.msgIsRead)));
            bean.setSendStatus(cursor.getInt(cursor.getColumnIndex(helper.msgSendStatus)));
            beans.add(bean);
            //LogUtils.e("DB---query---", bean.toString());
        }
        cursor.close();
        return beans;
    }

    /**
     * 按时间排序，查询固定条数内容
     * @param startIndex  从哪开始
     * @param count  每次加载多少条数据
     * @return
     */
    public List<ChatMessageBean> queryMsgLimit(String fromUser, String toUser, int startIndex, int count){
        try{
            List<ChatMessageBean> beans = new ArrayList<>();
            String sql = "select * from " + helper.SINGLE_CHAT +
                    " where (" + helper.msgFrom + " =? and " + helper.msgTo + " =?" +
                    ") or (" + helper.msgFrom + " =? and " + helper.msgTo + " =?" +
//                    "DbHelper.msgFrom + " = ? and " + DbHelper.msgTo + " = ? or " + DbHelper.msgFrom + " = ? and " + DbHelper.msgTo + " = ?"
                    ") order by " + helper.msgCreateTime + " desc" +    //desc降序查询，最新的一条消息，在最上面
                    " limit ?,?";
            LogUtils.e("DB---query---", sql);
            Cursor cursor = mDatabase.rawQuery(sql, new String[]{fromUser, toUser, toUser, fromUser, startIndex+"", count+""});
            while (cursor.moveToNext()) {
                ChatMessageBean bean = new ChatMessageBean();
                bean.setMsgId(cursor.getLong(cursor.getColumnIndex(helper.msgId)));
                bean.setMsgType(cursor.getString(cursor.getColumnIndex(helper.msgType)));
                bean.setCreateTime(cursor.getLong(cursor.getColumnIndex(helper.msgCreateTime)));
                bean.setContent(cursor.getString(cursor.getColumnIndex(helper.msgContent)));
                bean.setFromUser(cursor.getString(cursor.getColumnIndex(helper.msgFrom)));
                bean.setToUser(cursor.getString(cursor.getColumnIndex(helper.msgTo)));
                bean.setStrVoiceTime(cursor.getString(cursor.getColumnIndex(helper.voiceTime)));
                bean.setMsgIsRead(cursor.getInt(cursor.getColumnIndex(helper.msgIsRead)));
                bean.setSendStatus(cursor.getInt(cursor.getColumnIndex(helper.msgSendStatus)));
                beans.add(bean);
                //LogUtils.e("DB---query---", bean.toString());
            }
            cursor.close();
            Collections.sort(beans, new TimeSortCompare());  //将取出来的10条消息，按照时间大小顺序排列
            return beans;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 更改消息发送状态
     */

    /**
     * 查询未读消息数量
     */

    /**
     * 更改未读消息的状态--变成已读消息
     */
}

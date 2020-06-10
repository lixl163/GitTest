package com.zsinfo.guoranhao.chat.contacts;

/**
 * Created by lixl on 2018/7/3.
 *
 * 常量类
 */
public class Constants {

    /**
     * 正式环境，or 测试环境
     */
    public static final boolean isDebug = true;


    /**
     * 服务器地址
     */
    public static final String XMPP_HOST = getXmppHost();
    /**
     * 服务器名称(域名)
     */
    public static final String XMPP_SERVICE_NAME = getXmppServiceName();
    /**
     * 端口号
     */
    public static final int XMPP_PORT = 5222;

    private static String getXmppHost(){
        if (isDebug){
            return "61.164.113.166";
        }
        return "";
    }

    private static String getXmppServiceName(){
        if (isDebug){
            return "grhao.com";
        }
        return "";
    }
}

package com.zsinfo.guoranhao.chat.utils;

import android.text.TextUtils;

import java.text.SimpleDateFormat;

/**
 * Created by lixl on 2018/7/6.
 */
public class TimeUtils {

    /**
     * 是否跟当前时间一个年份
     * @param time
     * @return
     */
    public static boolean isSameYear(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        String year1 = sdf.format(time);
        String year2 = sdf.format(System.currentTimeMillis());
        if (TextUtils.equals(year1, year2)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取时间，作为上传图片及录音到又拍云时的文件路径
     *
     * @return
     */
    public static String getDatas(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String path = sdf.format(time);
        return path;
    }

    /**
     * 获取时间，只有时间，没有日期，用于显示在最近会话的item上
     *
     * @param time
     * @return
     */
    public static String getDate(long time) {
        String date = null;
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        date = df.format(time);
        return date;
    }

    /**
     * 显示具体月份和日期，表示是在同一年
     * @param time
     * @return
     */
    public static String geTimeNoS(long time) {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss");
        String times = df.format(time);
        return times;
    }

    /**
     * 将long型时间转String的工具方法，显示年月日 时分秒
     */
    public static String getTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = sdf.format(time);
        return date;
    }

}

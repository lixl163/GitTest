package com.zsinfo.guoranhao.utils;/*
 * @创建者     choe
 * @创建时间   2016/5/22 20:01
 * @描述	      ${TODO}
 * @更新者     $Author$
 * @更新时间   2016/5/22
 * @更新描述   ${TODO}
 */

import android.content.Context;
import android.content.SharedPreferences;

import com.zsinfo.guoranhao.activitys.MainApplication;


public class SPUtils {

    public static SharedPreferences sharedPreferences = MainApplication.baseApplication.getSharedPreferences("gsspsinfo", Context.MODE_PRIVATE);
    public static SharedPreferences.Editor editor = sharedPreferences.edit();



    public static synchronized void setValue(String key,String value){
        editor.putString(key,value).commit();
    }

    public static synchronized String getValue(String key){
        return sharedPreferences.getString(key,"");
    }

}

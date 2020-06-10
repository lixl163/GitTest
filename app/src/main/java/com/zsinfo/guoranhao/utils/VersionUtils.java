package com.zsinfo.guoranhao.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * @author cyk
 * @date 2016/12/15 12:03
 * @email choe0227@163.com
 * @desc 解决版本的适配问题的工具类
 * @modifier
 * @modify_time
 * @modify_remark
 */
public class VersionUtils {

    /**
     * 6.0 手机系统以上 检查并请求权限
     * @param context 必须为 Activity
     */
    public static void checkAndRequestPermissionAbove23(Activity context){
        int i = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA);
        if (i!= PackageManager.PERMISSION_GRANTED){

            if (ActivityCompat.shouldShowRequestPermissionRationale(context, //用户点击拒绝过一次，就给一个解释
                    Manifest.permission.READ_CONTACTS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            }else {//用户从来没有拒绝过

                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.CAMERA},Constant.TAKE_PHOTO_PERMISSION
                );
            }
        }
    }

}

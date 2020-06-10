package com.zsinfo.guoranhao.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 异常补货工具类
 */
public class KqwException implements Thread.UncaughtExceptionHandler{

    private static KqwException myCrashHandler;

    private Context mContext;

    private KqwException(Context context) {
        mContext = context;
    }

    public static synchronized KqwException getInstance(Context context) {
        if (null == myCrashHandler) {
            myCrashHandler = new KqwException(context);
        }
        return myCrashHandler;
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        long threadId = thread.getId();
        String exceptionName = throwable.toString();
        String exceptionMessage = Arrays.toString(throwable.getStackTrace());
        Log.i("KqwException", "---------------------异常开始展示---------------------------------");
        Log.i("KqwException", "threadId = " + threadId);
        Log.i("KqwException", "exceptionName = " + exceptionName);
        Log.i("KqwException", "exceptionMessage = " + exceptionMessage);
        Log.i("KqwException", "------------------------------------------------------");
        Log.i("KqwException", "------------------应用被重启----------------");
        // 重启应用
        mContext.startActivity(mContext.getPackageManager().getLaunchIntentForPackage(mContext.getPackageName()));
        //干掉当前的程序
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}

package com.zsinfo.guoranhao.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 */
public class ToastUtil {


    private static ExecutorService threadPool = Executors.newCachedThreadPool();
    //开启子线程
    public static void runOnThread(Runnable task) {

        threadPool.execute(task);
    }

    private static Handler sHandler = new Handler(Looper.getMainLooper());

    public static void runOnUIThread(Runnable task) {

        sHandler.post(task);
    }

    public static void showToast(final Context context, final String text) {
        runOnUIThread(new Runnable() {

            @Override
            public void run() {

                Toast.makeText(context, text, Toast.LENGTH_LONG).show();
            }
        });

    }


}

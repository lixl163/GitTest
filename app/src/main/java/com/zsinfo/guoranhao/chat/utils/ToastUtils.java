package com.zsinfo.guoranhao.chat.utils;

import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.zsinfo.guoranhao.activitys.MainApplication;
import com.zsinfo.guoranhao.utils.LogUtils;

/**
 * Created by lixl on 2018/7/5.
 *
 * 自定义Toast
 */
public class ToastUtils {

    private static Toast toast;
    private static View view;

    private ToastUtils() {
    }

    private static void getToast() {
        if (toast == null) {
            toast = new Toast(MainApplication.context);
        }
        if (view == null) {
            view = Toast.makeText(MainApplication.context, "", Toast.LENGTH_SHORT).getView();
        }
        toast.setView(view);
    }

    public static void showShortToast(CharSequence msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(int resId) {
        showToast(resId, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(CharSequence msg) {
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(int resId) {
        showToast(resId, Toast.LENGTH_LONG);
    }

    private static void showToast(CharSequence msg, int duration) {
        try {
            getToast();
            toast.setText(msg);
            toast.setDuration(duration);
            //toast.setGravity(Gravity.CENTER, 0, 0);  //Toast弹出位置在界面中间
            toast.show();
        } catch (Exception e) {
            com.zsinfo.guoranhao.utils.LogUtils.e("TOAST", e.getMessage());
        }
    }

    private static void showToast(int resId, int duration) {
        try {
            if (resId == 0) {
                return;
            }
            getToast();
            toast.setText(resId);
            toast.setDuration(duration);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } catch (Exception e) {
            LogUtils.e("TOAST", e.getMessage());
        }
    }

    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}

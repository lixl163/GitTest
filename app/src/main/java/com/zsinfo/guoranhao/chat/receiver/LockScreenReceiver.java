package com.zsinfo.guoranhao.chat.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zsinfo.guoranhao.chat.utils.SPUtils;
import com.zsinfo.guoranhao.utils.LogUtils;

/**
 * Created by lixl on 2018/7/24.
 */
public class LockScreenReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)) {
            LogUtils.e("LockScreenReceiver------", "screen on");
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            LogUtils.e("LockScreenReceiver------", "screen off");
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) {
            LogUtils.e("LockScreenReceiver------", "screen unlock");
        } else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
            LogUtils.e("LockScreenReceiver------", " receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
        }

        //注意，这个广播只在聊天界面存在，注意解决黑屏情况
        SPUtils.setParam(SPUtils.isChatForeground, true);
        //需要注意，在聊天界面finish之后，改false
    }
}

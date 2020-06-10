package com.zsinfo.guoranhao.utils;

import com.zsinfo.guoranhao.event.EventDialog;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by admin on 2017/9/5.
 */

public class DelayRunnable implements Runnable {
    private String url;
    public DelayRunnable (String url){
        this.url=url;
    }
    @Override
    public void run() {
        try {
            Thread.sleep(Constant.DEFAULT_TIMEOUT);
            EventDialog dialog =new EventDialog();
            dialog.setFlag(1);
            dialog.setUrl(url);
            EventBus.getDefault().post(dialog);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

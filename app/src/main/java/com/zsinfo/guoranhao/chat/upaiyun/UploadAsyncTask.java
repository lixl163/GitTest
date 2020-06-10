package com.zsinfo.guoranhao.chat.upaiyun;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * 使用 又拍云 存储图片和语音
 */
public class UploadAsyncTask extends AsyncTask<String, Void, String> {

    String type;
    private Context context;
    private ProgressDialog dialog;
    private Callback callback;

    // 空间域名
    private final String DEFAULT_PATH = "zhangshuoinfo.b0.upaiyun.com";
    // 申请的APP_KEY
    private final String APP_KEY = "LaubRPoyoLzq9tJ82/z+RSmFUVY=";
    // 空间名称
    private final String BUCKET = "zhangshuoinfo";
    // 过期时间 必须大于当前时间
    private final long EXPIRATION = System.currentTimeMillis() / 1000 + 1000 * 5 * 10;

    public UploadAsyncTask(String type, Context context, Callback callback) {
        super();
        this.type = type;
        this.context = context;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        dialog = new ProgressDialog(context);
        dialog.setMessage("正在发送，请稍等...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        if (TextUtils.isEmpty(params[0])) {
            return "";
        }
        String string = null;
        Log.i("msg", "文件大小：" + new File(params[0]).length() / (1024 * 1024) + "M");

        // 设置存储在服务器的路径和文件名称
        String SAVE_PATH = File.separator + "zsChat" + File.separator
                + System.currentTimeMillis() + "." + type;

        // 获取base64编码后的policy
        String policy;
        try {
            policy = UpYunUtils.makePolicy(SAVE_PATH, EXPIRATION, BUCKET);
            // 根据表单api签名密钥对policy进行签名
            // 通常我们建议这一步在用户自己的服务器上进行，并通过http请求取得签名后的结果。
            String signature = UpYunUtils.signature(policy + "&" + APP_KEY);
            @SuppressWarnings("unused")
            long startTime = System.currentTimeMillis();
            // 上传文件到对应的bucket中去。
            System.gc();
            string = "http://" + DEFAULT_PATH
                    + Uploader.upload(policy, signature,
                    BUCKET, params[0]);
        } catch (UpYunException e) {
            e.printStackTrace();
        }
        return string;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        dialog.dismiss();
        if (result != null) {
            callback.send(result);
        }
    }

    public interface Callback {
        public void send(String result);
    }

}

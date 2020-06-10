package com.zsinfo.guoranhao.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.beans.KFBean;
import com.zsinfo.guoranhao.utils.AppUtils;
import com.zsinfo.guoranhao.utils.DataCleanManager;
import com.zsinfo.guoranhao.utils.NetMessageUtil;
import com.zsinfo.guoranhao.utils.NetUtil;
import com.zsinfo.guoranhao.utils.SPUtils;
import com.zsinfo.guoranhao.utils.SharedPreferencesUtil;
import com.zsinfo.guoranhao.utils.ToastUtil;
import com.zsinfo.guoranhao.utils.UrlUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static com.zsinfo.guoranhao.chat.utils.SPUtils.loginTime;

public class StartActivity extends AppCompatActivity {
    private ImageView iv_start;
    private final String START_PIC_NAME="gss_start_pic.png";
    private final int SUCCESS=3000;
    private final int FAILED=3001;
    private final int TIAO_ZHUAN_NO_DELAY=3002;
    private final int DISPLAY_LOCAL_PIC=3003;
    private final int COUNT_TIME_CHANGE=3004;
    /**
     * 启动页停留的时间
     */
    private final long STOPTIME=2000;
    /**
     * 在广告页停留的界面
     */
    private final int DELAY_TIME=3000;
    private String adPageId="";
    private long startTime;
    private TextView tv_countTime;
    private boolean isNeedLauchMain=true;

    Handler handler = new Handler() {
        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
            switch(msg.what){
                case TIAO_ZHUAN_NO_DELAY:
                    if (isNeedLauchMain){
                        Log.e("aaaa","ssss");
                        isNeedLauchMain=false;
                        startActivity(new Intent(StartActivity.this, MainActivity.class));
                        if (SharedPreferencesUtil.getFirstStart()) {
                            // TODO: 2017/3/2
                            startActivity(new Intent(StartActivity.this, SpalshActivity.class));
                        }
                    }
                    finish();
                        break;
                case FAILED: //先判断是否在启动页停留了3000ms
                    long endTime = System.currentTimeMillis();
                    long sleepTime=0;
                    if (endTime-startTime<STOPTIME){
                        sleepTime=STOPTIME-endTime+startTime;
                    }
                    handler.sendEmptyMessageDelayed(TIAO_ZHUAN_NO_DELAY,sleepTime);
                        break;

                case SUCCESS:
                    Bitmap obj = (Bitmap) msg.obj;
                    iv_start.setImageBitmap(obj);
                    startCountTime();
                    handler.sendEmptyMessageDelayed(TIAO_ZHUAN_NO_DELAY,DELAY_TIME);
                    break;

                case DISPLAY_LOCAL_PIC:
                    String path=(String)msg.obj;
                    Bitmap bitmap = BitmapFactory.decodeFile(path);
                    iv_start.setImageBitmap(bitmap);
                    startCountTime();
                    handler.sendEmptyMessageDelayed(TIAO_ZHUAN_NO_DELAY,DELAY_TIME);
                    break;
                case COUNT_TIME_CHANGE:
                    int timeRemain= (int) msg.obj;
                    String timeContent=timeRemain+" 跳过";
                    tv_countTime.setText(timeContent);
                    break;
            }
        }
    };
    public void startCountTime(){
        tv_countTime.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i =DELAY_TIME/1000 ; i >=0; i--) {
                    try {
                        Message obtain = Message.obtain();
                        obtain.what = COUNT_TIME_CHANGE;
                        obtain.obj = i;
                        if (null!=handler) handler.sendMessage(obtain);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        tv_countTime= (TextView) findViewById(R.id.tv_countTime);
        iv_start = (ImageView) findViewById(R.id.iv_start);
        startTime= System.currentTimeMillis();

        checkH5Version();  //2019-9-25逻辑更改
        //handler.sendEmptyMessageDelayed(TIAO_ZHUAN_NO_DELAY, DELAY_TIME);   //2019-9-25逻辑更改：不管网络请求成功/失败，都需要跳转到MainActivity

        //判断是否是第一次启动APP，false 检查是否需要更新图片，然后替换图片
//        if (!SharedPreferencesUtil.getFirstStart()) {
//            checkIsNeedUpdateImg();
//        }else {
//            handler.sendEmptyMessageDelayed(TIAO_ZHUAN_NO_DELAY, DELAY_TIME);
//        }
//        tv_countTime.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                handler.sendEmptyMessage(TIAO_ZHUAN_NO_DELAY);
//            }
//        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(R.layout.none_view);
//        if (iv_start != null) {
//            Drawable drawable = iv_start.getDrawable();
//            if (drawable != null && drawable instanceof BitmapDrawable) {
//                BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
//                Bitmap bitmap = bitmapDrawable.getBitmap();
//                if (bitmap != null && !bitmap.isRecycled()) {
//                    bitmap.recycle();
//                    bitmap = null;
//                }
//
//            }
//            iv_start = null;
//        }
    }

    /**
     * 检查h5版本：通过版本号解决缓存问题
     */
    private void checkH5Version() {
        if (!NetUtil.isNetworkAvailable()) {
            ToastUtil.showToast(this, "网络连接不可用");
            return;
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(UrlUtils.SERVE + "?method=version_check&type=h5")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                //失败
                handler.sendEmptyMessageDelayed(TIAO_ZHUAN_NO_DELAY, DELAY_TIME);   //2019-9-25逻辑更改：不管网络请求成功/失败，都需要跳转到MainActivity
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //成功
                handler.sendEmptyMessageDelayed(TIAO_ZHUAN_NO_DELAY, DELAY_TIME);   //2019-9-25逻辑更改：不管网络请求成功/失败，都需要跳转到MainActivity
                String result = response.body().string();
                Log.e("lixl版本更新ToH5", result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String statusCode = jsonObject.optString("statusCode");
                    if (!"100000".equals(statusCode)) {
                        return;
                    }
                    String data = jsonObject.optString("data");
                    JSONObject dataObj = new JSONObject(data);
                    String number = dataObj.optString("number");
                    if (SPUtils.getValue("h5VersionName").equals(number)) {
                        return;
                    } else {
                        //本地版本和服务器的不一致，存储本地，并且清空缓存
                        SPUtils.setValue("h5VersionName", number);
                        DataCleanManager.cleanCache(StartActivity.this);
                        Log.e("lixl版本更新ToH5", "清除缓存");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 检查是否要更新图片
     */
    private void checkIsNeedUpdateImg(){
        NetMessageUtil.checkStartImageUpdate(new NetMessageUtil.CheckImageCallBack(){
            @Override
            public void success(String result) {
                try {
                    Log.e("StartActivity",result);
                    JSONObject object=new JSONObject(result);
                    String statusCode = object.optString("statusCode");
                    String statusStr = object.optString("statusStr");
                    String data = object.optString("data");
                    if ("100000".equals(statusCode)){//请求成功
                        JSONObject jsonObject=new JSONObject(data);
                        int Id = jsonObject.optInt("id");
                        adPageId= String.valueOf(Id);
                        if (TextUtils.isEmpty(adPageId)){//后台是没有数据的 data==空串
                            //观望3s 跳吧 少年
                            handler.sendEmptyMessage(TIAO_ZHUAN_NO_DELAY);
                        }else {//后台是有数据的

                            if (SharedPreferencesUtil.getActionPageNote().equals(adPageId)){
                                //加载本地
                                String s = loadLocalPic();
                                File file=new File(s);
                                if (!file.exists()){ //保存的文件被删除了 大兄弟
                                    Log.e("StartActivity加载本地","保存的文件被删除了");
                                    String pageUrl = jsonObject.optString("adPic");
                                    Log.e("StartActivity","pageUrl==="+pageUrl);
                                    downloadPic(pageUrl);
                                }else {// 没删除那你就加载咯
                                    Log.e("StartActivity加载本地","没删除那你就加载咯");
                                    long endTime = System.currentTimeMillis();
                                    long sleepTime=0;
                                    if (endTime-startTime<STOPTIME){
                                        sleepTime=STOPTIME-endTime+startTime;
                                    }
                                    Message displayPic = Message.obtain();
                                    displayPic.what=DISPLAY_LOCAL_PIC;
                                    displayPic.obj=s;
                                    handler.sendMessageDelayed(displayPic,sleepTime);
                                }
                            }else {
                                //下载并保存 显示
                                Log.e("StartActivity服务器拿的","6666");
                                String pageUrl = jsonObject.optString("adPic");
                                Log.e("StartActivity","pageUrl==="+pageUrl);
                                downloadPic(pageUrl);
                            }
                        }

                    }else { //请求失败  直接跳转
                        handler.sendEmptyMessage(FAILED);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(FAILED);
                }
            }

            @Override
            public void failed(VolleyError volleyError) {
                handler.sendEmptyMessage(FAILED);

            }
        });

    }
    /**
     * 下载启动页图片
     */
    private void downloadPic(String url){
        NetMessageUtil.getStartImage(this,url, new NetMessageUtil.ImageCallBack() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                Log.d("StartActivity","下载成功");
                saveImage2Local(bitmap);
                long endTime = System.currentTimeMillis();
                long sleepTime=0;
                if (endTime-startTime<STOPTIME){
                    sleepTime=STOPTIME-endTime+startTime;
                }
                Message obtain = Message.obtain();
                obtain.obj=bitmap;
                obtain.what=SUCCESS;
                handler.sendMessageDelayed(obtain,sleepTime);
            }

            @Override
            public void onFailed(VolleyError e) {
                Log.e("StartActivity","下载失败");
                handler.sendEmptyMessage(FAILED);
            }
        });
    }
    /**
     * 保存图片到本地
     */
    private void saveImage2Local(Bitmap bitmap){
        try {
            File startFile=new File(getFilesDir(),START_PIC_NAME);
            if (startFile.exists()){
                startFile.delete();
            }
            startFile=new File(getFilesDir(),START_PIC_NAME);
            FileOutputStream outputStream = new FileOutputStream(startFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.e("StartActivity","FileNotFoundException 保存图片成功");
            Log.e("StartActivity","保存这个版本马屁"+adPageId);
            if (!TextUtils.isEmpty(adPageId)){ //将最新版本图片保存到本地
                SharedPreferencesUtil.setActionPageNote(adPageId);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("StartActivity","FileNotFoundException 保存图片失败");
        } catch (IOException e) {
            Log.e("StartActivity","IOException 保存图片失败");
            e.printStackTrace();
        }

    }
    /**
     * 获取本地图片路径
     */
    private String loadLocalPic(){
        File file =new File(getFilesDir(),START_PIC_NAME);
        if (!file.exists()){
            Log.e("StartActivity","本地文件不存在");
            return null;
        }else {

            return file.getAbsolutePath();
        }
    }
}

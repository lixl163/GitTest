package com.zsinfo.guoranhao.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.alipay.sdk.util.H5PayResultModel;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import com.zsinfo.guoranhao.activitys.MainApplication;
import com.zsinfo.guoranhao.activitys.WebviewActivity;
import com.zsinfo.guoranhao.event.EventRefesh;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * @author cyk
 * @date 2016/12/14 14:17
 * @email choe0227@163.com
 * @desc
 * @modifier
 * @modify_time
 * @modify_remark
 * onLoadResource:加载资源时响应
 * onPageStart:在加载页面时响应
 * onPageFinish:在加载页面结束时响应
 * onReceiveError:在加载出错时响应
 */
public class MyWebViewClient extends WebViewClient {
    private Activity mContext;
    private static String currentUrl;
    private WebView webview;
    private Dialog progressDialog;


    public MyWebViewClient(Activity context, WebView webView) {
        mContext = context;
        webview = webView;

        progressDialog = ProgressDialog.show(mContext, null,"加载中...");
    }

    @Override
    public boolean shouldOverrideUrlLoading(final WebView view, String url) {
        Log.e("加载的url", "==============" + url);
        if (url.startsWith("tel:")) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            mContext.startActivity(intent);
            return true;
        }
        //判断所加载的地址是否是连连支付的回调
        for (int i = 0; i < UrlUtils.LIANLIAN_CALLBACKS.length; i++) {
            if (url.equals(UrlUtils.LIANLIAN_CALLBACKS[i])) {
                //截取目标地址的url
                String lianlianUrl = getRealUrlByUrl(url);
                // 跳转到一个新的页面
                Intent intent = new Intent(mContext, WebviewActivity.class);
                String title = getTitleByUrl(lianlianUrl);
                intent.putExtra(Constant.NEXT_PAGE_TITLE_NAME, title);
                intent.putExtra(Constant.NEXT_PAGE_URL, lianlianUrl);
                mContext.startActivity(intent);
                return true;
            }
        }
        if (!NetUtil.isNetworkAvailable()) {
            Toast.makeText(mContext, "当前网络连接不可用", Toast.LENGTH_LONG).show();
            return true;
        }
        //如果不是网页就调用其他浏览器
        // 返回值代表了不使用默认的动作（调用其他浏览器）
        if (!(url.startsWith("http") || url.startsWith("https"))) {
            return true;
        }
        final PayTask task = new PayTask(mContext);
        final String ex = task.fetchOrderInfoFromH5PayUrl(url);
        if (!TextUtils.isEmpty(ex)) {
            Log.e("paytask:::::", url);
            new Thread(new Runnable() {
                public void run() {
                    System.out.println("payTask:::" + ex);
                    final H5PayResultModel result = task.h5Pay(ex, true);
                    if (!TextUtils.isEmpty(result.getReturnUrl())) {
                        mContext.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                String aliPayUrl = result.getReturnUrl();
                                aliPayUrl = getRealUrlByUrl(aliPayUrl);
                                Log.e("支付宝回调url:::::", aliPayUrl);
                                // 跳转到一个新的页面
                                Intent intent = new Intent(mContext, WebviewActivity.class);
                                String titleByUrl = getTitleByUrl(aliPayUrl);
                                intent.putExtra(Constant.NEXT_PAGE_TITLE_NAME, titleByUrl);
                                intent.putExtra(Constant.NEXT_PAGE_URL, aliPayUrl);
                                mContext.startActivity(intent);
                            }
                        });
                    }
                }
            }).start();
        } else {
            view.loadUrl(url);
        }
        return true;
    }

    /**
     * 根据传入的url 来确定下个页面的title
     *
     * @param url
     * @return
     */
    private String getTitleByUrl(String url) {
        String title = "";
        if (url.contains(UrlUtils.ORDER_MANAGE_PAGE)) {
            title = "订单管理";
        } else if (url.contains(UrlUtils.PRE_ORDER_MANAGE_PAGE)) {
            title = "预购管理";
        } else if (url.contains(UrlUtils.ONLINE_RECHARGE_PAGE)) {
            title = "在线充值";
        }
        return title;
    }

    /**
     * 根据传入的url 来获取截取后的真实地址
     *
     * @param url
     * @return
     */
    private String getRealUrlByUrl(String url) {
        String realUrl = "";
        if (url.contains(UrlUtils.ORDER_MANAGE_PAGE)) {
            realUrl = UrlUtils.ORDER_MANAGE_PAGE;
        } else if (url.contains(UrlUtils.PRE_ORDER_MANAGE_PAGE)) {
            realUrl = UrlUtils.PRE_ORDER_MANAGE_PAGE;
        } else if (url.contains(UrlUtils.ONLINE_RECHARGE_PAGE)) {
            realUrl = UrlUtils.ONLINE_RECHARGE_PAGE;
        }
        return realUrl;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        super.onReceivedError(view, request, error);
        // TODO: 2017/10/26  错误
        com.umeng.socialize.utils.Log.e("conker", error.getDescription() + "");
        //小米6上会抛出该异常，
        if (TextUtils.equals(error.getDescription(),"net::ERR_CONTENT_DECODING_FAILED")){
            return;
        }
        if(request.isForMainFrame()){// 在这里加上个判断
            // 显示错误界面
            EventBus.getDefault().post(new EventRefesh());
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        currentUrl = url;
        super.onPageStarted(view, url, favicon);
        progressDialog.show();  //开始
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        if (UrlUtils.cacheUrl1.equals(url) || UrlUtils.cacheUrl2.equals(url)) {//是否需要缓存的页面
            String filename = url;
            if (UrlUtils.cacheUrl1.equals(url)) {
                filename = UrlUtils.cacheUrl2;
            }
            Log.e("onPageFinished=", "" + filename);
            File filesDir = new File(Environment.getExternalStorageDirectory() + "/guoranhao/html/");
            if (!filesDir.exists()) {

                filesDir.mkdirs();
            }
            File file = new File(filesDir, filename);
            //是首页需要缓存的页面
            if (MainApplication.isNeedCacheHomePage) {//需要下载到本地
                if (file.exists()) { //判断是否之前缓存过这个html 有的话就删除 没有就直接缓存

                    file.delete();
                    file = new File(filesDir, filename);
                }
                super.onPageFinished(view, url);
                MainApplication.isNeedCacheHomePage = false;
                getHtmlText(url, file);

            }/*else if (MainApplication.isNeedCacheAboutMe){
                if (file.exists()){ //判断是否之前缓存过这个html 有的话就删除 没有就直接缓存

                    file.delete();
                    file =new File(filesDir,filename);
                    Log.e("onPageFinished","需要缓存，且有缓存文件");
                }
                super.onPageFinished(view, url);
                Log.e("onPageFinished","需要缓存，但是没有缓存");
                MainApplication.isNeedCacheAboutMe=false;
                getHtmlText(url, file);

            }*/ else { //已经下载过了 就不需要下载了
                if (file.exists()) { //判断文件是否存在 存在就加载缓存文件
                    Uri uri = Uri.fromFile(file);
                    super.onPageFinished(view, uri.toString());
                } else { //如果文件被删除，则重新缓存
                    super.onPageFinished(view, url);
                    getHtmlText(url, file);//重新下载
                }
            }
            //删除历史记录
            WebViewUtils.clearHistroy(webview);
        } else { //不需要缓存的页面
            super.onPageFinished(view, url);
        }

        progressDialog.cancel(); //结束
    }

    @Override
    public void onLoadResource(WebView view, String url) {
        if (url.endsWith(".jpg") || url.endsWith(".png") || url.endsWith(".jpeg")) {//如果加载的是图片
            //截取图片的名称
            String filename = url.substring(url.lastIndexOf("/") + 1);
            //创建存储文件的路径
            File filesDir = new File(Environment.getExternalStorageDirectory() + "/guoranhao");
            if (!filesDir.exists()) {
                filesDir.mkdirs();
            }
            File picFile = new File(filesDir, filename);
            if (picFile.exists()) { //本地有这个图片

                //加载本地图片
                String localUrl = localPicToUrl(picFile);
                if (TextUtils.isEmpty(localUrl)) {//获取到本地图片的URL失败
                    //Log.e("本地有图","获取url失败");
                    super.onLoadResource(view, url);
                } else { //获取到本地图片的url成功
                    //Log.e("本地有图","获取url成功");
                    super.onLoadResource(view, localUrl);
                }
            } else { //本地没有这个图片
                //1.先加载
                super.onLoadResource(view, url);
                //2. 缓存图片到本地
                downLoadPicToLocal(url, picFile);
            }

        } else { //不是图片走正常的逻辑
            super.onLoadResource(view, url);
        }
    }


    private static OkHttpClient mClient = new OkHttpClient();
    private static String path;

    /**
     * 下载图片到本地
     */
    private void downLoadPicToLocal(String url, final File file) {
        Request request = new Request.Builder().url(url).build();
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //失败的时候
                if (file.exists()) {
                    file.delete();
                }
            }

            @Override
            public void onResponse(Response response) throws IOException {
                InputStream inputStream = response.body().byteStream();
                FileOutputStream fos = new FileOutputStream(file);

                byte[] b = new byte[1024];
                int len = -1;
                while ((len = inputStream.read(b)) != -1) {

                    fos.write(b, 0, len);
                    fos.flush();
                }

                fos.close();
                inputStream.close();

                //Log.e("保存图片成功","保存图片成功");
            }
        });
    }


    /**
     * 将本地图片转换成url
     */
    public String localPicToUrl(File fileName) {
        String url;
        Uri uri = Uri.fromFile(fileName);
        url = uri.toString();
        return url;
    }

    /**
     * 下载html文件
     *
     * @param url
     * @param destFile
     */
    private void getHtmlText(final String url, final File destFile) {
        Request request = new Request.Builder().url(url).build();

        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("请求失败", "");
            }

            @Override
            public void onResponse(Response response) throws IOException {
                ResponseBody body = response.body();
                byte[] bytes = body.source().readByteArray();
                try {
                    FileOutputStream fos = new FileOutputStream(destFile);
                    fos.write(bytes, 0, bytes.length);
                    fos.flush();
                    fos.close();
                    //Log.e("下载html完成","finished");
                } catch (Exception e) {
                    e.printStackTrace();
                    destFile.delete();
                }
            }
        });
    }

    /**
     * 获取当前的网页连接
     *
     * @return
     */
    public String getCurrentUrlLink() {
        return currentUrl;
    }

}

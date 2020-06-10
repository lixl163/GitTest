package com.zsinfo.guoranhao.activitys;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.yanzhenjie.durban.Controller;
import com.yanzhenjie.durban.Durban;
import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.js_interact.SettingInfo;
import com.zsinfo.guoranhao.utils.Constant;
import com.zsinfo.guoranhao.utils.MyWebChromeClient;
import com.zsinfo.guoranhao.utils.MyWebViewClient;
import com.zsinfo.guoranhao.utils.UrlUtils;
import com.zsinfo.guoranhao.utils.WebViewUtils;
import com.zsinfo.guoranhao.utils.imageload.GlideImageLoader;
import com.zsinfo.guoranhao.widget.imagepicker.ImagePickerAdapter;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;


// 之前项目中无法调起评论图上传,故新建
public class EvaluateActivity extends AppCompatActivity {

    private WebView webView;

    public static final int REQUEST_CODE_SELECT = 100;
    private TextView tv_header;

    private ArrayList<ImageItem> selImageList; //当前选择的所有图片

    private ImagePickerAdapter adapter;


    private RelativeLayout rl_back;

    private void initWidget() {
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, 1, R.drawable.alert_bg);
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    private void openImageChooserActivity() {

        ImagePicker.getInstance().setSelectLimit(1);
        Intent openintent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(openintent, REQUEST_CODE_SELECT);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluate);
        initWidget();
        initImagePicker();
        MainApplication.pushStack(this);
        tv_header = (TextView) findViewById(R.id.tv_header_title);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        tv_header.setVisibility(View.VISIBLE);
        tv_header.setText(getIntent().getStringExtra(Constant.NEXT_PAGE_TITLE_NAME));
        webView = (WebView) findViewById(R.id.mWebView);
        final WebCall webCall = new WebCall() {
            @Override
            public void fileChose(ValueCallback<Uri> uploadMsg) {
                Log.e("Conker", "onShowFileChooser: " + "");


            }

            @Override
            public void fileChose5(ValueCallback<Uri[]> uploadMsg) {
                Log.e("Conker", "onShowFileChooser: " + "");
                openImageChooserActivity();


            }
        };

        initSettings(webView, this);

        WebChromeClient c = new WebChromeClient() {

            // For Android >= 5.0
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                Log.e("Conker", "onShowFileChooser: " + "");

                uploadMessageAboveL = filePathCallback;

                if (webCall != null) webCall.fileChose5(filePathCallback);

                return true;
            }

            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback, String acceptType, String capture) {
                uploadMessage = valueCallback;
                openImageChooserActivity();
            }

        };
        webView.setWebChromeClient(c);


        webView.addJavascriptInterface(new SettingInfo(this, webView), "android");

        initWebViewListener();

        WebViewUtils.initSettings(webView, this);

        webView.loadUrl(UrlUtils.URL + getIntent().getStringExtra(Constant.NEXT_PAGE_URL));


        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public interface WebCall {

        void fileChose(ValueCallback<Uri> uploadMsg);

        void fileChose5(ValueCallback<Uri[]> uploadMsg);
    }

    /**
     * 初始化一些监听
     */
    protected void initWebViewListener() {
        //取消长按出现复制
        webView.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View view) {
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MainApplication.popStack(this);
    }

    public File getAppRootPath(Context context) {
        if (sdCardIsAvailable()) {
            return Environment.getExternalStorageDirectory();
        } else {
            return context.getFilesDir();
        }
    }

    public boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().canWrite();
        } else
            return false;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {

                    ArrayList<String> imagePathList = new ArrayList<>();
                    String url = images.get(0).path;
                    imagePathList.add(url);
                    Durban.with(this)
                            // 裁剪界面的标题。
                            .title("Crop")
//                            .statusBarColor(ContextCompat.getColor(this, R.color.main_color))
//                            .toolBarColor(ContextCompat.getColor(this, R.color.main_color))
//                            .navigationBarColor(ContextCompat.getColor(this, R.color.main_color))
                            // 图片路径list或者数组。
                            .inputImagePaths(imagePathList)
                            // 图片输出文件夹路径。
                            .outputDirectory(getAppRootPath(this).getAbsolutePath())
                            // 裁剪图片输出的最大宽高。
                            .maxWidthHeight(500, 500)
                            // 裁剪时的宽高比。
                            .aspectRatio(1, 1)
                            // 图片压缩格式：JPEG、PNG。
                            .compressFormat(Durban.COMPRESS_JPEG)
                            // 图片压缩质量，请参考：Bitmap#compress(Bitmap.CompressFormat, int, OutputStream)
                            .compressQuality(90)
                            // 裁剪时的手势支持：ROTATE, SCALE, ALL, NONE.
                            .gesture(Durban.GESTURE_ALL)
                            .controller(
                                    Controller.newBuilder()
                                            .enable(false) // 是否开启控制面板。
                                            .rotation(true) // 是否有旋转按钮。
                                            .rotationTitle(true) // 旋转控制按钮上面的标题。
                                            .scale(true) // 是否有缩放按钮。
                                            .scaleTitle(true) // 缩放控制按钮上面的标题。
                                            .build()) // 创建控制面板配置。
                            .requestCode(200)
                            .start();


                }
            }
        } else if (requestCode == 200) {

            // 解析剪切结果：
            if (resultCode == Activity.RESULT_OK) {
                ArrayList<String> mImageList = Durban.parseResult(data);

                String imageurl = mImageList.get(0);

                // TODO: 2017/8/22 上传图片
                Uri result = Uri.fromFile(new File(imageurl));
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(result);
                }

                Uri[] results = new Uri[]{result};
                if (uploadMessageAboveL != null) {
                    uploadMessageAboveL.onReceiveValue(results);
                }

            } else {
                // TODO 其它处理...
                Uri result = Uri.fromFile(new File(""));
                Uri[] results = new Uri[]{result};
                uploadMessageAboveL.onReceiveValue(results);
                uploadMessageAboveL = null;

            }

        }

        if (resultCode == Activity.RESULT_CANCELED) {
            Uri result = Uri.fromFile(new File(""));
            Uri[] results = new Uri[]{result};
            if (uploadMessageAboveL != null) {
                uploadMessageAboveL.onReceiveValue(results);
                uploadMessageAboveL = null;
            }
        }
    }

    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;

    /**
     * 初始化webview的settings信息
     */
    private void initSettings(WebView mWebView, Activity activity) {
        WebSettings settings = mWebView.getSettings();
        String userAgentString = settings.getUserAgentString();
        settings.setUserAgentString(userAgentString + "grh_app/android");
        settings.setAllowContentAccess(true);
        settings.setAppCacheMaxSize(1024 * 1024 * 8);//设置缓冲大小，我设的是8M
        String appCacheDir = activity.getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        settings.setAppCachePath(appCacheDir);
        settings.setAppCacheEnabled(true);
//        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        settings.setCacheMode(WebSettings.LOAD_DEFAULT);  //判断是否有网络，有的话，使用LOAD_DEFAULT，无网络时，使用LOAD_CACHE_ELSE_NETWORK
//        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //优先使用缓存
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);//  不使用缓存：
        settings.setGeolocationEnabled(true);//支持定位
        settings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //开启 database storage API 功能
        settings.setDatabaseEnabled(true);
        settings.setDatabasePath(activity.getCacheDir() + "/guoranhao.db");
        settings.setJavaScriptEnabled(true);//支持js
        settings.setDefaultTextEncodingName("UTF-8");//设置编码方式
        settings.setBuiltInZoomControls(false); //便页面支持缩放：
//        settings.setSupportZoom(true);  //支持缩放
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
//        settings.supportMultipleWindows();  //支持多窗口
        settings.setAllowFileAccess(true);//设置可以访问文件
        settings.setNeedInitialFocus(true);//当webview调用requestFocus时为webview设置节点 暂不知道什么用
        settings.setJavaScriptCanOpenWindowsAutomatically(true);//支持通过JS打开新窗口
//        打开页面时， 自适应屏幕：
        settings.setUseWideViewPort(true);//设置此属性，可任意比例缩放 将图片调整到适合webview的大小
        settings.setLoadWithOverviewMode(true);// 缩放至屏幕的大小
        settings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        settings.setDomStorageEnabled(true); //不设置此 无法加载h5

    }
}

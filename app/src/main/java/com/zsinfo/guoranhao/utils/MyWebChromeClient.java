package com.zsinfo.guoranhao.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.yanzhenjie.durban.Controller;
import com.yanzhenjie.durban.Durban;
import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.utils.imageload.GlideImageLoader;
import com.zsinfo.guoranhao.widget.imagepicker.ImagePickerAdapter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * @author cyk
 * @date 2016/12/14 14:02
 * @email choe0227@163.com
 * @desc
 * @modifier
 * @modify_time
 * @modify_remark
 */
public class MyWebChromeClient extends WebChromeClient {
    private Activity mContext;
    private String mCameraFilePath;
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static String TAG = "MyWebChromeClient";
    public final static int FILE_CHOOSER_RESULT_CODE = 10000;
    public final static int FILE_CHOOSER_RESULT_CODE_FOR_CREAM = 10001;

    public static final int REQUEST_CODE_SELECT = 100;

    private ArrayList<ImageItem> selImageList; //当前选择的所有图片

    private ImagePickerAdapter adapter;


    public MyWebChromeClient(Activity context) {
        mContext = context;
        initImagePicker();
        initWidget();
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
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

    // For Android >= 5.0
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        uploadMessageAboveL = filePathCallback;
        openImageChooserActivity();

        return true;
    }

    @Override
    //扩容
    public void onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        quotaUpdater.updateQuota(requiredStorage * 2);
    }

    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        Log.e("h5端的log", String.format("%s -- From line %s of %s", message, lineNumber, sourceID));
    }


    private void openImageChooserActivity() {

        ImagePicker.getInstance().setSelectLimit(1);
        Intent openintent = new Intent(mContext, ImageGridActivity.class);
        mContext.startActivityForResult(openintent, REQUEST_CODE_SELECT);
//        initDialog();
    }

    /**
     * 上传头像时的弹出框
     */
    private void initDialog() {
        new AlertDialog.Builder(mContext)
                .setTitle("更改头像")
                .setItems(new String[]{"拍照", "图库选取"},
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                switch (which) {
                                    case 0:
                                        Intent i1 = createCameraIntent();
                                        mContext.startActivityForResult(Intent.createChooser(i1, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
                                        break;
                                    case 1:
                                        Intent i = createFileItent();
                                        mContext.startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
                                        break;
                                }

                            }
                        }).setNegativeButton("取消", null).show();
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

    private void initWidget() {
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(mContext, selImageList, 1, R.drawable.alert_bg);
    }

    /**
     * 创建选择图库的intent
     *
     * @return
     */
    private Intent createFileItent() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        return intent;
    }


    /**
     * 创建调用照相机的intent
     *
     * @return
     */
    private Intent createCameraIntent() {
        VersionUtils.checkAndRequestPermissionAbove23(mContext);
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File externalDataDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        System.out.println("externalDataDir:" + externalDataDir);
        File cameraDataDir = new File(externalDataDir.getAbsolutePath()
                + File.separator + "browser-photo");
        cameraDataDir.mkdirs();
        mCameraFilePath = cameraDataDir.getAbsolutePath() + File.separator
                + System.currentTimeMillis() + ".jpg";
        System.out.println("mcamerafilepath:" + mCameraFilePath);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                Uri.fromFile(new File(mCameraFilePath)));

        return cameraIntent;
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
                    Durban.with(mContext)
                            // 裁剪界面的标题。
                            .title("Crop")
//                            .statusBarColor(ContextCompat.getColor(this, R.color.main_color))
//                            .toolBarColor(ContextCompat.getColor(this, R.color.main_color))
//                            .navigationBarColor(ContextCompat.getColor(this, R.color.main_color))
                            // 图片路径list或者数组。
                            .inputImagePaths(imagePathList)
                            // 图片输出文件夹路径。
                            .outputDirectory(getAppRootPath(mContext).getAbsolutePath())
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

    public boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().canWrite();
        } else
            return false;
    }

    public File getAppRootPath(Context context) {
        if (sdCardIsAvailable()) {
            return Environment.getExternalStorageDirectory();
        } else {
            return context.getFilesDir();
        }
    }

    /**
     * 处理拍照返回函数
     * @param requestCode
     * @param resultCode
     * @param data
     */
//    public  void onActivityResult(int requestCode, int resultCode, Intent data){
//        Log.e("mCameraFilePath",mCameraFilePath+"");
//        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
//            if (null == uploadMessage&& null == uploadMessageAboveL)
//                return;
//            Uri result = data == null || resultCode != Activity.RESULT_OK ? null
//                    : data.getData();
//            if (uploadMessageAboveL != null) {//5.0以上
//                onActivityResultAboveL(requestCode, resultCode, data);
//            }else if(uploadMessage != null) {
//                if (result == null && data == null
//                        && resultCode == Activity.RESULT_OK) {
//                    File cameraFile = new File(mCameraFilePath);
//
//                    Bitmap bitmap1 = getimage(cameraFile.getPath());
//
//                    result = Uri.parse(MediaStore.Images.Media.insertImage(
//                            mContext.getContentResolver(), bitmap1, null, null));
//                }
//                Log.e(TAG,"5.0-result="+result);
//                uploadMessage.onReceiveValue(result);
//                uploadMessage = null;
//            }
//        }
//    }

    /**
     * 处理拍照返回函数  5。0以上
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        Log.e(TAG, "5.0+ 返回了");
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            } else {
                File cameraFile = new File(mCameraFilePath);
                Bitmap bitmap1 = getimage(cameraFile.getPath());
                Uri result = Uri.parse(MediaStore.Images.Media.insertImage(
                        mContext.getContentResolver(), bitmap1, null, null));
                results = new Uri[]{result};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

    /**
     * 根据图片路径获取图p片
     *
     * @param srcPath
     * @return
     */
    private Bitmap getimage(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 裁剪图片大小
     *
     * @param image
     * @return
     */
    private Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}

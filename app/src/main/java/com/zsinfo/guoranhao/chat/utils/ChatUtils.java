package com.zsinfo.guoranhao.chat.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.zsinfo.guoranhao.chat.bean.ChatMessageBean;
import com.zsinfo.guoranhao.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ChatUtils {

    /**
     * 图片进行二次采样
     * @param path  图片路径
     * @return
     */
    public static Bitmap handlerBitmap(String path) {
        int sampleSize = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int wid = options.outWidth;
        int hei = options.outHeight;
        if (wid > 200 || hei > 150) {
            if (wid > 200) {
                sampleSize = wid / 200;
            } else if (hei > 150) {
                sampleSize = hei / 150;
            }
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 图片的二次采样
     * @param bytes  图片以流的形式
     * @return
     */
    public static Bitmap handlerBitmap(byte[] bytes) {
        int sampleSize = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        int height = options.outHeight;
        int width = options.outWidth;
        if (width > 200 || height > 150) {
            if (width > 200) {
                sampleSize = width / 200;
            } else if (height > 150) {
                sampleSize = height / 150;
            }
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    /**
     * 头像二次采样
     *
     * @param path
     * @return
     */
    public static Bitmap getNativeBitmap(String path) {
        int sampleSize = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;  //将这个值置为true，那么在解码的时候将不会返回bitmap，只会返回这个bitmap的尺寸。这个属性的目的是，如果你只想知道一个bitmap的尺寸，但又不想将其加载到内存时。
        BitmapFactory.decodeFile(path, options);
        int height = options.outHeight;  //表示这个Bitmap的宽和高
        int width = options.outWidth;
        if (width > 100 || height > 100) {
            if (width > height) {
                sampleSize = height / 100;
            } else if (height > 100) {
                sampleSize = width / 100;
            }
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;  //小于1的时候，将会被当做1处理，如果大于1，那么就会按照比例（1 / inSampleSize）缩小bitmap的宽和高、降低分辨率，大于1时这个值将会被处置为2的倍数
        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 根据一个网络连接(String)获取bitmap图像
     *
     * @param imageUri
     * @return
     * @throws MalformedURLException
     */
    public static Bitmap getbitmap(String imageUri) {
        Log.v(TAG, "getbitmap:" + imageUri);
        // 显示网络上的图片
        Bitmap bitmap = null;
        try {
            URL myFileUrl = new URL(imageUri);
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();

            Log.v(TAG, "image download finished." + imageUri);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            bitmap = null;
        } catch (IOException e) {
            e.printStackTrace();
            Log.v(TAG, "getbitmap bmp fail---");
            bitmap = null;
        }
        return bitmap;
    }

    /**
     * 压缩图片
     *
     * @param path
     * @param size
     * @return
     */
    public static String saveBitmapP(String path, int size) {
        int sampleSize = 1;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        int wid = options.outWidth;
        int hei = options.outHeight;
        if (wid > size || hei > size) {
            sampleSize = wid / size;
        }
        options.inSampleSize = sampleSize;
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        File file = null;
        try {
            file = new File(Environment.getExternalStorageDirectory()
                    + "/zsChat/" + System.currentTimeMillis() + ".png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }

    /**
     * 判断ChatActivity是否在前台运行
     *
     * @param context
     * @return String
     */
    public static boolean isChatForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = manager.getRunningTasks(1);
        if (taskInfo != null && taskInfo.size() > 0) {
            ComponentName cpn = taskInfo.get(0).topActivity;
            if (cpn.getClassName().contains("ChatActivity")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断ChatActivity是否在前台运行
     *
     * @param context
     * @return String
     */
    public static String whoRunningForeground(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfo = manager.getRunningTasks(1);
        if (taskInfo.size() > 0) {
            String cpn = taskInfo.get(0).topActivity.getShortClassName();
            return cpn;
        }
        return null;
    }

    /**
     * asmack的消息体中body部分解析成自定义的消息体
     * { "msgId": 1451455734279000, "msgType":
     * "voice", "creatTime": "1451455734279", "content":
     * "zhangshuoinfo.b0.upaiyun.com\/2015\/12\/1451438702.mp3", "fromUser":
     * "lzy", "toUser": "lizhengyang" }
     */
    public static ChatMessageBean getMessage(String messageBody) {
        ChatMessageBean myMessage = null;
        try {
            JSONObject obj = new JSONObject(messageBody);
            long msgId = obj.optLong("msgId");
            String msgType = obj.optString("msgType");
            long createTime = obj.optLong("createTime");
            String content = obj.optString("content");
            String fromUser = obj.optString("fromUser");
            String toUser = obj.optString("toUser");
            myMessage = new ChatMessageBean(msgId, msgType, createTime, content, fromUser, toUser, 1, 2);
        } catch (JSONException e) {
            com.zsinfo.guoranhao.utils.LogUtils.e("解析message---------", "文本图片消息体错误");
        }
        return myMessage;
    }

    /**
     * 解析语音消息
     *
     * @param messageBody
     * @return
     */
    public static ChatMessageBean getVoiceMessage(String messageBody) {
        ChatMessageBean myMessage = null;
        try {
            JSONObject obj = new JSONObject(messageBody);
            long msgId = obj.optLong("msgId");
            String msgType = obj.optString("msgType");
            long createTime = obj.optLong("createTime");
            String content = obj.optString("content");
            String fromUser = obj.optString("fromUser");
            String toUser = obj.optString("toUser");
            String strVoiceTime = obj.optString("strVoiceTime");
            myMessage = new ChatMessageBean(msgId, msgType, createTime, content, fromUser, toUser, strVoiceTime, 0, 2);
        } catch (JSONException e) {
            LogUtils.e("解析message---------", "语音消息体错误");

        }
        return myMessage;
    }

    /**
     * 生成文本和图片消息体
     *
     * @param msgType  消息类型
     * @param content  消息内容
     * @param fromUser 发送方
     * @param toUser   接收方
     * @return 构建成的json字符串
     */
    public static String toJson(String msgType, String content, String fromUser, String toUser) {
        return "{\"msgId\":" + System.currentTimeMillis() +
                ",\"msgType\":\"" + msgType +
                "\",\"createTime\":" + System.currentTimeMillis() +
                ",\"content\":\"" + content +
                "\",\"fromUser\":\"" + fromUser +
                "\",\"toUser\":\"" + toUser + "\"}";
    }

    /**
     * 生成语音消息体
     *
     * @param msgType
     * @param content
     * @param fromUser
     * @param toUser
     * @param strVoiceTime
     * @return
     */
    public static String toVoiceJson(String msgType, String content, String fromUser, String toUser, String strVoiceTime) {
        return "{\"msgId\":" + System.currentTimeMillis() +
                ",\"msgType\":\"" + msgType +
                "\",\"createTime\":" + System.currentTimeMillis() +
                ",\"content\":\"" + content +
                "\",\"fromUser\":\"" + fromUser +
                "\",\"toUser\":\"" + toUser +
                "\",\"strVoiceTime\":\"" + strVoiceTime + "\"}";
    }

    /**
     * 从JID中获取用户名
     *
     * @param jid
     * @return
     */
    public static String getNameFromJid(String jid) {
        return jid.split("@")[0];
    }

    /**
     * 去除用户名后面的"/Spark"、"/Smack"、"/iPhone"
     *
     * @param from
     * @return
     */
    public static String getJid(String from) {
        if (from.contains("/")) {
            return from.split("/")[0];
        }
        return from;
    }


    public static String geTimeNoS(long time) {
        SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
        String times = df.format(time);
        return times;
    }



    /**
     * 当收到群聊消息，调用此方法获取该消息发自谁
     *
     * @param msgFrom
     * @return
     */
    public static String getFromJid(String msgFrom) {
        return msgFrom.split("/")[1];
    }

    /**
     * 录音时 检查应用当前是否申请到录音权限
     *
     * @param context
     * @return
     */
    public static boolean isRecordPermission(Context context) {
        PackageManager manager = context.getPackageManager();
        if (manager.checkPermission("android.permission.RECORD_AUDIO", "com.zs.chat") == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 得到软件显示版本信息
     *
     * @param context 上下文
     * @return 当前版本信息
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            String packageName = context.getPackageName();
            verName = context.getPackageManager().getPackageInfo(packageName, 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    //获得jid  就是账户
    public static String getUserFromJid(String jid) {
        if (TextUtils.isEmpty(jid)) {
            return "";
        }
        int lastIndex = jid.lastIndexOf("/");
        return jid.substring(0, lastIndex);
    }

}

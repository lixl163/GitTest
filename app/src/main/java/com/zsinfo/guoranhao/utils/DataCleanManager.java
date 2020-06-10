package com.zsinfo.guoranhao.utils;

import android.content.Context;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

public class DataCleanManager {
    private static DataOutputStream dos = null;
    private static Process p = null;

    /**
     * 保留函数
     * 运行root权限下的命令
     * @param cmd 命令
     */
    private static void runRootCmd(String cmd){
        try {
            p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.close();
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(dos != null){
                try{
                    dos.close();
                }catch (IOException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * 删除输入参数目录下的文件
     * @param directory 文件目录
     */
    private static void deleteFileByDirectory(File directory){
        if(directory != null && directory.exists() && directory.isDirectory()){
            for(File file : directory.listFiles() )
                file.delete();
        }
    }

    /**
     * 删除目录下的指定文件名的文件
     * @param directory 文件目录
     * @param filename 文件名
     */
    private static void deleteFileByDirectory(File directory, String filename){
        if(directory != null && directory.exists() && directory.isDirectory()){
            for(File file : directory.listFiles()){
                if(file.toString().equals(filename)) {
                    file.delete();
                    Log.e("lixl", "DataCleanManager 删除成功");
                }
            }
        }
    }

    /**
     * 删除某目录下所有文件夹及文件
     * @param dir
     */
    private static void deleteAllFiles(File dir) {
        File files[] = dir.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) { // 判断是否为文件夹
                    deleteAllFiles(f);
                    try {
                        f.delete();
                    } catch (Exception e) {
                    }
                } else {
                    if (f.exists()) { // 判断是否存在
                        deleteAllFiles(f);
                        try {
                            f.delete();
                        } catch (Exception e) {
                        }
                    }
                }
            }
    }

    /**
     * 清除缓存文件
     * @param context
     */
    private static void cleanInternalCache(Context context){
        deleteFileByDirectory(context.getCacheDir());
    }

    /**
     * 清除database文件夹下的webview.db和 webviewCache.db
     * @param context
     */
    private static void cleanDatabases(Context context){
        String dataBasesFilePath = "/data/data/" + context.getPackageName() + "/database";
        deleteFileByDirectory(new File(dataBasesFilePath), "webview.db");
        deleteFileByDirectory(new File(dataBasesFilePath), "webviewCache.db");
    }

    /**
     * 清除webView相关的缓存/data/data/com.zsinfo.guoranhao/cache/org.chromium.android_webview
     * @param context
     */
    public static void cleanCache(Context context){
        String dataBasesFilePath = "/data/data/" + context.getPackageName() + "/cache";
        deleteAllFiles(new File(dataBasesFilePath+"/org.chromium.android_webview"));
    }

    /**
     * 清除webView相关的缓存/data/data/com.zsinfo.guoranhao/app_webview
     * @param context
     */
    public static void cleanAppWebview(Context context){
        //todo app_webview：需要慎重考虑，里面有Cookies和Web Data，小心账户信息清空(需要重新登录)
        String dataBasesFilePath = "/data/data/" + context.getPackageName() + "/app_webview";
        deleteAllFiles(new File(dataBasesFilePath));
    }

}

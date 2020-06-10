package com.zsinfo.guoranhao.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.zsinfo.guoranhao.activitys.MainApplication;

/**
 *
 */
public class SharedPreferencesUtil {
    private static SharedPreferences sharedPreferences = MainApplication.context.getSharedPreferences("com.zsinfo.guoranhao", Context.MODE_PRIVATE);
    private static SharedPreferences.Editor editor = sharedPreferences.edit();

    public static boolean getIsLogin() {
        return sharedPreferences.getBoolean("IsLogin", false);
    }


    public static void setIsLogin(Boolean IsLogin) {
        editor.putBoolean("IsLogin", IsLogin).commit();
    }

    public static String getWebsiteNode() {
        return sharedPreferences.getString("websiteNode", "3301");
    }

    public static void setWebsiteNode(String websiteNode) {
        editor.putString("websiteNode", websiteNode).commit();
    }

    public static String getwebsiteName() {
        return sharedPreferences.getString("websiteName", "杭州站");
    }

    public static void setWebsiteName(String websiteName) {
        editor.putString("websiteName", websiteName).commit();
    }

    public static String getfirmName() {
        return sharedPreferences.getString("firmName", "");
    }

    public static void setfirmName(String firmName) {
        editor.putString("firmName", firmName).commit();
    }

    public static String getfirmId() {
        return sharedPreferences.getString("firmId", "");
    }

    public static void setfirmId(String firmId) {
        editor.putString("firmId", firmId).commit();
    }

    //存存js回调
    public static String getJsCallBack() {
        return sharedPreferences.getString("jsCallBack", "");
    }

    public static void setJsCallBack(String jsCallBack) {
        editor.putString("jsCallBack", jsCallBack).commit();
    }


    //给js提供存储方法
    public static void SetJSMethod(String Key, String GetJSMethod) {
        editor.putString(Key, GetJSMethod).commit();
    }

    public static String GetJSMethod(String key) {
        return sharedPreferences.getString(key, "");
    }

    public static void ClearJSMethod(String key) {

        editor.remove(key).commit();
    }

    public static String getuserId() {
        return sharedPreferences.getString("userId", "");
    }

    public static void setuserId(String userId) {
        editor.putString("userId", userId).commit();
    }

    public static String getuserGrade() {
        return sharedPreferences.getString("userGrade", "");
    }

    public static void setuserGrade(String userGrade) {
        editor.putString("userGrade", userGrade).commit();
    }


    public static String getFaceImg() {
        return sharedPreferences.getString("FaceImg", "");
    }

    public static void setFaceImg(String userGrade) {
        editor.putString("FaceImg", userGrade).commit();
    }

    public static String getApp_share() {
        return sharedPreferences.getString("App_share", "");
    }

    public static void setApp_share(String App_share) {
        editor.putString("App_share", App_share).commit();
    }

    public static String getAndroid_download_uri() {
        return sharedPreferences.getString("Android_download_uri", "");
    }

    public static void setAndroid_download_uri(String Android_download_uri) {
        editor.putString("Android_download_uri", Android_download_uri).commit();
    }

    public static String getWebsideData() {
        return sharedPreferences.getString("websiteNodeDtta" + SharedPreferencesUtil.getWebsiteNode(), "");
    }

    public static void setWebsideData(String websiteNodeDtta) {
        editor.putString("websiteNodeDtta" + SharedPreferencesUtil.getWebsiteNode(), websiteNodeDtta).commit();
    }

    public static boolean getFirstStart() {
        return sharedPreferences.getBoolean("FirstStart", true);
    }

    public static void setFirstStart() {
        editor.putBoolean("FirstStart", false).commit();
    }


    public static String getdispatch_time() {
        return sharedPreferences.getString("dispatch_time", "");
    }

    public static void setdispatch_time(String userGrade) {
        editor.putString("dispatch_time", userGrade).commit();
    }

    public static String getHow_much_money_dispatch() {
        return sharedPreferences.getString("How_much_money_dispatch", "");
    }

    public static void setHow_much_money_dispatch(String userGrade) {
        editor.putString("How_much_money_dispatch", userGrade).commit();
    }

    public static String getPARTNER() {
        return sharedPreferences.getString("PARTNER", "");
    }

    public static void setPARTNER(String userGrade) {
        editor.putString("PARTNER", userGrade).commit();
    }

    public static String getSELLER() {
        return sharedPreferences.getString("SELLER", "");
    }

    public static void setSELLER(String userGrade) {
        editor.putString("SELLER", userGrade).commit();
    }

    public static String getCreate_order_end_date() {
        return sharedPreferences.getString("Create_order_end_date", "");
    }

    public static void setCreate_order_end_date(String userGrade) {
        editor.putString("Create_order_end_date", userGrade).commit();
    }

    public static String getRSA_PRIVATE() {
        return sharedPreferences.getString("RSA_PRIVATE", "");
    }

    public static void setRSA_PRIVATE(String userGrade) {
        editor.putString("RSA_PRIVATE", userGrade).commit();
    }

    public static String getAPP_ID() {
        return sharedPreferences.getString("ALIP_APP_ID", "");
    }

    public static void setAPP_ID(String apid) {
        editor.putString("ALIP_APP_ID", apid).commit();
    }

    public static String getNOTIFY_URL() {
        return sharedPreferences.getString("NOTIFY_URL", "");
    }

    public static void setNOTIFY_URL(String userGrade) {
        editor.putString("NOTIFY_URL", userGrade).commit();
    }


    public static String getscore() {
        return sharedPreferences.getString("score", "");
    }

    public static void setscore(String score) {
        editor.putString("score", score).commit();
    }

    public static String getNumber() {
        return sharedPreferences.getString("number", "");
    }

    public static void setNumber(String number) {
        editor.putString("number", number).commit();
    }

    public static String getTokenId() {
        return sharedPreferences.getString("tokenId", "");
    }

    public static void setTokenId(String tokenId) {
        editor.putString("tokenId", tokenId).commit();

    }

    public static String getSecretKey() {
        return sharedPreferences.getString("SecretKey", "");
    }

    public static void setSecretKey(String SecretKey) {
        editor.putString("SecretKey", SecretKey).commit();

    }

    public static String getPassword() {
        return sharedPreferences.getString("password", "");
    }

    public static void setPassword(String password) {
        editor.putString("password", password).commit();
    }


    public static boolean getIsLoca() {
        return sharedPreferences.getBoolean("Isloca", false);
    }

    public static void setIsLoca(Boolean isLoca) {
        editor.putBoolean("Isloca", isLoca).commit();
    }


    /**
     * 保存splash 图片的版本
     *
     * @param actionPageNote
     */
    public static void setActionPageNote(String actionPageNote) {
        editor.putString("actionPageNote", actionPageNote).commit();
    }

    /**
     * 获取splash 图片的版本,与服务器请求到的数据进行对比，一样则不更新
     *
     * @return
     */
    public static String getActionPageNote() {
        return sharedPreferences.getString("actionPageNote", "");
    }


    public static void clearData() {
        editor.remove("tokenId").commit();
        editor.remove("number").commit();
        editor.remove("score").commit();
        editor.remove("userGrade").commit();
        editor.remove("firmName").commit();
        editor.remove("firmId").commit();
        editor.remove("firmId").commit();
        editor.remove("userId").commit();
        editor.remove("websiteNode").commit();
        editor.remove("websiteName").commit();
        editor.remove("Isloca").commit();
    }


    /**
     * 清除保存的所有数据
     *
     * @return
     */
    public static boolean clearAllData() {
        return editor.clear().commit();
    }

//    /**
//     * 保存登录状态
//     * @param isLogin
//     */
//    public static void setIsLogin(boolean isLogin){
//        editor.putBoolean("isLogin",isLogin);
//    }
//
//    public static boolean getIsLogin(){
//
//        return  sharedPreferences.getBoolean("isLogin",false);
//    }

    /**
     * 保存账户信息
     *
     * @param userAccount
     */
    public static void setUserAccount(String userAccount) {
        editor.putString("userAccount", userAccount).commit();
    }

    public static String getUserAccount() {
        return sharedPreferences.getString("userAccount", "");
    }


    /**
     * 保存app分享信息
     *
     * @param msg
     */
    public static void setAppShareMsg(String msg) {
        editor.putString("appShareMsg", msg).commit();
    }

    public static String getAppShareMsg() {
        return sharedPreferences.getString("appShareMsg", "");
    }

    /**
     * 保存app下载地址
     *
     * @param url
     */
    public static void setAppDownload_android(String url) {

        editor.putString("appDownloadUrl", url).commit();
    }

    public static String getAppDownload_android() {
        String s = sharedPreferences.getString("appDownloadUrl", "");
        return s;
    }

    public static void setShopCarNum(int num) {
        editor.putInt("shopCarNum", num).commit();
    }

    public static int getShopCarNum() {
        return sharedPreferences.getInt("shopCarNum", 0);
    }

}

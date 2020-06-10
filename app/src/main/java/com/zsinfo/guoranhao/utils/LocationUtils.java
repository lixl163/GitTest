package com.zsinfo.guoranhao.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.zsinfo.guoranhao.activitys.MainApplication;
import com.zsinfo.guoranhao.widget.AlertDialog;

/**
 * Created by admin on 2017/10/11.
 */

public class LocationUtils {

    //声明mlocationClient对象
    private static AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    private static AMapLocationClientOption mLocationOption = null;
    //是否初始化过
    private static boolean isInited = false ;
    public static void startLocation(WebView webView, Activity context){
        if (!isInited){
            init(webView,context);
        }
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
        //启动定位
        mlocationClient.startLocation();
    }

    public static void stopLocation(){
        mlocationClient.stopLocation();
        isInited = false;
    }

    private static void init(WebView webView,Activity context){
        mlocationClient = new AMapLocationClient(MainApplication.context);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位监听
        mlocationClient.setLocationListener( new MyLocationListener(webView,context));
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
        isInited = true ;
    }

    public static class MyLocationListener implements AMapLocationListener{
        AlertDialog alertDialog = null;
        private WebView mWebView ;
        private Activity context;

        public MyLocationListener(WebView webView,Activity context) {
            mWebView = webView;
            this.context = context;
        }

        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    amapLocation.getLatitude();//获取纬度
                    amapLocation.getLongitude();//获取经度
                    amapLocation.getAccuracy();//获取精度信息
                    Log.e("定位信息哟",""+amapLocation.toString());
                    stopLocation();
                    /*
                        {"province":"浙江省","longitude":"120.180638","street":"建业路",
                        "AOIName":"杭州华业高科技产业园","latitude":"30.188515",
                        "formattedAddress":"浙江省杭州市滨江区建业路靠近杭州华业高科技产业园",
                        "city":"杭州市","citycode":"0571","district":"滨江区",
                        "adcode":"330108","number":"511号","country":"中国",
                        "POIName":"杭州华业高科技产业园"}
                     */
                    String province = amapLocation.getProvince();
                    double longitude = amapLocation.getLongitude();
                    double latitude = amapLocation.getLatitude();
                    String street = amapLocation.getStreet();
                    String AOIName = amapLocation.getAoiName();
                    String formattedAddress = amapLocation.getAddress();
                    String city = amapLocation.getCity();
                    String citycode = amapLocation.getCityCode();
                    String district = amapLocation.getDistrict();
                    String adcode = amapLocation.getAdCode();
                    String number = amapLocation.getStreetNum();
                    String country = amapLocation.getCountry();
                    String POIName = amapLocation.getPoiName();
                    String json ="{\"province\":\""+province+"\",\"longitude\":\""+longitude+"\",\"street\":\""+street+"\",\n" +
                            "                        \"AOIName\":\""+AOIName+"\",\"latitude\":\""+latitude+"\",\n" +
                            "                        \"formattedAddress\":\""+formattedAddress+"\",\n" +
                            "                        \"city\":\""+city+"\",\"citycode\":\""+citycode+"\",\"district\":\""+district+"\",\n" +
                            "                        \"adcode\":\""+adcode+"\",\"number\":\""+number+"\",\"country\":\""+country+"\",\n" +
                            "                        \"POIName\":\""+POIName+"\"}";

                    mWebView.loadUrl("javascript:setLocation('"+json+"')");
                }else if (amapLocation.getErrorCode() == 12){ // 缺少定位权限
//                    AlertDialog alertDialog = new AlertDialog.Builder(context)
//                            .setTitle("定位权限申请")
//                            .setMessage("定位权限已经关闭，请到设置界面开启")
//                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog .cancel();
//                                }
//                            })
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    Uri packageURI = Uri.parse("package:" + "com.zsinfo.guoranhao");
//                                    Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
//                                    context.startActivity(intent);
//                                    dialog.cancel();
//                                }
//                            }).create();
//                    alertDialog.show();
                    stopLocation();
                    String json = "{}";
                    mWebView.loadUrl("javascript:setLocation('"+json+"')");
                    alertDialog =new AlertDialog(context).builder()
                            .setCancelable(false)
                            .setTitle("定位权限申请")
                            .setMsg("定位权限已经关闭，请到设置界面开启")
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri packageURI = Uri.parse("package:" + "com.zsinfo.guoranhao");
                                    Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
                                    context.startActivity(intent);
                                    SharedPreferencesUtil.setIsLoca(true);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                        SharedPreferencesUtil.setIsLoca(false);
                                }
                            });

                    alertDialog.show();
                }else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError","location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                    String json = "{}";
                    mWebView.loadUrl("javascript:setLocation('"+json+"')");
                    stopLocation();
                }
            }
        }
    }

}

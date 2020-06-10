package com.zsinfo.guoranhao.chat.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

public class NetUtil {
	/**
	 * 判断是否有网络
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetConnected(Context context) {
		  ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);   
	        if (cm !=null) {   
	        	//如果仅仅是用来判断网络连接
	        	//则可以使用 cm.getActiveNetworkInfo().isAvailable();  
	            NetworkInfo[] info = cm.getAllNetworkInfo();   
	            if (info != null) {   
	                for (int i = 0; i < info.length; i++) {   
	                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {   
	                        return true;   
	                    }   
	                }   
	            }   
	        }   
	        return false;   
	}
	
	/**
	 * 检测网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetWorkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}

		return false;
	}
	
	/**
	 * 判断WIFI是否为打开状态
	 *
	 * @param context
	 * @return
	 */
	public static boolean isWifiOpen(Context context) {
		//获取系统服务
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		//获取状态
		NetworkInfo.State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		//判断wifi已连接的条件
		if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) {
			return true;
		}
		return false;
	}

	/**
	 * WIFI网络开关
	 *
	 * @param context
	 * @param enabled true-->open   false -->close
	 */
	public static boolean openWifi(Context context, boolean enabled) {
		WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return wm.setWifiEnabled(enabled);
	}

}

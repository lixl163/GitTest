package com.zsinfo.guoranhao.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.activitys.MainApplication;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */

public class NetMessageUtil {

    public static RequestQueue requestQueue;
    public static String result;
    CallBack callBack;
    private static TextView tipTextView;
    public static boolean isLast = false;



    public static RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            return Volley.newRequestQueue(MainApplication.context);
        } else {
            return requestQueue;
        }
    }

    public static void getGetString(String url, final CallBack callBack) {
        getRequestQueue().add(new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                callBack.send(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callBack.send(null);
            }
        }));
    }

    public static void getPostString(String url, final Map<String, String> hashMap, final CallBack callBack) {
        getRequestQueue().add(new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                callBack.send(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                try {
                    if (volleyError.networkResponse.statusCode == 404) {
                        ToastUtil.showToast(MainApplication.context,"服务器异常");
                    } else if (volleyError.networkResponse.statusCode == 500) {
                        ToastUtil.showToast(MainApplication.context,"服务器异常");
                    } else {
                        ToastUtil.showToast(MainApplication.context,"链接超时，请重试");
                    }
                } catch (Exception e) {
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return hashMap;
            }
        }).setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }


    public static void getPostStringWithLoading(Context context, String msg, String url, final Map<String, String> hashMap, final CallBack callBack) {
        final Dialog dialog = createLoadingDialog(context, msg);
        dialog.show();//显示Dialog
        getRequestQueue().add(new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                callBack.send(s);
                dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                dialog.dismiss();
                try {
                    if (volleyError.networkResponse.statusCode == 404) {
                        ToastUtil.showToast(MainApplication.context,"服务器异常");
                    } else if (volleyError.networkResponse.statusCode == 500) {
                        ToastUtil.showToast(MainApplication.context,"服务器异常");
                    } else {
                        ToastUtil.showToast(MainApplication.context,"链接超时，请重试");
                    }
                } catch (Exception e) {
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return hashMap;
            }
        }).setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
    }





    public interface CallBack {
        public void send(String result);
    }

    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
    public static Dialog createLoadingDialog(Context context, String msg) {
        LinearLayout layout = null;
        if (layout == null) {
            layout = inflaterView(context, msg);
        }
        tipTextView.setText(msg);
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCanceledOnTouchOutside(false);

        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT));// 设置布局
        return loadingDialog;

    }

    /**
     * 填充Dialog布局
     *
     * @return
     */
    private static LinearLayout inflaterView(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loading_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息
        return layout;
    }

    /**
     * 检查图片是否需要更新
     * //http://testapp.guoss.cn/gssapi/server/api.do?method=action_page_android
     */
    public static void checkStartImageUpdate(final CheckImageCallBack callBack){
        final HashMap<String ,String> map=new HashMap<>();
        map.put("method","action_page_ads");
        Log.e("StartActivity","websiteNode==="+SharedPreferencesUtil.getWebsiteNode());
        if(!TextUtils.isEmpty(SharedPreferencesUtil.getWebsiteNode())){

            map.put("websiteNode",SharedPreferencesUtil.getWebsiteNode());
        }
//        if (!TextUtils.isEmpty(SharedPreferencesUtil.getActionPageNote())){
//            map.put("actionPageNote",SharedPreferencesUtil.getActionPageNote());
//        }
//        getPostString(UrlUtils.SERVE, map,callBack);

        getRequestQueue().add(new StringRequest(Request.Method.POST, UrlUtils.SERVE, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                callBack.success(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callBack.failed(volleyError);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return map;
            }
        }).setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

    }
    /**
     * 获取启动页的图片
     */
    public static void getStartImage(Activity activity, String url, final ImageCallBack imageCallBack){
        Display defaultDisplay = activity.getWindowManager().getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        ImageRequest imageRequest = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageCallBack.onSuccess(bitmap);
                    }
                }, width, height, Bitmap.Config.ARGB_8888, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                e.printStackTrace();
                imageCallBack.onFailed(e);
            }
        });

        getRequestQueue().add(imageRequest).setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));;
    }

    public interface  ImageCallBack{
        void onSuccess(Bitmap bitmap);
        void onFailed(VolleyError e);
    }


    public interface CheckImageCallBack{
        void success(String s);
        void failed(VolleyError e);
    }



}

package com.zsinfo.guoranhao.activitys;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.imagepicker.ImagePicker;
import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.event.EventInterface;
import com.zsinfo.guoranhao.event.EventWebview;
import com.zsinfo.guoranhao.fragment.CommonsFragment;
import com.zsinfo.guoranhao.js_interact.SettingInfoUtils;
import com.zsinfo.guoranhao.utils.Constant;
import com.zsinfo.guoranhao.utils.MyWebChromeClient;
import com.zsinfo.guoranhao.utils.SPUtils;
import com.zsinfo.guoranhao.utils.SharedPreferencesUtil;
import com.zsinfo.guoranhao.utils.UrlUtils;

/**
 * Created by admin on 2017/8/29.
 */

public class WebviewActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout rl_back;
    private TextView title_text;
    private ImageView title_img;
    private TextView tv_title;
    private LinearLayout ll_box;
    private String callback_str = "";
    //用来标志返回是否需要刷新上一个界面
    private boolean isNeedRefresh = false;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @Override
    protected int getContentContainerId() {
        return R.id.contentContainer;
    }

    @Override
    protected void initFragment() {
        fragment = CommonsFragment.newInstance(url);
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!SharedPreferencesUtil.getJsCallBack().isEmpty() && fragment != null) {
            fragment.getmWebView().loadUrl("javascript:" + SharedPreferencesUtil.getJsCallBack());
            SharedPreferencesUtil.setJsCallBack("");
        }
    }

    @Override
    protected void onMsgEventReceive(EventInterface minterface) {
        //2018/05/09  需要将支付宝支付的结果 告诉h5 然后改变网页
        if (minterface instanceof EventWebview) {
            String alipayStatus = ((EventWebview) minterface).getAlipayStatus();
            int type = ((EventWebview) minterface).getType();
            Log.e("支付宝支付-----状态", alipayStatus);
            if (type == 1) {
                fragment.getmWebView().loadUrl("javascript:wxPayResult(" + alipayStatus + ")");
            } else {
                fragment.getmWebView().loadUrl("javascript:aliPayResult(" + alipayStatus + ")");
            }
        } else {
            Log.e("Conker", minterface + "");
        }


    }

    @Override
    protected void initView() {
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        tv_title = (TextView) findViewById(R.id.tv_header_title);
        rl_back.setOnClickListener(this);
        title_text = (TextView) findViewById(R.id.title_text);
        title_img = (ImageView) findViewById(R.id.title_img);
        ll_box = (LinearLayout) findViewById(R.id.ll_box);
    }

    @Override
    protected void initOthers() {
        Intent intent = getIntent();
        //获取url链接
        url = intent.getStringExtra(Constant.NEXT_PAGE_URL);
        //获取标题tiyle
        String title = intent.getStringExtra(Constant.NEXT_PAGE_TITLE_NAME);
        tv_title.setText(title);
        //获取标题右侧的文字txt
        String rightText = intent.getStringExtra(Constant.NEXT_PAGE_TITLE_TEXT);
        //获取标题右侧的icon
        String rightIcon = intent.getStringExtra(Constant.NEXT_PAGE_TITLE_IMG);
        //分别判断标题右侧是显示icon还是txt
        if (rightText != null || rightIcon != null){
            ll_box.setVisibility(View.VISIBLE);
            callback_str = intent.getStringExtra(Constant.NEXT_PAGE_TITLE_CALLBACK);
            if (!rightText.isEmpty()) {
                title_text.setVisibility(View.VISIBLE);
                title_text.setText(rightText);
                //需要根据主题变色
                String theme_type = SPUtils.getValue(Constant.THEME_TYPE);
                if (!TextUtils.isEmpty(theme_type)){
                    switch (theme_type){
                        case "1":
                            title_text.setTextColor(getResources().getColor(R.color.green_back));
                            break;
                        case "2":
                            title_text.setTextColor(getResources().getColor(R.color.green_back1));
                            break;
                        case "3":
                            title_text.setTextColor(getResources().getColor(R.color.green_back2));
                            break;
                        case "4":
                            title_text.setTextColor(getResources().getColor(R.color.green_back3));
                            break;
                    }
                }

            } else if (!rightIcon.isEmpty()) {
                title_img.setVisibility(View.VISIBLE);
                Glide.with(this).load(rightIcon).into(title_img);
            }

            ll_box.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!callback_str.isEmpty()) {
                        fragment.getmWebView().loadUrl("javascript:" + callback_str);
                    }
                }
            });
        }

        //是否需要刷新
        isNeedRefresh = intent.getBooleanExtra(Constant.IS_NEED_REFRESH, false);

    }

    @Override
    protected String getLoadUrl() {
        return url;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.rl_back:
                //将registerOnshow()方法本地存储的数据给清除
                SharedPreferencesUtil.SetJSMethod(url, "");  //2019.8.13清空数据 url = /html/goodsDetails.html?goodsId=45

                //首先判断当前页面是否是下订单完成界面,当前url是否包含
                if (url.contains(UrlUtils.ORDER_PAY_PAGE)) {
                    //判断是否是从订单管理界面点进来的
                    int existPageByUrl = MainApplication.isExistPageByUrl(UrlUtils.ORDER_MANAGE_PAGE);
                    if (existPageByUrl == -1) {//不存在
                        Intent intent = new Intent(this, WebviewActivity.class);
                        intent.putExtra(Constant.NEXT_PAGE_TITLE_NAME, "订单管理");
                        intent.putExtra(Constant.NEXT_PAGE_URL, UrlUtils.ORDER_MANAGE_PAGE);
                        startActivity(intent);
                    } else {//存在
                        finish();
                    }
                } else if (url.contains(UrlUtils.ORDER_PRE_PAGE)) { //当前为预购订金支付 ，返回预购订单管理
                    //{'hierarchy':1,'reload':1,'url':'url'}
                    //判断是否从我的预购点进来的
                    int existPageByUrl = MainApplication.isExistPageByUrl(UrlUtils.PRE_ORDER_MANAGE_PAGE);
                    if (existPageByUrl == -1) {//不存在
                        Intent intent = new Intent(this, WebviewActivity.class);
                        intent.putExtra(Constant.NEXT_PAGE_TITLE_NAME, "我的预购");
                        intent.putExtra(Constant.NEXT_PAGE_URL, UrlUtils.PRE_ORDER_MANAGE_PAGE);
                        startActivity(intent);
                    } else {//存在
                        finish();
                    }
                } else if (url.contains(UrlUtils.ORDER_MANAGE_PAGE)) { //订单管理返回时 不刷新 我的页面
                    //{'hierarchy':1,'reload':1,'url':'url'}
                    String json = "{'hierarchy':1,'reload':0,'url':'html/my.html'}";
                    SettingInfoUtils.goBack(json, this);
                } else if (url.contains(UrlUtils.PRE_ORDER_MANAGE_PAGE)) { //预订单管理 不刷新 我的界面
                    String json = "{'hierarchy':1,'reload':0,'url':'html/my.html'}";
                    SettingInfoUtils.goBack(json, this);
                } else if (url.contains(UrlUtils.ONLINE_RECHARGE_PAGE)) {//充值页面 返回上一级时需要刷新
                    String json = "";
                    if (isNeedRefresh) { //这里只关心层级 不关心url
                        json = "{'hierarchy':1,'reload':1,'url':'html/my.html'}";
                    } else {
                        json = "{'hierarchy':1,'reload':0,'url':'html/my.html'}";
                    }

                    finish();
                } else {
                    setResultData();
                    finish();
                }
                break;
        }
    }

    public void setResultData() {
        Intent intent = getIntent();
        intent.putExtra(Constant.IS_NEED_REFRESH, isNeedRefresh);
        //放入从哪个页面返回的
        intent.putExtra(Constant.BACK_URL, url);
        setResult(Constant.RESULTCODE, intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == ImagePicker.RESULT_CODE_ITEMS || requestCode == 200 || resultCode == Activity.RESULT_CANCELED) {//头像上传
            MyWebChromeClient myWebChromeClient = fragment.getMyWebChromeClient();
            myWebChromeClient.onActivityResult(requestCode, resultCode, data);
        }

        if (requestCode == Constant.REQUESTCODE && resultCode == Constant.RESULTCODE) {
            String back_url = data.getStringExtra(Constant.BACK_URL);
            boolean isRefresh = data.getBooleanExtra(Constant.IS_NEED_REFRESH, false);
            if (isRefresh) {//需要刷新

                if (MainApplication.map.containsKey(back_url)) {//是否存在这个键值
                    //CP刷新 从指定页面回到指定的页面
                    String s = MainApplication.map.get(back_url);
                    if (url != null && s != null) {
                        if (url.endsWith(s)) {//是这一对cp
                            reload();
                        }
                    }
                } else {
                    //非cp刷新，某个页面的上个页面都需要刷新
                    reload();
                }


            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setNeedRefresh(boolean needRefresh) {
        isNeedRefresh = needRefresh;
    }

    public boolean getNeedRefresh() {
        return isNeedRefresh;
    }

}

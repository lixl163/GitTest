package com.zsinfo.guoranhao.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.activitys.WebviewActivity;
import com.zsinfo.guoranhao.event.EventWebview;
import com.zsinfo.guoranhao.utils.Constant;
import com.zsinfo.guoranhao.utils.ToastUtil;
import com.zsinfo.guoranhao.utils.UrlUtils;

import org.greenrobot.eventbus.EventBus;

import static com.zsinfo.guoranhao.activitys.MainApplication.flag;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);

        api = WXAPIFactory.createWXAPI(this, Constant.WXPAY_APPID);
//		api= MainApplication.getWXAPI();
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e("WXPay", "onPayFinish, errCode = " + resp.errCode);
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//			AlertDialog.Builder builder = new AlertDialog.Builder(this);
//			builder.setTitle("提示");
//			builder.setMessage("微信支付结果：%s"+String.valueOf(resp.errCode));
//			//builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//			builder.show();

            if (0 == resp.errCode) {
                ToastUtil.showToast(this, "支付成功");

                    paySuccess(flag, this);
                    if ("3".equals(flag)) {
                        EventBus.getDefault().post(new EventWebview("9000", 1));
                    }


            } else if (-1 == resp.errCode) {
                ToastUtil.showToast(this, "支付失败,请重试" + resp.errCode);
                if ("3".equals(flag)) {
                    EventBus.getDefault().post(new EventWebview("7000", 1));
                }
            } else if (-2 == resp.errCode) {
                ToastUtil.showToast(this, "取消支付");
            }

            finish();
        }
    }

    /**
     * 支付成功后调用此方法
     *
     * @param flag 区分跳转到哪个界面
     */
    public static void paySuccess(String flag, Context context) {
        if (null == flag) {
            return;
        }
        String resultUrl = "";
        String titleName = "";
        boolean isRefresh = false;
        if ("1".equals(flag)) { //跳转到普通订单
//			resultUrl= "html/order_management.html";
            resultUrl = UrlUtils.ORDER_MANAGE_PAGE;
            titleName = "订单管理";
            toNextActivity(titleName, resultUrl, context, isRefresh);
            return;
        } else if ("2".equals(flag)) {//跳转到预支付
//			resultUrl="html/PreOrder_management.html";
            resultUrl = UrlUtils.PRE_ORDER_MANAGE_PAGE;
            titleName = "预支付";
            toNextActivity(titleName, resultUrl, context, isRefresh);
            return;
        } else if ("3".equals(flag)) {//跳转到充值
//			resultUrl="html/month_recharge.html?search=recharge";
//			resultUrl=UrlUtils.ONLINE_RECHARGE_PAGE;
//			titleName="在线充值";
//			isRefresh=true;
//			toNextActivity(titleName,resultUrl,context,isRefresh);

            return;
        } else {
            return;
        }
    }

    public static void toNextActivity(String titleName, String url, Context context, boolean isRefresh) {

        Intent intent = new Intent(context, WebviewActivity.class);
        intent.putExtra(Constant.NEXT_PAGE_TITLE_NAME, titleName);
        intent.putExtra(Constant.NEXT_PAGE_URL, url);
        intent.putExtra(Constant.IS_NEED_REFRESH, isRefresh);
        context.startActivity(intent);

    }
}
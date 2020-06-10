package com.zsinfo.guoranhao.activitys;

import android.Manifest;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.utils.Log;
import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.event.EventInterface;
import com.zsinfo.guoranhao.fragment.CommonsFragment;
import com.zsinfo.guoranhao.utils.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class QRActivity extends BaseActivity implements EasyPermissions.PermissionCallbacks, QRCodeView.Delegate {
    private static final String TAG = "ActionsQRScanActivity";
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private long clickTime = 0; //记录第一次点击的时间

    private FrameLayout mContentContainer;
    private ZXingView mQRCodeView;
    private TextView tv_title;
    private RelativeLayout rl_back;


    @Override
    protected void onMsgEventReceive(EventInterface minterface) {

    }

    @Override
    protected void initView() {
        mQRCodeView = (ZXingView) this.findViewById(R.id.zxingview);
        tv_title = (TextView) findViewById(R.id.tv_header_title);
        mContentContainer = (FrameLayout) findViewById(R.id.contentContainer);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mQRCodeView.setDelegate(this);
    }


    @Override
    protected void initOthers() {

        Intent intent = getIntent();
        String title = intent.getStringExtra(Constant.NEXT_PAGE_TITLE_NAME);
        tv_title.setText(title);
        url = intent.getStringExtra(Constant.NEXT_PAGE_URL);
    }

    @Override
    protected String getLoadUrl() {
        return url;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_qr;
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
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }


    @Override
    protected void onStart() {
        super.onStart();
        requestCodeQRCodePermissions();
        mQRCodeView.startCamera();
        mQRCodeView.startSpot();
    }

    @Override
    protected void onResume() {
        mQRCodeView.showScanRect();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {

        Log.e("Conker", result);

        if (!result.isEmpty()) {

            try {
                JSONObject jsonobject = new JSONObject(result);
                if (jsonobject.getString("NAME").equals("PUP")) {
                    mQRCodeView.stopCamera();
                    GetResult(result);

                } else {
                    Toast.makeText(getApplicationContext(), "二维码识别失败！",
                            Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "二维码识别失败！",
                        Toast.LENGTH_SHORT).show();
            }
        }

        mQRCodeView.startSpotAndShowRect();

    }

    private void GetResult(String result) {

        if (!this.isFinishing()) {

            fragment.runJsMethod("javascript:pub.scan.getScan('" + result + "')");
            mQRCodeView.setVisibility(View.GONE);
            mContentContainer.setVisibility(View.VISIBLE);
            mQRCodeView.onDestroy();

        }

    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "open camera error!");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "权限",
                    REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {

        ActivityFinish();

    }


}

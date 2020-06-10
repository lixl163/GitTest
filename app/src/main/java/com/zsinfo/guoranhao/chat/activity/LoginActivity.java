//package com.zsinfo.guoranhao.chat.activity;
//
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//
//import com.zsinfo.guoranhao.R;
//import com.zsinfo.guoranhao.chat.base.BaseActivity;
//import com.zsinfo.guoranhao.chat.utils.NetUtil;
//import com.zsinfo.guoranhao.chat.utils.SPUtils;
//import com.zsinfo.guoranhao.chat.utils.ToastUtils;
//import com.zsinfo.guoranhao.chat.xmpp.XmppConnection;
//
//import org.jivesoftware.smack.Roster;
//import org.jivesoftware.smack.RosterEntry;
//import org.jivesoftware.smack.packet.Presence;
//
//import java.util.Collection;
//
//import static com.zsinfo.guoranhao.chat.utils.SPUtils.autoLogin;
//
///**
// * Created by admin on 2018/7/4.
// *
// * 登录界面
// */
//public class LoginActivity extends BaseActivity {
//
//    private EditText et_login_user, et_login_pass;
//    private CheckBox cb_login_remember, cb_login_auto;
//    private Button btn_login;
//    private String userStr;
//    private String passStr;
//
//    @Override
//    public View setCreateView() {
//        View view = View.inflate(this, R.layout.activity_login, null);
//        return view;
//    }
//
//    @Override
//    public void initView() {
//        //标题栏部分，只显示中间的标题
//        isShowBackIcon(false);
//        isShowRight1(false);
//        isShowRight2(false);
//        setMiddleText("登录");
//
//        et_login_user = (EditText) findViewById(R.id.et_login_user);
//        et_login_pass = (EditText) findViewById(R.id.et_login_pass);
//        cb_login_remember = (CheckBox) findViewById(R.id.cb_login_remember);
//        cb_login_auto = (CheckBox) findViewById(R.id.cb_login_auto);
//        btn_login = (Button) findViewById(R.id.btn_login);
//        btn_login.setOnClickListener(mOnClickListener);
//    }
//
//    @Override
//    public void loadData() {
//
//        String name = (String) SPUtils.getParam(SPUtils.loginName, "");
//        String pass = (String) SPUtils.getParam(SPUtils.loginPwd, "");
//        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(pass)) {
//            cb_login_remember.setChecked(true);
//            et_login_user.setText(name);
//            et_login_pass.setText(pass);
//        }
//        boolean isAuto = (Boolean) SPUtils.getParam(autoLogin, false);
//        if (isAuto) {//自动登录
//            cb_login_auto.setChecked(true);
//            autoLogin();
//        }
//    }
//
//    View.OnClickListener mOnClickListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.btn_login:
//                    login();
//                    break;
//            }
//        }
//    };
//
//    private void login() {
//        userStr = et_login_user.getText().toString();
//        passStr = et_login_pass.getText().toString();
//        if (userStr.length() == 0 || passStr.length() == 0) {
//            ToastUtils.showShortToast("帐号或密码不能为空");
//            return;
//        }
//        if (!NetUtil.isNetConnected(this)){
//            ToastUtils.showShortToast("请检查当前网络状态");
//            return;
//        }
//        else {
//            // 关闭连接
//            XmppConnection.closeConnection();
//
//            new Thread(new Runnable() {
//                public void run() {
//                    try {
//                        // 重连服务器
//                        XmppConnection.connection = XmppConnection.getConnection();
//                        XmppConnection.login(userStr, passStr);
//                        handler.sendEmptyMessage(1);
//                    } catch (Exception e) {
//                        XmppConnection.closeConnection();
//                        Message msg = handler.obtainMessage();
//                        msg.what = 2;
//                        msg.obj = e.getMessage();
//                        handler.sendMessage(msg);
//                    }
//                }
//            }).start();
//        }
//    }
//
//    /**
//     * 自动登录
//     */
//    private void autoLogin(){
//        if (XmppConnection.connection == null){
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    XmppConnection.getConnection();
//                    handler.sendEmptyMessage(4);
//                }
//            }).start();
//        } else {
//            if (XmppConnection.connection.isConnected()){
//                isUserOnline();
//            }
//        }
//    }
//
//    /**
//     * 查询好友状态
//     */
//    private void isUserOnline(){
//        Roster roster = XmppConnection.connection.getRoster();
//        Collection<RosterEntry> entries = roster.getEntries();
//        for (RosterEntry entry : entries) {
//            Presence presence = roster.getPresence(entry.getUser());
//            Log.e("查询用户状态*****", entry.getUser()+""); //lxl@grhao.com
//            Log.e("查询用户状态*****", presence.isAvailable()+""); //true在线
//        }
//
//        //查询(当前)用户是否在线
//        String toUser =  (String) SPUtils.getParam(SPUtils.loginName, "") + "@" + XmppConnection.connection.getServiceName();
//        Presence presence = roster.getPresence(toUser);
//        Log.e("查询指定用户状态*****", toUser + "-----" + presence.isAvailable()+""); //true在线
//        Message message = handler.obtainMessage();
//        message.what = 3;
//        message.obj = presence.isAvailable();
//        handler.sendMessage(message);
//    }
//
//
//    @SuppressLint("HandlerLeak")
//    private Handler handler = new Handler() {
//        public void handleMessage(Message msg) {
//            if (msg.what == 1) {
//                ToastUtils.showShortToast("登录成功！");
//                saveToLocal();
//                toNextPage();
//            } else if (msg.what == 2) {
//                ToastUtils.showShortToast((String)msg.obj);
//            } else if (msg.what == 3){
//                if ((boolean)msg.obj){
//                    toNextPage();
//                } else {
//                    login();
//                }
//            } else if (msg.what == 4){
//                if (XmppConnection.connection == null){
//                    ToastUtils.showShortToast("未连接成功");
//                } else {
//                   isUserOnline();
//                }
//            }
//        }
//    };
//
//
//    private void saveToLocal(){
//        //默认记住账号密码
//        if (cb_login_remember.isChecked()) {
//            SPUtils.setParam(SPUtils.loginName, userStr);
//            SPUtils.setParam(SPUtils.loginPwd, passStr);
//        } else {
//            SPUtils.remove(SPUtils.loginName);
//            SPUtils.remove(SPUtils.loginPwd);
//        }
//        //默认自动登录
//        if (cb_login_auto.isChecked()) {
//            SPUtils.setParam(autoLogin, true);
//        } else {
//            SPUtils.remove(autoLogin);
//        }
//    }
//
//
//    private void toNextPage() {
//        //保存登录状态
//        SPUtils.setParam(SPUtils.isLogin, true);
//        // 跳转到聊天列表
//        Intent intent = new Intent();
//        intent.setClass(LoginActivity.this, ChatActivity.class);
//        startActivity(intent);
//        finish();
//    }
//
//
//}

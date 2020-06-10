package com.zsinfo.guoranhao.chat.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.beans.KFBean;
import com.zsinfo.guoranhao.chat.adapter.ChatAdapter;
import com.zsinfo.guoranhao.chat.base.BaseActivity;
import com.zsinfo.guoranhao.chat.bean.ChatMessageBean;
import com.zsinfo.guoranhao.chat.db.TalkListManager;
import com.zsinfo.guoranhao.chat.receiver.ChatBroadCastRecevier;
import com.zsinfo.guoranhao.chat.receiver.LockScreenReceiver;
import com.zsinfo.guoranhao.chat.upaiyun.UploadAsyncTask;
import com.zsinfo.guoranhao.chat.utils.ChatUtils;
import com.zsinfo.guoranhao.chat.utils.FileSaveUtil;
import com.zsinfo.guoranhao.chat.utils.ImageCheckoutUtil;
import com.zsinfo.guoranhao.chat.utils.KeyBoardUtils;
import com.zsinfo.guoranhao.chat.utils.NetUtil;
import com.zsinfo.guoranhao.chat.utils.PictureUtil;
import com.zsinfo.guoranhao.chat.utils.SPUtils;
import com.zsinfo.guoranhao.chat.utils.ToastUtils;
import com.zsinfo.guoranhao.chat.widget.AudioRecordButton;
import com.zsinfo.guoranhao.chat.widget.ChatBottomView;
import com.zsinfo.guoranhao.chat.widget.HeadIconSelectorView;
import com.zsinfo.guoranhao.chat.xmpp.XmppConnection;
import com.zsinfo.guoranhao.utils.LogUtils;
import com.zsinfo.guoranhao.utils.SharedPreferencesUtil;
import com.zsinfo.guoranhao.utils.UrlUtils;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.zsinfo.guoranhao.chat.utils.SPUtils.loginTime;

/**
 * Created by lixl on 2018/7/4.
 * <p>
 * desc:聊天界面（包括单聊和群聊）
 *
 * ①网络状况
 * ②xmpp连接登录
 * ③分配客服
 *
 * ④进入聊天界面，登录
 * ⑤退出聊天界面，断开连接
 */
public class ChatActivity extends BaseActivity implements ChatBroadCastRecevier.EventHandler {

    private SwipeRefreshLayout chat_swiperefresh;
    private ListView chat_listview;
    private EditText et_message;
    private TextView send_message;
    private ImageView btn_take_picture;
    private ImageView iv_voice;
    private AudioRecordButton voice_btn;
    private ChatBottomView cbv;

    private List<ChatMessageBean> msgList;
    private ChatAdapter chatAdapter;
    private TalkListManager talkListManager;

    //dialog
    private LinearLayout layout_tongbao_rl;
    private LinearLayout loading_view;
    private ProgressBar progressBar;
    private TextView tv_progress;
    private TextView btn_resert;

    private String fromJid = "";  //唯一标识，登录id
    private String toJid = "";
    private String pwd = "";
    private String fromUser = "";  //用户名
    private String toUser = "";  //客服
    private String userLogo = "";  //当前用户头像
    private Bitmap userLogoBitmap = null;
    private KFBean.DataBean kfDatas;
    private int index = 0;

    private String camPicPath;
    private File mCurrentPhotoFile;
    private static final int RECORD_AUDIO = 202;
    private static final int RECORD_CREAM = 203;
    private static final int RECORD_STORAGE = 204;
    private static final int IMAGE_SIZE = 100 * 1024;// 100kb

    private int startIndex = 0;
    private int msgCount = 10;
    private Chat chat;
    private ChatManager chat_Manger;
    private ChatBroadCastRecevier chatRecevier;
    private LockScreenReceiver lockScreenReceiver;
    private Intent serviceIntent;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                //登录
                try {
                    XmppConnection.login(fromJid, pwd);
                    getChat();
                    getConnData();
                } catch (Exception e){
                    //可能当前用户没有在openfire注册
                    showDialog("请退出账号重新登录，\n即可使用在线咨询");
                    btn_resert.setText("确定");
                    btn_resert.setVisibility(View.VISIBLE);
                }
            } else if (msg.what == 2) {
                ToastUtils.showShortToast((String)msg.obj);
                btn_resert.setVisibility(View.VISIBLE);
            } else if (msg.what == 3){
                if (!(boolean)msg.obj){
                    login();
                }
            }
        }
    };


    @Override
    public View setCreateView() {
        View view = View.inflate(this, R.layout.activity_chat, null);
        return view;
    }

    @Override
    public void initView() {
        //和标题栏相关的展示
        isShowBackIcon(true);
        isShowRight1(false);
        isShowRight2(false);

        fromJid = (String) SPUtils.getParam(SPUtils.loginName, "");
        fromUser = (String) SPUtils.getParam(SPUtils.loginNameAlias, "");
        userLogo = (String) SPUtils.getParam(SPUtils.userLogo, "");
        pwd = (String) SPUtils.getParam(SPUtils.loginPwd, "");

        chat_swiperefresh = (SwipeRefreshLayout) findViewById(R.id.chat_swiperefresh);
        chat_listview = (ListView) findViewById(R.id.chat_listview);
        et_message = (EditText) findViewById(R.id.et_message);
        send_message = (TextView) findViewById(R.id.send_message);
        btn_take_picture = (ImageView) findViewById(R.id.btn_take_picture);
        iv_voice = (ImageView) findViewById(R.id.iv_voice);
        cbv = (ChatBottomView) findViewById(R.id.cbv);
        voice_btn = (AudioRecordButton) findViewById(R.id.voice_btn);

        layout_tongbao_rl = (LinearLayout) findViewById(R.id.layout_tongbao_rl);
        loading_view = (LinearLayout) findViewById(R.id.loading_view);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_progress = (TextView) findViewById(R.id.tv_progress);
        btn_resert = (TextView) findViewById(R.id.btn_resert);

        send_message.setOnClickListener(this);
        btn_take_picture.setOnClickListener(this);
        iv_voice.setOnClickListener(this);
        btn_resert.setOnClickListener(this);

        //获取当前用户的jid和别名，没有，则关闭界面
        if (TextUtils.isEmpty(fromJid) || TextUtils.isEmpty(fromUser)){
            finish();
            return;
        }
        //获取客服，防止无网络情况下，没有获取到客服的情况发生
        if (!NetUtil.isNetConnected(this)){
            showDialog("当前无网络，请检查网络");
            btn_resert.setVisibility(View.VISIBLE); //失败
            return;
        }
        //获取客服列表
        getAsyncKF();
    }

    private void initData(){
        //模拟刷新效果
        if (chat_swiperefresh != null) {
            chat_swiperefresh.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (chat_swiperefresh.isRefreshing()) {  //判断是否正在刷新
                        chat_swiperefresh.setRefreshing(false);//关闭刷新状态
                    }
                }
            }, 1000);
        }
    }

    @Override
    public void loadData() {
        userLogo = (String) SPUtils.getParam(SPUtils.userLogo, "");
        setMiddleText("分配客服中...");
        //initService();
        initReceiver();
        getUserLogo(userLogo);
    }

    /**
     * 和xmpp相关的
     */
    private void getConn(){
        if (XmppConnection.connection == null){
            login();
            return;
        }
//        else {
//            showDialog("正在连接服务，请稍候");  //重新登录
//            isUserOnline();
//            return;
//        }
    }

    /**
     * xmpp后期的数据展示
     */
    private void getConnData(){
        getAdapter();
        getListener();
        getDb();
        dismissDialog();
    }

    /**
     * xmpp连接成功，获取发送消息体的Chat对象
     */
    private void getChat(){
        chat_Manger = XmppConnection.connection.getChatManager();
        chat = chat_Manger.createChat(toJid + "@" + XmppConnection.connection.getServiceName(), null);  //jid
        //chat.sendMessage()发送消息
    }

    /**
     * 获取客服列表
     * 客服列表接口iminfo_list，现在只传一个 ImInfo 对象 ，101010状态码代表没有客服
     */
    private void getAsyncKF(){
        showDialog("正在为您分配客服中，请稍候");
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .get()
                .url(UrlUtils.SERVE + "?method=iminfo_list")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Request request, IOException e) {
                //失败
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btn_resert.setText("很抱歉，当前暂无客服分配");
                        btn_resert.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                //成功
                final String result = response.body().string();
                Log.e("js 给的数据 kf", result);
                try {
                    JSONObject object = new JSONObject(result);
                    String statusCode = object.optString("statusCode");
                    final String statusStr = object.optString("statusStr");
                    Gson gson = new Gson();
                    KFBean kfBean = gson.fromJson(result, KFBean.class);
                    if ("100000".equals(statusCode)) {
                        kfDatas = kfBean.getData();
                        toJid = kfDatas.getStaffCode();
                        toUser = kfDatas.getStaffName();  //默认情况下，全部客服不在线，选择第一个客服
                        //toJid = "lixl";  //密码123456
                        //toUser = "李雪莉";
                        //记录登录的时间
                        String login_time = (String)SPUtils.getParam(loginTime, "");
                        if (TextUtils.isEmpty(login_time)){
                            SPUtils.setParam(loginTime, System.currentTimeMillis()+ "&" + toJid);
                            com.zsinfo.guoranhao.chat.utils.LogUtils.d("3天 ------1", System.currentTimeMillis()+ "&" + toJid);
                        } else {
                            String login_to_jid = login_time.split("&")[1];
                            if (!login_to_jid.equals(toJid)){
                                SPUtils.setParam(loginTime, System.currentTimeMillis()+ "&" + toJid);
                                com.zsinfo.guoranhao.chat.utils.LogUtils.d("3天 ------2", System.currentTimeMillis()+ "&" + toJid);
                            }
                        }
                        setAsync("");
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                btn_resert.setText(statusStr);
                                btn_resert.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showDialog("哦哦，出现了未知错误");
                            btn_resert.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });
    }

    /**
     * POST异步请求，查询某个用户是否在线
     *
     * http://im.grhao.com/plugins/presence/status?jid=lxl1@grhao.com&type=image
     */
//    private void getAsyncKFStauts(final String to_jid, final String to_user){
//        OkHttpClient okHttpClient = new OkHttpClient();
//        Request request = new Request.Builder()
//                .get()
//                .url("http://im.grhao.com/plugins/presence/status" +
//                        "?jid=" + to_jid + "@" + Constants.XMPP_SERVICE_NAME +
//                        "&type=xml")
//                .build();
//        okHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Request request, IOException e) {
//                //失败
//                btn_resert.setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onResponse(Response response) throws IOException {
//                //成功
//                final String result = response.body().string();
//                final int kfStatus = isUserOnLine(result);
//                LogUtils.e("ChatActivity------", index + ":" + to_jid + "，状态" + kfStatus + "=====" + result);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (kfStatus != 1){
//                        //if (result.contains("Unavailable")){
//                            index ++;
//                            if (index < kfDatas.size()){ //防止数组下标越界
//                                getAsyncKFStauts(kfDatas.get(index).getStaffCode(), kfDatas.get(index).getStaffName());
//                            } else {
//                                //表示index == kf_to_im.length最后一个了
//                                if (TextUtils.isEmpty(toUser)){
//                                    toJid = kfDatas.get(0).getStaffCode();
//                                    toUser = kfDatas.get(0).getStaffName();  //默认情况下，全部客服不在线，选择第一个客服
//                                    //记录登录的时间
//                                    String login_time = (String)SPUtils.getParam(loginTime, "");
//                                    if (TextUtils.isEmpty(login_time)){
//                                        SPUtils.setParam(loginTime, System.currentTimeMillis()+ "&" + toJid);
//                                        com.zsinfo.guoranhao.chat.utils.LogUtils.d("3天 ------1", System.currentTimeMillis()+ "&" + toJid);
//                                    } else {
//                                        String login_to_jid = login_time.split("&")[1];
//                                        if (!login_to_jid.equals(toJid)){
//                                            SPUtils.setParam(loginTime, System.currentTimeMillis()+ "&" + toJid);
//                                            com.zsinfo.guoranhao.chat.utils.LogUtils.d("3天 ------2", System.currentTimeMillis()+ "&" + toJid);
//                                        }
//                                    }
//                                    //setAsync("您好，当前果然好客服不在，系统自动为您分配" + toUser + "，请问有什么可以为你服务的吗？");
//                                    setAsync("(离线请留言)");
//                                }
//                            }
//                        } else {
//                            toJid = to_jid;
//                            toUser = to_user;  //在线的客服
//                            //记录登录的时间
//                            String login_time = (String)SPUtils.getParam(loginTime, "");
//                            if (TextUtils.isEmpty(login_time)){
//                                SPUtils.setParam(loginTime, System.currentTimeMillis()+ "&" + toJid);
//                                com.zsinfo.guoranhao.chat.utils.LogUtils.d("3天 ------1", System.currentTimeMillis()+ "&" + toJid);
//                            } else {
//                                String login_to_jid = login_time.split("&")[1];
//                                if (!login_to_jid.equals(toJid)){
//                                    SPUtils.setParam(loginTime, System.currentTimeMillis()+ "&" + toJid);
//                                    com.zsinfo.guoranhao.chat.utils.LogUtils.d("3天 ------2", System.currentTimeMillis()+ "&" + toJid);
//                                }
//                            }
//                            //setAsync("您好，我是果然好客服" + toUser + "，请问有什么可以为你服务的吗？");
//                            setAsync("");
//                        }
//                    }
//                });
//            }
//        });
//    }

    /**
     * 判断openfire用户的状态
     * strUrl : url格式 - http://my.openfire.com:9090/plugins/presence/status?jid=user1@my.openfire.com&type=xml
     * 返回值 : 0 - 用户不存在; 1 - 用户在线; 2 - 用户离线
     * 说明   ：必须要求 openfire加载 presence 插件，同时设置任何人都可以访问
     */
//    private int isUserOnLine(String strUrl){
//        int shOnLineState = 0;    //-不存在-
//        if(strUrl.indexOf("type=\"unavailable\"")>=0){
//            shOnLineState = 2;
//        } else if(strUrl.indexOf("type=\"error\"")>=0){
//            shOnLineState = 0;
//        } else if(strUrl.indexOf("priority")>=0 || strUrl.indexOf("id=\"")>=0){
//            shOnLineState = 1;
//        }
//        return shOnLineState;
//    }

    private void setAsync(final String kf_desc){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(kf_desc)){
                    setMiddleText("客服：" + toUser + kf_desc);
                } else {
                    setMiddleText("客服：" + toUser + "为您服务");
                }
                dismissDialog();
                getConn();
                //getHeadView(kf_desc);
            }
        });
    }

    /**
     * 使用账号密码登录
     */
    private void login() {
        // 关闭连接
        XmppConnection.closeConnection();
        //子线程连接
        new Thread(new Runnable() {
            public void run() {
                try {
                    // 重连服务器
                    XmppConnection.connection = XmppConnection.getConnection();
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    XmppConnection.closeConnection();
                    Message msg = handler.obtainMessage();
                    msg.what = 2;
                    msg.obj = e.getMessage();
                    handler.sendMessage(msg);
                }
            }
        }).start();
    }

    /**
     * 查询好友状态
     */
    private void isUserOnline(){
        Roster roster = XmppConnection.connection.getRoster();
        //查询(当前)用户是否在线
        String toUser =  fromJid + "@" + XmppConnection.connection.getServiceName();
        Presence presence = roster.getPresence(toUser);
        LogUtils.e("ChatActivity 查询指定用户状态*****", (String) SharedPreferencesUtil.getUserAccount() + "-----" + presence.isAvailable()+""); //true在线
        Message message = handler.obtainMessage();
        message.what = 3;
        message.obj = presence.isAvailable();
        handler.sendMessage(message);
    }

    /**
     * ListView
     * @param kf_desc  客服描述
     */
    private void getHeadView(String kf_desc){
        View v = LayoutInflater.from(ChatActivity.this).inflate(R.layout.item_chat_msg_left, null);
        ((TextView) v.findViewById(R.id.tv_othername)).setText("客服：" + toUser);
        ((TextView) v.findViewById(R.id.tv_othertime)).setVisibility(View.GONE);
        ((TextView) v.findViewById(R.id.content)).setText(kf_desc);
        chat_listview.addHeaderView(v);
    }

    /**
     * 下载用户头像
     * @param userLogo
     */
    private void getUserLogo(final String userLogo){
        if (!TextUtils.isEmpty(userLogo)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    userLogoBitmap = ChatUtils.getbitmap(userLogo);
                    LogUtils.e("ChatActivity Bitmap", "每次打开聊天界面，重新缓存一遍用户头像");
                }
            }).start();
        }
    }

    /**
     * 显示dialog
     */
    private void showDialog(String infomation){
        if (loading_view != null){
            layout_tongbao_rl.setVisibility(View.GONE);
            loading_view.setVisibility(View.VISIBLE);
            tv_progress.setText(infomation);
        }
    }

    /**
     * 隐藏dialog
     */
    private void dismissDialog(){
        if (loading_view != null){
            layout_tongbao_rl.setVisibility(View.VISIBLE);
            loading_view.setVisibility(View.GONE);
            btn_resert.setVisibility(View.GONE);
        }
    }

    private void initReceiver() {
        //动态注册广播
        chatRecevier = new ChatBroadCastRecevier();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("chat.receiver.CahtMessageBroadCast");
        registerReceiver(chatRecevier, intentFilter);
        chatRecevier.setEventHandler(this);

        lockScreenReceiver = new LockScreenReceiver();
        IntentFilter filter = new IntentFilter();
        // 屏幕灭屏广播
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        // 屏幕亮屏广播
        filter.addAction(Intent.ACTION_SCREEN_ON);
        // 屏幕解锁广播
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
        // example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
        // 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(lockScreenReceiver, filter);
    }

//    private void initService(){
//        //登录成功之后，设置后台服务
//        serviceIntent = new Intent(this, XmppService.class);
//        startService(serviceIntent);
//    }

    /**
     * 添加适配器，listview刷新监听
     */
    private void getAdapter(){
        msgList = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, msgList, fromJid, toJid, fromUser, toUser, userLogo, userLogoBitmap);
        chat_listview.setAdapter(chatAdapter);
        chat_listview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //消息聊天列表--触碰，隐藏软键盘
                btn_take_picture.setBackgroundResource(R.mipmap.chat_take_picture);
                cbv.setVisibility(View.GONE);
                KeyBoardUtils.hideKeyBoard(ChatActivity.this, et_message);
                return false;
            }
        });
        // 设置颜色属性的时候一定要注意是引用了资源文件还是直接设置16进制的颜色，因为都是int值容易搞混
        // 设置下拉进度的背景颜色，默认就是白色的
        chat_swiperefresh.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        chat_swiperefresh.setColorSchemeResources(R.color.orange, R.color.orange, R.color.actionsheet_red);
        chat_swiperefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                startIndex += msgCount;
                getDb();
                initData();
            }
        });
    }

    /**
     * 加载本地数据库
     */
    private void getDb() {
        if (!TextUtils.isEmpty(toJid)) {
            //数据库
            talkListManager = TalkListManager.getInstance();
            //// TODO: 2018/7/31  本地判断3天时间
            long currentTimeMillis = System.currentTimeMillis();
            String timeSplit = (String) SPUtils.getParam(loginTime, ""); //保存在本地的时间戳 & toJid
            String login_time = timeSplit.split("&")[0];
            String login_to_jid = timeSplit.split("&")[1];
            if (login_to_jid.equals(toJid)) {
                if (currentTimeMillis - Long.parseLong(login_time) >= SPUtils.LOGIN_TIME) {
                    talkListManager.deleteMessage(toJid);
                    SPUtils.remove(loginTime);
                    return;
                }
            }
            //msgList = talkListManager.queryTalkList(toJid);
            //msgList = talkListManager.queryTalkList();  //查询所有消息
            //通过数据库查询：分页消息
            msgList = talkListManager.queryMsgLimit(fromJid, toJid, startIndex, msgCount);
            //拉取到历史消息
            if (msgList.size() > 0) {
                //使用Adapter显示数据
                chatAdapter.setChats(msgList);
                //移动到底部
                chat_listview.setSelection(msgList.size()-1);
            }
        }
    }

    public void getListener() {
        cbv.setOnHeadIconClickListener(new HeadIconSelectorView.OnHeadIconClickListener() {

            @SuppressLint("InlinedApi")
            @Override
            public void onClick(int from) {
                switch (from) {
                    case ChatBottomView.FROM_CAMERA:
                        if(ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ChatActivity.this.requestPermissions(new String[]{Manifest.permission.CAMERA}, RECORD_CREAM);
                            }
                        } else{
                            final String state = Environment.getExternalStorageState();
                            if (Environment.MEDIA_MOUNTED.equals(state)) {
                                Intent intent = createCameraIntent();
                                startActivityForResult(intent, ChatBottomView.FROM_CAMERA);
                            } else {
                                ToastUtils.showShortToast("请检查内存卡");
                            }
                        }
                        break;
                    case ChatBottomView.FROM_GALLERY:
                        if(ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED	) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                ChatActivity.this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, RECORD_STORAGE);
                            }
                        } else {
                            String status = Environment.getExternalStorageState();
                            if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
                                Intent intent = createFileItent();
                                startActivityForResult(intent, ChatBottomView.FROM_GALLERY);
                            } else {
                                ToastUtils.showShortToast("没有SD卡");
                            }
                        }
                        break;

                    case ChatBottomView.FROM_PHRASE:
                    break;
                }
            }
        });

        voice_btn.setAudioFinishRecorderListener(new AudioRecordButton.AudioFinishRecorderListener() {

            @Override
            public void onFinished(float seconds, String filePath) {
                sendVoice(seconds, filePath);
            }

            @Override
            public void onStart() {
                chatAdapter.stopPlayVoice();
            }
        });

        chatAdapter.setVoiceIsReadListener(new ChatAdapter.VoiceIsRead() {

            @Override
            public void voiceOnClick(int position) {
                for (int i = 0; i < chatAdapter.unReadPosition.size(); i++) {
                    if (chatAdapter.unReadPosition.get(i).equals(position + "")) {
                        chatAdapter.unReadPosition.remove(i);
                        break;
                    }
                }
            }

        });
    }

    /**
     * 创建选择图库的intent
     *
     * @return
     */
    private Intent createFileItent() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        return intent;
    }

    /**
     * 创建调用照相机的intent
     *
     * @return
     */
    private Intent createCameraIntent() {
        camPicPath = getSavePicPath();
        File file = new File(camPicPath);
        Intent cameraIntent = new Intent();
        cameraIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);  //添加这一句表示对目标应用临时授权该Uri所代表的文件
            imageUri = FileProvider.getUriForFile(
                    ChatActivity.this,
                    "com.zsinfo.guoranhao.FileProvider",
                    file);
        } else {
            imageUri = Uri.fromFile(file);
        }
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        return cameraIntent;
    }

    /**
     * 调用系统照片的裁剪功能
     */
    public static Intent invokeSystemCrop(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop为true是设置在开启的intent中设置显示的view可以剪裁
        intent.putExtra("crop", "true");

        intent.putExtra("scale", true);

        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        // outputX,outputY 是剪裁图片的宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());

        return intent;
    }

    private String getSavePicPath() {
        final String dir = FileSaveUtil.pic_dir;
        try {
            FileSaveUtil.createSDDirectory(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileName = String.valueOf(System.currentTimeMillis() + ".png");
        return dir + fileName;
    }

    /**
     * 图片超过1M，压缩图片
     * @param path
     */
    private void imageCompression (final String path) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    final String GalPicPath = getSavePicPath();
                    Bitmap bitmap = PictureUtil.compressSizeImage(path);
                    boolean isSave = FileSaveUtil.saveBitmap(
                            PictureUtil.reviewPicRotate(bitmap, GalPicPath),
                            GalPicPath);
                    File file = new File(GalPicPath);
                    if (file.exists() && isSave) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                sendImage(GalPicPath);
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 发送语音消息
     * @param seconds 音频时长
     * @param filePath
     */
    protected void sendVoice(final float seconds, final String filePath) {
        if (!NetUtil.isNetConnected(this) || XmppConnection.connection == null){
            ToastUtils.showShortToast("当前不可发送消息，请确认网络连接状态或重新进入咨询界面!");
            return;
        }
        if (TextUtils.isEmpty(fromJid) || TextUtils.isEmpty(toJid)){
            ToastUtils.showShortToast("当前用户信息有误，请重新进入咨询界面");
            return;
        }
        new UploadAsyncTask("mp3", ChatActivity.this, new UploadAsyncTask.Callback() {

            @Override
            public void send(String result) {
                final long currentTime = System.currentTimeMillis();
                final String msgType = "voice";
                final int msgIsRead = 1; //自己发送的，默认已读状态
                int sendStatus = 2; //默认网络连接成功
                try {
                    chat.sendMessage(ChatUtils.toVoiceJson(msgType, result, fromJid, toJid, seconds+""));
                } catch (Exception e) {
                    e.printStackTrace();
                    sendStatus = 1; //表示发送失败
                }
                ChatMessageBean messageBean = new ChatMessageBean(currentTime, msgType, currentTime, result, fromJid, toJid, seconds+"", msgIsRead, sendStatus);
                chatAdapter.addChat(messageBean);
                talkListManager.insertMessage(false, messageBean, "");
            }
        }).execute(filePath);
    }

    /**
     * 发送图片消息，注意，这个方法要在主线程中
     * @param filePath
     */
    private void sendImage(final String filePath) {
        if (!NetUtil.isNetConnected(this) || XmppConnection.connection == null){
            ToastUtils.showShortToast("当前不可发送消息，请确认网络连接状态或重新进入咨询界面!");
            return;
        }
        if (TextUtils.isEmpty(fromJid) || TextUtils.isEmpty(toJid)){
            ToastUtils.showShortToast("当前用户信息有误，请重新进入咨询界面");
            return;
        }
        if (NetUtil.isNetConnected(ChatActivity.this)) {
            new UploadAsyncTask("png", ChatActivity.this,
                    new UploadAsyncTask.Callback() {

                        @Override
                        public void send(String result) {
                            final long currentTime = System.currentTimeMillis();
                            final String msgType = "image";
                            final int msgIsRead = 1; //自己发送的，默认已读状态
                            int sendStatus = 2; //默认网络连接成功
                            try {
                                //xmpp发送消息
                                chat.sendMessage(ChatUtils.toJson(msgType, result, fromJid, toJid));
                            } catch (XMPPException e) {
                                e.printStackTrace();
                                sendStatus = 1; //表示发送失败
                            }
                            ChatMessageBean messageBean = new ChatMessageBean(currentTime, msgType, currentTime, result, fromJid, toJid, msgIsRead, sendStatus);
                            chatAdapter.addChat(messageBean);
                            talkListManager.insertMessage(false, messageBean, "");
                        }
                    }).execute(filePath);
        } else {
            //todo 表示无网络，消息发送失败
            ToastUtils.showShortToast("网络链接异常，清检查网络后再试");
        }
    }

    /**
     * 发送文本消息
     * @param msg
     */
    private void sendMessage(String msg){
        if (!NetUtil.isNetConnected(this) || XmppConnection.connection == null){
            ToastUtils.showShortToast("当前不可发送消息，请确认网络连接状态或重新进入咨询界面!");
            return;
        }

        if (!TextUtils.isEmpty(msg)){
            if (TextUtils.isEmpty(fromJid) || TextUtils.isEmpty(toJid)){
                ToastUtils.showShortToast("当前用户信息有误，请重新进入咨询界面");
                return;
            }
            long currentTime = System.currentTimeMillis();
            String msgType = "text";
            int msgIsRead = 1; //自己发送的，默认已读状态
            int sendStatus = 2; //默认网络连接成功

            try {
                //xmpp发送消息
                String json = ChatUtils.toJson(msgType, msg, fromJid, toJid);
                chat.sendMessage(json);
                LogUtils.e("ChatActivity sendMessage------", json);
            } catch (XMPPException e) {
                e.printStackTrace();
                sendStatus = 1; //表示发送失败
            }
            //新增一条消息
            ChatMessageBean messageBean = new ChatMessageBean(
                    currentTime, msgType, currentTime, msg, fromJid, toJid, msgIsRead, sendStatus);
            //添加到Adapter中
            chatAdapter.addChat(messageBean);
            //插入本地数据库表中
            talkListManager.insertMessage(false, messageBean, "");
            //将消息框置空
            et_message.setText("");
        } else {
            ToastUtils.showShortToast("暂无发送的内容");
        }
    }

    @Override
    public void onNetChange(boolean paramBoolean, ChatMessageBean messageBean) {
        if (paramBoolean && chatAdapter != null){
            chatAdapter.addChat(messageBean);
        }
    }

    @Override
    public void onNotify(String paramName, String paramValue) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //查询所有数据，可以判断，是否是刚才黑屏了
        LogUtils.i("ChatActivity onResume-----", "呵呵");
        LogUtils.i("ChatActivity onResume-----", "呵呵"+chatAdapter);
        LogUtils.i("ChatActivity onResume-----", "呵呵"+msgList);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.i("ChatActivity onPause-----", "呵呵");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chat_listview.setAdapter(null);
        XmppConnection.closeConnection();
        unregisterReceiver(chatRecevier);
        unregisterReceiver(lockScreenReceiver);
        SPUtils.setParam(SPUtils.isChatForeground, false);  //将判断是否在前台的字段还原
        //stopService(serviceIntent);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()){
            case R.id.tv_back:
                //xmpp断开连接，离线--2018/07/16
                XmppConnection.closeConnection();
                finish();
                break;
            case R.id.send_message:
                String msg = et_message.getText().toString().trim();
                sendMessage(msg);
                break;

            case R.id.btn_take_picture:
                //软键盘 和 拍照布局 以及表情布局 不能同时出现
                if (cbv.getVisibility() == View.GONE) {
                    cbv.setVisibility(View.VISIBLE);
                    KeyBoardUtils.hideKeyBoard(ChatActivity.this, et_message);
                    btn_take_picture.setBackgroundResource(R.mipmap.chatting_setmode_keyboard_btn_normal);
                } else {
                    cbv.setVisibility(View.GONE);
                    KeyBoardUtils.showKeyBoard(ChatActivity.this, et_message);
                    btn_take_picture.setBackgroundResource(R.mipmap.chat_take_picture);
                }
                break;

            case R.id.iv_voice:
                //6.0以上手机的录音权限需要手动开启
                if(ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        this.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, RECORD_AUDIO);
                    }
                    return;
                }
                //发起录音的时候，软键盘，表情布局，拍照布局 都不能出现
                if (voice_btn.getVisibility() == View.GONE) {
                    btn_take_picture.setBackgroundResource(R.mipmap.chat_take_picture);
                    et_message.setVisibility(View.GONE);
                    cbv.setVisibility(View.GONE);
                    voice_btn.setVisibility(View.VISIBLE);
                    KeyBoardUtils.hideKeyBoard(this, et_message);
                    iv_voice.setBackgroundResource(R.mipmap.chatting_setmode_keyboard_btn_normal);
                } else {
                    et_message.setVisibility(View.VISIBLE);
                    voice_btn.setVisibility(View.GONE);
                    iv_voice.setBackgroundResource(R.mipmap.chatting_setmode_voice_btn_normal);
                    KeyBoardUtils.showKeyBoard(this, et_message);
                }
                break;

            case R.id.btn_resert:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            cbv.setVisibility(View.GONE);
            btn_take_picture.setBackgroundResource(R.mipmap.chat_take_picture);
            switch (requestCode) {
                case ChatBottomView.FROM_CAMERA:
                    try {
                        File camFile = new File(camPicPath); // 图片文件路径
                        if (camFile.exists()) {
                            int size = ImageCheckoutUtil
                                    .getImageSize(ImageCheckoutUtil
                                            .getLoacalBitmap(camPicPath));
                            if (size > IMAGE_SIZE) {
                                imageCompression(camPicPath);
                            } else {
                                sendImage(camPicPath);
                            }
                        } else {
                            ToastUtils.showShortToast("该文件不存在!");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case ChatBottomView.FROM_GALLERY:
                    Uri uri = data.getData();
                    String path = FileSaveUtil.getPath(getApplicationContext(), uri);
                    mCurrentPhotoFile = new File(path); // 图片文件路径
                    if (mCurrentPhotoFile.exists()) {
                        int size = ImageCheckoutUtil.getImageSize(ImageCheckoutUtil.getLoacalBitmap(path));
                        if (size > IMAGE_SIZE) {
                            imageCompression(path);
                        } else {
                            sendImage(path);
                        }
                    } else {
                        ToastUtils.showShortToast("该文件不存在!");
                    }
                    break;
            }
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch(requestCode){
            case RECORD_AUDIO:
                boolean albumAccepted = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(!albumAccepted){
                    Toast.makeText(ChatActivity.this, "请开启应用录音权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case RECORD_CREAM:
                boolean albumAccepted2 = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                if(!albumAccepted2){
                    Toast.makeText(ChatActivity.this, "请开启应用相机权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case RECORD_STORAGE:
                boolean albumAccepted3 = grantResults[0]==PackageManager.PERMISSION_GRANTED;
                if(!albumAccepted3){
                    Toast.makeText(ChatActivity.this, "请在设置中开启存储空间权限", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}

//package com.zsinfo.guoranhao.chat.activity;
//
//import android.graphics.Bitmap;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.zsinfo.guoranhao.R;
//import com.zsinfo.guoranhao.chat.utils.ChatUtils;
//import com.zsinfo.guoranhao.chat.utils.MD5Util;
//import com.zsinfo.guoranhao.chat.xmpp.XmppConnection;
//import com.zsinfo.guoranhao.utils.LogUtils;
//
//import org.jivesoftware.smack.PacketCollector;
//import org.jivesoftware.smack.Roster;
//import org.jivesoftware.smack.RosterEntry;
//import org.jivesoftware.smack.SmackConfiguration;
//import org.jivesoftware.smack.XMPPException;
//import org.jivesoftware.smack.filter.AndFilter;
//import org.jivesoftware.smack.filter.PacketFilter;
//import org.jivesoftware.smack.filter.PacketIDFilter;
//import org.jivesoftware.smack.filter.PacketTypeFilter;
//import org.jivesoftware.smack.packet.IQ;
//import org.jivesoftware.smack.packet.Presence;
//import org.jivesoftware.smack.packet.Registration;
//
//import java.io.ByteArrayOutputStream;
//import java.util.Collection;
//
//public class TestActivity extends AppCompatActivity {
//
//    private EditText et_name;
//    private Button btn_select;
//    private TextView tv_desc;
//    private TextView tv_roster;
//    private EditText et_reg_user;
//    private EditText et_reg_pass;
//    private Button btn_reg;
//    private TextView tv_reg_status;
//
//    private EditText et_name_md5;
//    private TextView tv_name_md5;
//    private Button btn_md5;
//
//    private EditText et_alias_name;
//    private TextView tv_alias_result;
//    private Button btn_alias;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_test);
//
//        initData();
//        initDataReg();
//        initMd5();
//        initAlias();
//    }
//
//    private void initAlias() {
//        et_alias_name = (EditText) findViewById(R.id.et_alias_name);
//        tv_alias_result = (TextView) findViewById(R.id.tv_alias_result);
//        btn_alias = (Button) findViewById(R.id.btn_alias);
//        btn_alias.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = et_alias_name.getText().toString().trim();
//                setAlia();
//            }
//        });
//    }
//
//    private void initMd5() {
//        et_name_md5 = (EditText) findViewById(R.id.et_name_md5);
//        tv_name_md5 = (TextView) findViewById(R.id.tv_name_md5);
//        btn_md5 = (Button) findViewById(R.id.btn_md5);
//        btn_md5.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = et_name_md5.getText().toString().trim();
//                String nameToMd5 = MD5Util.makeMD5(name);
//                tv_name_md5.setText(nameToMd5);
//                LogUtils.e("Test", nameToMd5);
//            }
//        });
//    }
//
//    private void initDataReg() {
//        et_reg_user = (EditText) findViewById(R.id.et_reg_user);
//        et_reg_pass = (EditText) findViewById(R.id.et_reg_pass);
//        btn_reg = (Button) findViewById(R.id.btn_reg);
//        tv_reg_status = (TextView) findViewById(R.id.tv_reg_status);
//        btn_reg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String reg_user = et_reg_user.getText().toString().trim();
//                String reg_pass = et_reg_pass.getText().toString().trim();
//                reg(reg_user, reg_pass);
//            }
//        });
//    }
//
//    private void initData() {
//        et_name = (EditText) findViewById(R.id.et_name);
//        tv_roster = (TextView) findViewById(R.id.tv_roster);
//        btn_select = (Button) findViewById(R.id.btn_select);
//        tv_desc = (TextView) findViewById(R.id.tv_desc);
//        btn_select.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String name = et_name.getText().toString().trim();
//                isUserOnline(name);
//            }
//        });
//    }
//
//    /**
//     * 查询好友状态
//     */
//    private void isUserOnline(String selectUser){
//        StringBuffer sb = new StringBuffer();
//
//        Roster roster = XmppConnection.connection.getRoster();
//        Collection<RosterEntry> entries = roster.getEntries();
//        for (RosterEntry entry : entries) {
//            Presence presence = roster.getPresence(entry.getUser());
//            sb.append("查询用户状态*****" + entry.getUser() + "\n");  //lxl@grhao.com
//            sb.append("查询用户状态*****" + presence.isAvailable() + "\n");  //true在线
//        }
//        tv_roster.setText(sb.toString());
//
//        //查询(当前)用户是否在线
//        selectUser =  selectUser + "@" + XmppConnection.connection.getServiceName();
//        Presence presence = roster.getPresence(selectUser);
//        tv_desc.setText(presence.getType().toString() + "-----" + presence.getStatus() + "：" + presence.isAvailable());
//    }
//
//    private void reg(String username, String password){
//        Registration reg = new Registration();
//        reg.setType(IQ.Type.SET);
//        reg.setTo(XmppConnection.connection.getServiceName());
//        reg.setUsername(username);
//        reg.setPassword(password);
//        reg.addAttribute("android", "geolo_createUser_android");
//        System.out.println("reg:" + reg);
//        PacketFilter filter = new AndFilter(new PacketIDFilter(reg
//                .getPacketID()), new PacketTypeFilter(IQ.class));
//        PacketCollector collector = XmppConnection.connection.createPacketCollector(filter);
//        XmppConnection.connection.sendPacket(reg);
//
//        IQ result = (IQ) collector.nextResult(SmackConfiguration.getPacketReplyTimeout());
//        // Stop queuing results
//        collector.cancel();// 停止请求results（是否成功的结果）
//
//        if (result == null) {
//            Toast.makeText(getApplicationContext(), "服务器没有返回结果",
//                    Toast.LENGTH_SHORT).show();
//        } else if (result.getType() == IQ.Type.ERROR) {
//            if (result.getError().toString().equalsIgnoreCase(
//                    "conflict(409)")) {
//                //Toast.makeText(getApplicationContext(), "这个账号已经存在", Toast.LENGTH_SHORT).show();
//                tv_reg_status.setText("这个账号已经存在");
//            } else {
//                //Toast.makeText(getApplicationContext(), "注册失败", Toast.LENGTH_SHORT).show();
//                tv_reg_status.setText("注册失败");
//            }
//        } else if (result.getType() == IQ.Type.RESULT) {
//            //Toast.makeText(getApplicationContext(), "恭喜你注册成功", Toast.LENGTH_SHORT).show();
//            tv_reg_status.setText("恭喜你注册成功");
//        }
//    }
//
//    private void setAlia() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // 设置头像
//                String imagePath = "https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=4145499948,3369439314&fm=27&gp=0.jpg";
//                Bitmap bm = ChatUtils.getbitmap(imagePath);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                try {
//                    XmppConnection.updateAvater(baos.toByteArray());
//                } catch (XMPPException e) {
//                    e.printStackTrace();
//                    LogUtils.e("XmppConnection VCard-------------", "头像设置失败");
//                }
//            }
//        }).start();
//
//    }
//}

package com.zsinfo.guoranhao.chat.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.chat.xmpp.XmppConnection;

import org.jivesoftware.smack.packet.Presence;

/**
 * Created by lixl on 2018/7/3.
 *
 * 所有Activity的基类
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener{

    public TextView tv_back;
    public TextView tv_content_title;
    public ImageView img_contacts;
    public ImageView img_settings;
    public FrameLayout flContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.base_activity);

        initTitle();
        initView();
        loadData();
    }

    /**
     * findViewById
     */
    private void initTitle() {
        tv_back = (TextView) findViewById(R.id.tv_back);
        tv_content_title = (TextView) findViewById(R.id.tv_content_title);
        img_contacts = (ImageView) findViewById(R.id.img_contacts);
        img_settings = (ImageView) findViewById(R.id.img_settings);
        flContainer = (FrameLayout) findViewById(R.id.fl_container);

        flContainer.addView(setCreateView() ==null ?  setEmptyView() : setCreateView());

        tv_back.setOnClickListener(this);
        img_contacts.setOnClickListener(this);
        img_settings.setOnClickListener(this);
    }

    /**
     * 添加当前layout布局
     * @return
     */
    public abstract View setCreateView();

    /**
     * 加载自定义控件
     */
    public abstract void initView();

    /**
     * 数据展示以及逻辑处理
     */
    public abstract void loadData();

    private View setEmptyView(){
        View view = View.inflate(this, R.layout.activity_empty, null);
        return view;
    }

    /**
     * 设置标题
     * @return
     */
    public void setMiddleText(String title){
        tv_content_title.setText(title);
    }

    /**
     * 设置右侧背景图片1
     * @return
     */
    public void setRightImageRes1(int resId){
        img_contacts.setImageResource(resId);
    }

    /**
     * 设置右侧背景图片2
     * @return
     */
    public void setRightImageRes2(int resId){
        img_settings.setImageResource(resId);
    }

    /**
     * 是否显示左侧返回键
     * @param isShow
     */
    public void isShowBackIcon(boolean isShow){
        if (isShow){
            tv_back.setVisibility(View.VISIBLE);
        } else {
            tv_back.setVisibility(View.GONE);
        }
    }

    /**
     * 是否显示右侧图标1
     * @param isShow
     */
    public void isShowRight1(boolean isShow){
        if (isShow){
            img_contacts.setVisibility(View.VISIBLE);
        } else {
            img_contacts.setVisibility(View.GONE);
        }
    }

    /**
     * 是否显示右侧图标2
     * @param isShow
     */
    public void isShowRight2(boolean isShow){
        if (isShow){
            img_settings.setVisibility(View.VISIBLE);
        } else {
            img_settings.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_settings:
                //startActivity(new Intent(this, TestActivity.class));
                //③更改在线状态
                Presence presence = new Presence(Presence.Type.available);  //更改在线状态true
                presence.setMode(Presence.Mode.available);
                XmppConnection.connection.sendPacket(presence);
                break;
        }
    }
}

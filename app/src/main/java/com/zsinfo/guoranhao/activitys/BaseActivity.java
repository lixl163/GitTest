package com.zsinfo.guoranhao.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewGroup;

import com.zsinfo.guoranhao.event.BaseEvent;
import com.zsinfo.guoranhao.event.EventInterface;
import com.zsinfo.guoranhao.fragment.CommonsFragment;
import com.zsinfo.guoranhao.utils.Constant;
import com.zsinfo.guoranhao.utils.SharedPreferencesUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by admin on 2017/8/28.
 */

public abstract  class BaseActivity  extends AppCompatActivity {

    protected String url;
    protected int contentContainerId =0;
    protected CommonsFragment fragment;
    protected FragmentTransaction ft;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initView();
        initOthers();
        url = getLoadUrl();
        MainApplication.pushStack(this);
        contentContainerId =getContentContainerId();
        if (contentContainerId ==0){
            throw  new IllegalArgumentException("contentContatinerId cannot find, u must to init");
        }
        initFragment();
        if (fragment ==null ){
            throw  new IllegalArgumentException("fragment cannot be null");
        }
        ft=getSupportFragmentManager().beginTransaction();
        ft.replace(contentContainerId,fragment);
        ft.commitAllowingStateLoss();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MainApplication.popStack(this);

        //后续新增，对应的是BaseFragment-onHidden()
        Log.e("lixl", "当前关闭的网页：" + url);
        SharedPreferencesUtil.SetJSMethod("loginSuccess", url);
        if (url.contains("login.html")){
            //不知道什么原因，多次跳转到MainActivity
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    public void finish() {
        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        view.removeAllViews();
        super.finish();
    }

    public void ActivityFinish()
    {
        this.finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventInterface minterface){
        if (minterface instanceof BaseEvent){
            BaseEvent event= (BaseEvent) minterface;
            if (Constant.BASE_RELOAD==event.getFlag()&&url.contains(event.getUrl())){
                reload();
                return;
            }
            onMsgEventReceive(minterface);
            return ;
        }
        onMsgEventReceive(minterface);
    }

    protected abstract void onMsgEventReceive(EventInterface minterface);
    protected abstract void initView();
    protected abstract void initOthers();
    protected abstract String getLoadUrl();
    protected abstract int getLayoutId();
    //填充fragment的容器id
    protected abstract int getContentContainerId();
    //初始化要填充的fragment
    protected abstract void initFragment();
    public void reload(){
        initFragment();
        ft =getSupportFragmentManager().beginTransaction().replace(contentContainerId,fragment);
        ft.commitAllowingStateLoss();
    }
    public String getUrl() {
        return url;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseActivity other = (BaseActivity) obj;
        if (url == null) {
            if (other.getUrl() != null)
                return false;
        } else if (!url.equals(other.getUrl()))
            return false;
        return true;
    }
}

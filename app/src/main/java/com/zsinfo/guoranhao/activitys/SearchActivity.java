package com.zsinfo.guoranhao.activitys;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.event.EventInterface;
import com.zsinfo.guoranhao.fragment.CommonsFragment;
import com.zsinfo.guoranhao.utils.Constant;
import com.zsinfo.guoranhao.utils.SharedPreferencesUtil;

/**
 * Created by admin on 2017/8/29.
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener {
    private ImageView iv_back;
    private ImageView iv_clear;
    private EditText et_search;
    private TextView tv_search;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected int getContentContainerId() {
        return R.id.contentContainer;
    }

    @Override
    protected void initFragment() {
        fragment= CommonsFragment.newInstance(url);
    }

    @Override
    protected void onMsgEventReceive(EventInterface minterface) {

    }

    @Override
    protected void initView() {
        iv_back= (ImageView) findViewById(R.id.iv_back);
        iv_clear= (ImageView) findViewById(R.id.iv_clear);
        et_search= (EditText) findViewById(R.id.et_search);
        tv_search= (TextView) findViewById(R.id.tv_search);
        iv_back.setOnClickListener(this);
        tv_search.setOnClickListener(this);
        iv_clear.setOnClickListener(this);
    }

    @Override
    protected void initOthers() {
        url =getIntent().getStringExtra(Constant.NEXT_PAGE_URL);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String trim = s.toString().replace(" ","").trim();
                if ("".equals(trim)){
                    fragment.runJsMethod("javascript:searchWithText('"+trim+"')");
                }
            }
        });
    }

    @Override
    protected String getLoadUrl() {
        return url;
    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.iv_back:
                SharedPreferencesUtil.SetJSMethod(url, "");  //2019.8.13清空数据 url = /html/search.html
                finish();
                break;
            case R.id.iv_clear:
                et_search.setText("");
                break;
            case R.id.tv_search:
                String msg = et_search.getText().toString().trim();
                fragment.runJsMethod("javascript:searchWithText('"+msg+"')");
                break;
        }
    }

    public void setEditText(String msg){
        et_search.setText(msg);
    }
}

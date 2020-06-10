package com.zsinfo.guoranhao.activitys;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zsinfo.guoranhao.R;
import com.zsinfo.guoranhao.utils.LocationUtils;
import com.zsinfo.guoranhao.utils.SharedPreferencesUtil;
import com.zsinfo.guoranhao.widget.AlertDialog;


public class SpalshActivity extends AppCompatActivity {
    ViewPager vp;
    //    int[] imas = new int[]{R.mipmap.ic_launcher,R.mipmap.icon_mine_list,R.mipmap.icon_chose_a};
    int[] imas = new int[]{R.drawable.index1, R.drawable.index2, R.drawable.index3};
    ImageView[] imageViews = new ImageView[imas.length];
    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);




        vp = (ViewPager) findViewById(R.id.vp);
        vp.setAdapter(new MyAdappter());
        vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
//                LogUtils.e("arg0====="+arg0);
//                LogUtils.e("arg1====="+arg1);
//                LogUtils.e("arg2====="+arg2);
//                if(arg0==imageViews.length-1){
////                    finish();
//                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        SharedPreferencesUtil.setFirstStart();
    }


    //初始化定位
    private void initLocation() {
        LocationUtils.startLocation(null, SpalshActivity.this);
        mAlertDialog = new AlertDialog(this);
        mAlertDialog.builder()
                .setCancelable(false)
                .setTitle("定位权限申请")
                .setMsg("定位权限已经关闭，请到设置界面开启")
                .setPositiveButton("确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Uri packageURI = Uri.parse("package:" + "com.zsinfo.guoranhao");
//                        Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
//                        startActivity(intent);

                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
        mAlertDialog.show();
    }


    public class MyAdappter extends PagerAdapter {
        public MyAdappter() {
            for (int i = 0; i < imageViews.length; i++) {
                ImageView iv = new ImageView(SpalshActivity.this);
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                Picasso.with(SpalshActivity.this).load(imas[i]).config(Bitmap.Config.RGB_565).into(iv);
                if (i == imageViews.length - 1) {
               AlertDialog     alertDialog =new AlertDialog(SpalshActivity.this).builder()
                            .setCancelable(false)
                            .setTitle("定位权限申请")
                            .setMsg("定位权限已经关闭，请到设置界面开启")
                            .setPositiveButton("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Uri packageURI = Uri.parse("package:" + "com.zsinfo.guoranhao");
                                    Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
                                    SpalshActivity.this.startActivity(intent);
                                    SharedPreferencesUtil.setIsLoca(true);
                                }
                            })
                            .setNegativeButton("取消", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    SharedPreferencesUtil.setIsLoca(false);
                                }
                            });
                    alertDialog.show();

//                    Uri packageURI = Uri.parse("package:" + "com.zsinfo.guoranhao");
//                    Intent intent =  new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,packageURI);
//                    startActivity(intent);

                    iv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SpalshActivity.this.finish();
                        }
                    });
                }
                imageViews[i] = iv;
            }
        }


        @Override
        public int getCount() {
            return imageViews.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup view, int position, Object object) {
            ImageView imageView = imageViews[position];
            imageView.setImageBitmap(null);
            if (imageView != null) {
                Drawable drawable = imageView.getDrawable();
                if (drawable != null && drawable instanceof BitmapDrawable) {
                    BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    if (bitmap != null && !bitmap.isRecycled()) {
                        bitmap.recycle();
                        bitmap = null;
                    }
                }
            }
            view.removeView(imageViews[position]);

        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            view.addView(imageViews[position]);
            return imageViews[position];
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setContentView(R.layout.none_view);
        vp = null;

    }
}

package com.zsinfo.guoranhao.chat.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zsinfo.guoranhao.R;

public class PicInfoActivity extends Activity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pic_info);
		final String url = getIntent().getStringExtra("url");
		ImageView iv = (ImageView) findViewById(R.id.imageView1);
		iv.setOnClickListener(this);
		Picasso.with(this)
				.load(url)
				.placeholder(null)
				.config(Bitmap.Config.RGB_565)
//				.transform(new Transformation() {   //图片压缩，导致失帧
//					@Override
//					public Bitmap transform(Bitmap arg0) {
//						ByteArrayOutputStream baos = new ByteArrayOutputStream();
//						arg0.compress(Bitmap.CompressFormat.JPEG, 9999, baos);
//						byte[] b = baos.toByteArray();
//						Bitmap bm = ChatUtils.handlerBitmap(b);
//						if (bm != arg0) {
//							arg0.recycle();
//							System.gc();
//							arg0=null;
//						}
//						return bm;
//					}
//
//					@Override
//					public String key() {
//						return url;
//					}
//				})
				.into(iv);
	}

	@Override
	public void onClick(View v) {
		finish();
	}

}

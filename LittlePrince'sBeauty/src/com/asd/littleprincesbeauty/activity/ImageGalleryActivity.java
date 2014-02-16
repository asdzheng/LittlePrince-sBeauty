package com.asd.littleprincesbeauty.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.asd.littleprincesbeauty.R;
import com.asd.littleprincesbeauty.ui.GalleryAdapter;
import com.asd.littleprincesbeauty.ui.ImageGallery;

public class ImageGalleryActivity extends Activity {
	// 屏幕宽度
	public static int screenWidth;
	// 屏幕高度
	public static int screenHeight;
	private ImageGallery gallery;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.image_gallery);
		screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight();
		gallery = (ImageGallery) findViewById(R.id.gallery);
		gallery.setVerticalFadingEdgeEnabled(false);// 取消竖直渐变边框
		gallery.setHorizontalFadingEdgeEnabled(false);// 取消水平渐变边框
		gallery.setAdapter(new GalleryAdapter(this));
	}
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.i("manager", "onConfigurationChanged...");
		screenWidth = getWindow().getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindow().getWindowManager().getDefaultDisplay().getHeight();
		super.onConfigurationChanged(newConfig);
	}
	 
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	
}
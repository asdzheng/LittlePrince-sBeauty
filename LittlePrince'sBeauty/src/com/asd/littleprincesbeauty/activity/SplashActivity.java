package com.asd.littleprincesbeauty.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.asd.littleprincesbeauty.R;

public class SplashActivity extends Activity {

	TextView textView1, textView2, textView3, textView4, textView5, textView6,
			textView7, textView8, textView9, textView10, textView11,
			textView12, textView13, textView14;

	ImageView head, logo;
	final static long DISAPPEAR_TIME = 36900;
	final static long LOAD_MAINTAB_TIME =1000;
	final static long START_ROTATE_TIME = 37000;
	
	final static long HEAD_SHOW_TIME = 10000;
	final static long HEAD_DURATION_TIME = 20000;

	
	final static long TEXT_SHOW_TIME = 2000;
	final static long TEXT_DURATION_TIME = 2000;
//	final static long CLOSE_TEXT_DURATION_TIME = 1;

	private long time1 = TEXT_SHOW_TIME, 
				 time2 = time1 + TEXT_DURATION_TIME, 
				 time3 = time2+ TEXT_DURATION_TIME, 
				 time4 = time3 + TEXT_DURATION_TIME, 
				 time5 = time4+ TEXT_DURATION_TIME, 
				 time6 = time5 + TEXT_DURATION_TIME, 
				 time7 = time6+ TEXT_DURATION_TIME, 
				 time8 = time7 + TEXT_DURATION_TIME, 
				 time9 = time8+ TEXT_DURATION_TIME, 
				 time10 = time9 + TEXT_DURATION_TIME, 
				 time11 = time10+ TEXT_DURATION_TIME,
				 time12 = time11 + TEXT_DURATION_TIME, 
				 time13 = time12+ TEXT_DURATION_TIME,
				 time14 = time13 + TEXT_DURATION_TIME;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.splash);
		initTab();
		showTextView();
		showHeadImage();

		viewChange(logo);
		// closeView();

		new Handler().postDelayed(new disAppearView(), DISAPPEAR_TIME);
		new Handler().postDelayed(new loadMainTabTask(), LOAD_MAINTAB_TIME);

	}

	private void viewChange(View view) {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.anim_set);
		animation.setStartOffset(START_ROTATE_TIME);
		animation.setInterpolator(new LinearInterpolator());
		view.setAnimation(animation);
	}

	private void showHeadImage() {
		head.setImageResource(R.drawable.head_moon);
		AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.0f, 0.6f);
		alphaAnimation1.setDuration(HEAD_SHOW_TIME);
		alphaAnimation1.setStartOffset(HEAD_DURATION_TIME);
		alphaAnimation1.setFillAfter(true);
		head.setAnimation(alphaAnimation1);
	}

	private void initTab() {
		logo = (ImageView) findViewById(R.id.logo);
		head = (ImageView) findViewById(R.id.head);
		textView1 = (TextView) findViewById(R.id.text1);
		textView2 = (TextView) findViewById(R.id.text2);
		textView3 = (TextView) findViewById(R.id.text3);
		textView4 = (TextView) findViewById(R.id.text4);
		textView5 = (TextView) findViewById(R.id.text5);
		textView6 = (TextView) findViewById(R.id.text6);
		textView7 = (TextView) findViewById(R.id.text7);
		textView8 = (TextView) findViewById(R.id.text8);
		textView9 = (TextView) findViewById(R.id.text9);
		textView10 = (TextView) findViewById(R.id.text10);
		textView11 = (TextView) findViewById(R.id.text11);
		textView12 = (TextView) findViewById(R.id.text12);
		textView13 = (TextView) findViewById(R.id.text13);
		textView14 = (TextView) findViewById(R.id.text14);
	}

	private void showTextView() {
		setTextAnimation(textView1, time1);
		setTextAnimation(textView2, time2);
		setTextAnimation(textView3, time3);
		setTextAnimation(textView4, time4);
		setTextAnimation(textView5, time5);
		setTextAnimation(textView6, time6);
		setTextAnimation(textView7, time7);
		setTextAnimation(textView8, time8);
		setTextAnimation(textView9, time9);
		setTextAnimation(textView10, time10);
		setTextAnimation(textView11, time11);
		setTextAnimation(textView12, time12);
		setTextAnimation(textView13, time13);
		setTextAnimation(textView14, time14);
	}

	private void setTextAnimation(TextView textView, long offset) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(TEXT_DURATION_TIME);
		alphaAnimation.setStartOffset(offset);
		textView.setAnimation(alphaAnimation);
	}

	class disAppearView implements Runnable {

		@Override
		public void run() {
			head.setAlpha(0);
			textView1.setVisibility(View.INVISIBLE);
			textView2.setVisibility(View.INVISIBLE);
			textView3.setVisibility(View.INVISIBLE);
			textView4.setVisibility(View.INVISIBLE);
			textView5.setVisibility(View.INVISIBLE);
			textView6.setVisibility(View.INVISIBLE);
			textView7.setVisibility(View.INVISIBLE);
			textView8.setVisibility(View.INVISIBLE);
			textView9.setVisibility(View.INVISIBLE);
			textView10.setVisibility(View.INVISIBLE);
			textView11.setVisibility(View.INVISIBLE);
			textView12.setVisibility(View.INVISIBLE);
			textView13.setVisibility(View.INVISIBLE);
			textView14.setVisibility(View.INVISIBLE);
		}
	}

	class loadMainTabTask implements Runnable {

		@Override
		public void run() {
			Intent intent = new Intent(SplashActivity.this, MainActivity.class);
			startActivity(intent);
			finish();
		}
	}

}

package com.asd.littleprincesbeauty.activity;

import com.asd.littleprincesbeauty.R;

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

public class SplashActivity extends Activity {

	TextView textView1, textView2, textView3, textView4, textView5, textView6,
			textView7, textView8, textView9, textView10, textView11,
			textView12, textView13, textView14;

	ImageView head, logo;

	final static long DURATION_TIME = 3000;
	final static long CLOSE_DURATION_TIME = 1;

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

		new Handler().postDelayed(new disAppearView(), 49950);
		new Handler().postDelayed(new loadMainTabTask(), 500);

	}

	private void viewChange(View view) {
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.anim_set);
		animation.setStartOffset(50000);
		animation.setInterpolator(new LinearInterpolator());
		view.setAnimation(animation);
	}

	private void showHeadImage() {
		head.setImageResource(R.drawable.head);
		AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.0f, 0.5f);
		alphaAnimation1.setDuration(20000);
		alphaAnimation1.setStartOffset(30000);
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
		setTextAnimation(textView1, 2000);
		setTextAnimation(textView2, 5000);
		setTextAnimation(textView3, 8000);
		setTextAnimation(textView4, 11000);
		setTextAnimation(textView5, 14000);
		setTextAnimation(textView6, 17000);
		setTextAnimation(textView7, 20000);
		setTextAnimation(textView8, 23000);
		setTextAnimation(textView9, 26000);
		setTextAnimation(textView10, 29000);
		setTextAnimation(textView11, 32000);
		setTextAnimation(textView12, 35000);
		setTextAnimation(textView13, 38000);
		setTextAnimation(textView14, 41000);

	}

	private void setTextAnimation(TextView textView, long offset) {
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(DURATION_TIME);
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

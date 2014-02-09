package com.asd.littleprincesbeauty.activity;

import java.util.Calendar;
import java.util.Random;

import com.asd.littleprincesbeauty.R;
import com.asd.littleprincesbeauty.ui.Text_Rubbler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

public class RubblerActivity extends Activity {
	
	private Random random;

	private String[] prizeStrs = { "嘉嘉，亲我一下", "嘉嘉,抱我一下", "嘉嘉，不许生气", "嘉嘉，快来见我",
			"嘉嘉,带我吃好吃的","嘉嘉，唱歌首歌给我听","嘉嘉，今晚，hiahiahia~~~" };
	private String rubblerStr; 
	private Text_Rubbler text_Rubbler;
	private int num = 0;
	
	private TextView prizeTx1, prizeTx2, prizeTx3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.prize_show);
		// 设置的颜色必须要有透明度。
		text_Rubbler = (Text_Rubbler) findViewById(R.id.rubbler);
		
		random = new Random();
		rubblerStr = prizeStrs[random.nextInt(7)];
		text_Rubbler.setText(rubblerStr);
		
		prizeTx1 = (TextView) findViewById(R.id.prize_01);
		prizeTx2 = (TextView) findViewById(R.id.prize_02);
		prizeTx3 = (TextView) findViewById(R.id.prize_03);
		
		showPrize();
		
		
		text_Rubbler.beginRubbler(0XFFCECECE, 5, 1f, this, rubblerStr, num);

		

	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);

		// TODO Auto-generated method stub
		super.onBackPressed();
	}

	public void setSharedPrefences(Context ctx, String text, int num) {
		if(num == 3) {
			return;
		}
		SharedPreferences preferences = ctx.getSharedPreferences("PRIZE",0);
		
		num ++ ;
		String numStr = num + "";
		
		SharedPreferences.Editor editor = preferences.edit();
		editor.putInt("num", num);
		editor.putString("prize" + numStr, text);
		editor.putString("day", getToday());

		editor.commit();
	}
	
	
	
	private String getToday() {
		Calendar calendar = Calendar.getInstance();
		String day = Integer.toString(calendar.get(Calendar.DAY_OF_MONTH));

		return day;
	}

	private void showPrize() {
		SharedPreferences preferences = this.getSharedPreferences("PRIZE",
				MODE_PRIVATE);
		String today = preferences.getString("day", "");		
		
		if(today != "" && today.equalsIgnoreCase(getToday())) {
			num = preferences.getInt("num", 0);
			if(num == 3) {
				String prize1 = preferences.getString("prize1","");
				String prize2 = preferences.getString("prize2","");
				String prize3 = preferences.getString("prize3","");
				
				prizeTx1.setText(prize1);
				prizeTx2.setText(prize2);
				prizeTx3.setText(prize3);
			} else if(num == 2) {
				String prize1 = preferences.getString("prize1","");
				String prize2 = preferences.getString("prize2","");
				
				prizeTx1.setText(prize1);
				prizeTx2.setText(prize2);
			} else if(num == 1) {
				String prize1 = preferences.getString("prize1","");
				
				prizeTx1.setText(prize1);
			}

		}
		

	}
}

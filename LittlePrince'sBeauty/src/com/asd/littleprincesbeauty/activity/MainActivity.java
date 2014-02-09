package com.asd.littleprincesbeauty.activity;

import com.asd.littleprincesbeauty.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button btn_write;
	private Button btn_prize;
	private TextView music;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// imageButton = (ImageButton) findViewById(R.id.music);

		music = (TextView) findViewById(R.id.music);
		btn_write = (Button) findViewById(R.id.write);
		btn_prize = (Button) findViewById(R.id.prize);

		btn_write.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				write();
			}
		});

		music.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				playMusic();
			}
		});

		btn_prize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				prize();
			}
		});

	}

	public void write() {
		Intent intent = new Intent(this, WriteNote.class);
		startActivity(intent);
		finish();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// 播放音乐方法
	public void playMusic() {
		Intent intent = new Intent(this, PlayMusicActivity.class);
		startActivity(intent);
		finish();

	}

	public void prize() {
		Intent intent = new Intent(this, RubblerActivity.class);
		startActivity(intent);
		finish();
	}
}

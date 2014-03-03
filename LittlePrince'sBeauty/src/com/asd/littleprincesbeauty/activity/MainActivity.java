package com.asd.littleprincesbeauty.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.asd.littleprincesbeauty.R;

public class MainActivity extends Activity implements OnClickListener {

	private ImageView write;
	private ImageView prize;
	private ImageView paint;
	private ImageView music;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		music = (ImageView) findViewById(R.id.music);
		write = (ImageView) findViewById(R.id.write);
		prize = (ImageView) findViewById(R.id.prize);
		paint = (ImageView) findViewById(R.id.paint);

		music.setOnClickListener(this);
		write.setOnClickListener(this);
		prize.setOnClickListener(this);
		paint.setOnClickListener(this);

	}

	public void write() {
		Intent intent = new Intent(this, WriteNoteActivity.class);
		startActivity(intent);
		finish();

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

	public void showPaint() {
		Intent intent = new Intent(this, ImageGalleryActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.write:
			write();
			break;
		case R.id.music:
			playMusic();
			break;
		case R.id.prize:
			prize();
			break;
		case R.id.paint:
			showPaint();
			break;
		default:
			break;
		}

	}
}

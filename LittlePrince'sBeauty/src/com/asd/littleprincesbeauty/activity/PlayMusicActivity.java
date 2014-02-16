package com.asd.littleprincesbeauty.activity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import android.app.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import com.asd.littleprincesbeauty.R;
import com.asd.littleprincesbeauty.tools.LrcHandle;
import com.asd.littleprincesbeauty.ui.WordView;

public class PlayMusicActivity extends Activity implements OnCompletionListener {
	private List<Integer> mTimeList = new ArrayList<Integer>();
	private Handler handler;// handler对象
	private WordView mWordView;
	private MediaPlayer mp;// MediaPlayer对象
	private ImageButton playbtn = null;// 播放按钮

	// private TextView music_name = null;
	private static final String MUSIC_CURRENT = "com.music.currentTime";
	private static final String MUSIC_DURATION = "com.music.duration";
	private static final String MUSIC_NEXT = "com.music.duration";

	private static final String MUSIC_STAR = "com.music.star";
	private static final int PLAY = 1;// 定义播放状态
	private static final int PAUSE = 2;// 暂停状态

	private static final int STATE_PLAY = 1;// 播放状态设为1,表示播放状态
	private static final int STATE_PAUSE = 2;// 播放状态设为2，表示暂停状态
	private int flag = 2;// 标记

	LrcHandle lrcHandle;
	InputStream is;
	WordViewInvalidateThread thread;

	private boolean isWait = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.paly_music);

		// music_name = (TextView) findViewById(R.id.muisc_name);
		mWordView = (WordView) findViewById(R.id.lrc);// 歌词
		// music_name.setText("星星堆满天");

		thread = new WordViewInvalidateThread();
		ShowPlayBtn();// 显示或者说监视播放按钮事件
		initMediaPlayer();
		initHander();

	}

	private void initHander() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				handler.sendEmptyMessageDelayed(1, 100);// 发送空消息持续时间
			}
		};
		lrcHandle = new LrcHandle();
		lrcHandle.readLRC(is);
		mTimeList = lrcHandle.getTime();
		System.out.println("TimeList =============== " + mTimeList);
	}

	private void initMediaPlayer() {
		if (mp != null) {
			mp.reset();
			mp.release();
		}
		mp = new MediaPlayer();// 实例化MediaPlayer对象
		mp = MediaPlayer.create(this, R.raw.haishangirl);
		mp.setOnCompletionListener(this);// 设置下一首的监听
		is = getResources().openRawResource(R.raw.haishan);
	}

	// 显示各个按钮并做监视
	private void ShowPlayBtn() {
		playbtn = (ImageButton) findViewById(R.id.playBtn);
		playbtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (flag) {
				case STATE_PLAY:
					pause();
					break;

				case STATE_PAUSE:
					play();
					break;
				}

			}
		});

	}

	@Override
	protected void onStart() {
		super.onStart();
		setup();// 初始化
	}

	@Override
	protected void onStop() {
		super.onStop();
		// unregisterReceiver(musicreceiver);//停止界面时，反注册广播接收器
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			stop();
			Intent intent = new Intent(this, MainActivity.class);
			startActivity(intent);
		}
		finish();
		return true;
	}

	// 播放音乐
	protected void play() {
		System.out.println("播放音乐");
		flag = PLAY;
		System.out.println("开始播放音乐 : " + mp.getCurrentPosition());
		playbtn.setImageResource(R.drawable.pause_button);
		if (mp != null) {
			mp.start();
			if (!thread.isAlive() && !isWait) {
				thread.start();
			} else if (isWait) {
				System.out.println("叫醒");
				new Thread() {
					@Override
					public void run() {
						synchronized (thread) {
							thread.notify();
						}
					}
				};
				isWait = true;
			}
		}

	}

	// 暂停
	protected void pause() {
		System.out.println("暂停音乐");
		flag = PAUSE;
		playbtn.setImageResource(R.drawable.play_button);
		if (mp != null) {
			mp.pause();

			if (!isWait) {
				new Thread() {
					@Override
					public void run() {
						synchronized (thread) {
							try {
								thread.wait();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				};

				isWait = true;
				System.out.println("卡住了吗？");
			}

		}
		System.out.println("音乐已经停止");
	}

	// 停止播放音乐
	private void stop() {
		if (mp != null) {
			mp.stop();
			mp = null;
		}

		if (handler != null) {
			handler.removeMessages(1);
			handler = null;
		}
	}

	// 准备
	private void setup() {
		init();
	}

	// 初始化服务
	private void init() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(MUSIC_CURRENT);
		filter.addAction(MUSIC_DURATION);
		filter.addAction(MUSIC_NEXT);
		filter.addAction(MUSIC_STAR);
		// registerReceiver(musicreceiver, filter);
	}

	class WordViewInvalidateThread extends Thread {

		@Override
		public void run() {
			if (mp != null) {

				while (mp.isPlaying()) {
					final int current = mp.getCurrentPosition();

					for (int i = 0; i < mTimeList.size() - 1; i++) {

						System.out.println("time size" + mTimeList.size());
						System.out.println("current == " + current
								+ " || i == " + mTimeList.get(i)
								+ " || i+1 == " + mTimeList.get(i + 1));

						if (current >= mTimeList.get(i)
								&& current < mTimeList.get(i + 1)) {
							try {
								Thread.sleep(mTimeList.get(i + 1)
										- mTimeList.get(i));

								System.out.println(mTimeList.get(i + 1)
										- mTimeList.get(i));

							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
					if (handler != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								if (current < mTimeList.get(mTimeList.size() - 1)) {
									System.out.println("current ============ "
											+ current
											+ " || time ============= "
											+ mTimeList.get(mTimeList.size() - 1));
									mWordView.invalidate();
								}
							}
						});
					}
				}

			}

		}
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		stop();
		System.out.println("音乐播放结束");
	}

}

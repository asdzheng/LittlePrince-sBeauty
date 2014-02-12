package com.asd.littleprincesbeauty.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.asd.littleprincesbeauty.R;
import com.asd.littleprincesbeauty.tools.LrcHandle;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

public class PlayMusicService extends Service implements OnCompletionListener {

	private List<Integer> mTimeList = new ArrayList<Integer>();

	private static final int PLAYING = 1;// 定义该怎么对音乐操作的常量,如播放是1
	private static final int PAUSE = 2;// 暂停事件是2
	private static final int STOP = 3;// 停止事件是3
	private static final String MUSIC_CURRENT = "com.music.currentTime";
	private static final String MUSIC_NEXT = "com.music.next";
	private static final String MUSIC_STAR = "com.music.star";
	private MediaPlayer mp;// MediaPlayer对象
	private Handler handler;// handler对象
	private int currentTime;// 当前时间
	private String music_name;
	private InputStream is;

	@Override
	public void onCreate() {
		if (mp != null) {
			mp.reset();
			mp.release();
		}
		mp = new MediaPlayer();// 实例化MediaPlayer对象
		mp = MediaPlayer.create(this, R.raw.haishanggirl);
		mp.setOnCompletionListener(this);// 设置下一首的监听
		is = getResources().openRawResource(R.raw.haishanggirl);
	}

	@Override
	public void onDestroy() {
		if (mp != null) {
			stop();
		}
		if (handler != null) {
			handler.removeMessages(1);
			handler = null;
		}
	}

	/**
	 * 开启服务的方法
	 */
	@Override
	public void onStart(Intent intent, int startId) {
		System.out.println("开始服务");
		init();
		/**
		 * 开始播放/暂停、停止
		 */
		int op = intent.getIntExtra("op", -1);
		if (op != -1) {
			switch (op) {
			case PLAYING:
				Toast.makeText(this, "play", Toast.LENGTH_SHORT).show();
				play();
				break;
			case PAUSE:
				Toast.makeText(this, "pause", Toast.LENGTH_SHORT).show();
				pause();
				break;
			case STOP:
				stop();
				break;

			}
		}

	}

	// 播放音乐
	private void play() {
		if (mp != null) {
			mp.start();

			/*LrcHandle lrcHandle = new LrcHandle();
			lrcHandle.readLRC(is);
			mTimeList = lrcHandle.getTime();
			new Thread(new Runnable() {
				int i = 0;

				@Override
				public void run() {
					while (mp.isPlaying()) {
						handler.post(new Runnable() {

							@Override
							public void run() {
								final Intent intent = new Intent();
								intent.setAction(MUSIC_STAR);
								sendBroadcast(intent);
							}
						});
						try {
							Thread.sleep(mTimeList.get(i + 1)
									- mTimeList.get(i));
						} catch (InterruptedException e) {
						}
						i++;
						if (i == mTimeList.size() - 1) {
							mp.stop();
							break;
						}
					}
				}
			}).start();
*/
		}
		System.out.println("开始播放音乐");
	}

	// 暂停音乐
	private void pause() {
		if (mp != null) {
			mp.pause();
		}
		System.out.println("音乐已经停止");
	}

	// 停止音乐
	private void stop() {
		if (mp != null) {
			mp.stop();
		}

		if (handler != null) {
			handler.removeMessages(1);
			handler = null;
		}

	}

	/**
	 * 初始化服务
	 */
	private void init() {
		final Intent intent = new Intent();
		intent.setAction(MUSIC_CURRENT);
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					currentTime = mp.getCurrentPosition();
					intent.putExtra("currentTime", currentTime);
					sendBroadcast(intent);

				}
				handler.sendEmptyMessageDelayed(1, 600);// 发送空消息持续时间
			}
		};

	}

	@Override
	public void onCompletion(MediaPlayer arg0) {
		stop();
		Intent intent = new Intent();
		intent.setAction(MUSIC_NEXT);
		sendBroadcast(intent);
		System.out.println("音乐播放结束");
		stopSelf();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}

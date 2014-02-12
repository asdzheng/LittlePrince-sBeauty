package com.asd.littleprincesbeauty.tools;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcHandle {
	private List<String> mWords = new ArrayList<String>();
	private List<Integer> mTimeList = new ArrayList<Integer>();

	// 处理歌词文件
	public void readLRC(InputStream is) {

		try {
			InputStreamReader inputStreamReader = new InputStreamReader(is,
					"utf-8");
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			String s = "";
			while ((s = bufferedReader.readLine()) != null) {
				addTimeToList(s);
System.out.println("sss===================" + s);
				if ((s.indexOf("[ar:") != -1) || (s.indexOf("[ti:") != -1)
						|| (s.indexOf("[by:") != -1)) {
					s = s.substring(s.indexOf(":") + 1, s.indexOf("]"));
				} else {
					String ss = s.substring(s.indexOf("["), s.indexOf("]") + 1);
					s = s.replace(ss, "");
				}
				mWords.add(s);
			}

			bufferedReader.close();
			inputStreamReader.close();
			is.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			mWords.add("没有歌词文件，赶紧去下载");
		} catch (IOException e) {
			e.printStackTrace();
			mWords.add("没有读取到歌词");
		}
	}

	public List<String> getWords() {
		return mWords;
	}

	public List<Integer> getTime() {
		return mTimeList;
	}

	// 分离出时间
	private int timeHandler(String string) {
		string = string.replace(".", ":");
		String timeData[] = string.split(":");
		// 分离出分、秒并转换为整型
		int minute = Integer.parseInt(timeData[0]);
		int second = Integer.parseInt(timeData[1]);
		int millisecond = Integer.parseInt(timeData[2]);

		// 计算上一行与下一行的时间转换为毫秒数
		int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;

		return currentTime;
	}

	private void addTimeToList(String string) {
		Matcher matcher = Pattern.compile(
				"\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\]").matcher(string);
		if (matcher.find()) {
			String str = matcher.group();
			mTimeList.add(timeHandler(str.substring(1, str.length() - 1)));
		}

	}
}

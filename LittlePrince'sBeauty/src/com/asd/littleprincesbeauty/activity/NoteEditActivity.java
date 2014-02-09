package com.asd.littleprincesbeauty.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.asd.littleprincesbeauty.R;
import com.asd.littleprincesbeauty.data.tool.DataUtils;
import com.asd.littleprincesbeauty.data.tool.Notes;
import com.asd.littleprincesbeauty.data.tool.Notes.TextNote;
import com.asd.littleprincesbeauty.model.WorkingNote;
import com.asd.littleprincesbeauty.model.WorkingNote.NoteSettingChangedListener;
import com.asd.littleprincesbeauty.tools.ResourceParser;
import com.asd.littleprincesbeauty.tools.ResourceParser.TextAppearanceResources;
import com.asd.littleprincesbeauty.ui.NoteEditText;
import com.asd.littleprincesbeauty.ui.NoteEditText.OnTextViewChangeListener;

import android.app.Activity;
import android.app.SearchManager;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class NoteEditActivity extends Activity implements OnClickListener,
		OnTextViewChangeListener, NoteSettingChangedListener {
	private class HeadViewHolder {
		public TextView tvModified;

		public ImageView ivAlertIcon;

		public TextView tvAlertDate;

		public ImageView ibSetBgColor;
	}

	private static final Map<Integer, Integer> sBgSelectorBtnsMap = new HashMap<Integer, Integer>();
	static {
		sBgSelectorBtnsMap.put(R.id.iv_bg_yellow, ResourceParser.YELLOW);
		sBgSelectorBtnsMap.put(R.id.iv_bg_red, ResourceParser.RED);
		sBgSelectorBtnsMap.put(R.id.iv_bg_blue, ResourceParser.BLUE);
		sBgSelectorBtnsMap.put(R.id.iv_bg_green, ResourceParser.GREEN);
		sBgSelectorBtnsMap.put(R.id.iv_bg_white, ResourceParser.WHITE);
	}

	private static final Map<Integer, Integer> sBgSelectorSelectionMap = new HashMap<Integer, Integer>();
	static {
		sBgSelectorSelectionMap.put(ResourceParser.YELLOW,
				R.id.iv_bg_yellow_select);
		sBgSelectorSelectionMap.put(ResourceParser.RED, R.id.iv_bg_red_select);
		sBgSelectorSelectionMap
				.put(ResourceParser.BLUE, R.id.iv_bg_blue_select);
		sBgSelectorSelectionMap.put(ResourceParser.GREEN,
				R.id.iv_bg_green_select);
		sBgSelectorSelectionMap.put(ResourceParser.WHITE,
				R.id.iv_bg_white_select);
	}

	private static final Map<Integer, Integer> sFontSizeBtnsMap = new HashMap<Integer, Integer>();
	static {
		sFontSizeBtnsMap.put(R.id.ll_font_large, ResourceParser.TEXT_LARGE);
		sFontSizeBtnsMap.put(R.id.ll_font_small, ResourceParser.TEXT_SMALL);
		sFontSizeBtnsMap.put(R.id.ll_font_normal, ResourceParser.TEXT_MEDIUM);
		sFontSizeBtnsMap.put(R.id.ll_font_super, ResourceParser.TEXT_SUPER);
	}

	private static final Map<Integer, Integer> sFontSelectorSelectionMap = new HashMap<Integer, Integer>();
	static {
		sFontSelectorSelectionMap.put(ResourceParser.TEXT_LARGE,
				R.id.iv_large_select);
		sFontSelectorSelectionMap.put(ResourceParser.TEXT_SMALL,
				R.id.iv_small_select);
		sFontSelectorSelectionMap.put(ResourceParser.TEXT_MEDIUM,
				R.id.iv_medium_select);
		sFontSelectorSelectionMap.put(ResourceParser.TEXT_SUPER,
				R.id.iv_super_select);
	}

	private static final String TAG = "NoteEditActivity";

	private HeadViewHolder mNoteHeaderHolder;

	private View mHeadViewPanel;

	private View mNoteBgColorSelector;

	private View mFontSizeSelector;

	private EditText mNoteEditor;

	private View mNoteEditorPanel;

	private WorkingNote mWorkingNote;

	private SharedPreferences mSharedPrefs;
	private int mFontSizeId;

	private static final String PREFERENCE_FONT_SIZE = "pref_font_size";

	public static final String TAG_CHECKED = String.valueOf('\u221A');
	public static final String TAG_UNCHECKED = String.valueOf('\u25A1');

	private LinearLayout mEditTextList;

	private String mUserQuery;
	private Pattern mPattern;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.note_edit);

		if (savedInstanceState == null && !initActivityState(getIntent())) {
			finish();
			return;
		}
		initResources();
	}

	/**
	 * Current activity may be killed when the memory is low. Once it is killed,
	 * for another time user load this activity, we should restore the former
	 * state
	 */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(Intent.EXTRA_UID)) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.putExtra(Intent.EXTRA_UID,
					savedInstanceState.getLong(Intent.EXTRA_UID));
			if (!initActivityState(intent)) {
				finish();
				return;
			}
			Log.d(TAG, "Restoring from killed activity");
		}
	}

	private boolean initActivityState(Intent intent) {
		/**
		 * If the user specified the {@link Intent#ACTION_VIEW} but not provided
		 * with id, then jump to the NotesListActivity
		 */

		mWorkingNote = null;

		if (TextUtils.equals(Intent.ACTION_VIEW, intent.getAction())) {
			long noteId = intent.getLongExtra(Intent.EXTRA_UID, 0);
			mUserQuery = "";

			/**
			 * Starting from the searched result
			 */
			if (intent.hasExtra(SearchManager.EXTRA_DATA_KEY)) {
				noteId = Long.parseLong(intent
						.getStringExtra(SearchManager.EXTRA_DATA_KEY));
				mUserQuery = intent.getStringExtra(SearchManager.USER_QUERY);
			}

			if (!DataUtils.visibleInNoteDatabase(getContentResolver(), noteId,
					Notes.TYPE_NOTE)) {
				Intent jump = new Intent(this, WriteNote.class);
				startActivity(jump);
				showToast(R.string.error_note_not_exist);
				finish();
				return false;
			} else {
				mWorkingNote = WorkingNote.load(this, noteId);
				if (mWorkingNote == null) {
					Log.e(TAG, "load note failed with note id" + noteId);
					finish();
					return false;
				}
			}
			getWindow()
					.setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
									| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		} else if (TextUtils.equals(Intent.ACTION_INSERT_OR_EDIT,
				intent.getAction())) {
			// New note
			int widgetId = intent.getIntExtra(Notes.INTENT_EXTRA_WIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
			int widgetType = intent.getIntExtra(Notes.INTENT_EXTRA_WIDGET_TYPE,
					Notes.TYPE_WIDGET_INVALIDE);
			int bgResId = intent.getIntExtra(Notes.INTENT_EXTRA_BACKGROUND_ID,
					ResourceParser.getDefaultBgId(this));

			// Parse call-record note
			String phoneNumber = intent
					.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			long callDate = intent
					.getLongExtra(Notes.INTENT_EXTRA_CALL_DATE, 0);
			if (callDate != 0 && phoneNumber != null) {
				if (TextUtils.isEmpty(phoneNumber)) {
					Log.w(TAG, "The call record number is null");
				}
				long noteId = 0;
				if ((noteId = DataUtils.getNoteIdByPhoneNumberAndCallDate(
						getContentResolver(), phoneNumber, callDate)) > 0) {
					mWorkingNote = WorkingNote.load(this, noteId);
					if (mWorkingNote == null) {
						Log.e(TAG, "load call note failed with note id"
								+ noteId);
						finish();
						return false;
					}
				} else {
					mWorkingNote = WorkingNote.createEmptyNote(this, widgetId,
							widgetType, bgResId);
					mWorkingNote.convertToCallNote(phoneNumber, callDate);
				}
			} else {
				mWorkingNote = WorkingNote.createEmptyNote(this, widgetId,
						widgetType, bgResId);
			}

			getWindow()
					.setSoftInputMode(
							WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
									| WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		} else {
			Log.e(TAG, "Intent not specified action, should not support");
			finish();
			return false;
		}
		mWorkingNote.setOnSettingStatusChangedListener(this);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		initNoteScreen();
	}

	private void initNoteScreen() {
		mNoteEditor.setTextAppearance(this,
				TextAppearanceResources.getTexAppearanceResource(mFontSizeId));
		if (mWorkingNote.getCheckListMode() == TextNote.MODE_CHECK_LIST) {
			switchToListMode(mWorkingNote.getContent());
		} else {
			mNoteEditor.setText(getHighlightQueryResult(
					mWorkingNote.getContent(), mUserQuery));
			mNoteEditor.setSelection(mNoteEditor.getText().length());
		}

		mHeadViewPanel.setBackgroundResource(mWorkingNote.getTitleBgResId());
		mNoteEditorPanel.setBackgroundResource(mWorkingNote.getBgColorResId());

		mNoteHeaderHolder.tvModified.setText(DateUtils.formatDateTime(this,
				mWorkingNote.getModifiedDate(), DateUtils.FORMAT_SHOW_DATE
						| DateUtils.FORMAT_NUMERIC_DATE
						| DateUtils.FORMAT_SHOW_TIME
						| DateUtils.FORMAT_SHOW_YEAR));

		/**
		 * TODO: Add the menu for setting alert. Currently disable it because
		 * the DateTimePicker is not ready
		 */
		showAlertHeader();
	}

	private void showAlertHeader() {
		if (mWorkingNote.hasClockAlert()) {
			long time = System.currentTimeMillis();
			if (time > mWorkingNote.getAlertDate()) {
				mNoteHeaderHolder.tvAlertDate
						.setText(R.string.note_alert_expired);
			} else {
				mNoteHeaderHolder.tvAlertDate.setText(DateUtils
						.getRelativeTimeSpanString(mWorkingNote.getAlertDate(),
								time, DateUtils.MINUTE_IN_MILLIS));
			}
			mNoteHeaderHolder.tvAlertDate.setVisibility(View.VISIBLE);
			mNoteHeaderHolder.ivAlertIcon.setVisibility(View.VISIBLE);
		} else {
			mNoteHeaderHolder.tvAlertDate.setVisibility(View.GONE);
			mNoteHeaderHolder.ivAlertIcon.setVisibility(View.GONE);
		}
		;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		initActivityState(intent);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		/**
		 * For new note without note id, we should firstly save it to generate a
		 * id. If the editing note is not worth saving, there is no id which is
		 * equivalent to create new note
		 */
		if (!mWorkingNote.existInDatabase()) {
			saveNote();
		}
		outState.putLong(Intent.EXTRA_UID, mWorkingNote.getNoteId());
		Log.d(TAG, "Save working note id: " + mWorkingNote.getNoteId()
				+ " onSaveInstanceState");
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (mNoteBgColorSelector.getVisibility() == View.VISIBLE
				&& !inRangeOfView(mNoteBgColorSelector, ev)) {
			mNoteBgColorSelector.setVisibility(View.GONE);
			return true;
		}

		if (mFontSizeSelector.getVisibility() == View.VISIBLE
				&& !inRangeOfView(mFontSizeSelector, ev)) {
			mFontSizeSelector.setVisibility(View.GONE);
			return true;
		}
		return super.dispatchTouchEvent(ev);
	}

	private boolean inRangeOfView(View view, MotionEvent ev) {
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		int x = location[0];
		int y = location[1];
		if (ev.getX() < x || ev.getX() > (x + view.getWidth()) || ev.getY() < y
				|| ev.getY() > (y + view.getHeight())) {
			return false;
		}
		return true;
	}

	private void initResources() {
		mHeadViewPanel = findViewById(R.id.note_title);
		mNoteHeaderHolder = new HeadViewHolder();
		mNoteHeaderHolder.tvModified = (TextView) findViewById(R.id.tv_modified_date);
		mNoteHeaderHolder.ivAlertIcon = (ImageView) findViewById(R.id.iv_alert_icon);
		mNoteHeaderHolder.tvAlertDate = (TextView) findViewById(R.id.tv_alert_date);
		mNoteHeaderHolder.ibSetBgColor = (ImageView) findViewById(R.id.btn_set_bg_color);
		mNoteHeaderHolder.ibSetBgColor.setOnClickListener(this);
		mNoteEditor = (EditText) findViewById(R.id.note_edit_view);
		mNoteEditorPanel = findViewById(R.id.sv_note_edit);
		mNoteBgColorSelector = findViewById(R.id.note_bg_color_selector);
		for (int id : sBgSelectorBtnsMap.keySet()) {
			ImageView iv = (ImageView) findViewById(id);
			iv.setOnClickListener(this);
		}

		mFontSizeSelector = findViewById(R.id.font_size_selector);
		for (int id : sFontSizeBtnsMap.keySet()) {
			View view = findViewById(id);
			view.setOnClickListener(this);
		}
		;
		mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		mFontSizeId = mSharedPrefs.getInt(PREFERENCE_FONT_SIZE,
				ResourceParser.BG_DEFAULT_FONT_SIZE);
		/**
		 * HACKME: Fix bug of store the resource id in shared preference. The id
		 * may larger than the length of resources, in this case, return the
		 * {@link ResourceParser#BG_DEFAULT_FONT_SIZE}
		 */
		if (mFontSizeId >= TextAppearanceResources.getResourcesSize()) {
			mFontSizeId = ResourceParser.BG_DEFAULT_FONT_SIZE;
		}
		mEditTextList = (LinearLayout) findViewById(R.id.note_edit_list);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (saveNote()) {
			Log.d(TAG, "Note data was saved with length:"
					+ mWorkingNote.getContent().length());
		}
		clearSettingState();
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_set_bg_color) {
			mNoteBgColorSelector.setVisibility(View.VISIBLE);
			findViewById(
					sBgSelectorSelectionMap.get(mWorkingNote.getBgColorId()))
					.setVisibility(-View.VISIBLE);
		} else if (sBgSelectorBtnsMap.containsKey(id)) {
			findViewById(
					sBgSelectorSelectionMap.get(mWorkingNote.getBgColorId()))
					.setVisibility(View.GONE);
			mWorkingNote.setBgColorId(sBgSelectorBtnsMap.get(id));
			mNoteBgColorSelector.setVisibility(View.GONE);
		} else if (sFontSizeBtnsMap.containsKey(id)) {
			findViewById(sFontSelectorSelectionMap.get(mFontSizeId))
					.setVisibility(View.GONE);
			mFontSizeId = sFontSizeBtnsMap.get(id);
			mSharedPrefs.edit().putInt(PREFERENCE_FONT_SIZE, mFontSizeId)
					.commit();
			findViewById(sFontSelectorSelectionMap.get(mFontSizeId))
					.setVisibility(View.VISIBLE);
			if (mWorkingNote.getCheckListMode() == TextNote.MODE_CHECK_LIST) {
				getWorkingText();
				switchToListMode(mWorkingNote.getContent());
			} else {
				mNoteEditor.setTextAppearance(this, TextAppearanceResources
						.getTexAppearanceResource(mFontSizeId));
			}
			mFontSizeSelector.setVisibility(View.GONE);
		}
	}

	@Override
	public void onBackPressed() {
		if (clearSettingState()) {
			System.out.println("clear Setting is true!!!");
			return;
		}

		saveNote();
		super.onBackPressed();
	}

	private boolean clearSettingState() {
		if (mNoteBgColorSelector.getVisibility() == View.VISIBLE) {
			mNoteBgColorSelector.setVisibility(View.GONE);
			return true;
		} else if (mFontSizeSelector.getVisibility() == View.VISIBLE) {
			mFontSizeSelector.setVisibility(View.GONE);
			return true;
		}
		return false;
	}

	@Override
	public void onBackgroundColorChanged() {
		findViewById(sBgSelectorSelectionMap.get(mWorkingNote.getBgColorId()))
				.setVisibility(View.VISIBLE);
		mNoteEditorPanel.setBackgroundResource(mWorkingNote.getBgColorResId());
		mHeadViewPanel.setBackgroundResource(mWorkingNote.getTitleBgResId());
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if (isFinishing()) {
			return true;
		}
		clearSettingState();
		menu.clear();

		return true;
	}

	@Override
	public void onEditTextDelete(int index, String text) {
		int childCount = mEditTextList.getChildCount();
		if (childCount == 1) {
			return;
		}

		for (int i = index + 1; i < childCount; i++) {
			((NoteEditText) mEditTextList.getChildAt(i).findViewById(
					R.id.et_edit_text)).setIndex(i - 1);
		}

		mEditTextList.removeViewAt(index);
		NoteEditText edit = null;
		if (index == 0) {
			edit = (NoteEditText) mEditTextList.getChildAt(0).findViewById(
					R.id.et_edit_text);
		} else {
			edit = (NoteEditText) mEditTextList.getChildAt(index - 1)
					.findViewById(R.id.et_edit_text);
		}
		int length = edit.length();
		edit.append(text);
		edit.requestFocus();
		edit.setSelection(length);
	}

	@Override
	public void onEditTextEnter(int index, String text) {
		/**
		 * Should not happen, check for debug
		 */
		if (index > mEditTextList.getChildCount()) {
			Log.e(TAG,
					"Index out of mEditTextList boundrary, should not happen");
		}

		View view = getListItem(text, index);
		mEditTextList.addView(view, index);
		NoteEditText edit = (NoteEditText) view.findViewById(R.id.et_edit_text);
		edit.requestFocus();
		edit.setSelection(0);
		for (int i = index + 1; i < mEditTextList.getChildCount(); i++) {
			((NoteEditText) mEditTextList.getChildAt(i).findViewById(
					R.id.et_edit_text)).setIndex(i);
		}
	}

	private void switchToListMode(String text) {
		mEditTextList.removeAllViews();
		String[] items = text.split("\n");
		int index = 0;
		for (String item : items) {
			if (!TextUtils.isEmpty(item)) {
				mEditTextList.addView(getListItem(item, index));
				index++;
			}
		}
		mEditTextList.addView(getListItem("", index));
		mEditTextList.getChildAt(index).findViewById(R.id.et_edit_text)
				.requestFocus();

		mNoteEditor.setVisibility(View.GONE);
		mEditTextList.setVisibility(View.VISIBLE);
	}

	private Spannable getHighlightQueryResult(String fullText, String userQuery) {
		SpannableString spannable = new SpannableString(fullText == null ? ""
				: fullText);
		if (!TextUtils.isEmpty(userQuery)) {
			mPattern = Pattern.compile(userQuery);
			Matcher m = mPattern.matcher(fullText);
			int start = 0;
			while (m.find(start)) {
				spannable.setSpan(new BackgroundColorSpan(this.getResources()
						.getColor(R.color.user_query_highlight)), m.start(), m
						.end(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
				start = m.end();
			}
		}
		return spannable;
	}

	private View getListItem(String item, int index) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.note_edit_list_item, null);
		final NoteEditText edit = (NoteEditText) view
				.findViewById(R.id.et_edit_text);
		edit.setTextAppearance(this,
				TextAppearanceResources.getTexAppearanceResource(mFontSizeId));
		CheckBox cb = ((CheckBox) view.findViewById(R.id.cb_edit_item));
		cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					edit.setPaintFlags(edit.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
				} else {
					edit.setPaintFlags(Paint.ANTI_ALIAS_FLAG
							| Paint.DEV_KERN_TEXT_FLAG);
				}
			}
		});

		if (item.startsWith(TAG_CHECKED)) {
			cb.setChecked(true);
			edit.setPaintFlags(edit.getPaintFlags()
					| Paint.STRIKE_THRU_TEXT_FLAG);
			item = item.substring(TAG_CHECKED.length(), item.length()).trim();
		} else if (item.startsWith(TAG_UNCHECKED)) {
			cb.setChecked(false);
			edit.setPaintFlags(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);
			item = item.substring(TAG_UNCHECKED.length(), item.length()).trim();
		}

		edit.setOnTextViewChangeListener(this);
		edit.setIndex(index);
		edit.setText(getHighlightQueryResult(item, mUserQuery));
		return view;
	}

	@Override
	public void onTextChange(int index, boolean hasText) {
		if (index >= mEditTextList.getChildCount()) {
			Log.e(TAG, "Wrong index, should not happen");
			return;
		}
		if (hasText) {
			mEditTextList.getChildAt(index).findViewById(R.id.cb_edit_item)
					.setVisibility(View.VISIBLE);
		} else {
			mEditTextList.getChildAt(index).findViewById(R.id.cb_edit_item)
					.setVisibility(View.GONE);
		}
	}

	@Override
	public void onCheckListModeChanged(int oldMode, int newMode) {
		if (newMode == TextNote.MODE_CHECK_LIST) {
			switchToListMode(mNoteEditor.getText().toString());
		} else {
			if (!getWorkingText()) {
				mWorkingNote.setWorkingText(mWorkingNote.getContent().replace(
						TAG_UNCHECKED + " ", ""));
			}
			mNoteEditor.setText(getHighlightQueryResult(
					mWorkingNote.getContent(), mUserQuery));
			mEditTextList.setVisibility(View.GONE);
			mNoteEditor.setVisibility(View.VISIBLE);
		}
	}

	private boolean getWorkingText() {
		boolean hasChecked = false;
		if (mWorkingNote.getCheckListMode() == TextNote.MODE_CHECK_LIST) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mEditTextList.getChildCount(); i++) {
				View view = mEditTextList.getChildAt(i);
				NoteEditText edit = (NoteEditText) view
						.findViewById(R.id.et_edit_text);
				if (!TextUtils.isEmpty(edit.getText())) {
					if (((CheckBox) view.findViewById(R.id.cb_edit_item))
							.isChecked()) {
						sb.append(TAG_CHECKED).append(" ")
								.append(edit.getText()).append("\n");
						hasChecked = true;
					} else {
						sb.append(TAG_UNCHECKED).append(" ")
								.append(edit.getText()).append("\n");
					}
				}
			}
			mWorkingNote.setWorkingText(sb.toString());
		} else {
			mWorkingNote.setWorkingText(mNoteEditor.getText().toString());
		}
		return hasChecked;
	}

	private boolean saveNote() {
		getWorkingText();
		boolean saved = mWorkingNote.saveNote();
		System.out.println("mWorkingNote.saveNote is  === " + saved);

		if (saved) {
			/**
			 * There are two modes from List view to edit view, open one note,
			 * create/edit a node. Opening node requires to the original
			 * position in the list when back from edit view, while creating a
			 * new node requires to the top of the list. This code
			 * {@link #RESULT_OK} is used to identify the create/edit state
			 */
			setResult(RESULT_OK);
		}
		return saved;
	}

	@Override
	public void onWidgetChanged() {

	}

	@Override
	public void onClockAlertChanged(long date, boolean set) {

	}

	private void showToast(int resId) {
		showToast(resId, Toast.LENGTH_SHORT);
	}

	private void showToast(int resId, int duration) {
		Toast.makeText(this, resId, duration).show();
	}
}

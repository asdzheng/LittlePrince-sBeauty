package com.asd.littleprincesbeauty.activity;

import java.util.ArrayList;

import com.asd.littleprincesbeauty.R;
import com.asd.littleprincesbeauty.data.tool.DataUtils;
import com.asd.littleprincesbeauty.data.tool.Notes;
import com.asd.littleprincesbeauty.data.tool.Notes.NoteColumns;
import com.asd.littleprincesbeauty.ui.NoteItemData;
import com.asd.littleprincesbeauty.ui.NotesListAdapter;
import com.asd.littleprincesbeauty.ui.NotesListItem;

import android.app.Activity;
import android.app.AlertDialog;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class WriteNote extends Activity implements OnClickListener,
		OnItemLongClickListener {
	private Button mAddNewNote;

	private static final int FOLDER_NOTE_LIST_QUERY_TOKEN = 0;

	private static final int FOLDER_LIST_QUERY_TOKEN = 1;

	private enum ListEditState {
		NOTE_LIST, SUB_FOLDER, CALL_RECORD_FOLDER
	};

	private ListEditState mState;

	private BackgroundQueryHandler mBackgroundQueryHandler;

	private NotesListAdapter mNotesListAdapter;

	private ListView mNotesListView;

	private long mCurrentFolderId;

	private ContentResolver mContentResolver;

	private static final String TAG = "NotesListActivity";

	public static final int NOTES_LISTVIEW_SCROLL_RATE = 30;

	private NoteItemData mFocusNoteDataItem;

	private static final String NORMAL_SELECTION = NoteColumns.PARENT_ID + "=?";

	private static final String ROOT_FOLDER_SELECTION = "(" + NoteColumns.TYPE
			+ "<>" + Notes.TYPE_SYSTEM + " AND " + NoteColumns.PARENT_ID
			+ "=?)" + " OR (" + NoteColumns.ID + "="
			+ Notes.ID_CALL_RECORD_FOLDER + " AND " + NoteColumns.NOTES_COUNT
			+ ">0)";

	private AlertDialog menuDialog;

	private final static int REQUEST_CODE_OPEN_NODE = 102;
	private final static int REQUEST_CODE_NEW_NODE = 103;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_list);

		initResources();

	}

	private void initResources() {

		mContentResolver = this.getContentResolver();

		mBackgroundQueryHandler = new BackgroundQueryHandler(
				this.getContentResolver());

		mAddNewNote = (Button) findViewById(R.id.btn_new_note);
		mAddNewNote.setOnClickListener(this);

		mNotesListView = (ListView) findViewById(R.id.notes_list);

		mNotesListView.setOnItemClickListener(new OnListItemClickListener());
		mNotesListView.setOnItemLongClickListener(this);
		mNotesListAdapter = new NotesListAdapter(this);
		mNotesListView.setAdapter(mNotesListAdapter);

		mState = ListEditState.NOTE_LIST;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_new_note:
			createNewNote();
			break;
		default:
			break;
		}
	};

	private void createNewNote() {
		Intent intent = new Intent(this, NoteEditActivity.class);
		intent.setAction(Intent.ACTION_INSERT_OR_EDIT);
		// intent.putExtra(Notes.INTENT_EXTRA_FOLDER_ID, mCurrentFolderId);
		this.startActivityForResult(intent, REQUEST_CODE_NEW_NODE);
	}

	private final class BackgroundQueryHandler extends AsyncQueryHandler {
		public BackgroundQueryHandler(ContentResolver contentResolver) {
			super(contentResolver);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
			switch (token) {
			case FOLDER_NOTE_LIST_QUERY_TOKEN:
				mNotesListAdapter.changeCursor(cursor);
				break;
			case FOLDER_LIST_QUERY_TOKEN:
				if (cursor != null && cursor.getCount() > 0) {
				} else {
					Log.e(TAG, "Query folder failed");
				}
				break;
			default:
				return;
			}
		}
	}

	private class OnListItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (view instanceof NotesListItem) {
				NoteItemData item = ((NotesListItem) view).getItemData();
				if (mNotesListAdapter.isInChoiceMode()) {
					if (item.getType() == Notes.TYPE_NOTE) {
						position = position
								- mNotesListView.getHeaderViewsCount();
					}
					return;
				}

				switch (mState) {
				case NOTE_LIST:
					if (item.getType() == Notes.TYPE_NOTE) {
						System.out.println("NOTE_LIST ===="
								+ item.getCallName());
						openNode(item);
					} else {
						Log.e(TAG, "Wrong note type in NOTE_LIST");
					}
					break;
				case CALL_RECORD_FOLDER:
					if (item.getType() == Notes.TYPE_NOTE) {
						openNode(item);
					} else {
						Log.e(TAG, "Wrong note type in SUB_FOLDER");
					}
					break;
				default:
					break;
				}
			}
		}

	}

	@Override
	protected void onStart() {
		startAsyncNotesListQuery();
		super.onStart();
	}

	private void startAsyncNotesListQuery() {
		String selection = (mCurrentFolderId == Notes.ID_ROOT_FOLDER) ? ROOT_FOLDER_SELECTION
				: NORMAL_SELECTION;
		mBackgroundQueryHandler.startQuery(FOLDER_NOTE_LIST_QUERY_TOKEN, null,
				Notes.CONTENT_NOTE_URI, NoteItemData.PROJECTION, selection,
				new String[] { String.valueOf(mCurrentFolderId) },
				NoteColumns.TYPE + " DESC," + NoteColumns.MODIFIED_DATE
						+ " DESC");
	}

	private void openNode(NoteItemData data) {
		Intent intent = new Intent(this, NoteEditActivity.class);
		intent.setAction(Intent.ACTION_VIEW);
		intent.putExtra(Intent.EXTRA_UID, data.getId());
		this.startActivityForResult(intent, REQUEST_CODE_OPEN_NODE);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		if (arg1 instanceof NotesListItem) {
			mFocusNoteDataItem = ((NotesListItem) arg1).getItemData();
			if (mFocusNoteDataItem.getType() == Notes.TYPE_NOTE
					&& !mNotesListAdapter.isInChoiceMode()) {
				initMenuDialog();
				menuDialog.show();

			}
		}
		return false;
	}

	public void initMenuDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		System.out.println("getSelectedItemIds() =================  "
				+ mNotesListAdapter.getSelectedItemIds());

		builder.setNegativeButton("取消", null);

		View view = LayoutInflater.from(this).inflate(R.layout.choice_dialog,
				null);
		Button delete = (Button) view.findViewById(R.id.delete_btn);
		Button send = (Button) view.findViewById(R.id.send_btn);

		delete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				confirm();

			}
		});

		send.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendMessage(mFocusNoteDataItem.getSnippet(), "13790765362");
				menuDialog.cancel();
			}
		});

		builder.setView(view);
		menuDialog = builder.create();
	}

	public void sendMessage(String msg, String phoneNumber) {
		SmsManager manager = SmsManager.getDefault();
		ArrayList<String> list = manager.divideMessage(msg); // 因为一条短信有字数限制，因此要将长短信拆分
		for (String text : list) {
			manager.sendTextMessage(phoneNumber, null, text, null, null);
		}
		Toast.makeText(getApplicationContext(), "分享成功", Toast.LENGTH_SHORT)
				.show();
	}

	public void confirm() {
		menuDialog.cancel();
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("确认删除文件");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (DataUtils.batchDeleteNotes(mContentResolver,
						mFocusNoteDataItem.getFocusId())) {
				} else {
					Log.e(TAG, "Delete notes error, should not happens");
				}
			}
		});
		builder.setNegativeButton("取消", null);
		builder.show();

	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
		super.onBackPressed();
	}

}

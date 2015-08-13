package com.github.sachil.uplayer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.MediaStore;

public class DatabaseHelper extends SQLiteOpenHelper {
	public static final String ITEM_PARENT_ID = "_parent_id";
	public static final String ITEM_URI = "uri";
	public static final String AUDIO_THUMB = "thumb";
	private static final String TAG = DatabaseHelper.class.getSimpleName();
	private static final String DATABASE_NAME = "Uplayer.db";
	private static final int DATABASE_VERSION = 1;

	private static DatabaseHelper HELPER = null;
	private String mTableName = null;

	private DatabaseHelper(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static DatabaseHelper newInstance(Context context) {

		if (HELPER == null)
			HELPER = new DatabaseHelper(context);
		return HELPER;
	}

	public void setTableName(String tableName) {

		mTableName = tableName;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String request = "CREATE TABLE IF NOT EXISTS " + mTableName + "("
				+ MediaStore.MediaColumns._ID + " TEXT, "
				+ ITEM_PARENT_ID + " TEXT, "
				+ MediaStore.MediaColumns.TITLE + " TEXT, "
				+ MediaStore.Audio.AudioColumns.ARTIST + " TEXT, "
				+ MediaStore.Audio.AudioColumns.ALBUM + " TEXT, "
				+ AUDIO_THUMB + " TEXT, "
				+ ITEM_URI + " TEXT, "
				+ MediaStore.Audio.AudioColumns.DURATION + " TEXT,"
				+ MediaStore.MediaColumns.MIME_TYPE + " TEXT,"
				+ MediaStore.MediaColumns.SIZE + " INTEGER);";

		db.execSQL(request);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		if (oldVersion < DATABASE_VERSION) {
			db.execSQL("DROP TABLE IF EXISTS " + mTableName + ";");
			onCreate(db);
		}
	}
}

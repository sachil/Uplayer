package com.github.sachil.uplayer.database;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.seamless.util.MimeType;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;
import android.util.Log;

public class DatabaseManager {
	private static final String TAG = DatabaseManager.class.getSimpleName();
	private static DatabaseManager MANAGER = null;
	private Context mContext = null;
	private DatabaseHelper mHelper = null;
	private SQLiteDatabase mDatabase = null;

	private DatabaseManager(Context context) {
		mContext = context;
		mHelper = DatabaseHelper.newInstance(mContext);
	}

	public static DatabaseManager newInstance(Context context) {
		if (MANAGER == null)
			MANAGER = new DatabaseManager(context);
		return MANAGER;
	}

	public boolean addItem(String tableName, Item item) {
		if (mDatabase == null)
			createTable(tableName);
		if (isItemExist(tableName, item))
			return false;

		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns._ID, item.getId());
		values.put(DatabaseHelper.ITEM_PARENT_ID, item.getParentID());
		values.put(MediaStore.MediaColumns.TITLE, item.getTitle());
		values.put(DatabaseHelper.ITEM_URI, item.getFirstResource().getValue());
		values.put(MediaStore.MediaColumns.SIZE,
				item.getFirstResource().getSize());

		values.put(MediaStore.MediaColumns.MIME_TYPE,
				item.getFirstResource().getProtocolInfo().getContentFormat());

		if (item instanceof MusicTrack) {
			MusicTrack musicItem = (MusicTrack) item;
			values.put(MediaStore.Audio.AudioColumns.ARTIST,
					musicItem.getCreator());
			values.put(MediaStore.Audio.AudioColumns.ALBUM,
					musicItem.getAlbum());
			values.put(DatabaseHelper.AUDIO_THUMB,
					musicItem
							.getFirstPropertyValue(
									DIDLObject.Property.UPNP.ALBUM_ART_URI.class)
							.toString());
			values.put(MediaStore.Audio.AudioColumns.DURATION,
					musicItem.getFirstResource().getDuration());

			values.put(MediaStore.MediaColumns.DATA, musicItem.getDate());

		}
		if (mDatabase.insert(tableName, null, values) != -1)
			return true;
		else
			return false;

	}

	public void addItems(String tableName, List<Item> items) {
		for (Item item : items)
			addItem(tableName, item);
	}

	public void deleteItem(String tableName, Item item) {
		if (mDatabase == null)
			createTable(tableName);

		String whereCase = DatabaseHelper.ITEM_URI + "=?";
		String[] whereArgs = new String[] {
				item.getFirstResource().getValue() };
		mDatabase.delete(tableName, whereCase, whereArgs);
	}

	public void deleteItems(String tablename, List<Item> items) {

		for (Item item : items)
			deleteItem(tablename, item);

	}

	public void updateItem(String tableName, Item oldItem, Item newItem) {
		if (mDatabase == null)
			createTable(tableName);

		String whereCase = MediaStore.MediaColumns.TITLE + "=?";
		String[] whereArgs = new String[] { oldItem.getTitle() };
		ContentValues values = new ContentValues();
		values.put(MediaStore.MediaColumns.TITLE, newItem.getTitle());
		values.put(DatabaseHelper.ITEM_URI,
				newItem.getFirstResource().getValue());
		values.put(MediaStore.MediaColumns.SIZE,
				newItem.getFirstResource().getSize());
		if (newItem instanceof MusicTrack) {
			MusicTrack musicItem = (MusicTrack) newItem;
			values.put(MediaStore.Audio.AudioColumns.ARTIST,
					musicItem.getCreator());
			values.put(MediaStore.Audio.AudioColumns.ALBUM,
					musicItem.getAlbum());
			values.put(DatabaseHelper.AUDIO_THUMB,
					musicItem
							.getFirstPropertyValue(
									DIDLObject.Property.UPNP.ALBUM_ART_URI.class)
							.toString());
			values.put(MediaStore.Audio.AudioColumns.DURATION,
					musicItem.getFirstResource().getDuration());
			values.put(MediaStore.MediaColumns.DATA, musicItem.getDate());
		}
		mDatabase.update(tableName, values, whereCase, whereArgs);
	}

	public List<Item> listItems(String tableName) {
		if (mDatabase == null)
			createTable(tableName);

		List<Item> items = new ArrayList<>();
		Cursor cursor = mDatabase.query(tableName, null, null, null, null, null,
				null, null);

		if (cursor.getCount() > 0) {
			if (cursor.moveToFirst()) {
				do {
					String id = cursor.getString(
							cursor.getColumnIndex(MediaStore.MediaColumns._ID));
					String parentId = cursor.getString(cursor
							.getColumnIndex(DatabaseHelper.ITEM_PARENT_ID));
					String title = cursor.getString(cursor
							.getColumnIndex(MediaStore.MediaColumns.TITLE));
					String uri = cursor.getString(
							cursor.getColumnIndex(DatabaseHelper.ITEM_URI));
					Long size = cursor.getLong(cursor
							.getColumnIndex(MediaStore.MediaColumns.SIZE));

					String mimeType = cursor.getString(cursor
							.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));

					String artist = cursor.getString(cursor.getColumnIndex(
							MediaStore.Audio.AudioColumns.ARTIST));
					String album = cursor.getString(cursor.getColumnIndex(
							MediaStore.Audio.AudioColumns.ALBUM));
					String thumb = cursor.getString(
							cursor.getColumnIndex(DatabaseHelper.AUDIO_THUMB));
					String duration = cursor.getString(cursor.getColumnIndex(
							MediaStore.Audio.AudioColumns.DURATION));
					String date = cursor.getString(cursor
							.getColumnIndex(MediaStore.MediaColumns.DATA));
					Res res = new Res(
							new MimeType(mimeType.substring(0,
									mimeType.indexOf('/')),
							mimeType.substring(mimeType.indexOf('/') + 1)),
							size, uri);
					res.setDuration(duration);
					MusicTrack musicTrack = new MusicTrack(id, parentId, title,
							artist, album, artist, res);
					musicTrack.setDate(date);
					if (thumb != null)
						musicTrack.addProperty(
								new DIDLObject.Property.UPNP.ALBUM_ART_URI(
										URI.create(thumb)));
					items.add(musicTrack);
				} while (cursor.moveToNext());

			}
		}
		return items;
	}

	private void createTable(String tableName) {
		mHelper.setTableName(tableName);
		mDatabase = mHelper.getWritableDatabase();
	}

	private boolean isItemExist(String tableName, Item item) {
		boolean isExist = false;
		Cursor cursor = mDatabase.query(tableName,
				new String[] { DatabaseHelper.ITEM_URI },
				DatabaseHelper.ITEM_URI + "=?",
				new String[] { item.getFirstResource().getValue() }, null, null,
				null);
		if (cursor.getCount() != 0)
			isExist = true;
		return isExist;
	}

}

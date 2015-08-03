package com.github.sachil.uplayer.upnp.dmc.playlist;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.github.sachil.uplayer.Utils;
import com.github.sachil.uplayer.upnp.dmc.ContentItem;
import com.github.sachil.uplayer.upnp.dmc.MetaDataToXMLGenerator;


public class PlaylistManager {
	private static final String LOG_TAG = PlaylistManager.class.getSimpleName();
	private static final String URL = "url";
	private static final String METADATA = "metadata";
	private static JSONArray mJsonArray = new JSONArray();

	public static String getPlaylistsPath(Context context) {
		String path = context.getFilesDir().getAbsolutePath();
		Log.e(LOG_TAG, "playlist storage path:" + path);
		return path;
	}

	public static ArrayList<PlaylistItem> listPlaylists(Context context) {
		ArrayList<PlaylistItem> playlists = new ArrayList<PlaylistItem>();
		String[] files = context.getFilesDir().list();
		for (int i = 0; i < files.length; i++)
			playlists.add(new PlaylistItem(files[i]));
		return playlists;
	}

	public static boolean createPlaylist(Context context, String name) {
		try {
			if (!isNameValid(name))
				return false;
			FileOutputStream fos = context.openFileOutput(name,
					Context.MODE_PRIVATE);
			fos.close();
			context.sendBroadcast(new Intent(Utils.PLAYLISTS_EDITED));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean editPlaylist(Context context, String name,
			String content) {
		try {
			FileOutputStream fos = context.openFileOutput(name,
					Context.MODE_PRIVATE);
			if (content != null)
				fos.write(content.getBytes());
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static ArrayList<PlaylistItem> readPlaylist(Context context,
			String name) {
		ArrayList<PlaylistItem> playlistItems = new ArrayList<PlaylistItem>();
		try {
			FileInputStream fis = context.openFileInput(name);
			// Log.e(LOG_TAG, "" + fis.available());
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer, 0, fis.available());
			fis.close();
			if (buffer.length > 0)
				mJsonArray = new JSONArray(new String(buffer));
			for (int i = 0; i < mJsonArray.length(); i++)
				playlistItems.add(new PlaylistItem(mJsonArray.getJSONObject(i)
						.getString(URL), mJsonArray.getJSONObject(i).getString(
						METADATA)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return playlistItems;
	}

	public static boolean deletePlaylist(Context context, String name) {
		boolean result = context.deleteFile(name);
		mJsonArray = new JSONArray();
		if (result)
			context.sendBroadcast(new Intent(Utils.PLAYLISTS_EDITED));
		return result;
	}

	public static boolean deleteAllPlaylists(Context context) {
		boolean result = true;
		mJsonArray = new JSONArray();
		for (String name : context.getFilesDir().list()) {
			if (!context.deleteFile(name))
				result = false;
		}
		context.sendBroadcast(new Intent(Utils.PLAYLISTS_EDITED));
		return result;
	}

	public static String toJsonString(List<ContentItem> items) {
		try {
			for(int i = 0; i < items.size();i++){
				JSONObject object = new JSONObject();
				object.put(URL, items.get(i).getItem().getFirstResource().getValue());
				object.put(METADATA,
						MetaDataToXMLGenerator.metadataToXml(items.get(i).getItem()));
				mJsonArray.put(object);
			}
			Log.e(LOG_TAG, "" + mJsonArray.toString());
			return mJsonArray.toString();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static boolean isNameValid(String name) {
		if (name.length() > 32 || name.length() < 1)
			return false;
		if (!name.matches(new String("[A-Za-z0-9\\.\\32\\-_\u4e00-\u9fa5]+")))
			return false;
		return true;
	}

	@SuppressWarnings("unused")
	private static JSONArray removeJsonArray(JSONArray array, int index) {
		JSONArray jsonArray = new JSONArray();
		try {
			for (int i = 0; i < array.length(); i++)
				if (i != index)
					jsonArray.put(array).get(i);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonArray;
	}

}

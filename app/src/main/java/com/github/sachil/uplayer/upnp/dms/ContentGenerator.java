package com.github.sachil.uplayer.upnp.dms;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.PersonWithRole;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.MusicTrack;
import org.seamless.util.MimeType;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.upnp.UpnpUnity;


/**
 * 
 * 通过扫描MediaStore，得到媒体内容(目前只针对music)的详细信息，并将这些媒体内容
 * 按照一定的逻辑结构添加到本地dms上。
 * 逻辑结构如下：
 * 
 * 			Album	按照专辑名称分类
 *		 /	 
 * music -- Artist	按照歌手名称分类
 * 		 \
 * 			Track	将所有music放在该目录下
 * 
 * @author 20001962
 *
 */
public class ContentGenerator {

	private static final String LOG_TAG = ContentGenerator.class
			.getSimpleName();

	private static final String AUDIO_PREFIX = "audio-item";

	private static boolean mIsprepared = false;
	private static boolean mPrepareFinished = false;
	private static String[] mAudioColumns = { MediaStore.Audio.Media._ID,
			MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
			MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.MIME_TYPE,
			MediaStore.Audio.Media.SIZE, MediaStore.Audio.Media.DURATION,
			MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DATE_ADDED,
			MediaStore.Audio.Media.ALBUM_ID };

	public static void prepareAudio(final Context context,
			final MediaServer mediaServer) {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mIsprepared)
					return;
				UpnpUnity.generateContainer(UpnpUnity.CONTENT_AUDIO_ID,
						context.getString(R.string.title_music), UpnpUnity.CONTENT_ROOT_ID, "object.container",
						true);
				UpnpUnity.generateContainer(UpnpUnity.CONTENT_VIDEO_ID,
						context.getString(R.string.title_video), UpnpUnity.CONTENT_ROOT_ID, "object.container",
						true);
				UpnpUnity.generateContainer(UpnpUnity.CONTENT_IMAGE_ID,
						context.getString(R.string.title_photo), UpnpUnity.CONTENT_ROOT_ID, "object.container",
						true);

				UpnpUnity.generateContainer(UpnpUnity.AUDIO_Album_ID, context.getString(R.string.title_album),
						UpnpUnity.CONTENT_AUDIO_ID, "object.container", true);

				UpnpUnity.generateContainer(UpnpUnity.AUDIO_Artist_ID,
						context.getString(R.string.title_artist), UpnpUnity.CONTENT_AUDIO_ID,
						"object.container", true);

				Container allTrackContainer = UpnpUnity.generateContainer(
						UpnpUnity.AUDIO_TRACK_ID, context.getString(R.string.title_tracks),
						UpnpUnity.CONTENT_AUDIO_ID, "object.container", true);

				Cursor cursor = context.getContentResolver().query(
						MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
						mAudioColumns, null, null, null);

				if (cursor != null) {
					if (cursor.moveToFirst()) {
						do {
							String id = AUDIO_PREFIX
									+ cursor.getInt(cursor
											.getColumnIndex(MediaStore.Audio.Media._ID));
							String title = cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.TITLE));
							String artist = cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.ARTIST));
							String filePath = cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.DATA));
							String mimeType = cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.MIME_TYPE));
							long size = cursor.getLong(cursor
									.getColumnIndex(MediaStore.Audio.Media.SIZE));
							long duration = cursor.getLong(cursor
									.getColumnIndex(MediaStore.Audio.Media.DURATION));
							String album = cursor.getString(cursor
									.getColumnIndex(MediaStore.Audio.Media.ALBUM));
							Date addDate = new Date(
									cursor.getLong(cursor
											.getColumnIndex(MediaStore.Audio.Media.DATE_ADDED)) * 1000);
							SimpleDateFormat format = new SimpleDateFormat(
									"yyyy-MM-dd");
							String date = format.format(addDate);
							int albumId = cursor.getInt(cursor
									.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
							String value = "http://" + mediaServer.getAddress()
									+ "/" + id;
							Log.i(LOG_TAG, "The access value is:" + value);
							Res res = new Res(new MimeType(mimeType.substring(
									0, mimeType.indexOf('/')), mimeType
									.substring(mimeType.indexOf('/') + 1)),
									size, value);

							res.setDuration(ModelUtil
									.toTimeString(duration / 1000));

							MusicTrack musicTrack = new MusicTrack(id,
									UpnpUnity.AUDIO_Album_ID, title, artist,
									album, new PersonWithRole(artist), res);

							String albumArt = getAlbumArt(context, albumId);
							if (albumArt != null) {
								musicTrack
										.addProperty(new DIDLObject.Property.UPNP.ALBUM_ART_URI(
												URI.create("http://"
														+ mediaServer
																.getAddress()
														+ albumArt)));
							}
							
							//按专辑名称分类
							if (!ContentTree.hasContentNode(album, true)) {
								Container container = UpnpUnity
										.generateContainer(album, album,
												UpnpUnity.AUDIO_Album_ID,
												"object.container.album.musicAlbum", true);
								container.addItem(musicTrack);
								container.setChildCount(container
										.getChildCount() + 1);
								ContentTree.addContentNode(id, new ContentNode(
										id, musicTrack, filePath), true);
							} else {
								ContentTree.getContentNode(album, true)
										.getContainer().addItem(musicTrack);
								ContentTree
										.getContentNode(album, true)
										.getContainer()
										.setChildCount(
												ContentTree
														.getContentNode(album,
																true)
														.getContainer()
														.getChildCount() + 1);
								ContentTree.addContentNode(id, new ContentNode(
										id, musicTrack, filePath), true);

							}
							
							//按歌手名称分类
							if (!ContentTree.hasContentNode(artist, true)) {
								Container container = UpnpUnity
										.generateContainer(artist, artist,
												UpnpUnity.AUDIO_Artist_ID,
												"object.container.person.musicArtist", true);
								container.addItem(musicTrack);
								container.setChildCount(container
										.getChildCount() + 1);
								ContentTree.addContentNode(id, new ContentNode(
										id, musicTrack, filePath), true);
							} else {
								ContentTree.getContentNode(artist, true)
										.getContainer().addItem(musicTrack);
								ContentTree
										.getContentNode(artist, true)
										.getContainer()
										.setChildCount(
												ContentTree
														.getContentNode(artist,
																true)
														.getContainer()
														.getChildCount() + 1);
								ContentTree.addContentNode(id, new ContentNode(
										id, musicTrack, filePath), true);

							}

							musicTrack.setDate(date);
							allTrackContainer.addItem(musicTrack);
							allTrackContainer.setChildCount(allTrackContainer
									.getChildCount() + 1);
							ContentTree.addContentNode(id, new ContentNode(id,
									musicTrack, filePath), true);
							Log.i(LOG_TAG, "Add Audio item:" + title);
						} while (cursor.moveToNext());
					}
					cursor.close();
					mIsprepared = true;
				}
			}
		});
		thread.start();
		mPrepareFinished = true;
	}

	public static boolean isFinished() {

		return mPrepareFinished;
	}

	
	/**
	 * 获取专辑封面
	 * @param context
	 * @param album_id 专辑id
	 * @return 专辑封面的绝对路径。
	 */
	private static String getAlbumArt(Context context, int album_id) {
		String mUriAlbums = "content://media/external/audio/albums";
		String[] projection = new String[] { "album_art" };
		Cursor cur = context.getContentResolver().query(
				Uri.parse(mUriAlbums + "/" + Integer.toString(album_id)),
				projection, null, null, null);
		String album_art = null;
		if (cur.getCount() > 0 && cur.getColumnCount() > 0) {
			cur.moveToNext();
			album_art = cur.getString(0);
		}
		cur.close();
		cur = null;
		return album_art;
	}
}

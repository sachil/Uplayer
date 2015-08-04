package com.github.sachil.uplayer.upnp.dmc;

import java.io.IOException;
import java.io.StringReader;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.TransportState;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import android.util.Log;
import android.util.Xml;

import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.Utils;

public class XMLToMetadataParser {
	private static final String LOG_TAG = XMLToMetadataParser.class
			.getSimpleName();

	public Metadata getDefaultMetadata() {

		return new Metadata();
	}

	public Metadata parseXmlToMetadata(Metadata meta, String xml) {
		Metadata metadata = null;
		if (meta == null)
			metadata = new Metadata();
		else
			metadata = meta;
		if (xml != null) {
			XmlPullParser parser = Xml.newPullParser();
			if (parser != null) {
				try {
					String title = null;
					String creator = null;
					String artist = null;
					String album = null;
					String albumArt = null;
					String duration = null;
					String size = null;

					parser.setInput(new StringReader(xml));
					int eventType = parser.getEventType();
					while (eventType != XmlPullParser.END_DOCUMENT) {
						switch (eventType) {
						case XmlPullParser.START_DOCUMENT:

							break;
						case XmlPullParser.START_TAG:
							String tag = parser.getName();
							if (tag.equalsIgnoreCase("title"))
								title = parser.nextText();
							else if (tag.equalsIgnoreCase("creator"))
								creator = parser.nextText();
							else if (tag.equalsIgnoreCase("artist"))
								artist = parser.nextText();

							else if (tag.equalsIgnoreCase("album"))
								album = parser.nextText();
							else if (tag.equalsIgnoreCase("albumArtURI"))
								albumArt = parser.nextText();
							else if (tag.equalsIgnoreCase("res")) {
								duration = parser.getAttributeValue(null,
										"duration");
								size = parser.getAttributeValue(null, "size");
							}
							break;

						case XmlPullParser.END_TAG:
							break;

						default:
							break;
						}
						eventType = parser.next();
					}
					if (metadata != null) {
						metadata.setTitle(title);
						metadata.setCreator(creator);
						metadata.setArtist(artist);
						metadata.setAlbum(album);
						metadata.setAlbumArt(albumArt);
						metadata.setSize(size);
						metadata.setDuration(duration);
					}
				} catch (XmlPullParserException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return metadata;
	}

	public class Metadata {
		private String mTitle = null;
		private String mCreator = null;
		private String mArtist = null;
		private String mAlbum = null;
		private String mAlbumArt = null;
		private long mSize = 0;
		private String mDuration = "00:00:00";
		private PlayMode mPlayMode = PlayMode.NORMAL;
		private boolean mIsMute = false;
		private int mVolume = 0;
		private TransportState mState = TransportState.NO_MEDIA_PRESENT;

		public Metadata() {
			String defaultValue = "";
			setTitle(defaultValue);
			setCreator(defaultValue);
			setAlbum(defaultValue);
			setArtist(defaultValue);
		}

		public void setState(TransportState state) {
			mState = state;
		}

		public TransportState getState() {
			return mState;
		}

		public void setMute(Boolean isMute) {
			mIsMute = isMute;
		}

		public boolean isMute() {
			return mIsMute;
		}

		public void setPlayMode(PlayMode mode) {
			mPlayMode = mode;
		}

		public PlayMode getPlayMode() {
			return mPlayMode;
		}

		public void setVolume(int volume) {
			mVolume = volume;
		}

		public int getVolume() {
			return mVolume;
		}

		public void setTitle(String title) {

			mTitle = title;
		}

		public void setCreator(String creator) {

			mCreator = creator;
		}

		public void setArtist(String artist) {

			mArtist = artist;
		}

		public void setAlbum(String album) {

			mAlbum = album;
		}

		public void setAlbumArt(String albumArt) {
			mAlbumArt = albumArt;
		}

		public void setSize(String size) {
			if (size != null)
				mSize = Integer.parseInt(size);
		}

		public void setDuration(String duration) {
			if (duration != null)
				mDuration = ModelUtil
						.toTimeString(ModelUtil.fromTimeString(duration));
		}

		public String getTitle() {

			return mTitle;
		}

		public String getCreator() {

			return mCreator;
		}

		public String getArtist() {

			return mArtist;
		}

		public String getAlbum() {

			return mAlbum;
		}

		public String getAlbumArt() {

			return mAlbumArt;
		}

		public long getSize() {

			return mSize;
		}

		public String getDuration() {

			return mDuration;
		}
	}

}

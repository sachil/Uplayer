package com.github.sachil.uplayer.upnp.dmc;

import org.fourthline.cling.model.ModelUtil;
import org.fourthline.cling.support.model.PlayMode;
import org.fourthline.cling.support.model.TransportState;

public class Metadata {
	private String mTitle = null;
	private String mCreator = null;
	private String mArtist = null;
	private String mAlbum = null;
	private String mAlbumArt = null;
	private long mSize = 0;
	private String mDuration = "00:00:00";
	private PlayMode mPlayMode = PlayMode.NORMAL;
	private int mVolume = 0;
	private TransportState mState = TransportState.NO_MEDIA_PRESENT;
	private boolean mIsMute = false;

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

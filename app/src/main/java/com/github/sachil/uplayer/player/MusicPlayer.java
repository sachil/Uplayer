package com.github.sachil.uplayer.player;

import java.io.IOException;

import org.fourthline.cling.support.model.TransportState;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.github.sachil.uplayer.upnp.TransportStateChangedListener;

public class MusicPlayer implements AVPlayer, MediaPlayer.OnPreparedListener,
		MediaPlayer.OnErrorListener, MediaPlayer.OnSeekCompleteListener,
		MediaPlayer.OnCompletionListener, MediaPlayer.OnInfoListener {

	private static final String TAG = MusicPlayer.class.getSimpleName();
	private static MusicPlayer PLAYER = null;
	private AudioManager mAudioManager = null;
	private MediaPlayer mMediaPlayer = null;
	private TransportStateChangedListener mListener = null;
	private String mUrl = null;
	private boolean mUrlChanged = false;
	private boolean mIsPrepared = false;

	private MusicPlayer(Context context) {
		mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mMediaPlayer.setOnCompletionListener(this);
		mMediaPlayer.setOnSeekCompleteListener(this);
		mMediaPlayer.setOnInfoListener(this);
		mMediaPlayer.setOnErrorListener(this);
		mMediaPlayer.setOnPreparedListener(this);
		mMediaPlayer.reset();
	}

	public static MusicPlayer getInstance(Context context) {
		if (PLAYER == null)
			PLAYER = new MusicPlayer(context);
		return PLAYER;
	}

	public void bindStateChangeListener(
			TransportStateChangedListener listener) {
		mListener = listener;
	}

	@Override
	public void onPrepared(MediaPlayer mediaPlayer) {
		mUrlChanged = false;
		mIsPrepared = true;
		mediaPlayer.start();
		mListener.transportStateChanged(TransportState.PLAYING);
	}

	@Override
	public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
		if (-38 == what || -2147483648 == what || 0 == what)
			return true;
		else
			return false;
	}

	@Override
	public void onSeekComplete(MediaPlayer mediaPlayer) {
		mediaPlayer.start();
		//发送间隔太短会导致接受失败。
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mListener.transportStateChanged(TransportState.PLAYING);
	}

	@Override
	public void onCompletion(MediaPlayer mediaPlayer) {
		mediaPlayer.stop();
		mListener.transportStateChanged(TransportState.STOPPED);
	}

	@Override
	public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
		return false;
	}

	@Override
	public void setUri(String uri) {
		mListener.transportStateChanged(TransportState.TRANSITIONING);
		mUrl = uri;
		mUrlChanged = true;
		mIsPrepared = false;
	}

	@Override
	public void play() {
		try {
			if (mUrlChanged) {
				mMediaPlayer.stop();
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(mUrl);
				mMediaPlayer.prepareAsync();
			} else {
				mMediaPlayer.start();
				mListener.transportStateChanged(TransportState.PLAYING);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void pause() {
		if (mMediaPlayer.isPlaying()){
			mMediaPlayer.pause();
			mListener.transportStateChanged(TransportState.PAUSED_PLAYBACK);
		}

	}

	@Override
	public boolean isPlaying() {

		return mMediaPlayer.isPlaying();
	}

	@Override
	public void next() {

	}

	@Override
	public void previous() {

	}

	@Override
	public void seek(int position) {
		mListener.transportStateChanged(TransportState.TRANSITIONING);
		mMediaPlayer.seekTo(position * 1000);
	}

	@Override
	public int getCurrentPosition() {
		if (mIsPrepared)
			return mMediaPlayer.getCurrentPosition();
		else
			return 0;
	}

	@Override
	public int getDuration() {
		if (mIsPrepared)
			return mMediaPlayer.getDuration();
		else
			return 0;
	}

	@Override
	public void setMute(boolean mute) {

		mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, mute);
	}

	@Override
	public boolean getMute() {
		return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0
				? false : true;
	}

	@Override
	public void setVolume(int volume) {
		if (volume >= 0 && volume <= mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
					AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
		}

	}

	@Override
	public int getVolume() {
		return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	@Override
	public void stop() {
		mMediaPlayer.stop();
		mListener.transportStateChanged(TransportState.STOPPED);
	}

	@Override
	public void close() {
		mMediaPlayer.stop();
		mMediaPlayer.reset();
		mMediaPlayer.release();
		mListener.transportStateChanged(TransportState.STOPPED);
	}
}

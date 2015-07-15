package com.github.sachil.uplayer.player;

import java.io.IOException;

import org.fourthline.cling.support.model.TransportState;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.util.Log;

import com.github.sachil.uplayer.upnp.TransportStateChangedListener;
import com.github.sachil.uplayer.upnp.dmr.AvtransportService;

public class AudioPlayer extends AbstractAVPlayer {
	private static final String LOG_TAG = AudioPlayer.class.getSimpleName();
	private MediaPlayer mMediaPlayer = null;
	private AudioManager mAudioManager = null;
	private String mPlayUri = null;
	private TransportStateChangedListener mCallback = null;
	private boolean mPlayUriChanged = false;
	private boolean mIsPrepared = false;

	private OnPreparedListener mPreparedListener = new OnPreparedListener() {

		@Override
		public void onPrepared(MediaPlayer mp) {
			// TODO Auto-generated method stub
			Log.e(LOG_TAG, "Prepared completed,start to play!");
			mp.start();
			mPlayUriChanged = false;
			mIsPrepared = true;
			mCallback.transportStateChanged(TransportState.PLAYING);
		}
	};

	private OnErrorListener mErrorListener = new OnErrorListener() {

		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			Log.e(LOG_TAG, "MediaPlayer error:" + what + "," + extra);
			if (-38 == what || -2147483648 == what || 0 == what)
				return false;
			else
				return true;
		}
	};

	private OnInfoListener mInfoListener = new OnInfoListener() {

		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			return false;
		}
	};

	private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {

		@Override
		public void onSeekComplete(MediaPlayer mp) {
			// TODO Auto-generated method stub
			Log.e(LOG_TAG, "Seek completed!");
			mp.start();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mCallback.transportStateChanged(TransportState.PLAYING);

		}
	};

	private OnCompletionListener mCompletionListener = new OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			Log.e(LOG_TAG, "Play completed!");
			// mMediaPlayer.stop();
			mCallback.transportStateChanged(TransportState.STOPPED);
		}
	};

	public AudioPlayer(Context context, AvtransportService service) {
		mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		if (service instanceof TransportStateChangedListener)
			mCallback = (TransportStateChangedListener) service;
		else
			throw new IllegalStateException(
					"The AvTransportService don't implement TransportStateChangedListener");
	}

	@Override
	public void setUri(String uri) {
		// TODO Auto-generated method stub
		mPlayUri = uri;
		Log.e(LOG_TAG, "playUri:" + mPlayUri);
		mPlayUriChanged = true;
		mIsPrepared = false;
	}

	@Override
	public void play() {
		// TODO Auto-generated method stub
		try {
			if (mMediaPlayer == null) {
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mMediaPlayer.setOnCompletionListener(mCompletionListener);
				mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
				mMediaPlayer.setOnInfoListener(mInfoListener);
				mMediaPlayer.setOnErrorListener(mErrorListener);
				mMediaPlayer.setOnPreparedListener(mPreparedListener);
				mMediaPlayer.reset();
				mMediaPlayer.setDataSource(mPlayUri);
				mCallback.transportStateChanged(TransportState.TRANSITIONING);
				mMediaPlayer.prepareAsync();
			} else {
				if (mPlayUriChanged) {
					mMediaPlayer.stop();
					mMediaPlayer.reset();
					mMediaPlayer.setDataSource(mPlayUri);
					mCallback
							.transportStateChanged(TransportState.TRANSITIONING);
					mMediaPlayer.prepareAsync();
				} else {
					mMediaPlayer.start();
					mCallback.transportStateChanged(TransportState.PLAYING);
				}

			}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		Log.e(LOG_TAG, "Start to pause");
		if (mIsPrepared)
			mMediaPlayer.pause();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if(mCallback != null)
			mCallback.transportStateChanged(TransportState.STOPPED);
		if (mMediaPlayer != null)
			mMediaPlayer.stop();
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		mCallback.transportStateChanged(TransportState.STOPPED);
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	@Override
	public boolean getMute() {
		// TODO Auto-generated method stub
		return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0 ? false
				: true;
	}

	@Override
	public int getVolume() {
		// TODO Auto-generated method stub
		return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	}

	@Override
	public void setMute(boolean mute) {
		// TODO Auto-generated method stub
		mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, mute);
	}

	@Override
	public void setVolume(int volume) {
		// TODO Auto-generated method stub
		if (volume >= 0
				&& volume <= mAudioManager
						.getStreamMaxVolume(AudioManager.STREAM_MUSIC)) {
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
					AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
		}
	}

	@Override
	public void seek(int position) {
		// TODO Auto-generated method stub

		mMediaPlayer.seekTo(position * 1000);
	}

	@Override
	public int getDuration() {
		// TODO Auto-generated method stub
		if (mIsPrepared)
			return mMediaPlayer.getDuration();
		else
			return 0;
	}

	@Override
	public int getCurrentPosition() {
		// TODO Auto-generated method stub
		if (mIsPrepared)
			return mMediaPlayer.getCurrentPosition();
		else
			return 0;
	}

	@Override
	public void next() {
		// TODO Auto-generated method stub

	}

	@Override
	public void previous() {
		// TODO Auto-generated method stub

	}

}
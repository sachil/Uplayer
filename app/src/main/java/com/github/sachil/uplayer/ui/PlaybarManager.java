package com.github.sachil.uplayer.ui;

import org.fourthline.cling.support.model.TransportState;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.player.MusicService;
import com.github.sachil.uplayer.ui.message.ActionMessage;
import com.github.sachil.uplayer.ui.message.ErrorMessage;
import com.github.sachil.uplayer.ui.message.PlayerMessage;
import com.github.sachil.uplayer.upnp.UpnpUnity;
import com.github.sachil.uplayer.upnp.dmc.Controller;
import com.github.sachil.uplayer.upnp.dmc.Metadata;
import com.github.sachil.uplayer.upnp.dmc.XMLFactory;

import de.greenrobot.event.EventBus;

public class PlaybarManager implements View.OnClickListener {

	private static final String TAG = PlaybarManager.class.getSimpleName();
	private Context mContext = null;
	private View mPlaybar = null;
	private SimpleDraweeView mAlbum = null;
	private TextView mTitle = null;
	private TextView mArtist = null;
	private ImageView mPlayPause = null;
	private ImageView mPlayList = null;
	private Controller mController = null;
	private PlaylistManager mPlaylistManager = null;
	private TransportState mState = TransportState.NO_MEDIA_PRESENT;

	public PlaybarManager(Context context, View contentView) {
		mContext = context;
		createView(contentView);
		mPlaylistManager = PlaylistManager.newInstance(mContext);
		mPlaylistManager.queryList();
		if (!EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().register(this);
	}

	public void onEvent(ActionMessage message) {

		switch (message.getViewId()) {
		case -1:
			if (mController == null)
				mController = MusicService.getInstance().getController();
			playItem();
			break;
		}
	}

	public void onEventMainThread(ErrorMessage message) {

		if (message.getId() == ErrorMessage.CONTROL_ERROR)
			Toast.makeText(mContext, message.getMessage(), Toast.LENGTH_SHORT)
					.show();
	}

	public void onEventMainThread(PlayerMessage message) {

		switch (message.getId()) {
		case PlayerMessage.REFRESH_METADATA:
			Metadata metadata = (Metadata) message.getExtra();
			syncState(PlayerMessage.REFRESH_METADATA, metadata);
			break;

		case PlayerMessage.REFRESH_VOLUME:
			metadata = (Metadata) message.getExtra();
			syncState(PlayerMessage.REFRESH_VOLUME, metadata);
			break;
		}
	}

	public void clean() {
		if (mPlaylistManager != null)
			mPlaylistManager.clean();
		if (EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().unregister(this);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.playbar_layout:

			if (UpnpUnity.CURRENT_RENDERER != null)
				mContext.startActivity(
						new Intent(mContext, PlayerActivity.class));
			break;

		case R.id.playbar_playpause:
			if (mController == null)
				mController = MusicService.getInstance().getController();
			if (mState == TransportState.PLAYING)
				mController.pause();
			else if (mState == TransportState.PAUSED_PLAYBACK)
				mController.play();
			break;
		case R.id.playbar_playlist:
			mPlaylistManager.showPlaylistView(mPlaybar);
			break;
		}
	}

	private void syncState(int id, Object data) {

		if (id == PlayerMessage.REFRESH_METADATA) {
			Metadata metadata = (Metadata) data;
			mState = metadata.getState();
			if (mState == TransportState.PLAYING) {
				mPlayPause.setImageResource(R.drawable.playback_pause);
				mTitle.setText(metadata.getTitle());
				mArtist.setText(metadata.getCreator());
				mAlbum.setImageURI(Uri.parse(metadata.getAlbumArt()));
			} else
				mPlayPause.setImageResource(R.drawable.playback_play);

		}
	}

	private void playItem() {

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					mController.setUrl(
							UpnpUnity.PLAYING_ITEM.getFirstResource()
									.getValue(),
							XMLFactory.metadataToXml(UpnpUnity.PLAYING_ITEM));
					Thread.sleep(1000);
					mController.play();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}

	private void createView(View contentView) {
		contentView.findViewById(R.id.playbar_layout).setOnClickListener(this);
		mPlaybar = contentView.findViewById(R.id.playbar_layout);
		mAlbum = (SimpleDraweeView) contentView
				.findViewById(R.id.playbar_album);
		mTitle = (TextView) contentView.findViewById(R.id.playbar_title);
		mArtist = (TextView) contentView.findViewById(R.id.playbar_artist);
		mPlayPause = (ImageView) contentView
				.findViewById(R.id.playbar_playpause);
		mPlayPause.setOnClickListener(this);
		mPlayList = (ImageView) contentView.findViewById(R.id.playbar_playlist);
		mPlayList.setOnClickListener(this);
	}

}

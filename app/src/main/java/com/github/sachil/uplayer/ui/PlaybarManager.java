package com.github.sachil.uplayer.ui;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder.IconValue;
import net.steamcrafted.materialiconlib.MaterialIconView;

import org.fourthline.cling.support.model.TransportState;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.ui.message.ActionMessage;
import com.github.sachil.uplayer.ui.message.PlayerMessage;
import com.github.sachil.uplayer.upnp.UpnpUnity;
import com.github.sachil.uplayer.upnp.dmc.Controller;
import com.github.sachil.uplayer.upnp.dmc.MetaDataToXMLGenerator;
import com.github.sachil.uplayer.upnp.dmc.XMLToMetadataParser;
import com.github.sachil.uplayer.upnp.dmc.XMLToMetadataParser.Metadata;

import de.greenrobot.event.EventBus;

public class PlaybarManager implements View.OnClickListener {

	private static final String TAG = PlaybarManager.class.getSimpleName();
	private Context mContext = null;
	private SimpleDraweeView mAlbum = null;
	private TextView mTitle = null;
	private TextView mArtist = null;
	private MaterialIconView mPlayPause = null;
	private MaterialIconView mPlayList = null;
	private Controller mController = null;
	private TransportState mState = TransportState.NO_MEDIA_PRESENT;

	public PlaybarManager(Context context, View contentView) {
		mContext = context;
		createView(contentView);
		if (!EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().register(this);
	}

	public void clean() {
		if (EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().unregister(this);
	}

	public void onEvent(ActionMessage message) {

		switch (message.getViewId()) {
		case -1:
			if (mController == null) {
				mController = Controller.getInstance();
				mController.registerLastChange();
			}

			playItem();
			break;
		}
	}

	public void onEventMainThread(PlayerMessage message) {

		switch (message.getId()) {
		case PlayerMessage.REFRESH_METADATA:
			XMLToMetadataParser.Metadata metadata = (XMLToMetadataParser.Metadata) message
					.getExtra();
			syncState(PlayerMessage.REFRESH_METADATA, metadata);
			break;

		case PlayerMessage.REFRESH_VOLUME:
			metadata = (XMLToMetadataParser.Metadata) message.getExtra();
			syncState(PlayerMessage.REFRESH_VOLUME, metadata);
			break;
		}
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
			if (mState == TransportState.PLAYING)
				mController.pause();
			else if (mState == TransportState.PAUSED_PLAYBACK)
				mController.play();
			break;
		case R.id.playbar_playlist:

			break;
		}
	}

	private void syncState(int id, Object data) {

		if (id == PlayerMessage.REFRESH_METADATA) {
			Metadata metadata = (Metadata) data;
			mState = metadata.getState();
			if (mState == TransportState.PLAYING) {
				mPlayPause.setIcon(IconValue.PAUSE);
				mTitle.setText(metadata.getTitle());
				mArtist.setText(metadata.getCreator());
				mAlbum.setImageURI(Uri.parse(metadata.getAlbumArt()));
			} else
				mPlayPause.setIcon(IconValue.PLAY);

		}
	}

	private void playItem() {

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					mController.setUrl(
							UpnpUnity.PLAYING_ITEM.getItem().getFirstResource()
									.getValue(),
							MetaDataToXMLGenerator.metadataToXml(
									UpnpUnity.PLAYING_ITEM.getItem()));
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
		mAlbum = (SimpleDraweeView) contentView
				.findViewById(R.id.playbar_album);
		mTitle = (TextView) contentView.findViewById(R.id.playbar_title);
		mArtist = (TextView) contentView.findViewById(R.id.playbar_artist);
		mPlayPause = (MaterialIconView) contentView
				.findViewById(R.id.playbar_playpause);
		mPlayPause.setOnClickListener(this);
		mPlayList = (MaterialIconView) contentView
				.findViewById(R.id.playbar_playlist);
		mPlayList.setOnClickListener(this);

	}

}

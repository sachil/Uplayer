package com.github.sachil.uplayer.ui;

import android.content.Context;
import android.view.View;

import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.database.DatabaseManager;
import com.github.sachil.uplayer.ui.adapter.PlaylistAdapter;
import com.github.sachil.uplayer.ui.content.PlaylistView;
import com.github.sachil.uplayer.ui.message.ActionMessage;
import com.github.sachil.uplayer.upnp.UpnpUnity;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.item.Item;

import de.greenrobot.event.EventBus;

public class PlaylistManager {

	private static final String TAG = PlaylistManager.class.getSimpleName();
	private static PlaylistManager MANAGER = null;
	private Context mContext = null;
	private PlaylistAdapter mAdapter = null;
	private PlaylistView mPlaylistView = null;
	private DatabaseManager mDatabaseManager = null;
	private String mTableName = "test";

	private PlaylistManager(Context context) {
		mContext = context;
		mAdapter = new PlaylistAdapter(mContext);
		mPlaylistView = new PlaylistView(mContext);
		mPlaylistView.setAdapter(mAdapter);
		mDatabaseManager = DatabaseManager.newInstance(mContext);
		queryList();
		if (!EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().register(this);
	}

	public static PlaylistManager newInstance(Context context) {
		if (MANAGER == null)
			MANAGER = new PlaylistManager(context);
		return MANAGER;
	}

	public void onEvent(ActionMessage message) {

		switch (message.getViewId()) {
		case R.id.menu_add_playlist:
			if (mDatabaseManager.addItem(mTableName, (Item) message.getExtra()))
				mAdapter.add((Item) message.getExtra());
			break;
		case R.id.playlist_delete:
			mDatabaseManager.deleteItem(mTableName, (Item) message.getExtra());
			mAdapter.remove((Item) message.getExtra());
			break;
		case R.id.playlist_row:
			DIDLObject object = (DIDLObject) message.getExtra();
			UpnpUnity.PLAYING_ITEM = (Item) object;
			EventBus.getDefault().post(new ActionMessage(-1, 0, null));
			break;
		case R.id.playlist_model:
			break;
		case R.id.playlist_clear:
			mDatabaseManager.deleteItems(mTableName, mAdapter.getItems());
			mAdapter.clear();
			break;
		}
	}

	public void showPlaylistView(View parent) {
		mPlaylistView.show(parent);
	}

	public void queryList() {
		mAdapter.refresh(mDatabaseManager.listItems(mTableName));
	}

	public void clean() {
		if (EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().unregister(this);
	}

}

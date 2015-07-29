package com.github.sachil.uplayer.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.support.model.container.Container;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.ui.adapter.ContentAdapter;
import com.github.sachil.uplayer.ui.content.DividerItemDecoration;
import com.github.sachil.uplayer.ui.message.ActionMessage;
import com.github.sachil.uplayer.ui.message.BrowseMessage;
import com.github.sachil.uplayer.upnp.UpnpUnity;
import com.github.sachil.uplayer.upnp.dmc.BrowseCallback;
import com.github.sachil.uplayer.upnp.dmc.ContentItem;
import com.github.sachil.uplayer.upnp.dms.ContentTree;

import de.greenrobot.event.EventBus;

public class ContentManager {

	private static final String TAG = ContentManager.class.getSimpleName();
	private Context mContext = null;
	private RecyclerView mRecyclerView = null;
	private ContentAdapter mContentAdapter = null;
	private List<ContentItem> mContentList = null;

	public ContentManager(Context context, View contentView) {

		mContext = context;
		mContentList = new ArrayList<>();
		if (!EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().register(this);
		createView(contentView);
	}

	public boolean isRootNode() {

		return UpnpUnity.CURRENT_CONTAINER.getContainer().getParentID()
				.equalsIgnoreCase(ContentTree
						.getRootContentNode(
								UpnpUnity.CURRENT_SERVER instanceof LocalDevice)
						.getContainer().getId());
	}

	public void viewParent() {
		String parent = UpnpUnity.CURRENT_CONTAINER.getContainer()
				.getParentID();
		if (ContentTree.hasContentNode(parent,
				UpnpUnity.CURRENT_SERVER instanceof LocalDevice)) {
			Container container = ContentTree
					.getContentNode(parent,
							UpnpUnity.CURRENT_SERVER instanceof LocalDevice)
					.getContainer();
			browseContent(UpnpUnity.CURRENT_SERVER, container);
			UpnpUnity.CURRENT_CONTAINER = new ContentItem(container,
					UpnpUnity.CURRENT_CONTAINER.getService());
		}
	}

	public void onEventMainThread(BrowseMessage message) {

		if (!message.isRootNode())
			mContentAdapter.refresh(message.getItems());
	}

	public void onEvent(ActionMessage message) {
		switch (message.getViewId()) {
		case R.id.nav_media:
			browseContent(UpnpUnity.CURRENT_SERVER,
					UpnpUnity.CURRENT_CONTAINER.getContainer());
			break;
		case R.id.item_row:
			ContentItem contentItem = (ContentItem) message.getExtra();
			if (contentItem.isContainer()) {
				UpnpUnity.CURRENT_CONTAINER = contentItem;
				browseContent(UpnpUnity.CURRENT_SERVER,
						UpnpUnity.CURRENT_CONTAINER.getContainer());
			} else {
				UpnpUnity.PLAYING_ITEM = contentItem;
				EventBus.getDefault().post(new ActionMessage(-1, 0, null));
			}
			break;
		}

	}

	public void clean() {
		if (EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().unregister(this);
	}

	private void createView(View contentView) {
		mRecyclerView = (RecyclerView) contentView
				.findViewById(R.id.main_recyclerview);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
		mRecyclerView.addItemDecoration(new DividerItemDecoration(mContext,
				DividerItemDecoration.VERTICAL_LIST));
		mContentAdapter = new ContentAdapter();
		mRecyclerView.setAdapter(mContentAdapter);
	}

	private void browseContent(Device device, Container container) {
		boolean isLoacl = device instanceof RemoteDevice ? false : true;
		UpnpUnity.UPNP_SERVICE.getControlPoint().execute(new BrowseCallback(
				UpnpUnity.CURRENT_CONTAINER.getService(), container, isLoacl));
	}
}

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

	private Device mSelectedDevice = null;
	private ContentItem mSelectedMedia = null;

	public ContentManager(Context context, View contentView) {

		mContext = context;
		mContentList = new ArrayList<>();
		if (!EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().register(this);
		createView(contentView);
	}

	public boolean isRootNode() {
		return mSelectedMedia.getContainer().getParentID()
				.equalsIgnoreCase(ContentTree
						.getRootContentNode(
								mSelectedDevice instanceof LocalDevice)
						.getContainer().getId());
	}

	public void viewParent() {
		String parent = mSelectedMedia.getContainer().getParentID();
		if (ContentTree.hasContentNode(parent,
				mSelectedDevice instanceof LocalDevice)) {
			Container container = ContentTree
					.getContentNode(parent,
							mSelectedDevice instanceof LocalDevice)
					.getContainer();
			browseContent(mSelectedDevice, container);
			mSelectedMedia = new ContentItem(container,
					mSelectedMedia.getService());
		}
	}

	public void onEventMainThread(BrowseMessage message) {

		if (!message.isRootNode())
			mContentAdapter.refresh(message.getItems());
	}

	public void onEvent(ActionMessage message) {

		switch (message.getViewId()) {
		case R.id.nav_media:
			HashMap<String, Object> extra = (HashMap<String, Object>) message
					.getExtra();
			mSelectedDevice = (Device) extra.get("dev");
			mSelectedMedia = (ContentItem) extra.get("media");
			browseContent(mSelectedDevice, mSelectedMedia.getContainer());
			break;
		case R.id.item_row:
			mSelectedMedia = (ContentItem) message.getExtra();
			if (mSelectedMedia.isContainer())
				browseContent(mSelectedDevice, mSelectedMedia.getContainer());
			else {

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
				mSelectedMedia.getService(), container, isLoacl));
	}
}

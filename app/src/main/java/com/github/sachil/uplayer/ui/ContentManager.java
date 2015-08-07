package com.github.sachil.uplayer.ui;

import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.support.model.container.Container;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.ui.adapter.ContentAdapter;
import com.github.sachil.uplayer.ui.content.DividerGridItemDecoration;
import com.github.sachil.uplayer.ui.content.DividerListItemDecoration;
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
	private DividerListItemDecoration mDecoration = null;

	private LAYOUT_TYPE mLayout = LAYOUT_TYPE.LIST;

	public enum LAYOUT_TYPE {
		LIST, GRID,STAGGERED_GRID
	}

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
		case R.id.menu_layout:
			mLayout = (LAYOUT_TYPE) message.getExtra();
			updateView();
			break;
		}

	}

	public LAYOUT_TYPE getLayout() {
		return mLayout;
	}

	public void clean() {
		if (EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().unregister(this);
	}

	private void createView(View contentView) {
		mRecyclerView = (RecyclerView) contentView
				.findViewById(R.id.main_recyclerview);
		updateView();

	}

	private void updateView() {

		if(mContentAdapter != null)
			mContentAdapter.changeLayoutType(mLayout);

		if (mLayout == LAYOUT_TYPE.LIST) {
			mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));

			if(mDecoration == null)
				mDecoration = new DividerListItemDecoration(
						mContext, DividerListItemDecoration.VERTICAL_LIST);

			mRecyclerView.removeItemDecoration(mDecoration);
			mRecyclerView.addItemDecoration(mDecoration);
		} else if (mLayout == LAYOUT_TYPE.GRID) {
			mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
			if(mDecoration != null)
				mRecyclerView.removeItemDecoration(mDecoration);
		}else{
			mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,
					StaggeredGridLayoutManager.VERTICAL));
			if(mDecoration != null)
				mRecyclerView.removeItemDecoration(mDecoration);
		}
		if (mContentAdapter == null) {
			mContentAdapter = new ContentAdapter(mContext);
			mRecyclerView.setAdapter(mContentAdapter);
			mContentAdapter.changeLayoutType(mLayout);
		}
	}

	private void browseContent(Device device, Container container) {
		boolean isLoacl = device instanceof RemoteDevice ? false : true;
		UpnpUnity.UPNP_SERVICE.getControlPoint().execute(new BrowseCallback(
				UpnpUnity.CURRENT_CONTAINER.getService(), container, isLoacl));
	}
}

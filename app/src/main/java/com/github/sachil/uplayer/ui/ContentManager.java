package com.github.sachil.uplayer.ui;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.ui.adapter.ContentAdapter;
import com.github.sachil.uplayer.ui.content.DividerItemDecoration;
import com.github.sachil.uplayer.ui.message.BrowseMessage;
import com.github.sachil.uplayer.upnp.UpnpUnity;
import com.github.sachil.uplayer.upnp.dmc.BrowseCallback;
import com.github.sachil.uplayer.upnp.dmc.ContentItem;

import java.util.ArrayList;
import java.util.List;

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

	public void onEvent(BrowseMessage message) {

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

	private void browseContent(Device device, ContentItem item) {
		if (item.isContainer()) {
			boolean isLoacl = device instanceof RemoteDevice ? false : true;
			UpnpUnity.UPNP_SERVICE.getControlPoint().execute(new BrowseCallback(
					item.getService(), item.getContainer(), isLoacl));
		} else {

		}
	}
}

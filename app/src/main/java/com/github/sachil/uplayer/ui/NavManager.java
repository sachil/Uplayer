package com.github.sachil.uplayer.ui;

import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.ExpandableListView;

import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.ui.adapter.NavAdapter;
import com.github.sachil.uplayer.ui.content.PatchedExpandableListView;
import com.github.sachil.uplayer.ui.message.BrowseMessage;
import com.github.sachil.uplayer.ui.message.DeviceMessage;
import com.github.sachil.uplayer.upnp.UpnpUnity;
import com.github.sachil.uplayer.upnp.dmc.BrowseCallback;
import com.github.sachil.uplayer.upnp.dmc.ContentItem;
import com.github.sachil.uplayer.upnp.dms.ContentTree;

import de.greenrobot.event.EventBus;

public class NavManager implements ExpandableListView.OnChildClickListener,
		View.OnClickListener {

	private static final String TAG = NavManager.class.getSimpleName();
	private Context mContext = null;
	private DrawerLayout mDrawerLayout = null;
	private PatchedExpandableListView mRenderer = null;
	private PatchedExpandableListView mServer = null;
	private PatchedExpandableListView mMedia = null;
	private NavAdapter mRendererAdapter = null;
	private NavAdapter mServerAdapter = null;
	private NavAdapter mMediaAdapter = null;
	private List<Device> mRendererList = null;
	private List<Device> mServerList = null;
	private List<ContentItem> mMediaList = null;
	private AndroidUpnpService mUpnpService = null;

	public NavManager(Context context, View contentView,
			AndroidUpnpService upnpService) {
		mContext = context;
		mUpnpService = upnpService;
		mRendererList = new ArrayList<>();
		mServerList = new ArrayList<>();
		mMediaList = new ArrayList<>();
		if (!EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().register(this);
		createView(contentView);
	}

	@Override
	public boolean onChildClick(ExpandableListView listView, View view,
			int groupPosition, int childPosition, long id) {

		listView.collapseGroup(groupPosition);
		switch (listView.getId()) {

		case R.id.nav_renderer:
			mRendererAdapter.refresh(
					mRendererAdapter.getChild(groupPosition, childPosition),
					null);
			break;

		case R.id.nav_library:
			mServerAdapter.refresh(
					mServerAdapter.getChild(groupPosition, childPosition),
					null);
			loadMedia((Device) mServerAdapter.getChild(groupPosition,
					childPosition));
			break;

		case R.id.nav_media:
			mMediaAdapter.refresh(
					mMediaAdapter.getChild(groupPosition, childPosition), null);
			mDrawerLayout.closeDrawers();
			break;
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.nav_settings:

			break;
		case R.id.nav_exit:

			break;
		}
		mDrawerLayout.closeDrawers();
	}

	public void onEventMainThread(DeviceMessage message) {

		if (message.getDeviceType() == DeviceMessage.DEVICE_TYPE.DMR) {
			if (message.isAdd()) {
				int position = mRendererList.indexOf(message.getDevice());
				if (position >= 0) {
					mRendererList.remove(position);
					mRendererList.add(position, message.getDevice());
				} else
					mRendererList.add(message.getDevice());
			} else {
				if (mRendererList.contains(message.getDevice()))
					mRendererList.remove(message.getDevice());
			}
			mRendererAdapter.refresh(mRendererList.get(0), mRendererList);
		} else {
			if (message.isAdd()) {
				int position = mServerList.indexOf(message.getDevice());
				if (position >= 0) {
					mServerList.remove(position);
					mServerList.add(position, message.getDevice());
				} else
					mServerList.add(message.getDevice());
			} else {
				if (mServerList.contains(message.getDevice()))
					mServerList.remove(message.getDevice());
			}
			mServerAdapter.refresh(mServerList.get(0), mServerList);
			loadMedia((Device) mServerAdapter.getChild(0, 0));
		}
	}

	public void onEventMainThread(BrowseMessage message) {
		if (message.isRootNode()) {
			mMediaList = message.getItems();
			mMediaAdapter.refresh(mMediaList.get(0), mMediaList);
		}
	}

	public void clean() {
		if (EventBus.getDefault().isRegistered(this))
			EventBus.getDefault().unregister(this);
	}

	private void createView(View contentView) {
		mDrawerLayout = (DrawerLayout)contentView.findViewById(R.id.main_drawer);
		mRenderer = (PatchedExpandableListView) contentView
				.findViewById(R.id.nav_renderer);
		mRendererAdapter = new NavAdapter(mContext);
		mRenderer.setAdapter(mRendererAdapter);
		mRenderer.setOnChildClickListener(this);
		mServer = (PatchedExpandableListView) contentView
				.findViewById(R.id.nav_library);
		mServerAdapter = new NavAdapter(mContext);
		mServer.setAdapter(mServerAdapter);
		mServer.setOnChildClickListener(this);
		mMedia = (PatchedExpandableListView) contentView
				.findViewById(R.id.nav_media);
		mMediaAdapter = new NavAdapter(mContext);
		mMedia.setAdapter(mMediaAdapter);
		mMedia.setOnChildClickListener(this);

		contentView.findViewById(R.id.nav_settings).setOnClickListener(this);
		contentView.findViewById(R.id.nav_exit).setOnClickListener(this);

	}

	private void loadMedia(Device device) {
		Service service = device.findService(
				new UDAServiceType(UpnpUnity.SERVICE_CONTENT_DIRECTORY));
		if (service != null) {
			boolean isLocal = device instanceof RemoteDevice ? false : true;
			mUpnpService.getControlPoint()
					.execute(new BrowseCallback(service, ContentTree
							.getRootContentNode(isLocal).getContainer(),
					isLocal));
		}
	}
}

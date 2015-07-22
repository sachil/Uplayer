package com.github.sachil.uplayer.upnp.dmc;

import java.util.List;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import android.os.Handler;
import android.util.Log;

import com.github.sachil.uplayer.upnp.UpnpUnity;

public class BrowseCallback extends Browse {
	private static final String LOG_TAG = BrowseCallback.class.getSimpleName();

	private Handler mHandler = null;
	private List<ContentItem> mContentList = null;
	@SuppressWarnings("rawtypes")
	private Service mService = null;
	private boolean mIsLocal = false;

	@SuppressWarnings("rawtypes")
	public BrowseCallback(Handler handler, Service service,
			Container container, List<ContentItem> contentList,
			boolean isLocal) {
		super(service, container.getId(), BrowseFlag.DIRECT_CHILDREN, "*", 0,
				null, new SortCriterion(true, "dc:title"));
		mHandler = handler;
		mContentList = contentList;
		mService = service;
		mIsLocal = isLocal;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void received(ActionInvocation actionInvocation,
			final DIDLContent didl) {
		// TODO Auto-generated method stub
		mContentList.clear();
		for (Container container : didl.getContainers()) {
			Log.e(LOG_TAG,
					"Add child container:" + container.getClazz().getValue()
							+ "," + container.getTitle() + ","
							+ container.getId() + "," + container.getParentID());
			mContentList.add(new ContentItem(container, mService));
			if (!mIsLocal) {
				UpnpUnity.generateContainer(container.getId(),
						container.getTitle(), container.getParentID(),
						container.getClazz().toString(), false);
			}

		}

		for (Item item : didl.getItems()) {
			Log.i(LOG_TAG, "Add child item:" + item.getTitle());
			mContentList.add(new ContentItem(item, mService));
		}

		mHandler.sendEmptyMessage(UpnpUnity.REFRESH_LIBRARY_LIST);
	}

	@Override
	public void updateStatus(Status arg0) {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("rawtypes")
	@Override
	public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
		// TODO Auto-generated method stub

	}

}

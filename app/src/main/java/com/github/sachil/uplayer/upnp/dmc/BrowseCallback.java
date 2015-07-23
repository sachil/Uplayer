package com.github.sachil.uplayer.upnp.dmc;

import java.util.ArrayList;
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

import com.github.sachil.uplayer.message.BrowseMessage;
import com.github.sachil.uplayer.upnp.UpnpUnity;

import de.greenrobot.event.EventBus;

public class BrowseCallback extends Browse {
	private static final String TAG = BrowseCallback.class.getSimpleName();

	private List<ContentItem> mContentList = null;
	private Service mService = null;
	private boolean mIsLocal = false;
	private boolean mIsRootNode = false;

	public BrowseCallback(Service service, Container container,
			boolean isLocal) {

		super(service, container.getId(), BrowseFlag.DIRECT_CHILDREN, "*", 0,
				null, new SortCriterion(true, "dc:title"));

		mIsRootNode = container.getParentID() == null ? true : false;

		mContentList = new ArrayList<>();
		mService = service;
		mIsLocal = isLocal;
	}

	@Override
	public void received(ActionInvocation actionInvocation,
			final DIDLContent didl) {
		// TODO Auto-generated method stub
		mContentList.clear();
		for (Container container : didl.getContainers()) {
			mContentList.add(new ContentItem(container, mService));
			if (!mIsLocal) {
				UpnpUnity.generateContainer(container.getId(),
						container.getTitle(), container.getParentID(),
						container.getClazz().toString(), false);
			}
		}
		for (Item item : didl.getItems())
			mContentList.add(new ContentItem(item, mService));

		EventBus.getDefault()
				.post(new BrowseMessage(mContentList, mIsRootNode));

	}

	@Override
	public void updateStatus(Status arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
		// TODO Auto-generated method stub

	}
}

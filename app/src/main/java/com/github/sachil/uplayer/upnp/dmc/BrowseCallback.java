package com.github.sachil.uplayer.upnp.dmc;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.contentdirectory.callback.Browse;
import org.fourthline.cling.support.model.BrowseFlag;
import org.fourthline.cling.support.model.DIDLContent;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.SortCriterion;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

import com.github.sachil.uplayer.ui.message.BrowseMessage;
import com.github.sachil.uplayer.upnp.UpnpUnity;

import de.greenrobot.event.EventBus;

public class BrowseCallback extends Browse {
	private static final String TAG = BrowseCallback.class.getSimpleName();

	private List<DIDLObject> mContentList = null;
	private Service mService = null;
	private boolean mIsLocal = false;
	private boolean mIsRootNode = false;

	public BrowseCallback(Service service, Container container,
			boolean isLocal) {

		super(service, container.getId(), BrowseFlag.DIRECT_CHILDREN, "*", 0,
				null, new SortCriterion(true, "dc:title"));
		mIsRootNode = container.getParentID() == null;
		mContentList = new ArrayList<>();
		mService = service;
		mIsLocal = isLocal;
	}

	@Override
	public void received(ActionInvocation actionInvocation,
			final DIDLContent didl) {
		mContentList.clear();
		for (Container container : didl.getContainers()) {
			mContentList.add(container);
			if (!mIsLocal) {
				UpnpUnity.generateContainer(container.getId(),
						container.getTitle(), container.getParentID(),
						container.getClazz().toString(), false);
			}
		}
		for (Item item : didl.getItems())
			mContentList.add(item);
		sortList();
		EventBus.getDefault()
				.post(new BrowseMessage(mContentList, mIsRootNode));

	}

	@Override
	public void updateStatus(Status arg0) {
	}

	@Override
	public void failure(ActionInvocation arg0, UpnpResponse arg1, String arg2) {
	}

	private void sortList() {

		Comparator<DIDLObject> comparator = new Comparator<DIDLObject>() {
			@Override
			public int compare(DIDLObject obj1, DIDLObject obj2) {

				if ((obj1 instanceof Container) && (obj2 instanceof Container))
					return obj1.getTitle().compareToIgnoreCase(obj2.getTitle());
				else if (!(obj1 instanceof Container)
						&& !(obj2 instanceof Container))
					return obj1.getTitle().compareToIgnoreCase(obj2.getTitle());
				else if (obj1 instanceof Container)
					return -1;
				else
					return 1;
			}
		};
		Collections.sort(mContentList, comparator);
	}
}

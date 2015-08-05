package com.github.sachil.uplayer.upnp.dmc;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;

public class ContentItem {

	private Service<Device, Service> mService = null;
	private DIDLObject mObject = null;
	private String mId = null;
	private boolean mIsContainer = false;

	public ContentItem(Container container, Service<Device, Service> service) {
		mObject = container;
		mService = service;
		if (container != null)
			mId = container.getId();
		mIsContainer = true;
	}

	public ContentItem(Item item, Service<Device, Service> service) {
		mObject = item;
		mService = service;
		if (item != null)
			mId = item.getId();
		mIsContainer = false;
	}

	public Container getContainer() {
		if (mIsContainer)
			return (Container) mObject;
		else
			return null;
	}

	public Item getItem() {
		if (!mIsContainer)
			return (Item) mObject;
		else
			return null;
	}

	public Service<Device, Service> getService() {

		return mService;
	}

	public boolean isContainer() {

		return mIsContainer;
	}

	public String getID() {

		return mId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ContentItem that = (ContentItem) o;
		return mId.equals(that.mId);
	}

	@Override
	public int hashCode() {

		return mObject.hashCode();
	}

	@Override
	public String toString() {

		return mObject.getTitle();
	}

}

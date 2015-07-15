package com.github.sachil.uplayer.upnp.dmc;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;



public class ContentItem {

	@SuppressWarnings("rawtypes")
	private Service<Device, Service> mService = null;
	private DIDLObject mObject = null;
	private String mId = null;
	private boolean mIsContainer = false;

	@SuppressWarnings("rawtypes")
	public ContentItem(Container container, Service<Device, Service> service) {
		mObject = container;
		mService = service;
		if (container != null)
			mId = container.getId();
		mIsContainer = true;
	}

	@SuppressWarnings("rawtypes")
	public ContentItem(Item item, Service<Device, Service> service) {
		mObject = item;
		mService = service;
		if (item != null)
			mId = item.getId();
		mIsContainer = false;
	}

	public Container getContainer() {
		if (mIsContainer == true)
			return (Container) mObject;
		else
			return null;
	}

	public Item getItem() {
		if (mIsContainer == false)
			return (Item) mObject;
		else
			return null;
	}

	@SuppressWarnings("rawtypes")
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

		if (!mId.equals(that.mId))
			return false;
		else
			return true;
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

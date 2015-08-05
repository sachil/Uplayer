package com.github.sachil.uplayer.upnp.dms;

import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;


/**
 * ContentNode是容器(Container)与内容(Item)的一个抽象，其目的是便于将
 * 容器与内容挂载到ContentTree上。
 *
 */
public class ContentNode {
	private Container mContainer = null;
	private Item mItem = null;
	private String mId = null;
	private String mPath = null;
	private boolean mIsItem = false;

	public ContentNode(String id, Container container) {
		mId = id;
		mContainer = container;
		mIsItem = false;
	}

	public ContentNode(String id, Item item, String path) {
		mId = id;
		mItem = item;
		mPath = path;
		mIsItem = true;

	}

	public String getId() {

		return mId;
	}

	public Container getContainer() {

		return mContainer;
	}

	public Item getItem() {

		return mItem;
	}

	public String getPath() {

		if (mIsItem && mPath != null)
			return mPath;
		else
			return null;
	}

	public boolean isItem() {

		return mIsItem;
	}

}

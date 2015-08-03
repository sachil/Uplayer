package com.github.sachil.uplayer.upnp.dms;

import com.github.sachil.uplayer.Utils;
import com.github.sachil.uplayer.upnp.UpnpUnity;

import java.util.HashMap;

import org.fourthline.cling.support.model.WriteStatus;
import org.fourthline.cling.support.model.container.Container;

/**
 * ContentTree用于展示和管理dms上的目录结构以及具体内容，ContentTree至少包含一个id号为0的
 * 容器(Container)，它是ContentTree的根节点，所有的目录和具体内容都需要挂载在该根节点下。
 * 
 * @author 20001962
 *
 */
public class ContentTree {
	
	/**
	 * 利用两个hashmap来分别管理本地dms上的内容和远端dms上的内容的目的是：在浏览dms上的内容的
	 * 时候按back键能够回退到当前目录的父目录继续进行浏览。由于本地dms的目录结构是在创建dms的
	 * 时候确定的，而远端dms的目录结构是在浏览的过程中一步一步确定的，所以在切换dms的过程中，并
	 * 不希望重新生成本地dms的目录结构(因为它的目录结构是固定的，且这是一项较耗时的工作)，所以
	 * 只需生成一次并将它保存。而远端dms则需要在切换dms的时候，将数据清空，并重新生成。
	 */
	private static HashMap<String, ContentNode> mHashMap = new HashMap<String, ContentNode>();
	private static HashMap<String, ContentNode> mLocalHashMap = new HashMap<String, ContentNode>();
	private static ContentNode mRemoteRootContentNode = createRootNode();
	private static ContentNode mLocalRootContentNode = createRootNode();

	protected static ContentNode createRootNode() {
		Container rootContainer = new Container();
		rootContainer.setId(UpnpUnity.CONTENT_ROOT_ID);
		rootContainer.setTitle(Utils.APP_NAME);
		rootContainer.setCreator(UpnpUnity.MANUFACTURER);
		rootContainer.setSearchable(true);
		rootContainer.setRestricted(true);
		rootContainer.setChildCount(0);
		rootContainer.setWriteStatus(WriteStatus.NOT_WRITABLE);
		ContentNode rootNode = new ContentNode(UpnpUnity.CONTENT_ROOT_ID,
				rootContainer);

		return rootNode;

	}

	public static void addRootContentNode() {
		if (!mHashMap.containsKey(UpnpUnity.CONTENT_ROOT_ID))
			mHashMap.put(UpnpUnity.CONTENT_ROOT_ID, mRemoteRootContentNode);
		if (!mLocalHashMap.containsKey(UpnpUnity.CONTENT_ROOT_ID))
			mLocalHashMap.put(UpnpUnity.CONTENT_ROOT_ID, mLocalRootContentNode);
	}

	public static ContentNode getRootContentNode(boolean isLocal) {
		if(isLocal)
			return mLocalRootContentNode;
		else
			return mRemoteRootContentNode;
	}

	public static ContentNode getContentNode(String id, boolean isLocal) {
		if (isLocal) {
			if (mLocalHashMap.containsKey(id))
				return mLocalHashMap.get(id);
			else
				return null;
		} else {
			if (mHashMap.containsKey(id))
				return mHashMap.get(id);
			else
				return null;
		}

	}

	public static boolean hasContentNode(String id, boolean isLocal) {
		if (isLocal) {
			if (mLocalHashMap.containsKey(id))
				return true;
			else
				return false;
		} else {
			if (mHashMap.containsKey(id))
				return true;
			else
				return false;
		}
	}

	public static void addContentNode(String id, ContentNode node,
			boolean isLocal) {
		if (isLocal) {
			if (!mLocalHashMap.containsKey(id))
				mLocalHashMap.put(id, node);
		} else {
			if (!mHashMap.containsKey(id))
				mHashMap.put(id, node);
		}
	}

	public static void removeContentNode(String id, boolean isLcoal) {
		if (isLcoal) {
			if (mLocalHashMap.containsKey(id))
				mLocalHashMap.remove(id);
		} else {
			if (mHashMap.containsKey(id))
				mHashMap.remove(id);
		}

	}
	
	/**
	 * 只会清理远程dms的目录结构，本地dms的目录结构将保留。
	 */
	public static void clear() {
		mHashMap.clear();
		mRemoteRootContentNode = null;
		mRemoteRootContentNode = createRootNode();
		mHashMap.put(UpnpUnity.CONTENT_ROOT_ID, mRemoteRootContentNode);
	}

}

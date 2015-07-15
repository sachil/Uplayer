package com.github.sachil.uplayer.upnp;

import com.github.sachil.uplayer.upnp.dms.ContentNode;
import com.github.sachil.uplayer.upnp.dms.ContentTree;

import java.text.DecimalFormat;
import java.util.UUID;

import org.fourthline.cling.model.types.UDN;
import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.WriteStatus;
import org.fourthline.cling.support.model.container.Container;

public class UpnpUnity {

	public static final String MANUFACTURER = "AlphaNetworks";
	public static UDN S_UDN = new UDN(UUID.randomUUID());
	public static UDN R_UDN = new UDN(UUID.randomUUID());
	public static final String MANUFACTURER_URL = "http://www.alphanetworks.com/";
	public static final String DMS_MODEL_NAME = "Uplayer Media Server";
	public static final String DMR_MODEL_NAME = "Uplayer Media Renderer";
	public static final String DMS_MODEL_DESCRIPTION = DMS_MODEL_NAME;
	public static final String DMR_MODEL_DESCRIPTION = DMR_MODEL_NAME;
	public static final String MODEL_NUMBER = "v1.0";

	public static final String DMS = "MediaServer";
	public static final String DMC = "";
	public static final String DMR = "MediaRenderer";
	public static final int VERSION = 1;

	public static final String DMS_NAME = "Uplayer Media Server";
	public static final String DMR_NAME = "Uplayer Media Renderer";

	public static final String SERVICE_CONTENT_DIRECTORY = "ContentDirectory";
	public static final String SERVICE_AVTRANSPORT = "AVTransport";
	public static final String SERVICE_CONNECTION_MANAGER = "ConnectionManager";
	public static final String SERVICE_RENDERING_CONTROL = "RenderingControl";

	public static final int REFRESH_DMR_LIST = 0x01;
	public static final int REFRESH_DMS_LIST = 0x02;
	public static final int REFRESH_LIBRARY_LIST = 0x03;
	public static final int REFRESH_LASTCHANGE = 0x04;
	public static final int REFRESH_ALBUMART = 0x05;
	public static final int REFRESH_CURRENT_POSITION = 0x06;

	public static final String CONTENT_ROOT_ID = "0";
	public static final String CONTENT_AUDIO_ID = "audio";
	public static final String CONTENT_VIDEO_ID = "video";
	public static final String CONTENT_IMAGE_ID = "image";

	public static final String AUDIO_Album_ID = "audio_album_id";
	public static final String AUDIO_Artist_ID = "audio_artist_id";
	public static final String AUDIO_TRACK_ID = "audio_track_id";
	
	public static final int PORT = 8087;

	/**
	 * 创建Container（容器）
	 * @param id container的id号
	 * @param title container的名称
	 * @param parentID container的父container的id号
	 * @param clazz upnp协议中有定义，请自行查看
	 * @param isLocal 是否在本地dms上创建
	 * @return 创建好的container
	 */
	public static Container generateContainer(String id, String title,
			String parentID, String clazz, boolean isLocal) {
		Container container = new Container();
		container.setId(id);
		container.setTitle(title);
		container.setParentID(parentID);
		container.setChildCount(0);
		container.setRestricted(true);
		container.setClazz(new DIDLObject.Class(clazz));
		container.setSearchable(true);
		container.setWriteStatus(WriteStatus.NOT_WRITABLE);
		ContentTree.addRootContentNode();
		ContentTree.getContentNode(parentID, isLocal).getContainer()
				.addContainer(container);
		ContentTree
				.getContentNode(parentID, isLocal)
				.getContainer()
				.setChildCount(
						ContentTree.getContentNode(parentID, isLocal)
								.getContainer().getChildCount() + 1);
		ContentTree.addContentNode(id, new ContentNode(id, container), isLocal);
		return container;
	}

	public static float getTimeToSeconds(String time) {
		if (time != null && time.contains(":")) {
			float seconds = 0;
			int index = 0;
			String lastString = time;
			int hour = Integer.parseInt(lastString.substring(0,
					lastString.indexOf(":")));
			index = lastString.indexOf(":") + 1;
			lastString = lastString.substring(index);
			int minute = Integer.parseInt(lastString.substring(0,
					lastString.indexOf(":")));
			index = lastString.indexOf(":") + 1;
			lastString = lastString.substring(index);
			int second = 0;
			int mSeconds = 0;
			if (lastString.contains(".")) {
				second = Integer.parseInt(lastString.substring(0,
						lastString.indexOf(".")));
				index = lastString.indexOf(".") + 1;
				lastString = lastString.substring(index);
				mSeconds = Integer.parseInt(lastString.substring(0));
			} else {
				second = Integer.parseInt(lastString.substring(0));
				mSeconds = 0;
			}
			seconds = hour * 60 * 60 + minute * 60 + second + (float) mSeconds
					/ 1000;
			return seconds;
		} else
			return 0;

	}

	public static String getSizeTostring(int size) {
		DecimalFormat format = new DecimalFormat("#.##");
		if (size < 1024 * 1024 * 1024)
			return format.format((float) size / (1024 * 1024)) + "M";
		else
			return format.format((float) size / (1024 * 1024 * 1024)) + "G";
	}
}

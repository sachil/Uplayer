package com.github.sachil.uplayer.ui.message;

import org.fourthline.cling.model.meta.Device;

/**
 * Created by 20001962 on 2015/7/23.
 */
public class DeviceMessage {

	public enum DEVICE_TYPE {
		UNKNOWN, DMS, DMR
	}

	private DEVICE_TYPE mType = DEVICE_TYPE.UNKNOWN;
	private Device mDevice = null;
	private boolean mIsAdd = false;

	public DeviceMessage(Device device,DEVICE_TYPE type,boolean isAdd) {
        mDevice = device;
        mType = type;
        mIsAdd = isAdd;
	}

	public DEVICE_TYPE getDeviceType() {

		return mType;
	}

	public Device getDevice() {

		return mDevice;
	}

	public boolean isAdd() {

		return mIsAdd;
	}

}

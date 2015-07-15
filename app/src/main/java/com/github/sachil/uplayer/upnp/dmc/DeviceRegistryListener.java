package com.github.sachil.uplayer.upnp.dmc;

import java.util.ArrayList;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;


import android.os.Handler;
import android.util.Log;

import com.github.sachil.uplayer.upnp.UpnpUnity;

public class DeviceRegistryListener extends DefaultRegistryListener {
	private static final String LOG_TAG = DeviceRegistryListener.class
			.getSimpleName();

	@SuppressWarnings("rawtypes")
	private ArrayList<Device> mDMSDeviceList = null;
	@SuppressWarnings("rawtypes")
	private ArrayList<Device> mDMRDeviceList = null;
	private Handler mHandler = null;

	@SuppressWarnings("rawtypes")
	public DeviceRegistryListener(Handler handler, ArrayList<Device> dmrList,
			ArrayList<Device> dmsList) {
		mHandler = handler;
		mDMRDeviceList = dmrList;
		mDMSDeviceList = dmsList;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void deviceAdded(Registry registry, Device device) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "One device is added."+device.getDetails().getFriendlyName());
		if (device.hasServices())
			refreshDevice(device, true);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void deviceRemoved(Registry registry, Device device) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "One device is removed.");
		if (device.hasServices())
			refreshDevice(device, false);
	}

	@SuppressWarnings("rawtypes")
	public void refreshDevice(Device device, boolean isAdd) {
		if (device.getType().getType().equalsIgnoreCase(UpnpUnity.DMR)) {
			if (isAdd) {
				if (mDMRDeviceList != null) {
					int position = mDMRDeviceList.indexOf(device);
					if (position >= 0) {
						mDMRDeviceList.remove(position);
						mDMRDeviceList.add(position, device);
					} else
						mDMRDeviceList.add(device);
				}
			} else {
				if (mDMRDeviceList != null)
					mDMRDeviceList.remove(device);

			}
			mHandler.sendEmptyMessage(UpnpUnity.REFRESH_DMR_LIST);
		} else if(device.getType().getType().equalsIgnoreCase(UpnpUnity.DMS)){
			if (isAdd) {
				if (mDMSDeviceList != null) {
					int position = mDMSDeviceList.indexOf(device);
					if (position >= 0) {
						mDMSDeviceList.remove(position);
						mDMSDeviceList.add(position, device);
					} else
						mDMSDeviceList.add(device);
				}
			} else {
				if (mDMSDeviceList != null)
					mDMSDeviceList.remove(device);
			}
		}
		mHandler.sendEmptyMessage(UpnpUnity.REFRESH_DMS_LIST);
	}
}

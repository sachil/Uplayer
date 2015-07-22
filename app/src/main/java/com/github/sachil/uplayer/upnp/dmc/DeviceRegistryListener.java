package com.github.sachil.uplayer.upnp.dmc;

import android.os.Handler;
import android.util.Log;

import com.github.sachil.uplayer.upnp.UpnpUnity;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import java.util.List;

public class DeviceRegistryListener extends DefaultRegistryListener {
    private static final String TAG = DeviceRegistryListener.class
            .getSimpleName();

    private List<Device> mDMSDeviceList = null;
    private List<Device> mDMRDeviceList = null;
    private Handler mHandler = null;

    public DeviceRegistryListener(Handler handler, List<Device> dmrList,
                                  List<Device> dmsList) {

        mHandler = handler;
        mDMRDeviceList = dmrList;
        mDMSDeviceList = dmsList;
    }

    @Override
    public void deviceAdded(Registry registry, Device device) {
        Log.e(TAG, "One device is added." + device.getDetails().getFriendlyName());
        if (device.hasServices())
            refreshDevice(device, true);
    }

    @Override
    public void deviceRemoved(Registry registry, Device device) {
        Log.e(TAG, "One device is removed.");
        if (device.hasServices())
            refreshDevice(device, false);
    }

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
        } else if (device.getType().getType().equalsIgnoreCase(UpnpUnity.DMS)) {
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

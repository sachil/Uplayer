package com.github.sachil.uplayer.upnp.dmc;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

import com.github.sachil.uplayer.ui.message.DeviceMessage;
import com.github.sachil.uplayer.ui.message.DeviceMessage.DEVICE_TYPE;
import com.github.sachil.uplayer.upnp.UpnpUnity;

import de.greenrobot.event.EventBus;

public class DeviceRegistryListener extends DefaultRegistryListener {
	private static final String TAG = DeviceRegistryListener.class
			.getSimpleName();

	@Override
	public void deviceAdded(Registry registry, Device device) {
		if (device.hasServices())
			refreshDevice(device, true);
	}

	@Override
	public void deviceRemoved(Registry registry, Device device) {
		if (device.hasServices())
			refreshDevice(device, false);
	}

	public void refreshDevice(Device device, boolean isAdd) {
		if (device.getType().getType().equalsIgnoreCase(UpnpUnity.DMR))
			EventBus.getDefault()
					.post(new DeviceMessage(device, DEVICE_TYPE.DMR, isAdd));
		else if (device.getType().getType().equalsIgnoreCase(UpnpUnity.DMS))
			EventBus.getDefault()
					.post(new DeviceMessage(device, DEVICE_TYPE.DMS, isAdd));
	}
}

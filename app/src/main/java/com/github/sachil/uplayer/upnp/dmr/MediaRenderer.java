package com.github.sachil.uplayer.upnp.dmr;

import java.io.ByteArrayOutputStream;
import java.net.URI;

import org.fourthline.cling.binding.LocalServiceBinder;
import org.fourthline.cling.binding.annotations.AnnotationLocalServiceBinder;
import org.fourthline.cling.model.DefaultServiceManager;
import org.fourthline.cling.model.ServiceManager;
import org.fourthline.cling.model.ValidationException;
import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.DeviceIdentity;
import org.fourthline.cling.model.meta.Icon;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.LocalService;
import org.fourthline.cling.model.meta.ManufacturerDetails;
import org.fourthline.cling.model.meta.ModelDetails;
import org.fourthline.cling.model.types.DeviceType;
import org.fourthline.cling.model.types.UDADeviceType;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.lastchange.LastChangeAwareServiceManager;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.upnp.UpnpUnity;

/**
 * 创建一个dmr，一个dmr设备包含3个service：AVTransportService、ConnectionManagerService和
 * RendererControlService，其中最关键的是AVTransportService，它主要实现dmc与dmr的交互工作，
 * 如：setUri、play、stop等。其次是RendererControlService，它主要实现对dmr硬件环境的调节与
 * 获取，如：设置静音setmute、设置音量setVolume等。ConnectionManagerService，主要管理连接，
 * 在使用http-get方式时，该service显得并不是那么重要。
 *
 */
public class MediaRenderer {
	
	private static final String LOG_TAG = MediaRenderer.class.getSimpleName();
	private static final int TIME_OUT = 2000;
	private LocalDevice mLocalDevice = null;
	private LastChange mAVTransportLastChange = new LastChange(
			new AVTransportLastChangeParser());
	private LastChange mRendererControlLastChange = new LastChange(
			new RenderingControlLastChangeParser());
	private LastChangeAwareServiceManager<AvtransportService> mAVtransportServiceManager = null;
	private LastChangeAwareServiceManager<RenderingControlService> mRenderercontrolServiceManager = null;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MediaRenderer(final Context context) {
		DeviceType type = new UDADeviceType(UpnpUnity.DMR, UpnpUnity.VERSION);
		DeviceDetails details = new DeviceDetails(
				UpnpUnity.DMR_NAME + "(" + android.os.Build.MODEL + ")",
				new ManufacturerDetails(UpnpUnity.MANUFACTURER,
						UpnpUnity.MANUFACTURER_URL),
				new ModelDetails(UpnpUnity.DMR_MODEL_NAME,
						UpnpUnity.DMR_MODEL_DESCRIPTION, UpnpUnity.MODEL_NUMBER));
		LocalServiceBinder binder = new AnnotationLocalServiceBinder();
		LocalService<ConnectionService> connectionManagerService = binder
				.read(ConnectionService.class);
		ServiceManager<ConnectionService> connectionManager = new DefaultServiceManager<>(
				connectionManagerService, ConnectionService.class);
		connectionManagerService.setManager(connectionManager);

		LocalService<AvtransportService> avtransportService = binder
				.read(AvtransportService.class);
		 mAVtransportServiceManager = new LastChangeAwareServiceManager<AvtransportService>(
				avtransportService, new AVTransportLastChangeParser()) {
			@Override
			protected AvtransportService createServiceInstance()
					throws Exception {
				return new AvtransportService(mAVTransportLastChange);
			}

			@Override
			protected int getLockTimeoutMillis() {
				return TIME_OUT;
			}
		};
		avtransportService.setManager(mAVtransportServiceManager);

		LocalService<RenderingControlService> renderercontrolService = binder
				.read(RenderingControlService.class);
		 mRenderercontrolServiceManager = new LastChangeAwareServiceManager<RenderingControlService>(
				renderercontrolService, new RenderingControlLastChangeParser()) {
			@Override
			protected RenderingControlService createServiceInstance()
					throws Exception {
				return new RenderingControlService(context,mRendererControlLastChange);
			}

			@Override
			protected int getLockTimeoutMillis() {
				return TIME_OUT;
			}
		};
		renderercontrolService.setManager(mRenderercontrolServiceManager);

		try {
			LocalService serviceList[] = { connectionManagerService,
					avtransportService, renderercontrolService };
			mLocalDevice = new LocalDevice(new DeviceIdentity(UpnpUnity.R_UDN),
					type, details,
					new Icon[] { createDefaultDeviceIcon(context) },
					serviceList);
		} catch (ValidationException e) {
			e.printStackTrace();
		}
	}

	public LocalDevice getDevice() {

		return mLocalDevice;
	}

	//创建该设备的图标
	private Icon createDefaultDeviceIcon(Context context) {
		BitmapDrawable bitDw = ((BitmapDrawable) context.getResources()
				.getDrawable(R.drawable.ic_launcher));
		Bitmap bitmap = bitDw.getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] imageInByte = stream.toByteArray();
		return new Icon("image/png", 48, 48, 8, URI.create("icon.png")
				.toString(), imageInByte);
	}

}

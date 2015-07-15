package com.github.sachil.uplayer.upnp.dms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;

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
import org.fourthline.cling.support.connectionmanager.ConnectionManagerService;
import org.fourthline.cling.support.model.ProtocolInfo;
import org.fourthline.cling.support.model.ProtocolInfos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;

import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.upnp.UpnpUnity;

public class MediaServer {

	private static final String LOG_TAG = MediaServer.class.getSimpleName();
	private static InetAddress mLocalAddress = null;
	private HttpServer mHttpServer = null;
	private LocalDevice mLocalDevice = null;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public MediaServer(Context context, InetAddress address) {
		DeviceType type = new UDADeviceType(UpnpUnity.DMS, UpnpUnity.VERSION);
		DeviceDetails details = new DeviceDetails(
				UpnpUnity.DMS_NAME + "(" + android.os.Build.MODEL + ")",
				new ManufacturerDetails(UpnpUnity.MANUFACTURER,
						UpnpUnity.MANUFACTURER_URL),
				new ModelDetails(UpnpUnity.DMS_MODEL_NAME,
						UpnpUnity.DMS_MODEL_DESCRIPTION, UpnpUnity.MODEL_NUMBER));
		LocalService<ContentDirectoryService> directoryService = new AnnotationLocalServiceBinder()
				.read(ContentDirectoryService.class);
		ServiceManager<ContentDirectoryService> directorManager = new DefaultServiceManager<ContentDirectoryService>(
				directoryService, ContentDirectoryService.class);
		directoryService.setManager(directorManager);

		LocalService<ConnectionManagerService> connectionService = new AnnotationLocalServiceBinder()
				.read(ConnectionManagerService.class);
		ServiceManager<ConnectionManagerService> connectionManager = new DefaultServiceManager<ConnectionManagerService>(
				connectionService, null) {

			@Override
			protected ConnectionManagerService createServiceInstance()
					throws Exception {
				// TODO Auto-generated method stub
				return new ConnectionManagerService(createProtocolInfos(), null);
			}

		};
		connectionService.setManager(connectionManager);

		try {
			LocalService serviceList[] = { directoryService, connectionService };

			mLocalDevice = new LocalDevice(new DeviceIdentity(UpnpUnity.S_UDN),
					type, details,
					new Icon[] { createDefaultDeviceIcon(context) },
					serviceList);

			mLocalAddress = address;
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {

						mHttpServer = new HttpServer(UpnpUnity.PORT);
						if (mHttpServer != null)
							mHttpServer.start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
			thread.start();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, "Create local device failed!");
			e.printStackTrace();
		}
	}

	public LocalDevice getDevice() {

		return mLocalDevice;
	}

	public void close() {
		if (mHttpServer != null) {
			mHttpServer.stop();
			mHttpServer = null;
		}
	}

	public String getAddress() {

		if (mLocalAddress != null)
			return mLocalAddress.getHostAddress() + ":" + UpnpUnity.PORT;
		else
			return null;
	}

	private ProtocolInfos createProtocolInfos() {
		ProtocolInfos infos = new ProtocolInfos("http-get:*:audio/mpeg:*");
		infos.add(new ProtocolInfo("http-get:*:audio/x-wav:*"));
		infos.add(new ProtocolInfo("http-get:*:audio/aac:*"));
		infos.add(new ProtocolInfo("http-get:*:image/bmp:*"));
		infos.add(new ProtocolInfo("http-get:*:image/jpeg:*"));
		infos.add(new ProtocolInfo("http-get:*:image/png:*"));
		infos.add(new ProtocolInfo("http-get:*:image/gif:*"));
		infos.add(new ProtocolInfo("http-get:*:video/mpeg:*"));
		infos.add(new ProtocolInfo("http-get:*:video/mp4:*"));
		infos.add(new ProtocolInfo("http-get:*:video/3gpp:*"));
		return infos;
	}

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

package com.github.sachil.uplayer.upnp.dmc;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.controlpoint.SubscriptionCallback;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.gena.GENASubscription;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UDAServiceType;
import org.fourthline.cling.support.avtransport.callback.GetPositionInfo;
import org.fourthline.cling.support.avtransport.callback.Pause;
import org.fourthline.cling.support.avtransport.callback.Play;
import org.fourthline.cling.support.avtransport.callback.Seek;
import org.fourthline.cling.support.avtransport.callback.SetAVTransportURI;
import org.fourthline.cling.support.avtransport.callback.Stop;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportLastChangeParser;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable.AVTransportURIMetaData;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable.CurrentPlayMode;
import org.fourthline.cling.support.avtransport.lastchange.AVTransportVariable.TransportState;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.PositionInfo;
import org.fourthline.cling.support.model.SeekMode;
import org.fourthline.cling.support.renderingcontrol.callback.GetMute;
import org.fourthline.cling.support.renderingcontrol.callback.GetVolume;
import org.fourthline.cling.support.renderingcontrol.callback.SetMute;
import org.fourthline.cling.support.renderingcontrol.callback.SetVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlLastChangeParser;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable.Mute;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable.Volume;
import org.fourthline.cling.model.gena.CancelReason;
import android.content.Context;
import android.util.Log;

import com.github.sachil.uplayer.UplayerUnity;
import com.github.sachil.uplayer.ui.message.PlayerMessage;
import com.github.sachil.uplayer.upnp.UpnpUnity;
import com.github.sachil.uplayer.upnp.dmc.XMLToMetadataParser.Metadata;

import de.greenrobot.event.EventBus;

public class Controller {
	private static final String TAG = Controller.class.getSimpleName();
	private static final int TIMEOUT = 5;
	private static final String LAST_CHANGE = "LastChange";
	private static final int DEFAULT_INSTANCE = 0;
	private Service mConnectionService = null;
	private Service mAvTransportService = null;
	private Service mRendererControlService = null;
	private AndroidUpnpService mAndroidUpnpService = null;
	private boolean mIsMute = false;
	private int mCurrentVolume = 0;
	private XMLToMetadataParser mXmlToMetadataparser = null;
	private Metadata mMetadata = null;
	private Context mContext = null;
	private SubscriptionCallback mAvSubscriptionCallback = null;
	private SubscriptionCallback mRenSubscriptionCallback = null;

	public Controller(Context context, AndroidUpnpService androidUpnpService,
			Device device) {
		mContext = context;
		mAndroidUpnpService = androidUpnpService;
		if (device != null) {
			mConnectionService = device.findService(
					new UDAServiceType(UpnpUnity.SERVICE_CONNECTION_MANAGER));
			mAvTransportService = device.findService(
					new UDAServiceType(UpnpUnity.SERVICE_AVTRANSPORT));
			mRendererControlService = device.findService(
					new UDAServiceType(UpnpUnity.SERVICE_RENDERING_CONTROL));
			registerLastChange();
		}
	}

	public void changeDevice(Device device) {
		if (device != null) {
			mConnectionService = null;
			mAvTransportService = null;
			mRendererControlService = null;
			mConnectionService = device.findService(
					new UDAServiceType(UpnpUnity.SERVICE_CONNECTION_MANAGER));
			mAvTransportService = device.findService(
					new UDAServiceType(UpnpUnity.SERVICE_AVTRANSPORT));
			mRendererControlService = device.findService(
					new UDAServiceType(UpnpUnity.SERVICE_RENDERING_CONTROL));
			registerLastChange();
		}
	}

	public void setUrl(String url, String metadata) {
		
		Log.e(TAG,"url:"+url+",metadata:"+metadata);
		
		
		
		SetAVTransportURI callback = new SetAVTransportURI(mAvTransportService,
				url, metadata) {

			@SuppressWarnings("rawtypes")
			@Override
			public void failure(ActionInvocation actionInvocation,
					UpnpResponse response, String message) {
				// TODO Auto-generated method stub
				Log.e(TAG, "Set play uri failed! the reason is:" + message
						+ "the response details is:" + response);
				UplayerUnity.showToast(mContext, message);
			}
		};
		if (mAvTransportService != null)
			mAndroidUpnpService.getControlPoint().execute(callback);

	}

	public void play() {
		Play callback = new Play(mAvTransportService) {

			@SuppressWarnings("rawtypes")
			@Override
			public void failure(ActionInvocation actionInvocation,
					UpnpResponse response, String message) {
				// TODO Auto-generated method stub
				Log.e(TAG, "Play failed! the reason is:" + message
						+ "the response details is:" + response);
				UplayerUnity.showToast(mContext, message);
			}
		};
		if (mAvTransportService != null)
			mAndroidUpnpService.getControlPoint().execute(callback);
	}

	public void pause() {
		Pause callback = new Pause(mAvTransportService) {

			@SuppressWarnings("rawtypes")
			@Override
			public void failure(ActionInvocation actionInvocation,
					UpnpResponse response, String message) {
				// TODO Auto-generated method stub
				Log.e(TAG, "Pause failed! the reason is:" + message
						+ "the response details is:" + response);
				UplayerUnity.showToast(mContext, message);
			}
		};
		if (mAvTransportService != null)
			mAndroidUpnpService.getControlPoint().execute(callback);
	}

	public void stop() {
		Stop callback = new Stop(mAvTransportService) {

			@SuppressWarnings("rawtypes")
			@Override
			public void failure(ActionInvocation actionInvocation,
					UpnpResponse response, String message) {
				// TODO Auto-generated method stub
				Log.e(TAG, "Stop failed! the reason is:" + message
						+ "the response details is:" + response);
				UplayerUnity.showToast(mContext, message);
			}
		};

		if (mAvTransportService != null)
			mAndroidUpnpService.getControlPoint().execute(callback);
	}

	public void getPosition() {
		GetPositionInfo callback = new GetPositionInfo(mAvTransportService) {

			@SuppressWarnings("rawtypes")
			@Override
			public void failure(ActionInvocation actionInvocation,
					UpnpResponse response, String message) {
				// TODO Auto-generated method stub
				Log.e(TAG, "Get position failed! the reason is:" + message
						+ "the response details is:" + response);
				UplayerUnity.showToast(mContext, message);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void received(ActionInvocation actionInvocation,
					PositionInfo positionInfo) {
				// TODO Auto-generated method stub
				EventBus.getDefault().post(new PlayerMessage(
						PlayerMessage.REFRESH_CURRENT_POSITION, positionInfo));

			}
		};
		if (mAvTransportService != null)
			mAndroidUpnpService.getControlPoint().execute(callback);
	}

	public void seek(SeekMode mode, String target) {
		Seek callback = new Seek(mAvTransportService, mode, target) {

			@SuppressWarnings("rawtypes")
			@Override
			public void failure(ActionInvocation actionInvocation,
					UpnpResponse response, String message) {
				// TODO Auto-generated method stub
				Log.e(TAG, "Seek failed! the reason is:" + message
						+ "the response details is:" + response);
				UplayerUnity.showToast(mContext, message);
			}
		};
		if (mAvTransportService != null)
			mAndroidUpnpService.getControlPoint().execute(callback);
	}

	public boolean getMute() {
		GetMute callback = new GetMute(mRendererControlService) {

			@SuppressWarnings("rawtypes")
			@Override
			public void failure(ActionInvocation actionInvocation,
					UpnpResponse response, String message) {
				// TODO Auto-generated method stub
				Log.e(TAG, "GetMute failed! the reason is:" + message
						+ "the response details is:" + response);
				UplayerUnity.showToast(mContext, message);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void received(ActionInvocation actionInvocation,
					boolean currentMute) {
				// TODO Auto-generated method stub
				mIsMute = currentMute;
			}
		};
		if (mRendererControlService != null)
			mAndroidUpnpService.getControlPoint().execute(callback);

		return mIsMute;
	}

	public void setMute(boolean desiredMute) {
		SetMute callback = new SetMute(mRendererControlService, desiredMute) {

			@SuppressWarnings("rawtypes")
			@Override
			public void failure(ActionInvocation actionInvocation,
					UpnpResponse response, String message) {
				// TODO Auto-generated method stub
				Log.e(TAG, "SetMute failed! the reason is:" + message
						+ "the response details is:" + response);
				UplayerUnity.showToast(mContext, message);
			}
		};
		if (mRendererControlService != null)
			mAndroidUpnpService.getControlPoint().execute(callback);
	}

	public int getVolume() {
		GetVolume callback = new GetVolume(mRendererControlService) {

			@SuppressWarnings("rawtypes")
			@Override
			public void failure(ActionInvocation actionInvocation,
					UpnpResponse response, String message) {
				// TODO Auto-generated method stub
				Log.e(TAG, "GetVolume failed! the reason is:" + message
						+ "the response details is:" + response);
				UplayerUnity.showToast(mContext, message);
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void received(ActionInvocation actionInvocation,
					int currentVolume) {
				// TODO Auto-generated method stub
				mCurrentVolume = currentVolume;
			}
		};
		if (mRendererControlService != null)
			mAndroidUpnpService.getControlPoint().execute(callback);

		return mCurrentVolume;
	}

	public void setVolume(long newVolume) {
		SetVolume callback = new SetVolume(mRendererControlService, newVolume) {

			@SuppressWarnings("rawtypes")
			@Override
			public void failure(ActionInvocation actionInvocation,
					UpnpResponse response, String message) {
				// TODO Auto-generated method stub
				Log.e(TAG, "SetVolume failed! the reason is:" + message
						+ "the response details is:" + response);
				UplayerUnity.showToast(mContext, message);
			}
		};
		if (mRendererControlService != null)
			mAndroidUpnpService.getControlPoint().execute(callback);
	}

	public Metadata getMetadata() {

		return mMetadata;
	}

	private void registerLastChange() {

		if (mXmlToMetadataparser == null)
			mXmlToMetadataparser = new XMLToMetadataParser();
		mMetadata = mXmlToMetadataparser.getDefaultMetadata();

		if (mAvSubscriptionCallback != null)
			mAvSubscriptionCallback.end();
		if (mRenSubscriptionCallback != null)
			mRenSubscriptionCallback.end();

		mAvSubscriptionCallback = new SubscriptionCallback(mAvTransportService,
				TIMEOUT) {

			@SuppressWarnings("rawtypes")
			@Override
			protected void failed(GENASubscription subscription,
					UpnpResponse responseStatus, Exception exception,
					String defaultMsg) {
				// TODO Auto-generated method stub
				Log.e(TAG,
						"avTransportCallback:Subscribe events failed!The reason is:"
								+ createDefaultFailureMessage(responseStatus,
										exception));
				UplayerUnity.showToast(mContext,
						createDefaultFailureMessage(responseStatus, exception));
			}

			@SuppressWarnings("rawtypes")
			@Override
			protected void eventsMissed(GENASubscription subscription,
					int numberOfMissedEvents) {
				// TODO Auto-generated method stub
				Log.e(TAG,
						"avTransportCallback:Missed events,the number is:"
								+ numberOfMissedEvents);
			}

			@SuppressWarnings("rawtypes")
			@Override
			protected void eventReceived(GENASubscription subscription) {
				// TODO Auto-generated method stub
				try {
					LastChange lastChange = new LastChange(
							new AVTransportLastChangeParser(),
							subscription.getCurrentValues().get(LAST_CHANGE)
									.toString());
					if (lastChange != null) {

						AVTransportURIMetaData avTransportURIMetaData = lastChange
								.getEventedValue(DEFAULT_INSTANCE,
										AVTransportVariable.AVTransportURIMetaData.class);
						TransportState transportState = lastChange
								.getEventedValue(DEFAULT_INSTANCE,
										AVTransportVariable.TransportState.class);
						CurrentPlayMode currentPlayMode = lastChange
								.getEventedValue(DEFAULT_INSTANCE,
										AVTransportVariable.CurrentPlayMode.class);
						if (avTransportURIMetaData != null
								&& avTransportURIMetaData.getValue() != null) {
							String metadata = avTransportURIMetaData.getValue();
							mXmlToMetadataparser.parseXmlToMetadata(mMetadata,
									metadata);

						}

						if (transportState != null) {
							mMetadata.setState(transportState.getValue());
						}

						if (currentPlayMode != null) {
							mMetadata.setPlayMode(currentPlayMode.getValue());
						}

						EventBus.getDefault().post(new PlayerMessage(
								PlayerMessage.REFRESH_METADATA, mMetadata));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			@SuppressWarnings("rawtypes")
			@Override
			protected void established(GENASubscription subscription) {
				// TODO Auto-generated method stub
				Log.i(TAG, "avTransportCallback:Established,the id is:"
						+ subscription.getSubscriptionId());
			}

			@SuppressWarnings("rawtypes")
			@Override
			protected void ended(GENASubscription subscription,
					CancelReason reason, UpnpResponse responseStatus) {
				// TODO Auto-generated method stub
				Log.e(TAG,
						"avTransportCallback:Subscribe events is ended!");
			}
		};

		mRenSubscriptionCallback = new SubscriptionCallback(
				mRendererControlService, TIMEOUT) {

			@SuppressWarnings("rawtypes")
			@Override
			protected void failed(GENASubscription subscription,
					UpnpResponse responseStatus, Exception exception,
					String defaultMsg) {
				// TODO Auto-generated method stub
				Log.e(TAG,
						"rendererControlCallback:Subscribe events failed!The reason is:"
								+ createDefaultFailureMessage(responseStatus,
										exception));
				UplayerUnity.showToast(mContext,
						createDefaultFailureMessage(responseStatus, exception));
			}

			@SuppressWarnings("rawtypes")
			@Override
			protected void eventsMissed(GENASubscription subscription,
					int numberOfMissedEvents) {
				// TODO Auto-generated method stub
				Log.e(TAG,
						"rendererControlCallback:Missed events,the number is:"
								+ numberOfMissedEvents);
			}

			@SuppressWarnings("rawtypes")
			@Override
			protected void eventReceived(GENASubscription subscription) {
				// TODO Auto-generated method stub
				try {
					LastChange lastChange = new LastChange(
							new RenderingControlLastChangeParser(),
							subscription.getCurrentValues().get(LAST_CHANGE)
									.toString());
					if (lastChange != null) {

						Mute mute = lastChange.getEventedValue(DEFAULT_INSTANCE,
								RenderingControlVariable.Mute.class);
						Volume volume = lastChange.getEventedValue(
								DEFAULT_INSTANCE,
								RenderingControlVariable.Volume.class);
						if (mute != null)
							mMetadata.setMute(mute.getValue().getMute());

						if (volume != null)
							mMetadata.setVolume(volume.getValue().getVolume());
						EventBus.getDefault().post(new PlayerMessage(
								PlayerMessage.REFRESH_VOLUME, mMetadata));

					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@SuppressWarnings("rawtypes")
			@Override
			protected void established(GENASubscription subscription) {
				// TODO Auto-generated method stub
				Log.i(TAG, "rendererControlCallback:Established,the id is:"
						+ subscription.getSubscriptionId());
			}

			@SuppressWarnings("rawtypes")
			@Override
			protected void ended(GENASubscription subscription,
					CancelReason reason, UpnpResponse responseStatus) {
				// TODO Auto-generated method stub
				Log.e(TAG,
						"rendererControlCallback:Subscribe events is ended!");
			}
		};

		if (mAvTransportService != null)
			mAndroidUpnpService.getControlPoint()
					.execute(mAvSubscriptionCallback);
		if (mRendererControlService != null)
			mAndroidUpnpService.getControlPoint()
					.execute(mRenSubscriptionCallback);
	}
}

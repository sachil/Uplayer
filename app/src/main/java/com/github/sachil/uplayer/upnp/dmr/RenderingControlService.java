package com.github.sachil.uplayer.upnp.dmr;

import org.fourthline.cling.binding.annotations.UpnpAction;
import org.fourthline.cling.binding.annotations.UpnpInputArgument;
import org.fourthline.cling.binding.annotations.UpnpOutputArgument;
import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.renderingcontrol.AbstractAudioRenderingControl;
import org.fourthline.cling.support.renderingcontrol.RenderingControlException;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelMute;
import org.fourthline.cling.support.renderingcontrol.lastchange.ChannelVolume;
import org.fourthline.cling.support.renderingcontrol.lastchange.RenderingControlVariable;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

public class RenderingControlService extends AbstractAudioRenderingControl {

	private static final String LOG_TAG = RenderingControlService.class
			.getSimpleName();
	private AudioManager mAudioManager = null;

	public RenderingControlService(Context context, LastChange lastChange) {
		super(lastChange);
		mAudioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
	}

	@Override
	public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
		// TODO Auto-generated method stub
		return new UnsignedIntegerFourBytes[] { new UnsignedIntegerFourBytes(0) };
	}

	@Override
	protected Channel[] getCurrentChannels() {
		// TODO Auto-generated method stub
		return new Channel[] { Channel.Master };
	}

	@Override
	@UpnpAction(out = @UpnpOutputArgument(name = "CurrentMute", stateVariable = "Mute"))
	public boolean getMute(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0,
			@UpnpInputArgument(name = "Channel") String arg1)
			throws RenderingControlException {
		// TODO Auto-generated method stub
		return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0 ? false
				: true;
	}

	@Override
	@UpnpAction(out = @UpnpOutputArgument(name = "CurrentVolume", stateVariable = "Volume"))
	public UnsignedIntegerTwoBytes getVolume(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0,
			@UpnpInputArgument(name = "Channel") String arg1)
			throws RenderingControlException {
		// TODO Auto-generated method stub
		return new UnsignedIntegerTwoBytes(
				mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
	}

	@Override
	@UpnpAction
	public void setMute(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0,
			@UpnpInputArgument(name = "Channel") String arg1,
			@UpnpInputArgument(name = "DesiredMute", stateVariable = "Mute") boolean arg2)
			throws RenderingControlException {
		// TODO Auto-generated method stub
		Log.e(LOG_TAG, "Start to set mute");
		mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, arg2);
		getLastChange().setEventedValue(
				getDefaultInstanceID(),
				new RenderingControlVariable.Mute(new ChannelMute(
						Channel.Master, arg2)));
		getLastChange().fire(getPropertyChangeSupport());
	}

	@Override
	@UpnpAction
	public void setVolume(
			@UpnpInputArgument(name = "InstanceID") UnsignedIntegerFourBytes arg0,
			@UpnpInputArgument(name = "Channel") String arg1,
			@UpnpInputArgument(name = "DesiredVolume", stateVariable = "Volume") UnsignedIntegerTwoBytes arg2)
			throws RenderingControlException {
		// TODO Auto-generated method stub
		int volume = arg2.getValue().intValue();
		int maxVolume = mAudioManager
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		Log.e(LOG_TAG, "MaxVolume:" + maxVolume + "currentVolume" + volume);
		if (volume >= 0 && volume <= maxVolume) {
			mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume,
					AudioManager.FLAG_PLAY_SOUND | AudioManager.FLAG_SHOW_UI);
			if (volume > 0)
				mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
			boolean isMute = volume == 0 ? true : false;
			getLastChange().setEventedValue(
					getDefaultInstanceID(),
					new RenderingControlVariable.Volume(new ChannelVolume(
							Channel.Master, volume)),
					new RenderingControlVariable.Mute(new ChannelMute(
							Channel.Master, isMute)));
			getLastChange().fire(getPropertyChangeSupport());
		} else
			throw new RenderingControlException(ErrorCode.INVALID_ARGS,
					"The volume is invalid.");
	}
}

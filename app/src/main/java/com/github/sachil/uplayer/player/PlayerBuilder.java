package com.github.sachil.uplayer.player;

import com.github.sachil.uplayer.UplayerUnity.MEDIA_TYPE;
import com.github.sachil.uplayer.upnp.dmr.AvtransportService;

public class PlayerBuilder {

	private static AVPlayer mPlayer = null;

	public static AVPlayer build(AvtransportService service, MEDIA_TYPE type) {

		if (type == MEDIA_TYPE.AUDIO) {
			mPlayer = MusicService.getInstance().getPlayer();
			((MusicPlayer) mPlayer).bindStateChangeListener(service);
		} else if (type == MEDIA_TYPE.VIDEO)
			;
		else if (type == MEDIA_TYPE.IMAGE)
			;
		else
			;
		return mPlayer;
	}
}

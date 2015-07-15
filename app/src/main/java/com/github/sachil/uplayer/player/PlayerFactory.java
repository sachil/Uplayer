package com.github.sachil.uplayer.player;

import android.content.Context;

import com.github.sachil.uplayer.UplayerUnity.MEDIA_TYPE;
import com.github.sachil.uplayer.upnp.dmr.AvtransportService;

public class PlayerFactory {

	private static AbstractAVPlayer mPlayer = null;
	
	public static AbstractAVPlayer createPlayer(Context context,AvtransportService service,MEDIA_TYPE type){
		
		if(type == MEDIA_TYPE.AUDIO)
			mPlayer = new AudioPlayer(context,service);
		else if(type == MEDIA_TYPE.VIDEO)
			;
		else if(type == MEDIA_TYPE.IMAGE)
			;
		else
			;
		return mPlayer;
	}
	
	public static AbstractAVPlayer getPlayer(){
		return mPlayer;
	}
}

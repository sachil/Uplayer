package com.github.sachil.uplayer.player;

public abstract class AbstractAVPlayer {
	
	public abstract void setUri(String uri);
	
	public abstract void play();
	
	public abstract void pause();
	
	public abstract void stop();
	
	public abstract void close();
	
	public abstract boolean getMute();
	
	public abstract int getVolume();
	
	public abstract void setMute(boolean mute);
	
	public abstract void setVolume(int volume);
	
	public abstract void seek(int position);
	
	public abstract int getDuration();
	
	public abstract int getCurrentPosition();
	
	public abstract void next();
	
	public abstract void previous();
	
}

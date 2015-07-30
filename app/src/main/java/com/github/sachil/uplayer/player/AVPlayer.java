package com.github.sachil.uplayer.player;

public interface AVPlayer {

	public void setUri(String uri);

	public void play();

	public void pause();

	public void stop();

	public void close();

	public boolean getMute();

	public int getVolume();

	public void setMute(boolean mute);

	public void setVolume(int volume);

	public void seek(int position);

	public int getDuration();

	public int getCurrentPosition();

	public boolean isPlaying();

	public void next();

	public void previous();

}

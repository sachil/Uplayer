package com.github.sachil.uplayer.upnp.dmc.playlist;


import com.github.sachil.uplayer.upnp.dmc.XMLToMetadataParser;
import com.github.sachil.uplayer.upnp.dmc.XMLToMetadataParser.Metadata;

public class PlaylistItem {
	private boolean mIsPlaylist = false;
	private Object mObject = null;
	public PlaylistItem(String playlistName){
		mIsPlaylist = true;
		mObject = new Playlist(playlistName);
	}
	
	public PlaylistItem(String transportUrl,String transportUrlmetadata){
		mIsPlaylist = false;
		mObject = new Item(transportUrl, transportUrlmetadata);
	}
	
	public boolean isPlaylist(){
		
		return mIsPlaylist;
	}
	
	public Playlist getPlaylist(){
		if(mIsPlaylist)
			return (Playlist) mObject;
		else
			return null;
	}
	
	public Item getItem(){
		if(!mIsPlaylist)
			return (Item) mObject;
		else
			return null;
	}

	public class Playlist{
		private String mPlaylistName = null;
		public Playlist(String name){
			mPlaylistName = name;
		}
		
		public void setName(String name){
			mPlaylistName = name;
		}
		
		public String getName(){
			return mPlaylistName;
		}
	}
	
	public class Item{
		private String mTransportUrl = null;
		private String mTransportUrlMetadata = null;
		private Metadata mMetadata = null;
		public Item(String transportUrl,String transportUrlmetadata){
			mTransportUrl = transportUrl;
			mTransportUrlMetadata = transportUrlmetadata;
			mMetadata = new XMLToMetadataParser().parseXmlToMetadata(null,transportUrlmetadata);
		}
		
		public String getTransportUrl(){
			
			return mTransportUrl;
		}
		
		public String getTransportUrlMetadata(){
			
			return mTransportUrlMetadata;
		}
		
		public Metadata getItemMetadata(){
			
			return mMetadata;
		}
		
		
	}
	

}

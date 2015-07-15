package com.github.sachil.uplayer;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class UplayerUnity {
	
	public static final String APP_NAME = "Uplayer";
	public static final String VIDEO_PREFIX = "video";
	public static final String AUDIO_PREFIX = "audio";
	public static final String IMAGE_PREFIX = "image";
	
	public static final String PLAYLISTS_EDITED = "0x001";
	public static final String OPTIONS_ITEM_CLICKED = "0x002";
	
	public static final int PLAYLIST_MAX_LENGTH = 500;
	public enum MEDIA_TYPE{
		AUDIO,
		IMAGE,
		VIDEO
	}
	
	private static final String LOG_TAG = UplayerUnity.class.getSimpleName();

	private static InetAddress mInetAddress = null;
	private static String mMacAddress = null;
	private static WifiManager mManager = null;
	private static WifiManager.MulticastLock mLock = null;
	private static Context mContext = null;
	
	public static void setContext(Context context){
		mContext = context;
	}
	
	public  static void showToast(final Context context,final String message){
		((Activity)context).runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
			}
		});
	}

	public static InetAddress getInetAddress(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ethernetInfo = connectivityManager
				.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		if (ethernetInfo.isConnected()) {
			NetworkInterface networkInterface = null;
			try {
				mMacAddress = loadFileAsString("/sys/class/net/eth0/address")
						.toLowerCase().substring(0, 17);
				networkInterface = NetworkInterface.getByName("eth0");
				if (networkInterface != null) {
					for (InetAddress inetAddress : Collections
							.list(networkInterface.getInetAddresses())) {
						if (!inetAddress.isLoopbackAddress()
								&& InetAddressUtils.isIPv4Address(inetAddress
										.getHostAddress())) {
							mInetAddress = inetAddress;
						}
					}
				}
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				Log.e(LOG_TAG, "Get local ip from ethernet failed!" + e);
			}
		} else {
			mManager = (WifiManager) context
					.getSystemService(android.content.Context.WIFI_SERVICE);
			if (mManager != null && mManager.isWifiEnabled()) {
				if (mLock == null) {
					mLock = mManager.createMulticastLock(LOG_TAG);
					mLock.setReferenceCounted(true);
					mLock.acquire();
				}
				WifiInfo info = mManager.getConnectionInfo();
				if (info != null) {
					mMacAddress = info.getMacAddress().toLowerCase();
					String ip = intToIp(info.getIpAddress());
					if (ip.startsWith("0"))
						mInetAddress = null;
					else
						try {
							mInetAddress = InetAddress.getByName(ip);
						} catch (UnknownHostException e) {
							// TODO Auto-generated catch block
							Log.e(LOG_TAG, "Get local ip from wifi failed!" + e);
						}
				}
			}
		}
		return mInetAddress;
	}

	public static String getHardwareAddressInString() {
		if (mMacAddress != null) {
			StringBuilder stringBuilder = new StringBuilder();
			String address[] = null;
			if (mMacAddress.contains(":"))
				address = mMacAddress.split(":");
			else if (mMacAddress.contains(".")) {
				address = mMacAddress.split("\\.");
			}
			for (int i = 0; i < address.length; i++)
				stringBuilder.append(address[i]);
			return stringBuilder.toString();
		} else
			return null;
	}

	public static boolean isNetworkConneced(Context context) {
		boolean isConnected = false;
		ConnectivityManager networkManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = networkManager
				.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		if (info != null && info.isConnected())
			isConnected = true;
		else {
			info = networkManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (info != null && info.isConnected())
				isConnected = true;
		}

		return isConnected;
	}

	public static void releaseLock() {
		if (mLock != null)
			mLock.release();
		Log.i(LOG_TAG, "Muticast lock released!");
	}
	
	public static Resources getResources(){
		
		return mContext.getResources();
	}

	private static String intToIp(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
				+ ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
	}

	private static String loadFileAsString(String path) {
		StringBuffer fileData = new StringBuffer(1024);
		try {
			BufferedReader reader = new BufferedReader(new FileReader(path));
			char buf[] = new char[1024];
			int numRead = 0;
			while ((numRead = reader.read(buf)) != -1) {
				String readData = String.valueOf(buf, 0, numRead);
				fileData.append(readData);
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG,
					"Get mac address from ethernet failed,reason is can't find file!");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(LOG_TAG, "Get mac address from ethernet failed!");
			e.printStackTrace();
		}

		return fileData.toString();
	}
}
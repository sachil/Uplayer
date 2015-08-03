package com.github.sachil.uplayer;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collections;

import org.apache.http.conn.util.InetAddressUtils;
import org.fourthline.cling.android.NetworkUtils;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class Utils {

	private static final String LOG_TAG = Utils.class.getSimpleName();

	public static final String APP_NAME = "Uplayer";
	public static final String VIDEO_PREFIX = "video";
	public static final String AUDIO_PREFIX = "audio";
	public static final String IMAGE_PREFIX = "image";

	public static final String PLAYLISTS_EDITED = "0x001";
	public static final String OPTIONS_ITEM_CLICKED = "0x002";

	public static final int PLAYLIST_MAX_LENGTH = 500;

	public enum MEDIA_TYPE {
		AUDIO, IMAGE, VIDEO
	}

	public static InetAddress getInetAddress(Context context) {

		InetAddress address = null;
		NetworkInfo networkInfo = NetworkUtils.getConnectedNetworkInfo(context);

		try {
			if (NetworkUtils.isWifi(networkInfo)) {
				WifiManager manager = (WifiManager) context
						.getSystemService(Context.WIFI_SERVICE);
				WifiInfo wifiInfo = manager.getConnectionInfo();
				String ip = intToIp(wifiInfo.getIpAddress());
				if (!ip.startsWith("0"))
					address = InetAddress.getByName(ip);
			} else if (NetworkUtils.isEthernet(networkInfo)) {
				NetworkInterface networkInterface;
				networkInterface = NetworkInterface.getByName("eth0");
				if (networkInterface != null) {
					for (InetAddress inetAddress : Collections
							.list(networkInterface.getInetAddresses())) {
						if (!inetAddress.isLoopbackAddress() && InetAddressUtils
								.isIPv4Address(inetAddress.getHostAddress())) {
							address = inetAddress;
						}
					}
				}
			} else
				address = InetAddress.getByName("127.0.0.1");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (SocketException e) {
			e.printStackTrace();
		}
		return address;
	}

	private static String intToIp(int ip) {
		return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "."
				+ ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
	}
}
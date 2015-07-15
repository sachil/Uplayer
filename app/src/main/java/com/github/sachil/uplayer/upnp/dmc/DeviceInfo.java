package com.github.sachil.uplayer.upnp.dmc;

import java.util.ArrayList;

public class DeviceInfo {

	private String mManufacturer = null;
	private String mManufacturerUrl = null;
	private String mModelName = null;
	private String mModelDescp = null;
	private String mModelNum = null;
	private String mUDN = null;
	private ArrayList<String> mSuportAudios = null;
	private ArrayList<String> mSuportVideos = null;
	private ArrayList<String> mSuportImages = null;

	public void setManufacturer(String manufacturer) {
		mManufacturer = manufacturer;
	}

	public void setManufacturerUrl(String url) {
		mManufacturerUrl = url;
	}

	public void setModelName(String name) {
		mModelName = name;
	}

	public void setModelDescription(String description) {
		mModelDescp = description;
	}

	public void setModelNum(String num) {
		mModelNum = num;
	}

	public void setUDN(String UDN) {
		mUDN = UDN;
	}

	public String getManufacturer() {
		return mManufacturer;
	}

	public String getManufacturerUrl() {
		return mManufacturerUrl;
	}

	public String getModelName() {
		return mModelName;
	}

	public String getModelDescription() {
		return mModelDescp;
	}

	public String getModelNum() {
		return mModelNum;
	}

	public String getUDN() {
		return mUDN;
	}

	public void setSuportAudios(ArrayList<String> audios) {
		mSuportAudios = audios;
	}

	public void setSuportVideos(ArrayList<String> videos) {
		mSuportVideos = videos;
	}

	public void setSuportImages(ArrayList<String> images) {
		mSuportImages = images;
	}

	public ArrayList<String> getSuportAudios() {
		return mSuportAudios;
	}

	public ArrayList<String> getSuportVideos() {
		return mSuportVideos;
	}

	public ArrayList<String> getSuportImages() {
		return mSuportImages;
	}

}

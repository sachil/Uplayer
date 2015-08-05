package com.github.sachil.uplayer.ui.message;

public class ErrorMessage {

	public static final int CONTROL_ERROR = -1;

	private int mId = 0;
	private String mMessage = null;

	public ErrorMessage(int id, String msg) {
		mId = id;
		mMessage = msg;
	}

	public int getId() {
		return mId;
	}

	public String getMessage() {
		return mMessage;
	}
}

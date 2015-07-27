package com.github.sachil.uplayer.ui.message;

public class ActionMessage {

	private int mViewId = -1;
	private int mAction = 0;
	private Object mExtra = null;

	public ActionMessage(int viewId, int action, Object extra) {
		mViewId = viewId;
		mAction = action;
		mExtra = extra;
	}

	public int getViewId() {

		return mViewId;
	}

	public int getAction() {

		return mAction;
	}

	public Object getExtra() {

		return mExtra;
	}

}

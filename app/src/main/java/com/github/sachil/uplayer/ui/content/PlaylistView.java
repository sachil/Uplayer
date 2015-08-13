package com.github.sachil.uplayer.ui.content;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.ui.message.ActionMessage;

import de.greenrobot.event.EventBus;

public class PlaylistView extends PopupWindow implements View.OnClickListener {

	private static final String TAG = PlaylistView.class.getSimpleName();
	private Context mContext = null;
	private RecyclerView mRecyclerView = null;

	public PlaylistView(Context context) {
		mContext = context;
		createView();
	}

	public void setAdapter(RecyclerView.Adapter adapter) {
		mRecyclerView.setAdapter(adapter);
	}

	public void show(View parent) {
		if (this.isShowing())
			this.dismiss();
		else {
			setAlpha(0.4f);
			this.showAsDropDown(parent, 0, -parent.getHeight());
		}
	}

	@Override
	public void onClick(View view) {
		EventBus.getDefault().post(new ActionMessage(view.getId(), 0, null));
	}

	private void createView() {
		View contentView = LayoutInflater.from(mContext)
				.inflate(R.layout.playlist, null);
		mRecyclerView = (RecyclerView) contentView
				.findViewById(R.id.playlist_recycler_view);
		mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
		mRecyclerView.addItemDecoration(new DividerListItemDecoration(mContext,
				DividerListItemDecoration.VERTICAL_LIST));

		contentView.findViewById(R.id.playlist_model).setOnClickListener(this);
		contentView.findViewById(R.id.playlist_clear).setOnClickListener(this);
		this.setContentView(contentView);
		WindowManager manager = ((Activity) mContext).getWindowManager();
		DisplayMetrics dm = new DisplayMetrics();
		manager.getDefaultDisplay().getMetrics(dm);
		this.setWidth(dm.widthPixels);
		this.setHeight(dm.heightPixels / 2);
		this.setFocusable(true);
		this.setTouchable(true);
		this.setAnimationStyle(R.style.PopAnimation);
		this.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				setAlpha(1.0f);
			}
		});
		this.update();
		this.setBackgroundDrawable(new ColorDrawable(
				mContext.getResources().getColor(R.color.white)));
	}

	private void setAlpha(float alpha) {
		WindowManager.LayoutParams layoutParams = ((Activity) mContext)
				.getWindow().getAttributes();
		layoutParams.alpha = alpha;
		((Activity) mContext).getWindow().setAttributes(layoutParams);
	}

}

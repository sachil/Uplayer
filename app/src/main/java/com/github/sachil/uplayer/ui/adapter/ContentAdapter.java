package com.github.sachil.uplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.Res;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.MusicTrack;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.ui.message.ActionMessage;
import com.github.sachil.uplayer.upnp.dmc.ContentItem;

import de.greenrobot.event.EventBus;

public class ContentAdapter
		extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final String TAG = ContentAdapter.class.getSimpleName();

	private List<ContentItem> mContents = null;

	public ContentAdapter() {
		mContents = new ArrayList<>();
	}

	public void refresh(List contents) {
		mContents = contents;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return mContents.size();
	}

	@Override
	public void onBindViewHolder(final RecyclerView.ViewHolder holder,
			final int position) {

		ContentItem contentItem = mContents.get(position);

		if (contentItem.isContainer()) {
			ContainerHolder viewHolder = (ContainerHolder) holder;
			Container container = contentItem.getContainer();
			viewHolder.mTitle.setText(container.getTitle());
			viewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					EventBus.getDefault().post(new ActionMessage(R.id.item_row,
							0, mContents.get(position)));
				}
			});

		} else {
			Item item = contentItem.getItem();
			if (item instanceof MusicTrack) {
				ItemHolder viewHolder = (ItemHolder) holder;
				MusicTrack track = (MusicTrack) item;
				viewHolder.mTitle.setText(track.getTitle());

				if (track.getFirstPropertyValue(
						DIDLObject.Property.UPNP.ALBUM_ART_URI.class) != null) {
					String albumUri = track
							.getFirstPropertyValue(
									DIDLObject.Property.UPNP.ALBUM_ART_URI.class)
							.toString();
					viewHolder.mItemImage.setImageURI(Uri.parse(albumUri));
				}

				viewHolder.mArtist.setText(track.getArtists()[0].getName());
				for (Res res : track.getResources()) {
					String duration = res.getDuration();
					if (duration != null && duration.contains(":")) {
						if (duration.contains("."))
							duration = duration.substring(0,
									duration.lastIndexOf("."));
						viewHolder.mDuration.setText(duration);
					}
				}

				viewHolder.mItemView
						.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View view) {
								EventBus.getDefault()
										.post(new ActionMessage(R.id.item_row,
												0, mContents.get(position)));
							}
						});
			}
		}
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		View contentView;
		if (viewType == 0) {
			contentView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.container_row, parent, false);
			ContainerHolder holder = new ContainerHolder(contentView);
			return holder;
		} else {
			contentView = LayoutInflater.from(parent.getContext())
					.inflate(R.layout.item_row, parent, false);
			ItemHolder holder = new ItemHolder(contentView);
			return holder;
		}
	}

	@Override
	public int getItemViewType(int position) {

		if (mContents.get(position).isContainer())
			return 0;
		else
			return 1;
	}

	public static class ContainerHolder extends RecyclerView.ViewHolder {

		private View mItemView = null;
		private TextView mTitle = null;

		public ContainerHolder(View view) {
			super(view);
			mItemView = view.findViewById(R.id.item_row);
			mTitle = (TextView) view.findViewById(R.id.container_title);
		}
	}

	public static class ItemHolder extends RecyclerView.ViewHolder {

		private View mItemView = null;
		private SimpleDraweeView mItemImage = null;
		private TextView mTitle = null;
		private TextView mArtist = null;
		private TextView mDuration = null;

		public ItemHolder(View view) {
			super(view);
			mItemView = view.findViewById(R.id.item_row);
			mItemImage = (SimpleDraweeView) view.findViewById(R.id.item_image);
			mTitle = (TextView) view.findViewById(R.id.item_title);
			mArtist = (TextView) view.findViewById(R.id.item_artist);
			mDuration = (TextView) view.findViewById(R.id.item_duration);
		}
	}

}

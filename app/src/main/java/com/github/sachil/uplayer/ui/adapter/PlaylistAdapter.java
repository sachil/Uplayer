package com.github.sachil.uplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.MusicTrack;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.ui.message.ActionMessage;

import de.greenrobot.event.EventBus;

public class PlaylistAdapter
		extends RecyclerView.Adapter<PlaylistAdapter.ViewHolder> {

	private static final String TAG = PlaylistAdapter.class.getSimpleName();
	private Context mContext = null;
	private List<Item> mListItems = null;

	public PlaylistAdapter(Context context) {
		mContext = context;
		mListItems = new ArrayList<>();
	}

	public void refresh(List<Item> items) {
		mListItems = items;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return mListItems.size();
	}

	@Override
	public PlaylistAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {

		View contentView = LayoutInflater.from(mContext)
				.inflate(R.layout.item_playlist, parent, false);
		return new ViewHolder(contentView);
	}

	@Override
	public void onBindViewHolder(PlaylistAdapter.ViewHolder holder,
			final int position) {

		Item item = mListItems.get(position);
		holder.mTitle.setText(item.getTitle());
		holder.mArtist.setText(item.getCreator());
		if (item instanceof MusicTrack) {
			MusicTrack musicTrack = (MusicTrack) item;
			if (musicTrack.getFirstPropertyValue(
					DIDLObject.Property.UPNP.ALBUM_ART_URI.class) != null) {
				String albumUri = musicTrack
						.getFirstPropertyValue(
								DIDLObject.Property.UPNP.ALBUM_ART_URI.class)
						.toString();
				holder.mItemImage.setImageURI(Uri.parse(albumUri));
			}
		}

		View.OnClickListener listener = new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				switch (view.getId()) {
				case R.id.playlist_delete:
					EventBus.getDefault().post(new ActionMessage(
							R.id.playlist_delete, 0, mListItems.get(position)));
					break;
				case R.id.playlist_row:
					EventBus.getDefault().post(new ActionMessage(
							R.id.playlist_row, 0, mListItems.get(position)));
					break;
				}
			}
		};
		holder.mDeleteMenu.setOnClickListener(listener);
		holder.mItemView.setOnClickListener(listener);
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {

		private View mItemView = null;
		private SimpleDraweeView mItemImage = null;
		private TextView mTitle = null;
		private TextView mArtist = null;
		private ImageView mDeleteMenu = null;

		public ViewHolder(View view) {
			super(view);
			mItemView = view.findViewById(R.id.playlist_row);
			mItemImage = (SimpleDraweeView) view
					.findViewById(R.id.playlist_image);
			mTitle = (TextView) view.findViewById(R.id.playlist_title);
			mArtist = (TextView) view.findViewById(R.id.playlist_artist);
			mDeleteMenu = (ImageView) view.findViewById(R.id.playlist_delete);
		}
	}

}

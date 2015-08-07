package com.github.sachil.uplayer.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import net.steamcrafted.materialiconlib.MaterialIconView;

import org.fourthline.cling.support.model.DIDLObject;
import org.fourthline.cling.support.model.container.Container;
import org.fourthline.cling.support.model.item.Item;
import org.fourthline.cling.support.model.item.MusicTrack;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.ui.ContentManager.LAYOUT_TYPE;
import com.github.sachil.uplayer.ui.message.ActionMessage;
import com.github.sachil.uplayer.upnp.dmc.ContentItem;

import de.greenrobot.event.EventBus;

public class ContentAdapter
		extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final String TAG = ContentAdapter.class.getSimpleName();
	private static final Handler UI_THREAD = new Handler(
			Looper.getMainLooper());
	private Context mContext = null;
	private List<ContentItem> mContents = null;
	private LAYOUT_TYPE mLayoutType = LAYOUT_TYPE.LIST;

	public ContentAdapter(Context context) {
		mContext = context;
		mContents = new ArrayList<>();
	}

	public void refresh(List contents) {
		mContents = contents;
		notifyDataSetChanged();
	}

	public void changeLayoutType(LAYOUT_TYPE type) {
		mLayoutType = type;
		notifyDataSetChanged();
	}

	@Override
	public int getItemCount() {
		return mContents.size();
	}

	@Override
	public int getItemViewType(int position) {
		int viewType = 0;
		if (mLayoutType == LAYOUT_TYPE.LIST) {
			if (mContents.get(position).isContainer())
				viewType = 0;
			else
				viewType = 1;
		} else if (mLayoutType == LAYOUT_TYPE.GRID) {
			if (mContents.get(position).isContainer())
				viewType = 2;
			else
				viewType = 3;
		}
		return viewType;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
			int viewType) {
		ViewHolder viewHolder = null;
		switch (viewType) {
		case 0:
			viewHolder = new ContainerHolder(LayoutInflater.from(mContext)
					.inflate(R.layout.container_line, parent, false));
			break;
		case 1:
			viewHolder = new ItemHolder(LayoutInflater.from(mContext)
					.inflate(R.layout.item_line, parent, false));
			break;
		case 2:
			viewHolder = new ContainerHolder(LayoutInflater.from(mContext)
					.inflate(R.layout.container_grid, parent, false));
			break;
		case 3:
			viewHolder = new ItemHolder(LayoutInflater.from(mContext)
					.inflate(R.layout.item_grid, parent, false));
			break;
		}
		return viewHolder;
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
				final ItemHolder viewHolder = (ItemHolder) holder;
				MusicTrack track = (MusicTrack) item;
				viewHolder.mTitle.setText(track.getTitle());

				if (track.getFirstPropertyValue(
						DIDLObject.Property.UPNP.ALBUM_ART_URI.class) != null) {
					String albumUri = track
							.getFirstPropertyValue(
									DIDLObject.Property.UPNP.ALBUM_ART_URI.class)
							.toString();
					if (mLayoutType == LAYOUT_TYPE.GRID)
						getAlbumArt(albumUri, viewHolder);
					else if (mLayoutType == LAYOUT_TYPE.LIST)
						viewHolder.mItemImage.setImageURI(Uri.parse(albumUri));
				}

				viewHolder.mArtist.setText(track.getArtists()[0].getName());
				View.OnClickListener listener = new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						switch (view.getId()) {
						case R.id.item_menu:
							showPopupMenu(view, null);
							break;
						case R.id.item_row:
							EventBus.getDefault().post(new ActionMessage(
									R.id.item_row, 0, mContents.get(position)));
							break;
						}
					}
				};
				viewHolder.mMenu.setOnClickListener(listener);
				viewHolder.mItemView.setOnClickListener(listener);
			}
		}
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
		private View mBasicView = null;
		private SimpleDraweeView mItemImage = null;
		private TextView mTitle = null;
		private TextView mArtist = null;
		private MaterialIconView mMenu = null;

		public ItemHolder(View view) {
			super(view);
			mItemView = view.findViewById(R.id.item_row);
			mItemImage = (SimpleDraweeView) view.findViewById(R.id.item_image);
			mTitle = (TextView) view.findViewById(R.id.item_title);
			mArtist = (TextView) view.findViewById(R.id.item_artist);
			mMenu = (MaterialIconView) view.findViewById(R.id.item_menu);
			mBasicView = view.findViewById(R.id.item_basic);
		}
	}

	private class AlbumArt {
		private int mColor = 0;
		private Bitmap mBitmap = null;

		public AlbumArt(int color, Bitmap bitmap) {
			mColor = color;
			mBitmap = bitmap;
		}
	}

	private void getAlbumArt(String uri, final ItemHolder viewHolder) {
		ImageRequest imageRequest = ImageRequestBuilder
				.newBuilderWithSource(Uri.parse(uri))
				.setProgressiveRenderingEnabled(true).build();
		ImagePipeline imagePipeline = Fresco.getImagePipeline();
		DataSource<CloseableReference<CloseableImage>> dataSource = imagePipeline
				.fetchDecodedImage(imageRequest, mContext);
		dataSource.subscribe(new BaseBitmapDataSubscriber() {
			@Override
			protected void onNewResultImpl(Bitmap bitmap) {
				int i = 0;
				int backgroundColor;
				int defalutColor = mContext.getResources()
						.getColor(R.color.half_transparent);
				do {
					Palette palette = Palette.from(bitmap).generate();
					backgroundColor = palette.getDarkMutedColor(defalutColor);
					i++;
				} while (i < 3 && backgroundColor == defalutColor);

				final AlbumArt albumArt = new AlbumArt(backgroundColor, bitmap);

				UI_THREAD.post(new Runnable() {
					@Override
					public void run() {
						viewHolder.mBasicView
								.setBackgroundColor(albumArt.mColor);
						viewHolder.mItemImage.setImageBitmap(albumArt.mBitmap);
					}
				});
			}

			@Override
			protected void onFailureImpl(
					DataSource<CloseableReference<CloseableImage>> dataSource) {
			}
		}, CallerThreadExecutor.getInstance());

	}

	private void showPopupMenu(View view, Item item) {

		PopupMenu popupMenu = new PopupMenu(mContext, view);
		popupMenu.inflate(R.menu.menu_item);
		popupMenu.setOnMenuItemClickListener(
				new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem menuItem) {

						switch (menuItem.getItemId()) {
						case R.id.menu_play:

							break;
						case R.id.menu_add_playlist:

							break;
						}
						return true;
					}
				});
		popupMenu.show();

	}

}

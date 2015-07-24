package com.github.sachil.uplayer.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.sachil.uplayer.R;

public class ContentAdapter
		extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	private static final String TAG = ContentAdapter.class.getSimpleName();

	@Override
	public int getItemCount() {
		return 100;
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});
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
		return 0;
	}

	public static class ContainerHolder extends RecyclerView.ViewHolder {

		private TextView mTitle = null;

		public ContainerHolder(View view) {
			super(view);
			mTitle = (TextView) view.findViewById(R.id.container_title);
		}
	}

	public static class ItemHolder extends RecyclerView.ViewHolder {

		public ItemHolder(View view) {
			super(view);
		}
	}

}

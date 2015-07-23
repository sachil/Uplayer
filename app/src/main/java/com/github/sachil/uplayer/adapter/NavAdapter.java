package com.github.sachil.uplayer.adapter;

import java.util.ArrayList;
import java.util.List;

import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.RemoteDevice;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.github.sachil.uplayer.R;
import com.github.sachil.uplayer.upnp.dmc.ContentItem;

/**
 * Created by 20001962 on 2015/7/17.
 */
public class NavAdapter extends BaseExpandableListAdapter {

	private static final String TAG = NavAdapter.class.getSimpleName();
	private Context mContext = null;
	private List<Object> mGroups = null;
	private List<List<Object>> mChilds = null;

	public NavAdapter(Context context) {

		mContext = context;
		mGroups = new ArrayList<>(1);
		mChilds = new ArrayList<>();
	}

	public void refresh(Object group,List child) {

		if(group != null){
			mGroups.clear();
			mGroups.add(group);
		}
		if(child != null){
			mChilds.clear();
			for (int i = 0; i < mGroups.size(); i++)
				mChilds.add(child);
		}

		notifyDataSetChanged();
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return mChilds.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return mChilds.get(groupPosition).size();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {

		ChildHolder viewHolder;
		Device device = null;
		ContentItem content = null;

		if (mChilds.get(groupPosition).get(childPosition) instanceof Device)
			device = (Device) mChilds.get(groupPosition).get(childPosition);
		else if (mChilds.get(groupPosition)
				.get(childPosition) instanceof ContentItem)
			content = (ContentItem) mChilds.get(groupPosition)
					.get(childPosition);

		if (convertView == null) {
			viewHolder = new ChildHolder();
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.nav_child, null);
			viewHolder.mChildImage = (SimpleDraweeView) convertView
					.findViewById(R.id.nav_child_image);
			viewHolder.mChildTitle = (TextView) convertView
					.findViewById(R.id.nav_child_title);
			convertView.setTag(viewHolder);
		} else
			viewHolder = (ChildHolder) convertView.getTag();

		if (device != null) {
			if (device instanceof RemoteDevice && device.hasIcons()) {
				String iconUri = ((RemoteDevice) device)
						.normalizeURI(device.getIcons()[0].getUri()).toString();
				viewHolder.mChildImage.setImageURI(Uri.parse(iconUri));
			} else
				viewHolder.mChildImage.setImageResource(R.drawable.ic_launcher);

			viewHolder.mChildTitle
					.setText(device.getDetails().getFriendlyName());
		} else {

			if (content.isContainer()) {
				viewHolder.mChildTitle
						.setText(content.getContainer().getTitle());
			}
		}

		return convertView;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mGroups.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mGroups.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		GroupHolder viewHolder;
		Device device = null;
		ContentItem content = null;

		if (mGroups.get(groupPosition) instanceof Device)
			device = (Device) mGroups.get(groupPosition);
		else
			content = (ContentItem) mGroups.get(groupPosition);

		if (convertView == null) {
			viewHolder = new GroupHolder();
			convertView = LayoutInflater.from(mContext)
					.inflate(R.layout.nav_group, null);
			viewHolder.mGroupTitle = (TextView) convertView
					.findViewById(R.id.nav_group_title);
			viewHolder.mGroupImage = (SimpleDraweeView) convertView
					.findViewById(R.id.nav_group_image);
			viewHolder.mGroupArrow = (ImageView) convertView
					.findViewById(R.id.nav_group_arrow);
			convertView.setTag(viewHolder);

		} else
			viewHolder = (GroupHolder) convertView.getTag();

		if (device != null) {
			if (device instanceof RemoteDevice && device.hasIcons()) {
				String iconUri = ((RemoteDevice) device)
						.normalizeURI(device.getIcons()[0].getUri()).toString();
				viewHolder.mGroupImage.setImageURI(Uri.parse(iconUri));
			} else
				viewHolder.mGroupImage.setImageResource(R.drawable.ic_launcher);
			viewHolder.mGroupTitle
					.setText(device.getDetails().getFriendlyName());
		} else {

			if (content.isContainer())
				viewHolder.mGroupTitle
						.setText(content.getContainer().getTitle());
		}

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	private static class GroupHolder {

		private SimpleDraweeView mGroupImage = null;
		private TextView mGroupTitle = null;
		private ImageView mGroupArrow = null;

	}

	private static class ChildHolder {

		private SimpleDraweeView mChildImage = null;
		private TextView mChildTitle = null;

	}

}

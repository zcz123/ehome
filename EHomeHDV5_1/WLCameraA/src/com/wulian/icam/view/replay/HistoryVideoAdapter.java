/**
 * Project Name:  iCam
 * File Name:     HistoryVideoAdapter.java
 * Package Name:  com.wulian.icam.adpter
 * @Date:         2015年5月27日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.replay;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.model.MediaItem;

/**
 * @ClassName: HistoryVideoAdapter
 * @Function: 历史回看适配器
 * @Date: 2015年5月27日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class HistoryVideoAdapter extends BaseAdapter {
	private Context context;
	private List<MediaItem> videoList;
	private MediaItem mediaItem;

	public HistoryVideoAdapter(Context context, List<MediaItem> videoList) {
		this.context = context;
		this.videoList = videoList;
	}

	@Override
	public int getCount() {
		return videoList.size();
	}

	@Override
	public Object getItem(int position) {
		return videoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (null == convertView) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.adapter_history_video, parent, false);
			holder = new ViewHolder();
			holder.rl_title_date = (RelativeLayout) convertView
					.findViewById(R.id.ll_title_date);
			holder.ll_media_item = (LinearLayout) convertView
					.findViewById(R.id.ll_media_item);
			holder.tv_title_date = (TextView) convertView
					.findViewById(R.id.tv_title_date);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_title);
			holder.iv_media_type = (ImageView) convertView
					.findViewById(R.id.iv_meida_type);
			holder.iv_preview = (ImageView) convertView
					.findViewById(R.id.iv_preview);
			holder.btn_play = (Button) convertView.findViewById(R.id.btn_play);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		mediaItem = videoList.get(position);
		if (mediaItem.getMeidaType().equals(MediaItem.TYPE_HEAD)) {
			holder.ll_media_item.setVisibility(View.GONE);
			holder.rl_title_date.setVisibility(View.VISIBLE);
		} else {
			holder.ll_media_item.setVisibility(View.VISIBLE);
			holder.rl_title_date.setVisibility(View.GONE);
		}
		return convertView;
	}

	private static class ViewHolder {
		public LinearLayout  ll_media_item;
		public RelativeLayout rl_title_date;
		public TextView tv_title_date, tv_time, tv_title;
		public ImageView iv_media_type, iv_preview;
		public Button btn_play;
	}
}

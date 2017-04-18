/**
 * Project Name:  iCam
 * File Name:     AlarmMessageAdapter.java
 * Package Name:  com.wulian.icam.adpter
 * @Date:         2014年12月4日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.info;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.model.FeedbackInfo;

/**
 * @ClassName: FeedbackInfosAdapter
 * @Function: 用户反馈列表适配器
 * @date: 2015年7月10日
 * @author: yuanjs
 * @email: yuanjsh@wuliangroup.cn
 */
public class FeedbackInfosAdapter extends BaseAdapter {
	private List<FeedbackInfo> data;
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private String timeTemp;
	private Bitmap avatar;

	public FeedbackInfosAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		data = new ArrayList<FeedbackInfo>();
		avatar = ICamGlobal.getInstance().getUserinfo().getAvatar(context);
	}

	public void refresh(List<FeedbackInfo> data) {
		this.data.clear();
		if (data != null && data.size() > 0) {
			this.data.addAll(data);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public FeedbackInfo getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_user_feedback,
					null);
			viewHolder.tv_feedback_time = (TextView) convertView
					.findViewById(R.id.tv_feedback_time);
			viewHolder.ll_user_feedback = (LinearLayout) convertView
					.findViewById(R.id.ll_user_feedback);
			viewHolder.ll_server_feedback = (LinearLayout) convertView
					.findViewById(R.id.ll_server_feedback);
			viewHolder.iv_user_head = (ImageView) convertView
					.findViewById(R.id.iv_user_head);
			viewHolder.iv_server_head = (ImageView) convertView
					.findViewById(R.id.iv_server_head);
			viewHolder.tv_user_feedback = (TextView) convertView
					.findViewById(R.id.tv_user_feedback);
			viewHolder.tv_server_feedback = (TextView) convertView
					.findViewById(R.id.tv_server_feedback);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		FeedbackInfo item = getItem(position);
		Date mDate = new Date(item.getCreadet() * 1000);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.getDefault());
		String strTime = df.format(mDate);
		String showTime = strTime;
		// 同一天只显示时分
		// timeTemp = strTime;
		// if (showTime.contains(timeTemp.substring(0, 10)) && position != 0) {
		// showTime = strTime.substring(11, strTime.length());
		// } else {
		// showTime = strTime;
		// timeTemp = strTime;
		// }
		viewHolder.tv_feedback_time.setText(showTime);
		if (item.getType() == 0) {// 用户端
			viewHolder.ll_user_feedback.setVisibility(View.VISIBLE);
			viewHolder.ll_server_feedback.setVisibility(View.GONE);
			viewHolder.tv_user_feedback.setText(item.getFeedback());
			if (avatar != null) {
				viewHolder.iv_user_head.setImageBitmap(avatar);
			}
		} else if (item.getType() == 1) {// 服务器端
			viewHolder.ll_user_feedback.setVisibility(View.GONE);
			viewHolder.ll_server_feedback.setVisibility(View.VISIBLE);
			viewHolder.tv_server_feedback.setText(item.getFeedback());
		} else {// 错误

		}
		return convertView;
	}

	private class ViewHolder {
		LinearLayout ll_user_feedback, ll_server_feedback;
		TextView tv_user_feedback, tv_server_feedback;
		TextView tv_feedback_time;
		ImageView iv_user_head, iv_server_head;
	}
}

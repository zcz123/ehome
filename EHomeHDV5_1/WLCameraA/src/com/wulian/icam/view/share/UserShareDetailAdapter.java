/**
 * Project Name:  iCam
 * File Name:     AlarmMessageAdapter.java
 * Package Name:  com.wulian.icam.adpter
 * @Date:         2014年12月4日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.share;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.model.AlarmMessage;
import com.wulian.icam.model.FeedbackInfo;
import com.wulian.icam.model.OauthUserDetail;

/**
 * @ClassName: UserShareDetailAdapter
 * @Function: 用户分享列表适配器
 * @date: 2015年7月10日
 * @author: yuanjs
 * @email: yuanjsh@wuliangroup.cn
 */
public class UserShareDetailAdapter extends BaseAdapter {
	private List<OauthUserDetail> data;
	private Context mContext;
	private LayoutInflater mLayoutInflater;
	private DeleteOauthCallBack deleteOauthCallBack;

	public UserShareDetailAdapter(Context context) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		data = new ArrayList<OauthUserDetail>();
	}

	public void refresh(List<OauthUserDetail> data) {
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
	public OauthUserDetail getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_already_oauths,
					null);
			viewHolder.iv_user_head = (ImageView) convertView
					.findViewById(R.id.iv_user_head);
			viewHolder.tv_user_account = (TextView) convertView
					.findViewById(R.id.tv_user_account);
			viewHolder.tv_time_last = (TextView) convertView
					.findViewById(R.id.tv_time_last);
			viewHolder.tv_times_open_device = (TextView) convertView
					.findViewById(R.id.tv_times_open_device);
			viewHolder.btn_delete_oauth = (Button) convertView
					.findViewById(R.id.btn_delete_oauth);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		OauthUserDetail item = getItem(position);
		if (item.getLasttime() != 0) {
			Date mDate = new Date(item.getLasttime() * 1000);
			DateFormat df = new SimpleDateFormat("MM-dd HH:mm:ss",
					Locale.getDefault());
			String lastTime = df.format(mDate);
			viewHolder.tv_time_last.setText(lastTime);
		} else {
			viewHolder.tv_time_last.setText(mContext.getResources()
					.getString(R.string.share_times_zero));
		}
		String account = "";
		if (!TextUtils.isEmpty(item.getUsername())) {
			account = item.getUsername();
		} else if (!TextUtils.isEmpty(item.getPhone())) {
			account = item.getPhone();
		} else if (!TextUtils.isEmpty(item.getEmail())) {
			account = item.getEmail();
		}
		viewHolder.tv_user_account.setText(account);
		if (item.getCount() != 0) {
			viewHolder.tv_times_open_device.setText(Html.fromHtml(String
					.format(mContext.getString(R.string.share_times_of_open),
							item.getCount())));
		} else {
			viewHolder.tv_times_open_device.setText(mContext.getResources()
					.getString(R.string.share_times_zero));
		}

		viewHolder.btn_delete_oauth
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						deleteOauthCallBack.deleteOauth(position);
					}
				});
		return convertView;
	}

	private class ViewHolder {
		TextView tv_user_account, tv_time_last, tv_times_open_device;
		ImageView iv_user_head;
		Button btn_delete_oauth;
	}

	public interface DeleteOauthCallBack {
		void deleteOauth(int position);
	}

	public void setDeleteOauthCallBack(DeleteOauthCallBack deleteOauthCallBack) {
		if (deleteOauthCallBack != null) {
			this.deleteOauthCallBack = deleteOauthCallBack;
		}
	}
}

package com.wulian.iot.view.adapter;

import java.util.List;

import com.wulian.icam.R;
import com.wulian.iot.Config;
import com.wulian.iot.bean.GalleryAlarmInfo;
import com.wulian.iot.view.device.play.PlayEagleVideoAvtivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class AlarmListViewAdapter extends SimpleAdapter<GalleryAlarmInfo>{

	public AlarmListViewAdapter(Context context,List<GalleryAlarmInfo> list){
		super(context, list);
	}
	@Override
	public View view(int position, View convertView, ViewGroup parent) {
		GalleryAlarmInfo obj=eList.get(position);
		Log.e("AlarmListViewAdapter", obj.getTitle());
		Log.e("AlarmListViewAdapter", "yuar"+obj.getYear());
		Log.e("AlarmListViewAdapter", "asTime"+obj.getTimeAck());
		ViewHolder mViewHolder=null;
		if (convertView==null) {
			mViewHolder=new ViewHolder();
			convertView = this.layoutInflater.inflate(R.layout.alarm_list_view_item, null);
			mViewHolder.mTime=(TextView) convertView.findViewById(R.id.tv_alarm_time);
			convertView.setTag(mViewHolder);
		}else {
			mViewHolder = (ViewHolder)convertView.getTag();
		}
		mViewHolder.mTime.setText(obj.getTitle());
		return convertView;
	}

	private final class ViewHolder{
		 private TextView mTime;
	}
}

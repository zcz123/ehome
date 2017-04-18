package com.wulian.icam.view.device.setting;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.model.WiFiScanResult;

public class WifiAdapter extends BaseAdapter {
	/** Anything worse than or equal to this will show 0 bars. */
	private static final int MIN_RSSI = -100;

	/** Anything better than or equal to this will show the max bars. */
	private static final int MAX_RSSI = -55;

	private Context mContext;
	private List<WiFiScanResult> mList;

	public WifiAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.mContext = context;
		this.mList = new ArrayList<WiFiScanResult>();
	}

	public void refreshList(List<WiFiScanResult> list) {
		this.mList.clear();
		if (list != null) {
			this.mList.addAll(list);
		}
		this.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.mList.size();
	}

	@Override
	public WiFiScanResult getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Holder mHolder;
		WiFiScanResult item = getItem(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_wifi_layout, null);
			mHolder = new Holder();
			mHolder.ssid_tv = (TextView) convertView
					.findViewById(R.id.wifi_ssid_tv);
			mHolder.wifi_signal_status_iv = (ImageView) convertView
					.findViewById(R.id.wifi_signal_status_iv);
			mHolder.wifi_selected_iv = (ImageView) convertView
					.findViewById(R.id.wifi_selected_iv);
			convertView.setTag(mHolder);
		} else {
			mHolder = (Holder) convertView.getTag();
		}
		mHolder.ssid_tv.setText(item.getSsid());
		int level = item.getSignalLevel();
		switch (level) {
		case 0:
			mHolder.wifi_signal_status_iv
					.setImageResource(item.getSecurity() != 0 ? R.drawable.ic_wifi_lock_signal_1
							: R.drawable.ic_wifi_signal_1);
			break;
		case 1:
			mHolder.wifi_signal_status_iv
					.setImageResource(item.getSecurity() != 0 ? R.drawable.ic_wifi_lock_signal_2
							: R.drawable.ic_wifi_signal_2);
			break;
		case 2:
			mHolder.wifi_signal_status_iv
					.setImageResource(item.getSecurity() != 0 ? R.drawable.ic_wifi_lock_signal_3
							: R.drawable.ic_wifi_signal_3);
			break;
		case 3:
			mHolder.wifi_signal_status_iv
					.setImageResource(item.getSecurity() != 0 ? R.drawable.ic_wifi_lock_signal_4
							: R.drawable.ic_wifi_signal_4);
			break;
		default:
			break;
		}
		if (position == 0&&!item.getMac_address().equals("")) {
			mHolder.wifi_selected_iv.setVisibility(View.VISIBLE);
		} else {
			mHolder.wifi_selected_iv.setVisibility(View.GONE);
		}
		return convertView;
	}

	public static int calculateSignalLevel(int rssi, int numLevels) {
		if (rssi <= MIN_RSSI) {
			return 0;
		} else if (rssi >= MAX_RSSI) {
			return numLevels - 1;
		} else {
			int partitionSize = (MAX_RSSI - MIN_RSSI) / (numLevels - 1);
			return (rssi - MIN_RSSI) / partitionSize;
		}
	}

	class Holder {
		TextView ssid_tv;
		ImageView wifi_signal_status_iv;
		ImageView wifi_selected_iv;
	}

}

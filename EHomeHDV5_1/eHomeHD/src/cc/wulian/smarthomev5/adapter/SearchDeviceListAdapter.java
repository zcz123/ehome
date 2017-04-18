package cc.wulian.smarthomev5.adapter;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.wulian.iot.Config;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.smarthomev5.activity.BackMusicActivationActivity;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.fragment.device.DeviceDetailsFragment;

public class SearchDeviceListAdapter extends WLBaseAdapter<WulianDevice> {

	private Map<WulianDevice,DeviceShortCutControlItem> deviceMap = new HashMap<WulianDevice, DeviceShortCutControlItem>();
	
	public SearchDeviceListAdapter(Context context) {
		super(context,null);
	}


	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return null;
	}

	@Override
	protected void bindView(Context context, View view, int pos,
			WulianDevice item) {
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final WulianDevice device = getItem(position);
		
		DeviceShortCutControlItem item = device.onCreateShortCutView(deviceMap.get(device),LayoutInflater.from(mContext));
		deviceMap.put(device, item);
		View view = item.getView();
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDetails(device);
			}
		});
		return view;
	}
	
	protected void showDetails(WulianDevice device) {
		String isvalidate=device.getDeviceInfo().getIsvalidate();

		if(isvalidate!=null&&isvalidate.equals("2")) { //为2时去验证
			Intent it = new Intent(mContext, BackMusicActivationActivity.class);
			it.putExtra(Config.DEVICE_ID, device.getDeviceID());
			it.putExtra(Config.DEVICE_TYPE, device.getDeviceType());
			it.putExtra(Config.GW_ID, device.getDeviceGwID());
			mContext.startActivity(it);
			return;
		}
		Bundle args = new Bundle();
		args.putString(DeviceDetailsFragment.EXTRA_DEV_GW_ID,
				device.getDeviceGwID());
		args.putString(DeviceDetailsFragment.EXTRA_DEV_ID, device.getDeviceID());
		Intent intent = new Intent();

		intent.setClass(mContext, DeviceDetailsActivity.class);
		if (args != null)
			intent.putExtras(args);
		mContext.startActivity(intent);
	
	}

}

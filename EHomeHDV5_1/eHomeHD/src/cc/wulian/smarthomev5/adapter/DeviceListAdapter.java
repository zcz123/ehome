package cc.wulian.smarthomev5.adapter;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.fragment.device.DeviceDetailsFragment;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.Trans2PinYin;

import com.yuantuo.customview.ui.WLDialog;

public class DeviceListAdapter extends WLBaseAdapter<WulianDevice>{

	private Resources resource;
	private Map<WulianDevice,DeviceShortCutControlItem> deviceMap = new HashMap<WulianDevice, DeviceShortCutControlItem>();
	public DeviceListAdapter(Context context) {
		super(context,null);
		resource = context.getResources();
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
		return view;
	}
	

	public int getPostionByLetter(String str){
		for (int i=0 ; i<getCount() ;i++) {
			String deviceName =DeviceTool.getDeviceShowName(getItem(i));
			if (deviceName == null)
				continue;
			if ((Trans2PinYin.trans2PinYin(deviceName).trim().toLowerCase())
					.startsWith(str.toLowerCase())) {
				return i;

			}
		}
		return 0;
	}
}

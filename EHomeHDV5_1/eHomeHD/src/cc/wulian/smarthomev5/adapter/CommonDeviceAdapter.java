package cc.wulian.smarthomev5.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuLayout;

public class CommonDeviceAdapter extends SwipeMenuAdapter<WulianDevice> {
	private Map<WulianDevice,DeviceShortCutControlItem> deviceMap = new HashMap<WulianDevice, DeviceShortCutControlItem>();
	
	public CommonDeviceAdapter(Context context, List<WulianDevice> data) {
		super(context, data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final WulianDevice device = getItem(position);
		DeviceShortCutControlItem item = device.onCreateShortCutView(deviceMap.get(device),LayoutInflater.from(mContext));
		deviceMap.put(device, item);
		convertView = item.getView();
		SwipeMenuLayout layout = null;
		if(convertView != null)
		  layout = createMenuView(position, parent, convertView);		
		return layout;
	}
}

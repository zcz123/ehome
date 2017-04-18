/**
 * xiaozhi 2014-7-28
 */
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

/**
 * @创作日期 2014-7-28
 */
public class AreaDeviceAdapter extends WLBaseAdapter<WulianDevice> {

	private Map<WulianDevice, DeviceShortCutControlItem> deviceMap = new HashMap<WulianDevice, DeviceShortCutControlItem>();

	public AreaDeviceAdapter(Context context, List<WulianDevice> data) {
		super(context, data);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return null;
	}

	@Override
	protected void bindView(Context context, View view, final int pos,
			WulianDevice item) {
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final WulianDevice device = getItem(position);
		DeviceShortCutControlItem item = device.onCreateShortCutView(
				deviceMap.get(device), LayoutInflater.from(mContext));
		deviceMap.put(device, item);
		View view = item.getView();
		return view;
	}

}

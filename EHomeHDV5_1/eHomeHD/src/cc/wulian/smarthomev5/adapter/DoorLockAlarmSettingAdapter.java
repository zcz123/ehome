package cc.wulian.smarthomev5.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.app.model.device.impls.controlable.doorlock.AbstractDoorLockAlarmItem;

public class DoorLockAlarmSettingAdapter extends WLBaseAdapter<AbstractDoorLockAlarmItem>{

	public DoorLockAlarmSettingAdapter(Context context) {
		super(context, new ArrayList<AbstractDoorLockAlarmItem>());
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rootView = getItem(position).getShowView();
		return rootView;
	}
}

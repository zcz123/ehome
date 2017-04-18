package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.smarthomev5.R;

public class IRDeviceKeysAdapter extends EditableBaseAdapter<DeviceIRInfo>
{

	public IRDeviceKeysAdapter( Context context, List<DeviceIRInfo> data )
	{
		super(context, data);
	}

	@Override
	protected View newView( Context context, LayoutInflater inflater, ViewGroup parent, int pos ) {
		return inflater.inflate(R.layout.item_infrared_device_key, parent, false);
	}

	@Override
	protected void bindView( Context context, View view, int pos, final DeviceIRInfo item ) {

		TextView deviceKeyTextView = (TextView) view.findViewById(R.id.infrared_transmission_key_name);
		if ("0".equals(item.getStatus())) deviceKeyTextView.setTextColor(context.getResources().getColor(R.color.holo_red_dark));

		if ("1".equals(item.getStatus())) deviceKeyTextView.setTextColor(context.getResources().getColor(android.R.color.white));

		if (("-1").equals(item.getCode())) deviceKeyTextView.setTextColor(context.getResources().getColor(android.R.color.white));

		deviceKeyTextView.setText(item.getName());

	}

	@Override
	public OnClickListener setEditableClickListener( int pos, DeviceIRInfo item ) {
		return null;
	}

}

package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;

public class MonitorSetInfoAdapter extends AbstractIconTextSimpleAdapter<CameraInfo>
{
	public MonitorSetInfoAdapter( Context context, List<CameraInfo> data )
	{
		super(context, data);
	}
	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.item_monitor_choose_type, parent, false);
	}
	@Override
	protected void bindView( Context context, View view, int pos, CameraInfo item ) {
		TextView textView = (TextView) view.findViewById(R.id.monitor_type);
		textView.setText(item.camName);
	}

	@Override
	public OnClickListener setEditableClickListener( int pos, CameraInfo item ) {
		return null;
	}
}

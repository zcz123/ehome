package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class SimpleMonitorDialogListAdapter extends WLBaseAdapter<RoomInfo>
{
	public SimpleMonitorDialogListAdapter( Context context, List<RoomInfo> data )
	{
		super(context, data);
	}

	@Override
	protected void bindView( Context context, View view, int pos, RoomInfo item ){
		ImageView iconImageView = (ImageView)view.findViewById(R.id.monitor_icon_iv);
		iconImageView.setImageResource(R.drawable.monitor_wl_list_image);
		TextView monitorName = (TextView)view.findViewById(R.id.monitor_name_tv);
		monitorName.setText(item.getName());
	}


	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.monitor_popup_list_item, null);
	}

}

package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.more.route.DeviceStatusFragment.DeviceStatusEntity;
import cc.wulian.smarthomev5.tools.AccountManager;

/**
 * Created by WIN7 on 2014/7/11.
 */
public class DeviceStatusAdapter extends WLBaseAdapter<DeviceStatusEntity> {
	
	public DeviceStatusAdapter(Context context, List<DeviceStatusEntity> data) {
		super(context, data);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		View newView = inflater.inflate(R.layout.more_setting_route_device_status_adapter, parent, false);
		return newView;
	}

	@Override
	protected void bindView(Context context, View view, int pos, DeviceStatusEntity item) {
		ImageView imagView = (ImageView) view.findViewById(R.id.device_status_item_imgview);
		TextView name = (TextView) view.findViewById(R.id.device_status_item_name);
		TextView area = (TextView) view.findViewById(R.id.device_status_item_area);
		TextView uplink = (TextView) view.findViewById(R.id.device_status_item_uplink);
		TextView data = (TextView) view.findViewById(R.id.device_status_item_data);
		imagView.setImageDrawable(item.getDrawable());
		name.setText(item.getDeviceName());
		area.setText("["+item.getArea()+"]");
		if(item.getUpLink()==null){
			uplink.setVisibility(View.INVISIBLE);
		}else{
			uplink.setText("Up:-"+item.getUpLink());
		}
		if (item.getData() == null){
			data.setText("Down:");
		}else {
			data.setText("Down:-"+item.getData());
		}
	}
}
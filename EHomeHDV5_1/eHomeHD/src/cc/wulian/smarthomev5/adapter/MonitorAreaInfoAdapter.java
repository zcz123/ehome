package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;

public class MonitorAreaInfoAdapter extends WLBaseAdapter<DeviceAreaEntity>
{   
	private TextView monintorAreaName;
	public MonitorAreaInfoAdapter( Context context, List<DeviceAreaEntity> data )
	{
		super(context, data);
	}
	public int getPositionByAreaID(String areaID){
		for(int i = 0 ; i< getCount() ;i++){
			DeviceAreaEntity entity = getItem(i);
			if(entity.getRoomID().equals(areaID)){
				return i;
			}
		}
		return getCount() -1;
	}
	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.item_monitor_area_choose, parent, false);
	}
	@Override
	protected void bindView( Context context, View view, int pos, DeviceAreaEntity item ) {
		monintorAreaName = (TextView) view.findViewById(R.id.monitor_arename);
		monintorAreaName.setBackgroundColor(Color.WHITE);
		monintorAreaName.setText(item.getName());
		monintorAreaName.setTextColor(Color.BLACK);
	}

}

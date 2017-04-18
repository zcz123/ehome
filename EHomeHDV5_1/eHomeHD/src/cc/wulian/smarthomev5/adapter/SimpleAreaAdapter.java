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

public class SimpleAreaAdapter extends WLBaseAdapter<RoomInfo>
{
	public SimpleAreaAdapter( Context context, List<RoomInfo> data )
	{
		super(context, data);
	}

	@Override
	protected void bindView( Context context, View view, int pos, RoomInfo item ){
		ImageView iconImageView = (ImageView)view.findViewById(R.id.area_icon_iv);
		if(!CmdUtil.SCENE_UNKNOWN.equals(item.getName())){
			iconImageView.setImageResource(DeviceTool.PressgetAreaIconResourceByIconIndex(item.getIcon()));
		}else{
			iconImageView.setImageDrawable(null);
		}
		TextView areaName = (TextView)view.findViewById(R.id.area_name_tv);
		areaName.setText(item.getName());
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.device_popup_area_item, null);
	}

}

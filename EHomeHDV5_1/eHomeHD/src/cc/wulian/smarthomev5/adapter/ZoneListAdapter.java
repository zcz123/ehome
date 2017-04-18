package cc.wulian.smarthomev5.adapter;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.ZoneListEntity;
import cc.wulian.smarthomev5.utils.LanguageUtil;

public class ZoneListAdapter extends WLBaseAdapter<ZoneListEntity> {
	
	public ZoneListAdapter(Context context, List<ZoneListEntity> data) {
		super(context, data);
	}

	  protected View newView(Context context, LayoutInflater inflater, ViewGroup paramViewGroup, int paramInt)
	  {
	    return inflater.inflate(R.layout.zone_list_item_layout, paramViewGroup, false);
	  }
	  
	  protected void bindView(Context context, View view, int position, ZoneListEntity item)
	  {
		  TextView zoneName=(TextView) view.findViewById(R.id.zone_name);
		  TextView zoneTime=(TextView) view.findViewById(R.id.zone_time);
		  zoneName.setText(item.getCity());
		  zoneTime.setText("");
	  }
}

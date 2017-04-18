package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.location;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.entity.ZoneListEntity;
import cc.wulian.smarthomev5.utils.LanguageUtil;

public class LocationCountryListAdapter extends WLBaseAdapter<String> {
	
	public LocationCountryListAdapter(Context context, List<String> data) {
		super(context, data);
	}

	  protected View newView(Context context, LayoutInflater inflater, ViewGroup paramViewGroup, int paramInt)
	  {
	    return inflater.inflate(R.layout.device_thermostat82_location_list_item, paramViewGroup, false);
	  }
	  
	  protected void bindView(Context context, View view, int position, String item)
	  {
		  TextView itemName=(TextView) view.findViewById(R.id.location_item_name_tv);
		  itemName.setText(item);		 	  
	  }
}

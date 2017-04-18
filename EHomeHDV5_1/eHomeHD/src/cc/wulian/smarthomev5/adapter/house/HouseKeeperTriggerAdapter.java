package cc.wulian.smarthomev5.adapter.house;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTriggerItem;

public class HouseKeeperTriggerAdapter extends WLBaseAdapter<HouseKeeperTriggerItem>{

//	private Map<AutoConditionInfo,HouseKeeperTriggerItem> triggerMap = new HashMap<AutoConditionInfo,HouseKeeperTriggerItem>();
	
	public HouseKeeperTriggerAdapter(Context context,List<HouseKeeperTriggerItem> infos) {
		super(context, infos);
	}

//	@Override
//	public View getView(int position, View convertView, ViewGroup parent) {
//		final AutoConditionInfo info = getItem(position);
//		HouseKeeperTriggerItem item = triggerMap.get(info);
//		triggerMap.put(info, item);
//		convertView = item.getView(info);
//		SwipeMenuLayout layout = null;
//		if(convertView != null)
//		  layout = createMenuView(position, parent, convertView);		
//		return layout;
//	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItem(position).getView();
	}
}

package cc.wulian.smarthomev5.adapter.house;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperConditionItem;

public class HouseKeeperConditionAdapter extends WLBaseAdapter<HouseKeeperConditionItem>{

//	private Map<AutoConditionInfo,HouseKeeperConditionItem> conditionMap = new HashMap<AutoConditionInfo,HouseKeeperConditionItem>();
	
	public HouseKeeperConditionAdapter(Context context,
			List<HouseKeeperConditionItem> infos) {
		super(context, infos);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getItem(position).getView();
	}

	
}

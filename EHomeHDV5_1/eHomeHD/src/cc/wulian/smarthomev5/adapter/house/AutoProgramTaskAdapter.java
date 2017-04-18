package cc.wulian.smarthomev5.adapter.house;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.smarthomev5.fragment.house.AutoProgramTaskItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuLayout;

public class AutoProgramTaskAdapter extends SwipeMenuAdapter<AutoProgramTaskInfo>{

	private Map<AutoProgramTaskInfo,AutoProgramTaskItem> autoTaskMap = new HashMap<AutoProgramTaskInfo, AutoProgramTaskItem>();
	public AutoProgramTaskAdapter(Context context,
			List<AutoProgramTaskInfo> data) {
		super(context, data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final AutoProgramTaskInfo info = getItem(position);
		AutoProgramTaskItem item = new AutoProgramTaskItem(mContext,info);
		autoTaskMap.put(info, item);
		convertView = item.getView(info);
		SwipeMenuLayout layout = null;
		if(convertView != null)
		  layout = createMenuView(position, parent, convertView);		
		return layout;
	}
}

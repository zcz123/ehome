package cc.wulian.smarthomev5.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.smarthomev5.entity.PermissionEntity;
import cc.wulian.smarthomev5.fragment.setting.permission.PermissionItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuLayout;

public class PermissionManagerAdapter extends SwipeMenuAdapter<PermissionEntity>{

	private Map<PermissionEntity,PermissionItem> permissionMap = new HashMap<PermissionEntity, PermissionItem>();
	
	public PermissionManagerAdapter(Context context,
			List<PermissionEntity> mData) {
		super(context, mData);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final PermissionEntity info = getItem(position);
		PermissionItem item = new PermissionItem(mContext,info);
		permissionMap.put(info, item);
		convertView = item.getView(info);
		SwipeMenuLayout layout = null;
		if(convertView != null)
		  layout = createMenuView(position, parent, convertView);		
		return layout;
	}
}

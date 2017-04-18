package cc.wulian.smarthomev5.fragment.setting.router;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.callback.router.entity.BlackAndWhiteEntity;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuLayout;

public class RouterBlackListAdapter extends
		SwipeMenuAdapter<BlackAndWhiteEntity> {

	public RouterBlackListAdapter(Context context,
			List<BlackAndWhiteEntity> mData) {
		super(context, mData);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		convertView = inflater.inflate(
				R.layout.device_df_router_setting_black_list_item, null);
		TextView tvName = (TextView) convertView
				.findViewById(R.id.router_setting_black_name);
		BlackAndWhiteEntity item = getItem(position);
		tvName.setText(item.getName());
		SwipeMenuLayout layout = null;
		if (convertView != null)
			layout = createMenuView(position, parent, convertView);
		return layout;
	}

}

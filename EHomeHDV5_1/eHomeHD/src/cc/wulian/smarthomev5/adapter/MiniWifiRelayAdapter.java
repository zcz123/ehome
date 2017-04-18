package cc.wulian.smarthomev5.adapter;

import java.util.ArrayList;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.RouteRemindWifiAdapter.WifiInfoEntity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MiniWifiRelayAdapter extends WLBaseAdapter<WifiInfoEntity> {

	public MiniWifiRelayAdapter(Context context,
			ArrayList<WifiInfoEntity> wifiInfos) {
		super(context, wifiInfos);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		View newView = inflater.inflate(R.layout.mini_wifi_relay_item, parent,
				false);
		return newView;
	}

	@Override
	protected void bindView(Context context, View view, int pos,
			WifiInfoEntity item) {

		TextView wifiSSID = (TextView) view
				.findViewById(R.id.mini_setting_wifi_name_tv);
		ImageView wifiCapabilities = (ImageView) view
				.findViewById(R.id.mini_setting_wifi_lock_iv);
		wifiSSID.setText(item.getSsid());
		if (item.getCapabilities().equals("none")) {
			wifiCapabilities.setVisibility(View.INVISIBLE);
		} else {
			wifiCapabilities.setVisibility(View.VISIBLE);
		}

	}

}
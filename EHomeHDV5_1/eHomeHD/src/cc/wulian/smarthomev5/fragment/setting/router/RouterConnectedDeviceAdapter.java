package cc.wulian.smarthomev5.fragment.setting.router;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.callback.router.entity.DeviceInfo;

public class RouterConnectedDeviceAdapter extends
		WLBaseAdapter<DeviceInfo> {
	private TextView name;
	private TextView upload;
	private TextView download;

	public RouterConnectedDeviceAdapter(Context context,
			List<DeviceInfo> data) {
		super(context, data);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(
				R.layout.device_df_router_setting_connected_device_list_item,
				null);
	}

	@Override
	protected void bindView(final Context context, View view, int pos,
			final DeviceInfo item) {
		super.bindView(context, view, pos, item);
		name = (TextView) view
				.findViewById(R.id.router_setting_device_item_name);
		upload = (TextView) view
				.findViewById(R.id.router_setting_device_item_speed_upload);
		download = (TextView) view
				.findViewById(R.id.router_setting_device_item_speed_download);
		name.setText(item.getName());

		StringBuffer sbUp = new StringBuffer();
		sbUp.append(item.getUp());
		sbUp.append("kb/s");
		upload.setText(sbUp);

		StringBuffer sbDown = new StringBuffer();
		sbDown.append(item.getDown());
		sbDown.append("kb/s");
		download.setText(sbDown);

		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent();
				intent.setClass(context,
						RouterConnectedDeviceDetailActivity.class);
				intent.putExtra(
						RouterConnectedDeviceDetailActivity.KEY_ITEM_MAC,
						item.getMac());
				intent.putExtra(
						RouterConnectedDeviceDetailActivity.KEY_ITEM_NAME,
						item.getName());
				intent.putExtra(
						RouterConnectedDeviceDetailActivity.KEY_ITEM_IP,
						item.getIp());
				context.startActivity(intent);

			}
		});
	}

}

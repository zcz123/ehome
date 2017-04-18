package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.entity.camera.MonitorWLCloudEntity;

/**
 * Created by WIN7 on 2014/7/11.
 */
public class MonitorWLCloudAdapter extends WLBaseAdapter<MonitorWLCloudEntity> {
	
	private final DeviceCache mCache;
	private final MainApplication mApplication;
    public MonitorWLCloudAdapter(Context context, List<MonitorWLCloudEntity> data ) {
		super(context, data);
		mCache = DeviceCache.getInstance(context);
		mApplication = MainApplication.getApplication();
//		authForOpenCamera = auth;
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		 return inflater.inflate(R.layout.monitor_wl_list_adapter, parent, false);
	}
	
	@Override
	protected void bindView(Context context, View view, int pos,
			final MonitorWLCloudEntity item) {
		
		LinearLayout adapterLayout = (LinearLayout) view.findViewById(R.id.monitor_wl_device_list_layout);
		TextView mTextView = (TextView) view.findViewById(R.id.detail_message);
		TextView isOnLineTextView = (TextView) view.findViewById(R.id.is_online);
		if(item.getMonitorIsOnline().equals("1")){
			isOnLineTextView.setText(mContext.getResources().getString(R.string.device_config_edit_dev_status_online));
			isOnLineTextView.setTextColor(Color.BLACK);
		}
		else{
			isOnLineTextView.setText(mContext.getResources().getString(R.string.device_config_edit_dev_status_offline));
			isOnLineTextView.setTextColor(Color.GRAY);
		}
		mTextView.setText(item.getMonitorDeviceNick());
	}
}

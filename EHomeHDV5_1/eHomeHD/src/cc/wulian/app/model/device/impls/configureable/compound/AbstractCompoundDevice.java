package cc.wulian.app.model.device.impls.configureable.compound;

import java.util.HashMap;
import java.util.List;

import com.wulian.icam.model.Device;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.configureable.touch.AbstractTouchDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.tools.DeviceList;
import cc.wulian.smarthomev5.tools.DeviceList.OnDeviceListItemClickListener;
import cc.wulian.smarthomev5.tools.JsonTool;

public abstract class AbstractCompoundDevice extends AbstractTouchDevice {
	public AbstractCompoundDevice(Context context, String type) {
		super(context, type);
	}

	public abstract String[] getDeviceEpResources();
	public abstract String[] getDeviceEpNames();

	public abstract List<DeviceInfo> getSelectDevices();

	@Override
	public View onCreateSettingView(LayoutInflater inflater, ViewGroup container) {
		View view = super.onCreateSettingView(inflater, container);
		getBindDevicesMap();
		for (int i = 0; i < getDeviceEpResources().length; i++) {
			final String ep = getDeviceEpResources()[i];
			LinearLayout itemView = (LinearLayout) inflater.inflate(
					R.layout.device_touch_bind_scene_item, null);
			TextView epNameTextView = (TextView) itemView
					.findViewById(R.id.touch_bind_ep_name);
			epNameTextView.setText(getDeviceEpNames()[i]);
			final TextView deviceNameTextView = (TextView) itemView
					.findViewById(R.id.touch_bind_scene_device_name);
			String deviceName = getResources().getString(
					R.string.device_no_bind_dev);
			if (bindDevicesMap.containsKey(ep)) {
				DeviceInfo deviceInfo =  bindDevicesMap.get(ep);
				if(deviceInfo != null){
					deviceName = getDefaultDeviceName(mContext,deviceInfo);
				}
			}
			deviceNameTextView.setText(deviceName);
			itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final DeviceList deviceList = new DeviceList(mContext,
							getSelectDevices());
					deviceList
							.setOnDeviceListItemClickListener(new OnDeviceListItemClickListener() {

								@Override
								public void onDeviceListItemClicked(
										DeviceList list, int pos,
										DeviceInfo deviceInfo) {
									if(!deviceInfo.getName().equals(mContext.getResources().getString(cc.wulian.smarthomev5.R.string.scene_unbind))){
										deviceNameTextView.setText(getDefaultDeviceName(mContext,deviceInfo));
										bindDevicesMap.put(ep, deviceInfo);
										JsonTool.uploadBindList(mContext,
												bindScenesMap, bindDevicesMap,
												gwID, devID, type);
									}else{
										deviceNameTextView.setText(deviceInfo.getName());
										JsonTool.uploadBindList(mContext,
												bindScenesMap, null,
												gwID, devID, type);
									}
									deviceList.dismiss();
								}
							});
					deviceList.show(v);
				}
			});
			contentLineLayout.addView(itemView);
		}
		return view;
	}

	
	private String getDefaultDeviceName(Context context, DeviceInfo deviceInfo){
		String defaultName = null;
		final DeviceCache mDeviceCache = DeviceCache.getInstance(context);
		WulianDevice device = mDeviceCache.getDeviceByID(mContext, deviceInfo.getGwID(), deviceInfo.getDevID());
		if(StringUtil.isNullOrEmpty(device.getDeviceName())){
			defaultName = device.getDefaultDeviceName();
		}else{
			defaultName = device.getDeviceName();
		}
		return defaultName;
	}
	protected void getBindDevicesMap() {
		bindDevicesMap = MainApplication.getApplication().bindDeviceInfoMap
				.get(getDeviceGwID() + getDeviceID());
		if (bindDevicesMap == null) {
			bindDevicesMap = new HashMap<String, DeviceInfo>();
		}
	}
}

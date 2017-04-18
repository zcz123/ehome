package cc.wulian.smarthomev5.fragment.more.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.DeviceStatusAdapter;
import cc.wulian.smarthomev5.adapter.RouteRemindWifiAdapter;
import cc.wulian.smarthomev5.adapter.RouteRemindWifiAdapter.WifiInfoEntity;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.event.FlowerEvent;
import cc.wulian.smarthomev5.event.RssiEvent;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.Preference;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class DeviceStatusFragment extends WulianFragment {

	public DeviceStatusAdapter mRouteSettingWifiAdapter;
	private List<DeviceStatusEntity> mDeviceStatusData;
	protected AccountManager mAccountManger = AccountManager.getAccountManger();

	@ViewInject(R.id.more_setting_route_device_status_lv)
	private ListView deviceStatusListView;

	private int currentSecond=60;

	Handler handler = new Handler();
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			currentSecond--;
			getSupportActionBar().setRightIconText(currentSecond+"s");
			if(currentSecond>0){
				handler.postDelayed(this, 1000);
			}else{
				currentSecond=60;
				getSupportActionBar().setRightIconText(mApplication.getResources().getString(
						R.string.device_config_edit_dev_refresh));
				getSupportActionBar().setRightMenuClickListener(
						new OnRightMenuClickListener() {

							@Override
							public void onClick(View v) {
								getData();
								getSupportActionBar().setRightMenuClickListener(null);
								handler.post(runnable);
							}
						});
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(null);
		getSupportActionBar().setTitle(
				R.string.more_setting_route_device_status_title);
		getSupportActionBar().setRightIconText(
				mApplication.getResources().getString(
						R.string.device_config_edit_dev_refresh));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						getData();
						getSupportActionBar().setRightMenuClickListener(null);
						handler.post(runnable);
					}
				});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.more_setting_route_device_status,
				container, false);
		ViewUtils.inject(this, view);
		return view;
	}

	private void getData() {
		mDeviceStatusData.clear();
		DeviceCache deviceCache = DeviceCache.getInstance(mActivity);
		for (WulianDevice device : deviceCache.getAllDevice()) {
			String devID = device.getDeviceID();
			Drawable drawable = device.getDefaultStateSmallIcon();
			String deviceName = device.getDeviceName();
			if (deviceName == null || deviceName.equals("")) {
				deviceName = device.getDefaultDeviceName();
			}
			DeviceAreaEntity areaEntity = AreaGroupManager.getInstance()
					.getDeviceAreaEntity(device.getDeviceGwID(),
							device.getDeviceRoomID());
			String area = areaEntity.getName();
			DeviceStatusEntity deviceStatusEntity = new DeviceStatusEntity(
					drawable, deviceName, area, null, null, devID);
			mDeviceStatusData.add(deviceStatusEntity);

			SendMessage.sendQueryDevRssiMsg(device.getDeviceGwID(),
					device.getDeviceID(), false);
		}
		mRouteSettingWifiAdapter.swapData(mDeviceStatusData);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mDeviceStatusData = new ArrayList<DeviceStatusEntity>();
		mRouteSettingWifiAdapter = new DeviceStatusAdapter(mActivity, null);
		deviceStatusListView.setAdapter(mRouteSettingWifiAdapter);
		getData();
	}

	public void onEventMainThread(RssiEvent event) {
		String data=event.data;
		String uplink=event.uplink;

		for(DeviceStatusEntity entity:mDeviceStatusData){
			if(entity.getDevID().equals(event.devID)){
				entity.setData(data);
				entity.setUpLink(uplink);
				mRouteSettingWifiAdapter.notifyDataSetChanged();
				return;
			}
		}
	}

	public static class DeviceStatusEntity {
		private Drawable drawable;
		private String deviceName;
		private String area;
		private String data;
		private String upLink;
		private String devID;

		public DeviceStatusEntity(Drawable drawable, String deviceName,
								  String area, String data, String upLink, String devID) {
			super();
			this.drawable = drawable;
			this.deviceName = deviceName;
			this.area = area;
			this.data = data;
			this.upLink = upLink;
			this.devID=devID;
		}

		public Drawable getDrawable() {
			return drawable;
		}

		public void setDrawable(Drawable drawable) {
			this.drawable = drawable;
		}

		public String getDeviceName() {
			return deviceName;
		}

		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}

		public String getArea() {
			return area;
		}

		public void setArea(String area) {
			this.area = area;
		}

		public String getData() {
			return data;
		}

		public void setData(String data) {
			this.data = data;
		}

		public String getUpLink() {
			return upLink;
		}

		public void setUpLink(String upLink) {
			this.upLink = upLink;
		}

		public String getDevID() {
			return devID;
		}

		public void setDevID(String devID) {
			this.devID = devID;
		}
	}

}

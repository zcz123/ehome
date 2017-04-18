package cc.wulian.smarthomev5.fragment.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BackMusicActivationActivity;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.adapter.AreaDeviceAdapter;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wulian.iot.Config;

public class AreaDevicesFragement extends WulianFragment {

	private static final Comparator<WulianDevice> DEVICE_COMPARATOR = new Comparator<WulianDevice>(){
		@Override
		public int compare( WulianDevice lhs, WulianDevice rhs ){
			if(lhs.isDeviceOnLine() && !rhs.isDeviceOnLine()){
				return -1;
			}else if(!lhs.isDeviceOnLine() && rhs.isDeviceOnLine()){
				return 1;
			}else{
				int lType = StringUtil.toInteger(lhs.getDeviceType(), 16);
				int rType = StringUtil.toInteger(rhs.getDeviceType(), 16);
				return lType - rType;
			}
		}
		
	};
	public static final String AREADEVICES_ROOMID = "areadevices_roomid";
	@ViewInject(R.id.area_device_ground_name)
	private TextView areaNameTextView;
	@ViewInject(R.id.area_device_detail_listview)
	private ListView areaDevicesListView;
	private AreaDeviceAdapter deviceAreaDeviceAdapter;
	private DeviceCache deviceCache;
	private String roomID="";
	private String roomName="";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initRoomData();
		initActionBar();
		deviceCache = DeviceCache.getInstance(mActivity);
		deviceAreaDeviceAdapter = new AreaDeviceAdapter(mActivity,null);
	}

	private void initRoomData() {
		roomID=getActivity().getIntent().getStringExtra(AREADEVICES_ROOMID);
		DeviceAreaEntity areaEntity = AreaGroupManager.getInstance()
				.getDeviceAreaEntity(AccountManager.getAccountManger().getmCurrentInfo().getGwID(),roomID);
		roomName= areaEntity.getName();
	}

	private void initActionBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.nav_device_title));
		getSupportActionBar().setTitle(roomName);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().show();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.device_area_devices_content,null);
		ViewUtils.inject(this, view);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (roomName != null) {
			areaNameTextView.setText(roomName
					+ mApplication.getResources().getString(R.string.nav_device_title));
			areaDevicesListView.setAdapter(deviceAreaDeviceAdapter);
			areaDevicesListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					showAreaDeviceDetails(deviceAreaDeviceAdapter.getItem(position));
				}
			});
			loadAreaDevices();
		}
	}

	private void loadAreaDevices() {
		TaskExecutor.getInstance().execute(new Runnable() {
			
			@Override
			public void run() {
				final List<WulianDevice> devices = getDevicesByArea();
				mActivity.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						deviceAreaDeviceAdapter.swapData(devices);
					}
				});
			}
		});
	}
	public void showAreaDeviceDetails(WulianDevice device) {
		String isvalidate=device.getDeviceInfo().getIsvalidate();
		if(isvalidate!=null&&isvalidate.equals("2")){
			Intent it =new Intent(mActivity, BackMusicActivationActivity.class);
			it.putExtra(Config.DEVICE_ID,device.getDeviceID());
			it.putExtra(Config.DEVICE_TYPE,device.getDeviceType());
			it.putExtra(Config.GW_ID,device.getDeviceGwID());
			startActivity(it);
			return;
		}
		Bundle args = new Bundle();
		args.putString(DeviceDetailsFragment.EXTRA_DEV_GW_ID,
				device.getDeviceGwID());
		args.putString(DeviceDetailsFragment.EXTRA_DEV_ID, device.getDeviceID());
		Intent intent = new Intent();

		intent.setClass(mActivity, DeviceDetailsActivity.class);
		if (args != null)
			intent.putExtras(args);
		mActivity.startActivity(intent);
	}

	private List<WulianDevice> getDevicesByArea(){
		List<WulianDevice> devices = new ArrayList<WulianDevice>();
		DeviceCache deviceCache = DeviceCache.getInstance(mActivity);
		for (WulianDevice device : deviceCache.getAllDevice()) {
			if(roomID.equals(device.getDeviceRoomID())){
				devices.add(device);
			}
		}
		Collections.sort(devices,DEVICE_COMPARATOR);
		return devices;
	}

	public void onEventMainThread(DeviceEvent event) {
		if(DeviceEvent.REFRESH.equals(event.action) || DeviceEvent.REMOVE.equals(event.action)){
			loadAreaDevices();
		}
	}
}

package cc.wulian.smarthomev5.fragment.house;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.Defenseable;
import cc.wulian.app.model.device.impls.sensorable.Sensorable;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectSensorDeviceDataFragment.SelectSensorDeviceDataListener;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;

import com.yuantuo.customview.ui.WLDialog.MessageListener;

/**
 * 触发事件和限制条件中设备的选择公共页, 主要是通过在不同安防设备类实现IViewResource接口中的onCreateHouseKeeperSelectControlDeviceDataView方法
 * 和不同检测设备类实现IViewResource接口中的onCreateHouseKeeperSelectSensorDeviceDataView方法, 来判定是dialog展示还是界面展示。
 * @author Administrator
 *
 */
public class HouseKeeperConditionSelectDeviceFragment extends WulianFragment{

	public static final String TRIGGER_INFO_DEVICE_SERIAL = "trigger_info_device_serial";
	public static final String TRIGGER_OR_CONDITION = "trigger_or_condition";
	
	private DeviceCache mDeviceCache ;
	private LinearLayout deviceLayout;
	private ScrollView scrollView;
	
	public static AutoConditionInfo conditionInfo;
	private String condition;
	
	private static ConditionDeviceListener conditionDeviceListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDeviceCache = DeviceCache.getInstance(mActivity);
		Bundle bundle = getArguments();
		if (bundle != null) {
			condition = bundle.getString(TRIGGER_OR_CONDITION);
			if(bundle.containsKey(TRIGGER_INFO_DEVICE_SERIAL)){
				conditionInfo = (AutoConditionInfo) bundle.getSerializable(TRIGGER_INFO_DEVICE_SERIAL);
			}else{
				conditionInfo = new AutoConditionInfo();
			}
		}
		initBar();
	}


	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_manager_fragment_choose_device, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		scrollView = (ScrollView) view.findViewById(R.id.house_keeper_task_device_scrollview);
		deviceLayout = (LinearLayout) view.findViewById(R.id.house_keeper_task_device_layout);
		scrollView.smoothScrollTo(0,0);
		initDeviceItem();
	}



	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		if(StringUtil.equals("trigger", condition)){
			getSupportActionBar().setIconText(R.string.house_rule_add_new_trigger_condition);
		}else if(StringUtil.equals("condition", condition)){
			getSupportActionBar().setIconText(R.string.house_rule_add_new_limit_condition);
		}else{
			getSupportActionBar().setIconText(R.string.about_back);
		}
		getSupportActionBar().setTitle(R.string.house_rule_add_new_condition_select_device);
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
				if(conditionDeviceListener != null){
					conditionDeviceListener.onConditionDeviceListenerChanged(null, null,null);
				}
				mActivity.finish();
			}
		});
	}
	
	private void initDeviceItem() {
		if(StringUtil.equals(condition, "trigger")){
			//可能加锁设备
			List<WulianDevice> deviceList = getLinkDevices();
			getConditionDeviceView(true,deviceList);	
		}else{
//			deviceList = getLinkSensorDevices();
			List<WulianDevice> deviceList = getLinkDevices();
			getConditionDeviceView(false,deviceList);	
		}
	}


	private void getConditionDeviceView(final boolean isTriggerCondition,List<WulianDevice> deviceList) {
		deviceLayout.removeAllViews();
		for(int i= 0; i < deviceList.size(); i++){
			final WulianDevice device = deviceList.get(i);
			if(!isTriggerCondition && !device.isLinkControlCondition()){
				 continue;
			}
			View itemView = new HouseKeeperConditionSelectDeviceItem(mActivity, device).getView();
			itemView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
			
			itemView.setOnClickListener(new OnClickListener() {
						
				@Override
				public void onClick(View arg0) {
					DialogOrActivityHolder holder = device.onCreateHouseKeeperSelectSensorDeviceDataView(inflater, conditionInfo,isTriggerCondition);
					if(holder.isShowDialog()){
						holder.createSelectControlDataDialog(mActivity, holder.getContentView(), new MessageListener() {
							
							@Override
							public void onClickPositive(View contentViewLayout) {
								if(conditionDeviceListener != null){
									if(device.getDeviceInfo().getType().equals("A1")){
										String deviceData = device.getDeviceID() + ">" + device.getDeviceType() + 
												">" + device.getDeviceInfo().getDevEPInfo().getEp() + ">" + device.getDeviceInfo().getDevEPInfo().getEpType();
										conditionDeviceListener.onConditionDeviceListenerChanged(conditionInfo.getObject(), conditionInfo.getExp(),conditionInfo.getDes());
									}else{
										String deviceData = device.getDeviceID() + ">" + device.getDeviceType() + 
												">" + device.getDeviceInfo().getDevEPInfo().getEp() + ">" + device.getDeviceInfo().getDevEPInfo().getEpType();
										conditionDeviceListener.onConditionDeviceListenerChanged(deviceData, conditionInfo.getExp(),conditionInfo.getDes());
									}
								}
								mActivity.finish();
							}
							
							@Override
							public void onClickNegative(View contentViewLayout) {
								
							}
						}).show();
					}else{
						Bundle bundle = new Bundle();
						bundle.putString(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, HouseKeeperSelectSensorDeviceDataFragment.class.getName());
						bundle.putString(HouseKeeperSelectSensorDeviceDataFragment.DEV_GW_ID, device.getDeviceGwID());
						bundle.putString(HouseKeeperSelectSensorDeviceDataFragment.DEV_ID, device.getDeviceID());
						holder.startActivity(mActivity, bundle);
						//选择设备后
						HouseKeeperSelectSensorDeviceDataFragment.setSelectSensorDeviceDataListener(new SelectSensorDeviceDataListener() {
							
							@Override
							public void onSelectDeviceDataChanged(String ep,String epType,String value, String des) {
								if(conditionDeviceListener != null){
									String deviceData = device.getDeviceID() + ">" + device.getDeviceType() + 
											">" + ep + ">" + epType;
									conditionDeviceListener.onConditionDeviceListenerChanged(deviceData, value, des);
								}
								mActivity.finish();
							}
						});
					}
//					taskView.setDeviceChooseListener(new DeviceChooseListener() {
//						
//						@Override
//						public void onDeviceChooseListenerChanged(AutoConditionInfo info) {
//							if(info == null){
//								
//							}else{
//								mActivity.finish();
//								if(conditionDeviceListener != null){
//									String deviceData = device.getDeviceID() + ">" + device.getDeviceType() + 
//											">" + device.getDeviceInfo().getDevEPInfo().getEp() + ">" + device.getDeviceInfo().getDevEPInfo().getEpType();
//									conditionDeviceListener.onConditionDeviceListenerChanged(deviceData, info.getExp());
//								}
//							}
//						}
//					});		
//					taskView.show();
				}
			});
			deviceLayout.addView(itemView);
		}
	}
	
	//获取缓存中应该联动的所有设备
		private List<WulianDevice> getLinkDevices(){
			Collection<WulianDevice> result = mDeviceCache.getAllDevice();
			List<WulianDevice> devices = new ArrayList<WulianDevice>();
			for(WulianDevice device : result){
				if(device.isLinkControl()){
					devices.add(device);
				}
			}
			Comparator<WulianDevice> comparator = new Comparator<WulianDevice>() {

				@Override
				public int compare(WulianDevice arg0, WulianDevice arg1) {
					if(arg0 instanceof Defenseable){
						return -1;
					}
					if(arg0 instanceof Sensorable && !(arg1 instanceof Defenseable)){
						return -1;
					}
					else if(arg0 instanceof Sensorable && arg1 instanceof Defenseable){
						return 1;
					}else if(!(arg0 instanceof Sensorable) && !(arg0 instanceof Defenseable)){
						return 1;
					}
					return 0;
				}
		            
		    };
			Collections.sort(devices, comparator);
			return devices;
		}

	// 获取缓存中应该联动的所有检测类设备
	private List<WulianDevice> getLinkSensorDevices() {
		Collection<WulianDevice> result = mDeviceCache.getAllDevice();
		List<WulianDevice> devices = new ArrayList<WulianDevice>();
		for (WulianDevice device : result) {
			if (device.isLinkControl() && !(device instanceof Defenseable)) {
				devices.add(device);
			}
		}
		return devices;
	}
		
//		private List<WulianDevice> getSensorDevices(){
//			Collection<WulianDevice> result = mDeviceCache.getAllDevice();
//			List<WulianDevice> devices = new ArrayList<WulianDevice>();
//			for(WulianDevice device : result){
//				if(device.isLinkControl() && (device instanceof Sensorable)){
//					devices.add(device);
//				}
//			}
//			return devices;
//		}
		
		public static void setConditionDeviceListener(ConditionDeviceListener conditionDeviceListener) {
			HouseKeeperConditionSelectDeviceFragment.conditionDeviceListener = conditionDeviceListener;
		}


		public interface ConditionDeviceListener{
			public void onConditionDeviceListenerChanged(String deviceData,String value,String des);
		}
}

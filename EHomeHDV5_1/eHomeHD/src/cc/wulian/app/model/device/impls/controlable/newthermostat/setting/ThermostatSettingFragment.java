package cc.wulian.app.model.device.impls.controlable.newthermostat.setting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Telephony.Mms;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.newthermostat.ThermostatDialogManager;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.LocationDownItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.LocationItem;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.SettingManagerAdapter;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.fragment.setting.EmptyItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.filterreminder.FilterReminderActivity;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.AboutItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.ClickSoundItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.DifferentialSettingDownItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.DifferentialSettingItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.EmergencyHeatDownItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.EmergencyHeatItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.EquipmentSettingDownItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.EquipmentSettingItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.FactoryResetDownItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.FactoryResetItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.FilterReminderDownItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.FilterReminderItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.TempratureFormatItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.TimeSyncDownItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.TimeSyncItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.VibrateItem;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.DifferentialSettingItem.ShowDifferentialDownViewListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.EmergencyHeatItem.ShowEmergencyDownViewListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.EquipmentSettingItem.ShowEquipmentDownViewListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.FactoryResetDownItem.ResetBtnListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.FactoryResetItem.ShowFactoryResetDownViewListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.FilterReminderDownItem.GoButtonListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.FilterReminderItem.ShowFliterDownViewListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.items.TimeSyncItem.ShowTimeDownViewListener;
import cc.wulian.smarthomev5.tools.SendMessage;

public class ThermostatSettingFragment extends WulianFragment{

	private final String TAG = getClass().getSimpleName();
	
	public static final String GWID = "gwId";
	public static final String DEVID = "devId";
	public static final String EP= "ep";
	public static final String EPTYPE= "epType";
	public static final SimpleDateFormat TIME_FPRMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	
	private ThermostatDialogManager dialogManager = ThermostatDialogManager.getDialogManager();
	
	private DeviceCache cache;
	private AbstractDevice device;
	private String mEpData;
	//click sound 
	private ClickSoundItem clickSoundItem; 
	//vibrate
	private VibrateItem vibrateItem;
	//Temprature Format
	private TempratureFormatItem tempratureFormatItem ;
	//Filter Remineder
	private FilterReminderItem filterReminederItem;
	private FilterReminderDownItem filterReminderDownItem;
	//Time Sync
	private TimeSyncItem timeSyncItem;
	private TimeSyncDownItem timeSyncDownItem;
	// Emergency Heat
	private EmergencyHeatItem emergencyHeatItem;
	private EmergencyHeatDownItem emergencyHeatDownItem;
	// Equipment Setting
	private EquipmentSettingItem equipmentSettingItem;
	private EquipmentSettingDownItem equipmentSettingDownItem;
	// Defferential Setting
	private	 DifferentialSettingItem differentialSettingItem;
	private DifferentialSettingDownItem differentialSettingDownItem;
	//location Setting
	private LocationItem locationItem;
	private LocationDownItem locationDownItem;
	// Factory Reset
	private	 FactoryResetItem factoryResetItem ;
	private FactoryResetDownItem factoryResetDownItem;
	
	//接收数据相关
	/**
	 * 返回标志位（02开关设置03模式设置04分机设置05温标设置06设备工作模式设置07时间设置08温度相关09声音设置0A紧急制热0B用户编程热冷0C用户编程自动）
	 */
	private String mReturnId;
	/**
	 * 温度校正数据
	 */
	private String mTempratureFormat;
	/**
	 * swing设置数据
	 */
	private String mSwingSetting;
	/**
	 * diff设置数据
	 */
	private String mDiffSetting;
	/**
	 * third设置数据
	 */
	private String mThirdSetting;
	/**
	 * 声效数据
	 */
	private String mClickSound;
	/**
	 * 紧急制热数据
	 */
	private String mEmergencyHeat;
	/**
	 * 时间数据
	 */
	private String mTime;
	/**
	 * 震动数据
	 */
	private String mVibrate;
	/**
	 * 模式 1=heat 2=cool 3=auto
	 */
	private String mMode;
	/**
	 * 供热方式
	 */
	private String mTemperatureType;
	/**
	 * 系统类型(如 一级制冷)
	 */
	private String mSystemType;

	@ViewInject(R.id.thermost_setting_lv)
	private ListView settingListView;
	
	private List<AbstractSettingItem> listViewItems;
	private ThermostatSettingManagerAdapter settingAdapter;
	private ViewGroup container = null;
	//查询 当前状态  数据
	private static final String CURRENT_QUERY_MODE_CMD = "12";
	private static final String CURRENT_QUERY_DATA_CMD = "11";
	private static final String RESET_CMD = "600";
	private static final String RECIVE_FAILED_CMD = "8";
	private static final String RESET_TAG = "06";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("ThermostatSettingInfo");
		mGwId = bundle.getString(GWID);
		mDevId = bundle.getString(DEVID);
		mEp = bundle.getString(EP);
		mEpType = bundle.getString(EPTYPE);
		
		mMode = bundle.getString("mode");
		mTemperatureType = bundle.getString("tempType");
		mTempratureFormat = bundle.getString("tempUnit");
		cache=DeviceCache.getInstance(mActivity);
		settingAdapter = new ThermostatSettingManagerAdapter(mActivity);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle("Setting");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_thermostat82_setting, container, false);
		this.container = container;
		ViewUtils.inject(this,rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		settingListView.setAdapter(settingAdapter);

	}
	
	@Override
	public void onShow() {
		super.onShow();
		initBar();
		loadData();
	}

	@Override
	public void onResume() {
		super.onResume();
		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, CURRENT_QUERY_MODE_CMD);
		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, CURRENT_QUERY_DATA_CMD);
		initBar();
		loadData();
	}

	private void loadData(){
		mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				initSettingItems();
			}
		});
	}
	
	private void initSettingItems(){
		
		listViewItems = new ArrayList<AbstractSettingItem>();
		EmptyItem emptyItem0 = new EmptyItem(mActivity);
		emptyItem0.initSystemState();
		EmptyItem emptyItem1 = new EmptyItem(mActivity);
		emptyItem1.initSystemState();
		EmptyItem emptyItem2 = new EmptyItem(mActivity);
		emptyItem2.initSystemState();
		EmptyItem emptyItem3 = new EmptyItem(mActivity);
		emptyItem3.initSystemState();
		
		//click sound 	
		clickSoundItem = new ClickSoundItem(mActivity);
		clickSoundItem.initSystemState();
		clickSoundItem.setSoundData(mGwId, mDevId, mEp, mEpType);
		//vibrate
		vibrateItem = new VibrateItem(mActivity);
		vibrateItem.initSystemState();
		vibrateItem.setVibrateData(mGwId, mDevId, mEp, mEpType);
		//Temprature Format
		tempratureFormatItem = new TempratureFormatItem(mActivity);
		tempratureFormatItem.initSystemState();
		tempratureFormatItem.setFormatData(mGwId, mDevId, mEp, mEpType);
		tempratureFormatItem.setIsFormatC(mTempratureFormat);
		//Filter Remineder
		createFilterReminder();
		//Time Synchronization
		createTimeSync();
		// Emergency Heat
		if((StringUtil.equals(mMode, "01") || StringUtil.equals(mMode, "03"))&&
				(StringUtil.equals(mTemperatureType, "03") || StringUtil.equals(mTemperatureType, "04"))){
			
			createEmergencyHeat();
		}else{
			emergencyHeatItem = null;
		}
		// Equipment Setting
		createEquipmentSetting();
		// Defferential Setting
		createDifferentialSetting();
		// LocationItem
		createLocation();
		// Factory Reset
		createFactoryReset();
		// About
		AboutItem aboutItem = new AboutItem(mActivity);
		aboutItem.initSystemState();
		
		listViewItems.add(emptyItem0);
		listViewItems.add(clickSoundItem);
		listViewItems.add(vibrateItem);
		listViewItems.add(tempratureFormatItem);
		listViewItems.add(filterReminederItem);
		listViewItems.add(timeSyncItem);
		listViewItems.add(emptyItem1);
		if(emergencyHeatItem != null){
			
			listViewItems.add(emergencyHeatItem);
			listViewItems.add(emptyItem2);
		}
		listViewItems.add(equipmentSettingItem);
		listViewItems.add(differentialSettingItem);
		listViewItems.add(locationItem);
		listViewItems.add(factoryResetItem);
		listViewItems.add(emptyItem3);
		listViewItems.add(aboutItem);
		settingAdapter.swapData(listViewItems);
		
	}
	
	private void createFilterReminder(){
		
		filterReminederItem = new FilterReminderItem(mActivity);
		filterReminederItem.initSystemState();
		filterReminderDownItem = new FilterReminderDownItem(mActivity);
		filterReminderDownItem.initSystemState();
		filterReminederItem.setFliterDownViewListener(new ShowFliterDownViewListener() {
			
			@Override
			public void onViewOpenChangeed(boolean isOpened) {
				
				if(isOpened){
					if(listViewItems.indexOf(filterReminderDownItem) == -1){
						
						int position = listViewItems.indexOf(filterReminederItem);
						listViewItems.add(position+1, filterReminderDownItem);
					}else{
						listViewItems.remove(filterReminderDownItem);
					}
					
				}else{
					return;
				}
				settingAdapter.swapData(listViewItems);
			}
		});
		
		filterReminderDownItem.setGoButtonListener(new GoButtonListener() {
			
			@Override
			public void onGoBtnClick() {
				Intent intent = new Intent(mActivity, FilterReminderActivity.class);
				startActivityForResult(intent, 1000);
			}
		});
	}

	private void createLocation(){
		locationItem = new LocationItem(mActivity);
		locationItem.setLocationItem(mGwId, mDevId, mEp, mEpType);
		locationItem.initSystemState();
		locationDownItem = new LocationDownItem(mActivity);
		locationDownItem.initSystemState();
		locationDownItem.setLocationDownData(mGwId);
		locationItem.setLocationDownViewListener(new LocationItem.ShowLocationDownViewListener() {

			@Override
			public void onViewOpenChangeed(boolean isOpened) {

				if(isOpened){
					int position = listViewItems.indexOf(locationItem);
					listViewItems.add(position+1, locationDownItem);

				}else{
					listViewItems.remove(locationDownItem);
				}
				settingAdapter.swapData(listViewItems);
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode == mActivity.RESULT_OK){
			String result = data.getStringExtra("fliterResult");
			filterReminderDownItem.setContentData(result);
		}
	}
	
	private void createTimeSync(){
		timeSyncItem = new TimeSyncItem(mActivity);
		timeSyncItem.initSystemState();
		timeSyncDownItem = new TimeSyncDownItem(mActivity);
		timeSyncDownItem.initSystemState();
		timeSyncDownItem.setTimeSyncData(mGwId, mDevId, mEp, mEpType);
		timeSyncItem.setShowTimeDownViewListener(new ShowTimeDownViewListener() {
			
			@Override
			public void onViewOpenChangeed(boolean isOpened) {
				
				if(isOpened){
					int position = listViewItems.indexOf(timeSyncItem);
					listViewItems.add(position+1, timeSyncDownItem);
					
				}else{
					listViewItems.remove(timeSyncDownItem);
				}
				settingAdapter.swapData(listViewItems);
			}
		});
	}
	
	private void createEmergencyHeat(){
		emergencyHeatItem = new EmergencyHeatItem(mActivity);
		emergencyHeatItem.initSystemState();
		emergencyHeatItem.setEmergencyHeatData(mGwId, mDevId, mEp, mEpType);
		emergencyHeatDownItem = new EmergencyHeatDownItem(mActivity);
		emergencyHeatDownItem.initSystemState();
		emergencyHeatItem.setEmergencyDownViewListener(new ShowEmergencyDownViewListener() {
			
			@Override
			public void onViewOpenChangeed(boolean isOpened) {
				
				if(isOpened){
					if(listViewItems.indexOf(emergencyHeatDownItem) != -1){
						return;
					}
					int position = listViewItems.indexOf(emergencyHeatItem);
					listViewItems.add(position+1, emergencyHeatDownItem);
					
				}else{
					listViewItems.remove(emergencyHeatDownItem);
				}
				settingAdapter.swapData(listViewItems);
			}
		});
	}
	
	private void createEquipmentSetting(){
		equipmentSettingItem = new EquipmentSettingItem(mActivity);
		equipmentSettingItem.initSystemState();
		equipmentSettingDownItem = new EquipmentSettingDownItem(mActivity);
		equipmentSettingDownItem.initSystemState();
		equipmentSettingDownItem.setEquipmentSettingData(mGwId, mDevId, mEp, mEpType);
		equipmentSettingItem.setEquipmentDownViewListener(new ShowEquipmentDownViewListener() {
			
			@Override
			public void onViewOpenChangeed(boolean isOpened) {
				
				if(isOpened){
					int position = listViewItems.indexOf(equipmentSettingItem);
					listViewItems.add(position+1, equipmentSettingDownItem);
					
				}else{
					listViewItems.remove(equipmentSettingDownItem);
				}
				settingAdapter.swapData(listViewItems);
			}
		});
	}
	
	private void createDifferentialSetting(){
		differentialSettingItem = new DifferentialSettingItem(mActivity);
		differentialSettingItem.initSystemState();
		differentialSettingDownItem = new DifferentialSettingDownItem(mActivity);
		differentialSettingDownItem.initSystemState();
		differentialSettingDownItem.setDefferentialSettingData(mGwId, mDevId, mEp, mEpType);
		differentialSettingDownItem.setmTempratureFormat(mTempratureFormat);
		differentialSettingItem.setDifferentialDownViewListener(new ShowDifferentialDownViewListener() {
			
			@Override
			public void onViewOpenChangeed(boolean isOpened) {
				
				if(isOpened){
					int position = listViewItems.indexOf(differentialSettingItem);
					listViewItems.add(position+1, differentialSettingDownItem);
					
				}else{
					listViewItems.remove(differentialSettingDownItem);
				}
				settingAdapter.swapData(listViewItems);
			}
		});
	}
	
	private void createFactoryReset(){
		factoryResetItem = new FactoryResetItem(mActivity);
		factoryResetItem.initSystemState();
		factoryResetDownItem = new FactoryResetDownItem(mActivity);
		factoryResetDownItem.initSystemState();
		factoryResetDownItem.setResetData(mGwId, mDevId, mEp, mEpType);
		factoryResetItem.setFactoryResetDownViewListener(new ShowFactoryResetDownViewListener() {
			
			@Override
			public void onViewOpenChangeed(boolean isOpened) {
				
				if(isOpened){
					int position = listViewItems.indexOf(factoryResetItem);
					listViewItems.add(position+1, factoryResetDownItem);
					
				}else{
					listViewItems.remove(factoryResetDownItem);
				}
				settingAdapter.swapData(listViewItems);
			}
		});
		
		factoryResetDownItem.setResetBtnListener(new ResetBtnListener() {
			
			@Override
			public void onResetClick() {
				SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, RESET_CMD);
				showResetDialog();
			}
		});
	}
	
	private void showResetDialog(){
		dialogManager.showResetDialog(mActivity);
		new Handler().postDelayed(new Runnable() {
		    @Override
		    public void run() {
		    	showResultFailedDialog();
		    }
		},20000);
	}
	
	private void showResetResultDialog(){
		if(StringUtil.equals(mEpData.substring(0, 1), RECIVE_FAILED_CMD)){
			showResultFailedDialog();
		}

		if(StringUtil.equals(mReturnId, RESET_TAG)){
			showResultSuccessDialog();
		}
	}
	
	private void showResultFailedDialog(){
		dialogManager.showFailedDialog(mActivity);
		new Handler().postDelayed(new Runnable() {
		    @Override
		    public void run() {
		    	dialogManager.diamissFialedDialog();
		    }
		},1000);
	}
	
	private void showResultSuccessDialog(){
		dialogManager.showSuccessDialog(mActivity);
		new Handler().postDelayed(new Runnable() {
		    @Override
		    public void run() {
		    	dialogManager.dismissSuccessDialog();
				getActivity().finish();
		    }
		},1000);
	}
	
	public void onEventMainThread(DeviceEvent event){
		device=(AbstractDevice) cache.getDeviceByID(mActivity, event.deviceInfo.getGwID(), event.deviceInfo.getDevID());
		mEpData=device.getDeviceInfo().getDevEPInfo().getEpData();
		handleEpData(mEpData);
		showResetResultDialog();
		clickSoundItem.setSoundOpen(mClickSound);
		vibrateItem.setVibrateOpen(mVibrate);
		tempratureFormatItem.setIsFormatC(mTempratureFormat);
		differentialSettingDownItem.setmTempratureFormat(mTempratureFormat);
		timeSyncDownItem.setDeviceTime(mTime);
		if(emergencyHeatItem !=null){
			emergencyHeatItem.setHeatOpen(mEmergencyHeat);
		}
		equipmentSettingDownItem.setEquipmentType(mTemperatureType,mSystemType);
	}

	private void handleEpData(String epData) {
		
		if(!StringUtil.isNullOrEmpty(epData)){
			Log.i(TAG+"-epdata", mEpData+"-"+mEpData.length());
			if(epData.length() == 28){
				mReturnId = epData.substring(0, 2);
				mTempratureFormat = epData.substring(2, 4);
				mSwingSetting = epData.substring(4, 6);
				mDiffSetting = epData.substring(6, 8);
				mThirdSetting = epData.substring(8, 10);
				mClickSound = epData.substring(10, 12);
				mEmergencyHeat = epData.substring(12, 14);
				mTime = epData.substring(14, 26);
				mVibrate = epData.substring(26, 28);

			}
			
			if(epData.length() == 38){
				mReturnId = epData.substring(0, 2);
				mTempratureFormat = epData.substring(6, 8);
				mTemperatureType = epData.substring(8, 10);
				mSystemType = epData.substring(10, 12);
				mMode = epData.substring(12, 14);
			}
		}
	}
	
	
}

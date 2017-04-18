package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.newthermostat.ThermostatDialogManager;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.ThermostatSettingFragment;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.ThermostatSettingManagerAdapter;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment.FuelDownItem.FuelDataChangedListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment.FuelItem.ShowFuelDownViewListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment.HVACTypeDownItem.HVACTypeDataChangedListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment.HVACTypeDownItem.HVACTypeTextChangedListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment.HVACTypeItem.ShowHVACTypeDownViewListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment.SendEquipmentItem.SendClickListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment.SystemTypeDownItem.SystemTypeDataChangedListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment.SystemTypeItem.ShowSystemTypeDownViewListener;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.fragment.setting.EmptyItem;
import cc.wulian.smarthomev5.tools.SendMessage;

public class EquipmentSettingFragment extends WulianFragment{
	
	private final String TAG = getClass().getSimpleName();
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private DeviceCache cache;
	private AbstractDevice device;
	private ThermostatDialogManager dialogManager = ThermostatDialogManager.getDialogManager();
	private String mEpData;
	private String sendCmd;
	
	@ViewInject(R.id.thermost_setting_lv)
	private ListView settingListView;
	private ThermostatSettingManagerAdapter settingAdapter;
	private List<AbstractSettingItem> listViewItems;
	private ViewGroup container = null;
	
	private SystemTypeItem systemTypeItem;
	private SystemTypeDownItem systemTypeDownItem;
	
	private FuelItem fuelItem;
	private FuelDownItem fuelDownItem;
	
	private HVACTypeItem hvacTypeItem;
	private HVACTypeDownItem hvacTypeDownItem;

	private SendEquipmentItem sendEquipmentItem; 
	
	private WLDialog sendDialog;
	private WLDialog niticeDialog;
	private String mReturnId;
	/**
	 * 工作模式 ：设备选择+供热方式
	 */
	private String mSystemMode;
	/**
	 * 系统类型(如 一级制冷)
	 */
	private String mSystemType;

	public static final String SYSTEM_MODE_00 ="0";
	public static final String SYSTEM_MODE_01 ="1";
	public static final String SYSTEM_MODE_02 ="2";
	public static final String SYSTEM_MODE_03 ="3";
	public static final String SYSTEM_MODE_04 ="4";
	
	public static final String SYSTEM_TYPE_01 ="1";
	public static final String SYSTEM_TYPE_02 ="2";
	public static final String SYSTEM_TYPE_03 ="3";
	public static final String SYSTEM_TYPE_04 ="4";
	public static final String SYSTEM_TYPE_05 ="5";
	public static final String SYSTEM_TYPE_06 ="6";
	
	private static final String EQUIPMENT_SEND_CMD_TAG = "6";
	//查询 当前数据
	private static final String CURRENT_QUERY_DATA_CMD = "11";
	
	private static final String RECIVE_FAILED_CMD = "8";
	private static final String RECIVE_SUCCESS_ID = "06";
	
	private static final String RESET = "Reset";
	private static final String SETTING = "Setting";
	private static final String EQUIPMENT_NOTICE = "There are options not set,please check!";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("EquipmentSettingInfo");
		mGwId = bundle.getString(ThermostatSettingFragment.GWID);
		mDevId = bundle.getString(ThermostatSettingFragment.DEVID);
		mEp = bundle.getString(ThermostatSettingFragment.EP);
		mEpType = bundle.getString(ThermostatSettingFragment.EPTYPE);
		mSystemMode = bundle.getString("systemMode");
		mSystemType = bundle.getString("systemType");
		cache=DeviceCache.getInstance(mActivity);
		settingAdapter = new ThermostatSettingManagerAdapter(mActivity);
	}
	
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle(SETTING);
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
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}


	private void showSendDialog(){
		WLDialog.Builder builder = new Builder(mActivity);

		builder.setContentView(R.layout.device_thermostat82_setting_euipment_send_dialog)
				.setPositiveButton("Ok")
				.setNegativeButton("Cancel")
				.setDismissAfterDone(true).setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, EQUIPMENT_SEND_CMD_TAG+sendCmd);
						showSetLoadDialog();
					}

					public void onClickNegative(View contentViewLayout) {

					}

				});
		sendDialog = builder.create();
		TextView dialogTextView1 = (TextView) sendDialog.findViewById(R.id.thermost_setting_equipment_dialog_tv2);
		TextView dialogTextView2 = (TextView) sendDialog.findViewById(R.id.thermost_setting_equipment_dialog_tv4);
		TextView dialogTextView3= (TextView) sendDialog.findViewById(R.id.thermost_setting_equipment_dialog_tv6);
		dialogTextView1.setText(systemTypeItem.getmSystemTypeText());
		dialogTextView2.setText(fuelItem.getmFuelText());
		dialogTextView3.setText(hvacTypeItem.getHVACTypeText());
		sendDialog.show();
	}
	
	private void showNoticeDialog(){
		WLDialog.Builder builder = new Builder(mActivity);

		builder.setTitle(null)
				.setSubTitleText(null)
				.setMessage(EQUIPMENT_NOTICE)
				.setNegativeButton(RESET)
				.setDismissAfterDone(true).setListener(new MessageListener() {
					
					@Override
					public void onClickPositive(View contentViewLayout) {
						
					}
					
					@Override
					public void onClickNegative(View contentViewLayout) {
						
					}
				});
				
		niticeDialog = builder.create();
		niticeDialog.show();
	}
	
	private void getSendCmd(){
		String systemTypeData = systemTypeItem.getmSystemTypeData();
		String fuelTypeData = fuelItem.getmFuelData();
		
		String sendModeCmd;
		String sendTypeCmd;
		
		if(StringUtil.equals(systemTypeData, SystemTypeDownItem.SYSTEM_TYPE_DATA_01)){
			if(StringUtil.equals(fuelTypeData, FuelDownItem.FUEL_DATA_01)){
				sendModeCmd = SYSTEM_MODE_01;
			}else{
				sendModeCmd = SYSTEM_MODE_02;
			}
		}else{
			if(StringUtil.equals(fuelTypeData, FuelDownItem.FUEL_DATA_01)){
				sendModeCmd = SYSTEM_MODE_03;
			}else{
				sendModeCmd = SYSTEM_MODE_04;
			}
		}
		
		sendTypeCmd = hvacTypeItem.getHVACTypeData();
		sendCmd = sendModeCmd + sendTypeCmd;
		Log.i(TAG+"sendCmd", sendCmd);
	}
	
	private void showSetLoadDialog(){
		dialogManager.showResetDialog(mActivity);
		
		new Handler().postDelayed(new Runnable() {
		    @Override
		    public void run() {
		    	showResultFailedDialog();
		    }
		},20000);
	}
	
	private void showSetResultDialog(){
		if(StringUtil.equals(mEpData.substring(0, 1), RECIVE_FAILED_CMD)){
			showResultFailedDialog();
		}

		if(StringUtil.equals(mReturnId, RECIVE_SUCCESS_ID)){
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
	
//	@Override
//	public void onShow() {
//		super.onShow();
//		initBar();
//		loadData();
//	}
	
	@Override
	public void onResume() {
		super.onResume();
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
		EmptyItem emptyItem1 = new EmptyItem(mActivity);
		emptyItem1.initSystemState();
		EmptyItem emptyItem2 = new EmptyItem(mActivity);
		emptyItem2.initSystemState();
		
		// SystemTypeItem
		createSystemType();
		// FuelItem
		createFuel();
		// HVACTypeItem
		createHVACType();
		sendEquipmentItem = new SendEquipmentItem(mActivity);
		sendEquipmentItem.initSystemState();
		sendEquipmentItem.setSendClickListener(new SendClickListener() {
			
			@Override
			public void onSendClick() {
				
				if(StringUtil.isNullOrEmpty(systemTypeItem.getmSystemTypeText()) || 
						StringUtil.isNullOrEmpty(fuelItem.getmFuelText()) || 
						StringUtil.isNullOrEmpty(hvacTypeItem.getHVACTypeText()) ){
					showNoticeDialog();
				}else{
					getSendCmd();
					showSendDialog();
				}
				
			}
		});
		
		listViewItems.add(emptyItem1);
		listViewItems.add(systemTypeItem);
		listViewItems.add(fuelItem);
		listViewItems.add(hvacTypeItem);
		listViewItems.add(emptyItem2);
		listViewItems.add(sendEquipmentItem);
		
		settingAdapter.swapData(listViewItems);
		
	}
	
	private void createSystemType(){
		systemTypeItem = new SystemTypeItem(mActivity);
		systemTypeItem.initSystemState();
//		systemTypeItem.setmSystemTypeData(mSystemMode,null);
		systemTypeDownItem = new SystemTypeDownItem(mActivity);
		systemTypeDownItem.initSystemState();
		systemTypeItem.setSystemTypeDownViewListener(new ShowSystemTypeDownViewListener() {
			
			@Override
			public void onViewOpenChangeed(boolean isOpened) {
				
				if(isOpened){
					int position = listViewItems.indexOf(systemTypeItem);
					listViewItems.add(position+1, systemTypeDownItem);
					
				}else{
					listViewItems.remove(systemTypeDownItem);
				}
				settingAdapter.swapData(listViewItems);
			}
		});
		
		systemTypeDownItem.setSystemTypeDataChangedListener(new SystemTypeDataChangedListener() {
			
			@Override
			public void onDataChanged(String data) {
				
				systemTypeItem.setmSystemTypeData(null,data);
				hvacTypeDownItem.setSystemType(data);
				hvacTypeItem.setHVACTypeText("");
				if(listViewItems.indexOf(systemTypeDownItem) != -1){
					listViewItems.remove(systemTypeDownItem);
					settingAdapter.swapData(listViewItems);
					systemTypeItem.setIsSystemTypeOpen(false);
				}
			}
		});
	}
	
	private void createFuel(){
		fuelItem = new FuelItem(mActivity);
		fuelItem.initSystemState();
//		fuelItem.setmFuelData(mSystemMode,null);
		fuelDownItem = new FuelDownItem(mActivity);
		fuelDownItem.initSystemState();
		fuelItem.setFuelDownViewListener(new ShowFuelDownViewListener() {
			
			@Override
			public void onViewOpenChangeed(boolean isOpened) {
				
				if(isOpened){
					int position = listViewItems.indexOf(fuelItem);
					listViewItems.add(position+1, fuelDownItem);
					
				}else{
					listViewItems.remove(fuelDownItem);
				}
				settingAdapter.swapData(listViewItems);
				
			}
		});
		fuelDownItem.setFuelDataChangedListener(new FuelDataChangedListener() {
			
			@Override
			public void onDataChanged(String data) {
				
				fuelItem.setmFuelData(null,data);
				hvacTypeDownItem.setFuel(data);
				hvacTypeItem.setHVACTypeText("");
				if(listViewItems.indexOf(fuelDownItem) != -1){
					listViewItems.remove(fuelDownItem);
					settingAdapter.swapData(listViewItems);
					fuelItem.setIsFuelOpen(false);
					
				}
			}
		});
		
	}
	
	private void createHVACType(){
		hvacTypeItem = new HVACTypeItem(mActivity);
		hvacTypeItem.initSystemState();
//		hvacTypeItem.setSystemData(mSystemMode);
//		hvacTypeItem.setHVACTypeData(mSystemType,null);
		hvacTypeDownItem = new HVACTypeDownItem(mActivity);
		hvacTypeDownItem.initSystemState();
		hvacTypeItem.setHvacTypeDownViewListener(new ShowHVACTypeDownViewListener() {
			
			@Override
			public void onViewOpenChangeed(boolean isOpened) {
				
				if(isOpened){
					int position = listViewItems.indexOf(hvacTypeItem);
					listViewItems.add(position+1, hvacTypeDownItem);
					
				}else{
					listViewItems.remove(hvacTypeDownItem);
				}
				settingAdapter.swapData(listViewItems);
			}
		});
		
		hvacTypeDownItem.setHvacTypeDataChangedListener(new HVACTypeDataChangedListener() {
			
			@Override
			public void onDataChanged(String data) {
				
				hvacTypeItem.setHVACTypeData(null,data);
				if(listViewItems.indexOf(hvacTypeDownItem) != -1){
					listViewItems.remove(hvacTypeDownItem);
					settingAdapter.swapData(listViewItems);
					hvacTypeItem.setIsHVACTypeOpen(false);
				}
			}
		});
		
		hvacTypeDownItem.setHvacTypeTextChangedListener(new HVACTypeTextChangedListener() {
			
			@Override
			public void onTextChanged(String data) {
				hvacTypeItem.setHVACTypeText(data);
			}
		});
	}

	
	public void onEventMainThread(DeviceEvent event){
		device=(AbstractDevice) cache.getDeviceByID(mActivity, event.deviceInfo.getGwID(), event.deviceInfo.getDevID());
		mEpData=device.getDeviceInfo().getDevEPInfo().getEpData();
		mGwId = event.deviceInfo.getGwID();
		mDevId = event.deviceInfo.getDevID();
		mEp = device.getDeviceInfo().getDevEPInfo().getEp();
		mEpType = device.getDeviceInfo().getDevEPInfo().getEpType();
		
		handleEpData(mEpData);
		//第一次 ToSet时  不显示Equipment设置类型
		if(!StringUtil.equals(mSystemMode,"00") && !StringUtil.equals(mSystemType,"00")){
			systemTypeItem.setmSystemTypeData(mSystemMode,null);
			fuelItem.setmFuelData(mSystemMode,null);
			hvacTypeItem.setSystemData(mSystemMode);
			hvacTypeItem.setHVACTypeData(mSystemType,null);
			hvacTypeDownItem.setmHVACTypeDownData(mSystemMode);
		}
		showSetResultDialog();
	}

	private void handleEpData(String epData) {
		if(!StringUtil.isNullOrEmpty(epData)){
			Log.i(TAG+"-epdata", epData+"-"+epData.length());
			if(epData.length() == 38){
				mReturnId = epData.substring(0, 2);
				mSystemMode = epData.substring(8, 10);
				Log.i(TAG+"mSystemMode", mSystemMode);
				mSystemType = epData.substring(10, 12);
				Log.i(TAG+"mSystemType", mSystemType);
			}
		}
	}
	

}

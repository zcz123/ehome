package cc.wulian.app.model.device.impls.controlable.floorwarm.setting;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.fragment.setting.EmptyItem;
import cc.wulian.smarthomev5.fragment.setting.flower.items.CustomPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SendMessage;

public class FloorWarmSettingFragment extends WulianFragment {

	private final String TAG = getClass().getSimpleName();

	public static final SimpleDateFormat TIME_FPRMAT = new SimpleDateFormat("yyyyMMddHHmmss");
	
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	
	private String mEpData;
	private DeviceCache cache;
	private AbstractDevice device;
	
	/**
	 * 返回标志位
	 */
	private String mReturnId;
	/**
	 * 过温保护开关 off:00  on:01
	 */
	private String mOverTempState;
	/**
	 * 过温保护温度
	 */
	private String mOverTempValue;
	/**
	 * 防冻保护开关 off:00  on:01
	 */
	private String mForstProtectState;
	/**
	 * 防冻保护温度
	 */
	private String mForstProtectTemp;
	/**
	 * 温度单位：00摄氏度，01华氏度
	 */
	private String mTemperatureUnit;
	/**
	 * 一键节能开关  off:00  on:01
	 */
	private String mEnergySavingState;
	/**
	 * 一键节能温度
	 */
	private String mEnergySavingTemp;
	/**
	 *  声效数据     off:00  on:01
	 */
	private String mClickSound;
	/**
	 *  震动数据     off:00  on:01
	 */
	private String mClickVibrate;
	/**
	 *  时间数据
	 */
	private String mSyncTime;
	/**
	 * 回差温度
	 */
	private String mDiffTemp;
	/**
	 * 系统选择    水地暖 :00     电地暖:01
	 */
	private String mSystemType;
	//按键声音 
	private ClickSoundItem clickSoundItem;
	//按键震动
	private ClickVibrateItem vibrateItem;
	//时间同步
	private TimeSyncItem timeSyncItem;
	//回差设置
	private DiffSettingItem diffSettingItem;
	//编程模式
	private ProgramSettingItem programSettingItem;
	//节能模式
	private EnergySavingItem energySavingItem;
	//系统选择
	private SystemTypeItem systemTypeItem;
	//地面过温保护
	private OverTempProtectItem overTempProtectItem;
	//防冻保护
	private ForstProtectItem forstProtectItem;
	//恢复出厂设置
	private FactoryResetItem factoryResetItem;
	
	@ViewInject(R.id.floorwarm_setting_lv)
	private ListView settingListView;
	
	private List<AbstractSettingItem> listViewItems;
	private FloorWarmSettingManagerAdapter settingAdapter;
	private View rootView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("FloorWarmSettingFragmentInfo");
		mGwId = bundle.getString(FloorWarmUtil.GWID);
		mDevId = bundle.getString(FloorWarmUtil.DEVID);
		mEp = bundle.getString(FloorWarmUtil.EP);
		mEpType = bundle.getString(FloorWarmUtil.EPTYPE);
		
		cache= DeviceCache.getInstance(mActivity);
		settingAdapter = new FloorWarmSettingManagerAdapter(mActivity);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle(mApplication.getResources().getString(R.string.set_titel));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.device_floorwarm_setting, container, false);
		ViewUtils.inject(this,rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		settingListView.setAdapter(settingAdapter);
		//post()可以延迟到所有生命周期方法执行完后，在显示popupWindow
		rootView.post(new Runnable() {
			@Override
			public void run() {
				showWarningPopWindow();
			}
		});
	}

	//第一次进入设置的时候 显示提示引导页
	private void showWarningPopWindow(){
		boolean isWarningShow = Preference.getPreferences().getBoolean("isFloorWarmSettingWarningShow",false);
		if(isWarningShow){
			return;
		}
		final CustomPopupWindow popupWindow=new CustomPopupWindow(getActivity(), R.layout.device_floorwarm_setting_warning);
		popupWindow.initEvent(new CustomPopupWindow.PopEvent() {
			@Override
			public void initWidget(View view) {
				Button clickBtn = (Button) view.findViewById(R.id.floorwarm_setting_warning_btn);
				clickBtn.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						popupWindow.dismiss();
					}
				});
			}
		});
		popupWindow.setBackgroundDrawable(null); //屏蔽系统返回键
		popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
		Preference.getPreferences().putBoolean("isFloorWarmSettingWarningShow",true);
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
		initBar();
		loadData();
		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FloorWarmUtil.CURRENT_QUERY_CMD_DATA);
		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FloorWarmUtil.CURRENT_QUERY_CMD_MODE);
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
		EmptyItem emptyItem3 = new EmptyItem(mActivity);
		emptyItem3.initSystemState();

		//系统选择
		systemTypeItem = new SystemTypeItem(mActivity);
		systemTypeItem.initSystemState();
		systemTypeItem.setSystemTypeData(mGwId, mDevId, mEp, mEpType);
		//按键声音	
		clickSoundItem = new ClickSoundItem(mActivity);
		clickSoundItem.initSystemState();
		clickSoundItem.setSoundData(mGwId, mDevId, mEp, mEpType);
		//按键震动
		vibrateItem = new ClickVibrateItem(mActivity);
		vibrateItem.initSystemState();
		vibrateItem.setVibrateData(mGwId, mDevId, mEp, mEpType);
		//时间同步
		timeSyncItem = new TimeSyncItem(mActivity);
		timeSyncItem.initSystemState();
		timeSyncItem.setTimeSyncData(mGwId, mDevId, mEp, mEpType);
		//回差设置
		diffSettingItem = new DiffSettingItem(mActivity);
		diffSettingItem.initSystemState();
		diffSettingItem.setDiffSettingData(mGwId, mDevId, mEp, mEpType);
		//编程模式
		programSettingItem = new ProgramSettingItem(mActivity);
		programSettingItem.initSystemState();
		programSettingItem.setProgramData(mGwId, mDevId, mEp, mEpType);
		//节能模式
		energySavingItem = new EnergySavingItem(mActivity);
		energySavingItem.initSystemState();
		energySavingItem.setEnergySavingData(mGwId, mDevId, mEp, mEpType);
		//地面过温保护
		overTempProtectItem = new OverTempProtectItem(mActivity);
		overTempProtectItem.initSystemState();
		overTempProtectItem.setOverTempData(mGwId, mDevId, mEp, mEpType);
		//防冻保护
		forstProtectItem = new ForstProtectItem(mActivity);
		forstProtectItem.initSystemState();
		forstProtectItem.setForstProtectData(mGwId, mDevId, mEp, mEpType);
		//恢复出厂设置
		factoryResetItem = new FactoryResetItem(mActivity);
		factoryResetItem.initSystemState();
		factoryResetItem.setFactoryResetData(mGwId, mDevId, mEp, mEpType);
		
		listViewItems.add(emptyItem1);
		listViewItems.add(systemTypeItem);
		listViewItems.add(clickSoundItem);
		listViewItems.add(vibrateItem);
		listViewItems.add(timeSyncItem);
		listViewItems.add(diffSettingItem);
		listViewItems.add(emptyItem2);
		listViewItems.add(programSettingItem);
		listViewItems.add(energySavingItem);
		listViewItems.add(overTempProtectItem);
//		if(!StringUtil.isNullOrEmpty(mSystemType) && StringUtil.equals(mSystemType,FloorWarmUtil.SYSTEM_TYPE_WATER_TAG)){
//			listViewItems.add(forstProtectItem);
//		}
		listViewItems.add(emptyItem3);
		listViewItems.add(factoryResetItem);
		settingAdapter.swapData(listViewItems);
		
	}

	private void refreshForstProtectItem(){
		if(!StringUtil.isNullOrEmpty(mSystemType)){
			if(StringUtil.equals(mSystemType, FloorWarmUtil.SYSTEM_TYPE_WATER_TAG)
					&&(listViewItems.indexOf(forstProtectItem) == -1)){
				int position = listViewItems.indexOf(overTempProtectItem);
				listViewItems.add(position+1,forstProtectItem);
			}
			else if(StringUtil.equals(mSystemType, FloorWarmUtil.SYSTEM_TYPE_ELECT_TAG)
					&&(listViewItems.indexOf(forstProtectItem) != -1)){
				listViewItems.remove(forstProtectItem);
			}
			settingAdapter.swapData(listViewItems);
		}
	}

	
	public void onEventMainThread(DeviceEvent event){
		device=(AbstractDevice) cache.getDeviceByID(mActivity, event.deviceInfo.getGwID(), event.deviceInfo.getDevID());
		mEpData=device.getDeviceInfo().getDevEPInfo().getEpData();
		Log.i(TAG+"-epdata", mEpData+"-"+mEpData.length());
		
		handleEpData(mEpData);
		refreshForstProtectItem();
		systemTypeItem.setSystemType(mSystemType);
		clickSoundItem.setSoundType(mClickSound);
		vibrateItem.setVibrateType(mClickVibrate);
		timeSyncItem.showSyncResult(mReturnId);
		diffSettingItem.setDiffSettingTemp(mDiffTemp);
		energySavingItem.setEnergySavingTemp(mEnergySavingState, mEnergySavingTemp);
		overTempProtectItem.setOverTempProtect(mOverTempState,mOverTempValue);
		forstProtectItem.setForstProtect(mForstProtectState,mForstProtectTemp);
		factoryResetItem.showResetResult(mEpData);
	}

	private void handleEpData(String epData) {
		
		if(!StringUtil.isNullOrEmpty(epData)){
			if(epData.length() == 46){
				mReturnId = epData.substring(0, 2);
				mOverTempState = mEpData.substring(4, 6);
				mOverTempValue = epData.substring(6, 10);
				mForstProtectState = mEpData.substring(10, 12);
				mForstProtectTemp = epData.substring(12, 16);
				mTemperatureUnit = epData.substring(16, 18);
				mEnergySavingState = epData.substring(36, 38);
				mEnergySavingTemp = epData.substring(38, 42);
			}
			if(epData.length() == 28){
				mReturnId = epData.substring(0, 2);
				mClickSound = epData.substring(2, 4);
				mSyncTime = epData.substring(4, 16);
				mClickVibrate = epData.substring(16, 18);
				mDiffTemp = epData.substring(18, 20);
				mSystemType = epData.substring(26, 28);
			}
			if(epData.length() >= 2){
				mReturnId = epData.substring(0, 2);
				if(StringUtil.equals(epData.substring(0,2), FloorWarmUtil.SYSTEM_TYPE_TAG)){
					mSystemType = epData.substring(2, 4);
				}
				if(StringUtil.equals(epData.substring(0,2), FloorWarmUtil.SOUND_TYPE_TAG)){
					mClickSound = epData.substring(2, 4);
				}
				if(StringUtil.equals(epData.substring(0,2), FloorWarmUtil.VIBRATE_TYPE_TAG)){
					mClickVibrate = epData.substring(2, 4);
				}
				if(StringUtil.equals(epData.substring(0,2), FloorWarmUtil.TIME_SYNC_TAG)){
					mSyncTime = epData.substring(2, 14);
				}
				if(StringUtil.equals(epData.substring(0,2), FloorWarmUtil.DIFF_SETTING_TAG)){
					mDiffTemp = epData.substring(2, 4);
				}
				if(StringUtil.equals(epData.substring(0,2), FloorWarmUtil.ENERGY_STATE_TAG)){
					mEnergySavingState = epData.substring(2, 4);
					mEnergySavingTemp = epData.substring(4, 8);
				}
				if(StringUtil.equals(epData.substring(0,2), FloorWarmUtil.OVER_TEMP_TAG)){
					if(StringUtil.equals(epData.substring(2,4), FloorWarmUtil.STATE_OFF)){
						mOverTempState = epData.substring(4 , 6);
					}
					if(StringUtil.equals(epData.substring(2,4), FloorWarmUtil.STATE_ON)){
						mOverTempValue = epData.substring(6 , 10);
					}
				}
				if(StringUtil.equals(epData.substring(0,2), FloorWarmUtil.FORST_PROTECT_TAG)){
					if(StringUtil.equals(epData.substring(2,4), FloorWarmUtil.STATE_OFF)){
						mForstProtectState = epData.substring(4 , 6);
					}
					if(StringUtil.equals(epData.substring(2,4), FloorWarmUtil.STATE_ON)){
						mForstProtectTemp = epData.substring(6 , 10);
					}
				}
			}

		}
	}
	
	
}

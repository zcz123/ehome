package cc.wulian.app.model.device.impls.controlable.fancoil.setting;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilUtil;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.fragment.setting.EmptyItem;
import cc.wulian.smarthomev5.tools.SendMessage;

public class FanCoilSettingFragment extends WulianFragment{

	private final String TAG = getClass().getSimpleName();
	
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
	 *  时间数据
	 */
	private String mSyncTime;
	/**
	 *  声效数据     off:00  on:01
	 */
	private String mClickSound;
	/**
	 *  震动数据     off:00  on:01
	 */
	private String mClickVibrate;
	/**
	 * 回差温度
	 */
	private String mDiffTemp;
	
	//按键声音 
	private ClickSoundItem clickSoundItem; 
	//按键震动
	private ClickVibrateItem vibrateItem;
	//过滤器提醒
	private FilterReminderItem filterReminderItem;
	//时间同步
	private TimeSyncItem timeSyncItem;
	//回差设置
	private DiffSettingItem diffSettingItem;
	//编程模式
	private ProgramSettingItem programSettingItem; 
	//节能模式
	private EnergySavingItem energySavingItem;
	//恢复出厂设置
	private FactoryResetItem factoryResetItem;

	@ViewInject(R.id.fancoil_setting_lv)
	private ListView settingListView;
	
	private List<AbstractSettingItem> listViewItems;
	private FanCoilSettingManagerAdapter settingAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("FanCoilSettingFragmentInfo");
		mGwId = bundle.getString(FanCoilUtil.GWID);
		mDevId = bundle.getString(FanCoilUtil.DEVID);
		mEp = bundle.getString(FanCoilUtil.EP);
		mEpType = bundle.getString(FanCoilUtil.EPTYPE);
		
		cache=DeviceCache.getInstance(mActivity);
		settingAdapter = new FanCoilSettingManagerAdapter(mActivity);
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
		View rootView = inflater.inflate(R.layout.device_fancoil_setting, container, false);
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
		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FanCoilUtil.CURRENT_QUERY_CMD_MODE);
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
		EmptyItem emptyItem3 = new EmptyItem(mActivity);
		emptyItem3.initSystemState();
		
		
		//按键声音	
		clickSoundItem = new ClickSoundItem(mActivity);
		clickSoundItem.initSystemState();
		clickSoundItem.setSoundData(mGwId, mDevId, mEp, mEpType);
		//按键震动
		vibrateItem = new ClickVibrateItem(mActivity);
		vibrateItem.initSystemState();
		vibrateItem.setVibrateData(mGwId, mDevId, mEp, mEpType);
		//过滤器提醒
		filterReminderItem = new FilterReminderItem(mActivity);
		filterReminderItem.initSystemState();
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
		//恢复出厂设置
		factoryResetItem = new FactoryResetItem(mActivity);
		factoryResetItem.initSystemState();
		factoryResetItem.setFactoryResetData(mGwId, mDevId, mEp, mEpType);
		
		listViewItems.add(emptyItem1);
		listViewItems.add(clickSoundItem);
		listViewItems.add(vibrateItem);
		listViewItems.add(filterReminderItem);
		listViewItems.add(timeSyncItem);
		listViewItems.add(diffSettingItem);
		listViewItems.add(emptyItem2);
		listViewItems.add(programSettingItem);
		listViewItems.add(energySavingItem);
		listViewItems.add(emptyItem3);
		listViewItems.add(factoryResetItem);
		settingAdapter.swapData(listViewItems);
		
	}

	public void onEventMainThread(DeviceEvent event){
		device=(AbstractDevice) cache.getDeviceByID(mActivity, event.deviceInfo.getGwID(), event.deviceInfo.getDevID());
		mEpData=device.getDeviceInfo().getDevEPInfo().getEpData();
		Log.i(TAG+"-epdata", mEpData+"-"+mEpData.length());
		
		handleEpData(mEpData);
		clickSoundItem.setSoundType(mClickSound);
		vibrateItem.setVibrateType(mClickVibrate);
		timeSyncItem.showSyncResult(mReturnId);
		diffSettingItem.setDiffSettingTemp(mDiffTemp);
		factoryResetItem.showResetResult(mEpData);
	}

	private void handleEpData(String epData) {
		
		if(!StringUtil.isNullOrEmpty(epData)){
			if(epData.length() == 40){
				mReturnId = epData.substring(0, 2);
			}
			if(epData.length() == 22){
				mReturnId = epData.substring(0, 2);
				mClickSound = epData.substring(2, 4);
				mClickVibrate = epData.substring(16, 18);
				mDiffTemp = epData.substring(18, 20);
			}
			if(StringUtil.equals(epData.substring(0,2),FanCoilUtil.SOUND_STATE_TAG)){
				mReturnId = epData.substring(0, 2);
				mClickSound = epData.substring(2, 4);
			}
			if(StringUtil.equals(epData.substring(0,2),FanCoilUtil.VIBRATE_STATE_TAG)){
				mReturnId = epData.substring(0, 2);
				mClickVibrate = epData.substring(2, 4);
			}
			if(StringUtil.equals(epData.substring(0,2),FanCoilUtil.TIME_SYNC_TAG)){
				mReturnId = epData.substring(0, 2);
				mSyncTime = epData.substring(2, 14);
			}
			if(StringUtil.equals(epData.substring(0,2),FanCoilUtil.DIFF_STATE_TAG)){
				mReturnId = epData.substring(0, 2);
				mDiffTemp = epData.substring(2, 4);
			}

		}
	}
	
	
}

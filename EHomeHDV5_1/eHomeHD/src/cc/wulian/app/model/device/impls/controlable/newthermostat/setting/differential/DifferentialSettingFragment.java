package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.differential;

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
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.differential.FirstStageItem.ShowFirstStageDownViewListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.differential.SecondStageItem.ShowSecondStageDownViewListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.differential.SendDifferentialItem.SendClickListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.differential.ThirdStageItem.ShowThirdStageDownViewListener;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.fragment.setting.EmptyItem;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.SendMessage;

public class DifferentialSettingFragment extends WulianFragment {

	private final String TAG = getClass().getSimpleName();
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private String mEpData;
	private DeviceCache cache;
	private AbstractDevice device;
	private AccountManager accountManager = AccountManager.getAccountManger();
	private ThermostatDialogManager dialogManager = ThermostatDialogManager.getDialogManager();
	private GatewayInfo gatewayInfo = accountManager.getmCurrentInfo();
	
	private ThermostatSettingManagerAdapter settingAdapter;
	private List<AbstractSettingItem> listViewItems;
	
	private WLDialog sendDialog;
	private WLDialog niticeDialog;
	
	@ViewInject(R.id.thermost_setting_lv)
	private ListView settingListView;
	
	private FirstStageItem mFirstStageItem;
	private FirstStageDownItem firstStageDownItem;
	
	private SecondStageItem mSecondStageItem;
	private SecondStageDownItem secondStageDownItem;
	
	private ThirdStageItem mThirdStageItem;
	private ThirdStageDownItem thirdStageDownItem;
	
	private SendDifferentialItem mSendDifferentialItem;
	
	private String mReturnId;
	private String mTempUnit;
	private String mFirstStage;
	private String mSecondStage;
	private String mThirdStage;
	private String firstStageValue;
	private String secondStageValue;
	private String thirdStageValue;
	private String firstSendCmd;
	private String secondSendCmd;
	private String thirdSendCmd;
	
	public static final String DIFF_TEMP_C = "00";
	public static final String DIFF_TEMP_F = "01";
	
	private static final String DIFF_CMD_TAG ="8";
	private static final String FIRST_CMD_TAG = "2";
	private static final String SECOND_CMD_TAG = "3";
	private static final String THIRD_CMD_TAG = "4";
	//查询 当前状态  工作模式数据
	private static final String CURRENT_QUERY_MODE_CMD = "12";
	//是否正常发送数据
	private static final String RECIVE_FAILED_CMD = "88";
	private static final String RECIVE_SUCCESS_ID = "08";
	
	private static final String NOTICE_RESET = "Reset";
	private static final String SETTING = "Setting";
	private static final String DIFF_NOTICE = "The value of the third stage should be higher than that"
			+ " of the second stage, and the value of the second stage should be higher than that "
			+ "of the first stage.";
	private static final String DIFF_NOTICE2 = "There are options not set,please check!";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		cache=DeviceCache.getInstance(mActivity);
		
		Bundle bundle = getArguments().getBundle("DifferentialSettingInfo");
		mGwId = bundle.getString(ThermostatSettingFragment.GWID);
		mDevId = bundle.getString(ThermostatSettingFragment.DEVID);
		mEp = bundle.getString(ThermostatSettingFragment.EP);
		mEpType = bundle.getString(ThermostatSettingFragment.EPTYPE);
		mTempUnit = bundle.getString("tempUnit"); 
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
		ViewUtils.inject(this,rootView);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		Log.i(TAG,"onViewCreated()");
		
		settingListView.setAdapter(settingAdapter);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, CURRENT_QUERY_MODE_CMD);
		initBar();
		loadData();
	}
	
	@Override
	public void onShow() {
		super.onShow();
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
		
		createFirstStage();
		createSecondStage();
		createThirdStage();
		
		mSendDifferentialItem = new SendDifferentialItem(mActivity);
		mSendDifferentialItem.initSystemState();
		mSendDifferentialItem.setSendClickListener(new SendClickListener() {
			
			@Override
			public void onSendClick() {
				getSendCmd();
				
				if((!StringUtil.isNullOrEmpty(firstStageValue))&&(!StringUtil.isNullOrEmpty(secondStageValue))
						&&(!StringUtil.isNullOrEmpty(thirdStageValue))){
					
					if(Double.valueOf(secondStageValue) > Double.valueOf(firstStageValue)
							&& Double.valueOf(secondStageValue) < Double.valueOf(thirdStageValue)){
						
							SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, DIFF_CMD_TAG+firstSendCmd);
							SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, DIFF_CMD_TAG+secondSendCmd);
							SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, DIFF_CMD_TAG+thirdSendCmd);
							showSetLoadDialog();
					}else{
						showNoticeDialog();
					}
//					Toast.makeText(mActivity, firstStageValue+"-"+secondStageValue+"-"+thirdStageValue, Toast.LENGTH_SHORT).show();
				}else{
					 showNoticeDialog2();
				}
			}
		});
		
		listViewItems.add(emptyItem1);
		listViewItems.add(mFirstStageItem);
		listViewItems.add(mSecondStageItem);
		listViewItems.add(mThirdStageItem);
		listViewItems.add(emptyItem2);
		listViewItems.add(mSendDifferentialItem);
		
		settingAdapter.swapData(listViewItems);
	}
	
	private void getSendCmd(){
		firstStageValue = firstStageDownItem.getmFirstStageValue();
		secondStageValue = secondStageDownItem.getmSecondStageValue();
		thirdStageValue = thirdStageDownItem.getmThirdStageValue();
		Log.i(TAG+":StageValue", firstStageValue+"-"+secondStageValue+"-"+thirdStageValue);
		
		String firstStageData = firstStageDownItem.getmFirstStage().substring(1,2);
		String secondStageData = secondStageDownItem.getmSecondStage().substring(1,2);
		String thirdStageData = thirdStageDownItem.getmThirdStage().substring(1,2);
		Log.i(TAG+":data", firstStageData+"-"+secondStageData+"-"+thirdStageData);
		firstSendCmd = FIRST_CMD_TAG + firstStageData;
		secondSendCmd = SECOND_CMD_TAG + secondStageData;
		thirdSendCmd = THIRD_CMD_TAG + thirdStageData;
		Log.i(TAG+":cmd", firstSendCmd+"-"+secondSendCmd+"-"+thirdSendCmd);
	}
	
	private void showNoticeDialog(){
		WLDialog.Builder builder = new Builder(mActivity);

		builder.setContentView(R.layout.device_thermostat82_setting_diff_send_dialog)
				.setNegativeButton(NOTICE_RESET)
				.setDismissAfterDone(true).setListener(new MessageListener() {
					
					@Override
					public void onClickPositive(View contentViewLayout) {
						
					}
					
					@Override
					public void onClickNegative(View contentViewLayout) {
						
					}
				});
				
		sendDialog = builder.create();
		TextView dialogTextView1 = (TextView) sendDialog.findViewById(R.id.thermost_setting_diff_dialog_first);
		TextView dialogTextView2 = (TextView) sendDialog.findViewById(R.id.thermost_setting_diff_dialog_second);
		TextView dialogTextView3= (TextView) sendDialog.findViewById(R.id.thermost_setting_diff_dialog_third);
		TextView dialogTextView4= (TextView) sendDialog.findViewById(R.id.thermost_setting_diff_dialog_tv);
		dialogTextView1.setText(firstStageValue);
		dialogTextView2.setText(secondStageValue);
		dialogTextView3.setText(thirdStageValue);
		dialogTextView4.setText(DIFF_NOTICE);
		sendDialog.show();
	}
	
	private void showNoticeDialog2(){
		WLDialog.Builder builder = new Builder(mActivity);

		builder.setTitle(null)
				.setSubTitleText(null)
				.setMessage(DIFF_NOTICE2)
				.setNegativeButton(NOTICE_RESET)
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
		if(mEpData.length() >= 2){
			if(StringUtil.equals(mEpData.substring(0, 2), RECIVE_FAILED_CMD)){
				showResultFailedDialog();
			}
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
		    }
		},1000);
	}
	
	private void createFirstStage(){
		mFirstStageItem = new FirstStageItem(mActivity);
		mFirstStageItem.initSystemState();
		firstStageDownItem = new FirstStageDownItem(mActivity);
		firstStageDownItem.initSystemState();
		firstStageDownItem.setFirstStageData(mTempUnit, null);
		mFirstStageItem.setFirstStageDownViewListener(new ShowFirstStageDownViewListener() {
			
			@Override
			public void onViewOpenChangeed(boolean isOpened) {
				if(isOpened){
					int position = listViewItems.indexOf(mFirstStageItem);
					listViewItems.add(position+1, firstStageDownItem);
					
				}else{
					listViewItems.remove(firstStageDownItem);
				}
				
				settingAdapter.swapData(listViewItems);
			}
		});
	}
	
	private void createSecondStage(){
		mSecondStageItem = new SecondStageItem(mActivity);
		mSecondStageItem.initSystemState();
		secondStageDownItem = new SecondStageDownItem(mActivity);
		secondStageDownItem.initSystemState();
		secondStageDownItem.setSecondStageData(mTempUnit, null);
		mSecondStageItem.setSecondStageDownViewListener(new ShowSecondStageDownViewListener() {
			
			@Override
			public void onViewOpenChangeed(boolean isOpened) {
				if(isOpened){
					int position = listViewItems.indexOf(mSecondStageItem);
					listViewItems.add(position+1, secondStageDownItem);
					
				}else{
					listViewItems.remove(secondStageDownItem);
				}
				
				settingAdapter.swapData(listViewItems);
			}
		});
	}
	
	private void createThirdStage(){
		mThirdStageItem = new ThirdStageItem(mActivity);
		mThirdStageItem.initSystemState();
		thirdStageDownItem = new ThirdStageDownItem(mActivity);
		thirdStageDownItem.initSystemState();
		thirdStageDownItem.setThirdStageData(mTempUnit, null);
		mThirdStageItem.setThirdStageDownViewListener(new ShowThirdStageDownViewListener() {
			
			@Override
			public void onViewOpenChangeed(boolean isOpened) {
				if(isOpened){
					int position = listViewItems.indexOf(mThirdStageItem);
					listViewItems.add(position+1, thirdStageDownItem);
					
				}else{
					listViewItems.remove(thirdStageDownItem);
				}
				
				settingAdapter.swapData(listViewItems);
			}
		});
	}
	
	
	public void onEventMainThread(DeviceEvent event){
		device=(AbstractDevice) cache.getDeviceByID(mActivity, event.deviceInfo.getGwID(), event.deviceInfo.getDevID());
		mEpData=device.getDeviceInfo().getDevEPInfo().getEpData();
		Log.i(TAG+"-epdata", mEpData+"-"+mEpData.length());
		
//		mGwId = event.deviceInfo.getGwID();
//		mDevId = event.deviceInfo.getDevID();
//		mEp = device.getDeviceInfo().getDevEPInfo().getEp();
//		mEpType = device.getDeviceInfo().getDevEPInfo().getEpType();
		handleEpData(mEpData);
		firstStageDownItem.setFirstStageData(mTempUnit, mFirstStage);
		secondStageDownItem.setSecondStageData(mTempUnit, mSecondStage);
		thirdStageDownItem.setThirdStageData(mTempUnit, mThirdStage);
		showSetResultDialog();
		
	}

	private void handleEpData(String epData) {
		
		if(epData.length()==38){
			mReturnId = epData.substring(0, 2);
			mTempUnit = epData.substring(6, 8);
			Log.i(TAG+"-mTempUnit", mTempUnit);
		}
		if(epData.length()==28){
			mReturnId = epData.substring(0, 2);
			mFirstStage = epData.substring(4, 6);
			Log.i(TAG+"-mFirstStage", mFirstStage);
			mSecondStage = epData.substring(6, 8);
			Log.i(TAG+"-mSecondStage", mSecondStage);
			mThirdStage = epData.substring(8, 10);
			Log.i(TAG+"-mThirdStage", mThirdStage);
		}
	}
	
}

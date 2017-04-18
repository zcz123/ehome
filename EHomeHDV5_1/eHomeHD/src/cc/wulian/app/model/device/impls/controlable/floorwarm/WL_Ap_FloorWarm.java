package cc.wulian.app.model.device.impls.controlable.floorwarm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yuantuo.customview.ui.ToastProxy;
import com.yuantuo.customview.ui.WLDialog;

import java.util.List;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmViewBulider.CountDownBtnListener;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmViewBulider.CurStateListener;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmViewBulider.CurTempListener;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmViewBulider.EnergySavingBtnListener;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmViewBulider.ProgramBtnListener;
import cc.wulian.app.model.device.impls.controlable.floorwarm.countdown.FloorWarmCountDownActivity;
import cc.wulian.app.model.device.impls.controlable.floorwarm.setting.FloorWarmSettingActivity;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.device.AreaGroupMenuPopupWindow;
import cc.wulian.smarthomev5.fragment.setting.flower.items.CustomPopupWindow;
import cc.wulian.smarthomev5.thirdparty.uei_yaokan.Logger;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * 地暖
 * @author hanx
 *
 */

@DeviceClassify(devTypes = { "Ap" }, category = Category.C_CONTROL)
public class WL_Ap_FloorWarm extends ControlableDeviceImpl implements Alarmable {
	private static final String TAG = "FloorWarm:";

	private FloorWarmViewBulider mViewBulider;
	//是否刷新倒计时界面
	private boolean isRefreshCountDown = true;

	/**
	 * 返回标志位（02开关设置03过温保护04防冻保护05温标设置06温度设置07时间设置08回差调整09声效设置）
	 * （0A系统选择0B编程模式0C编程模式开关0D震动设置0E定时设置0F一键节能0G联动0H恢复出厂设置）
	 */
	private String mReturnId;

	/**
	 * 开关 off:00  on:01
	 */
	private String mOnOff;

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
	 * 制热温度
	 */
	private String mHeatTemperature;
	/**
	 * 当前环境温度
	 */
	private String mCurrentTemp;
	/**
	 * 当前环境湿度
	 */
	private String mCurrentHumidity;
	/**
	 * 定时器状态  off:02  on:01
	 */
	private String mCountDownState;
	/**
	 * 定时器倒计时时间
	 */
	private String mCountDownTime;

	/**
	 * 一键节能开关  off:00  on:01
	 */
	private String mEnergySavingState;
	/**
	 * 一键节能温度
	 */
	private String mEnergySavingTemp;
	/**
	 *  地表温度
	 */
	private String mGroundTemp;
	/**
	 *  声效数据     off:00  on:01
	 */
	private String mClickSound;
	/**
	 *  时间数据
	 */
	private String mSyncTime;
	/**
	 *  震动数据     off:00  on:01
	 */
	private String mClickVibrate;
	/**
	 * 回差温度
	 */
	private String mDiffTemp;
	/**
	 * 编程模式开关  off:00  on:01
	 */
	private String mProgramState;
	/**
	 * DO联动
	 */
	private String mDOLinkage;
	/**
	 * 系统选择    水地暖 :00     电地暖:01
	 */
	private String mSystemType;

	public WL_Ap_FloorWarm(Context context, String type) {
		super(context, type);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
		mViewBulider = new FloorWarmViewBulider(inflater.getContext());

		return mViewBulider.getContentView();

	}

	@Override
	public void onResume() {
		super.onResume();
		controlDevice(ep, epType, FloorWarmUtil.CURRENT_QUERY_CMD_DATA);
		controlDevice(ep, epType, FloorWarmUtil.CURRENT_QUERY_CMD_MODE);
	}


	//获取设备编辑菜单 添加设置选项
	@Override
	public MoreMenuPopupWindow getDeviceMenu() {
		final MoreMenuPopupWindow popupWindow = new MoreMenuPopupWindow(mContext);
		List<MenuItem> mDeviceDetailsMenuItems  = getDeviceMenuItems(popupWindow);

		MenuItem deviceDetialsSettingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView
						.setText(mContext
								.getString(cc.wulian.smarthomev5.R.string.set_titel));
				iconImageView
						.setImageResource(R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				jumpToFloorWarmSettingActivity();
			}

			private void jumpToFloorWarmSettingActivity(){
				Intent intent = new Intent(mContext, FloorWarmSettingActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(FloorWarmUtil.GWID, gwID);
				bundle.putString(FloorWarmUtil.DEVID, devID);
				bundle.putString(FloorWarmUtil.EP, ep);
				bundle.putString(FloorWarmUtil.EPTYPE, epType);
				intent.putExtra("FloorWarmSettingFragmentInfo", bundle);
				mContext.startActivity(intent);
				popupWindow.dismiss();
			}
		};

		if(isDeviceOnLine()){
			mDeviceDetailsMenuItems.add(deviceDetialsSettingItem);
		}
		popupWindow.setMenuItems(mDeviceDetailsMenuItems);
		return popupWindow;
	}

	@Override
	public String getOpenSendCmd() {
		return FloorWarmUtil.STATE_CMD_ON;
	}

	@Override
	public boolean isClosed() {
		return isStateOff();
	}

	@Override
	public boolean isOpened() {
		return !isClosed();
	}

	@Override
	public String getCloseSendCmd() {
		return FloorWarmUtil.STATE_CMD_OFF;
	}

	//判断 状态 是否关闭
	private boolean isStateOff(){
		return isSameAs(mOnOff, FloorWarmUtil.STATE_OFF);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		mViewBulider.initFloorHeating();
//		fireWulianDeviceRequestControlSelf();
//		controlDevice(ep, epType, FloorWarmUtil.CURRENT_QUERY_CMD_DATA);
//		controlDevice(ep, epType, FloorWarmUtil.CURRENT_QUERY_CMD_MODE);

		mViewBulider.setmCurStateListener(new CurStateListener() {

			@Override
			public void onStateChanged(boolean isOpen) {
				if(isOpen){
					controlDevice(ep, epType, FloorWarmUtil.STATE_CMD_OFF);
				}else{
					controlDevice(ep, epType, FloorWarmUtil.STATE_CMD_ON);
				}
			}
		});

		mViewBulider.setmCurTempListener(new CurTempListener() {

			@Override
			public void onTempChanged(String temp) {
				String tempCmd = setTempCmd(temp);
				controlDevice(ep, epType, FloorWarmUtil.TEMP_CHANGE_CMD_TAG+tempCmd);
			}
		});

		mViewBulider.setmEnergySavingBtnListener(new EnergySavingBtnListener() {

			@Override
			public void onStateChanged(boolean isOpen) {
				if(isOpen){
					controlDevice(ep, epType, FloorWarmUtil.ENERGY_STATE_CMD_OFF + getEnergySavingCmd());
				}else{
					controlDevice(ep, epType, FloorWarmUtil.ENERGY_STATE_CMD_ON + getEnergySavingCmd());
				}

			}
		});

		mViewBulider.setmProgramBtnListener(new ProgramBtnListener() {

			@Override
			public void onStateChanged(boolean isOpen) {
				if(isOpen){
					controlDevice(ep, epType, FloorWarmUtil.PROGRAM_STATE_CMD_OFF);
				}else{
					controlDevice(ep, epType, FloorWarmUtil.PROGRAM_STATE_CMD_ON);
				}

			}
		});

		mViewBulider.setmCountDownBtnListener(new CountDownBtnListener() {

			@Override
			public void onButtonClicked() {
				jumpToCountDownActivity();
			}
		});
	}

	//温度转换为发送指令
	private String setTempCmd(String temp){
		String tempCmd = "";
		double tempdou=Double.parseDouble(temp)*100;
		String tempstr=(int)tempdou +"";
		tempCmd= StringUtil.appendLeft(tempstr, 4, '0');
		return tempCmd;
	}

	private String getEnergySavingCmd(){
		String tempCmd = "";
		if(!StringUtil.isNullOrEmpty(mEnergySavingTemp)){
			tempCmd = Integer.parseInt(mEnergySavingTemp , 16)+"";
		}else{
			tempCmd = "1800";
		}
		return tempCmd;
	}

	private void jumpToCountDownActivity(){
		Intent intent = new Intent(mContext, FloorWarmCountDownActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(FloorWarmUtil.GWID, gwID);
		bundle.putString(FloorWarmUtil.DEVID, devID);
		bundle.putString(FloorWarmUtil.EP, ep);
		bundle.putString(FloorWarmUtil.EPTYPE, epType);
		bundle.putString("mOnOff",mOnOff);
		bundle.putString("mCountDownState",mCountDownState);
		bundle.putString("mCountDownTime",mCountDownTime);
		intent.putExtra("CountDownFragmentInfo", bundle);
		mContext.startActivity(intent);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		refreshSensorView();
		initFloorWarmView();
		if(isRefreshCountDown){
			refreshCountView();
		}
	}

	private void initFloorWarmView() {
		initTemp();
		initMainView();
		initTempView();
		initProgress();
		if(isRefreshCountDown){
			initCountDown();
		}
	}

	//设置 主界面
	private void initMainView(){
		mViewBulider.setModeState(mOnOff, mCountDownState, mProgramState, mEnergySavingState,mOverTempState);
		mViewBulider.initViewByMode();
	}

	private void initTemp(){
		String energySavingTemp = FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str100(mEnergySavingTemp));
		String heatTemp = FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str100(mHeatTemperature));
		String groundTemp = FloorWarmUtil.hexStr2Str10(mGroundTemp);
		String overTemp = FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str100(mOverTempValue));
		mViewBulider.setEnergyTempValue(energySavingTemp);
		mViewBulider.setHeatTempValue(heatTemp);
		mViewBulider.setGroundAndOverTemp(groundTemp , overTemp);
	}

	//设置 温度值
	private void initTempView(){
		mViewBulider.setCurTemperatureView(FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str10(mCurrentTemp)));
		mViewBulider.setGroundTemperatureView(FloorWarmUtil.hexStr2Str10(mGroundTemp));
	}

	//设置温度ProgressBar显示
	private void initProgress(){
		mViewBulider.setCurProgress();
	}

	private void initCountDown(){
		mViewBulider.setCountDownTimeView(mCountDownTime);
	}

	//刷新倒计时时间
	private void refreshCountView(){
		mViewBulider.refreshCountDownView();
	}

	//刷新传感器异常报警界面
	private void refreshSensorView(){
		mViewBulider.refreshSensorView();
	}

	//接收数据
	@Override
	public void refreshDevice() {
		super.refreshDevice();
		disassembleCompoundCmd(epData);
	}

	private void disassembleCompoundCmd(String mEpData) {
		if(!isNull(mEpData)){
			Logger.i(TAG+"------epData", mEpData+"--"+mEpData.length());
			//基本数据
			if(mEpData.length() == 46){
				mReturnId = mEpData.substring(0, 2);
				mOnOff = mEpData.substring(2, 4);
				mOverTempState = mEpData.substring(4, 6);
				mOverTempValue = mEpData.substring(6, 10);
				mForstProtectState = mEpData.substring(10, 12);
				mForstProtectTemp = mEpData.substring(12, 16);
				mTemperatureUnit = mEpData.substring(16, 18);
				mHeatTemperature = mEpData.substring(18, 22);
				mCurrentTemp = mEpData.substring(22, 26);
				mCurrentHumidity = mEpData.substring(26, 28);
				mCountDownState = mEpData.substring(28, 30);
				mCountDownTime = mEpData.substring(30, 36);
				mEnergySavingState = mEpData.substring(36, 38);
				mEnergySavingTemp = mEpData.substring(38, 42);
				mGroundTemp = mEpData.substring(42, 46);
				isRefreshCountDown = true;
			}
			//模式数据
			else if(mEpData.length() == 28){
				mReturnId = mEpData.substring(0, 2);
				mClickSound = mEpData.substring(2, 4);
				mSyncTime = mEpData.substring(4, 16);
				mClickVibrate = mEpData.substring(16, 18);
				mDiffTemp = mEpData.substring(18, 20);
				mProgramState = mEpData.substring(20, 22);
				mDOLinkage = mEpData.substring(22, 26);
				mSystemType = mEpData.substring(26, 28);
				isRefreshCountDown = true;
			}
			//开关设置数据
			else if(isSameAs(mEpData.substring(0,2), FloorWarmUtil.STATE_DATA_TAG)){
				mReturnId = mEpData.substring(0, 2);
				mOnOff = mEpData.substring(2, 4);
				isRefreshCountDown = false;
			}
			//编程开关设置数据
			else if(isSameAs(mEpData.substring(0,2), FloorWarmUtil.PROGRAM_STATE_TAG)){
				mReturnId = mEpData.substring(0, 2);
				mProgramState = mEpData.substring(2, 4);
				isRefreshCountDown = false;
			}
			//温度设置数据
			else if(isSameAs(mEpData.substring(0,2), FloorWarmUtil.TEMP_CHANGE_TAG)){
				mReturnId = mEpData.substring(0, 2);
				mHeatTemperature = mEpData.substring(2, 6);
				isRefreshCountDown = false;
			}
			//一键节能开关数据
			else if(isSameAs(mEpData.substring(0,2), FloorWarmUtil.ENERGY_STATE_TAG)){
				mReturnId = mEpData.substring(0, 2);
				mEnergySavingState = mEpData.substring(2, 4);
				mEnergySavingTemp = mEpData.substring(4, 8);
				isRefreshCountDown = false;
			}
			//倒计时数据
			else if(isSameAs(mEpData.substring(0,2), FloorWarmUtil.COUNTDOWN_TAG)){
				isRefreshCountDown = true;
				mReturnId = mEpData.substring(0, 2);
				mCountDownState = mEpData.substring(2, 4);
				mCountDownTime = mEpData.substring(4, 10);
			}
			//过温保护数据
			if(StringUtil.equals(epData.substring(0,2), FloorWarmUtil.OVER_TEMP_TAG)){
				mReturnId = epData.substring(0, 2);
				//开关数据
				if(StringUtil.equals(epData.substring(2,4), FloorWarmUtil.STATE_OFF)){
					mOverTempState = epData.substring(4 , 6);
				}
				//设置温度数据
				if(StringUtil.equals(epData.substring(2,4), FloorWarmUtil.STATE_ON)){
					mOverTempValue = epData.substring(6 , 10);
				}
				isRefreshCountDown = false;
			}

		}
	}

	//报警
	@Override
	public CharSequence parseDestoryProtocol(String data) {
		StringBuilder sb = new StringBuilder();
		sb.append(DeviceTool.getDeviceAlarmAreaName(this));
		sb.append(DeviceTool.getDeviceShowName(this));
		if (LanguageUtil.isChina() || LanguageUtil.isTaiWan()) {
			sb.append(mContext.getString(R.string.home_device_alarm_default_voice_detect));
		} else {
			sb.append(" "+ mContext.getString(R.string.home_device_alarm_default_voice_detect)+ " ");
		}
		if(isSameAs(data , "0401")){
			controlDevice(ep, epType, FloorWarmUtil.CURRENT_QUERY_CMD_DATA);
			sb.append(mContext.getResources().getString(R.string.AP_outside_wrong));
			mViewBulider.setOutsideWarning(true);
			mViewBulider.setInsideWarning(false);
			mViewBulider.setSensorWarning(false);
		}else if(isSameAs(data , "0410")){
			controlDevice(ep, epType, FloorWarmUtil.CURRENT_QUERY_CMD_DATA);
			sb.append(mContext.getResources().getString(R.string.AP_inside_wrong));
			mViewBulider.setOutsideWarning(false);
			mViewBulider.setInsideWarning(true);
			mViewBulider.setSensorWarning(false);
		}else if(isSameAs(data , "0411")){
			controlDevice(ep, epType, FloorWarmUtil.CURRENT_QUERY_CMD_DATA);
			sb.append(mContext.getResources().getString(R.string.AP_both_wrong));
			mViewBulider.setOutsideWarning(false);
			mViewBulider.setInsideWarning(false);
			mViewBulider.setSensorWarning(true);
		}else if(isSameAs(data , "0422")){
			mViewBulider.setOutsideWarning(false);
			mViewBulider.setInsideWarning(false);
			mViewBulider.setSensorWarning(false);
			return null;
		}

		return sb.toString();
	}

	@Override
	public boolean isAlarming() {
		return false;
	}

	@Override
	public boolean isNormal() {
		return false;
	}

	@Override
	public String getCancleAlarmProtocol() {
		return null;
	}

	@Override
	public String getAlarmProtocol() {
		return null;
	}

	@Override
	public String getNormalProtocol() {
		return null;
	}

	@Override
	public String getAlarmString() {
		return null;
	}

	@Override
	public String getNormalString() {
		return null;
	}

	@Override
	public boolean isDestory() {
		return false;
	}

	@Override
	public boolean isLowPower() {
		return false;
	}

	@Override
	public CharSequence parseAlarmProtocol(String epData) {
		return null;
	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item, LayoutInflater inflater) {
		if (item == null) {
			item = new ControlableApShortCutControlItem(
					inflater.getContext());
		}
		item.setWulianDevice(this);
		return item;
	}

	//不显示设备列表中控制按钮
	protected class ControlableApShortCutControlItem extends
			DeviceShortCutControlItem {

		protected ControlableApShortCutControlItem(Context context) {
			super(context);
			View view = new View(mContext);
			controlLineLayout.addView(view);
		}

	}



	/**
	 * 管家场景列表显示
	 */
	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(DeviceShortCutSelectDataItem item,
																	   LayoutInflater inflater, AutoActionInfo autoActionInfo) {
		if(item == null){
			item = new ShortCutSelectDataItem(inflater.getContext());
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}

	/**
	 * 管家场景列表显示条目
	 */
	private class ShortCutSelectDataItem extends DeviceShortCutSelectDataItem{

		private LinearLayout controlableLineLayout;
		private TextView controlableTV;
		private ImageView controlableImage;

		public ShortCutSelectDataItem(Context context) {
			super(context);
			controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_thermostat82_cut_control_controlable, null);
			controlableTV = (TextView) controlableLineLayout.findViewById(R.id.thermostat_cut_control_tv);
			controlableImage = (ImageView) controlableLineLayout.findViewById(R.id.thermostat_cut_control_iv);
			controlLineLayout.addView(controlableLineLayout);
		}

		@Override
		public void setWulianDeviceAndSelectData(WulianDevice device, AutoActionInfo autoActionInfo) {
			super.setWulianDeviceAndSelectData(device, autoActionInfo);
			String epData = autoActionInfo.getEpData();
			if(!StringUtil.isNullOrEmpty(epData)){
				if(StringUtil.equals(epData , FloorWarmUtil.STATE_CMD_OFF)){
					controlableTV.setVisibility(View.INVISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.thermost_housekeeper_off);
				}else if(StringUtil.equals(epData , FloorWarmUtil.STATE_CMD_ON)){
					controlableTV.setVisibility(View.INVISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.thermost_housekeeper_on);
				}else if((epData.length() >2) && StringUtil.equals(epData.substring(0,1) , FloorWarmUtil.TEMP_CHANGE_CMD_TAG)){
					String tempHeat = Float.parseFloat(epData.substring(1 , 5))/100 + "";
					controlableTV.setText(tempHeat +"℃");
					controlableTV.setVisibility(View.VISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.thermost_housekeeper_heat);
				}
			}


		}

	}

	/**
	 * 管家场景设置
	 */
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(LayoutInflater inflater,
																				 final AutoActionInfo autoActionInfo) {
		final boolean[] isWarningDialogShow = {false};
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		holder.setShowDialog(false);
		String epData = autoActionInfo.getEpData();
		final View contentView =  inflater.inflate(R.layout.device_thermostat82_house_keeper_setting, null);
		final ImageButton btnStateOff = (ImageButton) contentView.findViewById(R.id.thermostat_house_off_btn);
		final ImageButton btnStateOn = (ImageButton) contentView.findViewById(R.id.thermostat_house_on_btn);
		final ImageButton btnStateFan = (ImageButton) contentView.findViewById(R.id.thermostat_house_fan_btn);
		final LinearLayout layoutHeat = (LinearLayout) contentView.findViewById(R.id.thermostat_house_heat_layout);
		final TextView tvHeatTemp = (TextView) contentView.findViewById(R.id.thermostat_house_seekbar_heat_progress);
		final SeekBar seekBarHeat = (SeekBar) contentView.findViewById(R.id.thermost_house_seekbar_heat);
		final LinearLayout layoutCool = (LinearLayout) contentView.findViewById(R.id.thermostat_house_cool_layout);
		layoutHeat.setBackground(mContext.getResources().getDrawable(R.drawable.account_manager_item_red_background));
		layoutCool.setVisibility(View.GONE);
		btnStateFan.setVisibility(View.INVISIBLE);
		if(!StringUtil.isNullOrEmpty(epData)){
			if(StringUtil.equals(epData , FloorWarmUtil.STATE_CMD_OFF)){
				btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_pre);
				btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_nor);
				tvHeatTemp.setText("10℃");
				tvHeatTemp.setTextColor(Color.GRAY);
				seekBarHeat.setProgress(0);
				seekBarHeat.setThumbOffset(0);
				seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
			}else if(StringUtil.equals(epData , FloorWarmUtil.STATE_CMD_ON)){
				btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_pre);
				btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_nor);
				tvHeatTemp.setText("10℃");
				tvHeatTemp.setTextColor(Color.GRAY);
				seekBarHeat.setProgress(0);
				seekBarHeat.setThumbOffset(0);
				seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
			}else if((epData.length() >2) && StringUtil.equals(epData.substring(0,1) , FloorWarmUtil.TEMP_CHANGE_CMD_TAG)){
				String tempHeat = Float.parseFloat(epData.substring(1 , 5))/100 + "";
				tvHeatTemp.setText(tempHeat +"℃");
				tvHeatTemp.setTextColor(Color.parseColor("#709E17"));
				int progress = (int)((Float.parseFloat(tempHeat) - 10) *2);
				DisplayMetrics metric = new DisplayMetrics();
				WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
				wm.getDefaultDisplay().getMetrics(metric);
				int width = metric.widthPixels;
				int left = (int)(((float)(progress)/seekBarHeat.getMax()) * (width/4*3-120))  +10;
				tvHeatTemp.setPadding(left ,0 , 10 ,0);
				seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont));
				seekBarHeat.setProgress(progress);
				btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_nor);
				btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_nor);
			}
		}

		//提示窗
		final AreaGroupMenuPopupWindow warningPopWindow= new AreaGroupMenuPopupWindow(mContext);
		final View warningContent = LayoutInflater.from(mContext).
				inflate(R.layout.device_fancoil_house_keeper_setting_warning_dialog, null);
		warningPopWindow.setContentView(warningContent);
		TextView warningBtnOk = (TextView) warningContent.findViewById(R.id.fancoil_house_keeper_prompt);
		warningBtnOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				warningPopWindow.dismiss();
			}
		});

		btnStateOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_pre);
				btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_nor);
				tvHeatTemp.setText("10℃");
				tvHeatTemp.setTextColor(Color.GRAY);
				seekBarHeat.setProgress(0);
				seekBarHeat.setThumbOffset(0);
				seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
				autoActionInfo.setEpData(FloorWarmUtil.STATE_CMD_OFF);
			}
		});

		btnStateOn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_pre);
				btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_nor);
				tvHeatTemp.setText("10℃");
				tvHeatTemp.setTextColor(Color.GRAY);
				seekBarHeat.setProgress(0);
				seekBarHeat.setThumbOffset(0);
				seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
				autoActionInfo.setEpData(FloorWarmUtil.STATE_CMD_ON);
			}
		});

		seekBarHeat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(!isWarningDialogShow[0]){
					warningPopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
				}
				tvHeatTemp.setText(((float)progress)/2 + 10 +"℃");
				tvHeatTemp.setTextColor(Color.parseColor("#709E17"));
				int left = (int)(((float)(progress)/seekBar.getMax()) * (seekBar.getWidth()-120))  +10;
				tvHeatTemp.setPadding(left ,0 , 10 ,0);
				seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont));
				btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_nor);
				btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_nor);
				isWarningDialogShow[0] = true;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				float tempHeat = ((float)seekBar.getProgress())/2 + 10;
				String tempHeatCmd = StringUtil.appendLeft((int)(tempHeat * 100)+"" ,4 ,'0');
				String heatEpData= createCompoundCmd(FloorWarmUtil.TEMP_CHANGE_CMD_TAG, tempHeatCmd);
				autoActionInfo.setEpData(heatEpData);
			}
		});

		holder.setContentView(contentView);
		return holder;
	}



}

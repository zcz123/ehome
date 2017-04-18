package cc.wulian.app.model.device.impls.controlable.fancoil;

import java.util.List;

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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilViewBulider.CountDownBtnListener;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilViewBulider.CurFanModeListener;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilViewBulider.CurModeListener;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilViewBulider.CurStateListener;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilViewBulider.CurTempListener;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilViewBulider.EnergySavingBtnListener;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilViewBulider.ProgramBtnListener;
import cc.wulian.app.model.device.impls.controlable.fancoil.countdown.FanCoilCountDownActivity;
import cc.wulian.app.model.device.impls.controlable.fancoil.setting.FanCoilSettingActivity;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.device.AreaGroupMenuPopupWindow;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;

/**
 * 风机盘管
 * @author hanx
 *
 */

@DeviceClassify(devTypes = { "Af" }, category = Category.C_CONTROL)
public class WL_Af_FanCoil extends ControlableDeviceImpl {
	private static final String TAG = "WL_Af_FanCoil:";
	//管家场景设置中组件
	private ImageButton btnStateOn;
	private ImageButton btnStateOff;
	private ImageButton btnModeHeat;
	private ImageButton btnModeCool;
	private ImageButton btnModeFan;
	private TextView tvTemp;
	private SeekBar seekBarTemp;
	private ImageButton btnFanLow;
	private ImageButton btnFanMid;
	private ImageButton btnFanHigh;
	private ImageButton btnFanAuto;
	
	private FanCoilViewBulider mViewBulider;
	//是否刷新倒计时界面
	boolean isRefreshCountDown = true;
	/**
	 * 返回标志位（01查询当前状态02开关设置03模式设置04风机设置05温标设置06温度设置07时间设置08回差调整09声效设置）
	 * （0B编程模式设置0C编程模式开关0D震动设置0E定时设置0F一键节能0G恢复出厂设置）
	 */
	private String mReturnId;
	
	/**
	 * 开关 off:00  on:01
	 */
	private String mOnOff;
	/**
	 * 模式 :01：制热    02：制冷    03：通风    04：制热节能   05：制冷节能
	 */
	private String mModeState;
	/**
	 * 风机 模式     00：关闭    01：低风   02：中风   03：高风   04：自动
	 */
	private String mFanState;
	/**
	 * 温度单位：00摄氏度，01华氏度
	 */
	private String mTemperatureUnit;
	/**
	 * 制热温度
	 */
	private String mHeatTemperature;
	/**
	 * 制冷温度
	 */
	private String mCoolTemperature;
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
	 * 制热节能温度
	 */
	private String mHeatEnergyTemp;
	/**
	 * 制冷节能温度
	 */
	private String mCoolEnergyTemp;
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
	
	public WL_Af_FanCoil(Context context, String type) {
		super(context, type);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
		mViewBulider = new FanCoilViewBulider(inflater.getContext());
		return mViewBulider.getContentView();
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
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				jumpToFanCoilSettingActivity();
				popupWindow.dismiss();
			}

			private void jumpToFanCoilSettingActivity(){
				Intent intent = new Intent(mContext, FanCoilSettingActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString(FanCoilUtil.GWID, gwID);
				bundle.putString(FanCoilUtil.DEVID, devID);
				bundle.putString(FanCoilUtil.EP, ep);
				bundle.putString(FanCoilUtil.EPTYPE, epType);
				intent.putExtra("FanCoilSettingFragmentInfo", bundle);
				mContext.startActivity(intent);
			}
		};
		if(isDeviceOnLine()){
			mDeviceDetailsMenuItems.add(deviceDetialsSettingItem);
		}
		popupWindow.setMenuItems(mDeviceDetailsMenuItems);
		return popupWindow;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		mViewBulider.initFloorHeating();
//		fireWulianDeviceRequestControlSelf();
		controlDevice(ep, epType, FanCoilUtil.CURRENT_QUERY_CMD_DATA);
		controlDevice(ep, epType, FanCoilUtil.CURRENT_QUERY_CMD_MODE);
		
		mViewBulider.setmCurStateListener(new CurStateListener() {
			
			@Override
			public void onStateChanged(boolean isOpen) {
				if(isOpen){
					controlDevice(ep, epType, FanCoilUtil.STATE_CMD_OFF);
				}else{
					controlDevice(ep, epType, FanCoilUtil.STATE_CMD_ON);
				}
			}
		});
		
		mViewBulider.setmCurTempListener(new CurTempListener() {
			
			@Override
			public void onTempChanged(String temp) {
				String mHeatTempCmd ="";
				String mCoolTempCmd = "";
				String tempCmd = "";
				if(isSameAs(mModeState, FanCoilUtil.MODE_HEAT)){
					mCoolTempCmd = "0000";
					mHeatTempCmd = setTempCmd(temp);
				}else if(isSameAs(mModeState, FanCoilUtil.MODE_COOL)){
					mHeatTempCmd = "0000";
					mCoolTempCmd = setTempCmd(temp);
				}
				tempCmd = createCompoundCmd(mHeatTempCmd, mCoolTempCmd);
				controlDevice(ep, epType, createCompoundCmd(FanCoilUtil.TEMP_CHANGE_CMD_TAG,tempCmd));
			}
		});
		
		mViewBulider.setmCurModeListener(new CurModeListener() {
			
			@Override
			public void onModelChanged(String mode) {
				if(isSameAs(mode, FanCoilUtil.MODE_HEAT)){
					controlDevice(ep, epType, FanCoilUtil.MODE_HEAT_CMD);
				}
				else if(isSameAs(mode, FanCoilUtil.MODE_COOL)){
					controlDevice(ep, epType, FanCoilUtil.MODE_COOL_CMD);
				}else{
					controlDevice(ep, epType, FanCoilUtil.MODE_FAN_CMD);
				}
				
			}
		});
		
		mViewBulider.setmCurFanModeListener(new CurFanModeListener() {
			
			@Override
			public void onFanModeChanged(String fnMode) {
				if(isSameAs(fnMode, FanCoilUtil.FAN_OFF)){
					controlDevice(ep, epType, FanCoilUtil.FAN_OFF_CMD);
				}
				else if(isSameAs(fnMode, FanCoilUtil.FAN_LOW)){
					controlDevice(ep, epType, FanCoilUtil.FAN_LOW_CMD);
				}
				else if(isSameAs(fnMode, FanCoilUtil.FAN_MID)){
					controlDevice(ep, epType, FanCoilUtil.FAN_MID_CMD);
				}
				else if(isSameAs(fnMode, FanCoilUtil.FAN_HIGH)){
					controlDevice(ep, epType, FanCoilUtil.FAN_HIGH_CMD);
				}
				else if(isSameAs(fnMode, FanCoilUtil.FAN_AUTO)){
					controlDevice(ep, epType, FanCoilUtil.FAN_AUTO_CMD);
				}
				
			}
		});
		
		mViewBulider.setmEnergySavingBtnListener(new EnergySavingBtnListener() {
			
			@Override
			public void onStateChanged(boolean isOpen) {
				if(isOpen){
					if(isSameAs(mModeState, FanCoilUtil.MODE_HEAT_ENERGY)){
						controlDevice(ep, epType, FanCoilUtil.MODE_HEAT_CMD);
					}
					if(isSameAs(mModeState, FanCoilUtil.MODE_COOL_ENERGY)){
						controlDevice(ep, epType, FanCoilUtil.MODE_COOL_CMD);
					}
				}else{
					if(isSameAs(mModeState, FanCoilUtil.MODE_HEAT)){
						controlDevice(ep, epType, FanCoilUtil.MODE_ENERGY_HEAT_CMD);
					}
					if(isSameAs(mModeState, FanCoilUtil.MODE_COOL)){
						controlDevice(ep, epType, FanCoilUtil.MODE_ENERGY_COOL_CMD);
					}
				}
				
			}
		});
		
		mViewBulider.setmProgramBtnListener(new ProgramBtnListener() {
			
			@Override
			public void onStateChanged(boolean isOpen) {
				if(isOpen){
					controlDevice(ep, epType, FanCoilUtil.PROGRAM_STATE_CMD_OFF);	
				}else{
					controlDevice(ep, epType, FanCoilUtil.PROGRAM_STATE_CMD_ON);	
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

	private void jumpToCountDownActivity(){
		Intent intent = new Intent(mContext, FanCoilCountDownActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(FanCoilUtil.GWID, gwID);
		bundle.putString(FanCoilUtil.DEVID, devID);
		bundle.putString(FanCoilUtil.EP, ep);
		bundle.putString(FanCoilUtil.EPTYPE, epType);
		bundle.putString("mOnOff",mOnOff);
		bundle.putString("mCountDownState",mCountDownState);
		bundle.putString("mCountDownTime",mCountDownTime);
		intent.putExtra("CountDownFragmentInfo", bundle);
		mContext.startActivity(intent);
	}

	@Override
	public String getCloseSendCmd() {
		return FanCoilUtil.STATE_CMD_OFF;
	}

	@Override
	public String getOpenSendCmd() {
		return FanCoilUtil.STATE_CMD_ON;
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
	public void initViewStatus() {
		super.initViewStatus();
		initDataFromEpdata();
		initFloorHeatingView();
		if(isRefreshCountDown){
			refreshCountView();
		}
	}

	private void initDataFromEpdata() {
		if(epData.startsWith("03")&&epData.length()==40){
//			1、xx:设备开关状态（开机、关机）2
//			2、yy:设备风机模式（关闭、低、中、高、自动）
//			3、zz：温度标志位
//			4、aa:设备工作模式(制热、制冷，通风、制热节能、制冷节能)
//			5、bbbb:制热温度
//			6、cccc:制冷温度
//			7、qqqq:当前环境温度数值
//			8、ii:当前环境湿度
//			9、dd：定时器状态
//			10、HHmmtt：定时器倒计时时间
//			11、rrrr：制热节能温度
//			12、ssss：制冷节能温度
			mOnOff=epData.substring(2,4);
			mFanState=epData.substring(4,6);
			mTemperatureUnit=epData.substring(6,8);
			mModeState=epData.substring(8,10);
			mHeatTemperature=epData.substring(10,14);
			mCoolTemperature=epData.substring(14,18);
			mCurrentTemp=epData.substring(18,22);
			mCurrentHumidity=epData.substring(22,24);
			mCountDownState=epData.substring(24,26);
			mCountDownTime=epData.substring(26,32);
			mHeatEnergyTemp=epData.substring(32,36);
			mCoolEnergyTemp=epData.substring(36);
		}
	}

	private void initFloorHeatingView() {
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
		mViewBulider.setMainState(mOnOff,mModeState,mFanState, mCountDownState, mProgramState);
		mViewBulider.initViewByMode();
	}
	
	private void initTemp(){
		String heatTemp = FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str100(mHeatTemperature));
		String coolTemp = FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str100(mCoolTemperature));
		String heatEnergyTemp = FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str100(mHeatEnergyTemp));
		String coolEnergyTemp = FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str100(mCoolEnergyTemp));
		mViewBulider.setHeatTempValue(heatTemp);
		mViewBulider.setCoolTempValue(coolTemp);
		mViewBulider.setEnergyTempHeat(heatEnergyTemp);
		mViewBulider.setEnergyTempCool(coolEnergyTemp);
	}

	
	//设置 温度值
	private void initTempView(){
		mViewBulider.setCurTemperatureView(FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str10(mCurrentTemp)));
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
	
	//判断 状态 是否关闭
	public boolean isStateOff(){
		return isSameAs(mOnOff, FanCoilUtil.STATE_OFF);
	}

	//接收数据
	@Override
	public void refreshDevice() {
		super.refreshDevice();
		disassembleCompoundCmd(epData);
	}

	
	private void disassembleCompoundCmd(String mEpData) {
		Log.i(TAG+"epData", epData+"-"+epData.length());
		if(!isNull(mEpData)){
			if(mEpData.length() == 40){
				mReturnId = mEpData.substring(0, 2);
				mOnOff = mEpData.substring(2, 4);
				mFanState = mEpData.substring(4, 6);
				mTemperatureUnit = mEpData.substring(6, 8);
				mModeState = mEpData.substring(8, 10);
				mHeatTemperature = mEpData.substring(10, 14);
				mCoolTemperature = mEpData.substring(14, 18);
				mCurrentTemp = mEpData.substring(18, 22);
				mCurrentHumidity = mEpData.substring(22, 24);
				mCountDownState = mEpData.substring(24, 26);
				mCountDownTime = mEpData.substring(26, 32);
				mHeatEnergyTemp = mEpData.substring(32, 36);
				mCoolEnergyTemp = mEpData.substring(36, 40);
				isRefreshCountDown = true;
			}
			if(mEpData.length() == 22){
				mReturnId = mEpData.substring(0, 2);
				mClickSound = mEpData.substring(2, 4);
				mSyncTime = mEpData.substring(4, 16);
				mClickVibrate = mEpData.substring(16, 18);
				mDiffTemp = mEpData.substring(18, 20);
				mProgramState = mEpData.substring(20, 22);
				isRefreshCountDown = false;
			}
			//状态开关数据
			if(isSameAs(mEpData.substring(0,2) ,FanCoilUtil.STATE_DATA_TAG)){
				mReturnId = mEpData.substring(0, 2);
				mOnOff = mEpData.substring(2 ,4);
				isRefreshCountDown = false;
			}
			//编程开关数据
			if(isSameAs(mEpData.substring(0,2) ,FanCoilUtil.PROGRAM_STATE_TAG)){
				mReturnId = mEpData.substring(0, 2);
				mProgramState = mEpData.substring(2 ,4);
				isRefreshCountDown = false;
			}
			//温度改变数据
			if(isSameAs(mEpData.substring(0,2) ,FanCoilUtil.TEMP_CHANGE_TAG)){
				mReturnId = mEpData.substring(0, 2);
				mHeatTemperature = mEpData.substring(2, 6);
				mCoolTemperature = mEpData.substring(6, 10);
				isRefreshCountDown = false;
			}
			//模式切换数据
			if(isSameAs(mEpData.substring(0,2) ,FanCoilUtil.MODE_SATTE_TAG)){
				mReturnId = mEpData.substring(0, 2);
				mModeState = mEpData.substring(2, 4);
				isRefreshCountDown = false;
			}
			//风机模式切换数据
			if(isSameAs(mEpData.substring(0,2) ,FanCoilUtil.FAN_STATE_TAG)){
				mReturnId = mEpData.substring(0, 2);
				mFanState = mEpData.substring(2, 4);
				isRefreshCountDown = false;
			}
			//倒计时数据
			if(isSameAs(mEpData.substring(0,2) ,FanCoilUtil.COUNTDOWN_STATE_TAG)){
				mReturnId = mEpData.substring(0, 2);
				mCountDownState = mEpData.substring(2, 4);
				mCountDownTime = mEpData.substring(4, 10);
				isRefreshCountDown = true;
			}
		}
		
	}
	
	 /** 
     * 温度设置
     * 温度转换为发送指令
     */  
	private String setTempCmd(String temp){
		String tempCmd = "";
		double tempdou=Double.parseDouble(temp)*100;
		String tempstr=(int)tempdou +"";
		tempCmd=StringUtil.appendLeft(tempstr, 4, '0');
		return tempCmd;
	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item, LayoutInflater inflater) {
		if (item == null) {
			item = new ControlableAfShortCutControlItem(
					inflater.getContext());
		}
		item.setWulianDevice(this);
		return item;
	}

	//不显示设备列表中控制按钮
	protected class ControlableAfShortCutControlItem extends
			DeviceShortCutControlItem {

		protected ControlableAfShortCutControlItem(Context context) {
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
	private class ShortCutSelectDataItem extends DeviceShortCutSelectDataItem {

		private LinearLayout controlableLineLayout;
		private TextView controlableTV;
		private ImageView controlableImage;

		public ShortCutSelectDataItem(Context context) {
			super(context);
			controlableLineLayout = (LinearLayout) inflater.inflate(R.layout.device_thermostat82_cut_control_controlable, null);
			controlableTV = (TextView) controlableLineLayout.findViewById(R.id.thermostat_cut_control_tv);
			controlableImage = (ImageView) controlableLineLayout.findViewById(R.id.thermostat_cut_control_iv);
			controlLineLayout.addView(controlableLineLayout);
		}

		@Override
		public void setWulianDeviceAndSelectData(WulianDevice device, AutoActionInfo autoActionInfo) {
			super.setWulianDeviceAndSelectData(device, autoActionInfo);
			String epData = autoActionInfo.getEpData();
			if(!StringUtil.isNullOrEmpty(epData)){
				if(isSameAs(epData , FanCoilUtil.STATE_CMD_ON)){
					controlableTV.setVisibility(View.INVISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.fancoil_house_state_on_pre);
				}else if(isSameAs(epData , FanCoilUtil.STATE_CMD_OFF)){
					controlableTV.setVisibility(View.INVISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.fancoil_house_state_off_pre);
				}else if(isSameAs(epData , FanCoilUtil.MODE_HEAT_CMD)){
					controlableTV.setVisibility(View.INVISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.fancoil_house_mode_hot_pre);
				}else if(isSameAs(epData , FanCoilUtil.MODE_COOL_CMD)){
					controlableTV.setVisibility(View.INVISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.fancoil_house_mode_cool_pre);
				}else if(isSameAs(epData , FanCoilUtil.MODE_FAN_CMD)){
					controlableTV.setVisibility(View.INVISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.fancoil_house_mode_fan_pre);
				}else if((epData.length() >2) && isSameAs(epData.substring(0,1) , FanCoilUtil.TEMP_CHANGE_CMD_TAG)){
					String tempInfo = epData.substring(1, 5);
					String tempValue = Float.parseFloat(tempInfo)/100 + "";
					controlableTV.setText(tempValue +"℃");
					controlableTV.setVisibility(View.VISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.fancoil_house_thermometer);
				}else if(isSameAs(epData ,  FanCoilUtil.FAN_LOW_CMD)){
					controlableTV.setVisibility(View.INVISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.fancoil_house_fan_low_pre);
				}else if(isSameAs(epData ,  FanCoilUtil.FAN_MID_CMD)){
					controlableTV.setVisibility(View.INVISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.fancoil_house_fan_mid_pre);
				}else if(isSameAs(epData ,  FanCoilUtil.FAN_HIGH_CMD)){
					controlableTV.setVisibility(View.INVISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.fancoil_house_fan_high_pre);
				}else if(isSameAs(epData ,  FanCoilUtil.FAN_AUTO_CMD)){
					controlableTV.setVisibility(View.INVISIBLE);
					controlableImage.setVisibility(View.VISIBLE);
					controlableImage.setImageResource(R.drawable.fancoil_house_fan_auto_pre);
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
		final boolean[] isWarningDialogShow = {false,false,false,false,false,false,false,false};
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		holder.setShowDialog(false);
		String epData = autoActionInfo.getEpData();
		final View contentView =  inflater.inflate(R.layout.device_fancoil_house_keeper_setting, null);
		btnStateOn = (ImageButton) contentView.findViewById(R.id.fancoil_house_on_btn);
		btnStateOff = (ImageButton) contentView.findViewById(R.id.fancoil_house_off_btn);
		btnModeHeat = (ImageButton) contentView.findViewById(R.id.fancoil_house_heat_btn);
		btnModeCool= (ImageButton) contentView.findViewById(R.id.fancoil_house_cool_btn);
		btnModeFan = (ImageButton) contentView.findViewById(R.id.fancoil_house_fan_btn);
		tvTemp = (TextView) contentView.findViewById(R.id.fancoil_house_seekbar_temp_progress);
		seekBarTemp = (SeekBar) contentView.findViewById(R.id.fancoil_house_seekbar_temp);
		btnFanLow = (ImageButton) contentView.findViewById(R.id.fancoil_house_fan_low_btn);
		btnFanMid = (ImageButton) contentView.findViewById(R.id.fancoil_house_fan_mid_btn);
		btnFanHigh = (ImageButton) contentView.findViewById(R.id.fancoil_house_fan_high_btn);
		btnFanAuto = (ImageButton) contentView.findViewById(R.id.fancoil_house_fan_auto_btn);

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

		if(!isNull(epData)){
			if(isSameAs(epData , FanCoilUtil.STATE_CMD_ON)){
				setHouseKeeperStateOnSelect();
			}else if(isSameAs(epData , FanCoilUtil.STATE_CMD_OFF)){
				setHouseKeeperStateOffSelect();
			}else if(isSameAs(epData , FanCoilUtil.MODE_HEAT_CMD)){
				setHouseKeeperModeHeatSelect();
			}else if(isSameAs(epData , FanCoilUtil.MODE_COOL_CMD)){
				setHouseKeeperModeCoolSelect();
			}else if(isSameAs(epData , FanCoilUtil.MODE_FAN_CMD)){
				setHouseKeeperModeFanSelect();
			}else if((epData.length() >2) && isSameAs(epData.substring(0,1) , FanCoilUtil.TEMP_CHANGE_CMD_TAG)){
				String tempInfo = epData.substring(1, 5);
				String tempValue = Float.parseFloat(tempInfo)/100 + "";
				setHouseKeeperTempSelect(tempValue,-1);
			}else if(isSameAs(epData ,  FanCoilUtil.FAN_LOW_CMD)){
				setHouseKeeperFanLowSelect();
			}else if(isSameAs(epData ,  FanCoilUtil.FAN_MID_CMD)){
				setHouseKeeperFanMidSelect();
			}else if(isSameAs(epData ,  FanCoilUtil.FAN_HIGH_CMD)){
				setHouseKeeperFanHighSelect();
			}else if(isSameAs(epData ,  FanCoilUtil.FAN_AUTO_CMD)){
				setHouseKeeperFanAutoSelect();
			}
		}

		btnStateOn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setHouseKeeperStateOnSelect();
				autoActionInfo.setEpData(FanCoilUtil.STATE_CMD_ON);
			}
		});
		btnStateOff.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setHouseKeeperStateOffSelect();
				autoActionInfo.setEpData(FanCoilUtil.STATE_CMD_OFF);
			}
		});
		btnModeHeat.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isWarningDialogShow[0]){
					warningPopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
				}
				setHouseKeeperModeHeatSelect();
				autoActionInfo.setEpData(FanCoilUtil.MODE_HEAT_CMD);
				isWarningDialogShow[0] = true;
			}
		});
		btnModeCool.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isWarningDialogShow[1]){
					warningPopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
				}
				setHouseKeeperModeCoolSelect();
				autoActionInfo.setEpData(FanCoilUtil.MODE_COOL_CMD);
				isWarningDialogShow[1] = true;
			}
		});

		btnModeFan.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isWarningDialogShow[2]){
					warningPopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
				}
				setHouseKeeperModeFanSelect();
				autoActionInfo.setEpData( FanCoilUtil.MODE_FAN_CMD);
				isWarningDialogShow[2] = true;
			}
		});

		seekBarTemp.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(!isWarningDialogShow[3]){
					warningPopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
				}
				tvTemp.setText(((float)progress)/2 + 10 +"℃");
				tvTemp.setTextColor(Color.parseColor("#709E17"));
				int left = (int)(((float)(progress)/seekBar.getMax()) * (seekBar.getWidth()-120))  +10;
				tvTemp.setPadding(left ,0 , 10 ,0);
				setHouseKeeperTempSelect(((float)progress)/2 + 10+"",progress);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				float temp= ((float)seekBar.getProgress())/2 + 10;
				String tempCmd = StringUtil.appendLeft((int)(temp * 100)+"" ,4 ,'0');
				String epData = createCompoundCmd(FanCoilUtil.TEMP_CHANGE_CMD_TAG,createCompoundCmd(tempCmd, tempCmd));
				autoActionInfo.setEpData(epData);
				isWarningDialogShow[3] = true;
			}
		});

		btnFanLow.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isWarningDialogShow[4]){
					warningPopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
				}
				setHouseKeeperFanLowSelect();
				autoActionInfo.setEpData(FanCoilUtil.FAN_LOW_CMD);
				isWarningDialogShow[4] = true;
			}
		});
		btnFanMid.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isWarningDialogShow[5]){
					warningPopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
				}
				setHouseKeeperFanMidSelect();
				autoActionInfo.setEpData(FanCoilUtil.FAN_MID_CMD);
				isWarningDialogShow[5] = true;
			}
		});
		btnFanHigh.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isWarningDialogShow[6]){
					warningPopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
				}
				setHouseKeeperFanHighSelect();
				autoActionInfo.setEpData(FanCoilUtil.FAN_HIGH_CMD);
				isWarningDialogShow[6] = true;
			}
		});
		btnFanAuto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isWarningDialogShow[7]){
					warningPopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
				}
				setHouseKeeperFanAutoSelect();
				autoActionInfo.setEpData(FanCoilUtil.FAN_AUTO_CMD);
				isWarningDialogShow[7] = true;
			}
		});
		holder.setContentView(contentView);
		return holder;
	}
	// 选中on
	private void setHouseKeeperStateOnSelect(){
		btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_pre);
		btnStateOff.setBackgroundResource(R.drawable.fancoil_house_state_off_nor);
		setModeUnselect();
		setTempUnselect();
		setFanSpeedUnselect();
	}
	// 选中off
	private void setHouseKeeperStateOffSelect(){
		btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_pre);
		btnStateOn.setBackgroundResource(R.drawable.fancoil_house_state_on_nor);
		setModeUnselect();
		setTempUnselect();
		setFanSpeedUnselect();
	}
	//选中模式制热
	private void setHouseKeeperModeHeatSelect(){
		btnModeHeat.setBackgroundResource(R.drawable.thermost_icon_mode_heat);
		btnModeCool.setBackgroundResource(R.drawable.fancoil_house_mode_cool_nor);
		btnModeFan.setBackgroundResource(R.drawable.fancoil_house_mode_fan_nor);
		setStateUnselect();
		setTempUnselect();
		setFanSpeedUnselect();
	}
	//选中模式制冷
	private void setHouseKeeperModeCoolSelect(){
		btnModeHeat.setBackgroundResource(R.drawable.fancoil_house_mode_hot_nor);
		btnModeCool.setBackgroundResource(R.drawable.thermost_icon_mode_cool);
		btnModeFan.setBackgroundResource(R.drawable.fancoil_house_mode_fan_nor);
		setStateUnselect();
		setTempUnselect();
		setFanSpeedUnselect();
	}

	// 选中模式通风
	private void setHouseKeeperModeFanSelect(){
		btnModeHeat.setBackgroundResource(R.drawable.fancoil_house_mode_hot_nor);
		btnModeCool.setBackgroundResource(R.drawable.fancoil_house_mode_cool_nor);
		btnModeFan.setBackgroundResource(R.drawable.fancoil_mode_fan_01);
		setStateUnselect();
		setTempUnselect();
		setFanSpeedUnselect();
	}

	// 选中温度调节
	private void setHouseKeeperTempSelect(String tempCool,int sProgess){
		if(sProgess == -1){
			tvTemp.setText(tempCool +"℃");
			tvTemp.setTextColor(Color.parseColor("#709E17"));
			int progress = (int)((Float.parseFloat(tempCool) - 10) *2);
			DisplayMetrics metric = new DisplayMetrics();
			WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
			wm.getDefaultDisplay().getMetrics(metric);
			int width = metric.widthPixels;
			int left = (int)(((float)(progress)/seekBarTemp.getMax()) * (width/4*3-120))  +10;
			tvTemp.setPadding(left ,0 , 10 ,0);
			seekBarTemp.setProgress(progress);
		}
		seekBarTemp.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont));
		setStateUnselect();
		setModeUnselect();
		setFanSpeedUnselect();
	}
	// 选中风速低速
	private void setHouseKeeperFanLowSelect(){
		btnFanLow.setBackgroundResource(R.drawable.fancoil_fan_low);
		setStateUnselect();
		setStateUnselect();
		setTempUnselect();
		setModeUnselect();
		btnFanMid.setBackgroundResource(R.drawable.fancoil_house_fan_mid_nor);
		btnFanHigh.setBackgroundResource(R.drawable.fancoil_house_fan_high_nor);
		btnFanAuto.setBackgroundResource(R.drawable.fancoil_house_fan_auto_nor);
	}
	// 选中风速中速
	private void setHouseKeeperFanMidSelect(){
		btnFanMid.setBackgroundResource(R.drawable.fancoil_fan_mid);
		setStateUnselect();
		setStateUnselect();
		setTempUnselect();
		setModeUnselect();
		btnFanLow.setBackgroundResource(R.drawable.fancoil_house_fan_low_nor);
		btnFanHigh.setBackgroundResource(R.drawable.fancoil_house_fan_high_nor);
		btnFanAuto.setBackgroundResource(R.drawable.fancoil_house_fan_auto_nor);
	}
	// 选中风速高速
	private void setHouseKeeperFanHighSelect(){
		btnFanHigh.setBackgroundResource(R.drawable.fancoil_fan_high);
		setStateUnselect();
		setStateUnselect();
		setTempUnselect();
		setModeUnselect();
		btnFanLow.setBackgroundResource(R.drawable.fancoil_house_fan_low_nor);
		btnFanMid.setBackgroundResource(R.drawable.fancoil_house_fan_mid_nor);
		btnFanAuto.setBackgroundResource(R.drawable.fancoil_house_fan_auto_nor);
	}
	// 选中风速自动
	private void setHouseKeeperFanAutoSelect(){
		btnFanAuto.setBackgroundResource(R.drawable.fancoil_house_fan_auto);
		setStateUnselect();
		setTempUnselect();
		setModeUnselect();
		btnFanLow.setBackgroundResource(R.drawable.fancoil_house_fan_low_nor);
		btnFanMid.setBackgroundResource(R.drawable.fancoil_house_fan_mid_nor);
		btnFanHigh.setBackgroundResource(R.drawable.fancoil_house_fan_high_nor);
	}
	// 状态不被选中 置灰
	private void setStateUnselect(){
		btnStateOn.setBackgroundResource(R.drawable.fancoil_house_state_on_nor);
		btnStateOff.setBackgroundResource(R.drawable.fancoil_house_state_off_nor);
	}

	// 温度不被选中 置灰
	private void setTempUnselect(){
		tvTemp.setText("10.0℃");
		tvTemp.setTextColor(Color.GRAY);
		seekBarTemp.setProgress(0);
		seekBarTemp.setThumbOffset(0);
		seekBarTemp.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
	}
	// 模式不被选中 置灰
	private void setModeUnselect(){
		btnModeHeat.setBackgroundResource(R.drawable.fancoil_house_mode_hot_nor);
		btnModeCool.setBackgroundResource(R.drawable.fancoil_house_mode_cool_nor);
		btnModeFan.setBackgroundResource(R.drawable.fancoil_house_mode_fan_nor);
	}
	// 风速不被选中 置灰
	private void setFanSpeedUnselect(){
		btnFanLow.setBackgroundResource(R.drawable.fancoil_house_fan_low_nor);
		btnFanMid.setBackgroundResource(R.drawable.fancoil_house_fan_mid_nor);
		btnFanHigh.setBackgroundResource(R.drawable.fancoil_house_fan_high_nor);
		btnFanAuto.setBackgroundResource(R.drawable.fancoil_house_fan_auto_nor);
	}

}

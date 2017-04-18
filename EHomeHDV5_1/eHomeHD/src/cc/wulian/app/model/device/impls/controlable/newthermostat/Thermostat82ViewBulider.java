package cc.wulian.app.model.device.impls.controlable.newthermostat;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import cc.wulian.app.model.device.impls.controlable.newthermostat.RangeProgressBar.OnAutoUpViewValueChanged;
import cc.wulian.app.model.device.impls.controlable.newthermostat.ThermostatArcProgressBar.OnUpViewValueChanged;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.thirdparty.uei_yaokan.Logger;

/**
 * 美标温控器界面创建器
 * @author hanx
 *
 */

public class Thermostat82ViewBulider {
	private static final String TAG = "WL_82_Thermostat82ViewBulider";
	
	private Context mContext;
	private View contentView;
	
	private LinearLayout mainLayout;
	private LinearLayout toSetLayout;
	private Button mToSetBtn;

	private ImageView mCircleImage;
	private ImageView mCircleImage2;
	private TextView mEmergencyTv;
	private TextView mModeTextView;
	private TextView mTempTextView;
	private TextView mTempTextView2;
	private Button mShutdownTVBtn;
	private ThermostatArcProgressBar mProgressBar;
	private RangeProgressBar mAutoProgressBar;
	private RelativeLayout mModeLayout;
	private RelativeLayout mShutdownLayout;
	
	private LinearLayout mBtnLayout;
	private ImageButton mModeBtn;
	private ImageButton mFanBtn;
	private ImageButton mShutdownBtn;
	private ImageButton mProgramBtn;
	private ImageButton mSettingBtn;
	private ToggleButton mDropdownBtn;
	
	//事件监听
	private CurModeListener curModeListener;
	private CurStateListener curStateListener;
	private CurFanModeListener curFanModeListener;
	private CurTempListener curTempListener;
	private ProgramBtnListener programBtnListener;
	private SettingBtnListener settingBtnListener;
	private ToSetBtnListener toSetBtnListener;
	private CurAutoTempListener curAutoTempListener;
	// 返回标记
	private String mReturnId;
	// 系统类型
	private String mSystemType;
	public static String RESET_RETURN_ID = "06";
	public static String RESET_SYSTEM_TYPE = "00";
	// 制热温度
	private String mHeatTemp;
	
	//制冷温度
	private String mCoolTemp;
	
	//自动制热温度
	private String mAutoHeatTemp;
	
	// 自动制冷温度
	private String mAutoCoolTemp;

	//紧急制热
	private String mEmergencyHeat;
	
	//温度单位 
	private String mTemperatureUnit;
	public static final String TEMP_UNIT_C = "00";
	public static final String TEMP_UNIT_F = "01";
	
	//风机模式 
	private String mFanMode;
	public static final String FANMODE_AUTO = "01";
	public static final String FANMODE_ON = "02";
	
	//工作模式
	private String mMode;
	public static final String MODE_HEAT = "01";
	public static final String MODE_COOL = "02";
	public static final String MODE_AUTO = "03";
	
	//开关 状态
	private String mState;
	public static final String STATE_OFF = "00";
	public static final String STATE_ON = "01";

	//圆盘背景图片
	private static final int DRAEABLE_CIECLE_NORMAL = R.drawable.thermost_icon_circular_02;
	private static final int DRAEABLE_CIECLE_HEAT = R.drawable.thermost_icon_circular_03;
	private static final int DRAEABLE_CIECLE_C = R.drawable.thermost_icon_circular_01;
	private static final int DRAEABLE_CIECLE_F = R.drawable.thermost_icon_circular_f;
	//heat cool auto 三种模式按钮图片
	private static final int DRAWABLE_HEAT = R.drawable.thermost_mode_heat_btn_selector;
	private static final int DRAWABLE_COOL = R.drawable.thermost_mode_cool_btn_selector;
	private static final int DRAWABLE_AUTO= R.drawable.thermost_mode_auto_btn_selector;
	
	//风机 工作模式按钮  图片
	private static final int DRAWABLE_FAN_AUTO = R.drawable.thermost_fan_auto_btn_selector;
	private static final int DRAWABLE_FAN_ON = R.drawable.thermost_fan_on_btn_selector;
	
	//工作状态  开、关按钮 图片
	private static final int DRAWABLE_STATE_ON = R.drawable.thermost_shutdown_on_btn_selector;
	private static final int DRAWABLE_STATE_OFF = R.drawable.thermost_shutdown_off_btn_selector;
	
	//进度 最大值 最小值
	public static final int PROGRESS_MAX_C = 32; 
	public static final int PROGRESS_MIN_C = 10; 
	public static final int PROGRESS_MAX_F = 90; 
	public static final int PROGRESS_MIN_F = 50; 
	
	public Thermostat82ViewBulider(Context mContext) {
		this.mContext = mContext;
		initContentView();
	}
	
	private void initContentView(){
		LayoutInflater inflater = LayoutInflater.from(mContext);
		contentView = inflater.inflate(R.layout.device_thermostat82, null);
	} 
	
	private void initViewData(){
		mainLayout = (LinearLayout) contentView.findViewById(R.id.thermostat_main_layout);
		toSetLayout = (LinearLayout) contentView.findViewById(R.id.thermostat_toset_layout);
		mToSetBtn = (Button) contentView.findViewById(R.id.thermostat_toset_btn);
		mCircleImage = (ImageView) contentView.findViewById(R.id.thermost_circular_image);
		mCircleImage2 = (ImageView) contentView.findViewById(R.id.thermost_circular_image2);
		mEmergencyTv = (TextView) contentView.findViewById(R.id.thermost_emergency_tv);
		mModeTextView = (TextView) contentView.findViewById(R.id.thermost_mode_tv);
		mTempTextView = (TextView) contentView.findViewById(R.id.thermost_temperature_tv);
		mTempTextView2 = (TextView) contentView.findViewById(R.id.thermost_temperature_tv2);
		mShutdownTVBtn = (Button) contentView.findViewById(R.id.thermost_shutdown_tbtn);
		mProgressBar = (ThermostatArcProgressBar) contentView.findViewById(R.id.thermost_ArcProgressBar);
		mAutoProgressBar = (RangeProgressBar) contentView.findViewById(R.id.thermost_ArcProgressBar_auto);
		mModeLayout = (RelativeLayout) contentView.findViewById(R.id.thermost_mode_layout);
		mShutdownLayout = (RelativeLayout) contentView.findViewById(R.id.thermost_shundown_layout);
		mBtnLayout = (LinearLayout) contentView.findViewById(R.id.thermost_dropdown_layout);
		mModeBtn = (ImageButton) contentView.findViewById(R.id.thermost_mode_btn);
		mFanBtn = (ImageButton) contentView.findViewById(R.id.thermost_fan_btn);
		mShutdownBtn = (ImageButton) contentView.findViewById(R.id.thermost_shutdown_btn);
		mProgramBtn = (ImageButton) contentView.findViewById(R.id.thermost_program_btn);
		mSettingBtn = (ImageButton) contentView.findViewById(R.id.thermost_setting_btn);
		mDropdownBtn = (ToggleButton) contentView.findViewById(R.id.thermost_pull_btn);
		
		mModeBtn.setOnClickListener(mClickListener);
		mFanBtn.setOnClickListener(mClickListener);
		mShutdownBtn.setOnClickListener(mClickListener);
		mProgramBtn.setOnClickListener(mClickListener);
		mSettingBtn.setOnClickListener(mClickListener);
		//mDropdownBtn.setOnClickListener(mClickListener);
		//mShutdownTVBtn.setOnClickListener(mClickListener);
	}
	
	public View getContentView(){
		return contentView;
	}
	
	
	public void setCurModeListener(CurModeListener curModeListener) {
		this.curModeListener = curModeListener;
	}

	public void setCurStateListener(CurStateListener curStateListener) {
		this.curStateListener = curStateListener;
	}

	public void setCurFanModeListener(CurFanModeListener curFanModeListener) {
		this.curFanModeListener = curFanModeListener;
	}

	public void setCurTempListener(CurTempListener curTempListener) {
		this.curTempListener = curTempListener;
	}

	public void setProgramBtnListener(ProgramBtnListener programBtnListener) {
		this.programBtnListener = programBtnListener;
	}

	public void setSettingBtnListener(SettingBtnListener settingBtnListener) {
		this.settingBtnListener = settingBtnListener;
	}
	
	public void setToSetBtnListener(ToSetBtnListener toSetBtnListener) {
		this.toSetBtnListener = toSetBtnListener;
	}

	public void setCurAutoTempListener(CurAutoTempListener curAutoTempListener) {
		this.curAutoTempListener = curAutoTempListener;
	}

	/**
	 * 初始化界面
	 */
	public void initThermostat(){
		
		initViewData();
		
		mDropdownBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
				
				if(isChecked){
					
					mBtnLayout.setVisibility(View.GONE);
				}else{
					mBtnLayout.setVisibility(View.VISIBLE);
					
				}
			}
		});
		
//		mProgressBar.setOnMoveViewValueChanged(new OnMoveViewValueChanged() {
//			
//			@Override
//			public void onMoveChanged(String value) {
//
//				if (curTempListener != null) {
//					curTempListener.onTempChanged(value);
//				}
//			}
//		});
		
		mProgressBar.setOnUpViewValueChanged(new OnUpViewValueChanged() {
			
			@Override
			public void onUpChanged(String value) {
				if (curTempListener != null) {
					curTempListener.onTempChanged(value);
				}
			}
		});
		
		mToSetBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				toSetBtnListener.onToSetBtnClick();
				
			}
		});
		
//		mAutoProgressBar.setOnAutoMoveViewValueChanged(new OnAutoMoveViewValueChanged() {
//			
//			@Override
//			public void onMoveChanged(String value1, String value2) {
//				
//				curAutoTempListener.onTempChanged(value1, value2);
//			}
//		});
		
		mAutoProgressBar.setOnAutoUpViewValueChanged(new OnAutoUpViewValueChanged() {
			
			@Override
			public void onUpChanged(String value1, String value2) {
				curAutoTempListener.onTempChanged(value1, value2);
			}
		});
	}
	
	/**
	 * 设置工作模式  和风机模式
	 */
	public void setMode(String mode,String fanMode, String state){
		mMode = mode;
		mFanMode = fanMode;
		mState = state;
	}
	
	/**
	 * 设置 返回标志 和系统模式
	 * @param returnId
	 * @param systemType
	 */
	public void setIdAndSystemType(String returnId,String systemType){
		mReturnId = returnId;
		mSystemType = systemType;
	}
	/**
	 * 设置 紧急制热状态
	 */
	public void setEmergencyHeat(String emergencyHeat){
		mEmergencyHeat = emergencyHeat;
	}
	
	/**
	 * 温度显示界面
	 * @param  currentTemperature
	 */
	public void setTemperatureView(String currentTemperature){
		
		if(!StringUtil.isNullOrEmpty(currentTemperature) ){
			if(StringUtil.equals(mTemperatureUnit, TEMP_UNIT_C)){
				
				String s1 =currentTemperature.substring(0, currentTemperature.indexOf("."));
				String s2 = currentTemperature.substring(currentTemperature.lastIndexOf("."),currentTemperature.length());
				mTempTextView.setText(s1);
				mTempTextView2.setVisibility(View.VISIBLE);
				mTempTextView2.setText(s2);
			}else{
				float tempFloat = Float.valueOf(currentTemperature).shortValue();
				String s = (int)(Math.ceil(tempFloat*1.8+32))+"";
				mTempTextView.setText(s);
				mTempTextView2.setVisibility(View.GONE);
			}
		}
		
	}
	
	/**
	 * 设置 温度 进度显示
	 *
	 */
	public void setCurProgress() {
		if(!StringUtil.isNullOrEmpty(mMode)){
			
			if(StringUtil.equals(mMode, MODE_HEAT)){
				setProgressMode();
				setProgressTemp(mHeatTemp);
			}
			else if(StringUtil.equals(mMode, MODE_COOL)){
				setProgressMode();
				setProgressTemp(mCoolTemp);
			}
			else{
				setProgressTemp2(mAutoHeatTemp,mAutoCoolTemp);
			}
			
		}	

	}

	private void setProgressMode(){
		mProgressBar.setMode(mMode);
	}
	
	// 根据温标  设置 拖动条 最大值 最小值  温度显示
	private void setProgressTemp(String temp1){
		if(!StringUtil.isNullOrEmpty(mTemperatureUnit)){
			if(!StringUtil.isNullOrEmpty(temp1)){
				Logger.i(TAG+":Temp", temp1);
				if(Double.valueOf(temp1) > 32){
					temp1 = (double)32+"";
				}
				if(Double.valueOf(temp1) < 10){
					temp1 = (double)10+"";
				}
				if(StringUtil.equals(mTemperatureUnit, TEMP_UNIT_C)){
					mProgressBar.setMaxValue(32);
					mProgressBar.setMinValue(10);
					mProgressBar.setProcess(temp1);
					
				}else{
					mProgressBar.setMaxValue(90);
					mProgressBar.setMinValue(50);
					mProgressBar.setProcess((int)Math.round((Float.parseFloat(temp1)*1.8+32))+"");
				}
				
			}
		}
		
	}
	
	// 根据温标  设置 拖动条 最大值 最小值  温度显示
		private void setProgressTemp2(String tempHeat,String tempCool){
			if(!StringUtil.isNullOrEmpty(mTemperatureUnit)){
				if((!StringUtil.isNullOrEmpty(tempHeat)) && (!StringUtil.isNullOrEmpty(tempCool))){
					if(StringUtil.equals(tempHeat, tempCool)){
						tempHeat = Double.valueOf(tempCool) - 3+"";
					}
					if(Double.valueOf(tempHeat) > 32){
						tempHeat = (double)32+"";
					}
					if(Double.valueOf(tempHeat) < 10){
						tempHeat = (double)10+"";
					}
					if(Double.valueOf(tempCool) > 32){
						tempCool = (double)32+"";
					}
					if(Double.valueOf(tempCool) < 10){
						tempCool = (double)10+"";
					}
					if(StringUtil.equals(mTemperatureUnit, TEMP_UNIT_C)){
						mAutoProgressBar.setMaxValue(32);
						mAutoProgressBar.setMinValue(10);
						mAutoProgressBar.setProcessHeat(tempHeat);
						mAutoProgressBar.setProcessCool(tempCool);
						
					}else{
						mAutoProgressBar.setMaxValue(90);
						mAutoProgressBar.setMinValue(50);
						mAutoProgressBar.setProcessHeat((int)Math.round((Float.parseFloat(tempHeat)*1.8+32))+"");
						mAutoProgressBar.setProcessCool((int)Math.round((Float.parseFloat(tempCool)*1.8+32))+"");
					}
					
				}
			}
			
		}
		
	
	//根据模式显示界面
	public void initModeView(){
		//StringUtil.equals(mReturnId, RESET_RETURN_ID) || 
		if(StringUtil.equals(mSystemType,RESET_SYSTEM_TYPE)){
			showToSetView();
		}
		else{
			showMainView();
			if(!StringUtil.isNullOrEmpty(mTemperatureUnit)){
				if(StringUtil.equals(mTemperatureUnit,TEMP_UNIT_C)){
					setCircleImageC();
				}else{
					setCircleImageF();
				}
			}
			
			if(StringUtil.equals(mState, STATE_ON)){
				setStateOn();
				if(StringUtil.equals(mEmergencyHeat,STATE_ON)){
					showEnergyHeatView();
				}else{
					dismissEnergyHeatView();
				}
				if(StringUtil.equals(mFanMode, FANMODE_AUTO)){
					setFanModeAutoBtn();
				
					switch (mMode) {
					case MODE_HEAT:
						showHeatView();
						break;
					case MODE_COOL:
						showCoolView();
						break;
					case MODE_AUTO:
						showAutoView();
						break;
					default:
						break;
					}
				}
				
				if(StringUtil.equals(mFanMode, FANMODE_ON)){
					setFanModeOnBtn();
					
					switch (mMode) {
						case MODE_HEAT:
							showHeatView();
							break;
						case MODE_COOL:
							showCoolView();
							break;
						case MODE_AUTO:
							showAutoView();
							break;
						default:
							break;
					}
				}
			}
			
			if(StringUtil.equals(mState, STATE_OFF)){
				showOffView();
				if(StringUtil.equals(mFanMode, FANMODE_AUTO)){
					setFanModeAutoBtn();
				}
				
				if(StringUtil.equals(mFanMode, FANMODE_ON)){
					setFanModeOnBtn();
				}
			}
		
		}	
	}
	
	//显示主界面
	public void showMainView(){
		mainLayout.setVisibility(View.VISIBLE);
		toSetLayout.setVisibility(View.GONE);
	}
	
	//显示 TO SET 重置界面
	public void showToSetView(){
		mainLayout.setVisibility(View.GONE);
		toSetLayout.setVisibility(View.VISIBLE);
	}
	
	// Heat界面
	public void showHeatView(){
//		mModeLayout.setVisibility(View.VISIBLE);
//		mShutdownLayout.setVisibility(View.GONE);
		mModeTextView.setText("heat");
		mProgressBar.setVisibility(View.VISIBLE);
		mAutoProgressBar.setVisibility(View.GONE);
		setModeHeatBtn();
	}
	
	//Cool界面
	public void showCoolView(){
//		mModeLayout.setVisibility(View.VISIBLE);
//		mShutdownLayout.setVisibility(View.GONE);
		mModeTextView.setText("cool");
		mProgressBar.setVisibility(View.VISIBLE);
		mAutoProgressBar.setVisibility(View.GONE);
		setModeCoolBtn();
		dismissEnergyHeatView();
	}
	
	//Auto界面
	public void showAutoView(){
//		mModeLayout.setVisibility(View.VISIBLE);
//		mShutdownLayout.setVisibility(View.GONE);
		mModeTextView.setText("auto");
		mProgressBar.setVisibility(View.GONE);
		mAutoProgressBar.setVisibility(View.VISIBLE);
		setModeAutoBtn();
	}
	
	//Off界面
	public void showOffView(){
//		mModeLayout.setVisibility(View.GONE);
//		mShutdownLayout.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.GONE);
		mAutoProgressBar.setVisibility(View.GONE);
		setStateOff();
		//mBtnLayout.setVisibility(View.INVISIBLE);
	}

	//紧急制热 界面
	private void showEnergyHeatView(){
		mCircleImage2.setImageResource(DRAEABLE_CIECLE_HEAT);
		mEmergencyTv.setVisibility(View.VISIBLE);
	}

	private void dismissEnergyHeatView(){
		mCircleImage2.setImageResource(DRAEABLE_CIECLE_NORMAL);
		mEmergencyTv.setVisibility(View.GONE);
	}

	//设置背景圆盘
	private void setCircleImageC(){
		mCircleImage.setImageResource(DRAEABLE_CIECLE_C);
	}
	private void setCircleImageF(){
		mCircleImage.setImageResource(DRAEABLE_CIECLE_F);
	}
	// 设置模式按钮 图片
	public void setModeHeatBtn(){
		mModeBtn.setBackgroundResource(DRAWABLE_HEAT);
	}
	public void setModeCoolBtn(){
		mModeBtn.setBackgroundResource(DRAWABLE_COOL);
	}
	public void setModeAutoBtn(){
		mModeBtn.setBackgroundResource(DRAWABLE_AUTO);
	}
	
	// 设置 风机  开 、关按钮  图片
	public void setFanModeOnBtn(){
		mFanBtn.setBackgroundResource(DRAWABLE_FAN_ON);
	}
	public void setFanModeAutoBtn(){
		mFanBtn.setBackgroundResource(DRAWABLE_FAN_AUTO);
	}
	
	//设置  状态 开、关   按钮图片
	public void setStateOn(){
		mShutdownBtn.setBackgroundResource(DRAWABLE_STATE_ON);
	}
	public void setStateOff(){
		mShutdownBtn.setBackgroundResource(DRAWABLE_STATE_OFF);
	}
	
	//设置温度
	public void setHeatTemp(String heatTemp) {
		this.mHeatTemp = heatTemp;
	}
	public void setCoolTemp(String coolTemp) {
		this.mCoolTemp = coolTemp;
	}
	public void setAutoHeatTemp(String autoHeatTemp) {
		this.mAutoHeatTemp = autoHeatTemp;
	}
	public void setAutoCoolTemp(String autoCoolTemp) {
		this.mAutoCoolTemp = autoCoolTemp;
	}
	//设置温度单位
	public void setTempUnit(String tempUint){
		this.mTemperatureUnit = tempUint;
	}

	
	public OnClickListener mClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View view) {
			String changeMode = mMode;
			String changeFnMode = mFanMode;
			boolean isStateOpen = true;
			switch (view.getId()) {
			case R.id.thermost_mode_btn:
				
				if(StringUtil.equals(changeMode, MODE_HEAT)){
					changeMode = MODE_COOL;
				}else if(StringUtil.equals(changeMode, MODE_COOL)){
					changeMode = MODE_AUTO;
				}else{
					changeMode = MODE_HEAT;
				}
				curModeListener.onModelChanged(changeMode);
				break;
			case R.id.thermost_shutdown_tbtn:
				if(mShutdownLayout.getVisibility() == View.VISIBLE){
					curStateListener.onStateChanged(true);
				}
				
				break;
			case R.id.thermost_program_btn:
				programBtnListener.onProgramBtnClick();
				break;
			case R.id.thermost_setting_btn:
				settingBtnListener.onSettingBtnClick();
				break;
				
			case R.id.thermost_fan_btn:
				if(StringUtil.equals(changeFnMode, FANMODE_AUTO)){
					changeFnMode = FANMODE_ON;
				}else{
					changeFnMode = FANMODE_AUTO;
				}
				curFanModeListener.onFanModeChanged(changeFnMode);
				break;
			case R.id.thermost_shutdown_btn:
				if(StringUtil.equals(mState, STATE_ON)){
					isStateOpen = false;
				}else{
					isStateOpen = true;
				}
				mModeBtn.setEnabled(isStateOpen);
				mProgramBtn.setEnabled(isStateOpen);
				curStateListener.onStateChanged(isStateOpen);
				break;
			default:
				break;
			}
			
		}
	};
	
	// auto模式下 温度改变监听
	public interface CurAutoTempListener {
		public void onTempChanged(String temp1,String temp2);
	}
	
	//温度改变  监听
	public interface CurTempListener {
		public void onTempChanged(String temp);
	}

	//模式改变 监听
	public interface CurModeListener {
		public void onModelChanged(String model);
	}

	// 状态改变监听  开、关
	public interface CurStateListener {
		public void onStateChanged(boolean isOpen);
	}
	
	//风机状态改变 监听
	public interface CurFanModeListener{
		public void onFanModeChanged(String fnMode);
	}
	
	// 编程按钮监听
	public interface ProgramBtnListener{
		public void onProgramBtnClick();
	}
	
	// 设置按钮监听
	public interface SettingBtnListener{
		public void onSettingBtnClick();
	}
	
	// 重置 ToSet按钮监听
	public interface ToSetBtnListener{
		public void onToSetBtnClick();
	}
	
	
}

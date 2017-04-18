package cc.wulian.app.model.device.impls.controlable.fancoil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilProgressBar.OnMoveViewValueChanged;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;

/**
 * 风机盘管界面创建器
 * @author hanx
 *
 */

public class FanCoilViewBulider {
	private static final String TAG = "FloorHeatingViewBulider:";

	private Context mContext;
	private View contentView;

	private FrameLayout mModeLayout;
	private TextView mModeTextView;
	private TextView mTempTextView;
	private TextView mTempTextView2;
	private FanCoilProgressBar mProgressBar;
	private ImageButton mEnergySavingBtn;
	private ImageView mModeFanImage;

	private LinearLayout mShutDownLayout;
	private ImageButton mShutdownBtn;

	private LinearLayout mCountDownLayout;
	private TextView mCountDownHourTv;
	private TextView mCountDownMinTv;
	private TextView mCountDownTypeTv;

	private ToggleButton mDropDownBtn;
	private LinearLayout mDropDownLayout;
	private ImageButton mFanBtn;
	private ImageButton mModeSwitchBtn;
	private ImageButton mStateBtn;
	private ImageButton mCountDownBtn;
	private ImageButton mProgramBtn;

	private CurModeListener mCurModeListener;
	private CurFanModeListener mCurFanModeListener;
	private CurStateListener mCurStateListener;
	private CurTempListener mCurTempListener;
	private EnergySavingBtnListener mEnergySavingBtnListener;
	private CountDownBtnListener mCountDownBtnListener;
	private ProgramBtnListener mProgramBtnListener;

	// 返回标记
	private String mReturnId;
	// 制热温度
	private String mHeatTemp;
	//制冷温度
	private String mCoolTemp;
	//温度单位 
	private String mTemperatureUnit;
	//开关 状态
	private String mState;
	//模式 状态
	private String mModeState;
	//出风 状态
	private String mFanState;
	//编程开关 状态
	private String mProgramState;
	//倒计时开关 状态
	private String mCountDownState;
	//倒计时时间
	private String mCountDownTime;
	//一键节能开关 状态
	private String mEnergySavingState;
	//制热节能温度
	private String mEnergyTempHeat;
	//制冷节能温度
	private String mEnergyTempCool;

	private Timer timer ;
	private TimerTask task;
	private int hourValue;
	private int minuteValue;
	private int secondValue;
	private Handler handler;

	//模式按钮图片
	private static final int DRAWABLE_MODE_HEAT= R.drawable.thermost_mode_heat_btn_selector;
	private static final int DRAWABLE_MODE_COOL= R.drawable.thermost_mode_cool_btn_selector;
	private static final int DRAWABLE_MODE_FAN= R.drawable.fancoil_mode_fan_btn_selector;
	private static final int DRAWABLE_MODE_UNABLE= R.drawable.thermost_icon_mode_heat_12;

	//风挡按钮图片
	private static final int DRAWABLE_FAN_LOW= R.drawable.fancoil_fan_low_btn_selector;
	private static final int DRAWABLE_FAN_MID= R.drawable.fancoil_fan_mid_btn_selector;
	private static final int DRAWABLE_FAN_HIGH= R.drawable.fancoil_fan_high_btn_selector;
	private static final int DRAWABLE_FAN_AUTO= R.drawable.fancoil_fan_auto_btn_selector;
	private static final int DRAWABLE_FAN_OFF= R.drawable.fancoil_fan_off_btn_selector;
	private static final int DRAWABLE_FAN_UNABLE = R.drawable.fancoil_fan_unable_01;

	//节能按钮状态 图片
	private static final int DRAWABLE_ENERGY_ON= R.drawable.fancoil_jieneng_on_btn_selector;
	private static final int DRAWABLE_ENERGY_OFF = R.drawable.fancoil_jieneng_off_btn_selector;

	//倒计时开关按钮图片
	private static final int DRAWABLE_COUNTDOWN_ON= R.drawable.floorheating_countdown_on_btn_selector;
	private static final int DRAWABLE_COUNTDOWN_OFF = R.drawable.floorheating_countdown_off_btn_selector;

	//开关状态按钮  图片
	private static final int DRAWABLE_STATE_ON= R.drawable.floorheating_state_on_btn_selector;
	private static final int DRAWABLE_STATE_OFF = R.drawable.floorheating_state_off_btn_selector;

	//编程状态按钮 图片
	private static final int DRAWABLE_PROGRAM_ON = R.drawable.floorheating_program_on_btn_selector;
	private static final int DRAWABLE_PROGRAM_OFF = R.drawable.floorheating_program_off_btn_selector;
	private static final int DRAWABLE_PROGRAM_UNABLE = R.drawable.floorheating_program_02;


	public FanCoilViewBulider(Context mContext) {
		this.mContext = mContext;
		initContentView();

		timer = new Timer();
		//倒计时Handler
		handler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if(msg.what == 1){
					refreshCountDown();
				}
			}
		};
	}

	private void initContentView(){
		LayoutInflater inflater = LayoutInflater.from(mContext);
		contentView = inflater.inflate(R.layout.device_fancoil, null);
	}

	public View getContentView(){
		return contentView;
	}

	public void setmCurModeListener(CurModeListener mCurModeListener) {
		this.mCurModeListener = mCurModeListener;
	}

	public void setmCurFanModeListener(CurFanModeListener mCurFanModeListener) {
		this.mCurFanModeListener = mCurFanModeListener;
	}

	public void setmCurStateListener(CurStateListener mCurStateListener) {
		this.mCurStateListener = mCurStateListener;
	}

	public void setmCurTempListener(CurTempListener mCurTempListener) {
		this.mCurTempListener = mCurTempListener;
	}

	public void setmEnergySavingBtnListener(EnergySavingBtnListener mEnergySavingBtnListener) {
		this.mEnergySavingBtnListener = mEnergySavingBtnListener;
	}

	public void setmCountDownBtnListener(CountDownBtnListener mCountDownBtnListener) {
		this.mCountDownBtnListener = mCountDownBtnListener;
	}

	public void setmProgramBtnListener(ProgramBtnListener mProgramBtnListener) {
		this.mProgramBtnListener = mProgramBtnListener;
	}

	/**
	 * 初始化界面
	 */
	public void initFloorHeating(){

		initViewData();

		mProgressBar.setOnUpViewValueChanged(new FanCoilProgressBar.OnUpViewValueChanged() {
			@Override
			public void onUpChanged(String value) {
				if (mCurTempListener != null) {
					mCurTempListener.onTempChanged(value);
				}
			}
		});

		mDropDownBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

				if(isChecked){

					mDropDownLayout.setVisibility(View.GONE);
				}else{
					mDropDownLayout.setVisibility(View.VISIBLE);

				}
			}
		});
	}

	private void initViewData(){
		mEnergySavingBtn = (ImageButton) contentView.findViewById(R.id.fancoil_energysaving_btn);
		mModeLayout = (FrameLayout) contentView.findViewById(R.id.fancoil_mode_layout);
		mModeTextView = (TextView) contentView.findViewById(R.id.fancoil_mode_tv);
		mTempTextView = (TextView) contentView.findViewById(R.id.fancoil_temperature_tv);
		mTempTextView2 = (TextView) contentView.findViewById(R.id.fancoil_temperature_tv2);
		mProgressBar = (FanCoilProgressBar) contentView.findViewById(R.id.fancoil_ArcProgressBar);
		mShutDownLayout = (LinearLayout) contentView.findViewById(R.id.fancoil_shutdown_layout);
		mShutdownBtn = (ImageButton) contentView.findViewById(R.id.fancoil_shutdown_btn);
		mModeFanImage = (ImageView) contentView.findViewById(R.id.fancoil_mode_fan_image);
		mCountDownLayout = (LinearLayout) contentView.findViewById(R.id.fancoil_countdown_time_layout);
		mCountDownHourTv = (TextView) contentView.findViewById(R.id.fancoil_countdown_time_hour);
		mCountDownMinTv = (TextView) contentView.findViewById(R.id.fancoil_countdown_time_min);
		mCountDownTypeTv = (TextView) contentView.findViewById(R.id.fancoil_countdown_time_type);
		mDropDownBtn = (ToggleButton) contentView.findViewById(R.id.fancoil_pull_btn);
		mDropDownLayout = (LinearLayout) contentView.findViewById(R.id.fancoil_dropdown_layout);
		mModeSwitchBtn = (ImageButton) contentView.findViewById(R.id.fancoil_mode_btn);
		mFanBtn = (ImageButton) contentView.findViewById(R.id.fancoil_fan_btn);
		mCountDownBtn = (ImageButton) contentView.findViewById(R.id.fancoil_countdown_btn);
		mStateBtn = (ImageButton) contentView.findViewById(R.id.fancoil_state_btn);
		mProgramBtn = (ImageButton) contentView.findViewById(R.id.fancoil_program_btn);

		mEnergySavingBtn.setOnClickListener(mClickListener);
		mModeSwitchBtn.setOnClickListener(mClickListener);
		mFanBtn.setOnClickListener(mClickListener);
		mCountDownBtn.setOnClickListener(mClickListener);
		mStateBtn.setOnClickListener(mClickListener);
		mProgramBtn.setOnClickListener(mClickListener);

	}

	/**
	 * 设置工作状态，倒计时状态，编程状态，一键节能状态
	 * @param
	 */
	public void setMainState(String state,String modeState,String fanState,String countDownState, String programState){
		this.mState = state;
		this.mModeState = modeState;
		this.mFanState = fanState;
		this.mCountDownState = countDownState;
		this.mProgramState = programState;
	}

	/**
	 * 根据模式设置View显示
	 */
	public void initViewByMode(){
		// 状态 开 、关
		if(StringUtil.equals(mState, FanCoilUtil.STATE_ON)){
			showMainView();
			//风机模式：关、低、中、高、自动
			if(StringUtil.equals(mFanState, FanCoilUtil.FAN_OFF)){
				setFanBtnOff();
			}
			else if(StringUtil.equals(mFanState, FanCoilUtil.FAN_LOW)){
				setFanBtnLow();
			}
			else if(StringUtil.equals(mFanState, FanCoilUtil.FAN_MID)){
				setFanBtnMid();
			}
			else if(StringUtil.equals(mFanState, FanCoilUtil.FAN_HIGH)){
				setFanBtnHigh();
			}else{
				setFanBtnAuto();
			}
			// 编程模式  开、关
			if(StringUtil.equals(mProgramState, FanCoilUtil.STATE_ON)){
				setProgramBtnOn();
			}else{
				setProgramBtnOff();
			}
			// 模式 ：制热、制冷、通风、制热节能、制冷节能
			if(StringUtil.equals(mModeState, FanCoilUtil.MODE_HEAT)){
				showHeatView();
			}
			else if(StringUtil.equals(mModeState, FanCoilUtil.MODE_COOL)){
				showCoolView();
			}else if(StringUtil.equals(mModeState, FanCoilUtil.MODE_FAN)){
				showFanView();
			}else if(StringUtil.equals(mModeState, FanCoilUtil.MODE_HEAT_ENERGY)){
				showEnergyView();
			}else if(StringUtil.equals(mModeState, FanCoilUtil.MODE_COOL_ENERGY)){
				showEnergyView();
			}
			//倒计时  开、关
			if(StringUtil.equals(mCountDownState, FanCoilUtil.COUNTDOWN_STATE_ON_TAG) ||
					StringUtil.equals(mCountDownState, FanCoilUtil.COUNTDOWN_STATE_OFF_TAG)){
				setCountDownBtnOn();
			}else{
				setCountDownBtnOff();
				cancelTimerTask();
			}

		}else if(StringUtil.equals(mState, FanCoilUtil.STATE_OFF)){
			showOffView();
			if(StringUtil.equals(mCountDownState, FanCoilUtil.COUNTDOWN_STATE_ON_TAG)){
				setCountDownBtnOn();
			}else{
				setCountDownBtnOff();
				cancelTimerTask();
			}
		}
	}

	//设置 一键节能温度
	public void setEnergyTempHeat(String energyTempHeat){
		this.mEnergyTempHeat = energyTempHeat;
	}

	public void setEnergyTempCool(String energyTempCool){
		this.mEnergyTempCool = energyTempCool;
	}

	//设置 制热温度
	public void setHeatTempValue(String heatTemp){
		this.mHeatTemp = heatTemp;
	}
	//设置 制冷温度
	public void setCoolTempValue(String coolTemp){
		this.mCoolTemp = coolTemp;
	}

	/**
	 * 当前温度显示界面
	 * @param  currentTemperature
	 */
	public void setCurTemperatureView(String currentTemperature){

		if(!StringUtil.isNullOrEmpty(currentTemperature) ){
			String s1 =currentTemperature.substring(0, currentTemperature.indexOf("."));
			String s2 =currentTemperature.substring(currentTemperature.indexOf("."), currentTemperature.length());
			mTempTextView.setText(s1);
			mTempTextView2.setText(s2);
		}
	}

	//设置 温度 进度显示
	public void setCurProgress() {
		if(!StringUtil.isNullOrEmpty(mModeState)){
			if(StringUtil.equals(mModeState, FanCoilUtil.MODE_HEAT)){
				setProgressMode(true);
				setProgressTemp(mHeatTemp);
			}else if(StringUtil.equals(mModeState, FanCoilUtil.MODE_COOL)){
				setProgressMode(false);
				setProgressTemp(mCoolTemp);
			}else if(StringUtil.equals(mModeState, FanCoilUtil.MODE_HEAT_ENERGY)){
				setProgressMode(true);
				setProgressTemp(mEnergyTempHeat);
			}else if(StringUtil.equals(mModeState, FanCoilUtil.MODE_COOL_ENERGY)){
				setProgressMode(false);
				setProgressTemp(mEnergyTempCool);
			}
		}
	}

	private void setProgressMode(boolean isHeat){
		mProgressBar.setHeat(isHeat);
	}

	// 根据温标  设置 拖动条 最大值 最小值  温度显示
		private void setProgressTemp(String temp){
				if(!StringUtil.isNullOrEmpty(temp)){
					if(Double.valueOf(temp) > FanCoilUtil.PROGRESS_MAX_C){
						temp = (double)32+"";
					}
					if(Double.valueOf(temp) < FanCoilUtil.PROGRESS_MIN_C){
						temp = (double)10+"";
					}
					mProgressBar.setMaxValue(FanCoilUtil.PROGRESS_MAX_C);
					mProgressBar.setMinValue(FanCoilUtil.PROGRESS_MIN_C);
					mProgressBar.setProcess(temp);

				}

		}

	/**
	 * 设置 倒计时时间 显示
	 * @param countDownTime
	 */
	public void setCountDownTimeView(String countDownTime){
		if(!StringUtil.isNullOrEmpty(countDownTime)){
			this.mCountDownTime = countDownTime;
			//倒计时 时间 16进制 转为10进制
			String time = FloorWarmUtil.hexTime2Time(mCountDownTime);
			mCountDownHourTv.setText(time.substring(0 , 2));
			mCountDownMinTv.setText(time.substring(2 , 4));
		}
		if(StringUtil.equals(mState, FanCoilUtil.STATE_ON)){
			mCountDownTypeTv.setText(mContext.getResources().getString(FanCoilUtil.COUNTDOWN_TYPE_TEXT_OFF));
		}else{
			mCountDownTypeTv.setText(mContext.getResources().getString(FanCoilUtil.COUNTDOWN_TYPE_TEXT_ON));
		}
	}

	public void refreshCountDownView(){
		if(!StringUtil.isNullOrEmpty(mCountDownState) && !StringUtil.isNullOrEmpty(mCountDownTime)){

			if(StringUtil.equals(mCountDownState, FanCoilUtil.COUNTDOWN_STATE_ON_TAG)
					|| StringUtil.equals(mCountDownState, FanCoilUtil.COUNTDOWN_STATE_OFF_TAG)){
				setCountTaskTime(mCountDownTime);
				startTimerTask();
			}else{
				cancelTimerTask();
			}
		}
	}

	//显示开机状态主页面
	private void showMainView(){
		//mShutdownBtn.setVisibility(View.GONE);
		mEnergySavingBtn.setVisibility(View.VISIBLE);
		mProgressBar.setVisibility(View.VISIBLE);
		setStateBtnOn();
	}

	//显示关机界面
	private void showOffView(){
		//mShutdownBtn.setVisibility(View.VISIBLE);
		mEnergySavingBtn.setVisibility(View.INVISIBLE);
		mProgressBar.setVisibility(View.INVISIBLE);
		setStateBtnOff();
		setModeBtnUnEnable();
		setProgramBtnUnEnable();
		setFanBtnUnEnable();
	}

	//制热界面
	private void showHeatView(){
		mModeTextView.setText(mContext.getResources().getString(R.string.AP_get_hot));
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressBar.setmEnabled(true);
		showModeLayout();
		setModeBtnHeat();
		setEnergySavingBtnOff();
	}
	//制冷界面
	private void showCoolView(){
		mModeTextView.setText(mContext.getResources().getString(R.string.air_conditioner_cooling_mode));
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressBar.setmEnabled(true);
		showModeLayout();
		setModeBtnCool();
		setEnergySavingBtnOff();
	}
	//风机界面
	private void showFanView(){
		mModeTextView.setText(mContext.getResources().getString(R.string.device_ac_cmd_air_supply));
		mEnergySavingBtn.setBackgroundResource(R.drawable.floorheating_air_supply);
		mProgressBar.setVisibility(View.GONE);
		showModeLayout();
		setModeBtnFan();
	}

	//节能界面
	private void showEnergyView(){
		mModeTextView.setText(mContext.getResources().getString(R.string.AP_get_enegry));
		mProgressBar.setmEnabled(false);
		showModeLayout();
		setEnergySavingBtnOn();
		setModeBtnUnEnable();
		setFanBtnUnEnable();
		setProgramBtnUnEnable();
	}

	//显示制热 或制冷 布局  
	private void showModeLayout(){
		mModeLayout.setVisibility(View.VISIBLE);
		mModeFanImage.setVisibility(View.GONE);
	}

	//显示  风机 布局
	private void showModeFanLayout(){
		mModeLayout.setVisibility(View.GONE);
		mModeFanImage.setVisibility(View.VISIBLE);
	}

	//显示倒计时时间
	private void showCountDownView(){
		mCountDownLayout.setVisibility(View.VISIBLE);
	}

	//隐藏倒计时时间
	private void dismissCountDownView(){
		mCountDownLayout.setVisibility(View.GONE);
	}

	//设置 倒计时按钮 开关状态
	private void setCountDownBtnOn(){
		mCountDownBtn.setBackgroundResource(DRAWABLE_COUNTDOWN_ON);
		showCountDownView();
	}
	private void setCountDownBtnOff(){
		mCountDownBtn.setBackgroundResource(DRAWABLE_COUNTDOWN_OFF);
		dismissCountDownView();
	}

	//设置开关按钮 状态
	private void setStateBtnOn(){
		mStateBtn.setBackgroundResource(DRAWABLE_STATE_ON);
	}
	private void setStateBtnOff(){
		mStateBtn.setBackgroundResource(DRAWABLE_STATE_OFF);
	}

	//设置编程按钮 开关状态
	private void setProgramBtnOn(){
		mProgramBtn.setEnabled(true);
		mProgramBtn.setBackgroundResource(DRAWABLE_PROGRAM_ON);
	}
	private void setProgramBtnOff(){
		mProgramBtn.setEnabled(true);
		mProgramBtn.setBackgroundResource(DRAWABLE_PROGRAM_OFF);
	}

	private void setProgramBtnUnEnable(){
		mProgramBtn.setEnabled(false);
		mProgramBtn.setBackgroundResource(DRAWABLE_PROGRAM_UNABLE);
	}

	//设置节能按钮 开关状态
	private void setEnergySavingBtnOn(){
		mEnergySavingBtn.setBackgroundResource(DRAWABLE_ENERGY_ON);
	}
	private void setEnergySavingBtnOff(){
		mEnergySavingBtn.setBackgroundResource(DRAWABLE_ENERGY_OFF);
	}

	//设置  模式按钮
	private void setModeBtnHeat(){
		mModeSwitchBtn.setEnabled(true);
		mModeSwitchBtn.setBackgroundResource(DRAWABLE_MODE_HEAT);
	}
	private void setModeBtnCool(){
		mModeSwitchBtn.setEnabled(true);
		mModeSwitchBtn.setBackgroundResource(DRAWABLE_MODE_COOL);
	}
	private void setModeBtnFan(){
		mModeSwitchBtn.setEnabled(true);
		mModeSwitchBtn.setBackgroundResource(DRAWABLE_MODE_FAN);
	}

	private void setModeBtnUnEnable(){
		mModeSwitchBtn.setEnabled(false);
		mModeSwitchBtn.setBackgroundResource(DRAWABLE_MODE_UNABLE);
	}

	//设置风机等级按钮
	private void setFanBtnOff(){
		mFanBtn.setEnabled(true);
		mFanBtn.setBackgroundResource(DRAWABLE_FAN_OFF);
	}
	private void setFanBtnLow(){
		mFanBtn.setEnabled(true);
		mFanBtn.setBackgroundResource(DRAWABLE_FAN_LOW);
	}
	private void setFanBtnMid(){
		mFanBtn.setEnabled(true);
		mFanBtn.setBackgroundResource(DRAWABLE_FAN_MID);
	}
	private void setFanBtnHigh(){
		mFanBtn.setEnabled(true);
		mFanBtn.setBackgroundResource(DRAWABLE_FAN_HIGH);
	}
	private void setFanBtnAuto(){
		mFanBtn.setEnabled(true);
		mFanBtn.setBackgroundResource(DRAWABLE_FAN_AUTO);
	}
	private void setFanBtnUnEnable(){
		mFanBtn.setEnabled(false);
		mFanBtn.setBackgroundResource(DRAWABLE_FAN_UNABLE);
	}

	public OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			boolean isEnergyOpen;
			boolean isStateOn;
			boolean isProgramOpen;
			String changeMode = mModeState;
			String changeFnMode = mFanState ;

			switch (view.getId()) {
			case R.id.fancoil_mode_btn: //模式按钮
				if(StringUtil.equals(changeMode, FanCoilUtil.MODE_HEAT)){
					changeMode = FanCoilUtil.MODE_COOL;
				}
				else if (StringUtil.equals(changeMode, FanCoilUtil.MODE_COOL)){
					changeMode = FanCoilUtil.MODE_FAN;
				}else{
					changeMode = FanCoilUtil.MODE_HEAT;
				}
				mCurModeListener.onModelChanged(changeMode);

				break;
			case R.id.fancoil_fan_btn:  //风挡按钮
				if(StringUtil.equals(changeFnMode, FanCoilUtil.FAN_OFF)){
					changeFnMode = FanCoilUtil.FAN_LOW;
				}else if(StringUtil.equals(changeFnMode, FanCoilUtil.FAN_LOW)){
					changeFnMode = FanCoilUtil.FAN_MID;
				}else if(StringUtil.equals(changeFnMode, FanCoilUtil.FAN_MID)){
					changeFnMode = FanCoilUtil.FAN_HIGH;
				}else if(StringUtil.equals(changeFnMode, FanCoilUtil.FAN_HIGH)){
					//通风模式  风速调节没有自动档
					if(StringUtil.equals(changeMode, FanCoilUtil.MODE_FAN)){
						changeFnMode = FanCoilUtil.FAN_LOW;
					}else{
						changeFnMode = FanCoilUtil.FAN_AUTO;
					}
				}else if(StringUtil.equals(changeFnMode, FanCoilUtil.FAN_AUTO)){
					changeFnMode = FanCoilUtil.FAN_LOW;
				}
				mCurFanModeListener.onFanModeChanged(changeFnMode);
				break;
			case R.id.fancoil_energysaving_btn: // 一键节能
				if(StringUtil.equals(mModeState, FanCoilUtil.MODE_HEAT_ENERGY) ||
						StringUtil.equals(mModeState, FanCoilUtil.MODE_COOL_ENERGY)){
					isEnergyOpen = true;
				}else{
					isEnergyOpen = false;
				}
				mEnergySavingBtnListener.onStateChanged(isEnergyOpen);
				break;
			case R.id.fancoil_countdown_btn: // 倒计时开关
				mCountDownBtnListener.onButtonClicked();
				break;
			case R.id.fancoil_state_btn: // 状态开关
				if(StringUtil.equals(mState, FanCoilUtil.STATE_ON)){
					isStateOn = true;
				}else{
					isStateOn = false;
				}
				mCurStateListener.onStateChanged(isStateOn);
				break;
			case R.id.fancoil_program_btn: // 编程模式开关
				if(StringUtil.equals(mProgramState, FanCoilUtil.STATE_ON)){
					isProgramOpen = true;
				}else{
					isProgramOpen = false;
				}
				mProgramBtnListener.onStateChanged(isProgramOpen);
				break;

			default:
				break;
			}

		}
	};

	//模式改变 监听
	public interface CurModeListener {
		public void onModelChanged(String mode);
	}

	//风机状态改变 监听
	public interface CurFanModeListener{
		public void onFanModeChanged(String fnMode);
	}

	//温度改变  监听
	public interface CurTempListener {
		public void onTempChanged(String temp);
	}

	// 状态改变监听  开、关
	public interface CurStateListener {
		public void onStateChanged(boolean isOpen);
	}

	// 编程按钮监听 开、关
	public interface ProgramBtnListener{
		public void onStateChanged(boolean isOpen);
	}

	// 倒计时监听  开、关
	public interface CountDownBtnListener {
		public void onButtonClicked();
	}

	// 一键节能按钮监听  开、关
	public interface EnergySavingBtnListener {
		public void onStateChanged(boolean isOpen);
	}

	//设置倒计时 hour minute
	private void setCountTaskTime(String countThreadTime){
		String time = FloorWarmUtil.hexTime2Time(countThreadTime);
		hourValue = Integer.parseInt(time.substring(0 , 2));
		minuteValue = Integer.parseInt(time.substring(2 , 4));
		secondValue = Integer.parseInt(time.substring(4,6));
	}

	//退出倒计时
	public void cancelTimerTask(){
		if(timer != null){
			if(task != null){
				task.cancel();
			}
		}
	}
	//启动倒计时
	private void startTimerTask(){
		if(timer != null){
			if(task != null){
				task.cancel();
			}
			task = new MyTimerTask();
			timer.schedule(task ,0,1000);
		}
	}

	private void refreshCountDown() {

		if (hourValue == 0) {
			if (minuteValue == 0) {
				if(secondValue == 0){
//				ToastProxy.makeText(mContext, "倒计时时间到", ToastProxy.LENGTH_SHORT).show();
					cancelTimerTask();
				}else{
					secondValue--;
					mCountDownHourTv.setText("0" + hourValue);
					mCountDownMinTv.setText("0" + minuteValue);
				}

			} else {
				mCountDownHourTv.setText("0" + hourValue);
				if(secondValue == 0){
					secondValue = 59;
					minuteValue--;

				}else{
					secondValue--;
				}
				if (minuteValue >= 10) {
					mCountDownMinTv.setText(minuteValue + "");
				} else {
					mCountDownMinTv.setText("0" + minuteValue);
				}

			}

		} else {
			if (minuteValue == 0) {
				if(secondValue == 0){
					secondValue = 59;
					minuteValue = 59;
					hourValue--;
				}else{
					secondValue--;
				}
				if (hourValue >= 10) {
					mCountDownHourTv.setText(hourValue + "");
				} else {
					mCountDownHourTv.setText("0" + hourValue);
				}
				mCountDownMinTv.setText(minuteValue + "");

			} else {
				if(secondValue == 0){
					secondValue = 59;
					minuteValue--;
				}else{
					secondValue--;
				}
				if (hourValue >= 10) {
					mCountDownHourTv.setText(hourValue + "");
				} else {
					mCountDownHourTv.setText("0" + hourValue);
				}
				if (minuteValue >= 10) {
					mCountDownMinTv.setText(minuteValue + "");
				} else {
					mCountDownMinTv.setText("0" + minuteValue);
				}

			}
		}

	}

	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

}

package cc.wulian.app.model.device.impls.controlable.floorwarm;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuantuo.customview.ui.ToastProxy;
import com.yuantuo.customview.ui.WLDialog;

import java.util.Timer;
import java.util.TimerTask;

import cc.wulian.app.model.device.impls.controlable.floorwarm.countdown.FloorWarmCountDownFragment;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;

/**
 * 地暖界面创建器
 * @author hanx
 *
 */

public class FloorWarmViewBulider {
	private static final String TAG = "FloorHeatingViewBulider:";

	private Context mContext;
	private View contentView;

	private LinearLayout mModeLayout;
	private LinearLayout mWarningLayout;
	private TextView mModeTextView;
	private TextView mTempTextView;
	private TextView mTempTextView2;
	private ImageButton mEnergySavingBtn;
	private FloorWarmProgressBar mProgressBar;

	private RelativeLayout mShutDownLayout;
	private Button mShutdownTVBtn;

	private LinearLayout mGroundTempBg;
	private TextView mGroundTempTextView;
	private ImageView mIvWarning;

	private LinearLayout mCountDownLayout;
	private TextView mCountDownHourTv;
	private TextView mCountDownMinTv;
	private TextView mCountDownTypeTv;

	private ImageButton mCountDownBtn;
	private ImageButton mStateBtn;
	private ImageButton mProgramBtn;

	private WLDialog mWarningDialog;

	private CurStateListener mCurStateListener;
	private CurTempListener mCurTempListener;
	private EnergySavingBtnListener mEnergySavingBtnListener;
	private CountDownBtnListener mCountDownBtnListener;
	private ProgramBtnListener mProgramBtnListener;

	private Timer timer ;
	private TimerTask task;
	private int hourValue;
	private int minuteValue;
	private int secondValue;
	private Handler handler;
	private TextView mSecondTv;
	//温控器是否是外部传感器报警
	private boolean isOutsideWarning =false;
	//是否是 内部传感器报警
	private boolean isInsideWarning = false;
	// 内部 外部都报警
	private boolean isSensorWarning = false;
	//是否报警弹窗
	boolean isOutsideDialogShow = true;
	boolean isInsideDialogShow = true;
	boolean isSensorDialogShow = true;

	// 返回标记
	private String mReturnId;
	// 制热温度
	private String mHeatTemp;
	//地面温度
	private String mGroundTemp;
	//过温保护开关
	private String mOverTempState;
	//过温保护温度
	private String mOverTempValue;
	//开关 状态
	private String mState;
	//编程开关 状态
	private String mProgramState;
	//倒计时开关 状态
	private String mCountDownState;
	//倒计时时间
	private String mCountDownTime;
	//一键节能开关 状态
	private String mEnergySavingState;
	//一键节能温度
	private String mEnergySavingTemp;

	//标识 传感器异常  1：内部  2：外部  3：内外部
	private static final int WARNING_INSIDE_TAG = 1;
	private static final int WARNING_OUTSIDE_TAG = 2;
	private static final int WARNING_SENOR_TAG = 3;

	//节能按钮状态 图片
	private static final int DRAWABLE_ENERGY_ON= R.drawable.floorheating_jieneng_on_btn_selector;
	private static final int DRAWABLE_ENERGY_OFF = R.drawable.floorheating_jieneng_off_btn_selector;

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

	//地表温度背景  图片
	private static final int DRAWABLE_GROUND_NORMAL= R.drawable.floorheating_ground_temp_normal;
	private static final int DRAWABLE_GROUND_ABNORMAL = R.drawable.floorheating_ground_temp_abnormal;

	public FloorWarmViewBulider(Context mContext) {
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
		contentView = inflater.inflate(R.layout.device_floorwarm, null);
	}

	public View getContentView(){
		return contentView;
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

		mProgressBar.setOnUpViewValueChanged(new FloorWarmProgressBar.OnUpViewValueChanged() {
			@Override
			public void onUpChanged(String value) {
				mCurTempListener.onTempChanged(value);
			}
		});
	}

	private void initViewData(){

		mModeLayout = (LinearLayout) contentView.findViewById(R.id.floorheating_mode_layout);
		mWarningLayout = (LinearLayout) contentView.findViewById(R.id.floorheating_warning_layout);
		mModeTextView = (TextView) contentView.findViewById(R.id.floorheating_mode_tv);
		mTempTextView = (TextView) contentView.findViewById(R.id.floorheating_temperature_tv);
		mTempTextView2 = (TextView) contentView.findViewById(R.id.floorheating_temperature_tv2);
		mEnergySavingBtn = (ImageButton) contentView.findViewById(R.id.floorheating_jieneng_btn);
		mProgressBar = (FloorWarmProgressBar) contentView.findViewById(R.id.floorheating_ArcProgressBar);
		mShutDownLayout = (RelativeLayout) contentView.findViewById(R.id.floorheating_shundown_layout);
		mShutdownTVBtn = (Button) contentView.findViewById(R.id.floorheating_shutdown_tbtn);
		mGroundTempBg = (LinearLayout) contentView.findViewById(R.id.floorwram_ground_bg);
		mGroundTempTextView = (TextView) contentView.findViewById(R.id.floorwarm_ground_temp_tv);
		mIvWarning = (ImageView) contentView.findViewById(R.id.floorwarm_warning_image);
		mCountDownLayout = (LinearLayout) contentView.findViewById(R.id.floorwarm_countdown_time_layout);
		mCountDownHourTv = (TextView) contentView.findViewById(R.id.floorwarm_countdown_time_hour);
		mCountDownMinTv = (TextView) contentView.findViewById(R.id.floorwarm_countdown_time_min);
		mCountDownTypeTv = (TextView) contentView.findViewById(R.id.floorwarm_countdown_time_type);
		mCountDownBtn = (ImageButton) contentView.findViewById(R.id.floorheating_countdown_btn);
		mStateBtn = (ImageButton) contentView.findViewById(R.id.floorheating_on_off_btn);
		mProgramBtn = (ImageButton) contentView.findViewById(R.id.floorheating_program_btn);

		mEnergySavingBtn.setOnClickListener(mClickListener);
		mCountDownBtn.setOnClickListener(mClickListener);
		mStateBtn.setOnClickListener(mClickListener);
		mProgramBtn.setOnClickListener(mClickListener);
		mSecondTv = (TextView) contentView.findViewById(R.id.floorwarm_countdown_time_second);
	}

	/**
	 * 设置工作状态，倒计时状态，编程状态，一键节能状态,过温保护开关
	 * @param
	 */
	public void setModeState(String state,String countDownState, String programState,String energyState,String overTempState){
		mState = state;
		mCountDownState = countDownState;
		mProgramState = programState;
		mEnergySavingState = energyState;
		mOverTempState = overTempState;
	}
	//设置 是否是外部报警
	public void setOutsideWarning(boolean isOutsideWarning){
		this.isOutsideWarning = isOutsideWarning;
	}

	public void setInsideWarning(boolean insideWarning) {
		isInsideWarning = insideWarning;
	}

	public void setSensorWarning(boolean sensorWarning) {
		isSensorWarning = sensorWarning;
	}

	//传感器异常显示
	public void refreshSensorView(){

		if(isOutsideWarning){
			showOutSideWaring();
			dismissInSideWaring();
			if(isOutsideDialogShow){
				showWarningDialog(WARNING_OUTSIDE_TAG);
				isOutsideDialogShow = false;
			}
		}else if(isInsideWarning){
			dismissOutSideWaring();
			showInSideWaring();
			if(isInsideDialogShow){
				showWarningDialog(WARNING_INSIDE_TAG);
				isInsideDialogShow = false;
			}
		}else if(isSensorWarning){
			showOutSideWaring();
			showInSideWaring();
			if(isSensorDialogShow){
				showWarningDialog(WARNING_SENOR_TAG);
				isSensorDialogShow = false;
			}
		}else{
			dismissInSideWaring();
			dismissOutSideWaring();
		}
	}

	//显示异常提示框
	private void showWarningDialog(int tag){
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(mContext.getResources().getString(R.string.operation_title));
		builder.setSubTitleText(null);
		builder.setContentView(R.layout.device_floorwarm_warning_dialog_content);
		builder.setPositiveButton(mContext.getResources().getString(R.string.common_ok));
		builder.setDismissAfterDone(true);
		builder.setListener(new WLDialog.MessageListener() {
			@Override
			public void onClickPositive(View view) {

			}

			@Override
			public void onClickNegative(View view) {

			}
		});
		mWarningDialog = builder.create();
		TextView tvDialog = (TextView) mWarningDialog.findViewById(R.id.floorwarm_warning_dialog_tv);
		if(tag == WARNING_INSIDE_TAG){
			tvDialog.setText(mContext.getResources().getString(R.string.floor_feating_abnormal_in));
		}else if(tag == WARNING_OUTSIDE_TAG){
			tvDialog.setText(mContext.getResources().getString(R.string.floor_feating_abnormal_out)+"\n" +
					mContext.getResources().getString(R.string.floor_feating_abnormal_out_1)+"\n" +
							mContext.getResources().getString(R.string.floor_feating_abnormal_out_2)+"\n" +
					mContext.getResources().getString(R.string.floor_feating_abnormal_out_3));
		}else if(tag == WARNING_SENOR_TAG){
			tvDialog.setText(mContext.getResources().getString(R.string.floor_feating_abnormal_in_and_out));
		}
		mWarningDialog.show();
	}

	/**
	 * 根据模式设置View显示
	 */
	public void initViewByMode(){

		if(!StringUtil.isNullOrEmpty(mGroundTemp) && !StringUtil.isNullOrEmpty(mOverTempValue)){
			if(!StringUtil.equals(mGroundTemp , "0.0") && !StringUtil.equals(mOverTempValue , "0.0")){

				if(Float.valueOf(mGroundTemp).shortValue() > Float.parseFloat(mOverTempValue)){
					setGroundTempAbnormal();
				}else{
					setGroundTempNormal();
				}
			}
		}

		if(StringUtil.equals(mOverTempState , FloorWarmUtil.STATE_ON)){
			mGroundTempBg.setVisibility(View.VISIBLE);
		}else if(StringUtil.equals(mOverTempState , FloorWarmUtil.STATE_OFF)){
			mGroundTempBg.setVisibility(View.INVISIBLE);
		}

		if(StringUtil.equals(mState, FloorWarmUtil.STATE_ON)){
			if(StringUtil.equals(mEnergySavingState, FloorWarmUtil.STATE_ON)){
				showEnergySavingView();
			}else{
				showMainView();
				if(StringUtil.equals(mProgramState, FloorWarmUtil.STATE_ON)){
					setProgramBtnOn();
				}else{
					setProgramBtnOff();
				}
			}

			if(StringUtil.equals(mCountDownState, FloorWarmUtil.COUNTDOWN_STATE_ON_TAG)
					|| StringUtil.equals(mCountDownState, FloorWarmUtil.COUNTDOWN_STATE_OFF_TAG)){
				setCountDownBtnOn();
			}else{
				setCountDownBtnOff();
				cancelTimerTask();
			}

		}else{
			showOffView();
			if(StringUtil.equals(mCountDownState, FloorWarmUtil.STATE_ON)){
				setCountDownBtnOn();
			}else{
				setCountDownBtnOff();
			}
		}
	}

	//设置 一键节能温度
	public void setEnergyTempValue(String energySavingTemp){
		this.mEnergySavingTemp = energySavingTemp;
	}

	//设置 制热温度
	public void setHeatTempValue(String heatTemp){
		this.mHeatTemp = heatTemp;
	}

	//设置 地面保护温度和过温保护温度
	public void setGroundAndOverTemp(String groundTemp,String overTemp){
		this.mGroundTemp = groundTemp;
		this.mOverTempValue = overTemp;
	}

	/**
	 * 当前温度显示界面
	 * @param  currentTemperature
	 */
	public void setCurTemperatureView(String currentTemperature){

		if(!StringUtil.isNullOrEmpty(currentTemperature) ){
			String s1 =currentTemperature.substring(0, currentTemperature.indexOf("."));
			String s2 =currentTemperature.substring(currentTemperature.lastIndexOf("."),currentTemperature.length());
			mTempTextView.setText(s1);
			mTempTextView2.setVisibility(View.VISIBLE);
			mTempTextView2.setText(s2);
		}
	}

	/**
	 * 地表温度显示界面
	 * @param
	 */
	public void setGroundTemperatureView(String groundTemperature){
			if(StringUtil.equals(mOverTempState , FloorWarmUtil.STATE_ON)){
				mGroundTempBg.setVisibility(View.VISIBLE);
				if(!StringUtil.isNullOrEmpty(groundTemperature) ){
					if(StringUtil.equals(groundTemperature , "100.0")) {
//						if(isOutsideWarning){
						mGroundTempTextView.setText("");
//						}else{
//							mGroundTempTextView.setText("100");
//						}
					}else{
						mGroundTempTextView.setText(groundTemperature);
					}
				}
			}else if(StringUtil.equals(mOverTempState , FloorWarmUtil.STATE_OFF)){
				mGroundTempBg.setVisibility(View.INVISIBLE);
			}

	}

	/**
	 * 设置 温度 进度显示
	 * @param
	 */
	public void setCurProgress() {
		if(!StringUtil.isNullOrEmpty(mEnergySavingState)){
			if(StringUtil.equals(mEnergySavingState, FloorWarmUtil.STATE_ON)){
				setProgressTemp(mEnergySavingTemp);
			}else{
				setProgressTemp(mHeatTemp);
			}
		}
	}

	// 根据温标  设置 拖动条 最大值 最小值  温度显示
	private void setProgressTemp(String temp){
		if(!StringUtil.isNullOrEmpty(temp)){
			if(Double.valueOf(temp) > FloorWarmUtil.PROGRESS_MAX_C){
				temp = (double)32+"";
			}
			if(Double.valueOf(temp) < FloorWarmUtil.PROGRESS_MIN_C){
				temp = (double)10+"";
			}
			mProgressBar.setMaxValue(FloorWarmUtil.PROGRESS_MAX_C);
			mProgressBar.setMinValue(FloorWarmUtil.PROGRESS_MIN_C);
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
		if(StringUtil.equals(mState, FloorWarmUtil.STATE_ON)){
			mCountDownTypeTv.setText(mContext.getResources().getString(R.string.AP_after_off));
		}else{
			mCountDownTypeTv.setText(mContext.getResources().getString(R.string.AP_after_on));
		}
	}

	public void refreshCountDownView(){
		if(!StringUtil.isNullOrEmpty(mCountDownState) && !StringUtil.isNullOrEmpty(mCountDownTime)){

			if(StringUtil.equals(mCountDownState, FloorWarmUtil.COUNTDOWN_STATE_ON_TAG)
					|| StringUtil.equals(mCountDownState, FloorWarmUtil.COUNTDOWN_STATE_OFF_TAG)){
				setCountTaskTime(mCountDownTime);
				startTimerTask();
			}else{
				cancelTimerTask();
			}
		}
	}

	//显示开机状态主页面
	private void showMainView(){
		mModeTextView.setText(mContext.getResources().getString(R.string.AP_get_hot));
		mProgressBar.setVisibility(View.VISIBLE);
		setStateBtnOn();
		setEnergySavingBtnOff();
		mProgressBar.setmEnabled(true);
	}

	//显示关机界面
	private void showOffView(){
		mProgressBar.setVisibility(View.INVISIBLE);
		setStateBtnOff();
		mEnergySavingBtn.setVisibility(View.INVISIBLE);
		setProgramBtnEnabled();
	}

	//显示节能界面
	private void showEnergySavingView(){
		mProgressBar.setVisibility(View.VISIBLE);
		mModeTextView.setText(mContext.getResources().getString(R.string.AP_get_enegry));
		setStateBtnOn();
		setEnergySavingBtnOn();
		mProgressBar.setmEnabled(false);
		setProgramBtnEnabled();
	}

	// 地表温度 正常、异常 显示
	private void setGroundTempNormal(){
		mGroundTempBg.setVisibility(View.VISIBLE);
		mGroundTempBg.setBackgroundResource(DRAWABLE_GROUND_NORMAL);
	}
	private void setGroundTempAbnormal(){
		mGroundTempBg.setVisibility(View.VISIBLE);
		mGroundTempBg.setBackgroundResource(DRAWABLE_GROUND_ABNORMAL);
	}

	//显示倒计时时间
	private void showCountDownView(){
		mCountDownLayout.setVisibility(View.VISIBLE);
	}

	//隐藏倒计时时间
	private void dismissCountDownView(){
		mCountDownLayout.setVisibility(View.INVISIBLE);
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
	private void setProgramBtnEnabled(){
		mProgramBtn.setEnabled(false);
		mProgramBtn.setBackgroundResource(DRAWABLE_PROGRAM_UNABLE);
	}

	//设置节能按钮 开关状态
	private void setEnergySavingBtnOn(){
		mEnergySavingBtn.setVisibility(View.VISIBLE);
		mEnergySavingBtn.setBackgroundResource(DRAWABLE_ENERGY_ON);
	}
	private void setEnergySavingBtnOff(){
		mEnergySavingBtn.setVisibility(View.VISIBLE);
		mEnergySavingBtn.setBackgroundResource(DRAWABLE_ENERGY_OFF);
	}

	//显示内部传感器异常图片
	public void showInSideWaring(){
		mWarningLayout.setVisibility(View.VISIBLE);
		mModeLayout.setVisibility(View.GONE);
	}

	private void dismissInSideWaring(){
		mWarningLayout.setVisibility(View.GONE);
		mModeLayout.setVisibility(View.VISIBLE);
	}

	//显示外部传感器异常图片
	private void showOutSideWaring(){
		mIvWarning.setVisibility(View.VISIBLE);
	}
	private void dismissOutSideWaring(){
		mIvWarning.setVisibility(View.GONE);
	}

	public OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {

			boolean isEnergyOpen;
			boolean isStateOn;
			boolean isProgramOpen;

			switch (view.getId()) {
				case R.id.floorheating_jieneng_btn: // 一键节能
					if(StringUtil.equals(mEnergySavingState, FloorWarmUtil.STATE_ON)){
						isEnergyOpen = true;
					}else{
						isEnergyOpen = false;
					}
					mEnergySavingBtnListener.onStateChanged(isEnergyOpen);
					break;
				case R.id.floorheating_countdown_btn: // 倒计时开关
					mCountDownBtnListener.onButtonClicked();
					break;
				case R.id.floorheating_on_off_btn: // 状态开关
					if(StringUtil.equals(mState, FloorWarmUtil.STATE_ON)){
						isStateOn = true;
					}else{
						isStateOn = false;
					}
					mCurStateListener.onStateChanged(isStateOn);
					break;
				case R.id.floorheating_program_btn: // 编程模式开关
					if(StringUtil.equals(mProgramState, FloorWarmUtil.STATE_ON)){
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
					mSecondTv.setText(secondValue+"");
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
				mSecondTv.setText(secondValue+"");
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
				mSecondTv.setText(secondValue+"");
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
				mSecondTv.setText(secondValue+"");
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

	class MyTimerTask extends TimerTask{
		@Override
		public void run() {
			Message message = new Message();
			message.what = 1;
			handler.sendMessage(message);
		}
	};

}

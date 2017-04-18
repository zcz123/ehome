package cc.wulian.app.model.device.impls.controlable.floorwarm.countdown;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.ToastProxy;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.SendMessage;

public class FloorWarmCountDownFragment extends WulianFragment {

	private final String TAG = getClass().getSimpleName();

	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private String mEpData;
	private DeviceCache cache;
	private AbstractDevice device;

	@ViewInject(R.id.countdown_time_hour_tv)
	private TextView mCountDownHourTv;
	@ViewInject(R.id.countdown_time_min_tv)
	private TextView mCountDownMinTv;
	@ViewInject(R.id.countdown_time_on_off_tv)
	private TextView mCountDowntypeTv;
	@ViewInject(R.id.countdown_time_edit_btn)
	private ImageButton mCountDownEditBtn;
	@ViewInject(R.id.countdown_open_btn)
	private Button mCountDownStartBtn;

	private WLDialog mTimePikerDialog;
	private TimePikerView mTimePikerView;

	/**
	 * 返回标志位
	 */
	private String mReturnId;
	/**
	 * 开关 off:00  on:01
	 */
	private String mOnOff;
	/**
	 * 定时器状态  off:02  on:01
	 */
	private String mCountDownState;
	/**
	 * 定时器倒计时时间
	 */
	private String mCountDownTime ;
	//倒计时默认时间
	private String mCountInitTime ;
	//倒计时编辑时间
	private String mCountEditTime;

	/**
	 * 定时器倒计时时间是否 重新编辑过
	 */
	private boolean isTimeEdited = false;
	//编辑按钮是否点击过
	private boolean isEditBtnClick = false;
	//是否点击启动倒计时按钮
	private boolean isCountBtnClick = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("CountDownFragmentInfo");
		mGwId = bundle.getString(FloorWarmUtil.GWID);
		mDevId = bundle.getString(FloorWarmUtil.DEVID);
		mEp = bundle.getString(FloorWarmUtil.EP);
		mEpType = bundle.getString(FloorWarmUtil.EPTYPE);
		mOnOff = bundle.getString("mOnOff");
		mCountDownState = bundle.getString("mCountDownState");
		mCountDownTime = bundle.getString("mCountDownTime");

		cache= DeviceCache.getInstance(mActivity);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle(mActivity.getResources().getString(R.string.AP_count_down_time));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_floorwarm_count_down, container, false);
		ViewUtils.inject(this,rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FloorWarmUtil.CURRENT_QUERY_CMD_DATA);

		mCountDownEditBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isEditBtnClick = true;
				showTimePikerDialog();
			}
		});

		mCountDownStartBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				//倒计时发送时间
				String mSendCountDownTime = "";
				//倒计时发送指令
				String mCountDownCmd = "";
				if(isTimeEdited){
					//时间编辑过
					mSendCountDownTime = setCountDownTime(mCountEditTime);
				}else{
					String currentTime = new StringBuilder().append(mCountDownHourTv.getText())
							.append(mCountDownMinTv.getText()).toString();
					mSendCountDownTime = setCountDownTime(currentTime);
				}

				if((StringUtil.equals(mCountDownState, FloorWarmUtil.COUNTDOWN_STATE_ON_TAG)
						|| StringUtil.equals(mCountDownState, FloorWarmUtil.COUNTDOWN_STATE_OFF_TAG))&& (!isTimeEdited)){
					mCountDownCmd = FloorWarmUtil.COUNTDOWN_CLOSE + "00";

				}else{
					if(StringUtil.equals(mOnOff, FloorWarmUtil.STATE_ON)){
						mCountDownCmd = FloorWarmUtil.COUNTDOWN_OPEN_STATE_OFF + mSendCountDownTime;
					}else{
						mCountDownCmd = FloorWarmUtil.COUNTDOWN_OPEN_STATE_ON + mSendCountDownTime;
					}
					isTimeEdited = false;
					isEditBtnClick = false;
				}
				Log.i(TAG+"-countcmd", mCountDownCmd);
				SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, mCountDownCmd);
			}
		});
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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	private void loadData(){
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				initView();
			}
		});
	}

	private void initView(){
		//倒计时 时间显示
		if(!StringUtil.isNullOrEmpty(mCountDownTime) && !isEditBtnClick){
			//倒计时 时间 16进制 转为10进制
			String time = FloorWarmUtil.hexTime2Time(mCountDownTime);
			mCountDownHourTv.setText(time.substring(0 , 2));
			mCountDownMinTv.setText(time.substring(2 , 4));
			if(StringUtil.equals(mCountDownState , FloorWarmUtil.COUNTDOWN_OFF_TAG)){
				if(Integer.parseInt(time.substring(0 , 2))==0 && Integer.parseInt(time.substring(2 ,4))<30){
					mCountDownMinTv.setText("30");
				}
			}
		}

		//设置 倒计时类型，开机/关机
		if(!StringUtil.isNullOrEmpty(mOnOff)){
			if(StringUtil.equals(mOnOff, FloorWarmUtil.STATE_ON )){
				mCountDowntypeTv.setText(mApplication.getResources().getString(R.string.AP_after_off));
			}else{
				mCountDowntypeTv.setText(mApplication.getResources().getString(R.string.AP_after_on));
			}
		}
		//倒计时状态  开启/关闭
		if(!StringUtil.isNullOrEmpty(mCountDownState)){
			//倒计时开机
			if(StringUtil.equals(mCountDownState, FloorWarmUtil.COUNTDOWN_STATE_ON_TAG )){

				mCountDownStartBtn.setText(mApplication.getResources().getString(R.string.AP_close_time));
				//倒计时关机
			}else if(StringUtil.equals(mCountDownState, FloorWarmUtil.COUNTDOWN_STATE_OFF_TAG )){

				mCountDownStartBtn.setText(mApplication.getResources().getString(R.string.AP_close_time));
				//倒计时不开启
			} else{
				mCountDownStartBtn.setText(mApplication.getResources().getString(R.string.AP_open_time));
			}
		}

	}

	//时间设置窗口
	private void showTimePikerDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(null);
		builder.setContentView(createViewTime());
		builder.setPositiveButton(mActivity.getResources().getString(R.string.common_ok));
		builder.setNegativeButton(mActivity.getResources().getString(R.string.common_cancel));
		builder.setDismissAfterDone(false);
		builder.setListener(new WLDialog.MessageListener() {
			@Override
			public void onClickPositive(View view) {
				isTimeEdited = true;
				isEditBtnClick = false;
				String hours = mTimePikerView.getSettingHourTime();
				String minutes = mTimePikerView.getSettingMinuesTime();
				Log.i(TAG+"-counttime", "hours:"+hours+",minues:"+minutes);
				if(StringUtil.equals(hours , "00") && StringUtil.equals(minutes ,"00")){
					WLToast.showToast(mActivity,mApplication.getResources().getString(R.string.AP_time_cantBe),WLToast.TOAST_SHORT);
					return;
				}
				mCountDownHourTv.setText(hours);
				mCountDownMinTv.setText(minutes);
				mCountEditTime = hours+minutes;

				//时间重新编辑过  按钮 由关闭 改为 开启
				if(isTimeEdited){
					mCountDownStartBtn.setText(mApplication.getResources().getString(R.string.AP_open_time));
					mCountDownState = FloorWarmUtil.STATE_ON;
				}
				if(mTimePikerDialog != null){
					mTimePikerDialog.dismiss();
				}

			}
			@Override
			public void onClickNegative(View view) {

			}
		});

		mTimePikerDialog = builder.create();
		mTimePikerDialog.show();
	}

	private View createViewTime(){
		mTimePikerView = new TimePikerView(mActivity);
		return mTimePikerView;
	}

	//时间显示 转换为 发送格式
	private String setCountDownTime(String time){
		String sendTime = "";
		if(!StringUtil.isNullOrEmpty(time)){
			int countHour = Integer.parseInt(time.substring(0, 2));
			int countMin = Integer.parseInt(time.substring(2, 4));
			sendTime = (countHour * 2) + (countMin/30) + "";
		}
		return StringUtil.appendLeft(sendTime, 2 , '0');
	}

	public void onEventMainThread(DeviceEvent event){
		device=(AbstractDevice) cache.getDeviceByID(mActivity, event.deviceInfo.getGwID(), event.deviceInfo.getDevID());
		mEpData=device.getDeviceInfo().getDevEPInfo().getEpData();
		Log.i(TAG+"-epdata", mEpData+"-"+mEpData.length());
		handleEpData(mEpData);
		initView();
	}

	private void handleEpData(String epData) {

		if(!StringUtil.isNullOrEmpty(epData)){
			if(epData.length() == 46){
				mReturnId = epData.substring(0, 2);
				mOnOff = epData.substring(2, 4);
				mCountDownState = epData.substring(28, 30);
				mCountDownTime = epData.substring(30, 36);
			}
			if(StringUtil.equals(epData.substring(0,2), FloorWarmUtil.COUNTDOWN_TAG)){
				mReturnId = epData.substring(0, 2);
				mCountDownState = epData.substring(2, 4);
				mCountDownTime = epData.substring(4, 10);
			}
		}
	}

}

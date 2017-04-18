package cc.wulian.app.model.device.impls.configureable.ir;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.configureable.ir.xml.ACCommand;
import cc.wulian.app.model.device.impls.configureable.ir.xml.ACCommand.FanSpeed;
import cc.wulian.app.model.device.impls.configureable.ir.xml.ACCommand.Mode;
import cc.wulian.app.model.device.impls.configureable.ir.xml.ACCommand.Power;
import cc.wulian.app.model.device.impls.configureable.ir.xml.ACCommand.Temp;
import cc.wulian.app.model.device.view.AnimationUtil;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.dao.IRDao;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class EpDataAirView extends AbstractEpDataView{

	private View mModeControlView;
	private View mFanSetView;
	private View mPowerSetView;
	private View mPlusTempView;
	private View mSubTempView;
	private TextView mCurrentTempView;
	private TextView mSetTempView;
	private ImageView mModeShowView;
	private ImageView mFanSpeedView;
	private int mCurrentModeIndex = -1;
	private int mCurrentFanIndex = -1;
	private int mCurrentPowerIndex = -1;
	private int mCurrentTempIndex = -1;
	private IRDao irDao = IRDao.getInstance();
	private DeviceIRInfo deviceIRInfo;
	private final View.OnClickListener mClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick( View v ) {
			ACCommand cmd = null;
			if (v == mModeControlView) {
				boolean over = Mode.checkOverRange(mCurrentModeIndex);
				if (over) {
					mCurrentModeIndex = 0;
				}
				cmd = Mode.getCommand(mCurrentModeIndex);
				updateMode(cmd);
				mCurrentModeIndex++;
			}
			else if (v == mFanSetView) {
				boolean over = FanSpeed.checkOverRange(mCurrentFanIndex);
				if (over) {
					mCurrentFanIndex = 0;
				}
				cmd = FanSpeed.getCommand(mCurrentFanIndex);
				updateFanSet(cmd);
				mCurrentFanIndex++;
			}
			else if (v == mPowerSetView) {
				boolean over = Power.checkOverRange(mCurrentPowerIndex);
				if (over) {
					mCurrentPowerIndex = 0;
				}
				cmd = Power.getCommand(mCurrentPowerIndex);
				updatePower(cmd);
				mCurrentPowerIndex++;
			}
			else if (v == mPlusTempView) {
				mCurrentTempIndex++;
				boolean over = Temp.checkOverRange(mCurrentTempIndex);
				if (over) {
					mCurrentTempIndex = 0;
				}
				cmd = Temp.getCommand(mCurrentTempIndex);
				updateTemp(cmd);
			}
			else if (v == mSubTempView) {
				mCurrentTempIndex--;
				boolean over = Temp.checkOverRange(mCurrentTempIndex);
				if (over) {
					mCurrentTempIndex = 0;
				}
				cmd = Temp.getCommand(mCurrentTempIndex);
				updateTemp(cmd);
			}

			if (cmd != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(CmdUtil.IR_MODE_CTRL);
				sb.append(getType());
				sb.append(deviceIRInfo.getKeyset());
				sb.append(cmd.getCmd());
				String sendData = sb.toString();
				setEpData(sendData,true);
			}
		}
	};

	public EpDataAirView(Context context, DeviceInfo info,String epData) {
		super(context, info,epData);
		
	}
	@Override
	public View onCreateView() {
		rootView =  inflater.inflate(R.layout.device_thermostat, null);
		return rootView;
	}

	private void setEpData(String epData,boolean isFire){
		if(epData!= null && epData.length()>=9){
			clearStates();
			String cmd = epData.substring(6,9);
			String cmdType = cmd.substring(0,1);
			String data = cmd.substring(1,3);
			if("4".equals(cmdType)){
				mPowerSetView.setSelected(true);
			}else if("5".equals(cmdType)){
				mModeControlView.setSelected(true);
			}else if("7".equals(cmdType)){
				mFanSetView.setSelected(true);
			}else if("8".equals(cmdType)){
				
			}
			if(isFire)
				fireSelectEpDataListener(epData);
		}
	}
	private void clearStates(){
		mPowerSetView.setSelected(false);
		mModeControlView.setSelected(false);
		mFanSetView.setSelected(false);
		
	}
	@Override
	public void onViewCreated(View view) {
		mModeControlView = view.findViewById(R.id.imageView_mode_set);
		mModeControlView.setOnClickListener(mClickListener);

		mFanSetView = view.findViewById(R.id.imageView_fan_set);
		mFanSetView.setOnClickListener(mClickListener);

		mPowerSetView = view.findViewById(R.id.imageView_power);
		mPowerSetView.setOnClickListener(mClickListener);

		mPlusTempView = view.findViewById(R.id.imageView_temp_plus);
		mPlusTempView.setOnClickListener(mClickListener);

		mSubTempView = view.findViewById(R.id.imageView_temp_sub);
		mSubTempView.setOnClickListener(mClickListener);

		mCurrentTempView = (TextView) view.findViewById(R.id.textView_current_temp);
		mSetTempView = (TextView) view.findViewById(R.id.textView_set_temp);
		mModeShowView = (ImageView) view.findViewById(R.id.imageView_current_mode);
		mFanSpeedView = (ImageView) view.findViewById(R.id.imageView_fan_speed);
		mModeShowView.setImageDrawable(resources.getDrawable(R.drawable.common_mode_auto));
		loadData();
		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mGestureDetector.onTouchEvent(event);
				return true;
			}
		});
		setEpData(epData,false);
	}
	
	@Override
	public String getType() {
		return TYPE_AIR_CONDITION;
	}
	public void loadData() {
		DeviceIRInfo info = new DeviceIRInfo();
		info.setGwID(deviceInfo.getGwID());
		info.setDeviceID(deviceInfo.getDevID());
		if(deviceInfo.getDevEPInfo() != null){
			info.setEp(deviceInfo.getDevEPInfo().getEp());
		}else{
			info.setEp(WulianDevice.EP_14);
		}
		info.setIRType(getType());
		deviceIRInfo = irDao.getById(info);
		if(deviceIRInfo == null){
			deviceIRInfo = info;
			deviceIRInfo.setIRType(getType());
			deviceIRInfo.setKeyset(CmdUtil.IR_GENERAL_KEY_DEFAULT);
		}
	}
	private void updateMode( ACCommand cmd ) {
		String cmdStr = cmd.getCmd();
		if(cmdStr == null || cmdStr.length() < 2) return;
		
		String mode = cmdStr.substring(1);
		Drawable modeDrawable = resources.getDrawable(R.drawable.common_mode_auto);
		if (Mode.COOL.equals(mode)) {
			modeDrawable = resources.getDrawable(R.drawable.common_mode_cool);
		}
		else if (Mode.DEF.equals(mode)) {
			modeDrawable = resources.getDrawable(R.drawable.common_mode_deh);
		}
		else if (Mode.FAN.equals(mode)) {
			modeDrawable = resources.getDrawable(R.drawable.common_mode_air_supply);
		}
		else if (Mode.HOT.equals(mode)) {
			modeDrawable = resources.getDrawable(R.drawable.common_mode_hot);
		}
		mModeShowView.setImageDrawable(modeDrawable);
	}

	private void updateFanSet( ACCommand cmd ) {
		String speedStr = cmd.getCmd();
		if(speedStr == null || speedStr.length() < 2) return;
		
		String speed = speedStr.substring(1);
		int speedIcon;
		if (FanSpeed.AUTO.equals(speed)) {
			speedIcon = R.drawable.common_mode_auto;
		}
		else {
			speedIcon = R.drawable.common_mode_fan_speed;
		}
		mFanSpeedView.setImageResource(speedIcon);
		Animation animation = AnimationUtil.getRotateAnimation(StringUtil.toInteger(speed) * 800);
		mFanSpeedView.setAnimation(animation);
	}

	private void updatePower( ACCommand cmd ) {
		String powerCmd = cmd.getCmd();
		if(Power.ON.equals(powerCmd)){
			mPowerSetView.setPressed(true);
		}else if(Power.OFF.equals(powerCmd)){
			mPowerSetView.setPressed(false);
		}
	}

	private void updateTemp( ACCommand cmd ) {
		mSetTempView.setText(cmd.getCmdDescription());
	}
}

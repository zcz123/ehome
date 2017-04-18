package cc.wulian.app.model.device.impls.configureable.ir;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.configureable.ir.xml.ACCommand;
import cc.wulian.app.model.device.impls.configureable.ir.xml.ACCommand.FanSpeed;
import cc.wulian.app.model.device.impls.configureable.ir.xml.ACCommand.Mode;
import cc.wulian.app.model.device.impls.configureable.ir.xml.ACCommand.Power;
import cc.wulian.app.model.device.impls.configureable.ir.xml.ACCommand.Temp;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.view.AnimationUtil;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.dao.IRDao;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLToast;

public class IRAirView extends AbstractIRView{
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
	private WulianDevice device;
	private IRDao irDao = IRDao.getInstance();
	private DeviceIRInfo deviceIRInfo;
	private final View.OnClickListener mClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick( View v ) {
			if (device == null || !device.isDeviceOnLine()) {
				WLToast.showToast(mContext, resources.getString(R.string.device_offline), WLToast.TOAST_SHORT);
				return;
			}
			if(deviceIRInfo == null){
				WLToast.showToast(mContext, resources.getString(R.string.device_no_config), WLToast.TOAST_SHORT);
				return;
			}
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
				sb.append(deviceIRInfo.getKeyset());
				sb.append(cmd.getCmd());

				String sendData = sb.toString();
				SendMessage.sendControlDevMsg(
						deviceIRInfo.getGwID(), 
						deviceIRInfo.getDeviceID(),
						deviceIRInfo.getEp(),
						deviceInfo.getType(),
						sendData);
			}
		}
	};

	public IRAirView(Context context, DeviceInfo info) {
		super(context, info);
		device = DeviceCache.getInstance(mContext).getDeviceByID(mContext, deviceInfo.getGwID(), deviceInfo.getDevID());
	}

	@Override
	public View onCreateView() {
		return inflater.inflate(R.layout.device_thermostat, null);
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
	}
	@Override
	public void reloadData() {
		DeviceIRInfo info = new DeviceIRInfo();
		info.setGwID(deviceInfo.getGwID());
		info.setDeviceID(deviceInfo.getDevID());
		if(deviceInfo.getDevEPInfo() != null){
			info.setEp(deviceInfo.getDevEPInfo().getEp());
		}else{
			info.setEp(WulianDevice.EP_14);
		}
		info.setIRType(getType());
		DeviceIRInfo resultInfo = irDao.getById(info);
		if(resultInfo == null)
			return ;
		if(deviceIRInfo == null)
			deviceIRInfo = resultInfo;
		else{
			deviceIRInfo.setCode(resultInfo.getCode());
			deviceIRInfo.setKeyset(resultInfo.getKeyset());
			deviceIRInfo.setEp(resultInfo.getEp());
			deviceIRInfo.setIRType(resultInfo.getIRType());
		}
	}

	@Override
	public String getType() {
		return TYPE_AIR_CONDITION;
	}

	@Override
	public Intent getSettingIntent() {
		DeviceIRInfo info = this.deviceIRInfo;
		if(info == null){
			info = new DeviceIRInfo();
			info.setDeviceID(device.getDeviceID());
			info.setGwID(device.getDeviceGwID());
			info.setIRType(getType());
			if(deviceInfo.getDevEPInfo() != null){
				info.setEp(deviceInfo.getDevEPInfo().getEp());
			}else{
				info.setEp(WulianDevice.EP_14);
			}
		}
		Intent intent = new Intent(mContext,DeviceSettingActivity.class);
		intent.putExtra(EditIRAirFragment.EXTRA_DEVICE_IR_AIR, info);
		intent.putExtra(AbstractDevice.SETTING_LINK_TYPE, AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, EditIRAirFragment.class.getName());
		return intent ;
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
	}

	private void updateTemp( ACCommand cmd ) {
		mSetTempView.setText(cmd.getCmdDescription());
	}
}

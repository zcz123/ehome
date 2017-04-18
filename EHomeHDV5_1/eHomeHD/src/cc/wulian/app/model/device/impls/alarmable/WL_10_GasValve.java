package cc.wulian.app.model.device.impls.alarmable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 * 位1:报警状态(0:正常,1:报警) <br/>
 * 位2:阀状态(0:关,1:开)
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_GAS_VALVE }, category = Category.C_SECURITY)
public class WL_10_GasValve extends AlarmableDeviceImpl implements Controlable 
{
	private static final String DATA_CTRL_STATE_CLOSE_0 = "0";
	private static final String DATA_CTRL_STATE_OPEN_1 = "1";

	private static final int SMALL_NORMAL_OPEN_1 = R.drawable.device_gas_valve_open;
	private static final int SMALL_ALARM_OPEN_2 = R.drawable.device_gas_valve_open_alarm;
	private static final int SMALL_NORMAL_CLOSE_1 = R.drawable.device_gas_valve_close;
	private static final int SMALL_ALARM_CLOSE_2 = R.drawable.device_gas_valve_close_alarm;

	private static final int BIG_NORMAL_OPEN_1 = R.drawable.device_gas_valve_open_icon;
	private static final int BIG_NORMAL_CLOSE_1 = R.drawable.device_gas_valve_close_icon;

	private ProgressBar mSecurityAlarm;
	private ImageView mSecurityNormal;
	
	private FrameLayout mSecuritySetUp;
	private FrameLayout mSecuritySwitch;
	private ImageView mSecuritySetOn;
	private ImageView mSecuritySetOff;

	public WL_10_GasValve(Context context, String type) {
		super(context, type);
	}


	@Override
	public boolean isAlarming() {
		if (isNull(epData) || epData.length() < 2)
			return false;

		return isSameAs(getAlarmProtocol(), substring(epData, 0, 1));
	}

	@Override
	public boolean isNormal() {
		if (isNull(epData) || epData.length() < 2)
			return true;

		return isSameAs(getNormalProtocol(), substring(epData, 0, 1));
	}

	@Override
	public String getAlarmProtocol() {
		return DATA_ALARM_STATE_ALARM_1;
	}

	@Override
	public String getNormalProtocol() {
		return DATA_ALARM_STATE_NORMAL_0;
	}


	@Override
	public boolean isStoped() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getStopSendCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStopProtocol() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public boolean isOpened() {
		if (isNull(epData) || epData.length() < 2)
			return false;

		return isSameAs(getOpenProtocol(), substring(epData, 1));
	}

	@Override
	public boolean isClosed() {
		if (isNull(epData) || epData.length() < 2)
			return true;

		return isSameAs(getCloseProtocol(), substring(epData, 1));
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_CTRL_STATE_OPEN_1;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_CTRL_STATE_CLOSE_0;
	}
	
	@Override
	public String getOpenProtocol() {
		return getOpenSendCmd();
	}

	@Override
	public String getCloseProtocol() {
		return getCloseSendCmd();
	}
	
	
//	 @Override
//	 public String controlDevice(String sendData){
//	 return !isNull(sendData) ? sendData :isOpened() ? getCloseSendCmd() : isClosed() ? getOpenSendCmd() :sendData;
//	 }

	@Override
	public Drawable getStateSmallIcon() {
		Drawable drawable = null;
		if (isOpened() && isAlarming()) {
			drawable = getDrawable(SMALL_ALARM_OPEN_2);
		} else if (isOpened() && isNormal()) {
			drawable = getDrawable(SMALL_NORMAL_OPEN_1);
		} else if (isClosed() && isAlarming()) {
			drawable = getDrawable(SMALL_ALARM_CLOSE_2);
		} else if (isClosed() && isNormal()) {
			drawable = getDrawable(SMALL_NORMAL_CLOSE_1);
		}
		// 09.12 add else default judge avoid null exception
		// but normally not come here
		else {
			drawable = getDrawable(SMALL_NORMAL_CLOSE_1);
		}
		return drawable;
	}

	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		int res = BIG_NORMAL_CLOSE_1;
		if (isOpened())
			res = BIG_NORMAL_OPEN_1;
		Drawable normalStateDrawable = getDrawable(res);
		drawables[0] = normalStateDrawable;
		return drawables;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_security_alarm_common_layout,
				container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);

		mSecurityAlarm = (ProgressBar) view
				.findViewById(R.id.device_security_alarm);
		mSecurityNormal = (ImageView) view
				.findViewById(R.id.device_security_normal);
		mSecuritySetUp = (FrameLayout) view.findViewById(R.id.device_alarm_security);
		mSecuritySetUp.setVisibility(View.GONE);
		mSecuritySwitch = (FrameLayout) view.findViewById(R.id.device_switch_security);
		mSecuritySwitch.setVisibility(View.VISIBLE);
		mSecuritySetOn = (ImageView) view
				.findViewById(R.id.device_security_on);
		mSecuritySetOff = (ImageView) view
				.findViewById(R.id.device_security_off);
		mSecuritySetOn.setOnClickListener(setUpGasOnClickListener);
		mSecuritySetOff.setOnClickListener(setUpGasOnClickListener);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();

		Drawable drawable = getStateBigPictureArray()[0];
		mSecurityNormal.setImageDrawable(drawable);
		drawable = mSecurityNormal.getDrawable();

		
		if(isOpened()){
			mSecuritySetOn.setVisibility(View.GONE);
			mSecuritySetOff.setVisibility(View.VISIBLE);
		}else{
			mSecuritySetOn.setVisibility(View.VISIBLE);
			mSecuritySetOff.setVisibility(View.GONE);
		}
		if (isNormal()) {
			mSecurityAlarm.setVisibility(View.GONE);
		} else if (isAlarming()) {
			mSecurityAlarm.setVisibility(View.VISIBLE);
		}
		
	}

	private View.OnClickListener setUpGasOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.device_security_on:
				controlDeviceWidthEpData(EP_14, epType, "1");
				break;
			case R.id.device_security_off:
				controlDeviceWidthEpData(EP_14, epType, "0");
				break;
			default:
				break;
			}
		}

	};

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater) {
		return getContrableShortCutView(item, inflater);
	}
}

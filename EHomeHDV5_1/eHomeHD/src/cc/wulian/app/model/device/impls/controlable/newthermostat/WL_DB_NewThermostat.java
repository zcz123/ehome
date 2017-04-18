package cc.wulian.app.model.device.impls.controlable.newthermostat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_THERMOSTAT_DB }, category = Category.C_CONTROL)
public class WL_DB_NewThermostat extends ControlableDeviceImpl {

	private static final String DATA_CTRL_STATE_PREFIX_X = "X"; // 命令类型

	private static final String DATA_CTRL_STATE_UNIT_CELSIUS = "℃"; // 摄氏度单位
	private static final String DATA_CTRL_STATE__UNIT_FAHRENHEIT = "℉"; // 华氏度单位

	// eheat
	private static final String DATA_CTRL_STATE_EHEAT_0 = "0"; // disable
	private static final String DATA_CTRL_STATE_EHEAT_1 = "1"; // enable

	// fnmode
	private static final String DATA_CTRL_STATE_FNMODE_MANUAL_1 = "1"; // manual模式
	private static final String DATA_CTRL_STATE_FNMODE_AUTO_2 = "2"; // auto模式
	private static final String DATA_CTRL_STATE_FNMODE_HEATONLY_3 = "3"; // 只制热模式
	private static final String DATA_CTRL_STATE_FNMODE_COOLONLY_4 = "4"; // 只制冷模式
	private static final String DATA_CTRL_STATE_FNMODE_AO_5 = "5"; // ao模式

	// mode
	private static final String DATA_CTRL_STATE_MODE_OFF_0 = "0"; // off
	private static final String DATA_CTRL_STATE_MODE_HEAT_1 = "1"; // heat
	private static final String DATA_CTRL_STATE_MODE_COOL_2 = "2"; // cool
	private static final String DATA_CTRL_STATE_MODE_AUTO_3 = "3"; // auto
	private static final String DATA_CTRL_STATE_MODE_EHEAT_4 = "4"; // eheat

	// fan
	private static final String DATA_CTRL_STATE_FAN_AUTOMATIC_1 = "1"; // automatic
	private static final String DATA_CTRL_STATE_FAN_ALWAYSON_2 = "2"; // alwayson

	// status
	private static final String DATA_CTRL_STATE_STATUS_OFF_0 = "0"; // off
	private static final String DATA_CTRL_STATE_STATUS_HEATING_1 = "1"; // heating
	private static final String DATA_CTRL_STATE_STATUS_COOLING_2 = "2"; // cooling

	// hold
	private static final String DATA_CTRL_STATE_HOLD_0 = "0"; // disable
	private static final String DATA_CTRL_STATE_HOLD_1 = "1"; // enable

	private String mYear;
	private String mMonth;
	private String mDay;
	private String mWeek; // 星期0-6
	private String mHour; // 24小时制动
	private String mMinute;
	private String mUnit; // 温度单位：0摄氏度，1华氏度
	private double mHeatlimit;
	private double mCoollimit;
	private double mHeatpoint;
	private double mCoolpoint;
	private double mTemperature; // 当前温度
	private String mEheat; // 是否为辅热
	private String mFnmode; // Fn模式 manual,auto,heatonly,coolonly,ao
	private String mMode; // 模式 off,heat,cool,auto,eheat
	private String mFan; // 风扇模式 automatic,always on
	private String mStatus; // 状态 off,heating,cooling
	private String mHold; // enable disbale

	private String mLastEpData;

	private NewThermostatViewBuilder mBuilder;

	private static final int SMALL_MODE_HEAT_D = R.drawable.device_thermost_open_mode_hot;
	private static final int SMALL_MODE_COOL_D = R.drawable.device_thermost_open_mode_cool;
	private static final int SMALL_MODE_FAN_D = R.drawable.device_thermost_open_mode_auto;
	private static final int SMALL_MODE_CLOSE_D = R.drawable.device_thermost_close;

	public WL_DB_NewThermostat(Context context, String type) {
		super(context, type);
	}

	/**
	 * 要修改
	 */
	@Override
	public Drawable getStateSmallIcon() {
		Drawable drawable;
		return getDrawable(SMALL_MODE_HEAT_D);
	}

	@Override
	public void refreshDevice() {
		super.refreshDevice();
		disassembleCompoundCmd(epData);
	}

	private void disassembleCompoundCmd(String epData) {
		if (isNull(epData))
			return;
		Log.i(getClass().getSimpleName(), "模式:" + epData);
		if (epData.length() < 4)
			return;
		if (!epData.startsWith(DATA_CTRL_STATE_PREFIX_X))
			return;

		// same data, no need disassemble
		if (isSameAs(epData, mLastEpData))
			return;

		String[] messageArrays = substring(epData, 1, epData.length()).split(
				" ");
		if (messageArrays.length != 18)
			return;

		mYear = messageArrays[0];
		mMonth = messageArrays[1];
		mDay = messageArrays[2];
		mWeek = messageArrays[3];
		mHour = messageArrays[4];
		mMinute = messageArrays[5];
		mUnit = messageArrays[6];
		mHeatlimit = Double.valueOf(messageArrays[7]);
		mCoollimit = Double.valueOf(messageArrays[8]);
		mHeatpoint = Double.valueOf(messageArrays[9]);
		mCoolpoint = Double.valueOf(messageArrays[10]);
		mTemperature = Double.valueOf(messageArrays[11]);
		mEheat = messageArrays[12];
		mFnmode = messageArrays[13];
		mMode = messageArrays[14];
		mFan = messageArrays[15];
		mStatus = messageArrays[16];
		mHold = messageArrays[17];

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		mBuilder = new NewThermostatViewBuilder(inflater.getContext());
		return mBuilder.getContentView();
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		mBuilder.initThermostat();
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		// if (isCtrlModePowerOff()) { // 关闭模式
		// showOffView();
		// } else {
		// if (isSameAs(DATA_CTRL_STATE_FNMODE_MANUAL_1, mFnmode)) {
		// showModeManualView(isCtrlModeEheatOn());
		// } else if (isSameAs(DATA_CTRL_STATE_FNMODE_AUTO_2, mFnmode)) {
		// showModeAutoView(isCtrlModeEheatOn());
		// } else if (isSameAs(DATA_CTRL_STATE_FNMODE_HEATONLY_3, mFnmode)) {
		// showModeHeatonlyView(isCtrlModeEheatOn());
		// } else if (isSameAs(DATA_CTRL_STATE_FNMODE_COOLONLY_4, mFnmode)) {
		// showModeCoolonlyView(isCtrlModeEheatOn());
		// } else if (isSameAs(DATA_CTRL_STATE_FNMODE_AO_5, mFnmode)) {
		// showModeAoView(isCtrlModeEheatOn());
		// }
		initThermostatView();
		// }

	}

	private void initThermostatView() {
		initModeView();
		initSetTemperature();
		initSymbol();
		initRoomTemperature();
	}

	private void initModeView() {
		mBuilder.setFnMode(StringUtil.equals(DATA_CTRL_STATE_EHEAT_1, mEheat),
				mFnmode, mMode);
		mBuilder.initModeView();
	}

	// 初始化室内温度
	private void initRoomTemperature() {
		if (mTemperature == 0) {
			return;
		}
		mBuilder.setRoomTemperature(mTemperature);
	}

	// 初始化温度单位
	private void initSymbol() {
		if (mUnit == null) {
			return;
		}
		mBuilder.setSymbol(mUnit);
	}

	// 初始化温度
	private void initSetTemperature() {
		if (mFnmode == null) {
			return;
		}
		mBuilder.setHeatLimit(mHeatlimit);
		mBuilder.setCoolLimit(mCoolpoint);
		mBuilder.setHeatPoint(mHeatpoint);
		mBuilder.setCoolPoint(mCoolpoint);
		mBuilder.setTemperature();

	}

	// /**
	// * 显示manual模式界面
	// *
	// * @param isEheatOn
	// */
	// private void showModeManualView(boolean isEheatOn) {
	// mBuilder.setFnMode(StringUtil.equals(DATA_CTRL_STATE_EHEAT_1, mEheat),
	// mFnmode, mMode);
	// mBuilder.initModeView();
	// }
	//
	// /**
	// * 显示auto模式界面
	// *
	// * @param isEheatOn
	// */
	// private void showModeAutoView(boolean isEheatOn) {
	// mBuilder.setFnMode(StringUtil.equals(DATA_CTRL_STATE_EHEAT_1, mEheat),
	// mFnmode, mMode);
	// mBuilder.initModeView();
	// }
	//
	// /**
	// * 显示heatonlyl模式界面
	// *
	// * @param isEheatOn
	// */
	// private void showModeHeatonlyView(boolean isEheatOn) {
	//
	// }
	//
	// /**
	// * 显示coolonly模式界面
	// *
	// * @param isEheatOn
	// */
	// private void showModeCoolonlyView(boolean isEheatOn) {
	//
	// }
	//
	// /**
	// * 显示ao模式界面
	// *
	// * @param isEheatOn
	// */
	// private void showModeAoView(boolean isEheatOn) {
	//
	// }
	//
	// /**
	// * 温控器关闭的界面
	// */
	// private void showOffView() {
	// }

	private boolean isCtrlModeEheatOn() {
		return isSameAs(DATA_CTRL_STATE_EHEAT_1, mEheat);
	}

	public boolean isCtrlModePowerOff() {
		return isSameAs(DATA_CTRL_STATE_STATUS_OFF_0, mStatus);
	}

}

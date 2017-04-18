package cc.wulian.app.model.device.impls.controlable.thermostat;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.impls.controlable.thermostat.ThermostatViewBuilder.CurModelListener;
import cc.wulian.app.model.device.impls.controlable.thermostat.ThermostatViewBuilder.CurSwitchListener;
import cc.wulian.app.model.device.impls.controlable.thermostat.ThermostatViewBuilder.CurTempListener;
import cc.wulian.app.model.device.impls.controlable.thermostat.ThermostatViewBuilder.CurWindSpeedListener;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_THERMOSTAT }, category = Category.C_CONTROL)
public class WL_78_Thermostat extends ControlableDeviceImpl {
	private static final String DATA_CTRL_STATE_PREFIX_0 = "0";

	// fan speed
	private static final String DATA_CTRL_STATE_PREFIX_FAN_SPEED_2 = "2";

	// control mode
	private static final String DATA_CTRL_STATE_PREFIX_CONTROL_MODE_3 = "3";

	private static final String DATA_CTRL_STATE_CONTROL_MODE_0 = "0";
	private static final String DATA_CTRL_STATE_CONTROL_MODE_00 = createCompoundCmd(
			DATA_CTRL_STATE_PREFIX_0, DATA_CTRL_STATE_CONTROL_MODE_0);

	private static final String DATA_CTRL_STATE_CONTROL_MODE_1 = "1";
	private static final String DATA_CTRL_STATE_CONTROL_MODE_01 = createCompoundCmd(
			DATA_CTRL_STATE_PREFIX_0, DATA_CTRL_STATE_CONTROL_MODE_1);

	private static final String DATA_CTRL_STATE_CONTROL_MODE_2 = "2";
	private static final String DATA_CTRL_STATE_CONTROL_MODE_02 = createCompoundCmd(
			DATA_CTRL_STATE_PREFIX_0, DATA_CTRL_STATE_CONTROL_MODE_2);

	private static final String DATA_CTRL_STATE_CONTROL_MODE_9 = "9";
	private static final String DATA_CTRL_STATE_CONTROL_MODE_09 = createCompoundCmd(
			DATA_CTRL_STATE_PREFIX_0, DATA_CTRL_STATE_CONTROL_MODE_9);

	// temp
	private static final String DATA_CTRL_STATE_PREFIX_HOT_TEMP_4 = "4";
	private static final String DATA_CTRL_STATE_PREFIX_COOL_TEMP_5 = "5";

	// refresh state
	private static final String DATA_CTRL_STATE_REFRESH_6 = "6";
	private static final String DATA_CTRL_STATE_REFRESH_06 = createCompoundCmd(
			DATA_CTRL_STATE_PREFIX_0, DATA_CTRL_STATE_REFRESH_6);

	private static final int SMALL_MODE_HEAT_D = R.drawable.device_thermost_open_mode_hot;
	private static final int SMALL_MODE_COOL_D = R.drawable.device_thermost_open_mode_cool;
	private static final int SMALL_MODE_FAN_D = R.drawable.device_thermost_open_mode_auto;
	private static final int SMALL_MODE_CLOSE_D = R.drawable.device_thermost_close;

	private ThermostatViewBuilder builder;
	private String mLastEpData;
	private String mControlMode;
	private int mFanSpeed;
	private String mTempSign;
	private float mCurrentTempValue;
	private int mCoolTemp;
	private int mHotTemp;

	private static final int DATA_CTRL_FAN_SPEED_MIN = 0;
	private static final int DATA_CTRL_FAN_SPEED_ONE = 1;
	private static final int DATA_CTRL_FAN_SPEED_TWO = 2;
	private static final int DATA_CTRL_FAN_SPEED_THREE = 3;
	private static final int DATA_CTRL_FAN_SPEED_MAX = 4;

	private static final int DATA_CTRL_MODE_MIN = 0;
	private static final int DATA_CTRL_MODE_ONE = 1;
	private static final int DATA_CTRL_MODE_MAX = 2;

	@Override
	public void refreshDevice() {
		super.refreshDevice();
		disassembleCompoundCmd(epData);
	}

	private void disassembleCompoundCmd(String epData) {
		if (isNull(epData))
			return;
		if (epData.length() < 4)
			return;
		if (!epData.startsWith(DATA_CTRL_STATE_REFRESH_06))
			return;

		// same data, no need disassemble
		if (isSameAs(epData, mLastEpData))
			return;

		mControlMode = substring(epData, 2, 4);
		mFanSpeed = StringUtil.toInteger(substring(epData, 4, 6), 16);
		mTempSign = substring(epData, 6, 8);
		mCurrentTempValue = StringUtil.toInteger(substring(epData, 8, 12), 16) / 10.0F;
		mCoolTemp = StringUtil.toInteger(substring(epData, 12, 14), 16);
		mHotTemp = StringUtil.toInteger(substring(epData, 14, 16), 16);

	}

	public WL_78_Thermostat(Context context, String type) {
		super(context, type);

	}

	@Override
	public String getOpenProtocol() {
		return getOpenSendCmd();
	}

	@Override
	public String getCloseProtocol() {
		return getCloseSendCmd();
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_CTRL_STATE_CONTROL_MODE_01;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_CTRL_STATE_CONTROL_MODE_00;
	}

	@Override
	public boolean isOpened() {
		return !isClosed();
	}

	@Override
	public boolean isClosed() {
		return isCtrlModePowerOff();
	}

	public boolean isCtrlModeHot() {
		return isSameAs(DATA_CTRL_STATE_CONTROL_MODE_00, mControlMode);
	}

	public boolean isCtrlModeCool() {
		return isSameAs(DATA_CTRL_STATE_CONTROL_MODE_01, mControlMode);
	}

	public boolean isCtrlModeFan() {
		return isSameAs(DATA_CTRL_STATE_CONTROL_MODE_02, mControlMode);
	}

	public boolean isCtrlModePowerOff() {
		return isSameAs(DATA_CTRL_STATE_CONTROL_MODE_09, mControlMode);
	}

	@Override
	public Drawable getStateSmallIcon() {
		Drawable drawable;
		if (isCtrlModeHot()) {
			drawable = getDrawable(SMALL_MODE_HEAT_D);
		} else if (isCtrlModeCool()) {
			drawable = getDrawable(SMALL_MODE_COOL_D);
		} else if (isCtrlModeFan()) {
			drawable = getDrawable(SMALL_MODE_FAN_D);
		} else if (isCtrlModePowerOff()) {
			drawable = getDrawable(SMALL_MODE_CLOSE_D);
		} else {
			drawable = getDrawable(SMALL_MODE_FAN_D);
		}
		return drawable;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		builder = new ThermostatViewBuilder(inflater.getContext());
		return builder.getContentView();
	}

	private void dismissOrShowCtrlView(boolean isOpen) {
		builder.setSwitchOpen(isOpen);
		builder.initSwitchStatus();
	}

	public void initWindSpeedView() {
		builder.setCurWindSpeed(mFanSpeed);
		builder.initWindSpeed();
	}

	public void initCurrentModelView() {
		if (mControlMode != null) {
			int curModel = StringUtil.toInteger(mControlMode.substring(1, 2));
			builder.setCurModel(curModel);
		}
		builder.initCurModel();
	}

	public void initProgressBar() {
		builder.setShowSettingTemp(true);
		if (mHotTemp == mCoolTemp) {
			builder.setCurProgress(mHotTemp);
		}
	}

	private void initSpeedShowState() {
		builder.setCurWindSpeed(mFanSpeed);
		builder.initSpeedShow();
	}

	private void showTempValue() {
		builder.setCurTemp(mCurrentTempValue);
		builder.setmTempSign(mTempSign);
		builder.initCurTemp();

	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		builder.initThermostatView();
		builder.setCurTempListener(new CurTempListener() {

			@Override
			public void onTempChanged(int temp) {
				if (isCtrlModeHot()) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(ep, epType, DATA_CTRL_STATE_PREFIX_HOT_TEMP_4
							+ String.valueOf(temp));
				} else if (isCtrlModeCool()) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(
							ep,
							epType,
							DATA_CTRL_STATE_PREFIX_COOL_TEMP_5
									+ String.valueOf(temp));
				}
			}
		});
		builder.setCurWindSpeedListener(new CurWindSpeedListener() {

			@Override
			public void onWindSpeedChanged(int speed) {
				if (ThermostatViewBuilder.WIND_SPEED_0 == speed) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(ep, epType,
							DATA_CTRL_STATE_PREFIX_FAN_SPEED_2
									+ DATA_CTRL_FAN_SPEED_MIN);
				} else if (ThermostatViewBuilder.WIND_SPEED_1 == speed) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(ep, epType,
							DATA_CTRL_STATE_PREFIX_FAN_SPEED_2
									+ DATA_CTRL_FAN_SPEED_ONE);
				} else if (ThermostatViewBuilder.WIND_SPEED_2 == speed) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(ep, epType,
							DATA_CTRL_STATE_PREFIX_FAN_SPEED_2
									+ DATA_CTRL_FAN_SPEED_TWO);

				} else if (ThermostatViewBuilder.WIND_SPEED_3 == speed) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(ep, epType,
							DATA_CTRL_STATE_PREFIX_FAN_SPEED_2
									+ DATA_CTRL_FAN_SPEED_THREE);
				} else if (ThermostatViewBuilder.WIND_SPEED_4 == speed) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(ep, epType,
							DATA_CTRL_STATE_PREFIX_FAN_SPEED_2
									+ DATA_CTRL_FAN_SPEED_MAX);
				}

			}
		});
		builder.setCurModelListener(new CurModelListener() {

			@Override
			public void onModelChanged(int model) {
				if (ThermostatViewBuilder.CUR_MODEL_HOT_0 == model) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(ep, epType,
							DATA_CTRL_STATE_PREFIX_CONTROL_MODE_3
									+ DATA_CTRL_MODE_MIN);
				} else if (ThermostatViewBuilder.CUR_MODEL_COOL_1 == model) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(ep, epType,
							DATA_CTRL_STATE_PREFIX_CONTROL_MODE_3
									+ DATA_CTRL_MODE_ONE);
				} else if (ThermostatViewBuilder.CUR_MODEL_FAN_2 == model) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(ep, epType,
							DATA_CTRL_STATE_PREFIX_CONTROL_MODE_3
									+ DATA_CTRL_MODE_MAX);
				}

			}
		});
		builder.setCurSwitchListener(new CurSwitchListener() {

			@Override
			public void oSwitchChanged(boolean open) {
				if (open) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(ep, epType, DATA_CTRL_STATE_CONTROL_MODE_01);
				} else {
					fireWulianDeviceRequestControlSelf();
					controlDevice(ep, epType, DATA_CTRL_STATE_CONTROL_MODE_00);
				}

			}
		});
		fireWulianDeviceRequestControlSelf();
		controlDevice(ep, epType, DATA_CTRL_STATE_REFRESH_6);

	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		if (isCtrlModePowerOff()) {
			dismissOrShowCtrlView(false);
			initSpeedShowState();
		} else {
			dismissOrShowCtrlView(true);
			showTempValue();
			initProgressBar();
			initCurrentModelView();
			initWindSpeedView();
			initSpeedShowState();
		}
	}
}

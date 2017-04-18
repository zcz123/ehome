package cc.wulian.app.model.device.impls.controlable.thermostat_cooperation;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_THERMOSTAT_O6 }, category = Category.C_CONTROL)
public class WL_O6_thermostat_custom extends ControlableDeviceImpl {
	private static final String CONTROL_ORDER_MARK = "#";
	private static final String CONTROL_ORDER_HEAD_2 = "2";
	private static final String CONTROL_QUERY_STATUS_1A = "1A#";
	private static final String CONTROL_SWITCH_STATUS_CLOSE_10 = "10#";
	private static final String CONTROL_SWITCH_STATUS_OPEN_11 = "11#";

	private static final int SMALL_STATUS_CLOSE = R.drawable.device_thermost_close;
	private static final int SMALL_STATUS_OPEN = R.drawable.device_thermost_open;

	private static final String STATUS_CALLBACK_AA = "AA";
	private static final String STATUS_REPORT_ALARM_05 = "05";
	private static final String STATUS_REPORT_CUR_TEMP_03 = "03";
	private static final String STATUS_REPORT_LOCAL_04 = "04";
	private static final String STATUS_REPORT_SET_TEMP_02 = "02";
	private static final String STATUS_REPORT_SWITCH_01 = "01";
	private static final String STATUS_SWITCH_CLOSE_00 = "00";
	private static final String STATUS_SWITCH_OPEN_01 = "01";

	private String alarmData;
	private TextView alarmStatus;
	private String curTempData;
	private Button getTemp;

	private CooMyArcProgressBar progressBar;
	private TextView setTemp;
	private String setTempData;
	private String switchData;
	private Button switchStatus;
	private String valveData;
	private TextView valveStatus;
	private Thread queryThread;

	public WL_O6_thermostat_custom(Context context, String type) {
		super(context, type);
	}

	@Override
	public void refreshDevice() {
		super.refreshDevice();
		if (queryThread == null) {
			queryThread = new Thread(new Runnable() {

				@Override
				public void run() {
					controlDevice(EP_14, epType, CONTROL_QUERY_STATUS_1A);
				}
			});
			queryThread.start();
		}
		disassembleCompoundCmd(epData);
	}

	private void disassembleCompoundCmd(String epData) {
		try {
			if (StringUtil.isNullOrEmpty(epData)) {
				return;
			} else if ((epData.startsWith(STATUS_REPORT_SWITCH_01))
					&& (epData.length() == 4)) {
				switchData = epData.substring(2, 4);
			} else if ((epData.startsWith(STATUS_REPORT_SET_TEMP_02))
					&& (epData.length() == 6)) {
				setTempData = epData.substring(2, 6);
			} else if ((epData.startsWith(STATUS_REPORT_CUR_TEMP_03))
					&& (epData.length() == 8)) {
				curTempData = epData.substring(2, 8);
			} else if ((epData.startsWith(STATUS_REPORT_LOCAL_04))
					&& (epData.length() == 10)) {
				switchData = epData.substring(2, 4);
				valveData = epData.substring(4, 6);
				setTempData = epData.substring(6, 10);
			} else if ((epData.startsWith(STATUS_REPORT_ALARM_05))
					&& (epData.length() == 4)) {
				alarmData = epData.substring(2, 4);
			} else if ((epData.startsWith(STATUS_CALLBACK_AA))) {
				switchData = epData.substring(2, 4);
				valveData = epData.substring(4, 6);
				curTempData = epData.substring(6, 12);
				setTempData = epData.substring(12, 16);
				alarmData = epData.substring(16, 18);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_thermostat_custom, null);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		progressBar = ((CooMyArcProgressBar) view
				.findViewById(R.id.device_thermostat_custom_arcprogressbar));
		getTemp = ((Button) view
				.findViewById(R.id.device_thermostat_custom_get_temp));
		setTemp = ((TextView) view
				.findViewById(R.id.device_thermostat_custom_set_temp));
		alarmStatus = ((TextView) view
				.findViewById(R.id.device_thermostat_custom_alarm_switch_tv));
		valveStatus = ((TextView) view
				.findViewById(R.id.device_thermostat_custom_valve_switch_tv));
		switchStatus = ((Button) view
				.findViewById(R.id.device_thermostat_custom_switch));
		progressBar
				.setOnUpViewValueChanged(new CooArcProgressBar.OnUpViewValueChanged() {
					public void onUpChanged(int paramInt) {
						String str = String.valueOf(paramInt + 5);
						setTemp.setText(getResources().getString(
								R.string.device_set_tempure)
								+ str + "°C");
						if (isOpened()) {
							if (paramInt < 10) {
								str = StringUtil.appendLeft(str, 2, '0');
							}
							fireWulianDeviceRequestControlSelf();
							controlDevice(EP_14, epType, CONTROL_ORDER_HEAD_2
									+ str + CONTROL_ORDER_MARK);
						}
					}
				});
		progressBar
				.setOnMoveViewValueChanged(new CooArcProgressBar.OnMoveViewValueChanged() {
					public void onMoveChanged(int paramInt) {
						setTemp.setText(getResources().getString(
								R.string.device_set_tempure)
								+ (paramInt + 5) + "°C");
					}
				});
		switchStatus.setOnClickListener(mClickListener);
		fireWulianDeviceRequestControlSelf();
		controlDevice(EP_14, epType, CONTROL_QUERY_STATUS_1A);
	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (arg0 == switchStatus) {
				if (isOpened()) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(EP_14, epType, CONTROL_SWITCH_STATUS_CLOSE_10);
				} else {
					fireWulianDeviceRequestControlSelf();
					controlDevice(EP_14, epType, CONTROL_SWITCH_STATUS_OPEN_11);
				}
			}
		}
	};

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		initCurTemp();
		initCurStatus();
		if (isOpened()) {
			progressBar
					.setBackgroundResource(R.drawable.device_thermost_temp_bg_2);
		} else {
			progressBar.setClickable(false);
			progressBar
					.setBackgroundResource(R.drawable.device_thermost_temp_bg_1);
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void initCurStatus() {
		if (isOpened()) {
			switchStatus.setText(getResources().getString(
					R.string.default_progress_off));
		} else {
			switchStatus.setText(getResources().getString(
					R.string.default_progress_on));
		}
		if (isValveOpened()) {

			valveStatus.setText(getResources().getString(
					R.string.device_state_open));
		} else {
			valveStatus.setText(getResources().getString(
					R.string.device_state_close));
		}
		if (isAlarming()) {
			alarmStatus.setText(getResources().getString(
					R.string.device_state_alarm));
			alarmStatus.setBackground(mContext.getResources().getDrawable(
					R.drawable.device_thermostart_alarm_bg));
		} else {
			alarmStatus.setText(getResources().getString(
					R.string.device_state_normal));
			alarmStatus.setBackground(mContext.getResources().getDrawable(
					R.drawable.device_thermostart_switch_bg));
		}
	}

	private void initCurTemp() {
		if (!StringUtil.isNullOrEmpty(setTempData)) {
			StringBuffer sb = new StringBuffer();
			if (!STATUS_SWITCH_CLOSE_00.equals(setTempData.substring(0, 2))) {
				sb.append(setTempData.substring(1, 2));
			}
			sb.append(setTempData.substring(3, 4));
			setTemp.setText(getResources().getString(
					R.string.device_set_tempure)
					+ sb + "°C");
			progressBar.setProcess(StringUtil.toInteger(sb.toString()));
		}
		if (!StringUtil.isNullOrEmpty(curTempData)) {
			StringBuffer sb = new StringBuffer();
			sb.append(curTempData.substring(1, 2));
			sb.append(curTempData.substring(3, 4));
			sb.append(".");
			sb.append(curTempData.substring(5, 6));
			getTemp.setText(sb);
		}
	}

	private boolean isAlarming() {
		if (!StringUtil.isNullOrEmpty(alarmData)) {
			if (isSameAs(STATUS_SWITCH_OPEN_01, alarmData)) {
				return true;
			} else if (isSameAs(STATUS_SWITCH_CLOSE_00, alarmData)) {
				return false;
			}
		}
		return false;

	}

	private boolean isValveOpened() {
		if (!StringUtil.isNullOrEmpty(valveData)) {
			if (isSameAs(STATUS_SWITCH_OPEN_01, valveData)) {
				return true;
			} else if (isSameAs(STATUS_SWITCH_CLOSE_00, valveData)) {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean isOpened() {
		return isSameAs(STATUS_SWITCH_OPEN_01, switchData);
	}

	@Override
	public boolean isClosed() {
		return !isOpened();
	}

	@Override
	public Drawable getStateSmallIcon() {
		Drawable drawable = getDrawable(SMALL_STATUS_CLOSE);
		if (isOpened()) {
			drawable = getDrawable(SMALL_STATUS_OPEN);
		}
		return drawable;
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
		return CONTROL_SWITCH_STATUS_OPEN_11;
	}

	@Override
	public String getCloseSendCmd() {
		return CONTROL_SWITCH_STATUS_CLOSE_10;
	}
}

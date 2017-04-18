package cc.wulian.app.model.device.impls.alarmable.onetranslator;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.alarmable.AlarmableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.SendMessage;

import com.yuantuo.customview.wheel.toc.ArrayWheelAdapter;
import com.yuantuo.customview.wheel.toc.OnWheelScrollListener;
import com.yuantuo.customview.wheel.toc.WheelView;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_ONETRANSLATOR }, category = Category.C_OTHER)
public class WL_B9_One_Wried_Wireless_Translator extends AlarmableDeviceImpl {

	// 跳转时当前开关状态 
	private String isOpen = null;
	private String defaultOutputStatus;

	public static final String KEY_ALARM_EP_DATA14 = "key_alarm_ep_data14";
	public static final String KEY_SWITCH_EP_DATA14 = "key_switch_ep_data14";

	// 端口1控制：2，表示查询信号输入方式 。端口2控制：3MTT，3表示设置报警输出，M表示输出标志
	private static final String DATA_PORT_1_QUERY_2 = "2";
	private static final String DATA_PORT_2_CTRL_OPEN_3000 = "3000";
	private static final String DATA_PORT_2_CTRL_CLOSE_3100 = "3100";
	private static final String DATA_PORT_2_CTRL_OPEN_CONTINUE_33 = "33";
	private static final String DATA_PORT_2_CTRL_CLOSE_CONTINUE_34 = "34";

	// 010K:端口1数据，01表示设置信号输入标识;0K表示输入方式(00为闭路输入,01为开路输入);
	private static final String DATA_PORT_1_SET_INPUT_CLOSE_0100 = "0100";
	private static final String DATA_PORT_1_SET_INPUT_OPEN_0101 = "0101";

	// 020K:端口1数据，02表示查询信号输入标识;0K表示输入方式(00为闭路输入,01为开路输入);
	private static final String DATA_PORT_1_QUERY_INPUT_CLOSE_0200 = "0200";
	private static final String DATA_PORT_1_QUERY_INPUT_OPEN_0201 = "0201";

	// 030MTT:端口2数据，03表示设置报警数据。0M表示告警输出状态
	private static final String DATA_PORT_2_SET_ALARM_OUTPUT_OPEN_STATUS_030000 = "030000";
	private static final String DATA_PORT_2_SET_ALARM_OUTPUT_CLOSE_STATUS_030100 = "030100";

	// 040N:端口1数据，04表示报警状态标识;0N表示报警状态(00为正常,01为报警);
	private static final String DATA_ALARM_STATE_NORMAL_0400 = "0400";
	private static final String DATA_ALARM_STATE_ALARM_0401 = "0401";

	// 6，读取默认输出方式
	private static final String DATA_PORT_2_QUERY_6 = "6";
	// button
	private Button inputbtn;
	private Button outputbtn;

	private LinearLayout inputLayout;
	private LinearLayout outputLayout;

	// input
	private ImageView inputSwitchPng;
	private ProgressBar mProgressBar;
	private ImageView defenseStatus;

	// output
	private ImageView outputSwitchPng;

	// continueOpen
	private TextView continueOpen;
	private SeekBar openSeekbar;
	private TextView openTimes;
	private Button openBtn;

	// continueClose
	private TextView continueClose;
	private SeekBar closeSeekbar;
	private TextView closeTimes;
	private Button closeBtn;

	// child dev name
	private TextView childDevName1;
	private TextView childDevName2;

	private ImageView mOutputOpenSwitch;
	private ImageView mOutputCloseSwitch;

	public WL_B9_One_Wried_Wireless_Translator(Context context, String type) {
		super(context, type);
	}

	@Override
	public boolean isDefenseSetup() {
		return isSameAs(DEFENSE_STATE_SET_1, getChildDevice(EP_14).getDeviceInfo().getDevEPInfo().getEpStatus());
	}
	@Override
	public boolean isAlarming() {
		boolean result = false;
		for (WulianDevice device : getChildDevices().values()) {
			DeviceEPInfo info = device.getDeviceInfo().getDevEPInfo();
			if (info.getEpStatus().equals("1")) {
				if (info.getEpData().endsWith(getAlarmProtocol())) {
					result = true;
				}
			}
		}
		return result;

	}

	@Override
	protected boolean isMultiepDevice() {
		return true;
	}

	@Override
	public boolean isNormal() {
		return !isAlarming();

	}

	@Override
	public String getAlarmProtocol() {
		return DATA_ALARM_STATE_ALARM_0401;
	}

	@Override
	public String getNormalProtocol() {
		return DATA_ALARM_STATE_NORMAL_0400;
	}

	/**
	 * 设备上线调用此方法 只调用一次
	 */
	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		super.onDeviceUp(devInfo);
	}

	/**
	 * 数据刷新操作
	 */
	@Override
	public synchronized void onDeviceData(String gwID, String devID,
			DeviceEPInfo devEPInfo,String cmd,String mode) {
		String ep = devEPInfo.getEp();
		WulianDevice device = getChildDevice(ep);
		if (device != null) {
			device.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
			removeCallbacks(mRefreshStateRunnable);
			post(mRefreshStateRunnable);
			fireDeviceRequestControlData();
		} else {
			super.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
		}
	}
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			if (arg0 == defenseStatus) {
				fireWulianDeviceRequestControlSelf();
				controlDevice(EP_14, getChildDevice(EP_14).getDeviceInfo().getDevEPInfo().getEpType(), null);
			} else if (arg0 == inputbtn) {
				inputbtn.setBackgroundResource(R.drawable.device_one_wried_wireless_input_btn_pressed);
				outputbtn
						.setBackgroundResource(R.drawable.device_one_wried_wireless_output_btn_normal);
				inputLayout.setVisibility(View.VISIBLE);
				outputLayout.setVisibility(View.GONE);

			} else if (arg0 == outputbtn) {
				inputbtn.setBackgroundResource(R.drawable.device_one_wried_wireless_input_btn_normal);
				outputbtn
						.setBackgroundResource(R.drawable.device_one_wried_wireless_output_btn_pressed);
				inputLayout.setVisibility(View.GONE);
				outputLayout.setVisibility(View.VISIBLE);

			} else if (arg0 == mOutputOpenSwitch) {
				fireWulianDeviceRequestControlSelf();
				SendMessage.sendControlDevMsg(gwID, devID, EP_15, "A2",
						DATA_PORT_2_CTRL_OPEN_3000);

			} else if (arg0 == mOutputCloseSwitch) {
				fireWulianDeviceRequestControlSelf();
				SendMessage.sendControlDevMsg(gwID, devID, EP_15, "A2",
						DATA_PORT_2_CTRL_CLOSE_3100);

			} else if (arg0 == openBtn) {
				int i = openSeekbar.getProgress();
				if (i > 0 && i < 100) {
					String str = String.valueOf(i);
					String TT = null;
					if (str.length() == 1) {
						TT = "0" + str;
					} else if (str.length() == 2) {
						TT = str;
					}
					fireWulianDeviceRequestControlSelf();
					SendMessage.sendControlDevMsg(gwID, devID, EP_15, "A2",
							DATA_PORT_2_CTRL_OPEN_CONTINUE_33 + TT);

				}
			} else if (arg0 == closeBtn) {
				int mcloseSeekbar = closeSeekbar.getProgress();
				if (mcloseSeekbar > 0 && mcloseSeekbar < 100) {
					String str = String.valueOf(mcloseSeekbar);
					String TT = null;
					if (str.length() == 1) {
						TT = "0" + str;
					} else if (str.length() == 2) {
						TT = str;
					}
					fireWulianDeviceRequestControlSelf();
					SendMessage.sendControlDevMsg(gwID, devID, EP_15, "A2",
							DATA_PORT_2_CTRL_CLOSE_CONTINUE_34 + TT);

				}
			}

		}

	};

	private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			if (arg0 == openSeekbar) {
				openTimes.setText(arg0.getProgress() + "s");

			} else if (arg0 == closeSeekbar) {
				closeTimes.setText(arg0.getProgress() + "s");

			}

		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {

		}

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

		}

	};

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_one_wired_wireless_translator,
				null);
	};

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);

		inputbtn = (Button) view
				.findViewById(R.id.device_one_wried_wireless_input_btn);
		inputbtn.setOnClickListener(mOnClickListener);
		outputbtn = (Button) view
				.findViewById(R.id.device_one_wried_wireless_output_btn);
		outputbtn.setOnClickListener(mOnClickListener);
		inputLayout = (LinearLayout) view
				.findViewById(R.id.device_one_wried_wireless_input);
		outputLayout = (LinearLayout) view
				.findViewById(R.id.device_one_wried_wireless_output);

		// input
		inputSwitchPng = (ImageView) view
				.findViewById(R.id.device_one_wried_wireless_switch_png_input);
		mProgressBar = (ProgressBar) view
				.findViewById(R.id.device_one_wried_wireless_alarm);
		defenseStatus = (ImageView) view
				.findViewById(R.id.device_one_wried_wireless_ctrl_defense);
		defenseStatus.setOnClickListener(mOnClickListener);

		// output
		outputSwitchPng = (ImageView) view
				.findViewById(R.id.device_one_wried_wireless_switch_png_output);
		mOutputOpenSwitch = (ImageView) view
				.findViewById(R.id.device_one_wried_wireless_output_switch_open);
		mOutputOpenSwitch.setOnClickListener(mOnClickListener);
		mOutputCloseSwitch = (ImageView) view
				.findViewById(R.id.device_one_wried_wireless_output_switch_close);
		mOutputCloseSwitch.setOnClickListener(mOnClickListener);

		// continueOpen
		continueOpen = (TextView) view
				.findViewById(R.id.device_one_wried_wireless_continue_open);
		continueOpen.setText(getResources().getString(
				R.string.device_one_translator_continue_open));
		openSeekbar = (SeekBar) view
				.findViewById(R.id.device_one_wried_wireless_seekbar_open);
		openSeekbar.setOnSeekBarChangeListener(mSeekBarChangeListener);
		openTimes = (TextView) view
				.findViewById(R.id.device_one_wried_wireless_continue_open_times);
		openBtn = (Button) view
				.findViewById(R.id.device_one_wried_wireless_continue_open_btn_sure);
		openBtn.setOnClickListener(mOnClickListener);

		// continueClose
		continueClose = (TextView) view
				.findViewById(R.id.device_one_wried_wireless_continue_close);
		continueClose.setText(getResources().getString(
				R.string.device_one_translator_continue_close));
		closeSeekbar = (SeekBar) view
				.findViewById(R.id.device_one_wried_wireless_seekbar_close);
		closeTimes = (TextView) view
				.findViewById(R.id.device_one_wried_wireless_continue_close_times);
		closeSeekbar.setOnSeekBarChangeListener(mSeekBarChangeListener);

		closeBtn = (Button) view
				.findViewById(R.id.device_one_wried_wireless_continue_close_btn_sure);
		closeBtn.setOnClickListener(mOnClickListener);

		// 子设备名称
		childDevName1 = (TextView) view
				.findViewById(R.id.device_one_wried_wireless_child_bind1);
		childDevName1.setText(getChildDevice(EP_14).getDeviceInfo()
				.getDevEPInfo().getEpName());
		childDevName2 = (TextView) view
				.findViewById(R.id.device_one_wried_wireless_child_bind2);
		childDevName2.setText(getChildDevice(EP_15).getDeviceInfo()
				.getDevEPInfo().getEpName());
		// 2，表示查询信号输入方式
		SendMessage.sendControlDevMsg(gwID, devID, EP_14, "A1", DATA_PORT_1_QUERY_2);
		// 端口2控制：6，读取默认输出方式
		SendMessage.sendControlDevMsg(gwID, devID, EP_15, "A2", DATA_PORT_2_QUERY_6);

	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		DeviceEPInfo ep14 = getChildDevice(EP_14).getDeviceInfo().getDevEPInfo();

		// 设备第一次上线并没有epStatus,需发送撤防命令后服务器才会存储epStatus
		if (StringUtil.isNullOrEmpty(ep14.getEpStatus())) {
			controlDevice(EP_14, getChildDevice(EP_14).getDeviceInfo().getDevEPInfo().getEpType(), DEFENSE_STATE_UNSET_0);

		} 
		if (DEFENSE_STATE_SET_1.equals(ep14.getEpStatus())) {

			defenseStatus.setImageDrawable(getResources().getDrawable(
					R.drawable.device_one_wried_wireless_defense));
			if (ep14.getEpData() != null
					&& ep14.getEpData().equals(DATA_ALARM_STATE_NORMAL_0400)) {
				mProgressBar.setVisibility(View.INVISIBLE);

			} else if (ep14.getEpData() != null
					&& ep14.getEpData().equals(DATA_ALARM_STATE_ALARM_0401)) {
				mProgressBar.setVisibility(View.VISIBLE);
			}

		} else{
			defenseStatus.setImageDrawable(getResources().getDrawable(
					R.drawable.device_one_wried_wireless_undefense));
			mProgressBar.setVisibility(View.INVISIBLE);

		}

		if (!StringUtil.isNullOrEmpty(ep14.getEpData())) {
			if (ep14.getEpData().startsWith("02")
				|| ep14.getEpData().startsWith("01")) {
				if (ep14.getEpData().equals(DATA_PORT_1_QUERY_INPUT_OPEN_0201)) {
					inputSwitchPng.setImageDrawable(getResources().getDrawable(
							R.drawable.device_one_wried_wireless_switch_open));
					isOpen = "o";
	
				} else if (ep14.getEpData().equals(
						DATA_PORT_1_QUERY_INPUT_CLOSE_0200)) {
					inputSwitchPng.setImageDrawable(getResources().getDrawable(
							R.drawable.device_one_wried_wireless_switch_close));
					isOpen = "c";
	
				} else if (ep14.getEpData().equals(DATA_PORT_1_SET_INPUT_OPEN_0101)) {
					inputSwitchPng.setImageDrawable(getResources().getDrawable(
							R.drawable.device_one_wried_wireless_switch_open));
					isOpen = "o";
	
				} else if (ep14.getEpData()
						.equals(DATA_PORT_1_SET_INPUT_CLOSE_0100)) {
					inputSwitchPng.setImageDrawable(getResources().getDrawable(
							R.drawable.device_one_wried_wireless_switch_close));
					isOpen = "c";
	
				}
			}
		}

		// 端口2数据，03表示设置报警数据 0M表示告警输出状态
		String ep15Data = getChildDevice(EP_15).getDeviceInfo()
				.getDevEPInfo().getEpData();
		if (StringUtil.isNullOrEmpty(ep15Data)) {
			return;

		} else if (ep15Data.startsWith("03")) {
			if (ep15Data
					.equals(DATA_PORT_2_SET_ALARM_OUTPUT_OPEN_STATUS_030000)) {
				outputSwitchPng.setImageDrawable(getResources().getDrawable(
						R.drawable.device_one_wried_wireless_switch_open));

			} else if (ep15Data
					.equals(DATA_PORT_2_SET_ALARM_OUTPUT_CLOSE_STATUS_030100)) {
				outputSwitchPng.setImageDrawable(getResources().getDrawable(
						R.drawable.device_one_wried_wireless_switch_close));

			} else if (ep15Data.startsWith("0303") && ep15Data.length() >= 6) {

				String openstr = ep15Data.substring(4, 6);
				int iopen = StringUtil.toInteger(openstr, 16);
				openSeekbar.setProgress(iopen);
				openTimes.setText(openSeekbar.getProgress() + "s");

				outputSwitchPng.setImageDrawable(getResources().getDrawable(
						R.drawable.device_one_wried_wireless_switch_open));

			} else if (ep15Data.startsWith("0304") && ep15Data.length() >= 6) {

				String closestr = ep15Data.substring(4, 6);
				int iclose = StringUtil.toInteger(closestr, 16);
				closeSeekbar.setProgress(iclose);
				closeTimes.setText(closeSeekbar.getProgress() + "s");

				outputSwitchPng.setImageDrawable(getResources().getDrawable(
						R.drawable.device_one_wried_wireless_switch_close));

			}

		} else if ((ep15Data.startsWith("05") || ep15Data.startsWith("06"))
				&& ep15Data.length() == 4) {
			defaultOutputStatus = ep15Data.substring(2, 4);
		}

	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(
			DeviceShortCutControlItem item, LayoutInflater inflater) {
		return getDefaultShortCutControlView(item, inflater);
	}

	
	public Intent getSettingIntent() {
		Intent intent = new Intent(mContext, DeviceSettingActivity.class);
		intent.putExtra(DeviceOneTranslatorFragment.GWID, gwID);
		intent.putExtra(DeviceOneTranslatorFragment.DEVICEID, devID);
		intent.putExtra(DeviceOneTranslatorFragment.ISOPEN, isOpen);
		intent.putExtra(DeviceOneTranslatorFragment.DEFAULT_STATUS, defaultOutputStatus);
		intent.putExtra(AbstractDevice.SETTING_LINK_TYPE,
				AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME,
				DeviceOneTranslatorFragment.class.getName());
		return intent;
	}
	
	@Override
	protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(cc.wulian.smarthomev5.R.string.set_titel));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				Intent i = getSettingIntent();
				mContext.startActivity(i);
				manager.dismiss();
			}
		};
		if(isDeviceOnLine())
			items.add(settingItem);
		return items;
	}
	
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, final AutoActionInfo autoActionInfo) {
		String epData = autoActionInfo.getEpData();
		if (epData == null)
			epData = "";
		View view = inflater
				.inflate(
						cc.wulian.smarthomev5.R.layout.scene_task_control_data_one_translator,
						null);
		final WheelView onetype = (WheelView) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_one_translator_one_type);
		final SeekBar oneSeekBar = (SeekBar) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_one_translator_one_seekbar);
		final TextView onevalue = (TextView) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_one_translator_one_value);

		String[] keytype = new String[] {
				getResources().getString(R.string.device_two_output_often_open),
				getResources().getString(R.string.device_state_open),
				getResources().getString(R.string.device_state_close),
				getResources()
						.getString(R.string.device_two_output_often_close) };

		TOCAdapter mAdapter = new TOCAdapter(mContext, keytype);
		onetype.setVisibleItems(4);
		onetype.setViewAdapter(mAdapter);
		onetype.setCurrentItem(1);
		oneSeekBar.setEnabled(false);

		oneSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				int mSeekProgress = oneSeekBar.getProgress();
				onevalue.setText(mSeekProgress + "s");
				String epDataOne = String.valueOf(initSendDataType(onetype.getCurrentItem())
						+ StringUtil.appendLeft(mSeekProgress + "", 2, '0'));
				autoActionInfo.setEpData("3" + epDataOne);
				autoActionInfo.changeEpAndEpType(EP_15,childDeviceMap.get(EP_15).getDeviceInfo().getDevEPInfo().getEpType() );
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

			}
		});

		onetype.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				if (onetype.getCurrentItem() == 1
						| onetype.getCurrentItem() == 2) {
					oneSeekBar.setProgress(0);
					onevalue.setText(0 + "s");
					oneSeekBar.setEnabled(false);

				} else if (onetype.getCurrentItem() == 0
						| onetype.getCurrentItem() == 3) {
					oneSeekBar.setProgress(0);
					onevalue.setText(0 + "s");
					oneSeekBar.setEnabled(true);

				}
				String epDataOne = String.valueOf(initSendDataType(onetype.getCurrentItem())
						+ StringUtil.appendLeft(oneSeekBar.getProgress() + "", 2, '0'));
				autoActionInfo.setEpData("3" + epDataOne);
				autoActionInfo.changeEpAndEpType(EP_15,childDeviceMap.get(EP_15).getDeviceInfo().getDevEPInfo().getEpType() );
			}
		});

		if (epData.startsWith("3") && epData.length() >= 4) {
			String oneType = epData.substring(1, 2);
			onetype.setCurrentItem(initReciveDataType(StringUtil.toInteger(oneType)));
			if (onetype.getCurrentItem() == 0 || onetype.getCurrentItem() == 3) {
				oneSeekBar.setEnabled(true);
			} else {
				oneSeekBar.setEnabled(false);
			}
			String oneValue = epData.substring(2, 4);
			if (oneValue.startsWith("0")) {
				oneValue = oneValue.substring(1, 2);
			}
			onevalue.setText(oneValue + "s");
			oneSeekBar.setProgress(StringUtil.toInteger(oneValue));

		}
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		holder.setContentView(view);
		holder.setShowDialog(true);
		holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}
	
	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,
			String ep, String epData) {
		if (!EP_15.equals(ep))
			return null;
		if (epData == null)
			epData = "";
		View view = inflater
				.inflate(
						cc.wulian.smarthomev5.R.layout.scene_task_control_data_one_translator,
						null);
		linkTaskControlEPData = new StringBuffer(epData);

		final WheelView onetype = (WheelView) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_one_translator_one_type);
		final SeekBar oneSeekBar = (SeekBar) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_one_translator_one_seekbar);
		final TextView onevalue = (TextView) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_one_translator_one_value);

		String[] keytype = new String[] {
				getResources().getString(R.string.device_two_output_often_open),
				getResources().getString(R.string.device_state_open),
				getResources().getString(R.string.device_state_close),
				getResources()
						.getString(R.string.device_two_output_often_close) };

		TOCAdapter mAdapter = new TOCAdapter(mContext, keytype);
		onetype.setVisibleItems(4);
		onetype.setViewAdapter(mAdapter);
		onetype.setCurrentItem(1);
		oneSeekBar.setEnabled(false);

		oneSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				int mSeekProgress = oneSeekBar.getProgress();
				onevalue.setText(mSeekProgress + "s");
				String epDataOne = String.valueOf(initSendDataType(onetype.getCurrentItem())
						+ StringUtil.appendLeft(mSeekProgress + "", 2, '0'));
				linkTaskControlEPData = new StringBuffer("3" + epDataOne);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

			}
		});

		onetype.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				if (onetype.getCurrentItem() == 1
						| onetype.getCurrentItem() == 2) {
					oneSeekBar.setProgress(0);
					onevalue.setText(0 + "s");
					oneSeekBar.setEnabled(false);

				} else if (onetype.getCurrentItem() == 0
						| onetype.getCurrentItem() == 3) {
					oneSeekBar.setProgress(0);
					onevalue.setText(0 + "s");
					oneSeekBar.setEnabled(true);

				}
				String epDataOne = String.valueOf(initSendDataType(onetype.getCurrentItem())
						+ StringUtil.appendLeft(oneSeekBar.getProgress() + "", 2, '0'));
				linkTaskControlEPData = new StringBuffer("3" + epDataOne);
			}
		});

		if (epData.startsWith("3") && epData.length() >= 4) {
			String oneType = linkTaskControlEPData.substring(1, 2);
			onetype.setCurrentItem(initReciveDataType(StringUtil.toInteger(oneType)));
			if (onetype.getCurrentItem() == 0 || onetype.getCurrentItem() == 3) {
				oneSeekBar.setEnabled(true);
			} else {
				oneSeekBar.setEnabled(false);
			}
			String oneValue = linkTaskControlEPData.substring(2, 4);
			if (oneValue.startsWith("0")) {
				oneValue = oneValue.substring(1, 2);
			}
			onevalue.setText(oneValue + "s");
			oneSeekBar.setProgress(StringUtil.toInteger(oneValue));

		}

		return createControlDataDialog(inflater.getContext(), view);
	}

	// 根据WheelView的位置初始化数据
	public int initSendDataType(int i) {
		switch (i) {
		case 0:
			i = 3;
			break;
		case 1:
			i = 0;
			break;
		case 2:
			i = 1;
			break;
		case 3:
			i = 4;
			break;

		default:
			break;
		}

		return i;
	}

	// 根据解析的data初始化WheelView的position
	public int initReciveDataType(int i) {
		switch (i) {
		case 0:
			i = 1;
			break;
		case 1:
			i = 2;
			break;
		case 3:
			i = 0;
			break;
		case 4:
			i = 3;
			break;

		default:
			break;
		}
		return i;
	}
	public class TOCAdapter extends ArrayWheelAdapter<String> {
		// Index of current item
		int currentItem;

		// Index of item to be highlighted
		// int currentValue;

		public TOCAdapter(Context context, String[] items) {
			super(context, items);
			setTextSize(16);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			// if (currentItem == currentValue) {
			// view.setTextColor(0xFF0000F0);
			// }
			view.setTypeface(Typeface.SANS_SERIF);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

}

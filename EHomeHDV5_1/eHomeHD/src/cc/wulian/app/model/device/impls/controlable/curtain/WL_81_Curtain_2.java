package cc.wulian.app.model.device.impls.controlable.curtain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl.ShortCutControlableDeviceSelectDataItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.DeviceTool;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_CURTAIN_2 }, category = Category.C_CONTROL)
public class WL_81_Curtain_2 extends ControlableDeviceImpl {
	private int SMALL_OPEN_D = R.drawable.device_shade_open;
	private int SMALL_CLOSE_D = R.drawable.device_shade_close;
	private int SMALL_STOP_D = R.drawable.device_shade_mid;
	public static final String OPERATION_MODE_NO_WORK = "0";
	public static final String OPERATION_MODE_WORKING = "1";
	public static final String OPERATION_MODE_NOTHING = "9";
	public static final String DATA_CLOSE = "000";
	public static final String DATA_OPEN = "100";
	public static final String DATA_STOP = "255";
	public static final String DATA_CHANGE_DIRECT = "250";
	public static final String DATA_CLEAR = "240";

	public static String STATE_CLOSE = "00";
	public static String STATE_OPEN = "64";
	public static String STATE_STOP = "FF";
	public static String STATE_CHANGE_DIRECTION = "01FA";
	public static String STATE_CLEAR = "01F0";
	private static final String SPLIT_SYMBOL = ">";

	private String currentEP = EP_14;
	@ViewInject(R.id.curtain1_title_btn)
	private Button curtain1Btn;
	@ViewInject(R.id.curtain2_title_btn)
	private Button curtain2Btn;
	@ViewInject(R.id.curtain_bg_iv)
	private ImageView curtainImageView;
	@ViewInject(R.id.curtain_child)
	private LinearLayout curtainChild;
	@ViewInject(R.id.curtain_child_dev_name)
	private TextView curtainChildDevName;
	@ViewInject(R.id.curtain_reset_ll)
	private LinearLayout curtainResetLineLayout;
	@ViewInject(R.id.curtain_change_direction_ib)
	private ImageButton curtainChangeImageButton;
	@ViewInject(R.id.curtain_clear_ib)
	private ImageButton curtainClearImageButton;
	@ViewInject(R.id.curtain_open_ib)
	private ImageButton curtainOpenImageButton;
	@ViewInject(R.id.curtain_stop_ib)
	private ImageButton curtainStopImageButton;
	@ViewInject(R.id.curtain_close_ib)
	private ImageButton curtainCloseImageButton;
	@ViewInject(R.id.curtain_adjust_sb)
	private SeekBar curtainSeekBar;
	@ViewInject(R.id.control_linearLayout)
	private LinearLayout controlLinearLayout;

	private String controlMode;
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			controlMode = OPERATION_MODE_NO_WORK;
			if (v == curtain1Btn) {
				setCurtainChecked(EP_14);
			} else if (v == curtain2Btn) {
				setCurtainChecked(EP_15);
			} else if (v == curtainChangeImageButton) {
				fireWulianDeviceRequestControlSelf();
				WulianDevice device = getChildDevice(currentEP);
				controlDevice(currentEP, device.getDeviceInfo().getDevEPInfo().getEpType(), controlMode
						+ DATA_CHANGE_DIRECT);
			} else if (v == curtainClearImageButton) {
				WulianDevice device = getChildDevice(currentEP);
				fireWulianDeviceRequestControlSelf();
				controlDevice(currentEP, device.getDeviceInfo().getDevEPInfo().getEpType(), controlMode + DATA_CLEAR);
			} else if (v == curtainOpenImageButton) {
				WulianDevice device = getChildDevice(currentEP);
				fireWulianDeviceRequestControlSelf();
				controlDevice(currentEP, device.getDeviceInfo().getDevEPInfo().getEpType(), controlMode + DATA_OPEN);
			} else if (v == curtainStopImageButton) {
				WulianDevice device = getChildDevice(currentEP);
				fireWulianDeviceRequestControlSelf();
				controlDevice(currentEP, device.getDeviceInfo().getDevEPInfo().getEpType(), controlMode + DATA_STOP);
			} else if (v == curtainCloseImageButton) {
				WulianDevice device = getChildDevice(currentEP);
				fireWulianDeviceRequestControlSelf();
				controlDevice(currentEP, device.getDeviceInfo().getDevEPInfo().getEpType(), controlMode + DATA_CLOSE);
			}
		}
	};

	public WL_81_Curtain_2(Context context, String type) {
		super(context, type);
	}

	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		super.onDeviceUp(devInfo);
	}

	@Override
	protected boolean isMultiepDevice() {
		return true;
	}

	@Override
	public synchronized void onDeviceData(String gwID, String devID, DeviceEPInfo devEPInfo,String cmd,String mode) {
		String ep = devEPInfo.getEp();
		String epData = devEPInfo.getEpData();
		WulianDevice device = getChildDevice(ep);
		if (epData != null
				&& (epData.endsWith(STATE_CLEAR) || epData.endsWith(STATE_CHANGE_DIRECTION) || epData.startsWith("09")))
			return;
		// device.getDeviceInfo().setDevEPInfo(devEPInfo);
		// device.refreshDevice();
		// removeCallbacks(mRefreshStateRunnable);
		// post(mRefreshStateRunnable);
		if (device != null) {
			device.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
			removeCallbacks(mRefreshStateRunnable);
			post(mRefreshStateRunnable);
			fireDeviceRequestControlData();
		} else {
			super.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
		}
	}

	@Override
	public boolean isStoped() {
		boolean result = true;
		for (WulianDevice device : getChildDevices().values()) {
			DeviceEPInfo info = device.getDeviceInfo().getDevEPInfo();
			if (!info.getEpData().endsWith(STATE_STOP)) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public String getStopProtocol() {
		return OPERATION_MODE_NO_WORK + DATA_STOP;
	}

	@Override
	public String getOpenProtocol() {
		return OPERATION_MODE_NO_WORK + DATA_OPEN;
	}

	@Override
	public String getCloseProtocol() {
		return OPERATION_MODE_NO_WORK + DATA_CLOSE;
	}

	@Override
	public boolean isOpened() {
		boolean result = false;
		for (WulianDevice device : getChildDevices().values()) {
			DeviceEPInfo info = device.getDeviceInfo().getDevEPInfo();
			boolean isOpen = true;
			if (info.getEpData().endsWith(STATE_CLOSE)) {
				isOpen = false;
				continue;
			}
			if (info.getEpData().endsWith(STATE_STOP)) {
				isOpen = false;
				continue;
			}
			if (isOpen == true) {
				result = true;
				break;
			}
		}
		return result;
	}

	@Override
	public boolean isClosed() {
		boolean result = true;
		for (WulianDevice device : getChildDevices().values()) {
			DeviceEPInfo info = device.getDeviceInfo().getDevEPInfo();
			if (!info.getEpData().endsWith(STATE_CLOSE)) {
				result = false;
			}
		}
		return result;
	}

	@Override
	public Drawable getStateSmallIcon() {
		return isStoped() ? getDrawable(SMALL_STOP_D) : isOpened() ? getDrawable(SMALL_OPEN_D)
				: isClosed() ? getDrawable(SMALL_CLOSE_D) : getDrawable(SMALL_CLOSE_D);
	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item, LayoutInflater inflater) {
		if (item == null) {
			item = new ControlableDeviceCurtainShortCutControlItem(inflater.getContext());
		}
		((ControlableDeviceCurtainShortCutControlItem) item).setStopVisiable(true);
		item.setWulianDevice(this);
		return item;
	}

	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(DeviceShortCutSelectDataItem item,
			LayoutInflater inflater, AutoActionInfo autoActionInfo) {

		if (item == null) {
			ShortCutCurtain_2DeviceSelectDataItem shortCutItem = new ShortCutCurtain_2DeviceSelectDataItem(
					inflater.getContext());
			shortCutItem.setStopVisiable(true);
			item = shortCutItem;
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}

	private class ShortCutCurtain_2DeviceSelectDataItem extends ShortCutControlableDeviceSelectDataItem {

		public ShortCutCurtain_2DeviceSelectDataItem(Context context) {
			super(context);
		}

		@Override
		protected boolean isOpened() {
			if (mDevice instanceof Controlable) {
				Controlable controlable = (Controlable) mDevice;
				Map<String, WulianDevice> childDevices = mDevice.getChildDevices();
				String epData = "";
				String ep = "";
				if (childDevices != null) {
					ep = WulianDevice.EP_0;
					for (WulianDevice childDevice : childDevices.values()) {
						if (childDevice instanceof Controlable) {
							epData += controlable.getOpenProtocol();
						}
					}
				} else {
					ep = mDevice.getDefaultEndPoint();
					epData = controlable.getOpenProtocol();
				}
				return StringUtil.equals(epData, this.autoActionInfo.getEpData());
			}
			return false;
		}

		@Override
		protected boolean isClosed() {
			if (mDevice instanceof Controlable) {
				Controlable controlable = (Controlable) mDevice;
				Map<String, WulianDevice> childDevices = mDevice.getChildDevices();
				String epData = "";
				String ep = "";
				if (childDevices != null) {
					ep = WulianDevice.EP_0;
					for (WulianDevice childDevice : childDevices.values()) {
						if (childDevice instanceof Controlable) {
							epData += controlable.getCloseProtocol();
						}
					}
				} else {
					ep = mDevice.getDefaultEndPoint();
					epData += controlable.getCloseProtocol();
				}
				return StringUtil.equals(epData, this.autoActionInfo.getEpData());
			}
			return false;
		}

		@Override
		protected boolean isStoped() {
			if (mDevice instanceof Controlable) {
				Controlable controlable = (Controlable) mDevice;
				Map<String, WulianDevice> childDevices = mDevice.getChildDevices();
				String epData = "";
				String ep = "";
				if (childDevices != null) {
					ep = WulianDevice.EP_0;
					for (WulianDevice childDevice : childDevices.values()) {
						if (childDevice instanceof Controlable) {
							epData += controlable.getStopProtocol();
						}
					}
				} else {
					ep = mDevice.getDefaultEndPoint();
					epData += controlable.getStopProtocol();
				}
				return StringUtil.equals(epData, this.autoActionInfo.getEpData());
			}
			return false;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
		View view = inflater.inflate(R.layout.device_curtain2_content, null);
		LinearLayout deviceContainerlineLayout = (LinearLayout) view.findViewById(R.id.curtain_device_content);
		View deviceView = inflater.inflate(R.layout.device_curtain_content, null);
		deviceContainerlineLayout.addView(deviceView);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		curtain1Btn.setOnClickListener(clickListener);
		curtain2Btn.setOnClickListener(clickListener);
		curtainChild.setVisibility(View.VISIBLE);
		curtainResetLineLayout.setVisibility(View.VISIBLE);
		curtainChangeImageButton.setOnClickListener(clickListener);
		curtainClearImageButton.setOnClickListener(clickListener);
		curtainOpenImageButton.setOnClickListener(clickListener);
		curtainStopImageButton.setOnClickListener(clickListener);
		curtainCloseImageButton.setOnClickListener(clickListener);
		curtainSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int percent = seekBar.getProgress();
				percent = percent > 90 ? 100 : percent < 10 ? 0 : percent;
				WulianDevice device = getChildDevice(currentEP);
				fireWulianDeviceRequestControlSelf();
				controlDevice(currentEP, device.getDeviceInfo().getDevEPInfo().getEpType(),
						controlMode + StringUtil.appendLeft(percent + "", 3, '0'));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}
		});
		setCurtainChecked(currentEP);
		mViewCreated = true;
	}

	private void setCurtainChecked(String ep) {
		currentEP = ep;
		if (EP_14.equals(currentEP)) {
			curtain1Btn.setBackgroundResource(R.drawable.curtain_title_bg_selected);
		} else {
			curtain1Btn.setBackgroundResource(R.drawable.curtain_title_bg_normal);
		}
		if (EP_15.equals(currentEP)) {
			curtain2Btn.setBackgroundResource(R.drawable.curtain_title_bg_selected);
		} else {
			curtain2Btn.setBackgroundResource(R.drawable.curtain_title_bg_normal);
		}
		WulianDevice device = getChildDevice(currentEP);
		String epData = device.getDeviceInfo().getDevEPInfo().getEpData();
		if (StringUtil.isNullOrEmpty(epData) || epData.length() < 4)
			return;
		// 子设备名称获取
		curtainChildDevName.setText(getChildDevice(currentEP).getDeviceInfo().getDevEPInfo().getEpName());
		int canCtrl = StringUtil.toInteger(epData.substring(0, 2), 16);
		if (canCtrl == 0) {
			controlLinearLayout.setVisibility(View.INVISIBLE);
		} else {
			controlLinearLayout.setVisibility(View.VISIBLE);
		}
		String stateData = epData.substring(2, 4);
		curtainSeekBar.setProgress(StringUtil.toInteger(stateData, 16));
		if (STATE_CLOSE.equals(stateData)) {
			curtainImageView.setImageResource(R.drawable.curtain_bg_close);
		} else if (STATE_OPEN.equals(stateData)) {
			curtainImageView.setImageResource(R.drawable.curtain_bg_open);
		} else {
			curtainImageView.setImageResource(R.drawable.curtain_bg_half_open);
		}

	}

	@Override
	public void initViewStatus() {
		setCurtainChecked(currentEP);
	}

	public static class ControlableDeviceCurtainShortCutControlItem extends ControlableDeviceShortCutControlItem {

		public ControlableDeviceCurtainShortCutControlItem(Context context) {
			super(context);
		}

		@Override
		protected void clickOpen() {
			if (mDevice instanceof Controlable) {
				Controlable controlable = (Controlable) mDevice;
				mDevice.controlDevice(EP_0, "80", controlable.getOpenProtocol() + controlable.getOpenProtocol());
			}
		}

		@Override
		protected void clickStop() {
			if (mDevice instanceof Controlable) {
				Controlable controlable = (Controlable) mDevice;
				mDevice.controlDevice(EP_0, "80", controlable.getStopProtocol() + controlable.getStopProtocol());
			}
		}

		@Override
		protected void clickClose() {
			if (mDevice instanceof Controlable) {
				Controlable controlable = (Controlable) mDevice;
				mDevice.controlDevice(EP_0, "80", controlable.getCloseProtocol() + controlable.getCloseProtocol());
			}
		}

	}

	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater, String ep, String epData) {
		if (epData == null) {
			epData = "";
		}
		View view = inflater.inflate(R.layout.device_curtain_adjust_control, null);
		linkTaskControlEPData = new StringBuffer(epData);
		SeekBar seekBarAdjust = (SeekBar) view.findViewById(R.id.device_curtain_adjust_seekbar);
		seekBarAdjust.setProgress(0);
		final Button mButtonOn = (Button) view.findViewById(R.id.device_curtain_adjust_open);
		final Button mButtonStop = (Button) view.findViewById(R.id.device_curtain_adjust_stop);
		final Button mButtonOff = (Button) view.findViewById(R.id.device_curtain_adjust_close);

		String lightText = linkTaskControlEPData.substring(1);
		int lightProcess = StringUtil.toInteger(lightText);
		if (StringUtil.equals(lightText, DATA_CLOSE)) {
			seekBarAdjust.setProgress(lightProcess);
			mButtonOn.setSelected(false);
			mButtonStop.setSelected(false);
			mButtonOff.setSelected(true);
		} else if (StringUtil.equals(lightText, DATA_OPEN)) {
			mButtonOn.setSelected(true);
			mButtonStop.setSelected(false);
			mButtonOff.setSelected(false);
			seekBarAdjust.setProgress(lightProcess);
		} else if (StringUtil.equals(lightText, DATA_STOP)) {
			mButtonOn.setSelected(false);
			mButtonStop.setSelected(true);
			mButtonOff.setSelected(false);
		} else {
			seekBarAdjust.setProgress(lightProcess);
		}

		mButtonOn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mButtonOn.setSelected(true);
				mButtonStop.setSelected(false);
				mButtonOff.setSelected(false);
				linkTaskControlEPData = new StringBuffer(OPERATION_MODE_NO_WORK + DATA_OPEN);
			}
		});
		mButtonStop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mButtonOn.setSelected(false);
				mButtonStop.setSelected(true);
				mButtonOff.setSelected(false);
				linkTaskControlEPData = new StringBuffer(OPERATION_MODE_NO_WORK + DATA_STOP);
			}
		});
		mButtonOff.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mButtonOn.setSelected(false);
				mButtonStop.setSelected(false);
				mButtonOff.setSelected(true);
				linkTaskControlEPData = new StringBuffer(OPERATION_MODE_NO_WORK + DATA_CLOSE);
			}
		});
		seekBarAdjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				linkTaskControlEPData = new StringBuffer(OPERATION_MODE_NO_WORK
						+ StringUtil.appendLeft(mSeekProgress + "", 3, '0'));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			}
		});

		return createControlDataDialog(inflater.getContext(), view);
	}

	private void setautoActionInfo(ImageView img1, ImageView img2, String currentDate, String OtherDate, String ep,
			AutoActionInfo autoActionInfo) {
		StringBuffer buffer = new StringBuffer();
		String epTxt = "";

		if (img1.isSelected() && img2.isSelected()) {
			epTxt = EP_0;
			if (StringUtil.equals(ep, EP_14)) {
				buffer.append(OPERATION_MODE_NO_WORK).append(currentDate).append(OPERATION_MODE_NO_WORK)
						.append(OtherDate);
			} else if (StringUtil.equals(ep, EP_15)) {
				buffer.append(OPERATION_MODE_NO_WORK).append(OtherDate).append(OPERATION_MODE_NO_WORK)
						.append(currentDate);
			}
		} else {
			if (img1.isSelected()) {
				epTxt = EP_14;
				buffer.append(OPERATION_MODE_NO_WORK).append(currentDate);
			} else if (img2.isSelected()) {
				epTxt = EP_15;
				buffer.append(OPERATION_MODE_NO_WORK).append(currentDate);
			} else {
				epTxt = EP_0;
			}
		}
		autoActionInfo.setEpData(buffer.toString());
		autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL + getDeviceType() + SPLIT_SYMBOL + epTxt + SPLIT_SYMBOL
				+ getDeviceType());
	}

	private void setDeviceDataViewEvent(final ImageView imgView1, final ImageView imgView2,
			final List<View> currentItems, final List<View> otherItems, final AutoActionInfo autoActionInfo,
			final String ep) {
		((SeekBar) currentItems.get(0)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				String currentDate = StringUtil.appendLeft(seekBar.getProgress() + "", 3, '0');
				String otherDate = StringUtil.appendLeft(((SeekBar) otherItems.get(0)).getProgress() + "", 3, '0');
				setDeviceDataViewStatus(currentItems, seekBar.getProgress(), false, false, false, true);
				setautoActionInfo(imgView1, imgView2, currentDate, otherDate, ep, autoActionInfo);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				currentItems.get(4).setSelected(true);
			}
		});

		((Button) currentItems.get(1)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setDeviceDataViewStatus(currentItems, StringUtil.toInteger(DATA_OPEN), true, false, false, true);
				String otherDate = getOtherEpdata(otherItems);
				setautoActionInfo(imgView1, imgView2, DATA_OPEN, otherDate, ep, autoActionInfo);
			}
		});
		((Button) currentItems.get(2)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setDeviceDataViewStatus(currentItems, 0, false, true, false, true);
				String otherDate = getOtherEpdata(otherItems);
				setautoActionInfo(imgView1, imgView2, DATA_STOP, otherDate, ep, autoActionInfo);
			}
		});
		((Button) currentItems.get(3)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setDeviceDataViewStatus(currentItems, StringUtil.toInteger(DATA_CLOSE), false, false, true, true);
				String otherDate = getOtherEpdata(otherItems);
				setautoActionInfo(imgView1, imgView2, DATA_CLOSE, otherDate, ep, autoActionInfo);
			}
		});
	}

	private void setDeviceDataViewStatus(List<View> items, int size, boolean onFlg, boolean onStop, boolean onOff,
			boolean select) {
		if (items == null)
			return;
		((SeekBar) items.get(0)).setProgress(size);
		items.get(1).setSelected(onFlg);
		items.get(2).setSelected(onStop);
		items.get(3).setSelected(onOff);
		items.get(4).setSelected(select);
	}

	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(LayoutInflater inflater,
			final AutoActionInfo autoActionInfo) {

		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		View contentview = inflater.inflate(R.layout.task_manager_two_curtain_device_choose, null);
		String[] type = autoActionInfo.getObject().split(SPLIT_SYMBOL);
		String epdata = autoActionInfo.getEpData();
		final List<View> oneItemList = new ArrayList<View>();
		final List<View> twoItemList = new ArrayList<View>();

		SeekBar seekBarAdjust = (SeekBar) contentview.findViewById(R.id.device_curtain_adjust_seekbar);
		seekBarAdjust.setTag("0");
		final ImageView oneImageView = (ImageView) contentview.findViewById(R.id.curtain_imageview);
		Button oneButtonOn = (Button) contentview.findViewById(R.id.device_curtain_adjust_open);
		Button oneButtonStop = (Button) contentview.findViewById(R.id.device_curtain_adjust_stop);
		Button oneButtonOff = (Button) contentview.findViewById(R.id.device_curtain_adjust_close);
		oneItemList.add(seekBarAdjust);
		oneItemList.add(oneButtonOn);
		oneItemList.add(oneButtonStop);
		oneItemList.add(oneButtonOff);
		oneItemList.add(oneImageView);

		final ImageView twoImageView = (ImageView) contentview.findViewById(R.id.curtain_imageview_1);
		SeekBar seekBarAdjust1 = (SeekBar) contentview.findViewById(R.id.device_curtain_adjust_seekbar1);
		seekBarAdjust1.setTag("0");
		Button twoButtonOn = (Button) contentview.findViewById(R.id.device_curtain_adjust_open1);
		Button twoButtonStop = (Button) contentview.findViewById(R.id.device_curtain_adjust_stop1);
		Button twoButtonOff = (Button) contentview.findViewById(R.id.device_curtain_adjust_close1);
		twoItemList.add(seekBarAdjust1);
		twoItemList.add(twoButtonOn);
		twoItemList.add(twoButtonStop);
		twoItemList.add(twoButtonOff);
		twoItemList.add(twoImageView);

		TextView oneTextView = (TextView) contentview.findViewById(R.id.curtain_text);
		TextView twoTextView = (TextView) contentview.findViewById(R.id.curtain_text_1);
		StringBuilder sb1 = new StringBuilder();
		sb1.append(DeviceTool.getDeviceShowName(this));
		sb1.append("-");
		sb1.append(DeviceUtil.ep2IndexString(EP_14));
		StringBuilder sb2 = new StringBuilder();
		sb2.append(DeviceTool.getDeviceShowName(this));
		sb2.append("-");
		sb2.append(DeviceUtil.ep2IndexString(EP_15));
		oneTextView.setText(sb1.toString());
		twoTextView.setText(sb2.toString());

		OnClickListener checkClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				String oneEpDate = getOtherEpdata(oneItemList);
				String twoEpDate = getOtherEpdata(twoItemList);
				if (v == oneImageView) {
					if (oneImageView.isSelected()) {
						oneImageView.setSelected(false);
						setautoActionInfo(oneImageView, twoImageView, twoEpDate, oneEpDate, EP_15, autoActionInfo);
					} else {
						oneImageView.setSelected(true);
						setautoActionInfo(oneImageView, twoImageView, oneEpDate, twoEpDate, EP_14, autoActionInfo);
					}
				} else if (v == twoImageView) {
					if (twoImageView.isSelected()) {
						twoImageView.setSelected(false);
						setautoActionInfo(oneImageView, twoImageView, oneEpDate, twoEpDate, EP_14, autoActionInfo);
					} else {
						twoImageView.setSelected(true);
						setautoActionInfo(oneImageView, twoImageView, twoEpDate, oneEpDate, EP_15, autoActionInfo);
					}
				}
			}
		};
		oneImageView.setOnClickListener(checkClickListener);
		twoImageView.setOnClickListener(checkClickListener);

		if (!StringUtil.isNullOrEmpty(epdata)) {
			if (StringUtil.equals(type[2], EP_14)) {
				String process = epdata.substring(1, 4);
				if (!StringUtil.isNullOrEmpty(process)) {
					setCurrentStatus(process, oneItemList);
				}
			} else if (StringUtil.equals(type[2], EP_15)) {
				String process = epdata.substring(1, 4);
				if (!StringUtil.isNullOrEmpty(process)) {
					setCurrentStatus(process, twoItemList);
				}
			} else {
				if (epdata.length() >= 8) {
					String oneProcess = epdata.substring(1, 4);
					if (!StringUtil.isNullOrEmpty(oneProcess)) {
						setCurrentStatus(oneProcess, oneItemList);
					}
					String twoProcess = epdata.substring(5, 8);
					if (!StringUtil.isNullOrEmpty(twoProcess)) {
						setCurrentStatus(twoProcess, twoItemList);
					}
				}
			}
		}

		setDeviceDataViewEvent(oneImageView, twoImageView, oneItemList, twoItemList, autoActionInfo, EP_14);
		setDeviceDataViewEvent(oneImageView, twoImageView, twoItemList, oneItemList, autoActionInfo, EP_15);

		holder.setShowDialog(false);
		holder.setContentView(contentview);
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}

	private void setCurrentStatus(String process, List<View> itemList) {
		if (StringUtil.equals(process, DATA_OPEN)) {
			setDeviceDataViewStatus(itemList, StringUtil.toInteger(process), true, false, false, true);
		} else if (StringUtil.equals(process, DATA_CLOSE)) {
			setDeviceDataViewStatus(itemList, StringUtil.toInteger(process), false, false, true, true);
		} else if (StringUtil.equals(process, DATA_STOP)) {
			setDeviceDataViewStatus(itemList, 0, false, true, false, true);
		} else {
			setDeviceDataViewStatus(itemList, StringUtil.toInteger(process), false, false, false, true);
		}
	}

	private String getOtherEpdata(List<View> otherItems) {
		String otherDate = "";
		if (otherItems.get(1).isSelected()) {
			otherDate = DATA_OPEN;
		} else if (otherItems.get(2).isSelected()) {
			otherDate = DATA_STOP;
		} else if (otherItems.get(3).isSelected()) {
			otherDate = DATA_CLOSE;
		} else {
			otherDate = StringUtil.appendLeft(((SeekBar) otherItems.get(0)).getProgress() + "", 3, '0');
		}
		return otherDate;
	}
}
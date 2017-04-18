package cc.wulian.app.model.device.impls.controlable.toc;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.dao.TOCDao;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.SendMessage;

import com.yuantuo.customview.wheel.toc.ArrayWheelAdapter;
import com.yuantuo.customview.wheel.toc.OnWheelScrollListener;
import com.yuantuo.customview.wheel.toc.WheelView;
/**
 * 二路输出转换器
 * @author Administrator
 *Two_Output_Converter的缩写
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_CONVERTERS_OUTPUT_2 }, category = Category.C_OTHER)
public class WL_A2_Two_Output_Converter extends AbstractDevice {

	private twoOutputGridViewAdapter mAdapter;
	private static TOCDao tocDao = TOCDao.getInstance();
	private ImageView oneSwitchPng;
	private ImageView twoSwitchPng;
	private GridView mGridView;
	private String curDefaultSettingData;

	public String getCurDefaultSettingData() {
		return curDefaultSettingData;
	}

	public WL_A2_Two_Output_Converter(Context context, String type) {
		super(context, type);
	}

	@Override
	public void refreshDevice() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_two_output_converter, null);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);

		oneSwitchPng = (ImageView) view
				.findViewById(R.id.device_two_output_converter_one_switch);
		twoSwitchPng = (ImageView) view
				.findViewById(R.id.device_two_output_converter_two_switch);
		mGridView = (GridView) view
				.findViewById(R.id.device_two_output_converter_gridview);
		fireWulianDeviceRequestControlSelf();
		SendMessage.sendControlDevMsg(gwID, devID, EP_14, type, "41");
	}
	@Override
	public void initViewStatus() {
		super.initViewStatus();

		if (StringUtil.isNullOrEmpty(mCurrentEpInfo.getEpData())) {
			return;

		} else if ((mCurrentEpInfo.getEpData().startsWith("03") || mCurrentEpInfo.getEpData()
				.startsWith("04")) && mCurrentEpInfo.getEpData().length() >= 10) {
			String oneSwtichData = mCurrentEpInfo.getEpData().substring(2, 6);
			String twoSwtichData = mCurrentEpInfo.getEpData().substring(6, 10);

			if (oneSwtichData.startsWith("04") || oneSwtichData.equals("0100")) {
				oneSwitchPng.setImageDrawable(getResources().getDrawable(
						R.drawable.device_two_output_converter_switch_close));

			} else if (oneSwtichData.startsWith("03")
					|| oneSwtichData.equals("0000")) {
				oneSwitchPng.setImageDrawable(getResources().getDrawable(
						R.drawable.device_two_output_converter_switch_open));

			}
			if (twoSwtichData.startsWith("04") || twoSwtichData.equals("0100")) {
				twoSwitchPng.setImageDrawable(getResources().getDrawable(
						R.drawable.device_two_output_converter_switch_close));

			} else if (twoSwtichData.startsWith("03")
					|| twoSwtichData.equals("0000")) {
				twoSwitchPng.setImageDrawable(getResources().getDrawable(
						R.drawable.device_two_output_converter_switch_open));

			}
		} else if ((mCurrentEpInfo.getEpData().startsWith("01") || mCurrentEpInfo.getEpData()
				.startsWith("02")) && mCurrentEpInfo.getEpData().length() >= 6) {
			curDefaultSettingData = mCurrentEpInfo.getEpData();

		}
		mAdapter = new twoOutputGridViewAdapter(getContext(),
				tocDao.findTwoOutputGridInfo());
		mGridView.setAdapter(mAdapter);

	}

	public Intent getSettingIntent() {
		Intent intent = new Intent(mContext, DeviceSettingActivity.class);
		intent.putExtra(DeviceTwoOutputFragment.GWID, gwID);
		intent.putExtra(DeviceTwoOutputFragment.DEVICEID, devID);
		intent.putExtra(DeviceTwoOutputFragment.DEVICE_TWO_OUTPUT, type);
		intent.putExtra(AbstractDevice.SETTING_LINK_TYPE,
				AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME,
				DeviceTwoOutputFragment.class.getName());
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
			LayoutInflater inflater,final  AutoActionInfo autoActionInfo) {
		String epData = autoActionInfo.getEpData();
		if(epData == null)
			epData = "";
		View view = inflater
				.inflate(
						cc.wulian.smarthomev5.R.layout.scene_task_control_data_two_converter,
						null);
		// one
		final WheelView onetype = (WheelView) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_two_converter_one_type);
		final SeekBar oneSeekBar = (SeekBar) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_two_converter_one_seekbar);
		final TextView onevalue = (TextView) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_two_converter_one_value);

		// two
		final WheelView twotype = (WheelView) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_two_converter_two_type);
		final SeekBar twoSeekBar = (SeekBar) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_two_converter_two_seekbar);
		final TextView twovalue = (TextView) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_two_converter_two_value);

		String[] keytype = new String[] {
				getResources().getString(R.string.device_two_output_often_open),
				getResources().getString(R.string.device_state_open),
				getResources().getString(R.string.device_two_output_none),
				getResources().getString(R.string.device_state_close),
				getResources()
						.getString(R.string.device_two_output_often_close) };

		OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				if (arg0 == oneSeekBar) {
					onevalue.setText(oneSeekBar.getProgress() + "s");

				} else if (arg0 == twoSeekBar) {
					twovalue.setText(twoSeekBar.getProgress() + "s");

				}
				String epDataOne = String.valueOf(initSendDataType(onetype.getCurrentItem())
						+ StringUtil.appendLeft(oneSeekBar.getProgress() + "", 2, '0'));
				String epDataTwo = String.valueOf(initSendDataType( twotype.getCurrentItem())
						+ StringUtil.appendLeft(twoSeekBar.getProgress() + "", 2, '0'));
				autoActionInfo.setEpData("3" + epDataOne + epDataTwo);

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

			}
		};

		TOCAdapter mAdapter = new TOCAdapter(mContext, keytype);

		onetype.setVisibleItems(5);
		onetype.setViewAdapter(mAdapter);
		oneSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

		twotype.setVisibleItems(5);
		twotype.setViewAdapter(mAdapter);
		twoSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

		onetype.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				if (onetype.getCurrentItem() == 1
						| onetype.getCurrentItem() == 2
						| onetype.getCurrentItem() == 3) {
					oneSeekBar.setProgress(0);
					onevalue.setText(0 + "s");
					oneSeekBar.setEnabled(false);

				} else if (onetype.getCurrentItem() == 0
						| onetype.getCurrentItem() == 4) {
					oneSeekBar.setProgress(0);
					onevalue.setText(0 + "s");
					oneSeekBar.setEnabled(true);

				}
				String epDataOne = String.valueOf(initSendDataType(onetype.getCurrentItem())
						+ StringUtil.appendLeft(oneSeekBar.getProgress() + "", 2, '0'));
				String epDataTwo = String.valueOf(initSendDataType(twotype.getCurrentItem())
						+ StringUtil.appendLeft(twoSeekBar.getProgress() + "", 2, '0'));
				autoActionInfo.setEpData("3" + epDataOne + epDataTwo);
			}
		});

		twotype.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				if (twotype.getCurrentItem() == 1
						| twotype.getCurrentItem() == 2
						| twotype.getCurrentItem() == 3) {
					twoSeekBar.setProgress(0);
					twovalue.setText(0 + "s");
					twoSeekBar.setEnabled(false);

				} else if (twotype.getCurrentItem() == 0
						| twotype.getCurrentItem() == 4) {
					twoSeekBar.setProgress(0);
					twovalue.setText(0 + "s");
					twoSeekBar.setEnabled(true);

				}
				String epDataOne = String.valueOf(initSendDataType(onetype.getCurrentItem())
						+ StringUtil.appendLeft(oneSeekBar.getProgress() + "", 2, '0'));
				String epDataTwo = String.valueOf(initSendDataType(twotype.getCurrentItem())
						+ StringUtil.appendLeft(twoSeekBar.getProgress() + "", 2, '0'));
				autoActionInfo.setEpData("3" + epDataOne + epDataTwo);
				
			}
		});

		if (epData.startsWith("3") && epData.length() >= 7) {
			// init one
			String oneType = epData.substring(1, 2);
			onetype.setCurrentItem(initReciveDataType(StringUtil.toInteger(oneType)));
			if (onetype.getCurrentItem() == 0 || onetype.getCurrentItem() == 4) {
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

			// init two
			String twoType = epData.substring(4, 5);
			twotype.setCurrentItem(initReciveDataType(StringUtil.toInteger(twoType)));
			if (twotype.getCurrentItem() == 0 || twotype.getCurrentItem() == 4) {
				twoSeekBar.setEnabled(true);
			} else {
				twoSeekBar.setEnabled(false);
			}
			String twoValue = epData.substring(5, 7);
			if (twoValue.startsWith("0")) {
				twoValue = twoValue.substring(1, 2);
			}
			twoSeekBar.setProgress(StringUtil.toInteger(twoValue));
			twovalue.setText(twoValue + "s");

		}
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		holder.setContentView(view);
		holder.setShowDialog(true);
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}
	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,
			String ep, String epData) {
		View view = inflater
				.inflate(
						cc.wulian.smarthomev5.R.layout.scene_task_control_data_two_converter,
						null);
		linkTaskControlEPData = new StringBuffer(epData);
		// one
		final WheelView onetype = (WheelView) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_two_converter_one_type);
		final SeekBar oneSeekBar = (SeekBar) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_two_converter_one_seekbar);
		final TextView onevalue = (TextView) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_two_converter_one_value);

		// two
		final WheelView twotype = (WheelView) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_two_converter_two_type);
		final SeekBar twoSeekBar = (SeekBar) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_two_converter_two_seekbar);
		final TextView twovalue = (TextView) view
				.findViewById(cc.wulian.smarthomev5.R.id.scene_task_two_converter_two_value);

		String[] keytype = new String[] {
				getResources().getString(R.string.device_two_output_often_open),
				getResources().getString(R.string.device_state_open),
				getResources().getString(R.string.device_two_output_none),
				getResources().getString(R.string.device_state_close),
				getResources()
						.getString(R.string.device_two_output_often_close) };

		OnSeekBarChangeListener mOnSeekBarChangeListener = new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				if (arg0 == oneSeekBar) {
					onevalue.setText(oneSeekBar.getProgress() + "s");

				} else if (arg0 == twoSeekBar) {
					twovalue.setText(twoSeekBar.getProgress() + "s");

				}
				String epDataOne = String.valueOf(initSendDataType(onetype.getCurrentItem())
						+ StringUtil.appendLeft(oneSeekBar.getProgress() + "", 2, '0'));
				String epDataTwo = String.valueOf(initSendDataType(twotype.getCurrentItem())
						+ StringUtil.appendLeft(twoSeekBar.getProgress() + "", 2, '0'));
				linkTaskControlEPData = new StringBuffer("3" + epDataOne + epDataTwo);

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

			}
		};

		TOCAdapter mAdapter = new TOCAdapter(mContext, keytype);

		onetype.setVisibleItems(5);
		onetype.setViewAdapter(mAdapter);
		oneSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

		twotype.setVisibleItems(5);
		twotype.setViewAdapter(mAdapter);
		twoSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

		onetype.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				if (onetype.getCurrentItem() == 1
						| onetype.getCurrentItem() == 2
						| onetype.getCurrentItem() == 3) {
					oneSeekBar.setProgress(0);
					onevalue.setText(0 + "s");
					oneSeekBar.setEnabled(false);

				} else if (onetype.getCurrentItem() == 0
						| onetype.getCurrentItem() == 4) {
					oneSeekBar.setProgress(0);
					onevalue.setText(0 + "s");
					oneSeekBar.setEnabled(true);

				}
				String epDataOne = String.valueOf(initSendDataType(onetype.getCurrentItem())
						+ StringUtil.appendLeft(oneSeekBar.getProgress() + "", 2, '0'));
				String epDataTwo = String.valueOf(initSendDataType( twotype.getCurrentItem())
						+ StringUtil.appendLeft(twoSeekBar.getProgress() + "", 2, '0'));
				linkTaskControlEPData = new StringBuffer("3" + epDataOne + epDataTwo);
			}
		});

		twotype.addScrollingListener(new OnWheelScrollListener() {

			@Override
			public void onScrollingStarted(WheelView wheel) {

			}

			@Override
			public void onScrollingFinished(WheelView wheel) {
				if (twotype.getCurrentItem() == 1
						| twotype.getCurrentItem() == 2
						| twotype.getCurrentItem() == 3) {
					twoSeekBar.setProgress(0);
					twovalue.setText(0 + "s");
					twoSeekBar.setEnabled(false);

				} else if (twotype.getCurrentItem() == 0
						| twotype.getCurrentItem() == 4) {
					twoSeekBar.setProgress(0);
					twovalue.setText(0 + "s");
					twoSeekBar.setEnabled(true);

				}
				String epDataOne = String.valueOf(initSendDataType(onetype.getCurrentItem())
						+ StringUtil.appendLeft(oneSeekBar.getProgress() + "", 2, '0'));
				String epDataTwo = String.valueOf(initSendDataType(twotype.getCurrentItem())
						+ StringUtil.appendLeft(twoSeekBar.getProgress() + "", 2, '0'));
				linkTaskControlEPData = new StringBuffer("3" + epDataOne + epDataTwo);
			}
		});

		if (epData.startsWith("3") && epData.length() >= 7) {
			// init one
			String oneType = linkTaskControlEPData.substring(1, 2);
			onetype.setCurrentItem(initReciveDataType(StringUtil.toInteger(oneType)));
			if (onetype.getCurrentItem() == 0 || onetype.getCurrentItem() == 4) {
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

			// init two
			String twoType = linkTaskControlEPData.substring(4, 5);
			twotype.setCurrentItem(initReciveDataType(StringUtil.toInteger(twoType)));
			if (twotype.getCurrentItem() == 0 || twotype.getCurrentItem() == 4) {
				twoSeekBar.setEnabled(true);
			} else {
				twoSeekBar.setEnabled(false);
			}
			String twoValue = linkTaskControlEPData.substring(5, 7);
			if (twoValue.startsWith("0")) {
				twoValue = twoValue.substring(1, 2);
			}
			twoSeekBar.setProgress(StringUtil.toInteger(twoValue));
			twovalue.setText(twoValue + "s");

		}

		return createControlDataDialog(inflater.getContext(), view);
	}
	/**
	 * 发送时候根据返回wheelview position 确定发送的协议
	 * @param i
	 * @return
	 */
	public int initSendDataType(int i) {
		switch (i) {
		case 0:
			i = 3;
			break;
		case 1:
			i = 0;
			break;
		case 2:
			i = 2;
			break;
		case 3:
			i = 1;
			break;
		case 4:
			i = 4;
			break;

		default:
			break;
		}

		return i;
	}
	/**
	 * 根据返回的协议，初始化wheel view 的位置
	 * @param i
	 * @return
	 */
	public int initReciveDataType(int i) {
		switch (i) {
		case 0:
			i = 1;
			break;
		case 1:
			i = 3;
			break;
		case 2:
			i = 2;
			break;
		case 3:
			i = 0;
			break;
		case 4:
			i = 4;
			break;

		default:
			break;
		}
		return i;
	}

	public class twoOutputGridViewAdapter extends
			WLBaseAdapter<TwoOutputEntity> {

		private TextView mGridTextView;

		public twoOutputGridViewAdapter(Context context,
				List<TwoOutputEntity> data) {
			super(context, data);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(
					R.layout.device_two_output_converter_gridview_item, null);
		}

		@Override
		protected void bindView(Context context, View view, int pos,
				TwoOutputEntity item) {
			mGridTextView = (TextView) view
					.findViewById(R.id.device_two_output_converter_gridview_text);
			mGridTextView.setText(item.getKeyName());

			mGridTextView.setOnClickListener(new controlSwitchListener(item,
					pos));

		}

		private class controlSwitchListener implements OnClickListener {

			final TwoOutputEntity records;
			private String N = null;
			private String oneTT = null;
			private String M = null;
			private String twoTT = null;
			private String zero = "0";
			private String one = "1";
			private String two = "2";
			private String three = "3";
			private String four = "4";

			public controlSwitchListener(TwoOutputEntity records, int position) {
				this.records = records;

			}

			@Override
			public void onClick(View arg0) {
				sendControl(records);

			}

			public void sendControl(TwoOutputEntity entity) {

				// 0开路 1闭路 2不改变状态 3开路延时 4闭路延时
				N = changeOutStatus(StringUtil.toInteger(entity.getOneType()), N);
				oneTT = StringUtil.appendLeft(entity.getOneValue(), 2, '0');
				M = changeOutStatus(StringUtil.toInteger(entity.getTwoType()), M);
				twoTT = StringUtil.appendLeft(entity.getTwoValue(), 2, '0');

				String sendData = three + N + oneTT + M + twoTT;
				fireWulianDeviceRequestControlSelf();
				SendMessage.sendControlDevMsg(gwID, devID, EP_14, type, sendData);

			}

			// 代码丑,目的为了根据wheelview的pos初始化发送数据
			public String changeOutStatus(int i, String str) {
				switch (i) {
				case 0:
					str = three;
					break;
				case 1:
					str = zero;
					break;
				case 2:
					str = two;
					break;
				case 3:
					str = one;
					break;
				case 4:
					str = four;
					break;
				default:
					break;

				}

				return str;

			}

		}

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

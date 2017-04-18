package cc.wulian.app.model.device.impls.controlable.ems;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.AbstractSwitchDevice;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView.DeviceCategoryEntity;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

/**
 * 控制 ： 10 关， 11 开， 12 查询， 13 切换， 21yymmddyymmdd:查询区间用电量 50:读所有计量数据<br/>
 * <br/>
 * 
 * (十六进制)<br/>
 * 位1～2:01(表示状态)<br/>
 * 位3～4:状态值(00:关,01:开,FF:未知)<br/>
 * (以下可无)<br/>
 * 位5～6:02(表示查询数据)<br/>
 * 位7～8:查询类型(01:电量)<br/>
 * 位9～16:电量(单位:0.001KW·h)<br/>
 * 位5～6:05(表示计量参数)<br/>
 * 位7～8:计量类型(00:全部)<br/>
 * 位9～12:电流(单位:0.001A)<br/>
 * 位13～16:电压(单位:0.1V)<br/>
 * 位17～20:频率(单位:0.1HZ)<br/>
 * 位21～26:功率(单位:0.1W)<br/>
 * 位27～34:电量(单位:0.001KW·h)
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_EMS }, category = Category.C_CONTROL)
public class WL_15_Ems extends AbstractSwitchDevice {
	private Map<String, Map<Integer, Integer>> categoryIcons = DeviceUtil
			.getSRDockCategoryDrawables();
	private static final String DEVICE_STATE_01 = "01";
	protected static  String DATA_CTRL_STATE_OPEN_01 = "01";
	protected static  String DATA_CTRL_STATE_CLOSE_00 = "00";

	private static final String DEVICE_QUERY_02 = "02";
	private static final String DEVICE_QUERY_TYPE_01 = "01";

	private static final String DEVICE_QUERY_05 = "05";
	private static final String DEVICE_QUERY_TYPE_00 = "00";

	protected static final String DATA_SEND_CMD_OPEN_11 = "11";
	protected static final String DATA_SEND_CMD_CLOSE_10 = "10";

	private static final String DATA_PROTOCOL_OPEN = DEVICE_STATE_01
			+ DATA_CTRL_STATE_OPEN_01;
	private static final String DATA_PROTOCOL_CLOSE = DEVICE_STATE_01
			+ DATA_CTRL_STATE_CLOSE_00;

	protected static int SMALL_OPEN_D = R.drawable.device_calc_dock_open;
	protected static int SMALL_CLOSE_D = R.drawable.device_calc_dock_close;

	protected static int BIG_OPEN_D = R.drawable.device_calc_dock_open_big;
	protected static int BIG_CLOSE_D = R.drawable.device_calc_dock_close_big;
	protected static final String UNIT_A = " A";
	protected static final String UNIT_V = " V";
	protected static final String UNIT_HZ = " HZ";
	protected static final String UNIT_W = " W";
	protected static final String UNIT_KW_H = " kW·h";

	protected ImageView mBottomView;

	protected TextView KW_H_TextView;
	protected TextView A_TextView;
	protected TextView V_TextView;
	protected TextView HZ_TextView;
	protected TextView W_TextView;

	public WL_15_Ems(Context context, String type) {
		super(context, type);
	}

	@Override
	public boolean isOpened() {
		if (isNull(epData))
			return false;

		return epData.startsWith(DATA_PROTOCOL_OPEN);
	}

	@Override
	public boolean isClosed() {
		if (isNull(epData))
			return true;

		return epData.startsWith(DATA_PROTOCOL_CLOSE);
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_SEND_CMD_OPEN_11;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_SEND_CMD_CLOSE_10;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_ems_two, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		mBottomView = (ImageView) view.findViewById(R.id.dev_state_imageview_0);
		KW_H_TextView = (TextView) view.findViewById(R.id.dev_textview_0);
		A_TextView = (TextView) view.findViewById(R.id.dev_textview_1);
		V_TextView = (TextView) view.findViewById(R.id.dev_textview_2);
		HZ_TextView = (TextView) view.findViewById(R.id.dev_textview_3);
		W_TextView = (TextView) view.findViewById(R.id.dev_textview_4);

		KW_H_TextView.setGravity(Gravity.CENTER);
		A_TextView.setGravity(Gravity.CENTER);
		V_TextView.setGravity(Gravity.CENTER);
		HZ_TextView.setGravity(Gravity.CENTER);
		W_TextView.setGravity(Gravity.CENTER);
		mBottomView.setImageDrawable(getStateBigPictureArray()[0]);
		mBottomView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, null,
						true);
			}
		});
		mViewCreated = true;
	}

	@Override
	public void initViewStatus() {
		mBottomView.setImageDrawable(getStateBigPictureArray()[0]);
		if (epData.length() <= 4)
			return;

		String emsAllData = epData.substring(4);
		String ems5_6 = emsAllData.substring(0, 2);
		String ems7_8;
		String ems9_34;
		if (DEVICE_QUERY_02.equals(ems5_6) && emsAllData.length() > 4) {
			ems7_8 = emsAllData.substring(2, 4);
			ems9_34 = emsAllData.substring(4);
			if (DEVICE_QUERY_TYPE_01.equals(ems7_8) && ems9_34.length() == 6) {
				double ems9_14 = StringUtil.toInteger(emsAllData.substring(4),
						16) / 10.0;
				KW_H_TextView.setText(getResources().getString(
						R.string.device_ems_electricity)
						+ ems9_14 + UNIT_KW_H);
			}
		} else if (DEVICE_QUERY_05.equals(ems5_6) && emsAllData.length() > 4) {
			ems7_8 = emsAllData.substring(2, 4);
			ems9_34 = emsAllData.substring(4);
			if (DEVICE_QUERY_TYPE_00.equals(ems7_8) && ems9_34.length() == 26
					&& emsAllData.length() >= 30) {

				double ems9_12 = Math.round(StringUtil.toInteger(
						emsAllData.substring(4, 8), 16)) / 1000.0;
				double ems27_34 = Math.round(((StringUtil.toInteger(
						emsAllData.substring(22, 30), 16) + 0.5D) / 1000) * 10) / 10.0;
				StringBuilder ems18_36 = new StringBuilder();
				ems18_36.append(
						getResources().getString(
								R.string.device_ems_electricity))
						.append(ems27_34).append(UNIT_KW_H);
				KW_H_TextView.setText(ems18_36);
				StringBuilder ems9_14 = new StringBuilder();
				ems9_14.append(
						getResources().getString(
								R.string.device_ems_electrical_current))
						.append(ems9_12).append(UNIT_A);
				A_TextView.setText(ems9_14);

				double ems13_16 = StringUtil.toInteger(
						emsAllData.substring(8, 12), 16) / 10.0;
				double ems17_20 = StringUtil.toInteger(
						emsAllData.substring(12, 16), 16) / 10.0;
				double ems21_26 = StringUtil.toInteger(
						emsAllData.substring(16, 22), 16) / 10.0;

				StringBuilder ems15_18 = new StringBuilder();
				ems15_18.append(
						getResources().getString(R.string.device_ems_voltage))
						.append(ems13_16).append(UNIT_V);
				V_TextView.setText(ems15_18);
				StringBuilder ems19_22 = new StringBuilder();
				ems19_22.append(
						getResources().getString(R.string.device_ems_frequency))
						.append(ems17_20).append(UNIT_HZ);
				HZ_TextView.setText(ems19_22);
				StringBuilder ems24_30 = new StringBuilder();
				ems24_30.append(
						getResources().getString(R.string.device_ems_power))
						.append(ems21_26).append(UNIT_W);
				W_TextView.setText(ems24_30);

			}
		}
	}

	@Override
	public void setResourceByCategory() {
		Map<Integer, Integer> dockMap = categoryIcons.get(getDeviceCategory());
		if (dockMap != null && dockMap.size() >= 4) {
			SMALL_OPEN_D = dockMap.get(0);
			SMALL_CLOSE_D = dockMap.get(1);
			BIG_OPEN_D = dockMap.get(2);
			BIG_CLOSE_D = dockMap.get(3);
		}
	}

	@Override
	public EditDeviceInfoView onCreateEditDeviceInfoView(LayoutInflater inflater) {
		EditDeviceInfoView view = super.onCreateEditDeviceInfoView(inflater);
		ArrayList<DeviceCategoryEntity> entities = new ArrayList<EditDeviceInfoView.DeviceCategoryEntity>();
		for (String key : categoryIcons.keySet()) {
			DeviceCategoryEntity entity = new DeviceCategoryEntity();
			entity.setCategory(key);
			entity.setResources(categoryIcons.get(key));
			entities.add(entity);
		}
		view.setDeviceIcons(entities);
		return view;
	}

	@Override
	public int getOpenSmallIcon() {
		return SMALL_OPEN_D;
	}

	@Override
	public int getCloseSmallIcon() {
		return SMALL_CLOSE_D;
	}

	@Override
	public int getOpenBigPic() {
		return BIG_OPEN_D;
	}

	@Override
	public int getCloseBigPic() {
		return BIG_CLOSE_D;
	}
}

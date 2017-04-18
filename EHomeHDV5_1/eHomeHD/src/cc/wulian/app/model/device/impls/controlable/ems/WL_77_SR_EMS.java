package cc.wulian.app.model.device.impls.controlable.ems;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;

import java.util.Map;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

/**
 * <p>
 * send control : <br>
 * 10:关 <br>
 * 11:开 <br>
 * 12:查询 <br>
 * 13:切换 <br>
 * 8xxxx:设置断电保护的最大功率(单位为W,0500-3000)
 * <p>
 * receive data : (十六进制) <br>
 * 09xxyyyyzzzzzz: 09表示当前状态; <br>
 * xx表示开关状态(00:关,01:开); <br>
 * yyyy表示当前功率(单位W,0x0000-0xFFFF); <br>
 * zzzzzz表示当前累计电量(单位WH,0x000000-0xF42400,最大16000KWH);
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_EMS_SR }, category = Category.C_CONTROL)
public class WL_77_SR_EMS extends WL_15_Ems {
	private static final String DATA_CTRL_PREFIX = "09";	//表示当前状态
	private static final String DATA_CTRL_PREFIX_SET = "8";	//设置断电保护最大功率

	private static final String DATA_CTRL_STATE_OPEN_01 = "01";		//开状态
	private static final String DATA_CTRL_STATE_CLOSE_00 = "00";	//关状态

	private static final String DATA_PROTOCOL_OPEN = createCompoundCmd(
			DATA_CTRL_PREFIX, DATA_CTRL_STATE_OPEN_01);
	private static final String DATA_PROTOCOL_CLOSE = createCompoundCmd(
			DATA_CTRL_PREFIX, DATA_CTRL_STATE_CLOSE_00);
	//断点保护最大功率
	private static final int DATA_CTRL_SET_MIN = 500;
	private static final int DATA_CTRL_SET_MAX = 3000;

	private static int SMALL_OPEN_D = R.drawable.device_calc_dock_open;
	private static int SMALL_CLOSE_D = R.drawable.device_calc_dock_close;

	private static int BIG_OPEN_D = R.drawable.device_calc_dock_open_big;
	private static int BIG_CLOSE_D = R.drawable.device_calc_dock_close_big;
	private Map<String, Map<Integer, Integer>> categoryIcons = DeviceUtil.getSRDockCategoryDrawables();

	/**
	 * 刷新界面
	 * @param epData
	 */
	private void disassembleCompoundCmd(String epData) {
		if (isNull(epData))
			return;
		if (epData.length() < 4)
			return;
		if (!epData.startsWith(DATA_CTRL_PREFIX))
			return;

		// same data, no need disassemble
		if (isSameAs(epData, this.mLastEpData))
			return;

		String mode = this.substring(epData, 0, 4);
		String w = this.substring(epData, 4, 8);
		String kwh = this.substring(epData, 8, 14);

		this.mControlMode = mode;
		this.mW = StringUtil.toInteger(w, 16);
		java.text.DecimalFormat   df=new   java.text.DecimalFormat("######0.00");
		this.mKWH =df.format(Math.round(StringUtil.toInteger(kwh, 16) + 0.5D) / 1000D);

	}

	private String mLastEpData;

	private String mControlMode;
	private int mW;
	private String mKWH;


	public WL_77_SR_EMS(Context context, String type) {
		super(context, type);
	}

	@Override
	public void refreshDevice() {
		super.refreshDevice();

		disassembleCompoundCmd(epData);
	}

	@Override
	public void setResourceByCategory() {
		Map<Integer, Integer> iconMap = categoryIcons.get(getDeviceCategory());
		if (iconMap != null && iconMap.size() >= 4) {
			SMALL_OPEN_D = iconMap.get(0);
			SMALL_CLOSE_D = iconMap.get(1);
			BIG_OPEN_D = iconMap.get(2);
			BIG_CLOSE_D = iconMap.get(3);
		}
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

	@Override
	public boolean isOpened() {
		return isSameAs(DATA_PROTOCOL_OPEN, mControlMode);
	}

	@Override
	public boolean isClosed() {
		return isSameAs(DATA_PROTOCOL_CLOSE, mControlMode);
	}

	/*
	 * @Override public String controlDevice( String sendData ){ // if not null
	 * or not same as open(stop, close) cmd, add prefix if(!isNull(sendData) &&
	 * !isSameAs(getOpenSendCmd(), sendData) && !isSameAs(getCloseSendCmd(),
	 * sendData)){ int dataInt = StringUtil.toInteger(sendData);
	 * 
	 * // see top protocol if (dataInt < DATA_CTRL_SET_MIN || dataInt >
	 * DATA_CTRL_SET_MAX) dataInt = DATA_CTRL_SET_MIN; sendData =
	 * DATA_CTRL_PREFIX_SET + String.format("%04d", dataInt); } return
	 * super.controlDevice(sendData); }
	 */

	@Override
	public void initViewStatus() {
		Drawable drawable = getStateBigPictureArray()[0];
		mBottomView.setImageDrawable(drawable);
		drawable = mBottomView.getDrawable();
		if (drawable instanceof AnimationDrawable) {
			((AnimationDrawable) drawable).start();
		}
		String curW = getString(R.string.device_ems_power);
		A_TextView.setText(curW);
		HZ_TextView.setText(mW + UNIT_W);
		String allKWH = getString(R.string.device_ems_electricity);
		KW_H_TextView.setText(allKWH);
		V_TextView.setText(mKWH + UNIT_KW_H);
	}

	// @Override
	// public Intent getSettingIntent() {
	// Intent intent = new Intent(mContext, EditDeviceStatusActivity.class);
	// intent.putExtra(DeviceOneTranslatorFragment.GWID, gwID);
	// intent.putExtra(DeviceOneTranslatorFragment.DEVICEID, devID);
	// intent.putExtra(EditDeviceStatusActivity.DEVICE_ONE_TRANSLATOR,
	// ConstUtil.DEV_TYPE_FROM_GW_EMS_SR);
	// intent.putExtra(AbstractDevice.SETTING_LINK_TYPE,
	// AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
	// return intent;
	// }

}

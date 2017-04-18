package cc.wulian.app.model.device.impls.alarmable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * <p>
 * send control : 10:消除报警
 * <p>
 * receive data : (十六进制) <br>
 * 位1～2:报警类型(01:烟雾)<br>
 * 位3～4:报警状态(00:正常,01:报警)
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_FIRE_SR }, category = Category.C_SECURITY)
public class WL_43_Fire_2 extends DefaultAlarmableDeviceImpl
{
	private static final String DATA_ALARM_STATE_NORMAL_00 = "0100";
	private static final String DATA_ALARM_STATE_ALARM_01 = "0101";

	private static final String DATA_CTRL_STATE_10 = "10";
	
	public WL_43_Fire_2(Context context, String type) {
		super(context, type);
	}

	@Override
	public boolean isLongDefenSetup() {
		return true;
	}
	
	@Override
	public String getCancleAlarmProtocol() {
		return DATA_CTRL_STATE_10;
	}

	@Override
	public boolean isAlarming() {
		return DATA_ALARM_STATE_ALARM_01.equals(epData);
	}

	@Override
	public boolean isNormal() {
		return DATA_ALARM_STATE_NORMAL_00.equals(epData);
	}

	@Override
	public String getAlarmProtocol() {
		return DATA_ALARM_STATE_ALARM_01;
	}

	@Override
	public String getNormalProtocol() {
		return DATA_ALARM_STATE_NORMAL_00;
	}


	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater) {
		return getDefaultShortCutControlView(item,inflater);
	}
	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		Drawable normalStateDrawable = getDrawable(R.drawable.device_smoke_normal_icon);
		drawables[0] = normalStateDrawable;
		return drawables;
	}
	@Override
	public Drawable getStateSmallIcon() {
		return getStateSmallIconDrawable(getDrawable(R.drawable.device_smoke_disarm),getDrawable(R.drawable.device_smoke_alarm));
	}
	
	@Override
	public CharSequence parseAlarmProtocol(String epData) {
		StringBuilder sb = new StringBuilder();
		sb.append(DeviceTool.getDeviceAlarmAreaName(this));
		sb.append(DeviceTool.getDeviceShowName(this));
		if(LanguageUtil.isChina() || LanguageUtil.isTaiWan()){
			sb.append(mContext.getString(R.string.home_device_alarm_default_voice_detect));
		}else{
			sb.append(" "+ mContext.getString(R.string.home_device_alarm_default_voice_detect) + " ");
		}
		sb.append(mContext.getString(R.string.home_device_alarm_type_07_voice));
		return sb.toString();
	}
	
	@Override
	public String getAlarmString() {
		return mResources.getString(R.string.home_device_alarm_type_07_voice);
	}

	@Override
	public String getNormalString() {
		return mResources.getString(R.string.home_device_trigger_select_cancle_alarm);
	}
}
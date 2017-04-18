package cc.wulian.app.model.device.impls.alarmable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.LanguageUtil;

@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_MOTION}, category = Category.C_SECURITY)
public class WL_02_IR_Sensors extends DefaultAlarmableDeviceImpl{

	public WL_02_IR_Sensors(Context context, String type) {
		super(context, type);
	}
	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		Drawable normalStateDrawable = getDrawable(R.drawable.device_pir_sensor_normal_icon);
		drawables[0] = normalStateDrawable;
		return drawables;
	}
	@Override
	public Drawable getStateSmallIcon() {
		return getStateSmallIconDrawable(getDrawable(R.drawable.device_pir_sensor_disarm),getDrawable(R.drawable.device_pir_sensor_alarm));
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
		sb.append(mContext.getString(R.string.home_device_alarm_type_02_voice));
		return sb.toString();
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		return mContext.getString(R.string.home_device_alarm_type_02_voice);
	}

	@Override
	public String getAlarmString() {
		return mResources.getString(R.string.home_device_alarm_type_02_voice);
	}

	@Override
	public String getNormalString() {
		return mResources.getString(R.string.device_state_normal);
	}
}

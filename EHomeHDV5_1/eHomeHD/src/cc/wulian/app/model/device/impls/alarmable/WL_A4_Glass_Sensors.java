package cc.wulian.app.model.device.impls.alarmable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.LanguageUtil;

@DeviceClassify(devTypes = ConstUtil.DEV_TYPE_FROM_GW_GLASS_BROKEN, category = Category.C_SECURITY)
public class WL_A4_Glass_Sensors extends DefaultAlarmableDeviceImpl{

	private static final String DATA_CTRL_STATE_0 = "0";
	
	public WL_A4_Glass_Sensors(Context context, String type) {
		super(context, type);
	}

	@Override
	public String getCancleAlarmProtocol() {
		return DATA_CTRL_STATE_0;
	}
	
	@Override
	public boolean isLongDefenSetup() {
		return true;
	}
	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		Drawable normalStateDrawable = getDrawable(R.drawable.device_glass_broken_normal_icon);
		drawables[0] = normalStateDrawable;
		return drawables;
	}
	@Override
	public Drawable getStateSmallIcon() {
		return getStateSmallIconDrawable(getDrawable(R.drawable.device_glass_broken_disarm),getDrawable(R.drawable.device_glass_broken_alarm));
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
		sb.append(mContext.getString(R.string.home_device_alarm_type_A4_voice));
		return sb.toString();
	}
	
	@Override
	public String getAlarmString() {
		return mResources.getString(R.string.home_device_alarm_type_A4_voice);
	}

	@Override
	public String getNormalString() {
		return mResources.getString(R.string.device_state_normal);
	}
}

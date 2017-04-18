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

@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_FIRE}, category = Category.C_SECURITY)
public class WL_07_Smoke_Sensors extends DefaultAlarmableDeviceImpl{

	public WL_07_Smoke_Sensors(Context context, String type) {
		super(context, type);
	}
	
	@Override
	public boolean isLongDefenSetup() {
		return true;
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

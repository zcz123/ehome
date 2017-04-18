package cc.wulian.app.model.device.impls.alarmable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.LanguageUtil;

@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_EMERGENCY}, category = Category.C_SECURITY)
public class WL_04_Emergency_Button extends DefaultAlarmableDeviceImpl{

	public WL_04_Emergency_Button(Context context, String type) {
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
		Drawable normalStateDrawable = getDrawable(R.drawable.device_dangerbutton_normal_icon);
		drawables[0] = normalStateDrawable;
		return drawables;
	}
	@Override
	public Drawable getStateSmallIcon() {
		return getStateSmallIconDrawable(getDrawable(R.drawable.device_dangerbutton_disarm),getDrawable(R.drawable.device_dangerbutton_alarm));
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
		sb.append(mContext.getString(R.string.home_device_alarm_type_04_voice));
		return sb.toString();
	}
	@Override
	public CharSequence parseDestoryProtocol(String epData) {
		String str = DeviceTool.getDeviceAlarmAreaName(this)
				+ DeviceTool.getDeviceShowName(this);
        if (LanguageUtil.isChina() || LanguageUtil.isTaiWan()) {
            if (!StringUtil.isNullOrEmpty(epData) && epData.equals("0201")) {
                str = str + mContext.getString(R.string.home_device_alarm_default_voice_detect)
                        + mContext.getString(R.string.home_device_alarm_default_voice_move);
            } else if (!StringUtil.isNullOrEmpty(epData) && epData.equals("0101")) {
                str = str + mContext.getString(R.string.home_device_alarm_default_voice_detect)
                        + mContext.getString(R.string.home_message_low_power_warn);
            }
        } else {
            if (!StringUtil.isNullOrEmpty(epData) && epData.equals("0201")) {
                str = str + " " + mContext.getString(R.string.home_device_alarm_default_voice_detect)
                        + mContext.getString(R.string.home_device_alarm_default_voice_move);
            } else if (!StringUtil.isNullOrEmpty(epData) && epData.equals("0101")) {
                str = str + " " + mContext.getString(R.string.home_device_alarm_default_voice_detect)
                        + mContext.getString(R.string.home_message_low_power_warn);
            }
        }
		return str;
	}
	
	@Override
	public String getAlarmString() {
		return mResources.getString(R.string.home_device_alarm_type_04_voice_remind);
	}

	@Override
	public String getNormalString() {
		return mResources.getString(R.string.home_device_trigger_select_cancle_alarm);
	}
}

package cc.wulian.app.model.device.impls.alarmable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.LanguageUtil;

@DeviceClassify(devTypes = ConstUtil.DEV_TYPE_FROM_GW_MOTION_F, category = Category.C_SECURITY)
public class WL_05_Electronic_Fence extends DefaultAlarmableDeviceImpl{

	private static final String DATA_CTRL_STATE_0 = "0";
	
	public WL_05_Electronic_Fence(Context context, String type) {
		super(context, type);
	}
	@Override
	public String getCancleAlarmProtocol() {
		return DATA_CTRL_STATE_0;
	}
	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		Drawable normalStateDrawable = getDrawable(R.drawable.device_motion_fence_normal_icon);
		drawables[0] = normalStateDrawable;
		return drawables;
	}
	@Override
	public Drawable getStateSmallIcon() {
		return getStateSmallIconDrawable(getDrawable(R.drawable.device_motion_fence_disarm),getDrawable(R.drawable.device_motion_fence_alarm));
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
		sb.append(mContext.getString(R.string.home_device_alarm_type_05_voice));
		return sb.toString();
	}
//	protected View getDefaultShortCutControlView(LayoutInflater inflater) {
//		if(inflater.getContext() != this.mContext || shortCutControlItem == null){
//			shortCutControlItem = new DeviceShortCutControlItem(inflater.getContext());
//		}
//		shortCutControlItem.setWulianDevice(this);
//		return shortCutControlItem.getView();
//	}
	@Override
	public String getAlarmString() {
		return mResources.getString(R.string.home_device_alarm_type_05_voice);
	}

	@Override
	public String getNormalString() {
		return mResources.getString(R.string.device_state_normal);
	}
	
}

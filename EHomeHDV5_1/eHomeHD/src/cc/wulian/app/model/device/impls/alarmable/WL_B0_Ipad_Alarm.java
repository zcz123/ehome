package cc.wulian.app.model.device.impls.alarmable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;

@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_IPADWARNING}, category = Category.C_SECURITY)
public class WL_B0_Ipad_Alarm extends DefaultAlarmableDeviceImpl{

	public WL_B0_Ipad_Alarm(Context context, String type) {
		super(context, type);
	}

	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		Drawable normalStateDrawable = getDrawable(R.drawable.device_warning_ipad_icon);
		drawables[0] = normalStateDrawable;
		return drawables;
	}
	@Override
	public Drawable getStateSmallIcon() {
		return getStateSmallIconDrawable(getDrawable(R.drawable.device_warning_ipad2),getDrawable(R.drawable.device_warning_ipad1));
	}
}

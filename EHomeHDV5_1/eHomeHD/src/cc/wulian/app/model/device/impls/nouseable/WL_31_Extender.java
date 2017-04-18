package cc.wulian.app.model.device.impls.nouseable;

import android.content.Context;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;

@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_EXTENDER}, 
		category = Category.C_OTHER)
public class WL_31_Extender extends AbstractNotUseableDevice
{
	public WL_31_Extender( Context context, String type )
	{
		super(context, type);
	}
}
package cc.wulian.app.model.device.impls.nouseable;

import android.content.Context;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;

@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_BUTTON_3}, 
		category = Category.C_OTHER)
public class WL_55_Button_3 extends AbstractNotUseableDevice
{
	public WL_55_Button_3( Context context, String type )
	{
		super(context, type);
	}


}

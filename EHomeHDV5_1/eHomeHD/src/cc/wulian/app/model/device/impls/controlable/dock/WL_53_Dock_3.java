package cc.wulian.app.model.device.impls.controlable.dock;

import android.content.Context;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 *	0:关,1:开,255:异常
 */
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_DOCK_3}, 
		category = Category.C_CONTROL)
public class WL_53_Dock_3 extends WL_51_Dock_2
{
	public WL_53_Dock_3( Context context, String type )
	{
		super(context, type);
	}
}

package cc.wulian.app.model.device.impls.controlable.light;

import android.content.Context;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 *	0:关,1:开,255:异常
 */
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_LIGHT_4}, 
		category = Category.C_LIGHT)
public class WL_64_Light_4 extends WL_63_Light_3
{
	private static final int SMALL_OPEN_D = R.drawable.device_button_4_open;
	private static final int SMALL_CLOSE_D 	= R.drawable.device_button_4_close;
	private static final String[] EP_SEQUENCE = {EP_14, EP_15, EP_16, EP_17};
	
	public WL_64_Light_4( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public int getOpenSmallIcon(){
		return SMALL_OPEN_D;
	}

	@Override
	public int getCloseSmallIcon(){
		return SMALL_CLOSE_D;
	}


	@Override
	public String[] getLightEPResources() {
		return EP_SEQUENCE;
	}

	@Override
	public String[] getLightEPNames() {
		String ep14Name = DeviceUtil.ep2IndexString(EP_14)+getResources().getString(R.string.device_type_11);
		String ep15Name = DeviceUtil.ep2IndexString(EP_15)+getResources().getString(R.string.device_type_11);
		String ep16Name = DeviceUtil.ep2IndexString(EP_16)+getResources().getString(R.string.device_type_11);
		String ep17Name = DeviceUtil.ep2IndexString(EP_17)+getResources().getString(R.string.device_type_11);
		return new String[]{ep14Name,ep15Name,ep16Name,ep17Name};
	}
	
}

package cc.wulian.app.model.device.impls.sensorable;

import android.content.Context;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 * float(单位M3)
 */
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_FLOW}, 
		category = Category.C_ENVIRONMENT)
public class WL_41_Flow extends SensorableDeviceImpl
{
	public static final String UNIT_M3 = " M3";
	
	public WL_41_Flow( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData){
		StringBuilder sb = new StringBuilder();
		sb.append(epData);
		sb.append(UNIT_M3);
		return sb;
	}

	@Override
	public String unit(String ep,String epType) {
		return UNIT_M3;
	}

	@Override
	public String unitName() {
		return mContext.getString(R.string.device_flow);
	}
	
}
package cc.wulian.app.model.device.impls.sensorable;

import android.content.Context;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 * int(单位PPM)
 */
@Deprecated
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_VOC}, 
		category = Category.C_ENVIRONMENT)
public class WL_20_VOC extends AbstractScanAnimSensorDevice
{
	private static final String UNIT_PPM 								= " PPM";
	
	private static final int 	 BIG_NORMAL_D 						= R.drawable.device_voc_normal_big;
	
	public WL_20_VOC( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData){
		StringBuilder sb = new StringBuilder();


		sb.append(epData);
		sb.append(UNIT_PPM);
		return sb;
	}

	@Override
	public int getScanStateNormalRes(){
		return BIG_NORMAL_D;
	}

	@Override
	public void onInitViewState( TextView topView, TextView midView, TextView bottomView ){
		topView.setText(this.parseDataWithProtocol(epData));
	}

	@Override
	public String unit(String ep,String epType) {
		return "PPM";
	}

	@Override
	public String unitName() {
		return mResources.getString(R.string.device_voc);
	}
	
}
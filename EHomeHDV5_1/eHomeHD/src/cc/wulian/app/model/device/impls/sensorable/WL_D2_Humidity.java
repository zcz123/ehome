package cc.wulian.app.model.device.impls.sensorable;

import android.content.Context;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.interfaces.LinkTaskBodyHumView;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_HUMIDITY}, 
		category = Category.C_ENVIRONMENT)
public class WL_D2_Humidity extends SensorableDeviceImpl{
	private static final String UNIT_C 	= "\u00B0C";
	private static final String UNIT_RH = "%";
	public WL_D2_Humidity(Context context, String type) {
		super(context, type);
	}

	@Override
	public String unit(String ep,String epType) {
		return UNIT_RH;
	}

	@Override
	public String unitName() {
		return getString(R.string.device_humidity);
	}

	@Override
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,
			TaskInfo taskInfo) {
		LinkTaskBodyHumView taskView = new LinkTaskBodyHumView(context, taskInfo);
		taskView.onCreateView();
		return taskView;
	}

}

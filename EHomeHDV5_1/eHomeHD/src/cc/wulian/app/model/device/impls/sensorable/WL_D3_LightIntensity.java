package cc.wulian.app.model.device.impls.sensorable;

import android.content.Context;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.interfaces.LinkTaskBodyLightView;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_LIGHT_INTENSITY}, 
		category = Category.C_ENVIRONMENT)
public class WL_D3_LightIntensity extends SensorableDeviceImpl{
	private static final String UNIT_LUX 	= "LUX";
	public WL_D3_LightIntensity(Context context, String type) {
		super(context, type);
	}

	@Override
	public String unit(String ep,String epType) {
		return UNIT_LUX;
	}

	@Override
	public String unitName() {
		return getString(R.string.device_illumination);
	}

	@Override
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,
			TaskInfo taskInfo) {
		LinkTaskBodyLightView taskView = new LinkTaskBodyLightView(context, taskInfo);
		taskView.onCreateView();
		return taskView;
	}
}

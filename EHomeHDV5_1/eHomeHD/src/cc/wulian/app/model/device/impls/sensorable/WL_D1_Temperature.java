package cc.wulian.app.model.device.impls.sensorable;

import android.content.Context;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.interfaces.LinkTaskBodyTempView;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_TEMPERATURE}, 
		category = Category.C_ENVIRONMENT)
public class WL_D1_Temperature extends SensorableDeviceImpl{
	private static final String UNIT_C 	= "\u00B0C";
	public WL_D1_Temperature(Context context, String type) {
		super(context, type);
	}

	@Override
	public String unit(String ep,String epType) {
		return UNIT_C;
	}

	@Override
	public String unitName() {
		return getString(R.string.device_tempure);
		
	}

	@Override
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,
			TaskInfo taskInfo) {
		LinkTaskBodyTempView taskView = new LinkTaskBodyTempView(context, taskInfo);
		taskView.onCreateView();
		return taskView;
	}

	
}

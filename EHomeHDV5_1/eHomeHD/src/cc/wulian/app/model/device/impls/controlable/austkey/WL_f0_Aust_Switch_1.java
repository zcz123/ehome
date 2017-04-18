package cc.wulian.app.model.device.impls.controlable.austkey;

import android.content.Context;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_SWITCH_KEY_1 }, category = Category.C_OTHER)
public class WL_f0_Aust_Switch_1 extends AbstractAustKeyDevice {
	private static final String[] EP_LIGHT = { EP_14 };
	private static final String[] EP_SEQUENCE = { EP_15 };
	private String ep14Name;
	private String ep15Name;

	public WL_f0_Aust_Switch_1(Context context, String type) {
		super(context, type);
	}

	@Override
	public String[] getLightEPInfo() {
		return EP_LIGHT;
	}

	@Override
	public String[] getSceneSwitchEPResources() {
		return EP_SEQUENCE;
	}

	
	@Override
	public String[] getSceneSwitchEPNames() {
		ep15Name = getResources().getString(R.string.device_bind_scene);
		return new String[] { ep15Name };
	}

	@Override
	public String[] getSwitchEPName() {
		ep14Name = getChildDevice(EP_14).getDeviceInfo().getDevEPInfo()
				.getEpName();
		if (StringUtil.isNullOrEmpty(ep14Name)) {
			ep14Name = getResources().getString(
					R.string.device_aust_switch_key_1);
		}
		return new String[] { ep14Name };
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(R.string.device_aust_key_1));
		return sb.toString();
	}

}

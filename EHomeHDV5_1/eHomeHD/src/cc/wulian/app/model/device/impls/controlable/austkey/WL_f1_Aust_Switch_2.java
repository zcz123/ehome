package cc.wulian.app.model.device.impls.controlable.austkey;

import android.content.Context;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_SWITCH_KEY_2 }, category = Category.C_OTHER)
public class WL_f1_Aust_Switch_2 extends AbstractAustKeyDevice {
	private static final String[] EP_LIGHT = { EP_14, EP_15 };
	private static final String[] EP_SEQUENCE = { EP_16, EP_17 };

	private String ep14Name;
	private String ep15Name;
	private String ep16Name;
	private String ep17Name;

	public WL_f1_Aust_Switch_2(Context context, String type) {
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
		ep16Name = getResources().getString(R.string.device_aust_scene_key_1);
		ep17Name = getResources().getString(R.string.device_aust_scene_key_2);
		return new String[] { ep16Name, ep17Name };
	}

	@Override
	public String[] getSwitchEPName() {
		ep14Name = getChildDevice(EP_14).getDeviceInfo().getDevEPInfo()
				.getEpName();
		if (StringUtil.isNullOrEmpty(ep14Name)) {
			ep14Name = getResources().getString(
					R.string.device_aust_switch_key_1);
		}
		ep15Name = getChildDevice(EP_15).getDeviceInfo().getDevEPInfo()
				.getEpName();
		if (StringUtil.isNullOrEmpty(ep15Name)) {
			ep15Name = getResources().getString(
					R.string.device_aust_switch_key_2);
		}
		return new String[] { ep14Name, ep15Name };
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(R.string.device_aust_key_2));
		return sb.toString();
	}

}

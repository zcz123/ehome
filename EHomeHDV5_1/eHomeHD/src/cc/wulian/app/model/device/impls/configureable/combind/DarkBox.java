package cc.wulian.app.model.device.impls.configureable.combind;

import android.content.Context;
import cc.wulian.app.model.device.impls.configureable.ConfigureableDeviceImpl;
import cc.wulian.ihome.wan.util.ConstUtil;

public class DarkBox extends ConfigureableDeviceImpl{

	public static String[] CONFIGBOXTYPE = new String[]{ConstUtil.DEV_TYPE_FROM_GW_TOUCH_2}; 
	public DarkBox(Context context, String type) {
		super(context, type);
	}
	
}

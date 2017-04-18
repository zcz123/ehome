package cc.wulian.app.model.device.impls.controlable.ems;

import android.content.Context;

public class WL_72_EMS extends WL_15_Ems {

	private static final String DATA_CTRL_PREFIX = "09";	//表示当前状态
	private static final String DATA_CTRL_PREFIX_SET = "8";	//设置断电保护的最大功率
	
	private static final String DATA_CTRL_STATE_OPEN_01 = "01";		//开状态
	private static final String DATA_CTRL_STATE_CLOSE_00 = "00";	//关状态
	public WL_72_EMS(Context context, String type) {
		super(context, type);
	}

}

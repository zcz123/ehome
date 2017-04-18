package cc.wulian.app.model.device.interfaces;


import java.util.Map;

import android.content.Context;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;

/**
 * one device has more ep
 */
public interface IMultiEpDevice
{
	public WulianDevice createDeviceByEp(Context context, DeviceEPInfo deviceEPInfo, Map<String, WulianDevice> dualEpMap);

	public WulianDevice getChildDeviceByEp( CharSequence ep );

	public CreateDeviceInterface getCreateDeviceInterface();
}

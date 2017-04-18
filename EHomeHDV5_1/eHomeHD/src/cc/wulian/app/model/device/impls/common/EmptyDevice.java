package cc.wulian.app.model.device.impls.common;

import android.content.Context;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.nouseable.AbstractNotUseableDevice;

/**
 * for unknow device type
 */
@DeviceClassify()
public class EmptyDevice extends AbstractNotUseableDevice
{
	public EmptyDevice( Context context, String type )
	{
		super(context, type);
	}

}

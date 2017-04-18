package cc.wulian.app.model.device.interfaces;

import android.content.Context;
import cc.wulian.app.model.device.WulianDevice;

/**
 * one device has more ep, but every ep has the same type````orz
 */
public interface IMultiEpSameTypeDevice extends IMultiEpDevice
{
	public WulianDevice getAchieveEpDevice( Context context, String type );
}

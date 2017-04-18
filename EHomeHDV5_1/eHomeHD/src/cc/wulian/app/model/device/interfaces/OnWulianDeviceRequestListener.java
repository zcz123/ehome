package cc.wulian.app.model.device.interfaces;

import cc.wulian.app.model.device.WulianDevice;

/**
 * use for controler submit control device state
 */
public interface OnWulianDeviceRequestListener
{
	/**
	 * when a device request control or set itself in model, we notify this request
	 */
	public void onDeviceRequestControlSelf( WulianDevice device);
	public void onDeviceRequestControlData(WulianDevice device);
}

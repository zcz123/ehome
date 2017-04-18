package cc.wulian.app.model.device.interfaces;

/**
 * use for notify other registrant device's state change
 */
public interface OnWulianDeviceStateChangeListener
{
	/**
	 * when device's online state changed, will notify other from this listener
	 */
	public void onDeviceOnLineStateChange( boolean onLine );

	// this time add online state listener, will add some other listener later
}

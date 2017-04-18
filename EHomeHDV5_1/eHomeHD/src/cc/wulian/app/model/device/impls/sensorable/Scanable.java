package cc.wulian.app.model.device.impls.sensorable;

import android.graphics.drawable.Drawable;

public interface Scanable
{
	public ScanAnimationInfo getScanAnimationInfo();
	/**
	 * entrty for animation
	 */
	public class ScanAnimationInfo
	{
		Drawable sensorStateDrawable;
		float scanSpeed;
	}
}

package cc.wulian.app.model.device.interfaces;

import android.graphics.drawable.Drawable;

/**
 * must use it
 */
public interface IProperties
{
	/**
	 * 默认状态返回的设备状态图（小）
	 */
	public Drawable getDefaultStateSmallIcon();

	public String getDefaultDeviceName();
	/**
	 * 根据状态返回的设备状态图（小）
	 */
	public Drawable getStateSmallIcon();
	
	/**
	 * 根据状态返回的设备状态图数组（大）
	 */
	public Drawable[] getStateBigPictureArray();
	
	/**
	 * parse data to string ,like data = 0 means open
	 * 
	 * @param isSimpleShow
	 *          is parse data for simple show
	 */
	public CharSequence parseDataWithProtocol(String epData);

	/**
	 * parse data to string with extData
	 *
	 * @param
	 *          is parse data for simple show
	 */
	public CharSequence parseDataWithExtData(String extData);
}

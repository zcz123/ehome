package cc.wulian.app.model.device;

import java.util.Map;

import android.content.Context;
import android.support.v4.app.Fragment;
import cc.wulian.app.model.device.interfaces.IProperties;
import cc.wulian.app.model.device.interfaces.IViewResource;
import cc.wulian.app.model.device.interfaces.OnWulianDeviceRequestListener;
import cc.wulian.app.model.device.interfaces.OnWulianDeviceStateChangeListener;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.smarthomev5.tools.ActionBarCompat;

public interface WulianDevice
		extends
			IProperties,
			IViewResource,
			Comparable<WulianDevice>,
			Cloneable
{
	public static final String EP_0 = "0";
	public static final String EP_14 = "14";
	public static final String EP_15 = "15";
	public static final String EP_16 = "16";
	public static final String EP_17 = "17";
	public static final String EP_18 = "18";
	public static final String EP_19 = "19";
	/**
	 * device receive from gateway or local
	 */
	public void onDeviceUp( DeviceInfo devInfo );

	/**
	 * device has in cache, this will update it's data
	 */
	public void onDeviceData( String gwID, String devID, DeviceEPInfo devEPInfo,String cmd,String mode );

	/**
	 * change device's setting
	 */
	public void onDeviceSet(String mode, DeviceInfo devInfo, DeviceEPInfo devEPInfo );

	/**
	 * change device's setting
	 */
	public void onDeviceSet(String mode, DeviceInfo devInfo, DeviceEPInfo devEPInfo,String cmd);

	/**
	 * device break down or offline
	 */
	public void onDeviceDestory( String gwID, String devID );

	/**
	 * refresh device data or state
	 */
	public void refreshDevice();
	
	/**
	 * return the device's context
	 */
	public Context getContext();

	/**
	 * get device data, may be contains epinfo
	 */
	public DeviceInfo getDeviceInfo();

	/**
	 * some device default ep is not 14, so it can set this
	 */
	public String getDefaultEndPoint();

	/**
	 * get device's gwID
	 */
	public String getDeviceGwID();

	/**
	 * get device's devID
	 */
	public String getDeviceID();

	/**
	 * get device's type
	 */
	public String getDeviceType();

	/**
	 * get device's name
	 */
	public String getDeviceName();

	/**
	 * get device's room id
	 */
	public String getDeviceRoomID();

	/**
	 * get device is online or not
	 */
	public boolean isDeviceOnLine();

	/**
	 * set device's online state
	 */
	public void setDeviceOnLineState( boolean isOnLine );

	/**
	 * device is create, like device up or temp device
	 */
	public boolean isDeviceUseable();

	/**
	 * if the original device has parent, set it
	 */
	public void setDeviceParent( WulianDevice parent );

	/**
	 * get device's parent or null
	 */
	public WulianDevice getDeviceParent();

	/**
	 * registerStateChangeListener {@link OnWulianDeviceStateChangeListener}
	 */
	public void registerStateChangeListener( OnWulianDeviceStateChangeListener listener );

	/**
	 * unregisterStateChangeListener {@link OnWulianDeviceStateChangeListener}
	 */
	public void unregisterStateChangeListener( OnWulianDeviceStateChangeListener listener );

	/**
	 * registerControlRequestListener {@link OnWulianDeviceRequestListener}
	 */
	public void registerControlRequestListener( OnWulianDeviceRequestListener listener );

	/**
	 * unregisterControlRequestListener {@link OnWulianDeviceRequestListener}
	 */
	public void unregisterControlRequestListener( OnWulianDeviceRequestListener listener );

	/**
	 * return all child device which attached on this device if had or <code>null</code>;
	 */
	public Map<String,WulianDevice> getChildDevices();
	
	/**
	 * get the child device of ep
	 * @param ep
	 * @return
	 */
	public WulianDevice getChildDevice(String ep);
	
	/**
	 * By category, some devices have different ways of showing
	 */
	public String getDeviceCategory();
	
	/**
	 * Modify device's default drawable resource according to category
	 */
	public void controlDevice(String ep,String epType,String epData);
	public boolean isLinkControl();
	public boolean isLinkControlCondition();
	/**
	 * 将解析破坏报警协议
	 * @param epData
	 * @return
	 */
	public CharSequence parseDestoryProtocol(String epData);
	
	/**
	 * can do something when the DeviceDetailsFragmeng destroyed
	 */
	public void onDeviceDetailsFragmentDestroy();
	
	/**
	 * get the current fragment
	 */
	public Fragment getCurrentFragment();
	
	/**
	 * set the current fragment
	 */
	public void setCurrentFragment(Fragment fragment);
	
	/**
	 * get the current ActionBar
	 */
	public ActionBarCompat getCurrentActionBar();
	
	/**
	 * set the current ActionBar
	 */
	public void setCurrentActionBar(ActionBarCompat actionBarCompat);
	
	public void registerEPDataToHTML(H5PlusWebView pWebview, String callBackId);

	public void registerEPDataToHTML(H5PlusWebView pWebview, String callBackId,String cmd);

	/**
	 * set HouseKeeperSelectControlDeviceDataFragment actionBarClickRightListener is valid
	 * @return
     */
	public boolean isHouseKeeperSelectControlDeviceActionBarUseable();

}
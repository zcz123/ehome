package cc.wulian.app.model.device.interfaces;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;

/**
 * for parent device create it's child device if had
 */
/**
 * @author WIN7
 *
 */
public interface CreateDeviceInterface
{
	/**
	 * getDeviceMap
	 */
	public Map<String, WulianDevice> getDeviceMap();

  /**
   * Returns an instance of {@link Iterator} that may be used to access the
   * objects contained by this {@code Collection}. 
   */
	public Iterator<WulianDevice> iterator();

	/**
	 * device first create
	 */
	public void onDeviceUp( DeviceInfo devInfo, boolean avoidSameType );
	
	/**
	 * create each ep device
	 */
	public WulianDevice createDeviceByEp( Context context, DeviceEPInfo deviceEPInfo, Map<String, WulianDevice> dualEpMap );

	/**
	 * get the child device by ep
	 */
	public WulianDevice getChildDeviceByEp( CharSequence ep );

	/**
	 * change ep device's setting
	 */
	public void onDeviceData( String gwID, String devID, DeviceEPInfo devEPInfo );

	/**
	 * change ep device's setting
	 */
	public void onDeviceSet( DeviceInfo devInfo, DeviceEPInfo devEPInfo );

	/**
	 * ep device break down or offline
	 */
	public void onDeviceDestory( String gwID, String devID );

	/**
	 * refresh device data or state
	 */
	public void refreshDevice();

	/**
	 * -------------     -------------				-------------------------
	 * | 				   |     | 				   |				| 					| 					|
	 * | Drawable0 |  +  | Drawable1 |  --->  | Drawable0 | Drawable1 |
	 * | 					 |     | 					 |				| 					| 					|
	 * -------------		 -------------				-------------------------
	 * @param mergeBackground
	 * 	if background not null, it will be add in the bottom layer in the returned drawable
	 */
	public Drawable getMergeWrappedStateSmallIcon( Drawable[] drawables, Drawable mergeBackground );

	/**
	 * return merged wrapped parsed data protocol
	 * @param isSimpleShow TODO
	 */
	public CharSequence getMergeWrappedParseDataWithProtocol(boolean isSimpleShow);
	
	/**
	 * return merged wrapped view list, contains all ep device's details view
	 */
	public List<View> onCreateMergeWrappedView( LayoutInflater inflater, ViewGroup mergeGroup );

	/**
	 * when device big created after {@link #onCreateMergeWrappedView(LayoutInflater, ViewGroup)}
	 */
	public void onMergeWrappedViewCreated( ViewGroup mergeGroup, Bundle saveState );

	/**
	 * init device big view state
	 */
	public void initMergeWrappedView();

	/**
	 * registerStateChangeListener
	 */
	public void registerStateChangeListener( OnWulianDeviceStateChangeListener listener );

	/**
	 * unregisterStateChangeListener
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
	 * return all child device which attached on this device;
	 */
	public  Map<String,WulianDevice> getChildDevices();

	/**
	 * set device's online state
	 */
	public void setDeviceOnLineState( boolean isOnLine );
}

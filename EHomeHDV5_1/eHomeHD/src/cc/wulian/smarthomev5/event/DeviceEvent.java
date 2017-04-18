package cc.wulian.smarthomev5.event;

import cc.wulian.ihome.wan.entity.DeviceInfo;

/**
 * like add, update, delete device info may trigger this event,
 * device up or down do <b>not</b> call this
 */
public class DeviceEvent
{
	public static final String REFRESH = "refresh";
	public static final String DONOTHING = "donothing";
	public static final String REMOVE = "remove";
	public static final String QUICK_EDIT = "quick_edit";
	public String action;
	public DeviceInfo deviceInfo;
	public boolean isFromMe;

	public DeviceEvent(String action){
		this.action = action;
	}
	public DeviceEvent( String action, DeviceInfo deviceInfo, boolean isFromMe )
	{
		this.action = action;
		this.deviceInfo = deviceInfo;
		this.isFromMe = isFromMe;
	}
	

}

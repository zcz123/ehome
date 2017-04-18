package cc.wulian.smarthomev5.databases.entitys;

public class Wifi {
	// Shake
	public static final String TABLE_WIFI = "T_WIFI";
	// Column
	public static final String GW_ID = "T_WIFI_GW_ID";

	/**
	 * maybe device id or scene id, by TYPE
	 */
	public static final String OPERATION_ID = "T_WIFI_OPER_ID";
	public static final String OPERATION_TYPE = "T_WIFI_TYPE";

	public static final String DEVICE_EP = "T_WIFI_DEV_EP";
	public static final String DEVICE_EP_TYPE = "T_WIFI_DEV_EP_TYPE";
	public static final String DEVICE_EP_DATA = "T_WIFI_DEV_EP_DATA";

	public static final String LAST_TIME = "T_WIFI_LAST_TIME";
	public static final String WIFI_SSID = "T_WIFI_SSID";
	public static final String CONDITION_CONTENT = "T_WIFI_CONDITION_CONTENT";

	public static final int POS_GW_ID = 0;
	public static final int POS_OPERATION_ID = 1;
	public static final int POS_OPERATION_TYPE = 2;
	public static final int POS_DEVICE_EP = 3;
	public static final int POS_DEVICE_EP_TYPE = 4;
	public static final int POS_DEVICE_EP_DATA = 5;
	public static final int POS_LAST_TIME = 6;
	public static final int POS_WIFI_SSID = 7;
	public static final int POS_CONDITION_CONTENT = 8;
}

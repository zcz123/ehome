package cc.wulian.smarthomev5.databases.entitys;

/**
 * Change Log:<br/>
 * 1.add column DEVICE_EP_NAME for issue #224 (2013-12-03)
 */
public final class Messages
{
	public static final String PRIORITY_DEFAULT = "1";

	public static final String TYPE_DEV_ALARM = "0";
	public static final String TYPE_DEV_SENSOR_DATA = "1";
	public static final String TYPE_DEV_ONLINE = "2";
	public static final String TYPE_SCENE_OPERATION = "3";
	public static final String TYPE_DEV_OFFLINE = "4";
	public static final String TYPE_DEV_LOW_POWER = "5";
	public static final String TYPE_DEV_DESTORY = "6";
	
	public static final String SMILE_DEFAULT = "-1";
	public static final String SMILE_A = "1";
	public static final String SMILE_B = "2";
	public static final String SMILE_C = "3";
	public static final String SMILE_D = "4";
	
	
	// db info
	public static final String TABLE_MSG_DEMO = "T_MSG_DEMO";
	// MSG
	public static final String TABLE_MSG = "T_MSG";

	// Column
	public static final String ID = "T_MSG_ID";
	public static final String GW_ID = "T_MSG_DEV_GW_ID";
	public static final String USER_ID = "T_MSG_USER_ID";
	public static final String DEVICE_ID = "T_MSG_DEV_ID";
	public static final String DEVICE_EP = "T_MSG_DEV_EP";
	public static final String DEVICE_EP_NAME = "T_MSG_DEV_EP_NAME";
	public static final String DEVICE_EP_TYPE = "T_MSG_DEV_EP_TYPE";
	public static final String DEVICE_EP_DATA = "T_MSG_DEV_EP_DATA";
	public static final String TIME = "T_MSG_TIME";
	public static final String PRIORITY = "T_MSG_PRIORITY";
	public static final String TYPE = "T_MSG_TYPE";
	public static final String SMILE = "T_MSG_SMILE";


	public static final int POS_ID = 0;
	public static final int POS_GW_ID = 1;
	public static final int POS_USER_ID = 2;
	public static final int POS_DEVICE_ID = 3;
	public static final int POS_DEVICE_EP = 4;
	public static final int POS_DEVICE_EP_NAME = 5;
	public static final int POS_DEVICE_EP_TYPE = 6;
	public static final int POS_DEVICE_EP_DATA = 7;
	public static final int POS_TIME = 8;
	public static final int POS_PRIORITY = 9;
	public static final int POS_TYPE = 10;
	public static final int POS_SMILE = 11;

}
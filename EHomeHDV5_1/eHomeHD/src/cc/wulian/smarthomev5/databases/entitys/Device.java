package cc.wulian.smarthomev5.databases.entitys;


public final class Device
{
	// Demo
	public static final String TABLE_DEVICE_DEMO = "T_DEVICE_DEMO";
	// DEVICE
	public static final String TABLE_DEVICE = "T_DEVICE";

	// Column
	public static final String GW_ID = "T_DEVICE_GW_ID";
	public static final String ID = "T_DEVICE_ID";
	public static final String EP = "T_DEVICE_EP";
	public static final String EP_NAME = "T_DEVICE_EP_NAME";
	public static final String EP_TYPE = "T_DEVICE_EP_TYPE";
	public static final String EP_DATA = "T_DEVICE_EP_DATA";
	public static final String EP_STATUS = "T_DEVICE_EP_STATUS";
	public static final String NAME = "T_DEVICE_NAME";
	public static final String AREA_ID = "T_DEVICE_AREA_ID";
	public static final String TYPE = "T_DEVICE_TYPE";
	public static final String CATEGORY = "T_DEVICE_CATEGORY";
	public static final String DATA = "T_DEVICE_DATA";
	public static final String ONLINE = "T_DEVICE_ONLINE";

	public static final String[] PROJECTION = new String[]{
		ID,
		GW_ID,
		TYPE,
		NAME,
		AREA_ID,
		CATEGORY
	};
	
	public static final String[] PROJECTION_EP = new String[]{
		EP,
		EP_TYPE,
		EP_NAME,
		EP_DATA,
		EP_STATUS
	};
	
	public static final String[] PROJECTION_SINGLE = new String[]{
		ID,
		GW_ID,
		TYPE,
		NAME,
		AREA_ID,
		CATEGORY,
		EP,
		EP_TYPE,
		EP_NAME,
		EP_DATA,
		EP_STATUS
	};

	public static final int POS_ID = 0;
	public static final int POS_EP = 1;
	public static final int POS_EP_TYPE = 2;
	public static final int POS_EP_NAME = 3;
	public static final int POS_EP_STATUS = 4;
	public static final int POS_EP_DATA = 5;
	public static final int POS_NAME = 6;
	public static final int POS_GW_ID = 7;
	public static final int POS_AREA_ID = 8;
	public static final int POS_TYPE = 9;
	public static final int POS_CATEGORY = 10;
	public static final int POS_DEVICE_DATA = 11;
	public static final int POS_STATE = 12;
	

	
}
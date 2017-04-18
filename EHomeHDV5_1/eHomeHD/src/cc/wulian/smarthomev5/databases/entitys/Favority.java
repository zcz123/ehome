package cc.wulian.smarthomev5.databases.entitys;

public final class Favority {
	// FAVORITY
	public static final String TABLE_FAVORITY = "T_FAVORITY";

	public static final String TYPE_DEVICE = "0";
	public static final String TYPE_SCENE = "1";
	// operate
	public static final String OPERATION_AUTO = "1";
	public static final String OPERATION_USER = "2";

	// Column
	public static final String GW_ID = "T_FAV_DEV_GW_ID";
	public static final String OPERATION_ID = "T_FAV_OPER_ID";
	public static final String OPERATION_DATA = "T_FAV_OPER_DATA";
	public static final String DEVICE_EP = "T_FAV_DEV_EP";
	public static final String DEVICE_EP_TYPE = "T_FAV_DEV_EP_TYPE";
	public static final String OPERATION_TYPE = "T_FAV_TYPE";
	public static final String COUNT = "T_FAV_COUNT";
	public static final String LAST_TIME = "T_FAV_LAST_TIME";

	public static final int POS_GW_ID = 0;
	public static final int POS_OPERATION_ID = 1;
	public static final int POS_OPERATION_DATA = 2;
	public static final int POS_DEVICE_EP = 3;
	public static final int POS_DEVICE_EP_TYPE = 4;
	public static final int POS_OPERATION_TYPE = 5;
	public static final int POS_COUNT = 6;
	public static final int POS_LAST_TIME = 7;
}

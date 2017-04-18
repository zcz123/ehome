package cc.wulian.smarthomev5.databases.entitys;

public class DaikinAirConditionRecord {
	public static final String TABLE_DAIKIN_AIR_CONDITION = "T_DAIKIN_AIR_CONDITION";
	public static final String GW_ID = "T_GW_ID";
	public static final String DEV_ID = "T_DEV_ID";
	public static final String KEY_ID = "T_KEY_ID";
	public static final String KEY_NAME = "T_KEY_NAME";

	public static String[] PROJECTION = { GW_ID, DEV_ID, KEY_ID, KEY_NAME };

	public static final int POS_GW_ID = 0;
	public static final int POS_DEV_ID = 1;
	public static final int POS_KEY_ID = 2;
	public static final int POS_KEY_NAME = 3;
}

package cc.wulian.smarthomev5.databases.entitys;

public class GPSLocation
{
	// GPSLocation
	public static final String TABLE_LOCATION = "T_LOCATION";

	// Column
	public static final String GW_ID = "T_LOC_GW_ID";
	public static final String ID = "T_LOC_ID";
	public static final String LATITUDE = "T_LOC_LATITUDE";
	public static final String LONGITUDE = "T_LOC_LONGITUDE";
	public static final String SCENE_ID_ENTER = "T_LOC_BIND_SCENE_ENTER";
	public static final String SCENE_ID_LEAVE = "T_LOC_BIND_SCENE_LEAVE";
	public static final String NAME = "T_LOC_NAME";
	public static final String TIME = "T_LOC_TIME";

	public static final String[] PROJECTION = new String[]{
		GW_ID,
		ID,
		LATITUDE,
		LONGITUDE,
		SCENE_ID_ENTER,
		SCENE_ID_LEAVE,
		NAME,
		TIME
	};
	
	public static final int POS_GW_ID = 0;
	public static final int POS_ID = 1;
	public static final int POS_LATITUDE = 2;
	public static final int POS_LONGITUDE = 3;
	public static final int POS_SCENE_ID_ENTER = 4;
	public static final int POS_SCENE_ID_LEAVE = 5;
	public static final int POS_NAME = 6;
	public static final int POS_TIME = 7;
}

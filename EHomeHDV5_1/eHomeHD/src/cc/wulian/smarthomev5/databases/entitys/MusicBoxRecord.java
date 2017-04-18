package cc.wulian.smarthomev5.databases.entitys;

public class MusicBoxRecord {
	public static final String TABLE_MUSICBOX_RECORDS = "T_MUSICBOX_RECORDS";
	public static final String GW_ID = "T_MB_GW_ID";
	public static final String DEV_ID = "T_MB_DEV_ID";
	public static final String EP = "T_MB_DEV_EP";
	public static final String SONG_ID = "T_MB_SONG_ID";
	public static final String SONG_NAME = "T_MB_SONG_NAME";
	public static String[] PROJECTION = { GW_ID, DEV_ID, EP, SONG_ID, SONG_NAME };
	public static final int POS_GW_ID = 0;
	public static final int POS_DEV_ID = 1;
	public static final int POS_EP = 2;
	public static final int POS_SONG_ID = 3;
	public static final int POS_SONG_NAME = 4;
}

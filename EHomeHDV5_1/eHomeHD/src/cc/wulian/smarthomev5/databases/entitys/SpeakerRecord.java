package cc.wulian.smarthomev5.databases.entitys;

public class SpeakerRecord
{
	public static final String TYPE_AUDIO_MP3 = "0";
	public static final String TYPE_AUDIO_FM = "1";

	public static final String TABLE_SPEAKER_RECORDS = "T_SPEAKER_RECORDS";
	public static final String GW_ID = "T_SR_GW_ID";
	public static final String DEV_ID = "T_SR_DEV_ID";
	public static final String EP = "T_SR_DEV_EP";
	public static final String SONG_ID = "T_SR_SONG_ID";
	public static final String SONG_NAME = "T_SR_SONG_NAME";
	public static final String AUDIO_TYPE = "T_SR_AUDIO_TYPE";

	public static String[] PROJECTION = {
		GW_ID,
		DEV_ID,
		EP,
		SONG_ID,
		SONG_NAME,
		AUDIO_TYPE
	};

	public static final int POS_GW_ID = 0;
	public static final int POS_DEV_ID = 1;
	public static final int POS_EP = 2;
	public static final int POS_SONG_ID = 3;
	public static final int POS_SONG_NAME = 4;
	public static final int POS_AUDIO_TYPE = 5;
}

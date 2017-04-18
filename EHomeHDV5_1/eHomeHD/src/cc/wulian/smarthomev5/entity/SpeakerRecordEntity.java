package cc.wulian.smarthomev5.entity;

import android.database.Cursor;
import cc.wulian.smarthomev5.databases.entitys.SpeakerRecord;

public class SpeakerRecordEntity
{
	public String gwID;
	public String devID;
	public String ep;
	public String songID;
	public String songName;
	public String audioType;

	public SpeakerRecordEntity()
	{

	}

	public SpeakerRecordEntity( Cursor cursor )
	{
		gwID = cursor.getString(SpeakerRecord.POS_GW_ID);
		devID = cursor.getString(SpeakerRecord.POS_DEV_ID);
		ep = cursor.getString(SpeakerRecord.POS_EP);
		songID = cursor.getString(SpeakerRecord.POS_SONG_ID);
		songName = cursor.getString(SpeakerRecord.POS_SONG_NAME);
		audioType = cursor.getString(SpeakerRecord.POS_AUDIO_TYPE);
	}
}

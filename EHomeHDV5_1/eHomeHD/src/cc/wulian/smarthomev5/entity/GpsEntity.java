package cc.wulian.smarthomev5.entity;

import android.database.Cursor;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.databases.entitys.Favority;
import cc.wulian.smarthomev5.databases.entitys.GPSLocation;

public class GpsEntity
{
	public String gwID;
	public int _ID;
	public double latitude;
	public double longitude;
	public String enterSceneID;
	public String leaveSceneID;
	public String name;
	public String time;

	public boolean receivedLocChange;

	private final MainApplication mApplication = MainApplication.getApplication();

	public GpsEntity()
	{
	}

	public GpsEntity( Cursor cursor )
	{
		gwID = cursor.getString(GPSLocation.POS_GW_ID);
		_ID = cursor.getInt(GPSLocation.POS_ID);
		latitude = cursor.getDouble(GPSLocation.POS_LATITUDE);
		longitude = cursor.getDouble(GPSLocation.POS_LONGITUDE);
		enterSceneID = cursor.getString(GPSLocation.POS_SCENE_ID_ENTER);
		leaveSceneID = cursor.getString(GPSLocation.POS_SCENE_ID_LEAVE);
		name = cursor.getString(GPSLocation.POS_NAME);
		time = cursor.getString(Favority.POS_LAST_TIME);

		receivedLocChange = hadSetLocation();
	}

	public boolean hadSetLocation(){
		return latitude != 0.0 && longitude != 0.0;
	}

	public boolean runDelete(){
		boolean success = mApplication.mDataBaseHelper.deleteFromLocation(gwID, String.valueOf(_ID));
		return success;
	}

	public static boolean insertNewEntity( String gwID ){
		MainApplication app = MainApplication.getApplication();
		return app.mDataBaseHelper.insertOrUpdateLocationInfo(gwID, -1, 0, 0, null, null,
				null, true);
	}

	public boolean updateEntity(){
		return mApplication.mDataBaseHelper.insertOrUpdateLocationInfo(gwID, _ID, latitude,
				longitude, enterSceneID, leaveSceneID, name, false);
	}
}

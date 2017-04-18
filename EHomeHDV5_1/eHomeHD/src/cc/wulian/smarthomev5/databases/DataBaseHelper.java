package cc.wulian.smarthomev5.databases;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cc.wulian.smarthomev5.databases.entitys.Area;
import cc.wulian.smarthomev5.databases.entitys.Device;
import cc.wulian.smarthomev5.databases.entitys.DeviceIR;
import cc.wulian.smarthomev5.databases.entitys.Favority;
import cc.wulian.smarthomev5.databases.entitys.GPSLocation;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.databases.entitys.Monitor;
import cc.wulian.smarthomev5.databases.entitys.MusicBoxRecord;
import cc.wulian.smarthomev5.databases.entitys.NFC;
import cc.wulian.smarthomev5.databases.entitys.Scene;
import cc.wulian.smarthomev5.databases.entitys.Shake;
import cc.wulian.smarthomev5.databases.entitys.SigninRecords;
import cc.wulian.smarthomev5.databases.entitys.Social;
import cc.wulian.smarthomev5.databases.entitys.SpeakerRecord;
import cc.wulian.smarthomev5.databases.entitys.Task.Auto;
import cc.wulian.smarthomev5.databases.entitys.Task.Timer;
import cc.wulian.smarthomev5.databases.entitys.TimingScene;
import cc.wulian.smarthomev5.databases.entitys.TwoOutputConverterRecord;
import cc.wulian.smarthomev5.databases.entitys.Version;
import cc.wulian.smarthomev5.databases.entitys.Wifi;
import cc.wulian.smarthomev5.entity.uei.UEI;

public class DataBaseHelper extends SQLiteOpenHelper {
	private static final String TAG = DataBaseHelper.class.getSimpleName();

	public static final String SORT_DESC = " DESC";
	public static final String SORT_ASC = " ASC";

	private static final String DATABASE_NAME = "com.yuantuo.ihome.data";

	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, Version.CURRENT_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		createTableArea(db, false);
		createTableDevice(db, false);
		createTableMsg(db, false);
		createTableMonitor(db, false);
		createTableDeviceIr(db, false);
		createTableLoginHis(db);
		createTableScene(db);
		createTableTaskAuto(db);
		createTableSceneTaskTimer(db);
		createTableTimingScene(db);
		createTableFavority(db);
		createTableLocation(db);
		createTableShake(db);
		createTableNFC(db);
		createTableSocial(db);
		createTableTwoOutputRecords(db);
		createTableMusicBox(db);
		// DEMO
		createTableArea(db, true);
		createTableDevice(db, true);
		createTableMsg(db, true);
		createTableMonitor(db, true);
		createTableWifi(db);
		createTableUEI(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		for (int j = oldVersion + 1; j <= newVersion; j++) {
			switch (j) {
			case Version.VERSION_13:
				version13AlertMessageTable(db);
				break;
			case Version.VERSION_14:
				version14Alert(db);
				break;
			case Version.VERSION_15:
				createTableWifi(db);
				break;
			case Version.VERSION_16:
				createTableNFC(db);
			}
				
		}
	}

	private void version13AlertMessageTable(SQLiteDatabase database) {
		database.execSQL("ALTER TABLE " + Messages.TABLE_MSG + " ADD "
				+ Messages.SMILE + " TEXT  default '" + Messages.SMILE_DEFAULT
				+ "';");
	}

	private void version14Alert(SQLiteDatabase db) {
		createTableTwoOutputRecords(db);
		dropTableSpeakerRecords(db);
		createTableMusicBox(db);
	}

	/**
	 * 创建区域表
	 * 
	 * @param db
	 * @param isDemo
	 */
	private void createTableArea(SQLiteDatabase db, boolean isDemo) {
		String tableName = getTableNameArea(isDemo);
		String sql = "create table if not exists " + tableName + "(" + Area.ID
				+ " integer," + Area.GW_ID + " text," + Area.NAME + " text,"
				+ Area.ICON + " text," + Area.COUNT + " text, primary key("
				+ Area.GW_ID + " , " + Area.ID + "))";
		db.execSQL(sql);
	}

	/**
	 * 删除区域表
	 * 
	 * @param db
	 * @param isDemo
	 */
	private void dropTableArea(SQLiteDatabase db, boolean isDemo) {
		String tableName = getTableNameArea(isDemo);
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameArea(boolean isDemo) {
		String tableName;
		if (isDemo) {
			tableName = Area.TABLE_AREA_DEMO;
		} else {
			tableName = Area.TABLE_AREA;
		}
		return tableName;
	}

	private void createTableDevice(SQLiteDatabase db, boolean isDemo) {
		String tableName = getTableNameDevice(isDemo);
		String sql = "create table if not exists " + tableName + "("
				+ Device.ID + " text not null," + Device.EP + " text,"
				+ Device.EP_TYPE + " text, " + Device.EP_NAME + " text,"
				+ Device.EP_STATUS + " text," + Device.EP_DATA + " text,"
				+ Device.NAME + " text," + Device.GW_ID + " text,"
				+ Device.AREA_ID + " integer," + Device.TYPE + " text,"
				+ Device.CATEGORY + " text," + Device.DATA + " text,"
				+ Device.ONLINE + " text, primary key(" + Device.GW_ID + ","
				+ Device.ID + ", " + Device.EP + "));";
		db.execSQL(sql);
	}

	private void dropTableDevice(SQLiteDatabase db, boolean isDemo) {
		String tableName = getTableNameDevice(isDemo);
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameDevice(boolean isDemo) {
		String tableName;
		if (isDemo) {
			tableName = Device.TABLE_DEVICE_DEMO;
		} else {
			tableName = Device.TABLE_DEVICE;
		}
		return tableName;
	}

	private void createTableMsg(SQLiteDatabase db, boolean isDemo) {
		String tableName = getTableNameMsg(isDemo);
		String sql = "create table if not exists " + tableName + "("
				+ Messages.ID + " integer," + Messages.GW_ID + " text,"
				+ Messages.USER_ID + " text," + Messages.DEVICE_ID + " text,"
				+ Messages.DEVICE_EP + " text," + Messages.DEVICE_EP_NAME
				+ " text," + Messages.DEVICE_EP_TYPE + " text,"
				+ Messages.DEVICE_EP_DATA + " text," + Messages.TIME + " text,"
				+ Messages.PRIORITY + " text, " + Messages.TYPE + " text"
				+ " default '" + Messages.TYPE_DEV_ALARM + "', "
				+ Messages.SMILE + " text default '" + Messages.SMILE_DEFAULT
				+ "'," + " primary key(" + Messages.GW_ID + ", " + Messages.ID
				+ "));";
		db.execSQL(sql);
	}

	protected String getTableNameMsg(boolean isDemo) {
		String tableName;
		if (isDemo) {
			tableName = Messages.TABLE_MSG_DEMO;
		} else {
			tableName = Messages.TABLE_MSG;
		}
		return tableName;
	}

	private void createTableMonitor(SQLiteDatabase db, boolean isDemo) {
		String tableName = getTableNameMonitor(isDemo);
		String sql = "create table if not exists " + tableName + "("
				+ Monitor.ID + " integer," + Monitor.GW_ID + " text,"
				+ Monitor.NAME + " text," + Monitor.ICON + " text,"
				+ Monitor.TYPE + " text," + Monitor.UID + " text,"
				+ Monitor.HOST + " text," + Monitor.PORT + " text,"
				+ Monitor.USER + " text," + Monitor.PWD + " text,"
				+ Monitor.BIND_DEV_ID + " text," + Monitor.AREAID + " text, "
				+ " primary key (" + Monitor.ID + "));";
		db.execSQL(sql);
	}

	private void dropTableMonitor(SQLiteDatabase db, boolean isDemo) {
		String tableName = getTableNameMonitor(isDemo);
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameMonitor(boolean isDemo) {
		String tableName;
		if (isDemo) {
			tableName = Monitor.TABLE_MONITOR_DEMO;
		} else {
			tableName = Monitor.TABLE_MONITOR;
		}
		return tableName;
	}

	private void createTableDeviceIr(SQLiteDatabase db, boolean isDemo) {
		String tableName = getTableNameDeviceIr(isDemo);
		String sql = "create table if not exists " + tableName + "("
				+ DeviceIR.ID + " text," + DeviceIR.EP + " text,"
				+ DeviceIR.GW_ID + " text," + DeviceIR.KEYSET + " text,"
				+ DeviceIR.TYPE + " text," + DeviceIR.CODE + " text, "
				+ DeviceIR.NAME + " text, " + DeviceIR.STATUS + " text, "
				+ "primary key (" + DeviceIR.GW_ID + " ," + DeviceIR.ID + " ,"
				+ DeviceIR.EP + " ," + DeviceIR.KEYSET + "));";

		db.execSQL(sql);
	}

	private void dropTableDeviceIr(SQLiteDatabase db, boolean isDemo) {
		String tableName = getTableNameDeviceIr(isDemo);
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameDeviceIr(boolean isDemo) {
		String tableName;
		if (isDemo) {
			tableName = DeviceIR.TABLE_DEVICE_IR;
		} else {
			tableName = DeviceIR.TABLE_DEVICE_IR;
		}
		return tableName;
	}

	private void createTableLoginHis(SQLiteDatabase db) {
		String tableName = getTableNameLoginHis();
		String sql = "create table if not exists " + tableName + "("
				+ SigninRecords.GW_ID + " TEXT not null primary key,"
				+ SigninRecords.GW_PWD + " TEXT," + SigninRecords.GW_IP
				+ " TEXT," + SigninRecords.GW_TIME + " TEXT);";
		db.execSQL(sql);
	}

	private void dropTableLoginHis(SQLiteDatabase db) {
		String tableName = getTableNameLoginHis();
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameLoginHis() {
		String tableName = SigninRecords.TABLE_SIGNIN;
		return tableName;
	}

	private void createTableScene(SQLiteDatabase db) {
		String tableName = getTableNameScene();
		String sql = "create table if not exists " + tableName + "("
				+ Scene.GW_ID + " varchar(12)," + Scene.ID
				+ " integer not null," + Scene.NAME + " text," + Scene.ICON
				+ " text," + Scene.STATUS + " text," + "primary key ("
				+ Scene.GW_ID + ", " + Scene.ID + "));";
		db.execSQL(sql);
	}

	private void dropTableScene(SQLiteDatabase db) {
		String tableName = getTableNameScene();
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameScene() {
		String tableName = Scene.TABLE_SCENE;
		return tableName;
	}

	private void createTableTaskAuto(SQLiteDatabase db) {
		String tableName = getTableNameTaskAuto();
		String sql = "create table if not exists " + tableName + "("
				+ Auto.GW_ID + " varchar(32) , " + Auto.SCENE_ID
				+ " integer , " + Auto.DEV_ID + " varchar(16), "
				+ Auto.DEV_TYPE + " varchar(4), " + Auto.EP + " varchar(16), "
				+ Auto.EP_TYPE + " varchar(4), " + Auto.EP_DATA
				+ " varchar(32), " + Auto.SENSOR_ID + " varchar(16), "
				+ Auto.SENSOR_EP + " varchar(2), " + Auto.SENSOR_TYPE
				+ " varchar(4), " + Auto.SENSOR_NAME + " varchar(32), "
				+ Auto.SENSOR_COND + " varchar(32), " + Auto.SENSOR_DATA
				+ " varchar(8), " + Auto.CONTENT_ID + " integer, " + Auto.DELAY
				+ " integer, " + Auto.FORWARD + " varchar(2), " + Auto.STATUS
				+ " varchar(2), " + Auto.AVAILABLE + " varchar(2), "
				+ Auto.ADD_TIME + " datetime, " + Auto.UP_TIME + " datetime, "
				+ Auto.DEL_TIME + " datetime, " + "primary key (" + Auto.GW_ID
				+ ", " + Auto.SCENE_ID + ", " + Auto.DEV_ID + ", " + Auto.EP
				+ " ," + Auto.SENSOR_ID + ", "
				// Auto.SENSOR_EP + ", " +
				+ Auto.CONTENT_ID + "));";
		db.execSQL(sql);
	}

	private void dropTableSceneTaskAuto(SQLiteDatabase db) {
		String tableName = getTableNameTaskAuto();
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameTaskAuto() {
		String tableName = Auto.TABLE_AUTO;
		return tableName;
	}

	private void createTableSceneTaskTimer(SQLiteDatabase db) {
		String tableName = getTableNameTaskTimer();
		String sql = "create table if not exists " + tableName + "("
				+ Timer.GW_ID + " varchar(32) , " + Timer.SCENE_ID
				+ " integer , " + Timer.DEV_ID + " varchar(16), "
				+ Timer.DEV_TYPE + " varchar(4), " + Timer.EP
				+ " varchar(16), " + Timer.EP_TYPE + " varchar(4), "
				+ Timer.EP_DATA + " varchar(32), " + Timer.CONTENT_ID
				+ " integer, " + Timer.TIME + " varchar(32), " + Timer.WEEKDAY
				+ " varchar(32), " + Timer.STATUS + " varchar(2), "
				+ Timer.AVAILABLE + " varchar(2), " + Timer.ADD_TIME
				+ " datetime, " + Timer.UP_TIME + " datetime, "
				+ Timer.DEL_TIME + " datetime, primary key (" + Timer.GW_ID
				+ ", " + Timer.SCENE_ID + ", " + Timer.DEV_ID + ", " + Timer.EP
				+ " ," + Timer.CONTENT_ID + "))";
		db.execSQL(sql);
	}

	private void dropTableSceneTaskTimer(SQLiteDatabase db) {
		String tableName = getTableNameTaskTimer();
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameTaskTimer() {
		String tableName = Timer.TABLE_TIMER;
		return tableName;
	}

	private void createTableTimingScene(SQLiteDatabase db) {
		String sql = "create table if not exists " + getTableNameTimingScene()
				+ "(" + android.provider.BaseColumns._ID
				+ " integer primary key autoincrement," + TimingScene.GW_ID
				+ " varchar(32), " + TimingScene.ID + " integer, "
				+ TimingScene.TIME + " varchar(32), " + TimingScene.WEEKDAY
				+ " varchar(32), " + TimingScene.GROUP_ID + " varchar(32), "
				+ TimingScene.GROUP_NAME + " varchar(32), "
				+ TimingScene.GROUP_STATUS + " varchar(2)" + ")";

		db.execSQL(sql);
	}

	private void dropTableTimingScene(SQLiteDatabase db) {
		String tableName = getTableNameTimingScene();
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameTimingScene() {
		return TimingScene.TABLE_TIMING_SCENE;
	}

	private void createTableFavority(SQLiteDatabase db) {
		String sql = "create table if not exists " + getTableNameFavority()
				+ "(" + Favority.GW_ID + " varchar(32),"
				+ Favority.OPERATION_ID + " varchar(16),"
				+ Favority.OPERATION_DATA + " varchar(16),"
				+ Favority.DEVICE_EP + " varchar(16),"
				+ Favority.DEVICE_EP_TYPE + " varchar(4), "
				+ Favority.OPERATION_TYPE + " varchar(4) default "
				+ Favority.TYPE_DEVICE + "," + Favority.COUNT + " integer,"
				+ Favority.LAST_TIME + " datetime," + "primary key ("
				+ Favority.GW_ID + "," + Favority.OPERATION_ID + ")" + ")";

		db.execSQL(sql);
	}

	private void dropTableFavority(SQLiteDatabase db) {
		String tableName = getTableNameFavority();
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameFavority() {
		return Favority.TABLE_FAVORITY;
	}

	private void createTableShake(SQLiteDatabase db) {
		String sql = "create table if not exists " + getTableNameShake() + "("
				+ Shake.GW_ID + " varchar(32)," + Shake.OPERATION_ID
				+ " varchar(16)," + Shake.OPERATION_TYPE + " varchar(1),"
				+ Shake.DEVICE_EP + " varchar(16)," + Shake.DEVICE_EP_TYPE
				+ " varchar(4)," + Shake.DEVICE_EP_DATA + " varchar(16),"
				+ Shake.LAST_TIME + " datetime," + "primary key ("
				+ Shake.GW_ID + "," + Shake.OPERATION_ID + ","
				+ Shake.DEVICE_EP + ")" + ")";

		db.execSQL(sql);
	}
	
	private void dropTableShake(SQLiteDatabase db) {
		String tableName = getTableNameShake();
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameShake() {
		return Shake.TABLE_SHAKE;
	}
	private void  createTableNFC(SQLiteDatabase db){
		String sql = "create table if not exists " + getTableNameNFC() + "("
				+ NFC.GW_ID + " varchar(32)," + NFC.NFC_UID +" varchar(32),"+NFC.OPERATION_ID
				+ " varchar(16)," + NFC.OPERATION_TYPE + " varchar(2),"
				+ NFC.DEVICE_EP + " varchar(8)," + NFC.DEVICE_EP_TYPE
				+ " varchar(8)," + NFC.DEVICE_EP_DATA + " varchar(20),"
			    + "primary key ("
				+ NFC.GW_ID + "," +NFC.NFC_UID+","+ NFC.OPERATION_ID + ","+NFC.OPERATION_TYPE+","
				+ NFC.DEVICE_EP + ")" + ")";

		db.execSQL(sql);
	}
	protected String getTableNameNFC() {
		return NFC.TABLE_NFC;
	}
	
	private void createTableSocial(SQLiteDatabase db) {
		String sql = "create table if not exists " + getTableNameSocial() + "("
				+ Social.GW_ID + " varchar(32)," + Social.SOCIAL_ID
				+ " integer," + Social.USER_TYPE + " varchar(2),"
				+ Social.USER_ID + " text," + Social.APP_ID + " text,"
				+ Social.USER_NAME + " text," + Social.DATA + " text,"
				+ Social.TIME + " datetime," + "primary key ("
				+ Social.SOCIAL_ID + ")" + ")";

		db.execSQL(sql);
	}

	private void dropTableSocial(SQLiteDatabase db) {
		String tableName = getTableNameSocial();
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameSocial() {
		return Social.TABLE_SOCIAL;
	}

	private void createTableLocation(SQLiteDatabase db) {
		String sql = "create table if not exists " + getTableNameLocation()
				+ "(" + GPSLocation.GW_ID + " varchar(32)," + GPSLocation.ID
				+ " integer," + GPSLocation.LATITUDE + " double,"
				+ GPSLocation.LONGITUDE + " double,"
				+ GPSLocation.SCENE_ID_ENTER + " varchar(32),"
				+ GPSLocation.SCENE_ID_LEAVE + " varchar(32),"
				+ GPSLocation.NAME + " varchar(32), " + GPSLocation.TIME
				+ " varchar(32)," + "primary key (" + GPSLocation.GW_ID + ","
				+ GPSLocation.ID + ")" + ")";

		db.execSQL(sql);
	}

	private void dropTableLocation(SQLiteDatabase db) {
		String tableName = getTableNameLocation();
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameLocation() {
		return GPSLocation.TABLE_LOCATION;
	}

	private void createTableSpeakerRecords(SQLiteDatabase db) {
		String sql = "create table if not exists "
				+ getTableNameSpeakerRecords() + "(" + SpeakerRecord.GW_ID
				+ " varchar(32), " + SpeakerRecord.DEV_ID + " varchar(32), "
				+ SpeakerRecord.EP + " varchar(32), " + SpeakerRecord.SONG_ID
				+ " varchar(32), " + SpeakerRecord.SONG_NAME + " varchar(32), "
				+ SpeakerRecord.AUDIO_TYPE + " varchar(4), primary key ("
				+ SpeakerRecord.GW_ID + ", " + SpeakerRecord.DEV_ID + ", "
				+ SpeakerRecord.EP + ", " + SpeakerRecord.SONG_ID + "))";
		db.execSQL(sql);
	}

	private void createTableTwoOutputRecords(SQLiteDatabase db) {
		String sql = "create table if not exists "
				+ getTableNameTwoOutputRecords() + "("
				+ TwoOutputConverterRecord.GW_ID + " varchar(32), "
				+ TwoOutputConverterRecord.DEV_ID + " varchar(32), "
				+ TwoOutputConverterRecord.KEY_ID + " varchar(64), "
				+ TwoOutputConverterRecord.KEY_NAME + " text, "
				+ TwoOutputConverterRecord.ONE_TYPE + " text, "
				+ TwoOutputConverterRecord.ONE_VALUE + " varchar(2), "
				+ TwoOutputConverterRecord.TWO_TYPE + " text, "
				+ TwoOutputConverterRecord.TWO_VALUE + " varchar(2), "
				+ " primary key ( " + TwoOutputConverterRecord.GW_ID + " , "
				+ TwoOutputConverterRecord.DEV_ID + ","
				+ TwoOutputConverterRecord.KEY_ID + " )) ";
		db.execSQL(sql);

	}

	private void createTableMusicBox(SQLiteDatabase db) {
		String sql = "create table if not exists "
				+ MusicBoxRecord.TABLE_MUSICBOX_RECORDS + "("
				+ MusicBoxRecord.GW_ID + " varchar(32), "
				+ MusicBoxRecord.DEV_ID + " varchar(32), " + MusicBoxRecord.EP
				+ " varchar(32), " + MusicBoxRecord.SONG_ID + " varchar(32), "
				+ MusicBoxRecord.SONG_NAME + " varchar(32), primary key ("
				+ MusicBoxRecord.GW_ID + ", " + MusicBoxRecord.DEV_ID + ", "
				+ MusicBoxRecord.EP + ", " + MusicBoxRecord.SONG_ID + "))";
		db.execSQL(sql);
	}

	private void createTableWifi(SQLiteDatabase db) {
		String sql = "create table if not exists " + Wifi.TABLE_WIFI + "("
				+ Wifi.GW_ID + " varchar(32)," + Wifi.OPERATION_ID
				+ " varchar(16)," + Wifi.OPERATION_TYPE + " varchar(1),"
				+ Wifi.DEVICE_EP + " varchar(16)," + Wifi.DEVICE_EP_TYPE
				+ " varchar(4)," + Wifi.DEVICE_EP_DATA + " varchar(16),"
				+ Wifi.LAST_TIME + " datetime," + Wifi.WIFI_SSID
				+ " varchar(32)," + Wifi.CONDITION_CONTENT + " varchar(60),"
				+ " primary key (" + Wifi.GW_ID + "," + Wifi.OPERATION_TYPE
				+ "," + Wifi.WIFI_SSID + ")" + ")";

		db.execSQL(sql);
	}

	protected String getTableNameTwoOutputRecords() {
		return TwoOutputConverterRecord.TABLE_TWO_OUTPUT;
	}

	private void dropTableSpeakerRecords(SQLiteDatabase db) {
		String tableName = getTableNameSpeakerRecords();
		db.execSQL("drop table if exists " + tableName + ";");
	}

	protected String getTableNameSpeakerRecords() {
		return SpeakerRecord.TABLE_SPEAKER_RECORDS;
	}

	@Deprecated
	public void insertBatch(SQLiteDatabase db, List<String> sqls) {
		try {
			db.beginTransaction();
			for (String sql : sqls) {
				db.execSQL(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.setTransactionSuccessful();
				db.endTransaction();
			}
		}
	}

	public ArrayList<Long> replaceBatch(String table, List<ContentValues> values) {
		ArrayList<Long> idList = new ArrayList<Long>();
		SQLiteDatabase db = getWritableDatabase();
		db.beginTransaction();
		try {
			for (ContentValues value : values) {
				idList.add(db.replace(table, null, value));
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		}
		return idList;
	}
	// 创建UEI设备列表数据库 addby likai
	private void createTableUEI(SQLiteDatabase db){
		String sql = "create table if not exists " + UEI.TABLE_UEI + "("
				+ UEI.GW_ID + " varchar(20)," 
				+ UEI.DEV_ID+ " varchar(20)," 
				+ UEI.APP_ID + " varchar(20),"
				+ UEI.KEY + " varchar(10)," 
				+ UEI.TIME + " varchar(30)," 
				+ UEI.VALUE + " varchar(1000),"
				+ " primary key (" 
				+ UEI.GW_ID + "," 
				+ UEI.DEV_ID+ "," 
				+ UEI.KEY +  ")" + ")";
		db.execSQL(sql);
	}
}
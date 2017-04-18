package cc.wulian.smarthomev5.databases;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.database.DatabaseUtilsCompat;
import android.text.TextUtils;
import cc.wulian.app.model.device.impls.controlable.aircondtion.AirCondition;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.collect.Lists;
import cc.wulian.smarthomev5.databases.entitys.Area;
import cc.wulian.smarthomev5.databases.entitys.DaikinAirConditionRecord;
import cc.wulian.smarthomev5.databases.entitys.Device;
import cc.wulian.smarthomev5.databases.entitys.DeviceIR;
import cc.wulian.smarthomev5.databases.entitys.Favority;
import cc.wulian.smarthomev5.databases.entitys.GPSLocation;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.databases.entitys.Monitor;
import cc.wulian.smarthomev5.databases.entitys.Scene;
import cc.wulian.smarthomev5.databases.entitys.Shake;
import cc.wulian.smarthomev5.databases.entitys.SigninRecords;
import cc.wulian.smarthomev5.databases.entitys.Social;
import cc.wulian.smarthomev5.databases.entitys.SpeakerRecord;
import cc.wulian.smarthomev5.databases.entitys.Task;
import cc.wulian.smarthomev5.databases.entitys.Task.Auto;
import cc.wulian.smarthomev5.databases.entitys.Task.Timer;
import cc.wulian.smarthomev5.databases.entitys.TimingScene;
import cc.wulian.smarthomev5.entity.ShakeEntity;
import cc.wulian.smarthomev5.entity.SocialEntity;
import cc.wulian.smarthomev5.entity.SpeakerRecordEntity;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class CustomDataBaseHelper extends DataBaseHelper {
	public static CustomDataBaseHelper getInstance(Context context) {
		if (instance == null)
			instance = new CustomDataBaseHelper(context);
		return instance;
	}

	private static CustomDataBaseHelper instance;
	private final MainApplication mApp;

	private CustomDataBaseHelper(Context context) {
		super(context);
		mApp = (MainApplication) context.getApplicationContext();
	}

	public int selectMaxIDFromMonitor() {
		return selectMaxID(getTableNameMonitor(mApp.isDemo), Monitor.ID);
	}

	public int selectMaxIDFromMsg() {
		return selectMaxID(getTableNameMsg(mApp.isDemo), Messages.ID);
	}

	public int selectMaxIDFromLocation() {
		return selectMaxID(getTableNameLocation(), GPSLocation.ID);
	}

	public int selectMaxID(String tableName, String key) {
		String tempString = null;
		String fieldID = " Max(" + key + ") ";
		String sql = " select " + fieldID + " from " + tableName;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);

		if (cursor.moveToNext())
			tempString = cursor.getString(0);
		return StringUtil.toInteger(tempString);
	}

	public Cursor selectRow(String tableName, String columnName,
			String columnValue) {

		if (TextUtils.isEmpty(tableName) || TextUtils.isEmpty(columnName)) {
			return null;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(" select * from ").append(tableName).append(" where ")
				.append(columnName).append(" = ").append(columnValue);

		return getReadableDatabase().rawQuery(sb.toString(), null);
	}

	public Cursor selectDeviceAreaDevices(String gwID, String areaID) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select * from ").append(Device.TABLE_DEVICE)
				.append(" where ").append(Device.AREA_ID).append(" = '")
				.append(areaID).append("' and ").append(Device.GW_ID)
				.append(" = '").append(gwID).append("'");

		return getReadableDatabase().rawQuery(sb.toString(), null);
	}

	public Cursor selectAllDeviceArea(String gwID) {
		StringBuilder sb = new StringBuilder();
		sb.append(" select * from ").append(Area.TABLE_AREA).append(" where ")
				.append(Area.GW_ID).append("= '").append(gwID).append("'");
		return getReadableDatabase().rawQuery(sb.toString(), null);
	}

	public String selectKeyNameByCode(String gwID, String devID, String code) {
		String keyName = null;
		String sql = "select " + DeviceIR.NAME + " from "
				+ DeviceIR.TABLE_DEVICE_IR + " where " + DeviceIR.GW_ID
				+ " = '" + gwID + "' AND " + DeviceIR.ID + " = '" + devID
				+ "' AND " + DeviceIR.CODE + " = '" + code + "'";
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.moveToNext())
			keyName = cursor.getString(0);
		return keyName;
	}


//	public boolean deleteFromDevice(String gwID, String devID) {
//		String tableNameDevice = getTableNameDevice(mApp.isDemo);
//		SQLiteDatabase db = this.getWritableDatabase();
//		int affectedRowNum = db.delete(tableNameDevice, DatabaseUtilsCompat
//				.concatenateWhere(Device.GW_ID + "=?", Device.ID + "=?"),
//				new String[] { gwID, devID });
//		return affectedRowNum != 0;
//	}

	public boolean deleteFromMonitor(String gwID, String camID) {
		String tableNameMonitor = getTableNameMonitor(mApp.isDemo);
		SQLiteDatabase db = this.getWritableDatabase();
		int affectedRowNum = db.delete(tableNameMonitor, DatabaseUtilsCompat
				.concatenateWhere(Monitor.GW_ID + "=?", Monitor.ID + "=?"),
				new String[] { gwID, camID });
		return affectedRowNum != 0;
	}

	public boolean deleteFromGwHistory(String gwID) {
		String tableNameHistory = getTableNameLoginHis();
		SQLiteDatabase db = this.getWritableDatabase();
		int affectedRowNum = db.delete(tableNameHistory, SigninRecords.GW_ID
				+ "=?", new String[] { gwID });
		return affectedRowNum != 0;
	}

	public boolean deleteFromRoomInfo(String gwID, String roomID) {
		String tableName = getTableNameArea(mApp.isDemo);
		SQLiteDatabase db = getWritableDatabase();

		String[] clauseArgs = null;
		List<String> args = Lists.newArrayList();

		args.add(gwID);
		String whereClause = Area.GW_ID + "=?";

		boolean isNullID = StringUtil.isNullOrEmpty(roomID);
		if (!isNullID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Area.ID + "=?");
			args.add(roomID);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteFromSceneInfo(String gwID, String sceneID) {
		String tableName = getTableNameScene();
		SQLiteDatabase db = getWritableDatabase();

		String[] clauseArgs = null;
		List<String> args = Lists.newArrayList();

		args.add(gwID);
		String whereClause = Scene.GW_ID + "=?";

		boolean isNullID = StringUtil.isNullOrEmpty(sceneID);
		if (!isNullID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Scene.ID + "=?");
			args.add(sceneID);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteFromTaskTimerAndAuto(String gwID, String sceneID) {
		boolean deleteTimer = deleteFromTaskTimer(gwID, sceneID, null, null);
		boolean deleteAuto = deleteFromTaskAuto(gwID, sceneID, null, null, null);
		return deleteTimer | deleteAuto;
	}

	public boolean deleteFromTaskTimer(String gwID, String sceneID,
			String devID, String ep) {
		String tableName = getTableNameTaskTimer();
		SQLiteDatabase db = getWritableDatabase();

		boolean nullScene = StringUtil.isNullOrEmpty(sceneID);
		boolean nullDev = StringUtil.isNullOrEmpty(devID);
		boolean nullEp = StringUtil.isNullOrEmpty(ep);

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = Timer.GW_ID + "=?";
		args.add(gwID);

		if (!nullScene) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Timer.SCENE_ID + "=?");
			args.add(sceneID);
		}

		if (!nullDev) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Timer.DEV_ID + "=?");
			args.add(devID);
		}

		if (!nullEp) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Timer.EP + "=?");
			args.add(ep);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteFromTaskAuto(String gwID, String sceneID,
			String devID, String ep, String sensorID) {
		String tableName = getTableNameTaskAuto();
		SQLiteDatabase db = getWritableDatabase();

		boolean nullScene = StringUtil.isNullOrEmpty(sceneID);
		boolean nullDev = StringUtil.isNullOrEmpty(devID);
		boolean nullEp = StringUtil.isNullOrEmpty(ep);
		boolean nullSensor = StringUtil.isNullOrEmpty(sensorID);

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = Timer.GW_ID + "=?";
		args.add(gwID);

		if (!nullScene) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Auto.SCENE_ID + "=?");
			args.add(sceneID);
		}

		if (!nullDev) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Auto.DEV_ID + "=?");
			args.add(devID);
		}

		if (!nullEp) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Auto.EP + "=?");
			args.add(ep);
		}

		if (!nullSensor) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Auto.SENSOR_ID + "=?");
			args.add(sensorID);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteFromFavority(String gwID, String type, String operateID) {
		String tableName = getTableNameFavority();
		SQLiteDatabase db = getWritableDatabase();

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = Favority.GW_ID + "=?";
		args.add(gwID);

		whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
				Favority.OPERATION_TYPE + "=?");
		args.add(type);

		boolean isNullID = StringUtil.isNullOrEmpty(operateID);
		if (!isNullID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Favority.OPERATION_ID + "=?");
			args.add(operateID);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteFromShake(String gwID, String operateID) {
		String tableName = getTableNameShake();
		SQLiteDatabase db = getWritableDatabase();

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = Shake.GW_ID + "=?";
		args.add(gwID);

		boolean isNullID = StringUtil.isNullOrEmpty(operateID);
		if (!isNullID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Shake.OPERATION_ID + "=?");
			args.add(operateID);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteShakeByGwID(String gwID) {
		String tableName = getTableNameShake();
		SQLiteDatabase db = getWritableDatabase();

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = Shake.GW_ID + "=?";
		args.add(gwID);

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteFromMessage(String gwID, String msgID, String msgType) {
		String tableName = getTableNameMsg(mApp.isDemo);
		SQLiteDatabase db = getWritableDatabase();

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = Messages.GW_ID + "=?";
		args.add(gwID);

		boolean isNullID = StringUtil.isNullOrEmpty(msgID);
		if (!isNullID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Messages.ID + "=?");
			args.add(msgID);
		}

		boolean isNullType = StringUtil.isNullOrEmpty(msgType);
		if (!isNullType) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Messages.TYPE + "=?");
			args.add(msgType);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteAlarmMessage(String gwID, String msgID, String devID,
			String msgType) {
		String tableName = getTableNameMsg(mApp.isDemo);
		SQLiteDatabase db = getWritableDatabase();

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = Messages.GW_ID + "=?";
		args.add(gwID);

		boolean isNullMsgID = StringUtil.isNullOrEmpty(msgID);
		if (!isNullMsgID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Messages.ID + "=?");
			args.add(msgID);
		}

		boolean isNllDevID = StringUtil.isNullOrEmpty(devID);
		if (!isNllDevID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Messages.DEVICE_ID + "=?");
			args.add(devID);
		}

		boolean isNullType = StringUtil.isNullOrEmpty(msgType);
		if (!isNullType) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Messages.TYPE + "=?");
			args.add(msgType);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteFromSocial(String gwID, String socialID) {
		String tableName = getTableNameSocial();
		SQLiteDatabase db = getWritableDatabase();

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = Social.GW_ID + "=?";
		args.add(gwID);

		boolean isNullSocialID = StringUtil.isNullOrEmpty(socialID);
		if (!isNullSocialID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Social.SOCIAL_ID + "=?");
			args.add(socialID);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteFromTimingScene(String gwID, String groupID) {
		String tableName = getTableNameTimingScene();
		SQLiteDatabase db = getWritableDatabase();

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = TimingScene.GW_ID + "=?";
		args.add(gwID);

		boolean isNullID = StringUtil.isNullOrEmpty(groupID);
		if (!isNullID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					TimingScene.GROUP_ID + "=?");
			args.add(groupID);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteFromDeviceIR(String gwID, String devID, String irType) {
		String tableName = getTableNameDeviceIr(mApp.isDemo);
		SQLiteDatabase db = getWritableDatabase();

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = DeviceIR.GW_ID + "=?";
		args.add(gwID);

		boolean isNullID = StringUtil.isNullOrEmpty(devID);
		if (!isNullID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					DeviceIR.ID + "=?");
			args.add(devID);
		}
		boolean isNullIrType = StringUtil.isNullOrEmpty(irType);
		if (!isNullIrType) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					DeviceIR.TYPE + "=?");
			args.add(irType);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteFromLocation(String gwID, String _ID) {
		String tableName = getTableNameLocation();
		SQLiteDatabase db = getWritableDatabase();

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = GPSLocation.GW_ID + "=?";
		args.add(gwID);

		boolean isNullID = StringUtil.isNullOrEmpty(_ID);
		if (!isNullID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					GPSLocation.ID + "=?");
			args.add(_ID);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

	public boolean deleteFromSpeakerRecords(String gwID, String devID,
			String ep, String songID) {
		String tableName = getTableNameSpeakerRecords();
		SQLiteDatabase db = getWritableDatabase();

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = SpeakerRecord.GW_ID + "=?";
		args.add(gwID);
		whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
				SpeakerRecord.DEV_ID + "=?");
		args.add(devID);
		whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
				SpeakerRecord.EP + "=?");
		args.add(ep);

		if (!StringUtil.isNullOrEmpty(songID)) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					SpeakerRecord.SONG_ID + "=?");
			args.add(songID);
		}
		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.delete(tableName, whereClause, clauseArgs);
		return affectedRowNum != 0;
	}

//	public boolean updateDevDataByDevId(String newDevData, String devID,
//			String ep, String gwID) {
//		String tableNameDevice = getTableNameDevice(mApp.isDemo);
//		SQLiteDatabase db = this.getWritableDatabase();
//
//		ContentValues cv = new ContentValues();
//		cv.put(Device.DATA, newDevData);
//		cv.put(Device.EP_DATA, newDevData);
//
//		int affectedRowNum = db.update(tableNameDevice, cv, Device.GW_ID + "=?"
//				+ " and " + Device.ID + " =?" + " and " + Device.EP + " =?",
//				new String[] { gwID, devID, ep });
//
//		return affectedRowNum > 0;
//	}

	public boolean updateDeviceInfoByDevId(String gwID, String devID,
			String ep, String epName, String epData, String epStatus,
			String name, String areaId, String data, String category) {
		String tableNameDevice = getTableNameDevice(mApp.isDemo);
		SQLiteDatabase db = this.getWritableDatabase();
		boolean isEmptyEp = TextUtils.isEmpty(ep);
		ContentValues cv = new ContentValues();
		cv.put(Device.GW_ID, gwID);
		cv.put(Device.ID, devID);
		cv.put(Device.EP_NAME, epName);
		if (!TextUtils.isEmpty(epData))
			cv.put(Device.EP_DATA, epData);
		if (!TextUtils.isEmpty(epStatus))
			cv.put(Device.EP_STATUS, epStatus);
		cv.put(Device.NAME, name);
		cv.put(Device.AREA_ID, areaId);
		cv.put(Device.CATEGORY, category);
		if (!TextUtils.isEmpty(data))
			cv.put(Device.DATA, data);

		String[] clauseArgs = null;
		List<String> args = Lists.newArrayList();

		String whereClause = DatabaseUtilsCompat.concatenateWhere(Device.GW_ID
				+ "=?", Device.ID + " =?");
		args.add(gwID);
		args.add(devID);

		if (!isEmptyEp) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Device.EP + " =?");
			args.add(ep);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.update(tableNameDevice, cv, whereClause,
				clauseArgs);
		return affectedRowNum > 0;
	}

	public boolean updateDeviceRoomInfo(String gwID, String newRoomID,
			String whereClauseRoomID) {
		String tableName = getTableNameDevice(mApp.isDemo);
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(Device.AREA_ID, newRoomID);
		int affectedRowNum = db.update(tableName, cv, DatabaseUtilsCompat
				.concatenateWhere(Device.GW_ID + "=?", Device.AREA_ID + "=?"),
				new String[] { gwID, whereClauseRoomID });
		return affectedRowNum >= 0;
	}

	public void updateGwHistoryIPStatus2Default() {
		String tableName = getTableNameLoginHis();
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(SigninRecords.GW_IP, "");
		db.update(tableName, cv, null, null);
	}

	public boolean updateTimingSceneByGroupID(String gwID, String groupID,
			String status) {
		String tableName = getTableNameTimingScene();
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put(TimingScene.GROUP_STATUS, status);

		boolean isNullID = StringUtil.isNullOrEmpty(groupID);

		String[] clauseArgs = null;
		List<String> args = Lists.newArrayList();

		String whereClause = TimingScene.GW_ID + "=?";
		args.add(gwID);

		if (!isNullID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					TimingScene.GROUP_ID + " =?");
			args.add(groupID);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.update(tableName, cv, whereClause, clauseArgs);
		return affectedRowNum > 0;
	}

	// TODO
	public boolean findFavority(String gwID, String id) {
		boolean isAlreadyContainThisFavority = false;
		String tableName = getTableNameFavority();
		SQLiteDatabase db = this.getWritableDatabase();

		String[] projection = { Favority.COUNT };
		String whereClause = Favority.GW_ID + "=?" + " AND "
				+ Favority.OPERATION_ID + "=?";
		String[] clauseArgs;

		List<String> args = Lists.newArrayList();
		args.add(gwID);
		args.add(id);

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		Cursor cursor = db.query(true, tableName, projection, whereClause,
				clauseArgs, null, null, null, null);
		if (cursor.moveToNext()) {
			isAlreadyContainThisFavority = true;
		}
		return isAlreadyContainThisFavority;
	}

	public void insertFavority(String gwID, String id, String ep,
			ContentValues cv) {
		String tableName = getTableNameFavority();
		SQLiteDatabase db = this.getWritableDatabase();

		String[] projection = { Favority.COUNT };
		String whereClause = Favority.GW_ID + "=?" + " AND "
				+ Favority.OPERATION_ID + "=?";
		String[] clauseArgs;

		List<String> args = Lists.newArrayList();
		args.add(gwID);
		args.add(id);

		boolean typeDevice = Favority.TYPE_DEVICE.equals(cv
				.getAsString(Favority.OPERATION_TYPE));
		if (typeDevice) {
			String deviceClause = Favority.DEVICE_EP + "=?";
			args.add(ep);
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					deviceClause);
		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		Cursor cursor = db.query(true, tableName, projection, whereClause,
				clauseArgs, null, null, null, null);
		int count = 0;
		if (cursor.moveToNext()) {
			count = cursor.getInt(0) + 1;
		}
		cv.put(Favority.COUNT, count);

		// exist data, update it
		if (count > 0) {
			db.update(tableName, cv, whereClause, clauseArgs);
		} else {
			db.insert(tableName, null, cv);
		}
	}

//	public void insertOrUpdateDevices(String gwID, String devID, String ep,
//			String epName, String epType, String epData, String epStatus,
//			String name, String areaId, String type, String data,
//			String category, boolean nullInsert) {
//		String tableNameDevices = getTableNameDevice(mApp.isDemo);
//		SQLiteDatabase db = getWritableDatabase();
//
//		ContentValues cv = new ContentValues();
//		cv.put(Device.GW_ID, gwID);
//		cv.put(Device.ID, devID);
//		cv.put(Device.EP, ep);
//		cv.put(Device.EP_NAME, epName);
//		cv.put(Device.EP_TYPE, epType);
//		cv.put(Device.EP_DATA, epData);
//		cv.put(Device.EP_STATUS, epStatus);
//		cv.put(Device.NAME, name);
//		cv.put(Device.AREA_ID, areaId);
//		cv.put(Device.TYPE, type);
//		cv.put(Device.CATEGORY, category);
//		cv.put(Device.DATA, data);
//		cv.put(Device.ONLINE, CmdUtil.DEV_ON_LINE);
//
//		int affectedRowNum = db.update(tableNameDevices, cv, Device.GW_ID
//				+ "=?" + " and " + Device.ID + "=?" + " and " + Device.EP
//				+ "=?", new String[] { gwID, devID, ep });
//		if (nullInsert && affectedRowNum <= 0)
//			db.insert(tableNameDevices, null, cv);
//	}

	public boolean insertOrUpdateMonitor(String ID, String gwID,
			String camName, int camType, String uid, String host, int port,
			String userName, String passWord, String bindDev, boolean nullInsert) {
		String tableNameMonitor = getTableNameMonitor(mApp.isDemo);
		SQLiteDatabase db = getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(Monitor.ID, ID);
		cv.put(Monitor.GW_ID, gwID);
		cv.put(Monitor.NAME, camName);
		cv.put(Monitor.TYPE, camType);
		cv.put(Monitor.UID, uid);
		cv.put(Monitor.HOST, host);
		cv.put(Monitor.PORT, port);
		cv.put(Monitor.USER, userName);
		cv.put(Monitor.PWD, passWord);
		cv.put(Monitor.BIND_DEV_ID, bindDev);

		int affectedRowNum = db
				.update(tableNameMonitor, cv, Monitor.GW_ID + "=?" + " and "
						+ Monitor.ID + "=?", new String[] { gwID, ID });

		long rowID = -1;

		if (nullInsert && affectedRowNum <= 0)
			rowID = db.insert(tableNameMonitor, null, cv);
		return affectedRowNum > 0 || rowID > -1;
	}

	public boolean insertOrUpdateGwHistory(String gwID, String gwPwd,
			String time, String gwIP, boolean nullInsert) {
		String tableName = getTableNameLoginHis();
		SQLiteDatabase db = getWritableDatabase();

		ContentValues cValues = new ContentValues();
		cValues.put(SigninRecords.GW_ID, gwID);
		cValues.put(SigninRecords.GW_TIME, time);

		if (!StringUtil.isNullOrEmpty(gwPwd))
			cValues.put(SigninRecords.GW_PWD, gwPwd);
		if (!StringUtil.isNullOrEmpty(gwIP))
			cValues.put(SigninRecords.GW_IP, gwIP);

		int affectedRowNum = db.update(tableName, cValues, SigninRecords.GW_ID
				+ "=?", new String[] { gwID });

		long rowID = -1;

		if (nullInsert && affectedRowNum <= 0)
			rowID = db.insert(tableName, null, cValues);
		return affectedRowNum > 0 || rowID > -1;
	}

	public boolean insertOrUpdateRoomInfo(RoomInfo info, boolean nullInsert) {
		String tableName = getTableNameArea(mApp.isDemo);
		SQLiteDatabase db = getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(Area.GW_ID, info.getGwID());
		cv.put(Area.ID, info.getRoomID());
		if (!StringUtil.isNullOrEmpty(info.getName()))
			cv.put(Area.NAME, info.getName());
		if (!StringUtil.isNullOrEmpty(info.getIcon()))
			cv.put(Area.ICON, info.getIcon());

		int affectedRowNum = db.update(
				tableName,
				cv,
				DatabaseUtilsCompat.concatenateWhere(Area.GW_ID + "=?", Area.ID
						+ "=?"),
				new String[] { info.getGwID(), info.getRoomID() });

		long rowID = -1;
		if (nullInsert && affectedRowNum <= 0)
			rowID = db.insert(tableName, null, cv);
		return affectedRowNum > 0 || rowID > -1;
	}

	public boolean insertOrUpdateSceneInfo(SceneInfo info, boolean nullInsert) {
		String tableName = getTableNameScene();
		SQLiteDatabase db = getWritableDatabase();

		boolean isNullID = StringUtil.isNullOrEmpty(info.getSceneID());

		List<String> args = Lists.newArrayList();
		String[] clauseArgs = null;

		args.add(info.getGwID());
		String whereClause = Scene.GW_ID + "=?";

		if (!isNullID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					Scene.ID + "=?");
			args.add(info.getSceneID());
		}

		ContentValues cv = new ContentValues();
		cv.put(Scene.GW_ID, info.getGwID());
		if (!isNullID)
			cv.put(Scene.ID, info.getSceneID());
		if (!StringUtil.isNullOrEmpty(info.getName()))
			cv.put(Scene.NAME, info.getName());
		if (!StringUtil.isNullOrEmpty(info.getIcon()))
			cv.put(Scene.ICON, info.getIcon());
		if (!StringUtil.isNullOrEmpty(info.getStatus()))
			cv.put(Scene.STATUS, info.getStatus());

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		int affectedRowNum = db.update(tableName, cv, whereClause, clauseArgs);

		long rowID = -1;
		if (nullInsert && affectedRowNum <= 0)
			rowID = db.insert(tableName, null, cv);
		return affectedRowNum > 0 || rowID > -1;
	}

	public boolean insertOrUpdateTaskTimer(String gwID, String sceneID,
			TaskInfo taskInfo) {
		String devID = taskInfo.getDevID();
		String type = taskInfo.getType();
		type = DeviceTool.createDeviceTypeCompat(type);
		String ep = taskInfo.getEp();
		String epType = taskInfo.getEpType();
		epType = DeviceTool.createDeviceTypeCompat(epType);
		String epData = taskInfo.getEpData();
		String contentID = taskInfo.getContentID();
		String available = taskInfo.getAvailable();
		String addTime = String.valueOf(System.currentTimeMillis());

		String time = taskInfo.getTime();
		String weekday = taskInfo.getWeekday();

		String tableNameTaskTimer = getTableNameTaskTimer();
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cValues = new ContentValues();
		cValues.put(Timer.GW_ID, gwID);
		cValues.put(Timer.SCENE_ID, sceneID);
		cValues.put(Timer.DEV_ID, devID);
		cValues.put(Timer.DEV_TYPE, type);
		cValues.put(Timer.EP, ep);
		cValues.put(Timer.EP_TYPE, epType);
		cValues.put(Timer.EP_DATA, epData);
		cValues.put(Timer.CONTENT_ID, contentID);
		cValues.put(Timer.TIME, time);
		cValues.put(Timer.WEEKDAY, weekday);
		cValues.put(Timer.AVAILABLE, available);
		cValues.put(Timer.ADD_TIME, addTime);

		int affectedRowNum = db.update(tableNameTaskTimer, cValues, Timer.GW_ID
				+ "=?" + " and " + Timer.SCENE_ID + "=?" + " and "
				+ Timer.DEV_ID + "=?" + " and " + Timer.EP + "=?" + " and "
				+ Timer.CONTENT_ID + "=?", new String[] { gwID, sceneID, devID,
				ep, contentID });

		long rowID = -1;
		if (affectedRowNum <= 0)
			rowID = db.insert(tableNameTaskTimer, null, cValues);
		return affectedRowNum > 0 || rowID > -1;
	}

	public boolean insertOrUpdateTaskAuto(String gwID, String sceneID,
			TaskInfo taskInfo) {
		String devID = taskInfo.getDevID();
		String type = taskInfo.getType();
		type = DeviceTool.createDeviceTypeCompat(type);
		String ep = taskInfo.getEp();
		String epType = taskInfo.getEpType();
		epType = DeviceTool.createDeviceTypeCompat(epType);
		String epData = taskInfo.getEpData();
		String contentID = taskInfo.getContentID();
		String available = taskInfo.getAvailable();
		String addTime = String.valueOf(System.currentTimeMillis());

		String sensorID = taskInfo.getSensorID();
		String sensorEp = taskInfo.getSensorEp();
		String sensorType = taskInfo.getSensorType();
		sensorType = DeviceTool.createDeviceTypeCompat(sensorType);
		String sensorName = taskInfo.getSensorName();
		String sensorCond = taskInfo.getSensorCond();
		String sensorData = taskInfo.getSensorData();
		String delay = taskInfo.getDelay();
		String forward = taskInfo.getForward();

		ContentValues cValues = new ContentValues();
		cValues.put(Auto.GW_ID, gwID);
		cValues.put(Auto.SCENE_ID, sceneID);
		cValues.put(Auto.DEV_ID, devID);
		cValues.put(Auto.DEV_TYPE, type);
		cValues.put(Auto.EP, ep);
		cValues.put(Auto.EP_TYPE, epType);
		cValues.put(Auto.EP_DATA, epData);
		cValues.put(Auto.CONTENT_ID, contentID);
		cValues.put(Auto.SENSOR_ID, sensorID);
		cValues.put(Auto.SENSOR_EP, sensorEp);
		cValues.put(Auto.SENSOR_TYPE, sensorType);
		cValues.put(Auto.SENSOR_NAME, sensorName);
		cValues.put(Auto.SENSOR_DATA, sensorData);
		cValues.put(Auto.SENSOR_COND, sensorCond);
		cValues.put(Auto.DELAY, delay);
		cValues.put(Auto.FORWARD, forward);
		cValues.put(Auto.AVAILABLE, available);
		cValues.put(Auto.ADD_TIME, addTime);

		String tableNameTaskAuto = getTableNameTaskAuto();
		SQLiteDatabase db = this.getWritableDatabase();

		int affectedRowNum = 0;
		long rowID = -1;
		if (StringUtil.isNullOrEmpty(sensorID)
				|| CmdUtil.SENSOR_DEFAULT.equals(sensorID)) {
			affectedRowNum = db.delete(tableNameTaskAuto, Auto.GW_ID + "=?"
					+ " and " + Auto.SCENE_ID + "=?" + " and " + Auto.DEV_ID
					+ "=?" + " and " + Auto.EP + "=?" + " and "
					+ Auto.CONTENT_ID + "=?", new String[] { gwID, sceneID,
					devID, ep, contentID });

			rowID = db.insert(tableNameTaskAuto, null, cValues);
		} else {
			affectedRowNum = db.update(tableNameTaskAuto, cValues, Auto.GW_ID
					+ "=?" + " and " + Auto.SCENE_ID + "=?" + " and "
					+ Auto.DEV_ID + "=?" + " and " + Auto.EP + "=?" +
					// Mark: multi sensor
					// " and " + Auto.SENSOR_ID + "=?" +
					" and " + Auto.CONTENT_ID + "=?",
			// new String[]{gwID, sceneID, devID, ep, sensorID, contentID});
					new String[] { gwID, sceneID, devID, ep, contentID });

			if (affectedRowNum <= 0)
				rowID = db.insert(tableNameTaskAuto, null, cValues);
		}
		return affectedRowNum > 0 || rowID > -1;
	}

	public boolean insertTimingScene(String gwID, String sceneID, String time,
			String weekday, String groupID, String groupName, String groupStatus) {
		String tableName = getTableNameTimingScene();
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cValues = new ContentValues();
		cValues.put(TimingScene.GW_ID, gwID);
		cValues.put(TimingScene.ID, sceneID);
		cValues.put(TimingScene.TIME, time);
		cValues.put(TimingScene.WEEKDAY, weekday);
		cValues.put(TimingScene.GROUP_ID, groupID);
		cValues.put(TimingScene.GROUP_NAME, groupName);
		cValues.put(TimingScene.GROUP_STATUS, groupStatus);

		long rowID = db.insert(tableName, null, cValues);
		return rowID > -1;
	}

	public boolean insertOrUpdateDeviceIRInfo(String gwID, String devID,
			String ep, String keyset, String irType, String code, String name,
			String status, boolean insert) {
		String tableNameDevice = getTableNameDeviceIr(mApp.isDemo);
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		if (insert) {
			cv.put(DeviceIR.GW_ID, gwID);
			cv.put(DeviceIR.ID, devID);
			cv.put(DeviceIR.EP, ep);
			cv.put(DeviceIR.KEYSET, keyset);
		}

		cv.put(DeviceIR.TYPE, irType);
		cv.put(DeviceIR.CODE, code);
		cv.put(DeviceIR.NAME, name);
		cv.put(DeviceIR.STATUS, status);

		int affectedRowNum = 0;
		long rowID = -1;
		// if (insert) {
		// rowID = db.insert(tableNameDevice, null, cv);
		// }
		// else {
		// List<String> args = Lists.newArrayList();
		// String whereClause = DeviceIR.GW_ID + "=?" + " and " + DeviceIR.ID +
		// "=?" + " and " + DeviceIR.EP + "=?" + " and " + DeviceIR.KEYSET +
		// "=?";
		// args.add(gwID);
		// args.add(devID);
		// args.add(ep);
		// args.add(keyset);
		//
		// if (!StringUtil.isNullOrEmpty(irType)) {
		// whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
		// DeviceIR.TYPE + "=?");
		// args.add(irType);
		// }
		//
		// String[] whereClauseArgs = new String[args.size()];
		// args.toArray(whereClauseArgs);
		// affectedRowNum = db.update(tableNameDevice, cv, whereClause,
		// whereClauseArgs);
		// }
		// return affectedRowNum > 0 || rowID > -1;

		List<String> args = Lists.newArrayList();
		String whereClause = DeviceIR.GW_ID + "=?" + " and " + DeviceIR.ID
				+ "=?" + " and " + DeviceIR.EP + "=?" + " and "
				+ DeviceIR.KEYSET + "=?";
		args.add(gwID);
		args.add(devID);
		args.add(ep);
		args.add(keyset);
		// temporary cancellation
		// if (!StringUtil.isNullOrEmpty(irType)) {
		// whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
		// DeviceIR.TYPE + "=?");
		// args.add(irType);
		// }

		String[] whereClauseArgs = new String[args.size()];
		args.toArray(whereClauseArgs);
		affectedRowNum = db.update(tableNameDevice, cv, whereClause,
				whereClauseArgs);

		if (insert && affectedRowNum <= 0)
			rowID = db.insert(tableNameDevice, null, cv);

		return affectedRowNum > 0 || rowID > -1;
	}

	public boolean insertOrUpdateLocationInfo(String gwID, int entityID,
			double latitude, double longitude, String enterSceneID,
			String leaveSceneID, String name, boolean insert) {
		String tableNameLocation = getTableNameLocation();
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		if (insert) {
			int maxID = selectMaxIDFromLocation();
			cv.put(GPSLocation.GW_ID, gwID);
			cv.put(GPSLocation.ID, maxID + 1);
		}

		cv.put(GPSLocation.LATITUDE, latitude);
		cv.put(GPSLocation.LONGITUDE, longitude);
		cv.put(GPSLocation.SCENE_ID_ENTER, enterSceneID);
		cv.put(GPSLocation.SCENE_ID_LEAVE, leaveSceneID);
		cv.put(GPSLocation.NAME, name);
		cv.put(GPSLocation.TIME, System.currentTimeMillis());

		int affectedRowNum = 0;
		long rowID = -1;
		if (insert) {
			rowID = db.insert(tableNameLocation, null, cv);
		} else {
			String wheleClause = GPSLocation.GW_ID + "=?" + " and "
					+ GPSLocation.ID + "=?";
			String[] args = { gwID, String.valueOf(entityID) };
			affectedRowNum = db
					.update(tableNameLocation, cv, wheleClause, args);
		}
		return affectedRowNum > 0 || rowID > -1;
	}

	public boolean insertShakeInfo(ShakeEntity info, boolean nullInsert) {
		String tableName = getTableNameShake();
		SQLiteDatabase db = getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(Shake.GW_ID, info.gwID);
		if (!StringUtil.isNullOrEmpty(info.operateID))
			cv.put(Shake.OPERATION_ID, info.operateID);
		if (!StringUtil.isNullOrEmpty(info.operateType))
			cv.put(Shake.OPERATION_TYPE, info.operateType);
		if (!StringUtil.isNullOrEmpty(info.ep))
			cv.put(Shake.DEVICE_EP, info.ep);
		if (!StringUtil.isNullOrEmpty(info.epType))
			cv.put(Shake.DEVICE_EP_TYPE, info.epType);

		if (!StringUtil.isNullOrEmpty(info.epData))
			cv.put(Shake.DEVICE_EP_DATA, info.epData);
		if (!StringUtil.isNullOrEmpty(info.time))
			cv.put(Shake.LAST_TIME, info.time);

		long rowID = -1;
		rowID = db.insert(tableName, null, cv);
		return rowID > -1;
	}

	public boolean insertChatMsg(SocialEntity info) {
		String tableName = getTableNameSocial();
		SQLiteDatabase db = getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(Social.GW_ID, info.gwID);

		// TODO use (or test) cast xx as int
		String incrementSocialID = "select MAX(" + Social.SOCIAL_ID + ") from "
				+ tableName;
		Cursor cursor = db.rawQuery(incrementSocialID, null);
		if (cursor.moveToNext()) {
			int socialID = cursor.getInt(0) + 1;
			cv.put(Social.SOCIAL_ID, socialID);
		}

		if (!StringUtil.isNullOrEmpty(info.userType))
			cv.put(Social.USER_TYPE, info.userType);
		if (!StringUtil.isNullOrEmpty(info.userID))
			cv.put(Social.USER_ID, info.userID);
		if (!StringUtil.isNullOrEmpty(info.appID))
			cv.put(Social.APP_ID, info.appID);
		if (!StringUtil.isNullOrEmpty(info.userName))
			cv.put(Social.USER_NAME, info.userName);
		if (!StringUtil.isNullOrEmpty(info.data))
			cv.put(Social.DATA, info.data);
		if (!StringUtil.isNullOrEmpty(info.time))
			cv.put(Social.TIME, info.time);

		long rowID = -1;
		rowID = db.insert(tableName, null, cv);
		return rowID > -1;
	}

	public boolean insertOrUpdateSpeakerRecords(SpeakerRecordEntity entity) {
		String tableName = getTableNameSpeakerRecords();
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(SpeakerRecord.GW_ID, entity.gwID);
		cv.put(SpeakerRecord.DEV_ID, entity.devID);
		cv.put(SpeakerRecord.EP, entity.ep);
		cv.put(SpeakerRecord.SONG_ID, entity.songID);
		cv.put(SpeakerRecord.SONG_NAME, entity.songName);
		cv.put(SpeakerRecord.AUDIO_TYPE, entity.audioType);

		int affectedRowNum = db.update(tableName, cv, SpeakerRecord.GW_ID
				+ "=?" + " and " + SpeakerRecord.DEV_ID + "=?" + " and "
				+ SpeakerRecord.EP + "=?" + " and " + SpeakerRecord.SONG_ID
				+ "=?", new String[] { entity.gwID, entity.devID, entity.ep,
				entity.songID });

		long rowID = -1;
		if (affectedRowNum <= 0)
			rowID = db.insert(tableName, null, cv);
		return affectedRowNum > 0 || rowID > -1;
	}

	public boolean onDelete(String gwID) {
		boolean result = false;
		SQLiteDatabase db = this.getWritableDatabase();
		db.beginTransaction();
		try {
			String[] clauses = { gwID };
			db.delete(getTableNameArea(false), Area.GW_ID + "=?", clauses);
			db.delete(getTableNameDevice(false), Device.GW_ID + "=?", clauses);
			db.delete(getTableNameDeviceIr(false), DeviceIR.GW_ID + "=?",
					clauses);
			db.delete(getTableNameLoginHis(), SigninRecords.GW_ID + "=?",
					clauses);
			db.delete(getTableNameScene(), Scene.GW_ID + "=?", clauses);
			db.delete(getTableNameTaskAuto(), Task.GW_ID + "=?", clauses);
			db.delete(getTableNameTaskTimer(), Task.GW_ID + "=?", clauses);
			db.delete(getTableNameMsg(false), Messages.GW_ID + "=?", clauses);
			db.delete(getTableNameTimingScene(), TimingScene.GW_ID + "=?",
					clauses);
			db.delete(getTableNameFavority(), Favority.GW_ID + "=?", clauses);
			db.delete(getTableNameShake(), Shake.GW_ID + "=?", clauses);
			db.delete(getTableNameSocial(), Social.GW_ID + "=?", clauses);
			db.setTransactionSuccessful();
			result = true;
		} catch (Exception e) {
			result = false;
		} finally {
			db.endTransaction();
		}
		return result;
	}
	
	public void insertDaikinAirConditionRecords(AirCondition condition) {
		String tableName = DaikinAirConditionRecord.TABLE_DAIKIN_AIR_CONDITION;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(DaikinAirConditionRecord.GW_ID, condition.getGwID());
		cv.put(DaikinAirConditionRecord.DEV_ID, condition.getDevID());
		cv.put(DaikinAirConditionRecord.KEY_ID, condition.getKeyID());
		cv.put(DaikinAirConditionRecord.KEY_NAME, condition.getKeyName());

		db.insert(tableName, null, cv);
	}
	
	public Cursor queryDaikinAirConditionRecords() {
		String tableName = DaikinAirConditionRecord.TABLE_DAIKIN_AIR_CONDITION;
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "select * from " + tableName;
		Cursor result = db.rawQuery(sql, null);
		return result;
	}
	
	public void updataDaikinAirConditionRecords(AirCondition condition) {
		String tableName = DaikinAirConditionRecord.TABLE_DAIKIN_AIR_CONDITION;
		SQLiteDatabase db = this.getWritableDatabase();

		ContentValues cv = new ContentValues();
		cv.put(DaikinAirConditionRecord.GW_ID, condition.getGwID());
		cv.put(DaikinAirConditionRecord.DEV_ID, condition.getDevID());
		cv.put(DaikinAirConditionRecord.KEY_ID, condition.getKeyID());
		cv.put(DaikinAirConditionRecord.KEY_NAME, condition.getKeyName());

		db.update(tableName, cv, DaikinAirConditionRecord.GW_ID + "=? and "
				+ DaikinAirConditionRecord.DEV_ID + " =? and "
				+ DaikinAirConditionRecord.KEY_ID + " =? ",
				new String[] { condition.getGwID(), condition.getDevID(),
						condition.getKeyID() });
	}
	
}
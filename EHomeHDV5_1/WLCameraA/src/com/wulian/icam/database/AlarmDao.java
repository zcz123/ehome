/**
 * Project Name:  iCam
 * File Name:     AlarmDao.java
 * Package Name:  com.wulian.icam.database
 * @Date:         2015年6月12日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wulian.icam.database.MsgContract.MsgArarmTable;
import com.wulian.icam.model.AlarmModel;

/**
 * @ClassName: AlarmDao
 * @Function: 报警数据库操作
 * @Date: 2015年6月12日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class AlarmDao {

	private SQLiteDatabase db;

	public AlarmDao(Context context) {
		db = new MsgDbHelper(context).getWritableDatabase();
	}

	// 插入一条报警，推送的报警都是一条一条的
	public long addAlarm(AlarmModel am, String uuid) {
		ContentValues values = new ContentValues();
		values.put(MsgArarmTable.CN_FROM, am.getFrom());// sip:xxx
		values.put(MsgArarmTable.CN_FUNCTION, am.getFunction());// 安防
		values.put(MsgArarmTable.CN_NAME, am.getName());// 声光报警器
		values.put(MsgArarmTable.CN_RETURN_DATA, am.getReturnData());
		values.put(MsgArarmTable.CN_TIME, am.getTime());
		values.put(MsgArarmTable.CN_TYPE, am.getType());
		values.put(MsgArarmTable.CN_UUID, uuid);
		long id = db.insert(MsgArarmTable.TABLE_NAME, null, values);
		am.setId("" + id);
		return id;
	}

	// 删除一条报警消息
	public int deleteAlarm(String uuid, String id) {
		return db.delete(MsgArarmTable.TABLE_NAME, MsgArarmTable.CN_UUID
				+ "=? and " + MsgArarmTable._ID + "=?",
				new String[] { uuid, id });
	}

	// 删除某人的所有报警消息
	public int deleteAlarmByUuid(String uuid, String type1, String type2) {
		return db.delete(MsgArarmTable.TABLE_NAME, MsgArarmTable.CN_UUID
				+ "=? and (" + MsgArarmTable.CN_TYPE + "!=? or "
				+ MsgArarmTable.CN_TYPE + "!=?)", new String[] { uuid ,type1,type2});
	}
	
	// 删除某人的所有系统消息
	public int deleteSystemByUuid(String uuid, String type1, String type2) {
		return db.delete(MsgArarmTable.TABLE_NAME, MsgArarmTable.CN_UUID
				+ "=? and (" + MsgArarmTable.CN_TYPE + "=? or "
				+ MsgArarmTable.CN_TYPE + "=?)", new String[] { uuid ,type1,type2});
	}

	// 删除多条报警消息
	public void deleteAlarms(String uuid, String... ids) {
		db.beginTransaction();
		try {
			for (String id : ids) {
				deleteAlarm(uuid, id);
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			db.endTransaction();
		}
	}

	// 查询本地数据库中所有的报警消息
	public List<AlarmModel> queryAlarms(String uuid, String type1, String type2) {
		Cursor cursor = null;
		ArrayList<AlarmModel> ams = new ArrayList<AlarmModel>();
		try {
			cursor = db.query(MsgArarmTable.TABLE_NAME, null,
					MsgArarmTable.CN_UUID + "=? and (" + MsgArarmTable.CN_TYPE
							+ "!=? and " + MsgArarmTable.CN_TYPE + "!=?)",
					new String[] { uuid, type1, type2 }, null, null,
					MsgArarmTable.CN_TIME + " desc");
			while (cursor.moveToNext()) {
				AlarmModel am = new AlarmModel();
				am.setFrom(cursor.getString(cursor
						.getColumnIndex(MsgArarmTable.CN_FROM)));
				am.setId(cursor.getString(cursor.getColumnIndex(MsgArarmTable._ID)));
				am.setName(cursor.getString(cursor
						.getColumnIndex(MsgArarmTable.CN_NAME)));
				am.setTime((cursor.getString(cursor
						.getColumnIndex(MsgArarmTable.CN_TIME))));
				am.setType(cursor.getString(cursor
						.getColumnIndex(MsgArarmTable.CN_TYPE)));
				am.setReturnData(cursor.getString(cursor
						.getColumnIndex(MsgArarmTable.CN_RETURN_DATA)));
				ams.add(am);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
		}
		return ams;
	}

	// 查询本地数据库中所有的系统消息
	public List<AlarmModel> querySystem(String uuid, String type1, String type2) {
		Cursor cursor = null;
		ArrayList<AlarmModel> ams = new ArrayList<AlarmModel>();
		try {
			cursor = db.query(MsgArarmTable.TABLE_NAME, null,
					MsgArarmTable.CN_UUID + "=? and (" + MsgArarmTable.CN_TYPE
							+ "=? or " + MsgArarmTable.CN_TYPE + "=?)",
					new String[] { uuid, type1, type2 }, null, null,
					MsgArarmTable.CN_TIME + " desc");
			while (cursor.moveToNext()) {
				AlarmModel am = new AlarmModel();
				am.setFrom(cursor.getString(cursor
						.getColumnIndex(MsgArarmTable.CN_FROM)));
				am.setId(cursor.getString(cursor.getColumnIndex(MsgArarmTable._ID)));
				am.setName(cursor.getString(cursor
						.getColumnIndex(MsgArarmTable.CN_NAME)));
				am.setTime((cursor.getString(cursor
						.getColumnIndex(MsgArarmTable.CN_TIME))));
				am.setType(cursor.getString(cursor
						.getColumnIndex(MsgArarmTable.CN_TYPE)));
				am.setReturnData(cursor.getString(cursor
						.getColumnIndex(MsgArarmTable.CN_RETURN_DATA)));
				ams.add(am);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(cursor!=null){
				cursor.close();
			}
		}
		return ams;
	}

	public void closeDb() {
		db.close();
	}
}

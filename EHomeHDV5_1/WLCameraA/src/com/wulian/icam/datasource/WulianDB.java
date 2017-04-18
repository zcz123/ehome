/**
 * Project Name:  RouteLibrary
 * File Name:     WulianDB.java
 * Package Name:  com.wulian.routelibrary.datasource.database
 * @Date:         2014年12月3日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.datasource;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import com.wulian.icam.model.AlarmMessage;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.OauthMessage;

/**
 * @ClassName: WulianDB
 * @Function: 数据库操作=>>>>>臃肿不堪！！！
 * @Date: 2014年12月3日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class WulianDB extends SQLiteOpenHelper implements DataSchema {// 1、实现接口以获取常量值
																		// 感觉这里逻辑上不通，但是语法上通
																		// 2、导入类名以获取常量值
	public WulianDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// db.execSQL(DevicesTable.CREATE_DEVICES_TABLE_SQL);
		db.execSQL(AlarmMessageTable.CREATE_ALARM_MESSAGE_TABLE_SQL);
		db.execSQL(BindingNoticeTable.CREATE_BUNDING_NOTICES_TABLE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		deleteAllTable(db);
		onCreate(db);
	}

	private void deleteAllTable(SQLiteDatabase db) {
		// db.execSQL(DevicesTable.DROP_DEVICES_TABLE_SQL);
		db.execSQL(AlarmMessageTable.DROP_ALARMMESSAGE_TABLE_SQL);
		db.execSQL(BindingNoticeTable.DROP_BINDINGMESSAGE_TABLE_SQL);
	}

	private void beginTransaction() {
		getWritableDatabase().beginTransaction();
	}

	private void endTransaction() {
		getWritableDatabase().endTransaction();
	}

	private void setTransactionSuccessful() {
		getWritableDatabase().setTransactionSuccessful();
	}

	private int update(String table, ContentValues values, String where,
			String[] whereArgs) {
		int numRows = 0;
		try {
			beginTransaction();
			numRows = getWritableDatabase().update(table, values, where,
					whereArgs);
			setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			endTransaction();
		}
		return numRows;
	}

	private long insert(String table, ContentValues values) {
		long rowId = 0;
		try {
			beginTransaction();
			rowId = getWritableDatabase().insert(table, null, values);
			setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			endTransaction();
		}

		return rowId;
	}

	private Cursor query(String table, String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		return getReadableDatabase().query(table, columns, selection,
				selectionArgs, groupBy, having, orderBy, limit);
	}

	private int delete(String table, String where, String[] whereArgs) {
		int numRows = 0;
		try {
			beginTransaction();
			numRows = getWritableDatabase().delete(table, where, whereArgs);
			setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			endTransaction();
		}

		return numRows;
	}

	// 插入单个设备
	protected boolean insertDevice(Device data, String uuid) {
		boolean succed = false;
		SQLiteStatement insert = null;
		try {
			beginTransaction();
			String sql = "insert into " + DevicesTable.TABLE_NAME + "("
					+ DevicesTable.UUID + "," + DevicesTable.DEVICE_ID + ","
					+ DevicesTable.DEVICE_NAME + "," + DevicesTable.SIP_ACCOUNT
					+ ") values (?,?,?,?)";
			insert = getWritableDatabase().compileStatement(sql);
			int index = 1;
			insert.bindString(index++, uuid);
			insert.bindString(index++, data.getDevice_id());
			insert.bindString(index++, data.getDevice_nick());
			insert.bindString(index++, data.getDevice_id());
			insert.execute();
			setTransactionSuccessful();
			succed = true;
		} catch (SQLException e) {
			e.printStackTrace();
			succed = false;
		} finally {
			if (insert != null) {
				insert.close();
			}
			endTransaction();
		}
		return succed;
	}

	// 插入多个设备
	protected boolean insertDevice(List<Device> data, String uuid) {
		boolean succed = false;
		SQLiteStatement insert = null;
		try {
			beginTransaction();
			String sql = "insert into " + DevicesTable.TABLE_NAME + "("
					+ DevicesTable.UUID + "," + DevicesTable.DEVICE_ID + ","
					+ DevicesTable.DEVICE_NAME + "," + DevicesTable.SIP_ACCOUNT
					+ ") values (?,?,?,?)";
			insert = getWritableDatabase().compileStatement(sql);
			int size = data.size();
			int index;
			for (int i = 0; i < size; i++) {
				index = 1;
				Device item = data.get(i);
				insert.bindString(index++, uuid);
				insert.bindString(index++, item.getDevice_id());
				insert.bindString(index++, item.getDevice_nick());
				insert.bindString(index++, "sip:" + item.getDevice_id() + "@"
						+ item.getSip_domain());
				insert.execute();
			}
			setTransactionSuccessful();
			succed = true;
		} catch (SQLException e) {
			e.printStackTrace();
			succed = false;
		} finally {
			if (insert != null) {
				insert.close();
			}
			endTransaction();
		}
		return succed;
	}

	// 更新设备描述
	protected int updateDevice(Device data, String uuid) {
		ContentValues values = new ContentValues();
		values.put(DevicesTable.DEVICE_NAME, data.getDevice_nick());
		return update(DevicesTable.TABLE_NAME, values, DevicesTable.UUID
				+ "=? and " + DevicesTable.DEVICE_ID + "=?", new String[] {
				uuid, data.getDevice_id() });
	}

	// 删除所有设备
	protected int deleteDevice(String uuid) {
		return getReadableDatabase().delete(DevicesTable.TABLE_NAME,
				DevicesTable.UUID + "=?", new String[] { uuid });
	}

	// 删除单个设备
	protected int deleteDevice(Device data, String uuid) {
		return getReadableDatabase().delete(DevicesTable.TABLE_NAME,
				DevicesTable.UUID + "=? and " + DevicesTable.DEVICE_ID + "=?",
				new String[] { uuid, data.getDevice_id() });
	}

	// 查询设备
	protected List<Device> queryDevices(String uuid) {
		List<Device> list = new ArrayList<Device>();
		Cursor cursor = null;
		try {
			cursor = getReadableDatabase().query(
					DevicesTable.TABLE_NAME,
					new String[] { DevicesTable.DEVICE_ID,
							DevicesTable.DEVICE_NAME },
					DevicesTable.UUID + "=?", new String[] { uuid }, null,
					null, null);
			int num = cursor.getCount();
			cursor.moveToFirst();
			for (int i = 0; i < num; i++) {
				Device item = new Device();
				item.setDevice_id(cursor.getColumnName(0));
				item.setDevice_nick(cursor.getColumnName(1));
				list.add(item);
				cursor.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return list;
	}

	// 插入消息
	protected boolean insertAlarmMessage(AlarmMessage data, String uuid) {
		boolean succed = false;
		SQLiteStatement insert = null;
		try {
			beginTransaction();
			String sql = "insert into " + AlarmMessageTable.TABLE_NAME + "("
					+ AlarmMessageTable.EVENT_TYPE + ","
					+ AlarmMessageTable.UUID + "," + AlarmMessageTable.SEND_URI
					+ "," + AlarmMessageTable.MSG_TIME + ") values (?,?,?,?)";
			insert = getWritableDatabase().compileStatement(sql);
			int index = 1;
			insert.bindLong(index++, data.getMsgType());
			insert.bindString(index++, uuid);
			insert.bindString(index++, data.getSendUri());
			insert.bindLong(index++, data.getStime());
			insert.execute();
			setTransactionSuccessful();
			succed = true;
		} catch (SQLException e) {
			e.printStackTrace();
			succed = false;
		} finally {
			insert.close();
			endTransaction();
		}
		return succed;
	}

	// 删除全部消息
	protected int deleteAlarmMessage(String uuid) {
		return getWritableDatabase().delete(AlarmMessageTable.TABLE_NAME,
				AlarmMessageTable.UUID + "=?", new String[] { uuid });
	}

	// 删除一条消息
	protected int deleteAlarmMessage(AlarmMessage data, String uuid) {
		return getWritableDatabase().delete(
				AlarmMessageTable.TABLE_NAME,
				AlarmMessageTable.UUID + "=? and " + AlarmMessageTable._ID
						+ "=?",
				new String[] { uuid, String.valueOf(data.getId()) });
	}

	// 查询消息
	protected List<AlarmMessage> queryAlarmMessages(String uuid) {
		List<AlarmMessage> list = new ArrayList<AlarmMessage>();
		Cursor cursor = null;
		/*
		 * String sql="select " + AlarmMessageTable.TABLE_NAME + "." +
		 * AlarmMessageTable._ID + "," + AlarmMessageTable.TABLE_NAME + "." +
		 * AlarmMessageTable.EVENT_TYPE + "," + DevicesTable.TABLE_NAME + "." +
		 * DevicesTable.DEVICE_NAME + "," + AlarmMessageTable.TABLE_NAME + "." +
		 * AlarmMessageTable.MSG_TIME + " from " + AlarmMessageTable.TABLE_NAME
		 * + " inner join " + DevicesTable.TABLE_NAME + " on " +
		 * DevicesTable.TABLE_NAME + "." + DevicesTable.SIP_ACCOUNT + " = " +
		 * AlarmMessageTable.TABLE_NAME + "." + AlarmMessageTable.SEND_URI +
		 * " where " + DevicesTable.TABLE_NAME + "." + DevicesTable.UUID + "=?";
		 */
		try {
			String sql = "select " + AlarmMessageTable.TABLE_NAME + "."
					+ AlarmMessageTable._ID + ","
					+ AlarmMessageTable.TABLE_NAME + "."
					+ AlarmMessageTable.EVENT_TYPE + ","
					+ AlarmMessageTable.TABLE_NAME + "."
					+ AlarmMessageTable.MSG_TIME + ","
					+ AlarmMessageTable.TABLE_NAME + "."
					+ AlarmMessageTable.SEND_URI + " from "
					+ AlarmMessageTable.TABLE_NAME + " where "
					+ AlarmMessageTable.UUID + "=?" + "order by "
					+ AlarmMessageTable._ID + " desc";
			cursor = getReadableDatabase().rawQuery(sql, new String[] { uuid });
			int num = cursor.getCount();
			cursor.moveToFirst();
			for (int i = 0; i < num; i++) {
				AlarmMessage item = new AlarmMessage();
				item.setId(cursor.getLong(0));
				item.setMsgType((int) cursor.getLong(1));
				item.setSendUri(cursor.getString(3));
				item.setStime(cursor.getLong(2));
				list.add(item);
				cursor.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}

	// 查询指定设备的消息
	protected List<AlarmMessage> queryAlarmMessages(String uuid, String send_uri) {
		List<AlarmMessage> list = new ArrayList<AlarmMessage>();
		Cursor cursor = null;
		/*
		 * Cursor cursor = getReadableDatabase().rawQuery( "select " +
		 * AlarmMessageTable.TABLE_NAME + "." + AlarmMessageTable._ID + "," +
		 * AlarmMessageTable.TABLE_NAME + "." + AlarmMessageTable.EVENT_TYPE +
		 * "," + DevicesTable.TABLE_NAME + "." + DevicesTable.DEVICE_NAME + ","
		 * + AlarmMessageTable.TABLE_NAME + "." + AlarmMessageTable.MSG_TIME +
		 * " from " + AlarmMessageTable.TABLE_NAME + " inner join " +
		 * DevicesTable.TABLE_NAME + " on " + DevicesTable.TABLE_NAME + "." +
		 * DevicesTable.SIP_ACCOUNT + " = " + AlarmMessageTable.TABLE_NAME + "."
		 * + AlarmMessageTable.SEND_URI + " where " + DevicesTable.UUID +
		 * "=? and " + DevicesTable.SIP_ACCOUNT + "=?", new String[] { uuid,
		 * device_uri });
		 */
		try {
			String sql = "select " + AlarmMessageTable.TABLE_NAME + "."
					+ AlarmMessageTable._ID + ","
					+ AlarmMessageTable.TABLE_NAME + "."
					+ AlarmMessageTable.EVENT_TYPE + ","
					+ AlarmMessageTable.TABLE_NAME + "."
					+ AlarmMessageTable.MSG_TIME + ","
					+ AlarmMessageTable.TABLE_NAME + "."
					+ AlarmMessageTable.SEND_URI + " from "
					+ AlarmMessageTable.TABLE_NAME + " where "
					+ AlarmMessageTable.UUID + "=? and "
					+ AlarmMessageTable.SEND_URI + "=?";
			cursor = getReadableDatabase().rawQuery(sql,
					new String[] { uuid, send_uri });
			int num = cursor.getCount();
			cursor.moveToFirst();
			for (int i = 0; i < num; i++) {
				AlarmMessage item = new AlarmMessage();
				item.setId(cursor.getLong(0));
				item.setMsgType((int) cursor.getLong(1));
				item.setSendUri(cursor.getString(3));
				item.setStime(cursor.getLong(2));
				list.add(item);
				cursor.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}

	// 查询指定设备的未读信息
	protected List<OauthMessage> queryBindingNoticesMessages(String uuid,
			String send_uri) {
		List<OauthMessage> list = new ArrayList<OauthMessage>();
		Cursor cursor = null;
		try {
			String sql = "select " + BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable._ID + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.DEVICE_ID + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.TIME + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.TYPE + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.UUID + " from "
					+ BindingNoticeTable.TABLE_NAME + " where "
					+ BindingNoticeTable.UUID + "=? and "
					+ BindingNoticeTable.DEVICE_ID + "=?";
			cursor = getReadableDatabase().rawQuery(sql,
					new String[] { uuid, send_uri });
			int num = cursor.getCount();
			cursor.moveToFirst();
			for (int i = 0; i < num; i++) {
				OauthMessage item = new OauthMessage();
				item.setId(cursor.getLong(0));
				item.setDevice_id(cursor.getString(1));
				item.setTime(cursor.getLong(2));
				item.setType(cursor.getInt(3));
				list.add(item);
				cursor.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}

	// 查询未读消息
	protected List<OauthMessage> queryBindingNoticesMessages(String uuid) {
		List<OauthMessage> list = new ArrayList<OauthMessage>();
		Cursor cursor = null;
		try {
			String sql = "select " + BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable._ID + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.DEVICE_ID + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.TIME + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.TYPE + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.USERNAME + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.PHONE + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.EMAIL + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.UUID + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.DESC + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.ISUNREAD + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.ISACCEPT + ","
					+ BindingNoticeTable.TABLE_NAME + "."
					+ BindingNoticeTable.ISHANDLE + " from "
					+ BindingNoticeTable.TABLE_NAME + " where "
					+ BindingNoticeTable.UUID + "=?" + "order by "
					+ BindingNoticeTable._ID + " desc";
			cursor = getReadableDatabase().rawQuery(sql, new String[] { uuid });
			int num = cursor.getCount();
			cursor.moveToFirst();
			for (int i = 0; i < num; i++) {
				OauthMessage item = new OauthMessage();
				item.setId(cursor.getLong(0));
				item.setDevice_id(cursor.getString(1));
				item.setTime(cursor.getLong(2));
				item.setType(cursor.getInt(3));
				item.setUserName(cursor.getString(4));
				item.setPhone(cursor.getString(5));
				item.setEmail(cursor.getString(6));
				item.setDesc(cursor.getString(8));
				item.setIsUnread(cursor.getInt(9) == 0 ? false : true);
				item.setAccept(cursor.getInt(10) == 0 ? false : true);
				item.setHandle(cursor.getInt(11) == 0 ? false : true);
				list.add(item);
				cursor.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return list;
	}

	// 删除一条未读消息
	protected int deleteBindingNoticeMessage(OauthMessage data, String uuid) {
		return getWritableDatabase().delete(
				BindingNoticeTable.TABLE_NAME,
				BindingNoticeTable.UUID + "=? and " + BindingNoticeTable._ID
						+ "=?",
				new String[] { uuid, String.valueOf(data.getId()) });
	}

	// 删除一条未读消息
	protected int deleteBindingNoticeMessage(Long id, String uuid) {
		return getWritableDatabase().delete(
				BindingNoticeTable.TABLE_NAME,
				BindingNoticeTable.UUID + "=? and " + BindingNoticeTable._ID
						+ "=?", new String[] { uuid, id + "" });
	}

	// 修改一条未读消息
	protected int updateBindingNoticeMessage(OauthMessage data, String uuid) {
		ContentValues values = new ContentValues();
		values.put(BindingNoticeTable.ISUNREAD, data.getIsUnread() ? 1 : 0);
		values.put(BindingNoticeTable.ISACCEPT, data.isAccept() ? 1 : 0);
		values.put(BindingNoticeTable.ISHANDLE, data.isHandle() ? 1 : 0);
		return getWritableDatabase().update(
				BindingNoticeTable.TABLE_NAME,
				values,
				BindingNoticeTable.UUID + "=? and " + BindingNoticeTable._ID
						+ "=?", new String[] { uuid, data.getId() + "" });
	}

	// 删除多条未读消息
	protected void deleteBindingNoticesMes(String uuid, Long... ids) {
		beginTransaction();
		try {
			for (Long id : ids) {
				deleteBindingNoticeMessage(id, uuid);
			}
			setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			endTransaction();
		}
	}

	// 删除全部未读消息
	protected int deleteAllBindingNoticeMessage(String uuid) {
		return getWritableDatabase().delete(BindingNoticeTable.TABLE_NAME,
				BindingNoticeTable.UUID + "=?", new String[] { uuid });
	}

	// 插入一条未读消息
	protected boolean insertBindingNoticeMessage(OauthMessage data, String uuid) {
		boolean succed = false;
		SQLiteStatement insert = null;
		try {
			beginTransaction();
			String sql = "insert into " + BindingNoticeTable.TABLE_NAME + "("
					+ BindingNoticeTable.TYPE + "," + BindingNoticeTable.UUID
					+ "," + BindingNoticeTable.DEVICE_ID + ","
					+ BindingNoticeTable.USERNAME + ","
					+ BindingNoticeTable.EMAIL + "," + BindingNoticeTable.PHONE
					+ "," + BindingNoticeTable.TIME + ","
					+ BindingNoticeTable.DESC + ","
					+ BindingNoticeTable.ISUNREAD + ","
					+ BindingNoticeTable.ISACCEPT +","
					+ BindingNoticeTable.ISHANDLE
					+ ") values (?,?,?,?,?,?,?,?,?,?,?)";
			insert = getWritableDatabase().compileStatement(sql);
			int index = 1;
			insert.bindLong(index++, data.getType());
			insert.bindString(index++, uuid);
			insert.bindString(index++, data.getDevice_id());
			insert.bindString(index++, data.getUserName());
			insert.bindString(index++, data.getEmail());
			insert.bindString(index++, data.getPhone());
			insert.bindLong(index++, data.getTime());
			insert.bindString(index++, data.getDesc());
			insert.bindString(index++, data.getIsUnread() ? "1" : "0");
			insert.bindString(index++, data.isAccept() ? "1" : "0");
			insert.bindString(index++, data.isHandle() ? "1" : "0");
			insert.execute();
			setTransactionSuccessful();
			succed = true;
		} catch (SQLException e) {
			e.printStackTrace();
			succed = false;
		} finally {
			if (insert != null) {
				insert.close();
				insert = null;
			}
			endTransaction();
		}
		return succed;
	}

	// 插入多条未读消息
	protected boolean insertBindingNoticeMessages(List<OauthMessage> data,
			String uuid) {
		boolean succed = false;
		SQLiteStatement insert = null;
		try {
			beginTransaction();
			String sql = "insert into " + BindingNoticeTable.TABLE_NAME + "("
					+ BindingNoticeTable.TYPE + "," + BindingNoticeTable.UUID
					+ "," + BindingNoticeTable.DEVICE_ID + ","
					+ BindingNoticeTable.USERNAME + ","
					+ BindingNoticeTable.EMAIL + "," + BindingNoticeTable.PHONE
					+ "," + BindingNoticeTable.TIME + ","
					+ BindingNoticeTable.DESC + ","
					+ BindingNoticeTable.ISUNREAD + ","
					+ BindingNoticeTable.ISACCEPT + ","
					+ BindingNoticeTable.ISHANDLE
					+ ") values (?,?,?,?,?,?,?,?,?,?,?)";
			insert = getWritableDatabase().compileStatement(sql);
			for (int i = data.size() - 1; i >= 0; i--) {
				int index = 1;
				insert.bindLong(index++, data.get(i).getType());
				insert.bindString(index++, uuid);
				insert.bindString(index++, data.get(i).getDevice_id());
				insert.bindString(index++, data.get(i).getUserName());
				insert.bindString(index++, data.get(i).getEmail());
				insert.bindString(index++, data.get(i).getPhone());
				insert.bindLong(index++, data.get(i).getTime());
				insert.bindString(index++, data.get(i).getDesc());
				insert.bindString(index++, data.get(i).getIsUnread() ? "1"
						: "0");
				insert.bindString(index++, data.get(i).isAccept() ? "1" : "0");
				insert.bindString(index++, data.get(i).isHandle() ? "1" : "0");
				insert.execute();
			}
			setTransactionSuccessful();
			succed = true;
		} catch (SQLException e) {
			e.printStackTrace();
			succed = false;
		} finally {
			if (insert != null) {
				insert.close();
				insert = null;
			}
			endTransaction();
		}
		return succed;
	}
}

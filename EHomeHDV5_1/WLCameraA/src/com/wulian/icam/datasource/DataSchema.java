/**
 * Project Name:  RouteLibrary
 * File Name:     DataSchema.java
 * Package Name:  com.wulian.routelibrary.datasource.database
 * @Date:         2014年12月3日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.datasource;

import android.provider.BaseColumns;

/**
 * @ClassName: DataSchema
 * @Function: 数据库设计
 * @Date: 2014年12月3日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public interface DataSchema {
	public static final int DATABASE_VERSION = 10;
	public static final String DATABASE_NAME = "wuliancamera.db";
	public static final String DROP_TABLE = "drop table if exists ";

	// 设备表
	public interface DevicesTable extends BaseColumns {
		String TABLE_NAME = "devices";
		String DROP_DEVICES_TABLE_SQL = DROP_TABLE + TABLE_NAME;

		String UUID = "uuid";
		String DEVICE_ID = "device_id";// 设备号
		String DEVICE_NAME = "device_name";// 设备名称
		String SIP_ACCOUNT = "sip_account";// Sip账号

		String CREATE_DEVICES_TABLE_SQL = "CREATE TABLE " + TABLE_NAME + "("
				+ _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + UUID
				+ " text not null," + DEVICE_ID + " text not null,"
				+ DEVICE_NAME + " text not null," + SIP_ACCOUNT
				+ " text not null, " + "unique (" + DEVICE_ID + ")" + ");";
	}

	// 警报表
	public interface AlarmMessageTable extends BaseColumns {
		String TABLE_NAME = "alarm_message";
		String DROP_ALARMMESSAGE_TABLE_SQL = DROP_TABLE + TABLE_NAME;

		// String MESSAGE_ID="message_id";
		String EVENT_TYPE = "event_type";// 事件类型
		String UUID = "uuid";// 接受者URI
		String SEND_URI = "send_uri";// 发送者URI
		String MSG_TIME = "msg_time";// 消息时间

		String CREATE_ALARM_MESSAGE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME
				+ "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ EVENT_TYPE + " INTEGER not null," + UUID + " text not null,"
				+ SEND_URI + " text not null," + MSG_TIME + " INTEGER" + ");";
	}

	// 警报表
	public interface OauthMessageTable extends BaseColumns {
		String TABLE_NAME = "oauth_message";
		String DROP_ALARMMESSAGE_TABLE_SQL = DROP_TABLE + TABLE_NAME;

		String UUID = "uuid";// 接受者URI
		String DESCRIPTION = "description";// 描述
		String USERNAME = "username";// 用户名
		String PHONE = "phone";// 电话号码
		String EMAIL = "email";// 邮箱
		String DEVICE_ID = "device_id";// 设备ID
		String MSG_TIME = "msg_time";// 消息时间

		String cREATE_OAUTH_MESSAGE_TABLE_SQL = "CREATE TABLE " + TABLE_NAME
				+ "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + UUID
				+ " text not null," + DESCRIPTION + " text not null,"
				+ USERNAME + " text," + PHONE + " text," + EMAIL + " text,"
				+ DEVICE_ID + " text not null," + MSG_TIME + " INTEGER" + ");";
	}

	// 未读信息表
	public interface BindingNoticeTable extends BaseColumns {
		String TABLE_NAME = "binding_notices";
		String DROP_BINDINGMESSAGE_TABLE_SQL = DROP_TABLE + TABLE_NAME;

		String DEVICE_ID = "device_id";// 设备ID
		String TYPE = "type";
		String USERNAME = "username";// 用户名
		String PHONE = "phone";// 电话号码
		String EMAIL = "email";// 邮箱
		String TIME = "time"; // 信息创建时间
		String UUID = "uuid";// 接收者URI
		String DESC = "desc";// 请求说明
		String ISUNREAD = "isunread";// 是否已查看
		String ISACCEPT = "isaccept";// 是否同意(同意授权，同意查看等)
		String ISHANDLE = "ishandle";// 是否已处理(同意授权，同意查看等)

		String CREATE_BUNDING_NOTICES_TABLE_SQL = "CREATE TABLE " + TABLE_NAME
				+ "(" + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DEVICE_ID
				+ " text not null," + TYPE + " text not null," + USERNAME
				+ " text ," + PHONE + " text ," + EMAIL + " text ," + TIME
				+ " text not null," + UUID + " INTEGER," + DESC + " text ,"
				+ ISUNREAD + " INTEGER ," + ISACCEPT + " INTEGER ," + ISHANDLE
				+ " INTEGER" + ");";
	}
}

/**
 * Project Name:  iCam
 * File Name:     MsgContract.java
 * Package Name:  com.wulian.icam.database
 * @Date:         2015年6月12日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.database;

import android.provider.BaseColumns;

/**
 * @ClassName: MsgContract
 * @Function: 消息中心的数据结构
 * @Date: 2015年6月12日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public final class MsgContract {
	private MsgContract() {
	}

	private static final String TYPE_TEXT = " TEXT, ";
	private static final String DROP_TABLE = "DROP TABLE IF EXISTS ";

	// 报警表(抽象类描述)
	public static abstract class MsgArarmTable implements BaseColumns {
		public static final String TABLE_NAME = "alarm";
		public static final String CN_UUID = "uuid";// 支持多账户
		public static final String CN_NAME = "name";// 名称
		public static final String CN_TYPE = "type";// 类型
		public static final String CN_FUNCTION = "function";// 功能
		public static final String CN_RETURN_DATA = "returnData";// 返回数据
		public static final String CN_FROM = "fromDevice";// 来自
		public static final String CN_TIME = "time";// 时间
		public static final String SQL_CREATE_ALARM = "CREATE TABLE "
				+ TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + CN_UUID + TYPE_TEXT
				+ CN_NAME + TYPE_TEXT + CN_TYPE + TYPE_TEXT + CN_FUNCTION
				+ TYPE_TEXT + CN_RETURN_DATA + TYPE_TEXT + CN_FROM + TYPE_TEXT
				+ CN_TIME + " text )";
		public static final String SQL_DROP_ALARM = DROP_TABLE + TABLE_NAME;
	}

	// 授权表 abstract class=>interface(接口描述,默认public static final,更加简洁)
	public static interface MsgOauthTable extends BaseColumns {
		String TABLE_NAME = "oauth";
		String SQL_DROP_OAUTH = DROP_TABLE + TABLE_NAME;;

		String UUID = "uuid";// 支持多账户
		String DEVICEID = "deviceid";
		String TYPE = "type";
		String EMAIL = "email";
		String USERNAME = "username";
		String DESC = "desc";
		String CREATEDAT = "createat";

		String SQL_CREATE_OAUTH = "CREATE TABLE " + TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + UUID + TYPE_TEXT
				+ DEVICEID + TYPE_TEXT + TYPE + TYPE_TEXT + EMAIL + TYPE_TEXT
				+ USERNAME + TYPE_TEXT + DESC + TYPE_TEXT + CREATEDAT
				+ " text )";
	}
}

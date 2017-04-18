/**
 * Project Name:  iCam
 * File Name:     MsgDBHelper.java
 * Package Name:  com.wulian.icam.database
 * @Date:         2015年6月12日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @ClassName: MsgDbHelper
 * @Function: 消息中心数据库=>orm框架
 * @Date: 2015年6月12日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class MsgDbHelper extends SQLiteOpenHelper {
	public static final int DATABASE_VERSION = 1;// 修改表结构则递增一个
	public static final String DATABASE_NAME = "MessgeInfo.db";

	public MsgDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(MsgContract.MsgArarmTable.SQL_CREATE_ALARM);
		db.execSQL(MsgContract.MsgOauthTable.SQL_CREATE_OAUTH);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 这里的数据库操作逻辑可优化：尽量保持旧数据
		db.execSQL(MsgContract.MsgArarmTable.SQL_DROP_ALARM);
		db.execSQL(MsgContract.MsgOauthTable.SQL_DROP_OAUTH);
		onCreate(db);
	}


}

/**
 * Project Name:  RouteLibrary
 * File Name:     DataSource.java
 * Package Name:  com.wulian.routelibrary.datasource
 * @Date:         2014年12月3日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.datasource;

import java.util.List;

import android.content.Context;

import com.wulian.icam.model.AlarmMessage;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.OauthMessage;

/**
 * @ClassName: DataSource
 * @Function: 数据库外部调用
 * @Date: 2014年12月3日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DataSource {// wjj:这个类完全可以不要，因为仅仅是方法调用方法，没有任何逻辑，浪费。
	private Context mAppContext;
	private WulianDB mDB;

	public DataSource(Context context) {
		mAppContext = context;
		getDB();
	}

	private WulianDB getDB() {
		if (mDB == null) {
			mDB = new WulianDB(mAppContext);
		}
		return mDB;
	}

	// 插入单个设备
	public boolean insert(Device data, String uuid) {
		return getDB().insertDevice(data, uuid);
	}

	// 插入多个设备
	public boolean insertDevice(List<Device> data, String uuid) {
		getDB().deleteDevice(uuid);// 暂时不删除过往消息记录，后期优化
		if (data != null) {
			return getDB().insertDevice(data, uuid);
		} else {
			return true;
		}
	}

	// 更新设备描述
	public int updateDevice(Device data, String uuid) {
		return getDB().updateDevice(data, uuid);
	}

	// 删除所有设备
	public int deleteDevice(String uuid) {
		return getDB().deleteDevice(uuid);
	}

	// 删除设备
	public int deleteDevice(Device data, String uuid) {
		return getDB().deleteDevice(data, uuid);
	}

	// 查询设备
	public List<Device> queryDevices(String uuid) {
		return getDB().queryDevices(uuid);
	}

	// 插入消息
	public boolean insertAlarmMessage(AlarmMessage data, String uuid) {
		return getDB().insertAlarmMessage(data, uuid);
	}

	// 删除全部消息
	public int deleteAlarmMessage(String uuid) {
		return getDB().deleteAlarmMessage(uuid);
	}

	// 删除一条消息
	public int deleteAlarmMessage(AlarmMessage data, String uuid) {
		return getDB().deleteAlarmMessage(data, uuid);
	}

	// 删除多条消息
	public int deleteAlarmMessage(List<AlarmMessage> data, String uuid) {
		if (data == null) {
			return 0;
		}
		int size = data.size();
		int result = 0;
		for (int i = 0; i < size; i++) {
			result += getDB().deleteAlarmMessage(data.get(i), uuid);
		}
		return result;
	}

	// 查询消息
	public List<AlarmMessage> queryAlarmMessages(String uuid) {
		return getDB().queryAlarmMessages(uuid);
	}

	// 查询指定设备的消息
	public List<AlarmMessage> queryAlarmMessages(String uuid, String device_uri) {
		return getDB().queryAlarmMessages(uuid, device_uri);
	}

	// 插入一条未读消息信息
	public boolean insertBindingNoticeMessage(OauthMessage data,
			String uuid) {
		return getDB().insertBindingNoticeMessage(data, uuid);
	}

	// 插入多条未读消息信息
	public boolean insertBindingNoticeMessages(List<OauthMessage> data,
			String uuid) {
		return getDB().insertBindingNoticeMessages(data, uuid);
	}

	// 删除全部未读消息信息
	public int deleteAllBindingNoticeMessage(String uuid) {
		return getDB().deleteAllBindingNoticeMessage(uuid);
	}

	// 删除单条未读消息
	public int deleteBindingNoticeMessage(OauthMessage data, String uuid) {
		return getDB().deleteBindingNoticeMessage(data, uuid);
	}

	// 获取所有未读消息
	public List<OauthMessage> queryBindingNotices(String uuid) {
		return getDB().queryBindingNoticesMessages(uuid);
	}
	// 删除多条未读消息
	public void deleteBindingNoticesMessage(String uuid,Long... ids){
		getDB().deleteBindingNoticesMes(uuid, ids);
	}
	// 更新一条未读消息
	public int updateBindingNoticeMessage(OauthMessage data, String uuid){
		return getDB().updateBindingNoticeMessage(data, uuid);
	}
}

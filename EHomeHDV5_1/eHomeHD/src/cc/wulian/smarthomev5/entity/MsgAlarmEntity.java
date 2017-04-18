package cc.wulian.smarthomev5.entity;

import android.text.TextUtils;
import cc.wulian.smarthomev5.databases.entitys.Messages;

public class MsgAlarmEntity
{
	public String alarmMsgID;
	public String gwID;
	public String devID;
	public String devName;
	public String epType;
	public String alarmTime;
	public String type;

	public MsgAlarmEntity()
	{

	}

	public String getAlarmMsgID() {
		return alarmMsgID;
	}

	public void setAlarmMsgID(String alarmMsgID) {
		this.alarmMsgID = alarmMsgID;
	}

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getDevID() {
		return devID;
	}

	public void setDevID(String devID) {
		this.devID = devID;
	}

	public String getEpType() {
		return epType;
	}

	public void setEpType(String epType) {
		this.epType = epType;
	}

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}

	public String getAlarmTime() {
		return alarmTime;
	}

	public void setAlarmTime(String alarmTime) {
		this.alarmTime = alarmTime;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public MsgAlarmEntity( String alarmMsgID, String gwID, String devID, String devName, String epType, String alarmTime, String type )
	{
		this.alarmMsgID = alarmMsgID;
		this.gwID = gwID;
		this.devID = devID;
		this.devName = devName;
		this.epType = epType;
		this.alarmTime = alarmTime;
		this.type = type;
	}

	public boolean isMessageAlarm() {
		return TextUtils.equals(Messages.TYPE_DEV_ALARM, type);
	}

	
	public static int valueOf(int size) {
		// TODO Auto-generated method stub
		return size;
	}
}

package cc.wulian.smarthomev5.entity;

import android.text.TextUtils;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.Messages;

public class MessageEventEntity implements Comparable<MessageEventEntity> {
    public String epStatus;
    public String userID;
    public String msgID;
    public String gwID;
    public String devID;
    public String ep;
    public String epType;
    public String epName;
    public String epData;
    public String time;
    public String priority;
    public String type;
    public String smile;
    public String epMsg;
    public String extData;

    public MessageEventEntity() {
    }

    public MessageEventEntity(String epStatus, String userID, String msgID,
                              String gwID, String devID, String ep, String epType,
                              String epName, String epData, String time, String priority,
                              String type, String smile, String epMsg) {
        this.epStatus = epStatus;
        this.userID = userID;
        this.msgID = msgID;
        this.gwID = gwID;
        this.devID = devID;
        this.ep = ep;
        this.epType = epType;
        this.epName = epName;
        this.epData = epData;
        this.time = time;
        this.priority = priority;
        this.type = type;
        this.smile = smile;
        this.epMsg = epMsg;
    }

    public String getMsgID() {
        return msgID;
    }

    public String getEpStatus() {
        return epStatus;
    }

    public void setEpStatus(String epStatus) {
        this.epStatus = epStatus;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }


    public String getGwID() {
        return gwID;
    }

    public void setGwID(String gwID) {
        this.gwID = gwID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDevID() {
        return devID;
    }

    public void setDevID(String devID) {
        this.devID = devID;
    }

    public String getEp() {
        return ep;
    }

    public void setEp(String ep) {
        this.ep = ep;
    }

    public String getEpName() {
        return epName;
    }

    public void setEpName(String epName) {
        this.epName = epName;
    }

    public String getEpType() {
        return epType;
    }

    public void setEpType(String epType) {
        this.epType = epType;
    }

    public String getEpData() {
        return epData;
    }

    public void setEpData(String epData) {
        this.epData = epData;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSmile() {
        return smile;
    }

    public void setSmile(String smile) {
        this.smile = smile;
    }

    public String getEpMsg() {
        return epMsg;
    }

    public void setEpMsg(String epMsg) {
        this.epMsg = epMsg;
    }


    public String getExtData() {
        return extData;
    }

    public void setExtData(String extData) {
        this.extData = extData;
    }

    public boolean isMessageScene() {
        return TextUtils.equals(Messages.TYPE_SCENE_OPERATION, type);
    }

    public boolean isMessageSensor() {
        return TextUtils.equals(Messages.TYPE_DEV_SENSOR_DATA, type);
    }

    public boolean isMessageAlarm() {
        return TextUtils.equals(Messages.TYPE_DEV_ALARM, type);
    }

    public boolean isMessageOffline() {
        return TextUtils.equals(Messages.TYPE_DEV_OFFLINE, type);
    }

    public boolean isMessageDestory() {
        return TextUtils.equals(Messages.TYPE_DEV_DESTORY, type);
    }

    public boolean isMessageLowPower() {
        return TextUtils.equals(Messages.TYPE_DEV_LOW_POWER, type);
    }

    public boolean isMessageOnline() {
        return TextUtils.equals(Messages.TYPE_DEV_ONLINE, type);
    }

    @Override
    public int compareTo(MessageEventEntity another) {
        long timeLeft = StringUtil.toLong(this.time);
        long timeRight = StringUtil.toLong(another.getTime());
        if (timeLeft < timeRight)
            return 1;
        else if (timeLeft > timeRight)
            return -1;
        return 0;
    }
}
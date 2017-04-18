package com.wulian.iot.bean;

import java.io.Serializable;

/**
 * Created by syf on 2016/10/20.
 */

public class IOTCDevConfigWifiPojo implements Serializable {
    private String tutkUid;
    private String tutkPwd;
    private String aimSSid;//配置wifi名称
    private String aimPwd;//配置wifi密码
    private int entryMode;//通过设备获取配网模式
    private int configWifiType;//0 猫眼 1门锁
    private int configDeviceMode;//通过设备获取配网模式
    private String door_89_deviceId;//配网门锁所需deviceId
    public void setDoor_89_deviceId(String door_89_deviceId) {
        this.door_89_deviceId = door_89_deviceId;
    }

    public String getDoor_89_deviceId() {
        return door_89_deviceId;
    }

    public String getTutkUid() {
        return tutkUid;
    }

    public void setTutkUid(String tutkUid) {
        this.tutkUid = tutkUid;
    }

    public String getTutkPwd() {
        return tutkPwd;
    }

    public void setTutkPwd(String tutkPwd) {
        this.tutkPwd = tutkPwd;
    }

    public String getAimSSid() {
        return aimSSid;
    }

    public void setAimSSid(String aimSSid) {
        this.aimSSid = aimSSid;
    }

    public String getAimPwd() {
        return aimPwd;
    }

    public void setAimPwd(String aimPwd) {
        this.aimPwd = aimPwd;
    }

    public int getConfigWifiType() {
        return configWifiType;
    }

    public void setConfigWifiType(int configWifiType) {
        this.configWifiType = configWifiType;
    }

    public int getEntryMode() {
        return entryMode;
    }

    public void setEntryMode(int entryMode) {
        this.entryMode = entryMode;
    }

    public int getConfigDeviceMode() {
        return configDeviceMode;
    }

    public void setConfigDeviceMode(int configDeviceMode) {
        this.configDeviceMode = configDeviceMode;
    }
}

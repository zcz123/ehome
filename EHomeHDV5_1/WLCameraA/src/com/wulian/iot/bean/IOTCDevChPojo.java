package com.wulian.iot.bean;

import java.io.Serializable;

/**
 * Created by syf on 2016/10/21.
 */

public class IOTCDevChPojo implements Serializable {
    private String tutkUid;
    private String tutkPwd;
    private int devConnMode;//连接模式
    private String devTag;//设备标记

    public IOTCDevChPojo(String tutkUid, String tutkPwd, int devConnMode, String devTag) {
        this.tutkUid = tutkUid;
        this.tutkPwd = tutkPwd;
        this.devConnMode = devConnMode;
        this.devTag = devTag;
    }

    public IOTCDevChPojo(){

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

    public void setDevConnMode(int devConnMode) {
        this.devConnMode = devConnMode;
    }

    public int getDevConnMode() {
        return devConnMode;
    }

    public String getDevTag() {
        return devTag;
    }

    public void setDevTag(String devTag) {
        this.devTag = devTag;
    }
}

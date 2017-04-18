package com.wulian.icam.model;

import java.io.Serializable;

/**
 * Created by hxc on 2016/12/26.
 * func:新猫眼设置界面查询结果保存在这个类中用于界面直接传递
 */

public class NewEagleInfo implements Serializable {
    private String language = "-1";//语言类型(1 - 中文 ， 2 - English)
    private String PIRSwitch = "-1";//PIR开关(0 - close , 1 - open)
    private String HoverDetectTime = "-1";//逗留检测时间(秒)
    private String PIRDetectLevel = "-1";//PIR灵敏度(0-高 ， 1-低)
    private String HoverProcMode = "-1";//联动类型(0-抓拍 ， 1-录像)
    private String HoverRecTime = "-1";//录像时间
    private String HoverSnapshotCount = "-1";//抓拍数量
    private String HoverSnapshotInterval = "-1";//抓拍间隔
    private String contrast = "-1";//对比度
    private String brightness = "-1";//亮度
    private String DayNightMode = "-1";//黑白模式(0-自动 ,1-白天 ,2-夜晚)

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getPIRSwitch() {
        return PIRSwitch;
    }

    public void setPIRSwitch(String PIRSwitch) {
        this.PIRSwitch = PIRSwitch;
    }

    public String getHoverDetectTime() {
        return HoverDetectTime;
    }

    public void setHoverDetectTime(String hoverDetectTime) {
        HoverDetectTime = hoverDetectTime;
    }

    public String getPIRDetectLevel() {
        return PIRDetectLevel;
    }

    public void setPIRDetectLevel(String PIRDetectLevel) {
        this.PIRDetectLevel = PIRDetectLevel;
    }

    public String getHoverProcMode() {
        return HoverProcMode;
    }

    public void setHoverProcMode(String hoverProcMode) {
        HoverProcMode = hoverProcMode;
    }

    public String getHoverRecTime() {
        return HoverRecTime;
    }

    public void setHoverRecTime(String hoverRecTime) {
        HoverRecTime = hoverRecTime;
    }

    public String getHoverSnapshotCount() {
        return HoverSnapshotCount;
    }

    public void setHoverSnapshotCount(String hoverSnapshotCount) {
        HoverSnapshotCount = hoverSnapshotCount;
    }

    public String getHoverSnapshotInterval() {
        return HoverSnapshotInterval;
    }

    public void setHoverSnapshotInterval(String hoverSnapshotInterval) {
        HoverSnapshotInterval = hoverSnapshotInterval;
    }

    public String getContrast() {
        return contrast;
    }

    public void setContrast(String contrast) {
        this.contrast = contrast;
    }

    public String getBrightness() {
        return brightness;
    }

    public void setBrightness(String brightness) {
        this.brightness = brightness;
    }

    public String getDayNightMode() {
        return DayNightMode;
    }

    public void setDayNightMode(String dayNightMode) {
        DayNightMode = dayNightMode;
    }


}

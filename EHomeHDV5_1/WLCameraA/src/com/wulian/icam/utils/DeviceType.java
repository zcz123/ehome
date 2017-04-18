/**
 * Project Name:  iCam
 * File Name:     DeviceType.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2015年6月29日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.utils;

import com.wulian.icam.R;

/**
 * @ClassName: DeviceType
 * @Function: 设备类型
 * @Date: 2015年6月29日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public enum DeviceType {
	NONE("00",R.string.common_icam_type_none,""),//不支持设备
	INDOOR("01", R.string.common_icam_type_01, "PTZ"), // 室内摄像头
	OUTDOOR("02", R.string.common_icam_type_02, "WIRED,VOICE"), // 户外摄像头
	SIMPLE("03", R.string.common_icam_type_03, "VOICE,ZIGBEE"), // 随便看摄像头
	INDOOR2("04", R.string.common_icam_type_04, "PTZ,VOICE,ZIGBEE"),// 02型室内摄像头
	SIMPLE_N("05", R.string.common_icam_type_05, "VOICE,ZIGBEE"),// 随便看摄像头夜视版
    DESKTOP_C("06",R.string.common_icam_type_05, "VOICE,ZIGBEE"),
    NewEagle("08",R.string.common_icam_type_05, "VOICE,ZIGBEE");
	String deviceType;// 设备类型
	int deviceNameResId;// 设备名
	String deviceFunction;// 设备功能

	private DeviceType(String deviceType, int deviceNameResId, String deviceFunction) {
		this.deviceType = deviceType;
		this.deviceNameResId = deviceNameResId;
		this.deviceFunction = deviceFunction;
	}

	public static DeviceType getDevivceTypeByID(String deviceType) {
		for (DeviceType item : DeviceType.values()) {
			if (item.deviceType.equalsIgnoreCase(deviceType)) {
				return item;
			}
		}
		return NONE;
	}

	public static DeviceType getDevivceTypeByDeviceID(String deviceId) {
		String type = "";
		if (deviceId != null) {
			int deviceLength = deviceId.length();
			if (deviceLength == 20) {
				type = deviceId.substring(4, 6);
			} else if (deviceLength == 16) {
				type = deviceId.substring(0, 2);
			}
		}
		for (DeviceType item : DeviceType.values()) {
			if (item.deviceType.equalsIgnoreCase(type)) {
				return item;
			}
		}
		return NONE;
	}

	public String getDeviceType() {
		return deviceType;
	}

	// 返回的是设备名的资源ID
	public int getDeviceNameResId() {
		return deviceNameResId;
	}

	/****************************/
	// PTZ :云台控制
	// WIRED :有线绑定
	// VOICE:双向语音
	// ZIGBEE:支持ZigBee
	/*************************/
	public String getDeviceFunction() {
		return deviceFunction;
	}

}

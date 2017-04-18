/**
 * Project Name:  iCam
 * File Name:     Media.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2015年5月29日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.model;

/**
 * @ClassName: Media
 * @Function: 媒体模型：图片、视频、语音等
 * @Date: 2015年5月29日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class MediaItem {
	public static final String TYPE_VIDEO = "video";
	public static final String TYPE_IMAGE = "image";
	public static final String TYPE_AUDIO = "audio";
	public static final String TYPE_HEAD = "head";
	private String meidaType;
	private String createTime;
	private Device relatedDevice;
	public String getMeidaType() {
		return meidaType;
	}
	public void setMeidaType(String meidaType) {
		this.meidaType = meidaType;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public Device getRelatedDevice() {
		return relatedDevice;
	}
	public void setRelatedDevice(Device relatedDevice) {
		this.relatedDevice = relatedDevice;
	}
	

}

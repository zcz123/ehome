/**
 * Project Name:  iCam
 * File Name:     VideoTimePeriod.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2016年2月5日
 * Copyright (c)  2016, wulian All Rights Reserved.
 */

package com.wulian.icam.model;

/**
 * @ClassName: VideoTimePeriod
 * @Function: TODO
 * @Date: 2016年2月5日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class VideoTimePeriod {
	private long timeStamp;
	private long endTimeStamp;
	private String fileName;

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public long getEndTimeStamp() {
		return endTimeStamp;
	}

	public void setEndTimeStamp(long endTimeStamp) {
		this.endTimeStamp = endTimeStamp;
	}

}

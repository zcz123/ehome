/**
 * Project Name:  iCam
 * File Name:     AlbumEntity.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2015年3月27日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.model;

import java.io.Serializable;

/**
 * @ClassName: AlbumEntity
 * @Function: 相册实体类
 * @Date: 2015年3月27日
 * @author: yuanjs
 * @email: yuanjsh@wuliangroup.cn
 */
public class AlbumEntity implements Serializable {
	/**
	 * serialVersionUID 作用:TODO
	 */
	private static final long serialVersionUID = -1705861550917663888L;
	/**
	 * 文件夹创建时间
	 */
	private String time;
	/**
	 * 文件夹名称
	 */
	private String fileName;
	/**
	 * 文件夹路径
	 */
	private String path;
	/**
	 * 该文件夹下图片个数
	 */
	private int count;
	/**
	 * 文件夹下第一张图片路径
	 */
	private String firstImagePath;
	/**
	 * 文件夹所对应的设备名称
	 */
	private String deviceName;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
		int lastIndex = this.path.lastIndexOf("/");
		this.fileName = this.path.substring(lastIndex + 1);
	}

	public String getFileName() {
		return fileName;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public String getFirstImagePath() {
		return firstImagePath;
	}

	public void setFirstImagePath(String firstImagePath) {
		this.firstImagePath = firstImagePath;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

}

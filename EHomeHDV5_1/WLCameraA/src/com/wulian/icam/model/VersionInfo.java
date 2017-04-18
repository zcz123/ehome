/**
 * Project Name:  iCam
 * File Name:     VersionInfo.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2014年11月5日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.model;

/**
 * @ClassName: VersionInfo
 * @Function: 版本细腻
 * @Date: 2014年11月5日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class VersionInfo {
	private String version_name;
	private int version_code;
	private String md5;
	private String download_url;
	private int size;
	private String desc;
	private String create_at;
	private String important;



	public String getVersion_name() {
		return version_name;
	}

	public void setVersion_name(String version_name) {
		this.version_name = version_name;
	}

	public int getVersion_code() {
		return version_code;
	}

	public void setVersion_code(int version_code) {
		this.version_code = version_code;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getDownload_url() {
		return download_url;
	}

	public void setDownload_url(String download_url) {
		this.download_url = download_url;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCreate_at() {
		return create_at;
	}

	public void setCreate_at(String create_at) {
		this.create_at = create_at;
	}

	public String getImportant() {
		return important;
	}

	public void setImportant(String important) {
		this.important = important;
	}

}

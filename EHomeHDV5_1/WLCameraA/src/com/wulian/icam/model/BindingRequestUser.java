/**
 * Project Name:  iCam
 * File Name:     BindingRequestUser.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2014年12月17日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.model;

/**
 * @ClassName: BindingRequestUser
 * @Function: 授权请求
 * @Date: 2014年12月17日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class BindingRequestUser {
	private String username;
	private String phone;
	private String email;
	private String desc;
	private String device_id;
	private long timestamp;

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPhone() {
		return phone;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsername() {
		return username;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getDevice_id() {
		return device_id;
	}
}

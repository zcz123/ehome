/**
 * Project Name:  iCam
 * File Name:     OauthDetail.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2015年7月9日
 * Copyright (c)  2015, wulian All Rights Reserved.
*/

package com.wulian.icam.model;
/**
 * @ClassName: OauthDetail
 * @Function:  已授权用户具体相关数据
 * @Date:      2015年7月9日
 * @author:    yuanjs
 * @email:     yuanjsh@wuliangroup.cn
 */
public class OauthUserDetail {
	private Long lasttime;//最近查看摄像机时间
	private int count;//用户查看摄像机次数
	private String username ;
	private String email ;
	private String phone ;
	private long timestamp;// updated_at
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Long getLasttime() {
		return lasttime;
	}
	public void setLasttime(Long lasttime) {
		this.lasttime = lasttime;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
}


/**
 * Project Name:  iCam
 * File Name:     BindingNoticeEntity.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2015年4月3日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.model;

import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @ClassName: BindingNoticeEntity
 * @Function: 未读信息
 * @Date: 2015年4月3日
 * @author: yuanjs
 * @email: yuanjsh@wuliangroup.cn
 */
public class OauthMessage implements Serializable{
	/**
	 * serialVersionUID 作用:TODO
	 */
	private static final long serialVersionUID = -7666963955204326608L;
	private long id; // 未读信息id
	private String device_id; //设备id
	/**
	 * 未读信息类型 1:request,2:add,3:response_accept,4:response_decline,5:delete,0:unknown
	 */
	private int type; 
	private String userName; // 信息用户名称
	private String phone;
	private String email;
	private long time; //信息创建时间
	private String YMDtime,HSMtime;
	private boolean isDelete;
	private String fromNick;// 来自
	private String desc; //请求描述
	private boolean isUnread;// 是否已查看
	private boolean isAccept;// 是否已同意
	private boolean isHandle;//是否已处理
	// private String imgUrl;// 后续实现，目前是默认图片
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
		try {
			Date date = new Date(time * 1000);
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd",
					Locale.ENGLISH);
			SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
			YMDtime = sdf1.format(date);
			HSMtime = sdf2.format(date);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	public String getTimeYMD() {
		return YMDtime;
	}
	public String getTimeHMS() {
		return HSMtime;
	}
	public boolean isDelete() {
		return isDelete;
	}
	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	public String getFromNick() {
		return fromNick;
	}
	public void setFromNick(String fromNick) {
		this.fromNick = fromNick;
	}
	public boolean getIsUnread() {
		return isUnread;
	}
	public void setIsUnread(boolean isUnread) {
		this.isUnread = isUnread;
	}
	public boolean isAccept() {
		return isAccept;
	}
	public void setAccept(boolean isAccept) {
		this.isAccept = isAccept;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public boolean isHandle() {
		return isHandle;
	}
	public void setHandle(boolean isHandle) {
		this.isHandle = isHandle;
	}
}

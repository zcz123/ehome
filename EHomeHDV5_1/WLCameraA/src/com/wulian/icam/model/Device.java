/**
 * Project Name:  iCam
 * File Name:     Device.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2014年10月21日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.model;

import java.io.Serializable;

import android.text.TextUtils;

import com.wulian.icam.common.APPConfig;

/**
 * @ClassName: Device
 * @Function: 设备bean
 * @Date: 2014年10月21日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class Device implements Serializable {
	private static final long serialVersionUID = 1L;
	private String device_id;
	private int is_online;
	private String device_nick;
	private String device_desc;
	private String sip_domain;
	private String sip_username;// 暂时等同device_id
	private String ip,owner;
	private int video_port, authcount, protect;
	private long updated_at;
	private boolean is_BindDevice = false;// 是否是主绑定设备
	private boolean is_lan = false;// 是否是局域网设备
	private boolean is_history_video = false;// 是否支持视频回看

	private String tutkPwd;//add syf  tutk
	private String tutkUid;
	
	public void setTutkPwd(String tutkPwd) {
		this.tutkPwd = tutkPwd;
	}
	public String getTutkPwd() {
		return tutkPwd;
	}
	public void setTutkUid(String tutkUid) {
		this.tutkUid = tutkUid;
	}
	public String getTutkUid() {
		return tutkUid;
	}
	public boolean getIs_BindDevice() {
		return is_BindDevice;
	}

	public void setIs_BindDevice(boolean is_BindDevice) {
		this.is_BindDevice = is_BindDevice;
	}

	public boolean getIs_lan() {
		return is_lan;
	}

	public void setIs_lan(boolean is_lan) {
		this.is_lan = is_lan;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public int getIs_online() {
		return is_online;
	}

	public void setIs_online(int is_online) {
		this.is_online = is_online;
	}

	public String getDevice_nick() {
		return device_nick;
	}

	public void setDevice_nick(String device_nick) {
		this.device_nick = device_nick;
	}

	public String getDevice_desc() {
		return device_desc;
	}

	public void setDevice_desc(String device_desc) {
		this.device_desc = device_desc;
	}

	public String getSip_domain() {
		if (TextUtils.isEmpty(sip_domain)) {
			return APPConfig.SERVERNAME;
		}
		return sip_domain;
	}

	public void setSip_domain(String sip_domain) {
		this.sip_domain = sip_domain;
	}

	public String getSip_username() {
		return sip_username;
	}

	public void setSip_username(String sip_username) {
		this.sip_username = sip_username;
	}

	public long getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(long updated_at) {
		this.updated_at = updated_at;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getVideo_port() {
		return video_port;
	}

	public void setVideo_port(int video_port) {
		this.video_port = video_port;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean getIs_history_video() {
		return is_history_video;
	}

	public void setIs_history_video(boolean is_history_video) {
		this.is_history_video = is_history_video;
	}

	@Override
	public String toString() {
		return "Device [device_id=" + device_id + ", is_online=" + is_online
				+ ", device_nick=" + device_nick + ", device_desc="
				+ device_desc + ", sip_domain=" + sip_domain
				+ ", sip_username=" + sip_username + ", ip=" + ip
				+ ", video_port=" + video_port + ", updated_at=" + updated_at
				+ ", is_BindDevice=" + is_BindDevice + ", is_lan=" + is_lan
				+ "]";
	}

	public int getAuthcount() {
		return authcount;
	}

	public void setAuthcount(int authcount) {
		this.authcount = authcount;
	}

	public int getProtect() {
		return protect;
	}

	public void setProtect(int protect) {
		this.protect = protect;
	}

	/**
	 * owner.
	 *
	 * @return  the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * owner
	 * @param   owner    the owner to set
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}
}

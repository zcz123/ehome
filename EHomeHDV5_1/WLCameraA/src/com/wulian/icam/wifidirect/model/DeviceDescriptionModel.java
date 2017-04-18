/**
 * Project Name:  ErWeiMaScanDemo
 * File Name:     DeviceDescriptionModel.java
 * Package Name:  com.android.erweima.demo.model
 * @Date:         2015年5月13日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.wifidirect.model;

import java.io.Serializable;

/**
 * @ClassName: DeviceDescriptionModel
 * @Function: TODO
 * @Date: 2015年5月13日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DeviceDescriptionModel implements Serializable {
	/**
	 * serialVersionUID 作用:TODO
	 */
	private static final long serialVersionUID = -5035381162385591962L;
	private String remoteIP;
	private String local_mac;
	private String model;
	private String serialnum;
	private String version;
	private String hardware;
	private String sipaccount;
	private int video_port;

	public String getLocal_mac() {
		return local_mac;
	}

	public void setLocal_mac(String local_mac) {
		this.local_mac = local_mac;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getSerialnum() {
		return serialnum;
	}

	public void setSerialnum(String serialnum) {
		this.serialnum = serialnum;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getHardware() {
		return hardware;
	}

	public void setHardware(String hardware) {
		this.hardware = hardware;
	}

	public String getSipaccount() {
		return sipaccount;
	}

	public void setSipaccount(String sipaccount) {
		this.sipaccount = sipaccount;
	}

	public int getVideo_port() {
		return video_port;
	}

	public void setVideo_port(int video_port) {
		this.video_port = video_port;
	}

	public String getRemoteIP() {
		return remoteIP;
	}

	public void setRemoteIP(String remoteIP) {
		this.remoteIP = remoteIP;
	}

	public void copyData(DeviceDescriptionModel data) {
		if (data != null) {
			setHardware(data.getHardware());
			setLocal_mac(data.getLocal_mac());
			setModel(data.getModel());
			setRemoteIP(data.getRemoteIP());
			setSerialnum(data.getSerialnum());
			setSipaccount(data.getSipaccount());
			setVersion(data.getVersion());
			setVideo_port(data.getVideo_port());
		}
	}
}

/**
 * Project Name:  iCam
 * File Name:     DeviceDetailMes.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2015年10月19日
 * Copyright (c)  2015, wulian All Rights Reserved.
*/

package com.wulian.icam.model;
/**
 * @ClassName: DeviceDetailMes
 * @Function:  设备详细信息bean
 * @Date:      2015年10月19日
 * @author:    yuanjs
 * @email:     jiansheng.yuan@wuliangroup.com.cn
 */
public class DeviceDetailMsg {
	private String version;
	private String wifi_ssid;
	private String wifi_signal;
	private String wifi_ip;
	private String wifi_mac;
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getWifi_ssid() {
		return wifi_ssid;
	}
	public void setWifi_ssid(String wifi_ssid) {
		this.wifi_ssid = wifi_ssid;
	}
	public String getWifi_signal() {
		return wifi_signal;
	}
	public void setWifi_signal(String wifi_signal) {
		this.wifi_signal = wifi_signal;
	}
	public String getWifi_ip() {
		return wifi_ip;
	}
	public void setWifi_ip(String wifi_ip) {
		this.wifi_ip = wifi_ip;
	}
	public String getWifi_mac() {
		return wifi_mac;
	}
	public void setWifi_mac(String wifi_mac) {
		this.wifi_mac = wifi_mac;
	}
}


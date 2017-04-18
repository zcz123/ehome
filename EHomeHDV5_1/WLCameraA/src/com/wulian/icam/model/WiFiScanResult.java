package com.wulian.icam.model;

import java.io.Serializable;

/**
 * WiFi 扫描结果
 * 
 * @author pml
 * 
 */
public class WiFiScanResult implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8390409083260173421L;
	private String ssid;// wifi名称
	private int signalLevel;// wifi信号强度;
	private String mac_address;// mac 地址
	private int security;// 加密方式
	private int netId;//配置好的指定ID的网络

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	public String getSsid() {
		return ssid;
	}

	public void setMac_address(String mac_address) {
		this.mac_address = mac_address;
	}

	public String getMac_address() {
		return mac_address;
	}

	public void setSignalLevel(int signalLevel) {
		this.signalLevel = signalLevel;
	}

	public int getSignalLevel() {
		return signalLevel;
	}

	public int getSecurity() {
		return security;
	}

	public void setSecurity(int security) {
		this.security = security;
	}
	public void setNetId(int netId) {
		this.netId = netId;
	}
	public int getNetId() {
		return netId;
	}
}

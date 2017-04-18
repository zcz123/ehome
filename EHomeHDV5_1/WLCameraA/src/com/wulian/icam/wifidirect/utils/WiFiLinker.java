/**
 * Project Name:  iCam
 * File Name:     WiFiLinker.java
 * Package Name:  com.wulian.icam.wifidirect.utils
 * @Date:         2015年6月15日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.wifidirect.utils;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * @ClassName: WiFiLinker
 * @Function: Wi-Fi连接器
 * @Date: 2015年6月15日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class WiFiLinker {
	private Context mContext;
	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;

	public final void WifiInit(Context context) {
		this.mContext = context;
		this.mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// this.mWifiInfo = this.mWifiManager.getConnectionInfo();
	}

	public final int WifiStatus() {
		if (mWifiManager != null) {
			return this.mWifiManager.getWifiState();
		} else {
			return WifiManager.WIFI_STATE_UNKNOWN;
		}
	}
 
	public boolean isWiFiEnable() {
		if (mWifiManager != null) {
			return mWifiManager.isWifiEnabled();
		}
		return false;
	}
	
	public WifiInfo getWifiInfo() {
		if (mWifiManager != null) {
			this.mWifiInfo = this.mWifiManager.getConnectionInfo();
		} else {
			this.mWifiInfo = null;
		}
		return this.mWifiInfo;
	}

	public final List<ScanResult> WifiGetScanResults() {
		return this.mWifiManager.getScanResults();
	}
	
	public final List<WifiConfiguration> wifiConfigList() {
		return this.mWifiManager.getConfiguredNetworks();//.get(0).allowedKeyManagement.
	}
	// "Wulian_E17629 形式"
	public final String getConnectedWifiSSID() {
		WifiInfo info = getWifiInfo();
		if (info != null) {
			return info.getSSID();
		} else {
			return null;
		}
	}

	// Wulian_E17629 形式
	public final String getSxConnectedWifiSSID() {
		WifiInfo info = getWifiInfo();
		if (info != null) {
			return info.getSSID().replace("\"", "");
		} else {
			return null;
		}
	}

	public final int getWifiIpInt() {
		if(getWifiInfo()!=null) {
			return getWifiInfo().getIpAddress();
		}else {
			return 0;
		}
	}

}

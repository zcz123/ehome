/**
 * Project Name:  iCam
 * File Name:     ConfigWiFiInfoModel.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2015年7月26日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.model;

import com.wulian.icam.common.iCamConstants;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ClassName: ConfigWiFiInfoModel
 * @Function: TODO
 * @Date: 2015年7月26日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class ConfigWiFiInfoModel implements Parcelable {
	private String deviceId;
	private String seed;
	private String wifiName;
	private String wifiPwd;
	private String bssid;
	private String security;
	private int isAddDevice;
	// private int retry;
	// private int needReset;
	private int smartConnect;// 直连方式
	private int qrConnect;// 二维码方式
	private int wiredConnect;// 有线方式
	private int apConnect;// 软AP方式
	private int configWiFiType;// 参看iCamConstants

	@Override
	public int describeContents() {

		// TODO Auto-generated method stub
		return 0;
	}

	public ConfigWiFiInfoModel() {
		deviceId = "";
		seed = "";
		wifiName = "";
		wifiPwd = "";
		bssid = "";
		security = "";
		isAddDevice = 0;
		// retry = 0;
		// needReset = 0;
		smartConnect = 0;
		qrConnect = 0;
		wiredConnect = 0;
		apConnect = 0;
		configWiFiType = iCamConstants.CONFIG_DIRECT_WIFI_SETTING;
	}

	public static final Parcelable.Creator<ConfigWiFiInfoModel> CREATOR = new Creator<ConfigWiFiInfoModel>() {

		@Override
		public ConfigWiFiInfoModel createFromParcel(Parcel source) {
			ConfigWiFiInfoModel data = new ConfigWiFiInfoModel();
			data.deviceId = source.readString();
			data.seed = source.readString();
			data.wifiName = source.readString();
			data.wifiPwd = source.readString();
			data.bssid = source.readString();
			data.security = source.readString();
			data.isAddDevice = source.readInt();
			// data.retry = source.readInt();
			// data.needReset = source.readInt();
			data.smartConnect = source.readInt();
			data.qrConnect = source.readInt();
			data.wiredConnect = source.readInt();
			data.apConnect = source.readInt();
			data.configWiFiType = source.readInt();
			return data;
		}

		@Override
		public ConfigWiFiInfoModel[] newArray(int size) {
			return new ConfigWiFiInfoModel[size];
		}

	};

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(deviceId);
		dest.writeString(seed);
		dest.writeString(wifiName);
		dest.writeString(wifiPwd);
		dest.writeString(bssid);
		dest.writeString(security);
		dest.writeInt(isAddDevice);
		// dest.writeInt(retry);
		// dest.writeInt(needReset);
		dest.writeInt(smartConnect);
		dest.writeInt(qrConnect);
		dest.writeInt(wiredConnect);
		dest.writeInt(apConnect);
		dest.writeInt(configWiFiType);
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getSeed() {
		return seed;
	}

	public void setSeed(String seed) {
		this.seed = seed;
	}

	public String getWifiName() {
		return wifiName;
	}

	public void setWifiName(String wifiName) {
		this.wifiName = wifiName;
	}

	public String getWifiPwd() {
		return wifiPwd;
	}

	public void setWifiPwd(String wifiPwd) {
		this.wifiPwd = wifiPwd;
	}

	public String getBssid() {
		return bssid;
	}

	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	public String getSecurity() {
		return security;
	}

	public void setSecurity(String security) {
		this.security = security;
	}

	public int getConfigWiFiType() {
		return configWiFiType;
	}

	public void setConfigWiFiType(int configWiFiType) {
		this.configWiFiType = configWiFiType;
	}

	public boolean isAddDevice() {
		return isAddDevice == 1 ? true : false;
	}

	public void setAddDevice(boolean isAddDevice) {// 是新加入设备则设为true，如果是配置wifi，设为false
		this.isAddDevice = isAddDevice ? 1 : 0;
	}

	// public int getRetry() {
	// return retry;
	// }
	//
	// public void setRetry(int retry) {
	// this.retry = retry;
	// }

	// public boolean needReset() {
	// return needReset == 1 ? true : false;
	// }
	//
	// public void setNeedReset(boolean needReset) {
	// this.needReset = needReset ? 1 : 0;
	// }

	public void setSmartConnect(boolean isSmartConnect) {
		this.smartConnect = isSmartConnect ? 1 : 0;
	}

	public boolean getSmartConnect() {
		return smartConnect == 1 ? true : false;
	}

	public void setQrConnect(boolean isQrConnect) {
		this.qrConnect = isQrConnect ? 1 : 0;
	}

	public boolean isQrConnect() {
		return qrConnect == 1 ? true : false;
	}

	public void setWiredConnect(boolean isWiredConnect) {
		this.wiredConnect = isWiredConnect ? 1 : 0;
	}

	public boolean getWiredConnect() {
		return wiredConnect == 1 ? true : false;
	}

	public void setApConnect(boolean isApConnect) {
		this.apConnect = isApConnect ? 1 : 0;
	}

	public boolean isApConnect() {
		return apConnect == 1 ? true : false;
	}

}

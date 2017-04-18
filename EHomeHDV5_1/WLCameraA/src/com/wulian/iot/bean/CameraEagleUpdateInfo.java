package com.wulian.iot.bean;

import java.io.Serializable;

/**
 * 
 * @author Administrator
<versionCode>1</versionCode>
<versionName>v1.1.0</versionName>
<remindTimes>1</remindTimes>
<devmodel>MY001</devmodel>
<fwmodel>1</fwmodel>
<versionTxts>test</versionTxts>
<url>
http://7xs5cf.dl1.z0.glb.clouddn.com/UPGRADE_fw_v1.1.0.bin
</url>
 */
public class CameraEagleUpdateInfo extends BaseCameraInfo{

	private static final long serialVersionUID = 1L;
	private int versionCode;
	private String versionName;
	private int remindTimes;
	private String devmodel;
	private String fwmodel;
	private String versionTxts;
	private String url;
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	public int getRemindTimes() {
		return remindTimes;
	}
	public void setRemindTimes(int remindTimes) {
		this.remindTimes = remindTimes;
	}
	public String getDevmodel() {
		return devmodel;
	}
	public void setDevmodel(String devmodel) {
		this.devmodel = devmodel;
	}
	public String getFwmodel() {
		return fwmodel;
	}
	public void setFwmodel(String fwmodel) {
		this.fwmodel = fwmodel;
	}
	public String getVersionTxts() {
		return versionTxts;
	}
	public void setVersionTxts(String versionTxts) {
		this.versionTxts = versionTxts;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}

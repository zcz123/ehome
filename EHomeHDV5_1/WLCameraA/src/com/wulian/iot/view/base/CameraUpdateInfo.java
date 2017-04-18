package com.wulian.iot.view.base;

//add by likai
public class CameraUpdateInfo {

	private int versionCode;
	private String versionName ;
	private int remindTimes ;
	private String versionTxts ;
	private String url ;
	
	
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

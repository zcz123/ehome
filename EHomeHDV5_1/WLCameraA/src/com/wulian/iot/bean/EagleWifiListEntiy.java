package com.wulian.iot.bean;

public class EagleWifiListEntiy extends BaseCameraInfo{

	/**
	 * char mode;                                    // refer to ENUM_AP_MODE
        char enctype;                                // refer to ENUM_AP_ENCTYPE
        char signal;                                   // signal intensity 0--100%
        char status;
	 */
	private String wifiname;
	private byte mode;
	private byte enctype;
	private byte signal;
	private byte status;
	public String getWifiname() {
		return wifiname;
	}
	public void setWifiname(String wifiname) {
		this.wifiname = wifiname;
	}
	public byte getMode() {
		return mode;
	}
	public void setMode(byte mode) {
		this.mode = mode;
	}
	public byte getEnctype() {
		return enctype;
	}
	public void setEnctype(byte enctype) {
		this.enctype = enctype;
	}
	public byte getSignal() {
		return signal;
	}
	public void setSignal(byte signal) {
		this.signal = signal;
	}
	public byte getStatus() {
		return status;
	}
	public void setStatus(byte status) {
		this.status = status;
	}
	
	
	public EagleWifiListEntiy() {
		super();
	}
	public EagleWifiListEntiy(String wifiname, byte mode, byte enctype,
			byte signal, byte status) {
		super();
		this.wifiname = wifiname;
		this.mode = mode;
		this.enctype = enctype;
		this.signal = signal;
		this.status = status;
	}
	@Override
	public String toString() {
		return "EagleWifiListEntiy [wifiname=" + wifiname + ", mode=" + mode
				+ ", enctype=" + enctype + ", signal=" + signal + ", status="
				+ status + "]";
	}
	
}

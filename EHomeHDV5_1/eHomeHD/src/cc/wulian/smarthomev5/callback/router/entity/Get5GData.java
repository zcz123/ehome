package cc.wulian.smarthomev5.callback.router.entity;

import java.util.List;

public class Get5GData {
	private String code;
	private String msg;
	private List<GetRadioEntity> radio1;
	private List<GetWifi_ifaceEntity> wifi_iface;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<GetRadioEntity> getRadio1() {
		return radio1;
	}

	public void setRadio1(List<GetRadioEntity> radio1) {
		this.radio1 = radio1;
	}

	public List<GetWifi_ifaceEntity> getWifi_iface() {
		return wifi_iface;
	}

	public void setWifi_iface(List<GetWifi_ifaceEntity> wifi_iface) {
		this.wifi_iface = wifi_iface;
	}

}

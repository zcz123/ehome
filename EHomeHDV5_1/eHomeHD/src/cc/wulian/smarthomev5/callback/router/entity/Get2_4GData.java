package cc.wulian.smarthomev5.callback.router.entity;

import java.util.List;

public class Get2_4GData {
	private int code;
	private String msg;
	private List<GetRadioEntity> radio0;
	private List<GetWifi_ifaceEntity> wifi_iface;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<GetRadioEntity> getRadio0() {
		return radio0;
	}

	public void setRadio0(List<GetRadioEntity> radio0) {
		this.radio0 = radio0;
	}

	public List<GetWifi_ifaceEntity> getWifi_iface() {
		return wifi_iface;
	}

	public void setWifi_iface(List<GetWifi_ifaceEntity> wifi_iface) {
		this.wifi_iface = wifi_iface;
	}

}

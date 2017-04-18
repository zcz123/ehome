package cc.wulian.smarthomev5.event;

import java.util.List;

import cc.wulian.smarthomev5.callback.router.entity.GetRadioEntity;
import cc.wulian.smarthomev5.callback.router.entity.GetWifi_ifaceEntity;

public class RouterWifiSettingEvent {
	public static final String ACTION_REFRESH = "ACTION_REFRESH";
	public static final String TYPE_2_4G_WIFI = "TYPE_2_4G_WIFI";
	public static final String TYPE_5G_WIFI = "TYPE_5G_WIFI";
	private String action;
	private String type;
	private List<GetWifi_ifaceEntity> Wifi_ifaceList;
	private List<GetRadioEntity> radioList;

	public RouterWifiSettingEvent(String action, String type,
			List<GetWifi_ifaceEntity> wifi_ifaceList,
			List<GetRadioEntity> radioList) {
		super();
		this.type = type;
		this.action = action;
		this.Wifi_ifaceList = wifi_ifaceList;
		this.radioList = radioList;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<GetWifi_ifaceEntity> getWifi_ifaceList() {
		return Wifi_ifaceList;
	}

	public void setWifi_ifaceList(List<GetWifi_ifaceEntity> wifi_ifaceList) {
		Wifi_ifaceList = wifi_ifaceList;
	}

	public List<GetRadioEntity> getRadioList() {
		return radioList;
	}

	public void setRadioList(List<GetRadioEntity> radioList) {
		this.radioList = radioList;
	}

}

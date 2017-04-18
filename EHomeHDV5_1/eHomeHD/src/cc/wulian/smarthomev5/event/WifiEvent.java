package cc.wulian.smarthomev5.event;

import java.util.List;

import cc.wulian.smarthomev5.entity.WifiEntity;

public class WifiEvent {
	public String action;
	public List<WifiEntity> entities;
	public WifiEntity wifiEntity;
	public boolean isFromMe;

	public WifiEvent(String action, List<WifiEntity> entities,
			WifiEntity wifiEntity, boolean isFromMe) {
		this.action = action;
		this.isFromMe = isFromMe;
		this.entities = entities;
		this.wifiEntity = wifiEntity;
	}

	public String getAction() {
		return action;
	}

	public List<WifiEntity> getEntities() {
		return entities;
	}

	public boolean isFromMe() {
		return isFromMe;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setEntities(List<WifiEntity> entities) {
		this.entities = entities;
	}

	public void setFromMe(boolean isFromMe) {
		this.isFromMe = isFromMe;
	}
}

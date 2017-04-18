package cc.wulian.smarthomev5.event;

import java.util.List;

import cc.wulian.smarthomev5.callback.router.entity.DeviceInfo;

public class RouterDevcieInfoEvent {
	public static final String ACTION_REFRESH = "ACTION_REFRESH";
	private List<DeviceInfo> list;
	private String action;

	public RouterDevcieInfoEvent(String action, List<DeviceInfo> list) {
		super();
		this.action = action;
		this.list = list;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<DeviceInfo> getList() {
		return list;
	}

	public void setList(List<DeviceInfo> list) {
		this.list = list;
	}

}

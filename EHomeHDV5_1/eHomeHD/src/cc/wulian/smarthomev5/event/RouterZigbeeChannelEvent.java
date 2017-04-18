package cc.wulian.smarthomev5.event;

public class RouterZigbeeChannelEvent {
	public static final String ACTION_REFRESH = "ACTION_REFRESH";
	public String action;
	public int zigbeeChannel;

	public RouterZigbeeChannelEvent(String action, int zigbeeChannel) {
		super();
		this.action = action;
		this.zigbeeChannel = zigbeeChannel;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public int getZigbeeChannel() {
		return zigbeeChannel;
	}

	public void setZigbeeChannel(int zigbeeChannel) {
		this.zigbeeChannel = zigbeeChannel;
	}

}

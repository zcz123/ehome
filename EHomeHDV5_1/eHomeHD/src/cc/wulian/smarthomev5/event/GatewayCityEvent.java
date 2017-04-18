package cc.wulian.smarthomev5.event;

public class GatewayCityEvent {

	private String mode;
	private String gwID;
	private String gwCityID;

	public GatewayCityEvent(String mode, String gwID, String gwCityID) {
		super();
		this.mode = mode;
		this.gwID = gwID;
		this.gwCityID = gwCityID;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getGwCityID() {
		return gwCityID;
	}

	public void setGwCityID(String gwCityID) {
		this.gwCityID = gwCityID;
	}
}

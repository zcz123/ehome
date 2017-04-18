package cc.wulian.smarthomev5.event;

public class GatewaInfoEvent {

	private String mode;
	private String gwID;
	private String gwName;
	private String gwChannel;
	

	public GatewaInfoEvent(String mode, String gwID, String gwName, String gwChannel) {
		super();
		this.mode = mode;
		this.gwID = gwID;
		this.gwName = gwName;
		this.gwChannel = gwChannel;
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

	public String getGwName() {
		return gwName;
	}

	public void setGwName(String gwName) {
		this.gwName = gwName;
	}
	public String getGwChannel(){
		return gwChannel;
	}
	public void setGwChannel(String gwChannel){
		this.gwChannel = gwChannel;
	}

}

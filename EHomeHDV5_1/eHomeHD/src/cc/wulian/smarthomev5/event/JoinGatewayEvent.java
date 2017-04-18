package cc.wulian.smarthomev5.event;

public class JoinGatewayEvent {
	public String mGwID;
	public String mDevID;
	public String mData;

	public JoinGatewayEvent(String gwID, String devID, String data) {
		this.mGwID = gwID;
		this.mDevID = devID;
		this.mData = data;
	}

}

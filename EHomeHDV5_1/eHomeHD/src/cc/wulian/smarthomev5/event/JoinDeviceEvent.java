package cc.wulian.smarthomev5.event;

public class JoinDeviceEvent {

	public String mGwID;
	public String mDevID;

	public JoinDeviceEvent(String gwID, String devID) {
		mGwID = gwID;
		mDevID = devID;
	}
}

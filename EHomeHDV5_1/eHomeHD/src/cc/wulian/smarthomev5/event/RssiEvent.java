package cc.wulian.smarthomev5.event;


/**
 * like add, update, delete device info may trigger this event, device up or
 * down do <b>not</b> call this
 */
public class RssiEvent {
	public String gwID;
	public String devID;
	public String data;
	public String uplink;

	public RssiEvent(String gwID,String devID,String data, String uplink) {
		this.gwID=gwID;
		this.devID=devID;
		this.data = data;
		this.uplink = uplink;
	}
}

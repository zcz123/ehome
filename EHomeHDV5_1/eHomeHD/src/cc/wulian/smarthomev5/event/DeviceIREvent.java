package cc.wulian.smarthomev5.event;

public class DeviceIREvent
{
	private static final String TAG = DeviceIREvent.class.getSimpleName();

	public final String action;
	public final String gwID;
	public final String devID;
	public final String irType;
	public final boolean isFromMe;

	public DeviceIREvent( String action, String gwID, String devID,String irType, boolean isFromMe )
	{
		this.action = action;
		this.gwID = gwID;
		this.devID = devID;
		this.irType = irType;
		this.isFromMe = isFromMe;
	}

	@Override
	public String toString() {
		return TAG + ":{" + "action:{" + action + "}" + ", isFromMe:{" + isFromMe + "}" + ", devID:{" + devID + "}" + "}";
	}
}

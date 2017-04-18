package cc.wulian.smarthomev5.event;

public class GatewayEvent
{
	private static final String TAG = GatewayEvent.class.getSimpleName();

	public static final String ACTION_CONNECTED = TAG + ":CONNECTED";
	public static final String ACTION_CONNECTING = TAG + ":CONNECTING";
	public static final String ACTION_DISCONNECTED = TAG + ":DISCONNECTED";
	public static final String ACTION_CHANGE_PWD = TAG + ":MODIFY_DATA";

	public final String action;
	public final int result;
	public final String gwID;

	public GatewayEvent( String action, String gwID, int result )
	{
		this.action = action;
		this.result = result;
		this.gwID = gwID;
	}
}

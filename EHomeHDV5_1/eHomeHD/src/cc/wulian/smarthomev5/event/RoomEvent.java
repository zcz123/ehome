package cc.wulian.smarthomev5.event;

public class RoomEvent
{
	private static final String TAG = RoomEvent.class.getSimpleName();

	public final String action;
	public final boolean isFromMe;
	public final String id;

	public RoomEvent( String action, boolean isFromMe, String id )
	{
		this.action = action;
		this.isFromMe = isFromMe;
		this.id = id;
	}

	@Override
	public String toString(){
		return TAG + ":{" + "action:{" + action + "}" + ", isFromMe:{" + isFromMe + "}" + ", id:{" + id + "}" + "}";
	}
}

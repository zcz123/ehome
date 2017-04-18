package cc.wulian.smarthomev5.event;

public class CameraEvent
{
	public final String gwID;
	public final String ID;

	public CameraEvent ( )
	{
		gwID = null;
		ID = null;
	}
	
	public CameraEvent( String gwID, String ID )
	{
		this.gwID = gwID;
		this.ID = ID;
	}
}

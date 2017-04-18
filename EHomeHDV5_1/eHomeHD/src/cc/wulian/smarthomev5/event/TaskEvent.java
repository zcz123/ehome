package cc.wulian.smarthomev5.event;

public class TaskEvent
{
	private static final String TAG = TaskEvent.class.getSimpleName();
	
	public  String action;
	public  boolean isFromMe;
	public  String gwID;
	public  String sceneID;
	public  String devID;
	public  String ep;

	public TaskEvent( String gwID,String action, boolean isFromMe, String sceneID, String devID, String ep )
	{
		this.gwID = gwID;
		this.action = action;
		this.isFromMe = isFromMe;
		this.sceneID = sceneID;
		this.devID = devID;
		this.ep = ep;
		this.gwID = gwID;
	}
	
	@Override
	public String toString(){
		return TAG + ":{" + "action:{" + action + "}" + ", isFromMe:{" + isFromMe + "}" + ", id:{" + sceneID + "}" + "}";
	}
}

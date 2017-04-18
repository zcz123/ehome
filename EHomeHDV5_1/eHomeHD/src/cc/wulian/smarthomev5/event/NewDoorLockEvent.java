package cc.wulian.smarthomev5.event;

import com.alibaba.fastjson.JSONObject;

public class NewDoorLockEvent
{
	public final String gwID;
	public final String devID;
	public final JSONObject data;
	public final String operType;

	public NewDoorLockEvent ( )
	{
		gwID = null;
		devID= null;
		data= null;
		operType=null;
	}
	
	public NewDoorLockEvent( String gwID ,String devID,JSONObject data,String operType)
	{
		this.gwID = gwID;
		this.devID = devID;
		this.data = data;
		this.operType = operType;
	}
}

package cc.wulian.smarthomev5.event;

public class MiniGatewayEvent
{
	public final String gwID;
	public final String cmdindex;
	public final String cmdtype;
	public final String data;
	public final String disable;

	public MiniGatewayEvent ( )
	{
		gwID = null;
		cmdindex= null;
		cmdtype= null;
		data= null;
		disable=null;
		
	}
	
	public MiniGatewayEvent( String gwID, String cmdindex ,String cmdtype,String data,String disable)
	{
		this.gwID = gwID;
		this.cmdindex = cmdindex;
		this.cmdtype = cmdtype;
		this.data = data;
		this.disable=disable;
	}
	public String getGwID() {
		return gwID;
	}

	public String getCmdindex() {
		return cmdindex;
	}

	public String getCmdtype() {
		return cmdtype;
	}

	public String getData() {
		return data;
	}
	
	public String getDisable() {
		return disable;
	}
}

package cc.wulian.smarthomev5.event;


public class AlarmEvent
{
	private String action;
	private String alarmStr;

	public AlarmEvent( String action, String alarmStr)
	{
		this.action = action;
		this.alarmStr = alarmStr;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getAlarmStr() {
		return alarmStr;
	}

	public void setAlarmStr(String alarmStr) {
		this.alarmStr = alarmStr;
	}
	
}

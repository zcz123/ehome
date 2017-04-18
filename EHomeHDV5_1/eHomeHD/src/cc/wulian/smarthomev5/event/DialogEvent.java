package cc.wulian.smarthomev5.event;

public class DialogEvent
{
	public final String actionKey;
	public final int resultCode;

	public DialogEvent( String actionKey, int resultCode )
	{
		this.actionKey = actionKey;
		this.resultCode = resultCode;
	}
}

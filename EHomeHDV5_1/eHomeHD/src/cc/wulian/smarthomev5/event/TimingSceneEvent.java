package cc.wulian.smarthomev5.event;


public class TimingSceneEvent
{
	private String action;
	public TimingSceneEvent() {
	}
	public TimingSceneEvent(String action) {
		this.action = action;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
	
}

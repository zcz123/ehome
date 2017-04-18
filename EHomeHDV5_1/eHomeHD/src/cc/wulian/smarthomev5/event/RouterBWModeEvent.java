package cc.wulian.smarthomev5.event;

public class RouterBWModeEvent {
	public static final String ACTION_REFRESH = "ACTION_REFRESH";
	public static final String CUR_MODEL_0 = "0";
	public static final String CUR_MODEL_1 = "1";
	public static final String CUR_MODEL_2 = "2";
	private String action;
	private String mode;

	public RouterBWModeEvent(String action, String mode) {
		super();
		this.action = action;
		this.mode = mode;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}

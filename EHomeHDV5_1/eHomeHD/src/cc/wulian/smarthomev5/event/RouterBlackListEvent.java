package cc.wulian.smarthomev5.event;

import java.util.List;

import cc.wulian.smarthomev5.callback.router.entity.BlackAndWhiteEntity;

public class RouterBlackListEvent {
	public static final String ACTION_REFRESH = "ACTION_REFRESH";
	private String action;
	private List<BlackAndWhiteEntity> list;

	public RouterBlackListEvent(String action, List<BlackAndWhiteEntity> list) {
		super();
		this.action = action;
		this.list = list;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public List<BlackAndWhiteEntity> getList() {
		return list;
	}

	public void setList(List<BlackAndWhiteEntity> list) {
		this.list = list;
	}

}

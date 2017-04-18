package cc.wulian.smarthomev5.event;

public class MigrationTaskEvent {
	// 未迁移
	public static final String ACTION_NO_MIGRATION = "0";
	// 已经迁移完成
	public static final String ACTION_CONPLETE_MIGRATION_SUCCESS = "1";
	// 迁移进行中
	public static final String ACTION_MIGRATIONING = "2";
	// 迁移出错
	public static final String ACTION_MIGRATION_FAIL = "3";
	private String action;
	private String data;

	public MigrationTaskEvent(String action, String data) {
		super();
		this.action = action;
		this.data = data;
	}

	public String getAction() {
		return action;
	}


	public void setAction(String action) {
		this.action = action;
	}


	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}

package cc.wulian.smarthomev5.callback.router.entity;

import java.util.List;

public class BlackAndWhiteData {
	private int code;
	private List<BlackAndWhiteEntity> list;
	private String msg;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<BlackAndWhiteEntity> getList() {
		return list;
	}

	public void setList(List<BlackAndWhiteEntity> list) {
		this.list = list;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}

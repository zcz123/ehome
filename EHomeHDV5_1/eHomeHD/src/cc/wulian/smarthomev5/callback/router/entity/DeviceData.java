package cc.wulian.smarthomev5.callback.router.entity;

import java.util.List;

public class DeviceData {
	private int code;
	private List<DeviceInfo> info;
	private String msg;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public List<DeviceInfo> getInfo() {
		return info;
	}

	public void setInfo(List<DeviceInfo> info) {
		this.info = info;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}

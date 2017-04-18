package cc.wulian.smarthomev5.event;

import com.alibaba.fastjson.JSONObject;

public class CommondDeviceConfigurationEvent {
	public final String gwID;
	// public final JSONObject jsonObject;
	public final String data;

	public CommondDeviceConfigurationEvent(String gwID, String string) {
		data= string;
		this.gwID = gwID;
	}
}

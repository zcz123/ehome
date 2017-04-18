package cc.wulian.smarthomev5.event;

import com.alibaba.fastjson.JSONArray;

import cc.wulian.ihome.wan.entity.SceneInfo;

public class DeviceBindSceneEvent {
	private static final String TAG = DeviceBindSceneEvent.class
			.getSimpleName();

	public JSONArray data;

	public DeviceBindSceneEvent(JSONArray data) {
		this.data=data;
	}
}

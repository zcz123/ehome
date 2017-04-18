package cc.wulian.smarthomev5.event;

import com.alibaba.fastjson.JSONObject;

public class FlowerEvent {
	
	public static final String ACTION_VOICE_CONTROL_GET = "ACTION_VOICE_CONTROL_GET";
	public static final String ACTION_VOICE_CONTROL_STATE = "ACTION_VOICE_CONTROL_STATE";
	public static final String ACTION_VOICE_CONTROL_CLEAR = "ACTION_VOICE_CONTROL_CLEAR";
	public static final String ACTION_VOICE_CONTROL_BIND = "ACTION_VOICE_CONTROL_BIND";
	public static final String ACTION_FLOWER_IMMEDIATELY_BROADCAST = "ACTION_FLOWER_IMMEDIATELY_BROADCAST"; // 立即播报
	public static final String ACTION_FLOWER_SELECT_LIGHT_EFFECT = "ACTION_FLOWER_SELECT_LIGHT_EFFECT"; // 敲击灯效
	public static final String ACTION_FLOWER_SET_LIGHT_TIME = "ACTION_FLOWER_SET_LIGHT_TIME"; // 敲击灯效持续时间
	public static final String ACTION_FLOWER_SET_BROADCAST_TIME = "ACTION_FLOWER_SET_BROADCASTTIME"; // 定时播报
	public static final String ACTION_FLOWER_SET_SHOW_TIME = "ACTION_FLOWER_SET_TIME_SHOW"; // 定时显示
	public static final String ACTION_FLOWER_BROADCAST_SET = "ACTION_BROADCAST_SET"; // 播报设置
	public static final String ACTION_FLOWER_BROADCAST_SWITCH = "ACTION_FLOWER_BROADCAST_SWITCH"; // 播报设置-播报开关
	public static final String ACTION_FLOWER_BROADCAST_CONVENTIONAL = "ACTION_FLOWER_BROADCAST_CONVENTIONAL"; // 播报设置-常规播报
	public static final String ACTION_FLOWER_BROADCAST_NETWORK_PROMPT = "ACTION_FLOWER_BROADCAST_NETWORK_PROMPT"; // 播报设置-网络提示音
	public static final String ACTION_FLOWER_BROADCAST_AUXILIARY_CUE = "ACTION_FLOWER_BROADCAST_AUXILIARY_CUE"; // 播报设置-辅助提示音
	public static final String ACTION_FLOWER_BROADCAST_VOLUME = "ACTION_FLOWER_BROADCAST_VOLUME"; // 播报设置-音量设置
	public static final String ACTION_FLOWER_POSITION_SET = "ACTION_POSITION_SET"; // 位置设置
	public static final String ACTION_FLOWER_TIMEZONE_GET="SETTING_TIMEZONE_GET";
	public static final String ACTION_FLOWER_TIMEZONE_SET="SETTING_TIMEZONE_SET";
	
	public static final String ACTION_FLOWER_HARD_DISK_INFO="ACTION_FLOWER_HARD_DISK_INFO"; //云盘信息
	
	private String action;

	private String eventStr;

	private JSONObject data;

	public FlowerEvent() {
	}

	public FlowerEvent(String action) {
		this.action = action;
	}

	public String getAction() {
		return this.action;
	}

	public String getEventStr() {
		return this.eventStr;
	}

	public void setAction(String paramString) {
		this.action = paramString;
	}

	public void setEventStr(String paramString) {
		this.eventStr = paramString;
	}

	public JSONObject getData() {
		return data;
	}

	public void setData(JSONObject data) {
		this.data = data;
	}

}

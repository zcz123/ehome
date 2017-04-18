/**
 * Project Name:  WulianICamH264
 * File Name:     SocketMsgApiType.java
 * Package Name:  com.wulian.h264decoder
 * @Date:         2015年5月28日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.h264decoder;

import android.text.TextUtils;

/**
 * @ClassName: SocketMsgApiType
 * @Function: Socket通讯Api类型
 * @Date: 2015年5月28日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public enum SocketMsgApiType {
	CONNECTION_SOCKET("00", "00", SocketAction.CONNECTION), // 握手
	GET_H264_FILE_INFO("01", "01", SocketAction.GET), // 获取所有H264视频文件信息
	GET_PICTURE_FILE_INFO("02", "02", SocketAction.GET), // 获取所有截图文件信息
	CONTROL_PLAY_PROGRESS("04", "04", SocketAction.CONTROL), // 进度控制
	STREAM_START_PLAY("05", "05", SocketAction.STREAM), // 启动回放
	CONTROL_STOP_PLAY("06", "06", SocketAction.CONTROL), // 停止回放
	PICTURE_GET_FILE("07", "07", SocketAction.PICTURE);// 获取jpg

	String requestCmd;// 请求命令
	String respondCmd;// 接收命令
	SocketAction action;// 行为

	private SocketMsgApiType(String requestCmd, String respondCmd,
			SocketAction action) {
		this.requestCmd = requestCmd;
		this.respondCmd = respondCmd;
		this.action = action;
	}

	public String getRequestCmd() {
		return this.requestCmd;
	}

	public String getRespondCmd() {
		return this.respondCmd;
	}

	public SocketAction getAction() {
		return this.action;
	}

	public static SocketMsgApiType getSipTypeByRespondCmd(String respond) {
		if (TextUtils.isEmpty(respond)) {
			return null;
		}
		for (SocketMsgApiType item : SocketMsgApiType.values()) {
			if (item.getRespondCmd().equalsIgnoreCase(respond)) {
				return item;
			}
		}
		return null;
	}

	public static SocketMsgApiType getSipTypeByRequestCmd(String request) {
		if (TextUtils.isEmpty(request)) {
			return null;
		}
		for (SocketMsgApiType item : SocketMsgApiType.values()) {
			if (item.getRequestCmd().equalsIgnoreCase(request)) {
				return item;
			}
		}
		return null;
	}

}

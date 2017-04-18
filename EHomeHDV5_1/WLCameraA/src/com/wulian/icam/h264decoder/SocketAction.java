/**
 * Project Name:  iCam
 * File Name:     SocketAction.java
 * Package Name:  com.wulian.icam.h264decoder
 * @Date:         2015年5月31日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.h264decoder;

/**
 * @ClassName: SocketAction
 * @Function: Socket 行为
 * @Date: 2015年5月31日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public enum SocketAction {
	DEFAULT, // 默认
	CONNECTION, // 握手
	GET, // 获取信息
	CONTROL, // 控制命令
	STREAM, // 流播放文件
	PICTURE;// 图片获取
	public static SocketAction getAction(String action) {
		for (SocketAction item : SocketAction.values()) {
			if (item.name().equalsIgnoreCase(action)) {
				return item;
			}
		}
		return DEFAULT;
	}

}

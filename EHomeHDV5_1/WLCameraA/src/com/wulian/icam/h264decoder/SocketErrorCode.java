/**
 * Project Name:  iCam
 * File Name:     SocketErrorCode.java
 * Package Name:  com.wulian.icam.h264decoder
 * @Date:         2015年5月31日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.h264decoder;

import com.wulian.icam.R;

/**
 * @ClassName: SocketErrorCode
 * @Function: Socket错误码
 * @Date: 2015年5月31日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public enum SocketErrorCode {

	SUCCESS(200, "成功", R.string.socket_success), // 成功
	UNKNOWN_HOST(400, "未知主机", R.string.socket_not_one_lan), // 不在同一局域网
	EOF(1, "文件结尾", R.string.socket_eof), // 文件读取结束
	INVALID_APPSECRET(125,"授权错误",R.string.socket_invalid_appsecret),//密码错误
	INVALID_VERSION(104,"固件版本较低",R.string.socket_low_version),//固件版本较低
	INVALID_IO(-9,"请求错误",R.string.socket_invalid_io),//请求错误
	UNKNOWN_EXCEPTION(0, "未知错误", R.string.socket_unknown_exception),// 客户端未知错误
	UNKNOWN_CAMERA_EXCEPTION(500, "未知错误", R.string.socket_unknown_exception);// 摄像头未知错误
	int errorCode;// 错误码
	String description;// 描述
	int resId;// 资源ID

	private SocketErrorCode(int errorCode, String description, int resId) {
		this.errorCode = errorCode;
		this.description = description;
		this.resId = resId;
	}

	public int getErrorCode() {
		return this.errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public int getResId() {
		return resId;
	}

	public static SocketErrorCode getTypeByCode(int code) {
		for (SocketErrorCode mErrorCode : SocketErrorCode.values()) {
			if (mErrorCode.getErrorCode() == code) {
				return mErrorCode;
			}
		}
		return UNKNOWN_EXCEPTION;
	}
}

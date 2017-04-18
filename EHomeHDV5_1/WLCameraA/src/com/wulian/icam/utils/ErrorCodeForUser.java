/**
 * Project Name:  iCam
 * File Name:     ErrorCodeForUser.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2014年11月5日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.utils;

import com.wulian.routelibrary.common.ErrorCode;
import com.wulian.routelibrary.common.RouteApiType;

/**
 * @ClassName: ErrorCodeForUser
 * @Function: 用户友好的错误提示
 * @Date: 2014年11月5日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public enum ErrorCodeForUser {

	// 1000-1099 非法的请求
	// 1100-1199 非法的参数
	// 2000-2099 未执行数据库操作的错误
	DEFAULT_ERROR(RouteApiType.V3_LOGIN, 0, "请求处理失败",
			"Request process fail"), LOGIN_INVALID_USER(
			RouteApiType.V3_LOGIN, 1111, "该账号还没有注册，无法登录",
			"This account has not been registered, cannot login"), LOGIN_INVALID_PWD1(
			RouteApiType.V3_LOGIN, 1101, "密码错误", "Password error"), LOGIN_INVALID_PWD2(
			RouteApiType.V3_LOGIN, 1122, "密码错误", "Password error"),  DEVICE_BIND_INVALID_ID(
			RouteApiType.V3_BIND_CHECK, 1006, "没有查到该设备记录",
			"No record of the equipment"), auth_invalid_user(
			RouteApiType.V3_BIND_RESULT, 1111, "该用户不存在", "User not exists");

	int errorCode;
	RouteApiType apiType;
	String description;
	String description_en;

	private ErrorCodeForUser(RouteApiType apiType, int errorCode,
			String description, String description_en) {
		this.errorCode = errorCode;
		this.apiType = apiType;
		this.description = description;
		this.description_en = description_en;
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	public RouteApiType getApiType() {
		return apiType;
	}

	public void setApiType(RouteApiType apiType) {
		this.apiType = apiType;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription_en() {
		return description_en;
	}

	public void setDescription_en(String description_en) {
		this.description_en = description_en;
	}

	/**
	 * @Function 根据用户的[请求类型]和[错误码]返回友好对象
	 * @author Wangjj
	 * @date 2014年11月5日
	 * @param apiType
	 * @param errorCode
	 * @return
	 */
	public static ErrorCodeForUser getErrorCodeForUser(RouteApiType apiType,
			int errorCode) {
		// 优先返回对用户友好错误提示
		for (ErrorCodeForUser e : ErrorCodeForUser.values()) {
			if (e.getApiType() == apiType && e.getErrorCode() == errorCode) {
				return e;
			}
		}

		// 其次返回底层的默认错误信息
		DEFAULT_ERROR.setDescription(ErrorCode.getTypeByCode(errorCode)
				.getDescription());
		DEFAULT_ERROR.setDescription_en(ErrorCode.getTypeByCode(errorCode)
				.getDescription_en());
		return DEFAULT_ERROR;
	}
}

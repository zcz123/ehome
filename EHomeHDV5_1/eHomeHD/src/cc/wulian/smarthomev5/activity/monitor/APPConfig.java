/**
 * Project Name:  FamilyRoute
 * File Name:     Config.java
 * Package Name:  com.wulian.familyroute.common
 * @Date:         2014年10月9日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package cc.wulian.smarthomev5.activity.monitor;

/**
 * @ClassName: Config
 * @Function: 配置参数，用于sharedpreference、默认常量
 * @Date: 2014年10月9日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class APPConfig {
	public static final String SP_CONFIG = "spConfig";
	public static final String IS_FIRST_LOGIN = "isFirstLogin";
	public static final String PASSWORD = "password";
	public static final String ENCRYPT_KEY = "wuliangroup.cc";// 加密解密的密码
	public static final String IS_AUTO_LOGIN = "isAutoLogin";// 一直为true
	public static final String IS_REM_PASS = "isRemPass";// 一直为true
	public static final String IS_LOGIN_OUT = "isLoginOut";
	public static final String ACCOUNT_NAME = "accuontName";
	public static final String NETWORK_PROTECT = "network_protect";
	public static final String FINISH_MAIN = "FINISH_MAIN";// 结束Main窗口
	public static final String FORGET_PWD = "forget";// 结束Main窗口
	public static final String EDIT_PWD = "edit";// 结束Main窗口
	public static final String FEEDBACK_TIMEOUT = "feedtime_timeout";// 反馈超时
	public static final int FEEDBACK_TIMEOUT_INTERVAL = 30;// 反馈超时30秒
	public static final int FINISH_DELAY = 1000;// 窗口关闭延时
	public static final String DEVICE_DEFAULT_WIFI_PWD = "87654321";
	public static final String DEVICE_WIFI_SSID_PREFIX = "Wulian_Camera_";
	public static final String SERVERNAME = "wuliangroup.cn";
	public static final int MOVE_SPEED = 1;
	public static final int REMOTEIP_RETRAY_TIMES = 5;
	public static final int LINK_TIME_OUT = 30000;
	public static final String DOMAIN_URL = "http://account.wuliangroup.cn/";
	public static final String REGISTER_URL = DOMAIN_URL + "register";
	public static final String FORGET_URL = DOMAIN_URL + "forget";
	public static final String VIDEO_INVERT = "_video_invert";
	public static final String DEFAULT_ALIYUN_STS_OSS_URL_SUFFIX = ".aliyuncs.com";

	public static final float DEFAULT_WIDTH_HEIGHT_RATIO = 9 / 16F;

}

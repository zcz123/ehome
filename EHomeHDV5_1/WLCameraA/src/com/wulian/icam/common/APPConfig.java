/**
 * Project Name:  FamilyRoute
 * File Name:     Config.java
 * Package Name:  com.wulian.familyroute.common
 * @Date:         2014年10月9日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.common;

/**
 * @ClassName: Config
 * @Function: 配置参数，用于sharedpreference、默认常量
 * @Date: 2014年10月9日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class APPConfig {
	public static final String SP_CONFIG = "spConfig";// 常规登录
	public static final String SP_LAN_CONFIG = "spLanConfig";// 局域网
	public static final String SP_USERS = "spUsers";// 登录用户
	public static final String SP_SNAPSHOT = "sp_snapshot";// 本地截图数据或头像或普通bitmap

	public static final String KEY_TEST_SERVER = "GOTO_TESTSERVER";
	public static final String KEY_OPEN_LOG = "OPEN_LOG";
	public static final String IS_FIRST_LOGIN = "isFirstLogin";
	public static final String PASSWORD = "password";
	public static final String ENCRYPT_KEY = "wuliangroup.cc";// 加密解密的密码
	public static final String IS_AUTO_LOGIN = "isAutoLogin";// 一直为true
	public static final String IS_REM_PASS = "isRemPass";// 一直为true
	public static final String IS_LOGIN_OUT = "isLoginOut";
	public static final String ACCOUNT_NAME = "accuontName";
	public static final String UID = "uid";
	public static final String ACCOUNT_USERINFO = "ACCOUNT_USERINFO";
	public static final String NETWORK_PROTECT = "_network_protect";
	public static final String FINISH_MAIN = "FINISH_MAIN";// 结束Main窗口
	public static final String FORGET_PWD = "forget";// 结束Main窗口
	public static final String EDIT_PWD = "edit";// 结束Main窗口
	public static final String FEEDBACK_TIMEOUT = "feedtime_timeout";// 反馈超时
	public static final int FEEDBACK_TIMEOUT_INTERVAL = 30;// 反馈超时30秒
	public static final int FINISH_DELAY = 500;// 窗口关闭延时
	public static final int MAX_MONITOR_AREA = 4;// 窗口关闭延时
	public static final String DEVICE_DEFAULT_WIFI_PWD = "87654321";
	public static final String DEVICE_WIFI_SSID_PREFIX = "Wulian_Camera_";
	public static final String SERVERNAME = "wuliangroup.cn";
	public static final int MOVE_SPEED = 1;
	public static final int REMOTEIP_RETRAY_TIMES = 2;// 获取设备信息重试2次，共3次
	public static final int LINK_RETRAY_TIMES = 2;// 软ip连接重试2次，共3次
	public static final int LINK_TIME_OUT = 20000;
	public static final int WIFI_TIME_OUT = 40000;// wifi配置超时
	// public static final String DOMAIN_URL = "https://account.test.sh.gg/";
	public static String DOMAIN_URL = "https://account.sh.gg/";
	// public static final String DOMAIN_URL =
	// "http://account.simplegg.com/";//国际版本(临时)
	public static String REGISTER_URL = DOMAIN_URL + "register";
	public static String FORGET_URL = DOMAIN_URL + "forget";
	public static final String FIREWARE = "cmic";
	public static final String ALBUM_DIR = "/iCam/Snapshot/";
//	public static final String ALBUM_DIR = "/wulian/camera/";//v5截图存储地址
	public static final String LATEST_VERSION = "latest_version";// 最新版本接口
	public static final long LATEST_VERSION_TIMEOUT = 10 * 60 * 1000;// 最新版本接口超时时间
	public static final long APP_KILL_DELEY = 5000;// App自杀延迟，如果不作取消处理，则该时间段内打开，会kill。
													// 500毫秒太快，缓存的意义不大
	public static final long DEVICE_INFO_DELAY = 2000;// 获取设备信息延迟
	public static final long CAMERA_WIFI_DELAY = 1000;// 连接摄像头重试延迟
	public static final String WEIBO_NICKNAME = "weibo_nickname";
	public static final String WEIBO_IMGURL = "weibo_imgurl";
	public static final String QQ_NICKNAME = "qq_nickname";
	public static final String QQ_IMGURL = "qq_imgurl";
	public static final String IS_THIRD_LOGIN = "is_third_login";
	public static final String IS_FIRST_MAIN = "is_first_main";// 首次进入主界面

	public static final String IS_MOVE_DETECTION = "_is_move_detection";
	public static final String MOVE_TIME = "_move_time";
	public static final String MOVE_WEEKDAY = "_move_weekday";
	public static final String MOVE_AREA = "_move_area";
	public static final String MOVE_SENSITIVITY = "_move_sensitivity";

	public static final String IS_COVER_DETECTION = "_is_cover_detection";
	public static final String COVER_TIME = "_cover_time";
	public static final String COVER_WEEKDAY = "_cover_weekday";
	public static final String COVER_AREA = "_cover_area";
	public static final String COVER_SENSITIVITY = "_cover_sensitivity";
	public static final String VIDEO_INVERT = "_video_invert";
	public static final String LAN_VIDEO_PWD = "_lan_video_pwd";
	public static final String HISTORY_SAVE_TIME = "_history_save_time";
	public static final String HISTORY_SAVE_WEEKDAY = "_history_save_weekday";

	public static final String LOGIN_USERS = "login_users";
	public static final String NICK_NAME = "_nick_name";

	public static final String ALARM_PUSH = "_alarm_push";
	public static final String MSG_UNREADER_ALARM_COUNT = "_msg_unreader_alarm_count";
	public static final String MSG_UNREADER_OAUTH_COUNT = "_msg_unreader_oauth_count";

	public static final int BINDING_NOTICES_CYCLE_TIME = 15;// 未读消息请求时间周期

	public static final String IS_GESTURE_PROTECT = "_is_gesture_protect";
	public static final String GESTURE_PWD = "_gesture_pwd";
	public static final String GESTURE_AUTO_CLEAR = "_gesture_auto_clear";
	public static final int GESTURE_TRY_TIMES = 5;

	public static final int VALIDITYOFBARCODE = 110;

	// V2特性
	// 是否是第一次展示摄像头二维码
	public static final String FIRST_SHOW_CAMERA_BARCODE = "_first_show_camera_barcode";
	public static final int WIFI_CONFIG_TIME = 30;
	// public static final int WIFI_CHECK_ADD_DEVICE_TIME = 10;
	// public static final int WIFI_CHECK_CONFIG_WIFI_TIME = 10;

	public static final int WIFI_CHECK_ADD_DEVICE_TIME = 90;
	public static final int WIFI_CHECK_CONFIG_WIFI_TIME = 30;
	public static final int WIFI_CHECK_CONFIG_WIFI_PERIOD = 1;// Seconds

	public static final int DEVICE_BIND_TIME = 20;

	public static final String JPUSH_FEEDBACK_TYPE = "feedback";
	public static final String JPUSH_BINDING_TYPE = "binding";
	public static final String KEY_TITLE = "title";
	public static final String KEY_MESSAGE = "message";
	public static final String KEY_EXTRAS = "extras";
	public static final float DEFAULT_WIDTH_HEIGHT_RATIO = 9/16F;
	public static final String DEFAULT_ALIYUN_STS_OSS_URL_SUFFIX=".aliyuncs.com";


	// public static final String FIRST_IN_FEEDBACKUI_BY_JPUSH =
	// "first_in_feedbackui_by_jpush";
	public static final String FIRST_IN_MSG_UI_BY_JPUSH = "first_in_msg_ui_by_jpush";

	public static final String HAND_INPUT_DEVICEID_CACHE = "_hand_input_deviceid_cache";

	public static void Init(boolean isTestServer) {
		if (isTestServer) {
			DOMAIN_URL = "https://account.test.sh.gg/";
		} else {
			DOMAIN_URL = "https://account.sh.gg/";
		}

		REGISTER_URL = DOMAIN_URL + "register";
		FORGET_URL = DOMAIN_URL + "forget";
		//Log.v("yanmin", "DOMAIN_URL = "+DOMAIN_URL);
	}


}

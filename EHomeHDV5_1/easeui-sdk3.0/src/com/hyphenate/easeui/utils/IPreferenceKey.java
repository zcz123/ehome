package com.hyphenate.easeui.utils;

public interface IPreferenceKey {
	public static final String P_KEY_PREFERENCE = "preference";
	public static final String P_KEY_AUTOLOGIN = "autosigin";//自动登录
	public static final String P_KEY_REMEMBER = "remembere";//记住密码
	public static final String P_KEY_USERNAME = "username";//最近一次登录的网关ID
	public static final String P_KEY_GATEWYNAME = "gatewyname";//网关名
	public static final String P_KEY_GUIDE= "P_GUIDE";//引导页观看记录
	public static final String P_KEY_CHAT_CURRENT_USER_NAME= "P_KEY_CHAT_CURRENT_USER_NAME"; //聊天的当前账号名称
	public static final String P_KEY_CHAT_PASSWORD= "P_KEY_CHAT_PASSWORD"; //聊天的账号密码
	public static final String P_KEY_CHAT_USER_NICK_NAME= "P_KEY_CHAT_USER_NICK_NAME"; //聊天的昵称
	public static final String P_KEY_CHAT_USER_NICK_NAME_TIME= "P_KEY_CHAT_USER_NICK_NAME_TIME"; //聊天的昵称修改时间
//	public static final String P_KEY_PREFERENCE = "preference";
//	public static final String P_KEY_REMEMBER = "remembere";
//	public static final String P_KEY_AUTOLOGIN = "autosigin";
//	public static final String P_KEY_USERNAME = "username";
//	public static final String P_KEY_GATEWYNAME = "gatewyname";
	public static final String P_KEY_GATEWYVERSION = "gatewyVersion";
	public static final String P_KEY_USERPWD = "pwd";
	// NEW ADD
	public static final String P_KEY_LAST_SAVE_IMAGE_VERSION = "P_KEY_LAST_SAVE_IMAGE_VERSION";

	public static final String P_KEY_THEME_ID = "P_KEY_THEME_ID";

	public static final String P_KEY_NORMAL_QUIT = "P_KEY_NORMAL_START";

	public static final String P_KEY_FIRST_IN = "P_KEY_FIRST_IN";
	public static final String P_KEY_VERSION = "P_KEY_VERSION";
	public static final String P_KEY_UPDATE_REMIND = "P_KEY_UPDATE_REMIND";

	// keep client's corrdinate location 位置坐标
	public static final String P_KEY_LOCATION_LAT = "P_KEY_LOCATION_LAT";
	public static final String P_KEY_LOCATION_LNG = "P_KEY_LOCATION_LNG";
	// keep client's stb loation
	public static final String P_KEY_LOCATION_ID = "P_KEY_LOCATION_ID";
	public static final String P_KEY_LOCATION_NAME = "P_KEY_LOCATION_NAME";
	public static final String P_KEY_LOCATION_TIME = "P_KEY_LOCATION_TIME";
	// keep advertisement loation
	public static final String P_KEY_ADV_LOCATION_COUNTRY = "P_KEY_ADV_LOCATION_COUNTRY";
	public static final String P_KEY_ADV_LOCATION_PROVINCE = "P_KEY_ADV_LOCATION_PROVINCE";
	public static final String P_KEY_ADV_LOCATION_CITY = "P_KEY_ADV_LOCATION_CITY";
	public static final String P_KEY_ADV_LOCATION_CITY_CODE = "P_KEY_ADV_LOCATION_CITY_CODE";
	public static final String P_KEY_ADV_CONTENT = "P_KEY_ADV_CONTENT";
	// default key gwID.xml

	public static final String P_KEY_USE_LOGINED_LOG = "P_KEY_USE_LOGINED_LOG";
	public static final String P_KEY_USE_EXIT_LOG = "P_KEY_USE_EXIT_LOG";
	public static final String P_KEY_GO_BACKGROPUND_TIME = "P_KEY_GO_BACKGROPUND_TIME";
	// push
	public static final String P_KEY_START_ON_BOOT_COMPLETED = "P_KEY_START_ON_BOOT_COMPLETED";
	public static final String P_KEY_RUN_ON_BACKGROUND = "P_KEY_RUN_ON_BACKGROUND";
	public static final String P_KEY_OPEN_GPS = "P_KEY_OPEN_GPS";
	public static final String P_KEY_OPEN_SHAKE = "P_KEY_OPEN_SHAKE";
	public static final String P_KEY_OPEN_WIFI = "P_KEY_OPEN_WIFI";

	public static final String P_KEY_VOICE_SPEED = "P_KEY_VOICE_SPEED";
	public static final String P_KEY_BAIDU_TOKEN = "P_KEY_BAIDU_TOKEN";
	public static final String P_KEY_BAIDU_TOKEN_TIME = "P_KEY_BAIDU_TOKEN_TIME";

	public static final String P_KEY_ALARM_NOTE_TYPE_AUDIO = "P_KEY_ALARM_NOTE_TYPE_AUDIO";
	public static final String P_KEY_ALARM_NOTE_TYPE_AUDIO_NAME = "P_KEY_ALARM_NOTE_TYPE_AUDIO_NAME";
	public static final String P_KEY_ALARM_NOTE_TYPE_AUDIO_ENABLE = "P_KEY_ALARM_NOTE_TYPE_AUDIO_ENABLE";

	public static final String P_KEY_OFFLINE_ALARM_NOTE_TYPE_AUDIO = "P_KEY_OFFLINE_ALARM_NOTE_TYPE_AUDIO";
	public static final String P_KEY_OFFLINE_ALARM_NOTE_TYPE_AUDIO_NAME = "P_KEY_OFFLINE_ALARM_NOTE_TYPE_AUDIO_NAME";
	public static final String P_KEY_OFFLINE_ALARM_NOTE_TYPE_AUDIO_ENABLE = "P_KEY_OFFLINE_ALARM_NOTE_TYPE_AUDIO_ENABLE";

	public static final String P_KEY_LOW_POWER_ALARM_NOTE_TYPE_AUDIO = "P_KEY_LOW_POWER_ALARM_NOTE_TYPE_AUDIO";
	public static final String P_KEY_LOW_POWER_ALARM_NOTE_TYPE_AUDIO_NAME = "P_KEY_LOW_POWER_ALARM_NOTE_TYPE_AUDIO_NAME";
	public static final String P_KEY_LOW_POWER_ALARM_NOTE_TYPE_AUDIO_ENABLE = "P_KEY_LOW_POWER_ALARM_NOTE_TYPE_AUDIO_ENABLE";

	public static final String P_KEY_DESTORY_ALARM_NOTE_TYPE_AUDIO = "P_KEY_DESTORY_ALARM_NOTE_TYPE_AUDIO";
	public static final String P_KEY_DESTORY_ALARM_NOTE_TYPE_AUDIO_NAME = "P_KEY_DESTORY_ALARM_NOTE_TYPE_AUDIO_NAME";
	public static final String P_KEY_DESTORY_ALARM_NOTE_TYPE_AUDIO_ENABLE = "P_KEY_DESTORY_ALARM_NOTE_TYPE_AUDIO_ENABLE";

	public static final String P_KEY_ALARM_NOTE_TYPE_VIBRATE_ENABLE = "P_KEY_ALARM_NOTE_TYPE_VIBRATE_ENABLE";
	public static final String P_KEY_ALARM_NOTE_TYPE_TTS_ENABLE = "P_KEY_ALARM_NOTE_TYPE_TTS_ENABLE";

	// door pwd
	public static final String P_KEY_DEVICE_DOOR_LOCK_PWD = "P_KEY_DEVICE_DOOR_LOCK_PWD";

	// offline device alarm or sensor data
	public static final String P_KEY_QUERY_DEVICE_OFFLINE_LAST_DATE = "P_KEY_QUERY_DEVICE_OFFLINE_LAST_DATE";

	public static final String P_KEY_ALARM_CONTINUE_TIME = "P_KEY_ALARM_CONTINUE_TIME";

	public static final String P_KEY_IR_CURRENT_PAGE = "P_KEY_IR_CURRENT_PAGE";

	// for two output default data
	public static final String P_KEY_TWO_OUTPUT_DEFAULT_DATA = "P_KEY_TWO_OUTPUT_DEFAULT_DATA";
	// for area group home setting
	public static final String P_KEY_AREA_GROUP_HOME_SETTING = "P_KEY_AREA_GROUP_HOME_SETTING";
	public static final String P_KEY_REDDOT_NAVIGATION_MENU = "P_KEY_REDDOT_NAVIGATION_MENU";
	public static final String P_KEY_REDDOT_NAVIGATION_CONTACT_US = "P_KEY_REDDOT_NAVIGATION_CONTACT_US";
	public static final String p_KEY_ANNOUNCEMENT_VERSTION = "p_KEY_ANNOUNCEMENT_VERSTION";
	public static final String P_KEY_ANNOUNCEMENT_LOTTERY_VERSTION = "P_KEY_ANNOUNCEMENT_LOTTERY_VERSTION";
	public static final String P_KEY_ANNOUNCEMENT_WIN_LOTTERY_VERSTION = "P_KEY_ANNOUNCEMENT_WIN_LOTTERY_VERSTION";
	public static final String p_KEY_WULIAN_CLOUD_BASE_URL = "p_KEY_WULIAN_CLOUD_BASE_URL";

	public static final String P_KEY_DEFAULT_SCENE_SETTING = "P_KEY_DEFAULT_SCENE_SETTING";
	public static final String P_KEY_DEFAULT_SCENE_SELECT = "P_KEY_DEFAULT_SCENE_SELECT";

	public static final String P_KEY_ALARM_ALL_DOOR_LOCK = "P_KEY_ALARM_ALL_DOOR_LOCK";
	public static final String P_KEY_ALARM_OPEN_DOOR_LOCK = "P_KEY_ALARM_OPEN_DOOR_LOCK";
	public static final String P_KEY_ALARM_CLOSE_DOOR_LOCK = "P_KEY_ALARM_CLOSE_DOOR_LOCK";
	public static final String P_KEY_ALARM_PWD_ERROR_DOOR_LOCK = "P_KEY_ALARM_PWD_ERROR_DOOR_LOCK";
	public static final String P_KEY_ALARM_PASSWORD_DOOR_LOCK = "P_KEY_ALARM_PASSWORD_DOOR_LOCK";
	public static final String P_KEY_ALARM_BUTTON_DOOR_LOCK = "P_KEY_ALARM_BUTTON_DOOR_LOCK";
	public static final String P_KEY_ALARM_FINGER_DOOR_LOCK = "P_KEY_ALARM_FINGER_DOOR_LOCK";
	public static final String P_KEY_ALARM_CARD_DOOR_LOCK = "P_KEY_ALARM_CARD_DOOR_LOCK";
	public static final String P_KEY_ALARM_KEY_DOOR_LOCK = "P_KEY_ALARM_KEY_DOOR_LOCK";

	public static final String P_CAMERA_APK_DOWNLOAD_COMPLETE= "P_CAMERA_APK_DOWNLOAD_COMPLETE";
//	public static final String P_KEY_GUIDE= "P_GUIDE";
	public static final String P_KEY_HOUSE_HAS_UPGRADE= "P_KEY_HOUSE_HAS_UPGRADE";
	public static final String P_KEY_HOUSE_OWN_SEND_QUERY= "P_KEY_HOUSE_OWN_SEND_QUERY";
	public static final String P_KEY_HOUSE_RULE_TIMING_STATUS= "P_KEY_HOUSE_RULE_TIMING_STATUS";
	public static final String P_KEY_HOUSE_OWN_SEND_MODIFY= "P_KEY_HOUSE_OWN_SEND_MODIFY";
	public static final String P_KEY_HOUSE_SCENE_TASK_SEND_QUERY= "P_KEY_HOUSE_SCENE_TASK_SEND_QUERY";
	public static final String P_KEY_NFC_MESSAGE= "P_KEY_NFC_MESSAGE";
	//	public static final String P_KEY_HOUSE_OWN_SEND_DO_UPGRADE= "P_KEY_HOUSE_OWN_SEND_DO_UPGRADE";
//	public static final String P_KEY_HOUSE_OWN_SEND_GET_UPGRADE= "P_KEY_HOUSE_OWN_SEND_GET_UPGRADE";
//	public static final String P_KEY_HOUSE_OWN_SEND_CLEAR_UPGRADE= "P_KEY_HOUSE_OWN_SEND_CLEAR_UPGRADE";
	public static final String P_KEY_TIME_ZONE_AUTO_SYNCHRONOUS= "P_KEY_TIME_ZONE_AUTO_SYNCHRONOUS"; //时区自动同步 boolean
	public static final String P_KEY_HOUSE_ADD_RULE_GUIDE= "P_KEY_HOUSE_ADD_RULE_GUIDE"; //管家规则引导
	public static final String P_KEY_HOUSE_INTRODUCE_GUIDE= "P_KEY_HOUSE_INTRODUCE_GUIDE"; //管家介绍
	public static final String P_KEY_FLOWER_FM_ADD_TIME= "P_KEY_FLOWER_FM_ADD_TIME"; //电台获取网关时间
	public static final String P_KEY_FLOWER_FM_ADD_FIRST_TIME= "P_KEY_FLOWER_FM_ADD_FIRST_TIME"; //是否开始保存电台的时间
	public static final String P_KEY_FLOWER_FM_DEVICE_MAP= "P_KEY_FLOWER_FM_DEVICE_MAP"; //电台获取Map
	public static final String P_KEY_CHECK_IS_ADMIN= "P_KEY_CHECK_IS_ADMIN"; //判断是否是授权用户
	public static final String P_KEY_CHECK_ACCOUNT_ENTER_TYPE= "P_KEY_CHECK_ACCOUNT_ENTER_TYPE"; //判断通过网关直接登录还是账号登录
	public static final String P_KEY_VOC_HTML_URI= "p_key_voc_html_uri"; //voc设备的html uri
	public static final String P_KEY_NOISE_HTML_URI= "p_key_noise_html_uri"; //noise设备的html uri
	public static final String P_KEY_AIR_HTML_URI= "p_key_air_html_uri"; //air设备的html uri
	public static final String P_KEY_TEMHUM_HTML_URI= "p_key_temhum_html_uri"; //temhum设备的html uri
	public static final String P_KEY_PM_HTML_URI= "p_key_pm_html_uri"; //pm设备的html uri
}
package cc.wulian.smarthomev5.utils;

import java.io.File;

public class CmdUtil {
	/**
	 * app type: 0:PC,1:APhone,2:APad,3:iPhone,4:iPad,5:WPhone,6:WPad
	 */
	public final static String CLIENT_APP_TYPE_V5 = "smarthomev5";

	public static final String GATEWAY_DOWN_ACTION = "GatewayDown";

	public static final String ACTION_INSTALL_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
	public static final String EXTRA_DUPLICATE = "duplicate";
	public static final String URL_OLD_SDK = "content://com.android.launcher.settings/favorites?notify=true";
	public static final String URL_NEW_SDK = "content://com.android.launcher2.settings/favorites?notify=true";
	public static final String SHORTCUT_TITLE = "title";
	public static final String SHORTCUT_ICON_RESOURCE = "iconResource";

	public static final int MESSAGE_DOWNLOAD_NEW_LOGO = 79;
	public static final int MESSAGE_DOWNLOAD_SHOW_DIALOG = 80;
	public static final int MESSAGE_DOWNLOAD_BEGIN = 81;
	public static final int MESSAGE_DOWNLOAD_OK = 82;
	public static final int MESSAGE_DOWNLOAD_FAIL = 83;
	public static final int MESSAGE_DOWNLOAD_ING = 84;
	public static final int MESSAGE_CONNECT_GATEWAY_ERROR_INFO = 85;
	public static final int MESSAGE_CONNECT_GATEWAY_ERROR_OFF_LINE = 86;
	public static final int MESSAGE_CONNECT_GATEWAY_ERROR_OTHER = 87;
	public static final int MESSAGE_CONNECT_GATEWAY = 88;
	public static final int MESSAGE_REFRESH_DEV_LIST = 90;
	public static final int MESSAGE_REFRESH_DEV_DATA = 91;
	public static final int MESSAGE_REFRESH_DEV_STUS = 92;
	public static final int MESSAGE_ADD_AREA = 93;
	public static final int MESSAGE_EDIT_DEL_AREA = 94;
	public static final int MESSAGE_REFRESH_DIALOG_LIST = 95;
	public static final int MESSAGE_REFRESH_SCENE_LIST = 96;
	public static final int MESSAGE_ADD_SCENE = 97;
	public static final int MESSAGE_EDIT_DEL_SCENE = 98;
	public static final int MESSAGE_LOAD_SCENE_LIST = 99;
	public static final int MESSAGE_PROCESS_DISMISS = 100;
	public static final int MESSAGE_PROCESS_SHOW = 101;
	public static final int MESSAGE_SAVE_INFO_ING = 102;
	public static final int MESSAGE_DISCONNECT_SERVER = 103;
	public static final int MESSAGE_RECONNECT_SERVER = 104;
	public static final int MESSAGE_DEL_ALL_MESSAGE = 105;
	public static final int MESSAGE_SAVE_FAILE = 106;
	public static final int MESSAGE_DEL_ALL_DATA_SURE = 107;
	public static final int MESSAGE_REFRESH_DEV_RSSI_LIST = 108;
	public static final int MESSAGE_NFC_READ = 109;
	public static final int MESSAGE_NFC_WRITE = 110;
	public static final int MESSAGE_DEL_DEVICE = 111;
	public static final int MESSAGE_RINGTON_SUBMIT = 112;
	public static final int MESSAGE_RINGTON_CACEL = 113;
	public static final int MESSAGE_CHANGE_THEME = 114;
	public static final int MESSAGE_CHECK_NETWORK_TYPE = 115;
	public static final int MESSAGE_REFRESH_MONITOR = 116;
	public static final int MESSAGE_SEND_HEART = 117;
	public static final int MESSAGE_SWITCH_TIMING_SCENE = 118;
	public static final int MESSAGE_REFRESH_DEV_IR_STUS = 119;
	public static final int MESSAGE_REFRESH_DEV_CAMERA_UID = 120;
	public static final int MESSAGE_DEVICE_DOOR_PASS_CHECK = 121;

	public static final String MARK_ALARM = "Alarm";
	public static final String MARK_EDIT_SHARE = "Edit";
	public static final String MARK_ADD_DEVICE = "AddDevice";
	public static final String MARK_DEL_DEVICE = "DelDevice";
	public static final String MARK_ADD_AREA = "AddArea";
	public static final String MARK_EDIT_AREA = "EditArea";
	public static final String MARK_DEL_AREA = "DelArea";
	public static final String MARK_DEL_AREA_DEVICE = "DelAreaDevice";
	public static final String MARK_ADD_SCENE = "AddScene";
	public static final String MARK_EDIT_SCENE = "EditScene";
	public static final String MARK_DEL_SCENE = "DelScene";
	public static final String MARK_DEL_SCENE_TASK = "DelSceneDevice";
	public static final String MARK_DEL_ALL_MSG = "DelAllMessage";
	public static final String MARK_DEL_ALL_DATA = "DelAllData";
	public static final String MARK_DEL_ALL_DATA_SURE = "DelAllDataSure";
	public static final String MARK_QUIT = "Quit";
	public static final String MARK_LOGOUT = "Logout";
	public static final String MARK_SAVE = "Save";
	public static final String MARK_UPDATE_SOFT = "Update";
	public static final String MARK_ADD_SHORTCUT = "AddShortcut";
	public static final String MARK_AUTOLOGIN_FAIL_ERROR_NAME_OR_PWD = "AutoLoginError";
	public static final String MARK_AUTOLOGIN_FAIL_ERROR_OFF_LINE = "AutoLoginErrorOffLine";
	public static final String MARK_AUTOLOGIN_FAIL_ERROR_OTHER = "AutoLoginErrorOther";
	public static final String MARK_RINGTON_SUBMIT_REQUEST = "MARK_RINGTON_SUBMIT_REQUEST";

	public static final String SHARE_TYPE_EMAIL = "email";
	public static final String SHARE_TYPE_PHONE = "phone";

	public static final String BOOLEAN_TYPE = "boolean";
	public static final String INT_TYPE = "integer";

	public static final String COMPANY_PPM = "PPM";
	public static final String COMPANY_C = "'C";
	public static final String COMPANY_LUX = "LUX";
	public static final String COMPANY_PERCENT = "%";
	public static final String COMPANY_RH = "%RH";
	public static final String COMPANY_KG = "KG";
	public static final String COMPANY_M3 = "M3";
	public static final String COMPANY_MM = "MM";
	public static final String COMPANY_NULL = "N/A";
	public static final String COMPANY_COLON = ":";
	public static final String COMPANY_ZERO = "0";
	public static final String COMPANY_EMPTY = "";
	public static final String COMPANY_A = "A";
	public static final String COMPANY_V = "V";
	public static final String COMPANY_HZ = "HZ";
	public static final String COMPANY_W = "W";
	public static final String COMPANY_KW_H = "KW·H";
	public static final String COMPANY_COMMA = ",";
	public static final String COMPANY_SEMI = ";";

	public static final String DEV_OFF_LINE = "0";
	public static final String DEV_ON_LINE = "1";
	public static final String DEV_ON = "2";
	public static final String DEV_OFF = "3";
	public static final String DEV_CATE_1 = "0101";
	public static final String DEV_CATE_2 = "0102";
	public static final String DEV_CATE_3 = "0103";
	public static final String DEV_CATE_4 = "0104";

	public static final String SCENE_USING = "2";
	public static final String SCENE_UNUSE = "1";
	public static final String SCENE_UNKNOWN = "-1";

	public static final String DEV_STUS_ON = "1";
	public static final String DEV_STUS_OFF = "0";

	public static final String DEV_ALARM_ON = "1";
	public static final String DEV_ALARM_OFF = "0";

	public static final int NFC_TYPE_SCENE = 0;
	public static final int NFC_TYPE_DEVICE = 1;

	public static final String MODE_SWITCH = "0";
	public static final String MODE_ADD = "1";
	public static final String MODE_UPD = "2";
	public static final String MODE_DEL = "3";
	public static final String MODE_DEL_TIME = "4";
	public static final String MODE_ADD_TIME = "5";
	public static final String MODE_SEARCH_TIME = "6";
	public static final String MODE_BATCH_ADD = "4";

	public static final String GW_DEFAULT_DEMO_ID = "000000000000";
	public static final String ROOM_DEFAULT = "-1";
	public static final String SENSOR_DEFAULT = "-1";
	public static final String IR_STATUS_NOSTUDY = "0";
	public static final String IR_STATUS_STUDY = "1";
	public static final String IR_MODE_STUDY = "1";
	public static final String IR_MODE_CTRL = "2";
	public static final String IR_MODE_MATCH = "3";
	public static final int IR_DEFAULT_CODE = 511;
	public static final int IR_AIR_DEFAULT_CODE = 0;
	public static final int IR_TV_DEFAULT_CODE = 256;
	public static final String IR_KEYSET_DEFAULT = "00";
	public static final String IR_GENERAL_KEY_DEFAULT = "000";
	public static final String IR_GENERAL_KEY_611 = "611";
	public static final String IR_GENERAL_KEY_612 = "612";
	public static final String IR_GENERAL_KEY_700 = "700";
	public static final String IR_GENERAL_KEY_701 = "701";
	public static final String ID_UNKNOW = "-1";
	public static final String ID_NFC_UNKNOW = "-1";
	public static final String SEARCH_DEVICE_DEFAULT_SHOW = "10";

	public static final String MAP_KEY_IMAGE = "image";
	public static final String MAP_KEY_NAME = "name";

	public static final String NEW_SKIN_VERSION_NAME = "Splash.png";
	public static final String NEW_SKIN_VERSION_PATH = FileUtil.getUpdatePath()
			+ File.separator + CmdUtil.NEW_SKIN_VERSION_NAME;

	public static final String APPERROR_EMAIL_ADD = "feedback@wulian.mobi";
	public static final String SAVE_MAP_KEY_SCENE_TASK_LIST = "sceneTaskList";

	// 梦想之花
	public static final String FLOWER_IMMEDIATELY_BROADCAST = "1"; // 立即播报
	public static final String FLOWER_LIGHT_EFFECT = "2"; // 敲击灯效
	public static final String FLOWER_LIGHT_TIME = "3"; // 敲击灯效持续时间
	public static final String FLOWER_TIMING_BROADCAST = "4"; // 定时播报
	public static final String FLOWER_SET_SHOW_TIME = "5"; // 定时显示
	public static final String FLOWER_BROADCAST_SWITCH = "61"; // 播报设置-播报开关
	public static final String FLOWER_BROADCAST_CONVENTIONAL = "62"; // 播报设置-常规播报
	public static final String FLOWER_BROADCAST_NETWORK_PROMPT = "63"; // 播报设置-网络提示音
	public static final String FLOWER_BROADCAST_AUXILIARY_CUE = "64"; // 播报设置-辅助提示音
	public static final String FLOWER_BROADCAST_VOLUME = "65"; // 播报设置-音量设置

	public static final String FLOWER_POSITION_SET = "7"; // 位置设置

	public static final String GET_HOUSE_STATUS = "get";
	public static final String DO_HOUSE_STATUS = "do";
	public static final String CLEAR_HOUSE_STATUS = "clear";
	
	public static final String HOUSE_RULES_USING = "2";
	public static final String HOUSE_RULES_UNUSE = "1";
	
	public static final String MINIGATEWAY_GET_JUDGE_RELAY_SIGN = "1";
	public static final String MINIGATEWAY_GET_RELAY_SEARCH = "2";
	public static final String MINIGATEWAY_SET_RELAY = "3";
	public static final String MINIGATEWAY_SET_RELAY_SSID = "4";
	public static final String MINIGATEWAY_GET_IP = "5";
	public static final String MINIGATEWAY_GET_RELAY_WIFI_INFO = "6";

}
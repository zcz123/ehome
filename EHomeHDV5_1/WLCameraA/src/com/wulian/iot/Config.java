package com.wulian.iot;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.wulian.icam.R;
import com.yuantuo.netsdk.TKCamHelper;

import android.content.Context;
import android.os.Environment;

public class Config {
    public static final String CAMERA_HELPER = "helper";
    public static int definition = -1;//设置视频清晰   0为超清  1 为标清。
    public final static String rootVideo = Environment.getExternalStorageDirectory() + "/wulian/video/";
    public final static String rootPhoto = "/storage/emulated/0/wulian/camera/";
    public final static String eaglePhoto = Environment.getExternalStorageDirectory() + "/wulian/eagle/snapshot/";
    public final static String eagleVideo = "/storage/emulated/0/wulian/eagle/video/";
    public final static int videoNameLength = 23;
    public final static int firmwareLength = 32;
    public final static String COMMON_SHARED = "perferences";
    public static final String MOVE_TIME = "_move_time";
    public static final String MOVE_WEEKDAY = "_move_weekday";
    public static final String MOVE_AREA = "_move_area";
    public static final String MOVE_SENSITIVITY = "_move_sensitivity";
    public static final String SNAPSHOT = "_snapshot_or_avatar";
    public static final String XMLUrl = "http://otacdn.wulian.cc/quanjiaodu_zh.xml";//固件升级获取url
    public static final String Data = "{'cmd':'GET_FM_INFO','msgid':'001'} \r\n\r\n";
    public final static int Adaptive = 0; // 自适应
    public final static int H_Definition = 1;// 高清
    public final static int S_Definition = 2;// 标清
    public final static int Fluency = 3;// 流畅

    public final static int parse_xml = 1;
    public final static int parse_json = 2;
    public final static int UPDATE_IS_OK = 11;

    public final static String PORT = "PORT";
    public final static String BUILD = "BUILD";
    public final static String VERSION = "VERSION";
    public final static String DEVICE_IP = "DEVICE_IP";
    public final static String tutkUid = "TUTKUID";
    public final static String tutkPwd = "TUTKPWD";
    public final static String eagleName = "EAGLENAME";
    public final static String isAdmin = "ISADMIN";
    public final static String aimSSID = "ssid";
    public final static String aimPwd = "sspwd";
    public final static String tutkEntryMode = "EntryMode";
    public final static String deskBean = "deskBean";
    //获取猫眼设备信息 保存到sp中用
    public final static String MODEL_EAGLE = "model_eagle";
    public final static String VENDOR_EAGLE = "vendor_eagle";
    public final static String VERSION_EAGLE = "version_eagle";
    public final static String CHANNEL_EAGLE = "channel_eagle";
    public final static String TOTAL_EAGLE = "total_eagle";
    public final static String FREE_EAGLE = "free_eagle";
    public final static String RESERVED_EAGLE = "reserved_eagle";
    public final static String SENSITIVITY_EAGLE = "sensitivity_eagle";
    public final static String CAMERANAME_EAGLE = "cameraname_eagle";

    //add by guofeng
    public final static String IS_SET_PROTECT = "_set_protect";
    public final static String IS_HAWKEYE_UID = "_is_hawkeyeuid";       //是否为鹰眼UID

    public final static String COMMON_SHARED_HAWKEYE = "_perferenceshawk";  //是否为鹰眼首选项的标志
    public final static String IS_SET_APWIFI = "_set_apwifi";       //即作为鹰眼 是否配置过wifi的标志。
    public final static String IS_EAGLE_EYE = "_is_egaleeye";       //鹰眼的标志
    public static final int modify = 0;
    public static final int delete = 1;
    public static final String firHead = Environment.getExternalStorageDirectory() + "/wulian/camera/version/";
    public static final String firSuffix = ".fw";
    public static final String suffix = ".jpg";
    public static final String status = "status";
    public static final String sdexist = "sdexist";
    public static final String totalMB = "totalMB ";
    public static final String freeMB = "freeMB";
    public static final int LOCAL_VOIDE = 0;
    public static final int SERVER_VOIDE = 1;
    public static String CAMERA = "CAMERA";
    public static String EAGLE = "EAGLE";
    public static String Hawkeye = "HAWKEYE";
    public static boolean isEagleNetWork = true;
    public static int cameraVideoType = 0;
    public static int eagleVideoType = 1;
    public final static String playEagleVideoTyep = "playEagleVideoTyep";
    public final static String DECODE_MODE = "decodeMode";
    public final static String DESK_CAMERA_DEFINITION_SP = "DESK_CAMERA_DEFINITION";
    public final static String DESK_CAMERA_DECODE_SP = "DESK_CAMERA_DECODE_SP";
    public final static String DESK_CAMERA_SENSITIVITY_SP = "DESK_CAMERA_SENSITIVITY_SP";
    public static boolean isResBaiduPush = true;
    public final static String eagleSettingEnter = "Enter";
    public final static int EagleConnMode = 1;

    public final static String DEVICE_ID="device_id";
    public final static String DEVICE_TYPE="device_type";
    public final static String GW_ID="GW_ID";

    public final static String DESK_CAMERA="desk_camera";
    public final static String OTHER_CAMERA="other_camera";
    public final static String WLCLOUD_CAMERA="wlcloud_camera";
    public final static String EAGLE_CAMERA="eagle_camera";

    public static class Language {
        public static final int english = 1;
        public static final int chinese = 2;

        public static Map<String, Object> camLanguage(Context context) {
            Map<String, Object> map = new HashMap<>();
            map.put(context.getResources().getString(R.string.desk_language_english), english);
            map.put(context.getResources().getString(R.string.desk_language_chinese), chinese);
            return map;
        }
    }

    public static class Resolution {
        public static final int _1080P = 0;
        public static final int _720P = 1;

        public static Map<String, Object> camRes(Context context) {
            Map<String, Object> map = new HashMap<>();
            map.put("1080p", _1080P);
            map.put("720p", _720P);
            return map;
        }
    }

    public static class Volume {
        public static final int MUTE = 10;
        public static final int LOW = -68;
        public static final int MID = -36;
        public static final int HIGH = -24;
        public static final int VERYHIGH = -2;

        public static LinkedHashMap<String, Object> camVolume(Context context) {
            LinkedHashMap<String, Object> map = new LinkedHashMap<>();
            map.put(context.getResources().getString(R.string.air_conditioner_mute), MUTE);
            map.put(context.getResources().getString(R.string.cateye_sensitivity_setting_low), LOW);
            map.put(context.getResources().getString(R.string.cateye_sensitivity_setting_mid), MID);
            map.put(context.getResources().getString(R.string.cateye_sensitivity_setting_high), HIGH);
            map.put(context.getResources().getString(R.string.dt_super_higher), VERYHIGH);
            return map;
        }
    }

    public static class IRSeries {
        public static final int CLOSE = 0;
        public static final int OPEN = 1;
        public static final int AUTO = 2;

        public static Map<String, Object> camIRSeries(Context context) {
            Map<String, Object> map = new HashMap<>();
            map.put(context.getResources().getString(cc.wulian.app.model.device.R.string.device_state_close), CLOSE);
            map.put(context.getResources().getString(cc.wulian.app.model.device.R.string.device_state_open), OPEN);
            map.put(context.getResources().getString(cc.wulian.app.model.device.R.string.device_ac_cmd_auto), AUTO);
            return map;
        }
    }
}

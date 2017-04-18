package cc.wulian.app.model.device.impls.controlable.fancoil;

import java.text.SimpleDateFormat;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;

public class FanCoilUtil {

	public static final String GWID = "gwId";
	public static final String DEVID = "devId";
	public static final String EP= "ep";
	public static final String EPTYPE= "epType";
	
	//查询当前状态指令
	public static final String CURRENT_QUERY_TAG = "1";
	//查询基本数据  发送指令
	public static final String CURRENT_QUERY_CMD_DATA = "11";
	//查询模式数据  发送指令
	public static final String CURRENT_QUERY_CMD_MODE= "12";
	
	
	//开关状态 标志
	public static final String STATE_OFF = "00";
	public static final String STATE_ON = "01";
	//状态 开关 指令标志
	public static final String STATE_DATA_TAG = "02";
	// 开关  发送指令
	public static final String STATE_CMD_ON = "21";
	public static final String STATE_CMD_OFF = "20";
	
	//编程状态开关 指令
	public static final String PROGRAM_STATE_TAG = "0C";
	//编程状态开关 发送指令
	public static final String PROGRAM_STATE_CMD_ON = "C1";
	public static final String PROGRAM_STATE_CMD_OFF = "C0";

	//温度改变指令
	public static final String TEMP_CHANGE_CMD_TAG = "6";
	public static final String TEMP_CHANGE_TAG = "06";
	//工作模式 
	public static final String MODE_HEAT = "01";
	public static final String MODE_COOL = "02";
	public static final String MODE_FAN = "03";
	public static final String MODE_HEAT_ENERGY = "04";
	public static final String MODE_COOL_ENERGY= "05";
	
	//模式改变指令
	public static final String MODE_SATTE_TAG = "03";
	public static final String MODE_HEAT_CMD = "31";
	public static final String MODE_COOL_CMD = "32";
	public static final String MODE_FAN_CMD = "33";
	public static final String MODE_ENERGY_HEAT_CMD = "34";  //制热节能
	public static final String MODE_ENERGY_COOL_CMD = "35";  //制冷节能

	
	//风机模式
	public static final String FAN_OFF = "00";
	public static final String FAN_LOW = "01";
	public static final String FAN_MID = "02";
	public static final String FAN_HIGH = "03";
	public static final String FAN_AUTO = "04";
	
	//风机模式改变 指令
	public static final String FAN_STATE_TAG = "04";
	public static final String FAN_OFF_CMD = "40";
	public static final String FAN_LOW_CMD = "41";
	public static final String FAN_MID_CMD= "42";
	public static final String FAN_HIGH_CMD = "43";
	public static final String FAN_AUTO_CMD = "44";
	
	//倒计时设置 指令
	public static final String COUNTDOWN_OFF_TAG = "00";       //倒计时关闭
	public static final String COUNTDOWN_STATE_ON_TAG = "01";  //倒计时开机returnId
	public static final String COUNTDOWN_STATE_OFF_TAG = "02"; //倒计时关机returnId
	public static final String COUNTDOWN_STATE_TAG = "0E";
	public static final String COUNTDOWN_CLOSE= "E0";
	public static final String COUNTDOWN_OPEN_STATE_ON = "E1";
	public static final String COUNTDOWN_OPEN_STATE_OFF = "E2";
	
	//进度 最大值 最小值
	public static final int PROGRESS_MAX_C = 32; 
	public static final int PROGRESS_MIN_C = 10;
		
	//按键声音指令
	public static final String SOUND_STATE_TAG = "09";
	public static final String SOUND_CMD_ON = "91";
	public static final String SOUND_CMD_OFF= "90";
	
	//按键震动指令
	public static final String VIBRATE_STATE_TAG = "0D";
	public static final String VIBRATE_CMD_ON = "D1";
	public static final String VIBRATE_CMD_OFF= "D0";

	//时间同步 指令
	public static final String TIME_SYNC_TAG = "07";
	public static final String TIME_SYNC_CMD = "7";
	public static final SimpleDateFormat TIME_SYNC_FORMAT= new SimpleDateFormat("yyyyMMddHHmmss");
	
	//回差设置 指令
	public static final String DIFF_STATE_TAG = "08";
	public static final String DIFF_SETTING_CMD = "8";

	//节能模式 指令
	public static final String ENERGY_STATE_TAG = "0F";
	public static final String ENERGY_SETTING_CMD = "F";

	//编程模式 指令
	public static final String PROGRAM_SETTING_TAG = "0B";
	public static final String PROGRAM_SETTING_CMD = "B";

	//恢复出厂设置
	public static final String RESET_CMD = "G";
	public static final String RESET_FAILED_CMD = "92";
	public static final String RESET_SUCCESS_CMD = "12";

	public static final String TEMP_UNIT_C= "℃";

	//过滤器提醒
	public static final String P_KEY_FANCOIL_FILTER_REMINDER = "FANCOIL_FILTER_REMINDER_DATA";
	public static final String FILTER_REMINDER_CLOSE = "FILTER_REMINDER_CLOSE";
	public static final String FILTER_REMINDER_THREE = "FILTER_REMINDER_THREE";
	public static final String FILTER_REMINDER_SIX = "FILTER_REMINDER_SIX";
	public static final String FILTER_REMINDER_NINE = "FILTER_REMINDER_NINE";
	public static final String FILTER_REMINDER_TWELVE = "FILTER_REMINDER_TWELVE";
		
	public static final String MODE_HEAT_TEXT = "制热";
	public static final String MODE_COOL_TEXT  = "制冷";
	public static final String MODE_ENERGY_TEXT = "节能";
	public static final int COUNTDOWN_TYPE_TEXT_ON = R.string.device_fancoil_countdowm_on;
	public static final int  COUNTDOWN_TYPE_TEXT_OFF = R.string.device_fancoil_countdowm_off;
	public static final String COUNTDOWN_OPEN_TEXT = "启动倒计时";
	public static final String COUNTDOWN_CLOSE_TEXT = "关闭倒计时";
	
	public static final int DRAWABLE_BUTTON_ON = R.drawable.thermost_setting_icon_on;
	public static final int DRAWABLE_BUTTON_OFF = R.drawable.thermost_setting_icon_off;
	public static final int DRAWABLE_ARROW_RIGHT = R.drawable.thermost_setting_arrow_right;

}

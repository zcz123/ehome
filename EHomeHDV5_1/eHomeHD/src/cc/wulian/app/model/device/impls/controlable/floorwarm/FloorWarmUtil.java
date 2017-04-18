package cc.wulian.app.model.device.impls.controlable.floorwarm;

import java.text.SimpleDateFormat;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;

public class FloorWarmUtil {

	public static final String GWID = "gwId";
	public static final String DEVID = "devId";
	public static final String EP= "ep";
	public static final String EPTYPE= "epType";
	public static final SimpleDateFormat TIMESTAMP_CMD = new SimpleDateFormat("yyyyMMddHHmmss");
	
	//查询当前状态指令
	public static final String CURRENT_QUERY_TAG = "1";
	//查询基本数据  发送指令
	public static final String CURRENT_QUERY_CMD_DATA = "11";
	//查询模式数据  发送指令
	public static final String CURRENT_QUERY_CMD_MODE= "12";
	
	
	//开关状态 标志
	public static final String STATE_OFF = "00";
	public static final String STATE_ON = "01";
	//状态 开关 指令
	public static final String STATE_DATA_TAG = "02";
	// 开关  发送指令
	public static final String STATE_CMD_ON = "21";
	public static final String STATE_CMD_OFF = "20";
	
	//编程状态开关 指令
	public static final String PROGRAM_STATE_TAG = "0C";
	//编程状态开关 发送指令
	public static final String PROGRAM_STATE_CMD_ON = "C1";
	public static final String PROGRAM_STATE_CMD_OFF = "C0";
	
	//节能状态开关 指令
	public static final String ENERGY_STATE_TAG = "0F";
	//节能状态开关发送指令
	public static final String ENERGY_STATE_CMD_ON = "F1";
	public static final String ENERGY_STATE_CMD_OFF = "F0";
	
	//温度改变指令
	public static final String TEMP_CHANGE_CMD_TAG = "6";
	public static final String TEMP_CHANGE_TAG = "06";
	//温度单位
	public static final String TEMP_UNIT_C = "00";
	public static final String TEMP_UNIT_F = "01";
	
	//倒计时设置 指令
	public static final String COUNTDOWN_OFF_TAG = "00";       //倒计时关闭
	public static final String COUNTDOWN_STATE_ON_TAG = "01";  //倒计时开机returnId
	public static final String COUNTDOWN_STATE_OFF_TAG = "02"; //倒计时关机returnId
	public static final String COUNTDOWN_CMD_TAG = "E";
	public static final String COUNTDOWN_TAG = "0E";
	public static final String COUNTDOWN_CLOSE= "E0";
	public static final String COUNTDOWN_OPEN_STATE_ON = "E1";
	public static final String COUNTDOWN_OPEN_STATE_OFF = "E2";
	
	//进度 最大值 最小值
	public static final int PROGRESS_MAX_C = 32; 
	public static final int PROGRESS_MIN_C = 10;

	//系统设置
	public static final String SYSTEM_TYPE_TAG = "0A";
	public static final String SYSTEM_TYPE_CMD_TAG = "A";
	public static final String SYSTEM_TYPE_WATER_TAG = "00";  //水地暖
	public static final String SYSTEM_TYPE_ELECT_TAG = "01";   //电地暖
	public static final String SYSTEM_TYPE_WATER_CMD = "A0";  //水地暖
	public static final String SYSTEM_TYPE_ELECT_CMD = "A1";   //电地暖
		
	//按键声音指令
	public static final String SOUND_TYPE_TAG = "09";
	public static final String SOUND_CMD_ON = "91";
	public static final String SOUND_CMD_OFF= "90";
	
	//按键震动指令
	public static final String VIBRATE_TYPE_TAG = "0D";
	public static final String VIBRATE_CMD_ON = "D1";
	public static final String VIBRATE_CMD_OFF= "D0";
	
	//温标指令
	public static final String TEMP_UNIT_C_CMD= "50";
	public static final String TEMP_UNIT_F_CMD= "51";
	
	//时间同步 指令
	public static final String TIME_SYNC_CMD = "7";
	public static final String TIME_SYNC_TAG = "07";
	public static final String TIME_SYNC_FAIL_TAG = "87";
	
	//回差设置 指令
	public static final String DIFF_SETTING_CMD = "8";
	public static final String DIFF_SETTING_TAG = "08";

	//节能模式
	public static final String ENERGY_SAVING_CMD = "F";

	//过温保护
	public static final String OVER_TEMP_CMD = "3";
	public static final String OVER_TEMP_TAG = "03";
	public static final String OVER_TEMP_CMD_ON = "3100000";
	public static final String OVER_TEMP_CMD_OFF = "3000000";
	public static final String OVER_TEMP_SETTING_STATE = "0";  //开关设置
	public static final String OVER_TEMP_SETTING_TEMP= "1";    //温度设置

	//防冻保护
	public static final String FORST_PROTECT_CMD = "4";
	public static final String FORST_PROTECT_TAG = "04";
	public static final String FORST_PROTECT_CMD_ON = "4100000";
	public static final String FORST_PROTECT_CMD_OFF = "4000000";
	public static final String FORST_PROTECT_SETTING_STATE = "0";  //开关设置
	public static final String FORST_PROTECT_SETTING_TEMP= "1";    //温度设置

	//恢复出厂设置
	public static final String RESET_CMD = "H";
	public static final String RESET_FAILED_CMD = "92";
	public static final String RESET_SUCCESS_CMD = "12";

	//编程
	public static final String PROGRAM_CMD_TAG = "B";
	public static final String PROGRAM_CMD = "B1";
	public static final String PROGRAM_TAG = "0B";
	
	public static final String TEMP_UNIT_C_TEXT = "℃";
	public static final String TEMP_UNIT_F_TEXT = "℉";

	public static final int DRAWABLE_BUTTON_ON = R.drawable.thermost_setting_icon_on;
	public static final int DRAWABLE_BUTTON_OFF = R.drawable.thermost_setting_icon_off;
	public static final int DRAWABLE_ARROW_RIGHT = R.drawable.thermost_setting_arrow_right;

	/**
	 * 温度转换
	 * 十六进制转换成十进制字符串 除以100
	 */
	public static String hexStr2Str100(String hexStr) {
		String s = "";
		if(!StringUtil.isNullOrEmpty(hexStr)){
			int i = StringUtil.toInteger(hexStr, 16);
			s = String.valueOf(((double)i)/100);
		}
		return s;
	}
	/**
	 * 温度转换
	 * 十六进制转换成十进制字符串 除以10
	 * 支持负数
	 */
	public static String hexStr2Str10(String hexStr) {
		String s = "";
		if(!StringUtil.isNullOrEmpty(hexStr)){
			int i =  Integer.valueOf(hexStr, 16).shortValue();
			if(Math.abs(i) != i){
				s = String.valueOf(((double)Math.abs(i))/10 * (-1) );
			}else{
				s = String.valueOf(((double)i)/10);
			}
		}
		return s;
	}
	/**
	 * 倒计时转换
	 * 十六进制转换成十进制
	 */
	public static String hexTime2Time(String hexTime){
		String time = "";
		if(!StringUtil.isNullOrEmpty(hexTime)){
			String hour = String.valueOf(StringUtil.toInteger(hexTime.substring(0, 2),16));
			String minues = String.valueOf(StringUtil.toInteger(hexTime.substring(2, 4),16));
			String second = String.valueOf(StringUtil.toInteger(hexTime.substring(4, 6),16));
			String timehour = StringUtil.appendLeft(hour, 2 ,'0');
			String timeMinues = StringUtil.appendLeft(minues, 2 ,'0');
			String timeSecond = StringUtil.appendLeft(second, 2 ,'0');
			time = timehour+timeMinues+timeSecond;
		}
		return time;
	}

	/**
	 * 温度转换
	 * 十六进制转换成十进制
	 */
	public static String hexStr2Str(String hexStr) {
		StringBuilder strBuilder = new StringBuilder();
		if(!StringUtil.isNullOrEmpty(hexStr)){
			String str = String.valueOf(Integer.parseInt(hexStr,16));
			if(str.length() == 1){
				strBuilder.append("0");
			}
			strBuilder.append(str);
		}
		return strBuilder.toString();
	}

	//温度 小数点后为0，则 显示整数
	public static String getTempFormat(String temp){
		if(!StringUtil.isNullOrEmpty(temp)){
			double d = Double.parseDouble(temp);
			if( d - (int)d == 0){
				temp = String.valueOf((int)d);
			}
		}
		return temp;
	}

	//处理温度小数与设备显示保持一致
	public static String tempFormatDevice(String temp){
		if(!StringUtil.isNullOrEmpty(temp)){
			double d = Double.valueOf(temp);
			double t = Math.abs(Double.valueOf(temp));
			if ((t - 0.5) < (int) t) {
				d = (int) d;
			}
			if ((t - 0.5) > (int) t) {

				if(d != t){
					d = (int) d - 0.5;
				}else{
					d = (int) d + 0.5;
				}
			}
			temp = String.valueOf(d);
		}
		return temp;
	}

}

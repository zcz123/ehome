package cc.wulian.app.model.device.EpDataAnalysis;

import android.content.Context;

/**
 * 燃气灶Epdata解析
 * @author yuxiaoxuan
 * @date 2017年3月7日08:49:34
 *
 */
public class WifiEpdataAnalysis_Od extends WifiEpdataAnalysis {

	public WifiEpdataAnalysis_Od(Context context) {
		super(context);
	}

	private boolean isAlarm_timingArrived=false;//是否有定时时间已到提醒
	private String alarmInfo_timingArrived="";
	private boolean isAlarm_TurnoffFire=false;//是否有关火提醒
	private String alarming_TurnoffFire="";
	private DeviceState deviceState=null;
	@Override
	public void initAlarmState() {
		isAlarm_timingArrived=false;
		alarmInfo_timingArrived="";
		isAlarm_TurnoffFire=false;
		alarming_TurnoffFire="";
		deviceState=null;
	}

	@Override
	public void analysisSingleCmd(String cmdType, String cmdValue) {
		if(deviceState==null){
			deviceState=new DeviceState();
		}
		int cmdValue10=Integer.valueOf(cmdValue,16);
		switch (cmdType) {
			case "FF":{
				deviceState.Get_ContinuedbootAlarm=cmdValue10;
			}
			break;
			case "FE":{
				deviceState.Get_RightFireTiming=cmdValue10;
			}
			break;
			case "FD":{
				deviceState.Get_LeftFireTiming=cmdValue10;
			}
			break;
			case "FC":{
				deviceState.Get_Continuedboot=cmdValue10;
			}
			break;
			case "02":{
				deviceState.GET_LEFT_STOVE_FIRE_STATUS=cmdValue10;
			}break;
			case "04":{
				deviceState.GET_RIGHT_STOVE_FIRE_STATUS=cmdValue10;
			}break;
			case "06":{
				deviceState.GET_LEFT_STOVE_REMAIN_TIME_HOUR=cmdValue10;
			}
			break;
			case "07":{
				deviceState.GET_LEFT_STOVE_REMAIN_TIME_MINUTE=cmdValue10;
			}
			break;
			case "08":{
				deviceState.GET_RIGHT_STOVE_REMAIN_TIME_HOUR=cmdValue10;
			}
			break;
			case "09":{
				deviceState.GET_RIGHT_STOVE_REMAIN_TIME_MINUTE=cmdValue10;
			}
			break;
			default:
				break;
		}
	}
	@Override
	public void analysisEnd() {
		//左炉烹饪时间到
		boolean timingArrived_left=deviceState.GET_LEFT_STOVE_FIRE_STATUS==1
				&&deviceState.GET_LEFT_STOVE_REMAIN_TIME_HOUR==0
				&&deviceState.GET_LEFT_STOVE_REMAIN_TIME_MINUTE==0
				&&deviceState.Get_LeftFireTiming==1;
		//右炉烹饪时间到
		boolean timingArrived_right=deviceState.GET_RIGHT_STOVE_FIRE_STATUS==1
				&&deviceState.GET_RIGHT_STOVE_REMAIN_TIME_HOUR==0
				&&deviceState.GET_RIGHT_STOVE_REMAIN_TIME_MINUTE==0
				&&deviceState.Get_RightFireTiming==1;
		isAlarm_timingArrived=timingArrived_left||timingArrived_right;
		if(timingArrived_left&&timingArrived_right){//这种情况不会出现
			alarmInfo_timingArrived="左炉和右炉烹饪时间到";
		}else{
			if(timingArrived_left){
//				alarmInfo_timingArrived="左炉烹饪时间到";
				alarmInfo_timingArrived=this.context.getString(cc.wulian.app.model.device.R.string.gas_stoves_time_to_time_left);
			}else if(timingArrived_right){
//				alarmInfo_timingArrived="右炉烹饪时间到";
				alarmInfo_timingArrived=this.context.getString(cc.wulian.app.model.device.R.string.gas_stoves_time_to_time_right);
			}
		}
		isAlarm_TurnoffFire=deviceState.Get_Continuedboot==1
				&&deviceState.Get_ContinuedbootAlarm==1;
		if(isAlarm_TurnoffFire){
//			alarming_TurnoffFire="持续点燃时间已达5小时，请确认是否需要关火";
			alarming_TurnoffFire=this.context.getString(cc.wulian.app.model.device.R.string.gas_stoves_turn_off_the_reminder_alarm);
		}
	}
	@Override
	public String getAlarmingMsg() {
		String msg="";
		if(isAlarm_timingArrived){
			msg+=alarmInfo_timingArrived+" ";
		}
		if(isAlarm_TurnoffFire){
			msg+=alarming_TurnoffFire+" ";
		}
		return msg;
	}
	@Override
	public boolean isHaveAlarming(){
		boolean isAlarming=false;
		if(isAlarm_timingArrived||isAlarm_TurnoffFire){
			isAlarming=true;
		}
		return isAlarming;
	}
	@Override
	public String printLog() {
		StringBuffer strb=new StringBuffer();
		if(deviceState!=null){
			strb.append("左炉报警日志 ");
			strb.append("左炉着火状态："+deviceState.GET_LEFT_STOVE_FIRE_STATUS+";");
			strb.append("左炉剩余小时："+deviceState.GET_LEFT_STOVE_REMAIN_TIME_HOUR+";");
			strb.append("左炉剩余分钟："+deviceState.GET_LEFT_STOVE_REMAIN_TIME_MINUTE+";");
			strb.append("左炉定时提醒："+deviceState.Get_LeftFireTiming+";");
			strb.append("~~~~~~~~~~~ ");

			strb.append("右炉报警日志 ");
			strb.append("右炉着火状态："+deviceState.GET_RIGHT_STOVE_FIRE_STATUS+";");
			strb.append("右炉剩余小时："+deviceState.GET_RIGHT_STOVE_REMAIN_TIME_HOUR+";");
			strb.append("右炉剩余分钟："+deviceState.GET_RIGHT_STOVE_REMAIN_TIME_MINUTE+";");
			strb.append("右炉定时提醒："+deviceState.Get_RightFireTiming+";");
			strb.append("~~~~~~~~~~~ ");
		}
		return strb.toString();
	}
	/**
	 * 设备状态，注释中的指令码都是十六进制
	 * @author yuxiaoxuan
	 * @date 2017年3月7日13:52:08
	 *
	 */
	class DeviceState{
		/**
		 * 5小时持续开机提醒，指令码：FF</br>
		 * 0	无提醒
		 * 1	有提醒
		 */
		public int Get_ContinuedbootAlarm=0;
		/**
		 * 右炉头定时提醒，指令码：FE</br>
		 * 0	无提醒
		 * 1	有提醒
		 */
		public int Get_RightFireTiming=0;
		/**
		 * 左炉头定时提醒，指令码：FD</br>
		 * 0	无提醒
		 * 1	有提醒
		 */
		public int Get_LeftFireTiming=0;
		/**
		 * 累计5小时连续开机,指令码：FC</br>
		 * 0 	否
		 * 1	是
		 */
		public int Get_Continuedboot=0;
		/**
		 * 左炉头定时剩余时间小时部分，指令码：6
		 */
		public int GET_LEFT_STOVE_REMAIN_TIME_HOUR=0;
		/**
		 * 左炉头定时剩余时间分钟部分，指令码：7
		 */
		public int GET_LEFT_STOVE_REMAIN_TIME_MINUTE=0;
		/**
		 * 右炉头定时剩余时间小时部分，指令码：8
		 */
		public int GET_RIGHT_STOVE_REMAIN_TIME_HOUR=0;
		/**
		 * 右炉头定时剩余时间分钟部分,指令码：9
		 */
		public int GET_RIGHT_STOVE_REMAIN_TIME_MINUTE=0;
		/**
		 * 左炉着火状态,指令码：2</br>
		 * 1	左炉未点着火
		 * 2	左炉已点着火
		 */
		public int GET_LEFT_STOVE_FIRE_STATUS=0;
		/**
		 * 右炉着火状态，指令码：4</br>
		 * 1	右炉未点着火
		 * 2	右炉已点着火
		 */
		public int GET_RIGHT_STOVE_FIRE_STATUS=0;
	}

}
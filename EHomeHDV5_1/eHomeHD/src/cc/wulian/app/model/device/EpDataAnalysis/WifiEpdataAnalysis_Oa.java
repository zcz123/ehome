package cc.wulian.app.model.device.EpDataAnalysis;

import android.content.Context;

/**
 * 解析油烟机的Epdata</br>
 * 只解析报警提醒部分
 * @author yuxiaoxuan
 * @date 2017年3月7日08:43:22
 *
 */
public class WifiEpdataAnalysis_Oa extends WifiEpdataAnalysis {
	public WifiEpdataAnalysis_Oa(Context context) {
		super(context);
	}
	private boolean isAlarmShutdown=false;//是否关机提醒
	private String alarmInfo_ShutDown="";
	private boolean isAlarmCleaning=false;//是否清洗提醒
	private String alarmInfo_Cleaning="";
	private DeviceState deviceState=null;
	public boolean getIsAlarmShutdown(){
		return isAlarmShutdown;
	}
	public boolean getAlarmCleaning(){
		return isAlarmCleaning;
	}
	public String getAlarmInfo_ShutDown(){
		return alarmInfo_ShutDown;
	}
	public String getAlarmInfo_Clean(){
		return alarmInfo_Cleaning;
	}

	@Override
	public void initAlarmState() {
		isAlarmShutdown=false;
		isAlarmCleaning=false;
		alarmInfo_ShutDown="";
		alarmInfo_Cleaning="";
		deviceState=null;
	}
	@Override
	public void analysisSingleCmd(String cmdType, String cmdValue) {
		if(deviceState==null){
			deviceState=new DeviceState();
		}
		int cmdValue10=Integer.valueOf(cmdValue,16);
		switch (cmdType) {
			case "0F":{
				deviceState.GET_OFF_REMIND_CTL=cmdValue10;
			}
			break;
			case "13":{
				deviceState.GET_OFF_REMIND_ALARM=cmdValue10;
			}break;
			case "0E":{
				deviceState.GET_ALREADY_RUN_TIME=cmdValue10;
			}break;
			case "10":{
				deviceState.GET_OFF_REMIND_TIME=cmdValue10;
			}break;
			case "11":{
				deviceState.GET_CLEANING=cmdValue10;
			}break;
			case "FF":{

			}break;
			default:
				break;
		}

	}
	@Override
	public void analysisEnd() {
		if(deviceState.GET_OFF_REMIND_CTL==1&&deviceState.GET_OFF_REMIND_ALARM==1){
			isAlarmShutdown=true;
			String msg=this.context.getString(cc.wulian.app.model.device.R.string.grease_pump_shutdown_alarm);
//			alarmInfo_ShutDown=String.format("持续开机时间已达%s小时,建议您关机处理！", deviceState.GET_OFF_REMIND_TIME);
			alarmInfo_ShutDown=String.format(msg, deviceState.GET_OFF_REMIND_TIME);
		}
		if(deviceState.GET_CLEANING==1){
			isAlarmCleaning=true;
			String msg=this.context.getString(cc.wulian.app.model.device.R.string.grease_pump_cleaning_reminder_alarm);
//			alarmInfo_Cleaning=String.format("累计运行时间已%s小时，请及时清洗！", deviceState.GET_ALREADY_RUN_TIME);
			alarmInfo_Cleaning=String.format(msg, deviceState.GET_ALREADY_RUN_TIME);
		}
	}
	@Override
	public String getAlarmingMsg() {
		String msg="";
		if(isAlarmShutdown){
			msg+=alarmInfo_ShutDown+" ";
		}
		if(isAlarmCleaning){
			msg+=alarmInfo_Cleaning+" ";
		}
		return msg;
	}
	@Override
	public boolean isHaveAlarming(){
		boolean isAlarming=false;
		if(isAlarmShutdown||isAlarmCleaning){
			isAlarming=true;
		}
		return isAlarming;
	}
	/**
	 * 设备状态，注释中的指令码都是十六进制
	 * @author yuxiaoxuan
	 * @date 2017年3月7日09:11:34
	 *
	 */
	class DeviceState{
		/**
		 * 是否打开关机提醒功能，指令码OF</br>
		 * 0	关机提醒功能关闭</br>
		 * 1	关机提醒功能开启</br>
		 */
		public int GET_OFF_REMIND_CTL=0;
		/**
		 * 关机提醒时间是否已到，指令码13</br>
		 * 0	关机提醒时间未到</br>
		 * 1	关机提醒时间到
		 */
		public int GET_OFF_REMIND_ALARM=0;
		/**
		 * 已累计运行时间，指令码OE
		 */
		public int GET_ALREADY_RUN_TIME=0;
		/**
		 * 关机运行时间，指令码10
		 */
		public int GET_OFF_REMIND_TIME=0;
		/**
		 * 清洗提醒是否开启,指令码FF</br>
		 * 0 未开启</br>
		 * 1 开启
		 */
		public int GET_CLEANING=0;
	}



}


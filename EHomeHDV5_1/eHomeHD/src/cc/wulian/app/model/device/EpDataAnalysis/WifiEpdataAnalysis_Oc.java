package cc.wulian.app.model.device.EpDataAnalysis;


import android.content.Context;

/**
 * 解析滚筒洗衣机的Epdata
 * @author yuxiaoxuan
 * @date 2017年3月7日08:43:22
 *
 */
public class WifiEpdataAnalysis_Oc extends WifiEpdataAnalysis {

	public WifiEpdataAnalysis_Oc(Context context) {
		super(context);
	}
	private boolean isAlarmWashingEnd=false;
	private String alarmInfo_WashingEnd="";
	private DeviceState deviceState=null;
	@Override
	public void initAlarmState() {
		isAlarmWashingEnd=false;
		alarmInfo_WashingEnd="";
	}

	@Override
	public void analysisSingleCmd(String cmdType, String cmdValue) {
		if(deviceState==null){
			deviceState=new DeviceState();
		}
		int cmdValue10=Integer.valueOf(cmdValue,16);
		switch (cmdType) {
			case "0C":
				deviceState.Get_WshingEnd=cmdValue10;
				break;
			default:
				break;
		}
	}

	@Override
	public void analysisEnd() {
		if(deviceState.Get_WshingEnd==7){
			isAlarmWashingEnd=true;
//			alarmInfo_WashingEnd="洗衣结束，请及时取走您的衣物！";
			alarmInfo_WashingEnd=this.context.getString(cc.wulian.app.model.device.R.string.washing_machine_finish_hint);
		}

	}
	@Override
	public String getAlarmingMsg() {
		String msg="";
		if(isAlarmWashingEnd){
			msg=alarmInfo_WashingEnd;
		}
		return msg;
	}
	@Override
	public boolean isHaveAlarming(){
		boolean isAlarming=false;
		if(isAlarmWashingEnd){
			isAlarming=true;
		}
		return isAlarming;
	}
	class DeviceState{
		/**
		 * 是否洗衣结束，指令码：0C
		 * 7 洗衣结束；返回其它是未结束
		 */
		public int Get_WshingEnd=0;
	}
}

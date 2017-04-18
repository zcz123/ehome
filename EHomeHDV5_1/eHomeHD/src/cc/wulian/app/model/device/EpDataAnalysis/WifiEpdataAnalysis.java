package cc.wulian.app.model.device.EpDataAnalysis;

import android.content.Context;

import com.wulian.icam.utils.StringUtil;

/**
 * 为解析wifi设备提供通用的方法
 * @author yuxiaoxuan
 * @date 2017年3月7日08:43:31
 */
public abstract class WifiEpdataAnalysis {
	private static final String const_header="AAAA";
	private static final String const_alarming="8200";
	protected String epData;
	protected Context context;
	private String roomName;
	private String devcieName;
	public WifiEpdataAnalysis(Context context){
		this.context=context;
	}
	public void setEpdata(String epdata){
		this.epData=epdata;
	}
	public void setMsgPrefix(String roomName,String deviceName){
		this.roomName=roomName;
		this.devcieName=deviceName;
	}
	//是否是wifi设备返回的数据
	private boolean isWifiData(){
		boolean iswifidata=false;
		if(!StringUtil.isNullOrEmpty(this.epData)&&epData.length()>=4){
			String aaaa=epData.substring(0, 4);
			if(aaaa.toUpperCase().equals(const_header)){
				iswifidata=true;
			}
		}
		return iswifidata;
	}
	//是否是报警信息
	protected boolean isAlarming(){
		boolean isalarming=false;
		boolean isWifiData=this.isWifiData();
		if(isWifiData&&epData.length()>=8){
			String alarmingFlag=epData.substring(8, 12);
			if(alarmingFlag.equals(const_alarming)){
				isalarming=true;
			}
		}
		return isalarming;
	}
	public void AnalysisEpdata(){
		initAlarmState();
		if(this.isAlarming()){
			convertEpdataToDeviceState();
		}
	}
	private void convertEpdataToDeviceState(){
		String strCount=epData.substring(12, 14);
		int cmdCount=Integer.valueOf(strCount,16);//获取指令码数量
		int startIndex=14;
		int Incremental=4;
		for(int i=0;i<cmdCount;i++){
			String singleCmd=epData.substring(startIndex,startIndex+Incremental);
			String cmdType=singleCmd.substring(0, 2).toUpperCase();
			String cmdValue=singleCmd.substring(2,4);
			analysisSingleCmd(cmdType,cmdValue);
			startIndex+=Incremental;
		}
		analysisEnd();
	}
	/**
	 * 初始化报警状态，一般情况下，报警状态初始化值为false，即无报警；
	 */
	protected abstract void initAlarmState();
	/**
	 * 解析单条指令
	 * @param cmdType 指令类型，16进制；
	 * @param cmdValue 指令值，16进制；
	 */
	protected abstract void analysisSingleCmd(String cmdType,String cmdValue);
	/**
	 * 解析结束，一般情况下，解析结束后，就可以判断具体报警情况了
	 */
	protected abstract void analysisEnd();
	/**
	 * 获取报警信息描述
	 * @return 报警信息描述
	 */
	public String getAlarmingFullMsg(){
		String msg=getAlarmingMsg();
		if(StringUtil.isNullOrEmpty(msg)){
			return null;
		}else{
			String alarmingMsg=this.roomName+this.devcieName+getAlarmingMsg();
			return alarmingMsg;
		}
	}

	public String getAlramingBriefMsg(){
		String msg=getAlarmingMsg();
		if(StringUtil.isNullOrEmpty(msg)){
			return null;
		}else{
			String alarmingMsg=getAlarmingMsg();
			return alarmingMsg;
		}
	}

	protected abstract String getAlarmingMsg();

	/**
	 * 是否有报警
	 * @return
     */
	public abstract boolean isHaveAlarming();

	/**
	 * 获取报警日志
	 * @return
     */
	public String printLog(){return "";};
}

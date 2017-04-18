package cc.wulian.smarthomev5.callback;

import java.util.List;
import java.util.Set;

import android.content.Context;
import android.util.Log;

import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.ihome.wan.MessageCallback;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.entity.MonitorInfo;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.ihome.wan.entity.RulesGroupInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;

import cc.wulian.smarthomev5.utils.LogUtil;
import de.greenrobot.event.EventBus;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class ServiceCallback implements MessageCallback {
	protected final CallBackGateway mGwCallBack;
	protected final CallBackDevice mDeviceCallBack;
	protected final CallBackModul mModulCallBack;

	public ServiceCallback(Context context) {
		mDeviceCallBack = new CallBackDevice(context, this);
		mGwCallBack = new CallBackGateway(context, this);
		mModulCallBack = new CallBackModul(context, this);
	}

	// @Override
	// public void ConnectServer(int result) {
	// mGwCallBack.ConnectServer(result);
	// }

	@Override
	public void ConnectGateway(int result, String gwID, GatewayInfo gatewayInfo) {
		mGwCallBack.ConnectGateway(result, gwID, gatewayInfo);
	}
	@Override
	public void GatewayData(int result, String gwID) {
		mGwCallBack.GatewayData(result, gwID);
	}

	@Override
	public void DeviceUp(DeviceInfo devInfo, Set<DeviceEPInfo> devEPInfoSet,
			boolean isFirst) {
		mDeviceCallBack.DeviceUp(devInfo, devEPInfoSet, isFirst);
	}

	@Override
	public void DeviceDown(String gwID, String devID, String status) {
		mDeviceCallBack.DeviceDown(gwID, devID, status);
	}

	@Override
	public void DeviceData(String gwID, String devID, String type,
			DeviceEPInfo devEPInfo) {
		mDeviceCallBack.DeviceData(gwID, devID, type, devEPInfo);
	}

	@Override
	public void SetDeviceIRInfo(String gwID, String mode, String devID,
			String ep, String irType, Set<DeviceIRInfo> devIRInfoSet) {
		mDeviceCallBack.SetDeviceIRInfo(gwID, devID, ep, mode, irType,
				devIRInfoSet);
	}

	@Override
	public void SetDeviceInfo(String mode, DeviceInfo deviceInfo,
			DeviceEPInfo devEPInfo) {
		mDeviceCallBack.SetDeviceInfo(mode, deviceInfo, devEPInfo);
	}

	@Override
	public void SetRoomInfo(String mode, RoomInfo roomInfo) {
		mModulCallBack.SetRoomInfo(mode, roomInfo, true);
	}

	@Override
	public void SetSceneInfo(String mode, SceneInfo sceneInfo) {
		mModulCallBack.SetSceneInfo(mode, sceneInfo, true);
	}

	@Override
	public void SetTaskInfo(String gwID, String version, String sceneID,
			String devID, String type, String ep, String epType,
			Set<TaskInfo> taskInfoSet) {
		mModulCallBack.SetTaskInfo(gwID, version, sceneID, devID, type, ep,
				epType, taskInfoSet);
	}

	@Override
	public void SetMonitorInfo(MonitorInfo monitorInfo) {
	}

	@Override
	public void HandleException(Exception e) {
		LogUtil.logException("HandleSeviceException:", e);
	}

	@Override
	public void GetRoomInfo(String gwID, Set<RoomInfo> roomInfos) {
		mModulCallBack.GetRoomInfo(gwID, roomInfos);
	}

	@Override
	public void GetSceneInfo(String gwID, Set<SceneInfo> sceneInfos) {
		mModulCallBack.GetSceneInfo(gwID, sceneInfos);
	}

	@Override
	public void GetTaskInfo(String gwID, String version, String sceneId,
			Set<TaskInfo> taskInfos) {
		mModulCallBack.GetTaskInfo(gwID, version, sceneId, taskInfos);
	}

	@Override
	public void GetMonitorInfo(String gwID, Set<MonitorInfo> monitorInfos) {
	}

	@Override
	public void GetDeviceIRInfo(String gwID, String devID, String ep,
			String mode, Set<DeviceIRInfo> devIRInfoSet) {
		mDeviceCallBack.GetDeviceIRInfo(gwID, devID, ep, mode, devIRInfoSet);
	}

	@Override
	public void SetBindSceneInfo(String gwID, String mode, String devID,
			JSONArray data) {
		mModulCallBack.SetBindSceneInfo(gwID, mode, devID, data);
	}

	@Override
	public void GetBindSceneInfo(String gwID, String devID, JSONArray data) {
		mModulCallBack.GetBindSceneInfo(gwID, devID, data);
	}

	@Override
	public void SetBindDevInfo(String gwID, String mode, String devID,
			JSONArray data) {
		mDeviceCallBack.SetBindDevInfo(gwID, mode, devID, data);
	}

	@Override
	public void GetBindDevInfo(String gwID, String devID, JSONArray data) {
		mDeviceCallBack.GetBindDevInfo(gwID, devID, data);
	}

	@Override
	public void QueryDevRssiInfo(String gwID, String devID, String data, String uplink) {
		mDeviceCallBack.QueryDevRssiInfo(gwID, devID, data, uplink);
	}

	@Override
	public void QueryDevRelaInfo(String gwID, String devID, String data) {
		mDeviceCallBack.QueryDevRelaInfo(gwID, devID, data);
	}

	@Override
	public void DeviceHardData(String gwID, String devID, String devType,
			String data) {
		mDeviceCallBack.DeviceHardData(gwID, devID, devType, data);
	}

	@Override
	public void PermitDevJoin(String gwID, String devID, String data) {
		mDeviceCallBack.PermitDevJoin(gwID, devID, data);
	}

	@Override
	public void GetDevAlarmNum(String gwID, String userID, String devID,
			String data) {
		mDeviceCallBack.GetDevAlarmNum(gwID, userID, devID, data);
	}

	@Override
	public void GetDevRecordInfo(String gwID, String mode, String count,
			JSONArray data) {
		mDeviceCallBack.GetDevRecordInfo(gwID, mode, count, data);
	}

	@Override
	public void SetTimerSceneInfo(String gwID, String mode, String groupID,
			String groupName, String status, JSONArray data) {
		mModulCallBack.SetTimerSceneInfo(gwID, mode, groupID, groupName,
				status, data);
	}

	@Override
	public void GetTimerSceneInfo(String gwID, JSONArray data) {
		mModulCallBack.GetTimerSceneInfo(gwID, data);
	}

	@Override
	public void ReportTimerSceneInfo(String gwID, JSONArray data) {
		mModulCallBack.ReportTimerSceneInfo(gwID, data);
	}

	// from : appID, alias : userName,
	@Override
	public void PushUserChatAll(String gwID, String userType, String userID,
			String from, String alias, String time, String data) {
		mModulCallBack.GetChatMsg(gwID, userType, userID, from, alias, time,
				data, true);
	}

	@Override
	public void PushUserChatMsg(String gwID, String userType, String userID,
			String from, String alias, String time, String data) {
		mModulCallBack.GetChatMsg(gwID, userType, userID, from, alias, time,
				data, false);
	}

	@Override
	public void PushUserChatSome(String gwID, String userType, String userID,
			String from, String alias, String to, String time, String data) {
	}

	@Override
	public void SetCombindDevInfo(String gwID, String mode, String bindID,
			String name, String roomID, String devIDLeft, String devIDRight) {
		mModulCallBack.SetCombindDevInfo(gwID, mode, bindID, name, roomID,
				devIDLeft, devIDRight);
	}

	@Override
	public void GetCombindDevInfo(String gwID, JSONArray data) {
		mModulCallBack.GetCombindDevInfo(gwID, data);
	}

	@Override
	public void readOfflineDevices(String gwID, String status) {
		mModulCallBack.readOfflineDevices(gwID, status);
	}

	@Override
	public void offlineDevicesBack(DeviceInfo devcieInfo,
			Set<DeviceEPInfo> devEPInfoSet) {
		mModulCallBack.offlineDevicesBack(devcieInfo, devEPInfoSet);
	}

//	@Override
//	public void setGatewayInfo(String gwID, String mode, String gwVer,
//			String gwName, String roomID, String gwLocation, String gwPath,
//			String gwChannel) {
//		mGwCallBack.setGatewayInfo(gwID, mode, gwVer, gwName, roomID,
//				gwLocation, gwPath, gwChannel);
//		if (!StringUtil.isNullOrEmpty(gwChannel)) {
//			mGwCallBack.RouterZigbeeChannel(gwID, mode, gwChannel);
//		}
//	}

	@Override
	public void reqeustOrSetTwoStateConfigration(String mode, String gwID,
			String devID, String ep, JSONArray data) {
		mDeviceCallBack.reqeustOrSetTwoStateConfigration(mode, gwID, devID, ep,
				data);
	}

	@Override
	public void sendControlGroupDevices(String gwID, String group, String mode,
			String data) {
		mDeviceCallBack.sendControlGroupDevices(gwID, group, mode, data);
	}

	@Override
	public void SetAutoProgramTaskInfo(String gwID, String operType,
			AutoProgramTaskInfo autoProgramTaskInfo) {
		mModulCallBack.SetAutoProgramTaskInfo(gwID, operType,
				autoProgramTaskInfo);
	}

	@Override
	public void GetAutoProgramTaskInfo(String gwID,
			List<AutoProgramTaskInfo> autoTaskInfos) {
		mModulCallBack.GetAutoProgramTaskInfo(gwID, autoTaskInfos);
	}

	public void getDreamFlowerConfigMsg(String gwID, String cmdindex,
			String cmdtype, JSONObject data) {
		mModulCallBack.getDreamFlowerConfigMsg(gwID, cmdindex, cmdtype, data);
	}

	@Override
	public void GetRouterConfigMsg(String gwID, String cmdIndex,
			String cmdType, JSONObject data) {
		mGwCallBack.RouterData(gwID, cmdIndex, cmdType, data);
	}

	@Override
	public void GetMigrationTaskMsg(String gwID, String data, String cmdtype) {
		mGwCallBack.MigrationTaskMsg(gwID, data, cmdtype);
	}
	@Override
	public void getTimezonConfigMsg(String gwID,  String cmdtype,JSONObject data) {
		mModulCallBack.getTimezonConfigMsg(gwID,  cmdtype,data);
	}

	@Override
	public void getAutoProgramRulesEffectStatus(String gwID, String cmdtype,
			List<RulesGroupInfo> rulesGroupInfos) {
		mModulCallBack.getAutoProgramRulesEffectStatus(gwID, cmdtype ,rulesGroupInfos);
	}

	@Override
	public void cloudConfigMsg(String gwID, String cmd, String cmdindex,JSONObject data) {
		mModulCallBack.cloudConfigMsg(gwID, cmd, cmdindex,data);
		
	}

	@Override
	public void MiniGatewayWifiSetting(String gwID, String data, String cmdtype,String cmdindex, String disable) {
		mGwCallBack.MiniGatewaySearchWifiList(gwID, data, cmdtype,cmdindex,disable);
	}

	
	@Override
	public void NewDoorLockAccountSetting(String gwID, String devID, String opertype, JSONObject data) {
		mGwCallBack.NewDoorLockAccountSetting(gwID, devID, opertype, data);
	}

	@Override
	public void setGatewayMasterslaveType(String s, String s1, String s2) {
		mGwCallBack.setGatewayMasterslaveType(s,s1,s2);
	}

	public void timingCameraInfo(GatewayInfo gatewayInfo,String p){
	}
//	@Override
//	public void setGatewayInfo(String arg0, String arg1, String arg2,
//			String arg3, String arg4, String arg5, String arg6, String arg7,
//			String arg8, String arg9) {
//		// TODO Auto-generated method stub
//		
//	}

	// 通用设备配置
/*	@Override
	public void commondDeviceConfiguration(String gwID, String cmd,JSONObject data){
		mModulCallBack.commondDeviceConfiguration(gwID, cmd, data);
	}*/




	
//	@Override
//	public void NewDoorLockAccountSetting(String arg0, JSONObject arg1,
//			String arg2, String arg3, String arg4) {
//		// TODO Auto-generated method stub
//		
//	}
    @Override
	public void setGatewayInfo(String gwID, String mode, String gwVer,
			String gwName, String roomID, String gwLocation, String gwPath,
			String gwChannel, String tutkUID, String tutkPASSWD,String gwCityID,String bn) {
		mGwCallBack.setGatewayInfo(gwID, mode, gwVer, gwName, roomID,
				gwLocation, gwPath, gwChannel,tutkUID,tutkPASSWD,gwCityID,bn);
		if (!StringUtil.isNullOrEmpty(gwChannel)) {
			mGwCallBack.RouterZigbeeChannel(gwID, mode, gwChannel);
		}

	}
	@Override
	public void commondDeviceConfiguration(String arg0, String arg1,
			String arg2, long arg3, String arg4, String arg5) {
		mModulCallBack.commondDeviceConfiguration(arg0,arg1,arg2,arg3,arg4,arg5);
		try {
			JSONObject data=JSONObject.parseObject(arg5) ;
			mModulCallBack.commondDeviceConfiguration(arg0, "406", data);
		} catch (ClassCastException e) {
			Log.d("", "转换失败！");
		}

	}

	@Override
	public void queryChildGatewayList(String s, String s1) {
		mGwCallBack.queryChildGatewayList(s,s1);
	}
//

	@Override
	public void managerChildGateway(String s, String s1, String s2, String s3, String s4) {
		mGwCallBack.managerChildGateway(s,s1,s2,s3,s4);
	}

	@Override
	public void setChildGateWayInfo(String gwID, String subGwID, String mode, String gwVer, String gwName, String gwRoomID) {
		mGwCallBack.setChildGateWayInfo(gwID,subGwID,mode,gwVer,gwName,gwRoomID);
	}

	@Override
	public void gatewayCloneAndBackup(String gwID, String appID, String operType, String oldGWID, String result, String step) {
		mGwCallBack.gatewayCloneAndBackup(gwID,appID,operType,oldGWID,result,step);
	}

	@Override
	public void WifiJionNetwork(String gwID, String appID,String devType,String typeID, String opt, String mode,String data) {
		mDeviceCallBack.WifiJionNetwork(gwID,appID,devType,typeID,opt,mode,data);
	}
}

package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import android.content.Context;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.dao.IRDao;
import cc.wulian.smarthomev5.entity.TaskEntity;
import cc.wulian.smarthomev5.entity.TimingSceneEntity;
import cc.wulian.smarthomev5.entity.TimingSceneGroupEntity;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.TimingFlowerEntity;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.DateUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

public class JsonTool
{
	public static void makeTaskJSONObject( JSONObject object, TaskInfo task, String mode ) {
		try {
			object.put(ConstUtil.KEY_MODE, mode);
			object.put(ConstUtil.KEY_CONTENT_ID, task.getContentID());
			object.put(ConstUtil.KEY_EP_DATA, task.getEpData());
			object.put(ConstUtil.KEY_AVAILABLE, task.getAvailable());
			object.put(ConstUtil.KEY_TASK_MODE, TaskEntity.VALUE_TASK_MODE_REALTIME);
			object.put(ConstUtil.KEY_SENSOR_ID,  StringUtil.isNullOrEmpty(task.getSensorID())?"-1":task.getSensorID());
			object.put(ConstUtil.KEY_SENSOR_EP, StringUtil.isNullOrEmpty(task.getSensorEp())?"":task.getSensorEp());
			// Mark: for compatible v3 save task compatible device type
			object.put(ConstUtil.KEY_SENSOR_TYPE, StringUtil.isNullOrEmpty(task.getSensorType()) ? "" : task.getSensorType());
			object.put(ConstUtil.KEY_SENSOR_NAME, StringUtil.isNullOrEmpty(task.getSensorName()) ? "" : task.getSensorName());
			object.put(ConstUtil.KEY_SENSOR_COND, StringUtil.isNullOrEmpty(task.getSensorCond()) ? "" : task.getSensorCond());      
			object.put(ConstUtil.KEY_SENSOR_DATA, StringUtil.isNullOrEmpty(task.getSensorData()) ? "" : task.getSensorData());      
			object.put(ConstUtil.KEY_DELAY, StringUtil.isNullOrEmpty(task.getDelay()) ? "" : task.getDelay());                 
			object.put(ConstUtil.KEY_TASK_MUTILLINKAGE, StringUtil.isNullOrEmpty(task.getMutilLinkage()) ? "" : task.getMutilLinkage());             
			object.put(ConstUtil.KEY_FORWARD, StringUtil.isNullOrEmpty(task.getForward()) ? "" : task.getForward());             
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void makeTaskTriggerJSONObject( JSONObject object, AutoConditionInfo info) {
		try {
			object.put(ConstUtil.KEY_TRIGGER_TYPE, info.getType());
			object.put(ConstUtil.KEY_TRIGGER_OBJECT, info.getObject());
			object.put(ConstUtil.KEY_TRIGGER_EXP, info.getExp());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	public static void makeTaskActionJSONObject( JSONObject object, AutoActionInfo info) {
		try {
//			object.put(ConstUtil.KEY_ACTION_SORTNUM, info.getSortNum());
			object.put(ConstUtil.KEY_ACTION_TYPE, info.getType());
			object.put(ConstUtil.KEY_ACTION_OBJECT, info.getObject());
			object.put(ConstUtil.KEY_ACTION_EPDATA, info.getEpData());
			if(!StringUtil.isNullOrEmpty(info.getDescription())){
				object.put(ConstUtil.KEY_ACTION_DESCRIPTION, info.getDescription());
			}
			if(!StringUtil.isNullOrEmpty(info.getDelay()) && !StringUtil.equals(info.getDelay(), "0")){
				object.put(ConstUtil.KEY_ACTION_DELAY, info.getDelay());
			}
			if(info.getEtrDataArr()!=null&&info.getEtrDataArr().size()>0){
				object.put(ConstUtil.KEY_ACTION_ETRDATAARR, info.getEtrDataArr());
			}
			if(!StringUtil.isNullOrEmpty(info.getCancelDelay())){
//				object.put(ConstUtil.KEY_ACTION_CANCEL_DELAY, info.getCancelDelay());
				object.put("cancelDelay", info.getCancelDelay());
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	public static void makeTimerTaskJSONObject( JSONObject object, TaskInfo task ) {
		AccountManager accountManger = AccountManager.getAccountManger();
		TimeZone timeZone = TimeZone.getTimeZone(accountManger.getmCurrentInfo().getZoneID());
		String serverTime = DateUtil.convert2ServerTime(task.getTime(), timeZone);
		String serverWeekDay = DateUtil.convert2ServerWeekday(task.getWeekday(),task.getTime());

		try {
			object.put(ConstUtil.KEY_TASK_MODE, TaskEntity.VALUE_TASK_MODE_TIMING);
			object.put(ConstUtil.KEY_TIME, serverTime);
			object.put(ConstUtil.KEY_WEEKDAY, serverWeekDay);
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public static void uploadTimingSceneList(String opMode,TimingSceneGroupEntity group,List<TimingSceneEntity> entites) {
		final AccountManager mAccountManger = AccountManager.getAccountManger();
		TimeZone timeZone = TimeZone.getTimeZone(mAccountManger.getmCurrentInfo().getZoneID());
		final JSONArray array = new JSONArray();
		for (TimingSceneEntity timingScene : entites) {
			try {
				JSONObject object = new JSONObject();
				object.put(ConstUtil.KEY_SCENE_ID, timingScene.sceneID);
				String serverTime = DateUtil.convert2ServerTime(timingScene.time, timeZone);
				Logger.debug("local time:"+timingScene.time+";server time"+serverTime);
				String serverWeekDay = DateUtil.convert2ServerWeekday( timingScene.weekDay,timingScene.time);
				object.put(ConstUtil.KEY_TIME, serverTime);
				object.put(ConstUtil.KEY_WEEKDAY, serverWeekDay);
				array.add(object);
			}
			catch (JSONException e) {
			}
		}
		String mode = opMode;
		final String groupID = group.groupID;
		final String groupName = group.groupName;
		final String groupStatus = group.groupStatus;
		if (array.size()== 0) {
			mode = CmdUtil.MODE_DEL;
		}
		NetSDK.sendSetTimerSceneMsg(mAccountManger.getmCurrentInfo().getGwID(), mode, groupID, groupName, groupStatus, array);
		TaskExecutor.getInstance().executeDelay(new Runnable() {
			
			@Override
			public void run() {
				NetSDK.sendSetTimerSceneMsg(mAccountManger.getmCurrentInfo().getGwID(), CmdUtil.MODE_SWITCH, groupID, groupName, groupStatus, null);
			}
		}, 500);
	}
	public static void uploadBindList( Context context,Map<String,SceneInfo> bindScenesMap,Map<String,DeviceInfo> bindDevicesMap, String gwID,String devID, String type ) {
		JSONArray array = new JSONArray();
		if(bindScenesMap != null){
			for(String key : bindScenesMap.keySet()){
				try{
					SceneInfo info = bindScenesMap.get(key);
					if (info != null) {
						JSONObject object = new JSONObject();
						object.put(ConstUtil.KEY_EP, key);
						object.put(ConstUtil.KEY_SCENE_ID, info.getSceneID());
						array.add(object);
					}
				}catch(Exception e){
					
				}
			}
		}
		if(bindDevicesMap != null){
			for(String key : bindDevicesMap.keySet()){
				try {
					DeviceInfo devInfo = bindDevicesMap.get(key);
					if(devInfo != null){
						JSONObject jsonObject = new JSONObject();
						jsonObject.put(ConstUtil.KEY_EP, key);
						jsonObject.put(ConstUtil.KEY_SCENE_ID, "-1");
						jsonObject.put(ConstUtil.KEY_BIND_DEV_ID, devInfo.getDevID());
						jsonObject.put(ConstUtil.KEY_BIND_DATA,StringUtil.getStringEscapeEmpty(devInfo.getDevEPInfo().getEpData()));
						array.add(jsonObject);
					}
				}
				catch (JSONException e) {
				}
			}
		}
		String mode = CmdUtil.MODE_ADD;
		if (array.size() == 0) mode = CmdUtil.MODE_DEL;
		SendMessage.sendSetBindSceneMsg(context, gwID, mode, devID, type, array);
	}
	public static void updateDeviceIRInfo(Context context,String gwID,String devID,String ep,DeviceIRInfo deviceIRInfo, String irType ){
		try {
			JSONObject obj = makeDeviceIRInfo(deviceIRInfo);
			JSONArray array = new JSONArray();
			array.add(obj);
			SendMessage.sendSetDevIRMsg(context,gwID, CmdUtil.MODE_UPD, devID, ep, irType, array);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 保存红外信息至服务器
	 */
	
	public static void deleteIrInfo( Context context,String gwID,String devID,String ep,List<DeviceIRInfo> deviceIRInfos, String irType ){
		List<Object> irJsonObjects = new ArrayList<Object>();
		if(deviceIRInfos != null){
			for(DeviceIRInfo info : deviceIRInfos){
				try{
					JSONObject object = makeDeviceIRInfo(info);
					irJsonObjects.add(object);
				}catch(Exception e){
					
				}
			}
		}
		
		JSONArray array = new JSONArray(irJsonObjects);
		NetSDK.sendSetDevIRMsg(gwID, CmdUtil.MODE_DEL, devID, ep, irType, array);
	}
	private final static int MAX_SEGMENT_LENGTH = 10;
	public static void saveIrInfoBath( Context context,String gwID,String devID,String ep,List<DeviceIRInfo> deviceIRInfos, String irType ) {
		
		if(deviceIRInfos == null)
			return ;
//		List<DeviceIRInfo> deleteIrInfos = new ArrayList<DeviceIRInfo>();
//		DeviceIRInfo irInfo = new  DeviceIRInfo();
//		irInfo.setGwID(gwID);
//		irInfo.setDeviceID(devID);
//		irInfo.setIRType(irType);
//		List<DeviceIRInfo> allInfos = IRDao.getInstance().findListAll(irInfo);
//		for(int i=0 ;i< allInfos.size() ;i++){
//			DeviceIRInfo ir = allInfos.get(i);
//			if(!deviceIRInfos.contains(ir)){
//				deleteIrInfos.add(ir);
//			}
//		}
		deleteIrInfo(context, gwID, devID, ep, null, irType);
		
		String mode = CmdUtil.MODE_BATCH_ADD;
		List<JSONArray> batchList = new ArrayList<JSONArray>();
		JSONArray array =null;
		for(int i=0 ;i < deviceIRInfos.size() ;i++){
			DeviceIRInfo info = deviceIRInfos.get(i);
			JSONObject object = makeDeviceIRInfo(info);
			if(i % MAX_SEGMENT_LENGTH == 0){
				array = new JSONArray();
				array.add(object);
				batchList.add(array);
			}else{
				array.add(object);
			}
		}
		for(JSONArray batchObject :batchList ){
			NetSDK.sendSetDevIRMsg(gwID, mode, devID, ep, irType, batchObject);
			try {
				Thread.sleep(200);
			}
			catch (InterruptedException e1) {
			}
		}
	}
public static void saveIrInfo( Context context,String gwID,String devID,String ep,List<DeviceIRInfo> deviceIRInfos, String irType ) {
		if(deviceIRInfos == null)
			return ;
		List<Object> irJsonObjects = new ArrayList<Object>();
		for(DeviceIRInfo info : deviceIRInfos){
			try{
				JSONObject object = makeDeviceIRInfo(info);
				irJsonObjects.add(object);
			}catch(Exception e){
				
			}
		}
		if(irJsonObjects.size() >0){
			JSONArray array = new JSONArray(irJsonObjects);
			SendMessage.sendSetDevIRMsg(context,gwID, CmdUtil.MODE_ADD, devID, ep, irType,array);
		}
	}
	private static JSONObject makeDeviceIRInfo(DeviceIRInfo info)
			throws JSONException {
		JSONObject object = new JSONObject();
		object.put(ConstUtil.KEY_KEYSET, info.getKeyset());
		object.put(ConstUtil.KEY_CODE, info.getCode());
		object.put(ConstUtil.KEY_NAME, info.getName());
		object.put(ConstUtil.KEY_STUS, info.getStatus());
		object.put(ConstUtil.KEY_IR_TYPE, info.getIRType());
		return object;
	}
	
	public static void deleteAndQueryAutoTaskList(String mode, AutoProgramTaskInfo info){
		final AccountManager mAccountManger = AccountManager.getAccountManger();
		String gwID = mAccountManger.getmCurrentInfo().getGwID();
		String operType = mode;
		String programID = info.getProgramID();
		String programName = info.getProgramName();
		String programDesc = info.getProgramDesc(); 
		String programType = info.getProgramType(); 
		String status = info.getStatus(); 
		NetSDK.sendSetProgramTask(gwID, operType, programID, programName, programDesc,programType,status, null, null, null);
	}
	public static void SetFlowerShowTiming(String cmdindex, List<TimingFlowerEntity> list)
	{
	    AccountManager accountManager = AccountManager.getAccountManger();
	    String gwID = accountManager.getmCurrentInfo().getGwID();
	    JSONArray data = new JSONArray();
	    for(TimingFlowerEntity entity:list){
		  StringBuffer buffer = new StringBuffer();
		  buffer.append(entity.getTime());
		  buffer.append(entity.getWeekDay());
		  data.add(buffer.toString());
	    }
	    SendMessage.sendSetFlowerConfigMsg(gwID, cmdindex, data);
	}
}
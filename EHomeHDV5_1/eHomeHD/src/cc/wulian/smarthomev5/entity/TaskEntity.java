package cc.wulian.smarthomev5.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.text.TextUtils;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;

public class TaskEntity
{
	public static final int NORMAL_TASK = 1;
	public static final int LINK_TASK  =2;
	
	public static String VALUE_TASK_MODE_REALTIME = "1";
	public static String VALUE_TASK_MODE_TIMING = "2";
	public static String VALUE_CONTENT_STOP ="1";
	public static String VALUE_CONTENT_OPEN ="2";
	public static String VALUE_CONTENT_CLOSE ="3";
	public static String VALUE_AVAILABL_YES ="1";
	public static String VALUE_AVAILABL_NO ="0";
	public static String VALUE_SENSOR_ID_NORMAL ="-1";
	public static final String VALUE_FORWARD_DISABLE = "0";
	public static final String VALUE_FORWARD_ENABLE = "1";
	public static final String VALUE_CONDITION_LESS = "-1";
	public static final String VALUE_CONDITION_EQUAL = "0";
	public static final String VALUE_CONDITION_MORE = "1";
	public static final String VALUE_MULTI_LINK_YES = "1";
	public static final String VALUE_MULTI_LINK_NO = "0";
	
	public String gwID;
	public String sceneID;
	public String version;
	public List<TaskGroup> grouplList = new ArrayList<TaskGroup>();
	public TaskEntity(){
	}
	@Override
	public boolean equals( Object o ){
		if (o instanceof TaskEntity){
			TaskEntity entity = (TaskEntity) o;
			return TextUtils.equals(gwID, entity.gwID)
					&& TextUtils.equals(sceneID, entity.sceneID);
		}
		else{
			return super.equals(o);
		}
	}
	public void updateTaskInfo(TaskInfo info){
		if(!StringUtil.isNullOrEmpty(info.getSensorID()) && !TaskEntity.VALUE_SENSOR_ID_NORMAL.equals(info.getSensorID())){
			if(TaskEntity.VALUE_AVAILABL_NO.equals(info.getAvailable())){
				getLinkGroup().removeSensorTask(info.getSensorID(), info.getSensorEp(), info.getDevID(), info.getEp(),info.getSensorCond(),info.getSensorData());
			}else{
				getLinkGroup().updateSensorTask(info);
			}
		}
		else{
			if(TaskEntity.VALUE_AVAILABL_NO.equals(info.getAvailable())){
				getNormalGroup().removeTask(info.getDevID(),info.getEp());
			}else{
				getNormalGroup().updateTask(info);
			}
		}
	}
	public void addNormalGroup(TaskGroup group){
		grouplList.add(0,group);
	}
	public void addLinkGroup(SensorGroup group){
		grouplList.add(1,group);
	}
	public TaskGroup getNormalGroup(){
		if(grouplList.size() == 2){
			return grouplList.get(0);
		}
		return null;
	}
	public SensorGroup getLinkGroup(){
		if(grouplList.size() == 2 && (grouplList.get(1)) instanceof SensorGroup ){
			return (SensorGroup)grouplList.get(1);
		}
		return null;
	}
	public String getGwID() {
		return gwID;
	}
	public void setGwID(String gwID) {
		this.gwID = gwID;
	}
	public String getSceneID() {
		return sceneID;
	}
	public void setSceneID(String sceneID) {
		this.sceneID = sceneID;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public List<TaskGroup> getGrouplList() {
		return grouplList;
	}
	public void setGrouplList(List<TaskGroup> grouplList) {
		this.grouplList = grouplList;
	}
	public static class TaskGroup{
		private String name;
		private int groupID;
		public String gwID;
		public String sceneID;
		public List<TaskInfo> taskList = new ArrayList<TaskInfo>();
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public List<TaskInfo> getTaskList() {
			return taskList;
		}
		public void setTaskList(List<TaskInfo> taskList) {
			this.taskList = taskList;
		}
		public int getGroupID() {
			return groupID;
		}
		public void setGroupID(int groupID) {
			this.groupID = groupID;
		}
		public String getGwID() {
			return gwID;
		}
		public void setGwID(String gwID) {
			this.gwID = gwID;
		}
		public String getSceneID() {
			return sceneID;
		}
		public void setSceneID(String sceneID) {
			this.sceneID = sceneID;
		}
		
		public void addTask(TaskInfo info){
			int count =0 ;
			while(count < taskList.size()){
				TaskInfo tempInfo = taskList.get(count);
				if(StringUtil.equals(tempInfo.getDevID(),info.getDevID()) && StringUtil.equals(tempInfo.getEp(),info.getEp()) )
					break;
				else
				count++;
			}
			if(count == taskList.size())
				taskList.add(info);
		}
		public void updateTask(TaskInfo taskInfo){
			removeTask(taskInfo.getDevID(),taskInfo.getEp());
			addTask(taskInfo);
			
		}
		public void removeTask(String deviceId,String ep){
			taskList.remove(getTask(deviceId, ep));
		}
		public TaskInfo getTask(String deviceId,String ep){
			for(TaskInfo info : taskList){
				if(info.getDevID().equals(deviceId) && info.getEp().equals(ep)){
					return info;
				}
			}
			return null;
		}
		public void clear(){
			taskList.clear();
		}
	}
	public static class SensorGroup extends TaskGroup{
		Map<String,List<TaskInfo>> taskSensorMap = new HashMap<String,List<TaskInfo>>();
		public void removeSensorTask(String sensorID,String sensorEP,String deviceID,String ep,String cond,String sensorData){
			List<TaskInfo> taskSensorsList = getTaskSensorsList(sensorID, sensorEP);
			if(taskSensorsList == null)
				return ;
			for(int i= taskSensorsList.size()-1;i >=0 ;i--){
				TaskInfo info = taskSensorsList.get(i);
				if(info.getDevID().equals(deviceID) && info.getEp().equals(ep) && info.getSensorCond().equals(cond) && info.getSensorData().equals(sensorData)){
					taskSensorsList.remove(info);
				}
			}
		}
		public TaskInfo getSensorTask(String sensorID,String sensorEP,String deviceId,String ep,String condition,String epData){
			List<TaskInfo> taskSensorsList = getTaskSensorsList(sensorID, sensorEP);
			for(TaskInfo info : taskSensorsList){
				if(info.getDevID().equals(deviceId) && info.getEp().equals(ep) && info.getSensorCond().equals(condition) && info.getEpData().equals(epData)){
					return info;
				}
			}
			return null;
		}
		public void updateSensorTask(TaskInfo taskInfo){
			addSensorTask(taskInfo);
		}
		public void addSensorTask(TaskInfo info){
			removeSensorTask(info.getSensorID(), info.getSensorEp(), info.getDevID(), info.getEp(),info.getSensorCond(),info.getSensorData());
			List<TaskInfo> sensorList = getTaskSensorsList(info.getSensorID(),info.getSensorEp());
			sensorList.add(info);
		}
		public List<TaskInfo> getTaskSensorsList(String sensorID,String sensorEP) {
			List<TaskInfo> sensorList = taskSensorMap.get(sensorID+sensorEP);
			if(sensorList == null){
				sensorList = new ArrayList<TaskInfo>();
				taskSensorMap.put(sensorID+sensorEP,sensorList);
			}
			return sensorList;
		}
		public void addTask(TaskInfo info){
			int count =0 ;
			while(count < taskList.size()){
				if(StringUtil.equals(taskList.get(count).getSensorID(),info.getSensorID()) && StringUtil.equals(taskList.get(count).getSensorEp(),info.getSensorEp()) )
					break;
				else
				count++;
			}
			if(count == taskList.size())
				taskList.add(info);
		}
		public TaskInfo getTask(String sensorID,String sensorEP){
			for(TaskInfo info : taskList){
				if(info.getSensorID().equals(sensorID) && info.getSensorEp().equals(sensorEP)){
					return info;
				}
			}
			return null;
		}
		public void clear(){
			super.clear();
			taskSensorMap.clear();
		}
	}
}


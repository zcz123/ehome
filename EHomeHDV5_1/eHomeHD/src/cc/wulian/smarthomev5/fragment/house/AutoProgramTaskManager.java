package cc.wulian.smarthomev5.fragment.house;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;

public class AutoProgramTaskManager {

	
	private Map<String, AutoProgramTaskInfo> autoTaskProgramMap = new HashMap<String, AutoProgramTaskInfo>();
	
	private static AutoProgramTaskManager instance = new AutoProgramTaskManager();
	private Preference preference = Preference.getPreferences();
	
	Comparator<AutoProgramTaskInfo> comparator = new Comparator<AutoProgramTaskInfo>() {

		@Override
		public int compare(AutoProgramTaskInfo info1,
				AutoProgramTaskInfo info2) {
			long info1ToLong = StringUtil.toLong(info1.getProgramID());
			long info2ToLong = StringUtil.toLong(info2.getProgramID());
			if(info1ToLong > info2ToLong){
				return 1;
			}else{
				return -1;
			}
		}

	};
	private AutoProgramTaskManager() {

	}
	
	public static AutoProgramTaskManager getInstance() {
		return instance;
	}
	
	
	public boolean contains(String gwID) {
		return autoTaskProgramMap.containsKey(gwID);
	}
	
	public int size() {
		return autoTaskProgramMap.size();
	}

	public void clear() {
		autoTaskProgramMap.clear();
	}

	public AutoProgramTaskInfo getProgramTaskinfo(String gwID,String programID) {
		return autoTaskProgramMap.get(gwID + programID);
	}

	public void deleteAutoTaskInfo(String gwID,String programID){
		autoTaskProgramMap.remove(gwID + programID);
	}
	
	public void updateBasicAutoTaskInfo(AutoProgramTaskInfo autoProgramTaskInfo){
		updateAutoTaskInfo(autoProgramTaskInfo,false);
	}
		
	public void updateAutoTaskInfo(AutoProgramTaskInfo autoProgramTaskInfo){
		updateAutoTaskInfo(autoProgramTaskInfo,true);
	}

	private void updateAutoTaskInfo(AutoProgramTaskInfo autoProgramTaskInfo, boolean isUpdateRuleList) {
		AutoProgramTaskInfo taskInfo = getProgramTaskinfo(autoProgramTaskInfo.getGwID(), autoProgramTaskInfo.getProgramID());
		if(taskInfo != null){
			taskInfo.setGwID(autoProgramTaskInfo.getGwID());
			taskInfo.setProgramID(autoProgramTaskInfo.getProgramID());
			if(!StringUtil.isNullOrEmpty(autoProgramTaskInfo.getProgramDesc())){
				taskInfo.setProgramDesc(autoProgramTaskInfo.getProgramDesc());
			}
			if(!StringUtil.isNullOrEmpty(autoProgramTaskInfo.getProgramName())){
				taskInfo.setProgramName(autoProgramTaskInfo.getProgramName());
			}
			if(!StringUtil.isNullOrEmpty(autoProgramTaskInfo.getProgramType())){
				taskInfo.setProgramType(autoProgramTaskInfo.getProgramType());
			}
			if(!StringUtil.isNullOrEmpty(autoProgramTaskInfo.getStatus())){
				taskInfo.setStatus(autoProgramTaskInfo.getStatus());
			}
			if(isUpdateRuleList){
				taskInfo.setActionList(autoProgramTaskInfo.getActionList());
				taskInfo.setTriggerList(autoProgramTaskInfo.getTriggerList());
				taskInfo.setRoot(autoProgramTaskInfo.getRoot());
			}
		}else{
			autoTaskProgramMap.put(autoProgramTaskInfo.getGwID() + autoProgramTaskInfo.getProgramID(), autoProgramTaskInfo);
		}
	}
	
	public List<AutoProgramTaskInfo> getAutoTaskList(String gwID) {
		List<AutoProgramTaskInfo> autoProgramTaskInfos = new ArrayList<AutoProgramTaskInfo>();
		for(String key : autoTaskProgramMap.keySet()){
			if(key.startsWith(gwID)){
				autoProgramTaskInfos.add(autoTaskProgramMap.get(key));
			}
		}
		Collections.sort(autoProgramTaskInfos,comparator);
		return autoProgramTaskInfos;
	}
	
	public AutoProgramTaskInfo getAutoProgramTypeScene(String sceneID){
		List<AutoProgramTaskInfo> allAutoTaskList = getAutoTaskList(AccountManager.getAccountManger().getmCurrentInfo().getGwID());
		for(AutoProgramTaskInfo info : allAutoTaskList){
			if(StringUtil.equals(info.getProgramType(), "0") && StringUtil.equals(info.getProgramName(), sceneID)){
				return info;
			}
		}
		return null;
	} 
	
	public AutoProgramTaskInfo getAutoProgramTypeTime(String sceneID){
		List<AutoProgramTaskInfo> allAutoTaskList = getAutoTaskList(AccountManager.getAccountManger().getmCurrentInfo().getGwID());
		boolean obtainStatus = preference.getBoolean(AccountManager.getAccountManger().getmCurrentInfo().getGwID() + "1" + IPreferenceKey.P_KEY_HOUSE_RULE_TIMING_STATUS, true);
		String status = ""; 
		if(obtainStatus){
			status = "2";
		}else{
			status = "1";
		}
		for(AutoProgramTaskInfo info : allAutoTaskList){
			if(StringUtil.equals(info.getProgramType(), "1")  && StringUtil.equals(info.getProgramName(), sceneID)){
				info.setStatus(status);
				return info;
			}
		}
		return null;
	} 
	
	public List<AutoProgramTaskInfo> getAutoProgramTypeHouseKeeper(){
		List<AutoProgramTaskInfo> autoProgramTypeHouseKeeperlist = new ArrayList<AutoProgramTaskInfo>(); 
		List<AutoProgramTaskInfo> allAutoTaskList = getAutoTaskList(AccountManager.getAccountManger().getmCurrentInfo().getGwID());
		for(AutoProgramTaskInfo info : allAutoTaskList){
			if(StringUtil.equals(info.getProgramType(), "2")){
				autoProgramTypeHouseKeeperlist.add(info);
			}
		}
		return autoProgramTypeHouseKeeperlist;
	} 
	
}

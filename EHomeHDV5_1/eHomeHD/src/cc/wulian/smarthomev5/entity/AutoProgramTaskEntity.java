package cc.wulian.smarthomev5.entity;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;

public class AutoProgramTaskEntity {

	private String gwID;
	private String programID;
	private String programName;
	private String programDesc;
	private String programType;
	
	public List<AutoProgramTaskInfo> autoProgramList = new ArrayList<AutoProgramTaskInfo>();

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getProgramID() {
		return programID;
	}

	public void setProgramID(String programID) {
		this.programID = programID;
	}

	public String getProgramName() {
		return programName;
	}

	public void setProgramName(String programName) {
		this.programName = programName;
	}

	public String getProgramDesc() {
		return programDesc;
	}

	public String getProgramType() {
		return programType;
	}

	public void setProgramType(String programType) {
		this.programType = programType;
	}

	public void setProgramDesc(String programDesc) {
		this.programDesc = programDesc;
	}

	public List<AutoProgramTaskInfo> getAutoTaskList() {
		return autoProgramList;
	}

	public void setAutoTaskList(List<AutoProgramTaskInfo> autoTaskList) {
		this.autoProgramList = autoTaskList;
	}
	
	@Override
	public boolean equals( Object o ){
		if (o instanceof AutoProgramTaskEntity){
			AutoProgramTaskEntity entity = (AutoProgramTaskEntity) o;
			return TextUtils.equals(gwID, entity.gwID)
					&& TextUtils.equals(programID, entity.programID);
		}
		else{
			return super.equals(o);
		}
	}
	
	public void addAutoTask(AutoProgramTaskInfo autoProgramTaskInfo){
		int count =0 ;
		while(count < autoProgramList.size()){
			AutoProgramTaskInfo taskInfo = autoProgramList.get(count);
			if(StringUtil.equals(taskInfo.getProgramID(),autoProgramTaskInfo.getProgramID()) && StringUtil.equals(taskInfo.getProgramName(),autoProgramTaskInfo.getProgramName()) )
				break;
			else
			count++;
		}
		if(count == autoProgramList.size())
			autoProgramList.add(autoProgramTaskInfo);
	}
	
	public void updateAutoTask(AutoProgramTaskInfo autoProgramTaskInfo){
		removeTask(autoProgramTaskInfo.getProgramID(),autoProgramTaskInfo.getProgramName());
		addAutoTask(autoProgramTaskInfo);
		
	}
	
	public void removeTask(String programID,String programName){
		autoProgramList.remove(getAutoTask(programID, programName));
	}
	public AutoProgramTaskInfo getAutoTask(String programID,String programName){
		for(AutoProgramTaskInfo info : autoProgramList){
			if(info.getProgramID().equals(programID) && info.getProgramName().equals(programName)){
				return info;
			}
		}
		return null;
	}
	public void clear(){
		autoProgramList.clear();
	}
}

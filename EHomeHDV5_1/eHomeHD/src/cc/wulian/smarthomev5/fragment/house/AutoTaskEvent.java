package cc.wulian.smarthomev5.fragment.house;

import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;

public class AutoTaskEvent {

	public static final String QUERY = "query";
	public static final String STATUS = "status";
	public static final String MODIFY = "modify";
	public static final String REMOVE = "remove";
	public static final String ADDRULE = "addrule";
	public String action;
	public AutoProgramTaskInfo taskInfo;

	public AutoTaskEvent(){
		
	}
	public AutoTaskEvent(String action){
		this.action = action;
	}
	public AutoTaskEvent( String action, AutoProgramTaskInfo taskInfo)
	{
		this.action = action;
		this.taskInfo = taskInfo;
	}
}

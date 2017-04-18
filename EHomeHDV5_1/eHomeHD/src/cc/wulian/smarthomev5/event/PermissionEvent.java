package cc.wulian.smarthomev5.event;

import cc.wulian.smarthomev5.entity.PermissionEntity;

public class PermissionEvent {

	public String action;
	public PermissionEntity entity;
	public static final String REJECT = "reject";
	public static final String ACCEPT = "accept";

	public PermissionEvent(){
		
	}
	public PermissionEvent(String action){
		this.action = action;
	}
	public PermissionEvent( String action, PermissionEntity entity)
	{
		this.action = action;
		this.entity = entity;
	}
}

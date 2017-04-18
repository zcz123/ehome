package cc.wulian.smarthomev5.event;

import java.util.List;

import cc.wulian.smarthomev5.entity.ShakeEntity;

public class ShakeEvent
{
	public  String action;
	public  List<ShakeEntity> entities;
	public ShakeEntity shakeEntity;
	public  boolean isFromMe;

	public ShakeEvent( String action ,List<ShakeEntity> entities,ShakeEntity shakeEntity,boolean isFromMe )
	{
		this.action = action;
		this.isFromMe = isFromMe;
		this.entities = entities;
		this.shakeEntity =shakeEntity;
	}

	public String getAction() {
		return action;
	}

	public List<ShakeEntity> getEntities() {
		return entities;
	}

	public boolean isFromMe() {
		return isFromMe;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public void setEntities(List<ShakeEntity> entities) {
		this.entities = entities;
	}

	public void setFromMe(boolean isFromMe) {
		this.isFromMe = isFromMe;
	}
	
}

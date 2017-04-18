package cc.wulian.smarthomev5.event;

import cc.wulian.smarthomev5.entity.SocialEntity;

public class SocialEvent
{
	public String action;

	public SocialEntity entity;
	
	public SocialEvent(SocialEntity entity)
	{
		this.entity = entity;
	
	}

	public SocialEvent( String action )
	{
		this.action = action;
	}
}

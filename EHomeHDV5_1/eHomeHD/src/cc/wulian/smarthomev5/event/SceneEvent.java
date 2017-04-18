package cc.wulian.smarthomev5.event;

import cc.wulian.ihome.wan.entity.SceneInfo;

public class SceneEvent
{
	private static final String TAG = SceneEvent.class.getSimpleName();
	
	public final String action;
	public final boolean isFromMe;
	public final SceneInfo sceneInfo;

	public SceneEvent( String action, boolean isFromMe, SceneInfo sceneInfo )
	{
		this.action = action;
		this.isFromMe = isFromMe;
		this.sceneInfo = sceneInfo;
	}
	
	@Override
	public String toString(){
		return TAG + ":{" + "action:{" + action + "}" + ", isFromMe:{" + isFromMe + "}" + ", sceneInfo:{" + sceneInfo + "}" + "}";
	}
}

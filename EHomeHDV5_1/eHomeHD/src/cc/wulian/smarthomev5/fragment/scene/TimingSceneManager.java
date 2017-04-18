package cc.wulian.smarthomev5.fragment.scene;

import java.util.HashMap;
import java.util.Map;

import cc.wulian.smarthomev5.entity.TimingSceneGroupEntity;

public class TimingSceneManager {

	private static TimingSceneManager instance = new TimingSceneManager();
	private Map<String,TimingSceneGroupEntity> timingSceneGroupMap = new HashMap<String,TimingSceneGroupEntity>();
	private  TimingSceneGroupEntity defaultGroup = new TimingSceneGroupEntity();
	
	private TimingSceneManager(){
		timingSceneGroupMap.put(defaultGroup.getGroupID(), defaultGroup);
	}
	public static TimingSceneManager getInstance(){
		return instance;
	}
	public TimingSceneGroupEntity getDefaultGroup() {
		return defaultGroup;
	}
	
}

package cc.wulian.smarthomev5.fragment.scene;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.entity.TaskEntity;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class SceneTaskManager {

	private static Map<String, TaskEntity> taskEntityMap = new HashMap<String, TaskEntity>();
	private static SceneTaskManager instance = new SceneTaskManager();
	private static MainApplication mApplication = MainApplication
			.getApplication();

	private SceneTaskManager() {

	}

	public static SceneTaskManager getInstance() {
		return instance;
	}

	public void addTaskEntity(TaskEntity entity) {
		taskEntityMap.put(entity.gwID + "_" + entity.sceneID, entity);
	}

	public boolean contains(String gwId, String sceneId) {
		return taskEntityMap.containsKey(gwId + "_" + sceneId);
	}

	public int size() {
		return taskEntityMap.size();
	}

	public void clear() {
		taskEntityMap.clear();
	}

	public TaskEntity getTaskEntity(String gwId, String sceneId) {
		return taskEntityMap.get(gwId + "_" + sceneId);
	}

	public String getCurUsingSceneID() {
		TreeMap<String, SceneInfo> mSceneInfoMap = mApplication.sceneInfoMap;
		Iterator<SceneInfo> iterator = mSceneInfoMap.values().iterator();
		String curSceneID = "";
		while (iterator.hasNext()) {
			SceneInfo sceneInfo = iterator.next();
			if (CmdUtil.SCENE_USING.equals(sceneInfo.getStatus())) {
				curSceneID = sceneInfo.getSceneID();
			}

		}
		return curSceneID;
	}

}

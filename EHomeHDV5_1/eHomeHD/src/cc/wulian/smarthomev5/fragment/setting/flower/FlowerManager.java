package cc.wulian.smarthomev5.fragment.setting.flower;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.ihome.wan.util.CollectionsUtil;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.FlowerVoiceControlEntity;
import cc.wulian.smarthomev5.fragment.setting.flower.entity.TimingFlowerEntity;
import cc.wulian.smarthomev5.tools.AccountManager;

public class FlowerManager {
	public static FlowerManager instance = new FlowerManager();
	private Map<String, List<TimingFlowerEntity>> timeMap = new HashMap<String, List<TimingFlowerEntity>>();
	private Map<String, FlowerVoiceControlEntity> controlEntitesMap = new LinkedHashMap<String, FlowerVoiceControlEntity>();

	public static FlowerManager getInstance() {
		return instance;
	}

	public List<TimingFlowerEntity> addTimingFlowerEntity(
			TimingFlowerEntity entity, String key) {
		List<TimingFlowerEntity> list = timeMap.get(key);
		if (list == null) {
			list = new ArrayList<TimingFlowerEntity>();
			timeMap.put(key, list);
		}
		list.add(entity);
		return list;
	}

	public List<TimingFlowerEntity> getFlowerTimingEntities(String key) {
		List<TimingFlowerEntity> list = timeMap.get(key);
		if (list == null) {
			list = new ArrayList<TimingFlowerEntity>();
			timeMap.put(key, list);
		}
		return list;
	}

	public List<TimingFlowerEntity> getNewTimingEntities(String key) {

		List<TimingFlowerEntity> list = timeMap.get(key);
		if (list == null) {
			list = new ArrayList<TimingFlowerEntity>();
		}
		return new ArrayList<TimingFlowerEntity>(list);
	}

	public List<TimingFlowerEntity> removeTimingFlowerEntitiesNewList(
			TimingFlowerEntity entity, String key) {
		List<TimingFlowerEntity> list = timeMap.get(key);
		if (list != null)
			list.remove(entity);
		return list;
	}

	public void putVoiceControlEntity(FlowerVoiceControlEntity entity) {
		this.controlEntitesMap.put(entity.getIndex(), entity);
	}

	public void removeVoiceControlEntity(String key) {
		this.controlEntitesMap.remove(key);
	}

	public FlowerVoiceControlEntity getVoiceControlEntity(String key) {
		return this.controlEntitesMap.get(key);
	}

	public List<FlowerVoiceControlEntity> getVoiceControlEntities() {
		for (int i = 1; i <= 5; i++) {
			if (!controlEntitesMap.containsKey(i + "")) {
				FlowerVoiceControlEntity entity = new FlowerVoiceControlEntity();
				entity.setIndex(i + "");
				entity.setBindScene(FlowerVoiceControlEntity.VALUE_UNBINDSCENE);
				entity.setStudy(FlowerVoiceControlEntity.VALUE_UNSTUDYED);
				entity.setGwID(AccountManager.getAccountManger().getmCurrentInfo()
						.getGwID());
				controlEntitesMap.put(entity.getIndex(), entity);
			}
		}
		return CollectionsUtil.mapConvertToList(controlEntitesMap);
	}
}
package cc.wulian.smarthomev5.fragment.device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.ihome.wan.util.CollectionsUtil;
import cc.wulian.smarthomev5.entity.CombindDeviceEntity;

public class CombindDeviceManager {

	private static Map<String,CombindDeviceEntity> combindMap = new HashMap<String , CombindDeviceEntity>();
	
	private static CombindDeviceManager instance = new CombindDeviceManager();
	public static CombindDeviceManager getInstance(){
		return instance;
	}
	public void putCombindDevice(CombindDeviceEntity entity){
		combindMap.put(entity.getBindID(), entity);
	}
	public void removeCombindDevice(String combindID){
		combindMap.remove(combindID);
	}
	public CombindDeviceEntity getCombindDevice(String bindID){
		return combindMap.get(bindID);
	}
	public List<CombindDeviceEntity> getCombindEntites(){
		return CollectionsUtil.mapConvertToList(combindMap);
	}
}

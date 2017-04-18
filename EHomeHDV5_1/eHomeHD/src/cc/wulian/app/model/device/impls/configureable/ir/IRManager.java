package cc.wulian.app.model.device.impls.configureable.ir;

import java.util.HashMap;
import java.util.Map;

public class IRManager {

	private Map<String,IRGroupManager> irManagerMap = new HashMap<String,IRGroupManager>();
	public static IRManager irManager = new IRManager();
	public static IRManager getInstance(){
		return irManager;
	}
	
	public IRGroupManager getIrGroupManager(String gwID,String devID){
		if(!irManagerMap.containsKey(gwID+devID)){
			IRGroupManager manager = new IRGroupManager();
			manager.setDevID(devID);
			manager.setGwID(gwID);
			irManagerMap.put(gwID+devID, manager);
		}else {
			irManagerMap.get(gwID+devID).refereshIRGroup();
		}
		return irManagerMap.get(gwID+devID);
	}
}

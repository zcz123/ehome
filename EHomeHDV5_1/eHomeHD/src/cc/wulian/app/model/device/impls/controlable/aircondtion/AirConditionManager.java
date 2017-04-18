package cc.wulian.app.model.device.impls.controlable.aircondtion;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AirConditionManager {

	public Map<String, AirCondition> airConditionMap = new LinkedHashMap<String, AirCondition>();
	public static AirConditionManager instance = new AirConditionManager();

	private static List<AirConditionDataListener> listeners = new ArrayList<AirConditionDataListener>();

	public static AirConditionManager getInstance() {
		return instance;
	}

	public void addAriCondition(AirCondition airCondition) {
		airConditionMap.put(airCondition.getCurID(), airCondition);
	}

	public void removeAirCondition(AirCondition airCondition) {
		airConditionMap.remove(airCondition.getCurID());
	}

	public List<AirCondition> getAllAirConditions() {
		List<AirCondition> result = new ArrayList<AirCondition>();
		for (String key : airConditionMap.keySet()) {
			result.add(airConditionMap.get(key));
		}
		return result;
	}

	public AirCondition getAirConditionByID(String airID) {
		return airConditionMap.get(airID);
	}

	public static void addDataListener(AirConditionDataListener listener) {
		listeners.add(listener);
	}

	public static void removeDataListener(AirConditionDataListener listener) {
		listeners.remove(listener);
	}

	public static void fireEpDataListener(String airID) {
		for (AirConditionDataListener l : listeners) {
			l.onAirDataChanged(airID);
		}
	}

	public static interface AirConditionDataListener {
		public void onAirDataChanged(String airID);
	}
}

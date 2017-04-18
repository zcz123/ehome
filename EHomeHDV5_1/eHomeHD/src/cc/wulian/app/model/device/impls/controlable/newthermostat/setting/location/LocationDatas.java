package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationDatas {

	//位置 大洲大洋分类
	public static final String[] locationArray = {"Asia","Pacific"};
	//位置 国家数组  与大洲、大洋 分组对应
	public static final String[] locationCountryArray0= {"China","Japan"};
	public static final String[] locationCountryArray1= {"United States"};
	
	//国家分组集合
	private static List<String[]> locationCountryList = new ArrayList<>();
	// 位置Map  key->大洲大洋分类      value->国家
	private static Map<String, String[]> locationMap = new HashMap<>();
	
	//所有国家数组
	public static final String[] locationCountryArray= {"China","Japan","United States"};
	//国家Code数组
	public static final String[] locationCountryCodeArray= {"CN","JP","US"};
	//国家 Code Map   key->国家名称    value->国家Code
	private static Map<String, String> countryCodeMap = new HashMap<>();
	
	
	public static void initLocationDatas(){
		locationCountryList.add(locationCountryArray0);
		locationCountryList.add(locationCountryArray1);
		
		for (int i = 0; i < locationArray.length; i++) {
			locationMap.put(locationArray[i], locationCountryList.get(i));
		}
	}
	
	public static List<String[]> getLocationCountryList() {
		
		return locationCountryList;
	}
	
	public static Map<String, String[]> getLocationMap() {
		return locationMap;
	}
	
	public static Map<String, String> getCountryCodeMap() {
		for (int i = 0; i < locationCountryArray.length; i++) {
			countryCodeMap.put(locationCountryArray[i], locationCountryCodeArray[i]);
		}
		return countryCodeMap;
	}
}

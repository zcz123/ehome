package cc.wulian.app.model.device.impls.configureable.ir;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.util.CollectionsUtil;

/**
 * 控制的相应机器（机顶盒、空调、通用）的信息，如：模式、类型、名称
 * @author Administrator
 *
 */
public class IRGroup {
	public static int MODE_CONTROL = 0;
	public static int MODE_STUDY = 1;
	private String groupType;
	private String groupName;
	private int mode = MODE_STUDY;
	protected Map<String,DeviceIRInfo> deviceIrInfoMap = new TreeMap<String, DeviceIRInfo>(new Comparator<String>() {

		@Override
		public int compare(String key1, String key2) {
			//升序排列
			return key1.compareTo(key2);
		}
		
	});
	
	public String getGroupType() {
		return groupType;
	}
	public void setGroupType(String groupType) {
		this.groupType = groupType;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	/**
	 * 得到设备红外信息，向map集合中添加key控制码和设备红外的信息
	 * @param info
	 */
	public void addDeviceIrInfo(DeviceIRInfo info){
		deviceIrInfoMap.put(info.getKeyset(), info);
	}
	/**
	 * 删除设备红外的信息（设备的控制码）
	 * @param info
	 */
	public void removeDeviceIrInfo(DeviceIRInfo info){
		deviceIrInfoMap.remove(info.getKeyset());
	}
	/**
	 * 得到控制码，来得到设备红外的信息
	 * @param info
	 */
	public DeviceIRInfo getDeviceIrInfo(String key){
		return deviceIrInfoMap.get(key);
	}
	public void clear(){
		deviceIrInfoMap.clear();
	}
	/**
	 * 判断是否为学习模式
	 * @return
	 */
	public boolean isStudyMode(){
		if(MODE_STUDY == mode)
			return true;
		return false;
	}
	public int size(){
		return deviceIrInfoMap.size();
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	/**
	 * 得到所有的key
	 * @return
	 */
	public List<DeviceIRInfo> getAllKeys() {
		return CollectionsUtil.mapConvertToList(deviceIrInfoMap);
	}
}

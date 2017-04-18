package cc.wulian.app.model.device.impls.configureable.ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.smarthomev5.activity.MainApplication;

public class IRGroupManager {
	public static String TYPE_GENERAL = "00";
	public static String TYPE_AIR_CONDITION = "01";
	public static String TYPE_STB = "02";
	public static String NO_TYPE = "03";
	private String gwID;
	private String devID;
	private Map<String,IRGroup> irMap = new HashMap<String,IRGroup>();
	private MainApplication application = MainApplication.getApplication();
	private IRGroup noGroup = new IRGroup();
	public IRGroupManager(){
		//实例化了三个头视图显示的对象(机顶盒、空调、通用)，放在map集合里面
		IRGroup groupSTB = new IRGroup();
		groupSTB.setGroupType(TYPE_STB);
		groupSTB.setGroupName(application.getString(R.string.device_ir_STB));
		irMap.put(TYPE_STB, groupSTB);

		IRGroup groupAir = new IRGroup();
		groupAir.setGroupType(TYPE_AIR_CONDITION);
		groupAir.setGroupName(application.getString(R.string.device_cate_8));
		irMap.put(TYPE_AIR_CONDITION, groupAir);

		IRGroup groupGeneral = new IRGroup();
		groupGeneral.setGroupType(TYPE_GENERAL);
		groupGeneral.setGroupName(application.getString(R.string.device_ir_current));
		irMap.put(TYPE_GENERAL, groupGeneral);
	}
	public void refereshIRGroup(){
		IRGroup groupSTB = irMap.get(TYPE_STB);
		groupSTB.setGroupName(application.getString(R.string.device_ir_STB));

		IRGroup groupAir = irMap.get(TYPE_AIR_CONDITION);
		groupAir.setGroupName(application.getString(R.string.device_cate_8));

		IRGroup groupGeneral = irMap.get(TYPE_GENERAL);
		groupGeneral.setGroupName(application.getString(R.string.device_ir_current));
	}

	/**
	 * 添加到头视图的方法（从map里面取）
	 * @param info
	 */
	public void addIrInfo(DeviceIRInfo info){
		IRGroup group = irMap.get(info.getIRType());
		if(group != null){
			group.addDeviceIrInfo(info);
		}else{
			noGroup.addDeviceIrInfo(info);
		}
	}
	/**
	 * 删除头视图相应的模式的方法（从map里面移除）
	 * @param info
	 */
	public void removeIRInfo(DeviceIRInfo info){
		IRGroup group = irMap.get(info.getIRType());
		if(group != null){
			group.removeDeviceIrInfo(info);
		}else{
			noGroup.removeDeviceIrInfo(info);
		}
	}
	
	/**
	 * 得到头视图的机器、控制a码（从map里面取）
	 * @param info
	 */
	public DeviceIRInfo getDeviceIRInfo(String irType,String key){
		IRGroup group = irMap.get(irType);
		if(group != null){
			return group.getDeviceIrInfo(key);
		}
		return null;
	}
	public void clear(){
		getAriGroup().clear();
		getSTBGroup().clear();
		getGeneralGroup().clear();
		noGroup.clear();
	}
	/**
	 * 取机顶盒的信息
	 * @return
	 */
	public IRGroup getSTBGroup(){
		return irMap.get(TYPE_STB);
	}
	/**
	 * 取空调的信息
	 * @return
	 */
	public IRGroup getAriGroup(){
		return irMap.get(TYPE_AIR_CONDITION);
	}
	/**
	 * 取通用的信息
	 * @return
	 */
	public IRGroup getGeneralGroup(){
		return irMap.get(TYPE_GENERAL);
	}
	public int getGeneralGroupSize(){
		int size = 0;
		IRGroup group = getGeneralGroup();
		if(group != null)
			size = group.size();
		size += noGroup.size();
		return size;
	}
	public DeviceIRInfo getGeneralDeviceIrInfo(String key){
		IRGroup group = irMap.get(TYPE_GENERAL);
		DeviceIRInfo info = noGroup.getDeviceIrInfo(key);
		if(info == null)
			info = group.getDeviceIrInfo(key);
		return info;
	}
	public void removeGeneralDeviceIrInfo(DeviceIRInfo info){
		IRGroup group = getGeneralGroup();
		if(group != null)
			group.removeDeviceIrInfo(info);
		noGroup.removeDeviceIrInfo(info);
	}
	
	public void addGeneralDeviceIrInfo(DeviceIRInfo info){
		IRGroup group = getGeneralGroup();
		if(group != null)
			group.addDeviceIrInfo(info);
	}
	
	public List<DeviceIRInfo> getGeneralGroupDeviceIrInfos(){
		List<DeviceIRInfo> infos = noGroup.getAllKeys();
		IRGroup group = getGeneralGroup();
		if(group != null)
			infos.addAll(group.getAllKeys());
		return infos;
	}
	/**
	 * 通过key取机相应的信息
	 * @return
	 */
	public IRGroup getGroup(String type){
		return irMap.get(type);
	}
	
	/**
	 *	获取所有机器的信息
	 * @return
	 */
	public List<IRGroup> getIRGroups (){
		List<IRGroup> result = new ArrayList<IRGroup>();
		result.add(getSTBGroup());
		result.add(getAriGroup());
		result.add(getGeneralGroup());
		return result;
				
	}
	public String getGwID() {
		return gwID;
	}
	public void setGwID(String gwID) {
		this.gwID = gwID;
	}
	public String getDevID() {
		return devID;
	}
	public void setDevID(String devID) {
		this.devID = devID;
	}
	//获取默认 相应设备红外的信息 (gwId、DeviceId、ep、keyset（红外按键码）、irType（红外类型）、code（红外控制码）、name、status)
	public List<DeviceIRInfo> getDefaultDeviceIRInfo(String groupType){
		ArrayList<DeviceIRInfo> result = new ArrayList<DeviceIRInfo>();
		//设置机顶盒、空调、通用的设备信息
		if(TYPE_STB.equals(groupType)){
			DeviceIRInfo stbDefaultIrInfo = new DeviceIRInfo();
			stbDefaultIrInfo.setDeviceID(devID);
			stbDefaultIrInfo.setGwID(gwID);
			stbDefaultIrInfo.setIRType(TYPE_STB);
			stbDefaultIrInfo.setEp(WulianDevice.EP_14);
			String key = "256";
			stbDefaultIrInfo.setName(key);
			stbDefaultIrInfo.setStatus("0");
			stbDefaultIrInfo.setCode(key);
			stbDefaultIrInfo.setKeyset(key);
			result.add(stbDefaultIrInfo);
		}else if(TYPE_AIR_CONDITION.equals(groupType)){
			DeviceIRInfo airDefaultIrInfo = new DeviceIRInfo();
			airDefaultIrInfo.setDeviceID(devID);
			airDefaultIrInfo.setGwID(gwID);
			airDefaultIrInfo.setIRType(TYPE_AIR_CONDITION);
			airDefaultIrInfo.setEp(WulianDevice.EP_14);
			String key = "000";
			airDefaultIrInfo.setName(application.getString(R.string.device_state_close));
			airDefaultIrInfo.setCode(key);
			airDefaultIrInfo.setStatus("0");
			airDefaultIrInfo.setKeyset(key);
			result.add(airDefaultIrInfo);
		}else if(TYPE_GENERAL.equals(groupType)){
			DeviceIRInfo generalDefaultIrInfo = new DeviceIRInfo();
			generalDefaultIrInfo.setDeviceID(devID);
			generalDefaultIrInfo.setGwID(gwID);
			generalDefaultIrInfo.setIRType(TYPE_GENERAL);
			generalDefaultIrInfo.setEp(WulianDevice.EP_14);
			String key = "511";
			generalDefaultIrInfo.setName(application.getString(R.string.device_state_close));
			generalDefaultIrInfo.setCode(key);
			generalDefaultIrInfo.setStatus("0");
			generalDefaultIrInfo.setKeyset(key);
			result.add(generalDefaultIrInfo);
		}
		return result;
		
	}
	/**
	 * 得到所有的红外设备信息
	 * @return
	 */
	public List<DeviceIRInfo> getAllIRInfos(){
		ArrayList<DeviceIRInfo> result = new ArrayList<DeviceIRInfo>();
		result.addAll(getSTBGroup().getAllKeys());
		result.addAll(getAriGroup().getAllKeys());
		result.addAll(getGeneralGroup().getAllKeys());
		result.addAll(noGroup.getAllKeys());
		return result;
	}
	
}

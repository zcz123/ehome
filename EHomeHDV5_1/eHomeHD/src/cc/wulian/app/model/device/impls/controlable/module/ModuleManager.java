package cc.wulian.app.model.device.impls.controlable.module;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.wulian.ihome.wan.util.StringUtil;

public class ModuleManager {

	public static final String OTHER_MODE = "12";
	public Map<String, CreatModuleInterface> moduleLightMap = new LinkedHashMap<String, CreatModuleInterface>();
	
	public static ModuleManager instance = new ModuleManager();
	
	private ModuleManager(){
		mCacheModule();
	}
	
	public static ModuleManager getInstance(){
		return instance;
	}
	
	public void addModuleLight(CreatModuleInterface moduleLightStatue){
		moduleLightMap.put(moduleLightStatue.getModuleMode(), moduleLightStatue);
	}
	
	public void removeModuleLight(CreatModuleInterface moduleLightStatue){
		moduleLightMap.remove(moduleLightStatue.getModuleMode());
	}
	
	public void mCacheModule(){
		for(int i=1 ;i <= 9 ;i++){
			ModuleLightStatue  moduleLightStatue = new ModuleLightStatue(StringUtil.appendLeft(i+"", 2, '0'));
			if(i >=1 && i <= 4 ){
				moduleLightStatue.setAdjustSpeed(false);
			}else if(i == 9){
				moduleLightStatue.setAdjustLight(false);
				moduleLightStatue.setAdjustSpeed(false);
			}
			moduleLightMap.put(moduleLightStatue.getModuleMode(), moduleLightStatue);
		}
		OtherMode otherMode = new OtherMode(OTHER_MODE);
		moduleLightMap.put(otherMode.getModuleMode(), otherMode);
	}
//	public boolean getisAdjustLight(String mode){
//		return moduleLightMap.get(mode).isAdjustLight();
//	}
//	public boolean getisAdjustSpeed(String mode){
//		return moduleLightMap.get(mode).isAdjustSpeed();
//	}
	public CreatModuleInterface getModuleMode(String mode){
		return moduleLightMap.get(mode);
	}
	public OtherMode getOtherMode(){
		return (OtherMode) moduleLightMap.get(OTHER_MODE);
	}
}
